package com.model;

import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
	public static void cadastrarProduto(Connection con, ObjetoProdutoImport prod)throws Exception {
		String query = "INSERT INTO PRODUTOS VALUES (NULL,?,?,?,?,NULL, ?, 1);";
		 try {
			PreparedStatement ps;
			List<Object> estoque = DBOperations.selectSqlList(con, "SELECT IDPROD FROM PRODUTOS WHERE CODBARRA = ?", prod.getCodBa());
			if(estoque.size() == 0) {
				throw new Exception("Codigo já cadastrado");
			}else {
				ps = con.prepareStatement(query);
				if(prod.getProd().length() >= 80) {prod.setProd(prod.getProd().substring(0, 80)); }
				prod.setProd(prod.getProd().toUpperCase());
				ps.setString(1, prod.getCodBa());
				ps.setString(2, prod.getProd());
				ps.setDouble(3, prod.getValorUltV());
				ps.setInt(4, prod.getQuanti());
				ps.setDouble(5, prod.getValorCusto());
				ps.executeUpdate();
			}
		} catch (DataTruncation e) {
			e.printStackTrace();
			throw new Exception("Codigo de Barras Inválido");
		} catch(SQLException e2) {
			e2.printStackTrace();
			throw new Exception("Dados Inválidos");
		}
		 throw new Exception("Falha ao inserir produto");
	}
	
	/**
	 *  Atualiza as propriedades do produto cadastrado.
	 *
	 * @param con the con
	 * @param chave the chave
	 * @param codBarra the cod barra
	 * @param descr the descr
	 * @param valor the valor
	 * @param quantidade the quantidade
	 * @param valorC the valor C
	 * @return true, if successful
	 */
	
	public static boolean UpdateItemBd(Connection con, int chave, String codBarra, String descr, double valor,
			Integer quantidade, double valorC) {
		// String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?,
		// VLR_ULT_VENDA = ?,QUANTIDADE = ?, PRECO_CUSTO = ? WHERE IDPROD = ?;";
		// ou
		// String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?,
		// VLR_ULT_VENDA = ?,QUANTIDADE = QUANTIDADE + ? ,PRECO_CUSTO = ? WHERE IDPROD =
		// ?;";
		String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ? ,QUANTIDADE = QUANTIDADE + ? WHERE IDPROD = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, codBarra);
			if (descr.length() >= 80) {
				descr = descr.substring(0, 80);
			}
			ps.setString(2, descr);
			ps.setDouble(3, valor);
			ps.setInt(4, quantidade);
			ps.setInt(5, chave);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
