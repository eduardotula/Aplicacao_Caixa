package com.model;

import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



// TODO: Auto-generated Javadoc
/**
 * The Class CustomSQL.
 */
public class CustomSQL {

	/**
	 * Cadastra um novo produto
	 *
	 * @param con
	 * @param prod um protudo para ser cadastrado
	 * @throws Exception
	 */
	public static void cadastrarProduto(Connection con, ObjetoProdutoImport prod) throws Exception {
		String query = "INSERT INTO PRODUTOS VALUES (NULL,?,?,?,?,NULL, ?, 1);";
		try {
			List<Object> estoque = DBOperations.selectSqlList(con, "SELECT IDPROD FROM PRODUTOS WHERE CODBARRA = ?",
					prod.getCodBa());
			if (estoque.size() > 0) {
				throw new Exception("Codigo já cadastrado");
			} else {
				if (prod.getProd().length() >= 80) {
					prod.setProd(prod.getProd().substring(0, 80));
				}
				DBOperations.DmlSql(con, query, prod.getCodBa(), prod.getProd(), prod.getValorUltV(), prod.getQuanti(),
						prod.getValorCusto());
			}
		} catch (DataTruncation e) {
			e.printStackTrace();
			throw new Exception("Codigo de Barras Inválido");
		} catch (SQLException e2) {
			e2.printStackTrace();
			throw new Exception("Dados Inválidos");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Falha ao inserir produto");
		}
	}

	/**
	 * <p>
	 * Atualiza as propriedades do produto cadastrado somando com a quantidade de
	 * produtos em estoque
	 * <p>
	 * Eg. qtdBanco = 1 qtdAtualizar = 5 qtdBanco + qtdAtualizar = 6
	 * 
	 *
	 * @param con        the con
	 * @param chave      the chave
	 * @param codBarra   the cod barra
	 * @param descr      the descr
	 * @param valor      the valor
	 * @param quantidade the quantidade
	 * @param valorC     the valor C
	 * @return true, if successful
	 * @throws SQLException
	 * @throws ClassCastException
	 */

	public static void UpdateItemBdSafe(Connection con, int chave, String codBarra, String descr, double valor,
			Integer quantidade, double valorC) throws ClassCastException, SQLException {
		String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ?,QUANTIDADE = QUANTIDADE + ? ,PRECO_CUSTO = ? WHERE IDPROD = ?;";

		if (descr.length() >= 80) {
			descr = descr.substring(0, 80);
		}
		DBOperations.DmlSql(con, query, codBarra, descr, valor, quantidade, valorC, chave);
	}

	/**
	 * <p>
	 * Atualiza as propriedades do produto cadastrado, o valor quantidade sera o
	 * novo valor de quantidade ignorando a quantidade em estoque
	 *
	 * <p>
	 * Eg. qtdBanco = 1 qtdAtualizar = 5 qtdBanco = 5
	 *
	 * @param con        the con
	 * @param chave      the chave
	 * @param codBarra   the cod barra
	 * @param descr      the descr
	 * @param valor      the valor
	 * @param quantidade the quantidade
	 * @param valorC     the valor C
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException       the SQL exception
	 */
	public static void UpdateItemBd(Connection con, int chave, String codBarra, String descr, double valor,
			Integer quantidade, double valorC) throws ClassCastException, SQLException {
		String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ?,QUANTIDADE ? ,PRECO_CUSTO = ? WHERE IDPROD = ?;";

		if (descr.length() >= 80) {
			descr = descr.substring(0, 80);
		}
		DBOperations.DmlSql(con, query, codBarra, descr, valor, quantidade, valorC, chave);
	}


	/**
	 * Operacao abrir caixa.
	 *
	 * @param con    the con
	 * @param valorT the valor T
	 * @param funcio the funcio
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException       the SQL exception
	 */
	public static void OperacaoAbrirCaixa(Connection con, double valorT, String funcio)
			throws ClassCastException, SQLException {
		LocalDate data = LocalDate.now();
		// Realiza a criação do Caixa
		String queryInsertControle = "INSERT INTO CONTROLECAIXA VALUES (NULL,?,?);";
		DBOperations.DmlSql(con, queryInsertControle, java.sql.Date.valueOf(data), funcio);

		// obtem o valor da chave do caixa aberto
		Integer[] id = DBOperations.selectSql1Dimen(con,
				"SELECT FIRST 1 IDCAIXA FROM CONTROLECAIXA ORDER BY IDCAIXA DESC", new Integer[0]);
		if (id.length < 1) {
			id[0] = 1;
		}
		// Atualiza a tabela systema para armazenar o caixa atualmente aberto e atualiza
		// o valor para caixa aberto
		DBOperations.DmlSql(con, "UPDATE SISTEMA SET VALOR = 1,VALOR2 = ? WHERE IDSYS = 1", id[0]);
		// Utiliza a chave do caixa para salvar as operacoes do caixa
		DBOperations.DmlSql(con, "INSERT INTO OPERACOES_CAIXA VALUES (NULL,?,?,0,0,?,?);", "Caixa Aberto", valorT,
				id[0], Time.valueOf(LocalTime.now()));

	}

	/**
	 * Busca os valores total de sangria e troco do caixa aberto
	 *
	 * @param con the con
	 * @return the controle caixa values
	 */
	public static double[] getControleCaixaValues(Connection con) {
		double sangria = 0;
		double valorTroco = 0;
		double valores[] = { 0, 0 };
		// SETVALOR RET
		try {
			Integer[] chaveUltiC = DBOperations.selectSql1Dimen(con, "SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1;",
					new Integer[0]);
			if(chaveUltiC.length < 1) return valores;
			Integer chaveUltiCaixa = chaveUltiC[0];
			// Obtem o valor do ultimo troco salvo
			valorTroco = DBOperations.selectSql1Dimen(con,
					"SELECT TROCOCAIXA FROM OPERACOES_CAIXA "
							+ "WHERE CONTROLECAIXA_IDCAIXA = ? AND OPERACAO = 'Caixa Aberto';",
					new Double[0], chaveUltiCaixa)[0];
			// Obtem o valor de Retiradas
			Double[] sangrias = DBOperations.selectSql1Dimen(con,
					"SELECT VALORDINHEIRO FROM OPERACOES_CAIXA"
							+ " WHERE CONTROLECAIXA_IDCAIXA = ? AND OPERACAO = 'Retirada'",
					new Double[0], chaveUltiCaixa);
			for (Double s : sangrias) {
				sangria = sangria + s;
			}
			valores[0] = valorTroco;
			valores[1] = sangria;
			return valores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valores;
	}

	public static void operacaoFecharCaixa(Connection con, ArrayList<PrintRelatoriosProds> prodTabela, int idCaixa) throws SQLException {
		System.out.println("IDCAIXA " + idCaixa);
		double somaTot = 0.0;

		ResultSet rs = DBOperations.selectSqlRs(con,
				"SELECT P.CODBARRA,P.DESCRICAO, V.QUANTI, V.VALORDINHEIRO, V.VALORCARTAO, V.VALORTOT,"
						+ "V.HORA FROM VENDAS V INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
						+ "WHERE CONTROLECAIXA_IDCAIXA = ?;",
				idCaixa);

		while (rs.next()) {
			String cod = rs.getString("CODBARRA");
			String desc = rs.getString("DESCRICAO");
			int quanti = (rs.getInt("QUANTI"));
			double valorUni = (rs.getDouble("VALORDINHEIRO") + rs.getDouble("VALORCARTAO"));
			double valorTot = (rs.getDouble("VALORTOT"));
			somaTot = valorTot + somaTot;
			Time hora = (rs.getTime("HORA"));
			PrintRelatoriosProds prodsPrint = new PrintRelatoriosProds(cod, desc, quanti, valorUni, valorTot,
					hora.toString());
			prodTabela.add(prodsPrint);
		}

		DBOperations.DmlSql(con, "UPDATE SISTEMA SET VALOR = 0, VALOR2 = 0 WHERE IDSYS = 1");

	}

}
