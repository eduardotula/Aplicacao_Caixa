package com.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.view.MainVenda;


//Contem Todas as Operaï¿½áes de banco de dados
public class DBFrenteCaixa {

	PreparedStatement ps;
	SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
	ResultSet rs;
	DefaultTableModel model;

	public void appendAnyTable(PreparedStatement ps, int quantiColunas, DefaultModels tableModels) {
		try {
			ResultSet rs = ps.executeQuery();
			Object[] a = new Object[quantiColunas];
			while(rs.next()) {
				int co = 1;
				for(int i = 0;i<quantiColunas;i++) {
					if(rs.getObject(co) instanceof java.sql.Date) {
						a[i] = ((Date) rs.getObject(co)).toLocalDate();
					}else if(rs.getObject(co) instanceof java.sql.Time) {
						a[i] = ((Time) rs.getObject(co)).toLocalTime();
					}else if(rs.getObject(co) instanceof BigDecimal) {
						a[i] = ((BigDecimal) rs.getObject(co)).doubleValue();
					}else {
						a[i] = rs.getObject(co);
					}
					co++;
				}
				tableModels.addRow(a);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void appendAnyTable(String query, int quantiColunas, DefaultModels tableModels, Object[] sets){
		try {
			PreparedStatement ps = MainVenda.con.prepareStatement(query);
			int co = 1;
			if(sets != null) {
				for(int i = 0;i<sets.length;i++) {
					if(sets[i] instanceof LocalDate) {
						ps.setDate(co, java.sql.Date.valueOf((LocalDate) sets[i]));
					}else if(sets[i] instanceof LocalTime) {
						ps.setTime(co, java.sql.Time.valueOf((LocalTime) sets[i]));
					}else {
						ps.setObject(i, sets[i]);
					}
					co++;
				}
			}
			appendAnyTable(ps, quantiColunas, tableModels);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// Classe MainVenda
	// Busca impressora salva
	public String getImpressora(Connection con) {
		try {
			ps = con.prepareStatement("SELECT IMPRESSORA FROM CADASTRO_LOJA WHERE ID = 1");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer getFornecedorIdByName(String nome) {
		try {
			ps = MainVenda.con.prepareStatement("SELECT ID FROM FORNECEDORES WHERE NOME = ?");
			ps.setString(1, nome);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt("ID");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	// Insere uma nova venda
	public void updateVendas(ArrayList<DbGetter> produtosVendido, Connection con, LocalDate date, LocalTime time) {

		int arraySize = produtosVendido.size() - 1;
		for (int i = 0; arraySize >= i; i++) {

			DbGetter g = produtosVendido.get(i);
			int chaveProd = g.getChaveEsto();
			int quanti = g.getQuant();

			String queryI = "INSERT INTO VENDAS VALUES(NULL, ?, ?, ?, ?, ?, ?, ?,?,?);";
			String queryUpDate = "UPDATE PRODUTOS SET QUANTIDADE = QUANTIDADE - ?, DATA_ULT_VENDA = ? WHERE IDPROD = ?; ";
			Date dateSQ = java.sql.Date.valueOf(date);
			try {
				ps = con.prepareStatement(queryI);
				ps.setInt(1, quanti);
				ps.setDouble(2, g.getValorUn());
				ps.setDouble(3, g.getValorDinheiro());
				ps.setDouble(4, g.getValorCartao());
				ps.setDouble(5, g.getValorTot());
				ps.setString(6, g.getTipoPagamento());
				ps.setTime(7, java.sql.Time.valueOf(time));
				System.out.println(chaveProd);
				System.out.println(MainVenda.IdCaixa);
				ps.setInt(8, chaveProd);
				ps.setInt(9, MainVenda.IdCaixa);
				ps.executeUpdate();

				ps = con.prepareStatement(queryUpDate);
				ps.setInt(1, quanti);
				ps.setDate(2, dateSQ);
				ps.setInt(3, chaveProd);
				System.out.println(queryUpDate);
				System.out.println(quanti);
				System.out.println(dateSQ);
				System.out.println(chaveProd);

				ps.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Falha em completar Venda");
			}

		}
	}

	// Tabela de Recargas
	public Double getRecargas(Connection con, DefaultModels model, Integer idCaixa) {
		String query = "SELECT ID,RECARGA,NUMERO, VALOR, HORA, DATA FROM RECARGAS WHERE CONTROLECAIXA_IDCAIXA = ?";
		Object[] o = new Object[6];
		Double soma = 0.0;
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, idCaixa);
			rs = ps.executeQuery();
			while (rs.next()) {
				o[0] = rs.getInt(1);
				o[1] = rs.getString(2);
				o[2] = rs.getString(3);
				o[3] = rs.getDouble(4);
				soma = soma + rs.getDouble("VALOR");
				o[4] = rs.getTime(5).toLocalTime();
				o[5] = rs.getDate(6).toLocalDate();
				model.addRow(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return soma;
	}

	// Atualiza o valor do produto com base no valor de sua ultima venda
	public void updatePreco(Connection con, double vlr, int id) {
		String queryU = "UPDATE PRODUTOS SET VLR_ULT_VENDA = ? WHERE IDPROD = ?;";

		try {
			ps = con.prepareStatement(queryU);
			ps.setDouble(1, vlr);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Check se Caixa esta atualmente aberto
	public int getCaixaAberto(Connection con) {
		String query = "SELECT VALOR FROM SISTEMA WHERE IDSYS = 1;";
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("VALOR");
				return i;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha em Verificar status do Caixa");
		}
		return 0;
	}

	public void setCaixaAberto(Connection con, int statusCaixa) {
		String query = "UPDATE SISTEMA SET VALOR = ?, VALOR2 = 0 WHERE IDSYS = 1;";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, statusCaixa);
			ps.executeUpdate();
			System.out.println("Status Caixa atualizado: " + statusCaixa);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha em Salvar Status");
		}

	}

	public Integer getIdCaixa(Connection con) {
		String query = "SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1;";
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("VALOR2");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getFuncionarioByCaixaID(Connection con, Integer caixaID) {
		String query = "SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, caixaID);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("FUNCIONARIO");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Classe RelatorioDia
	// Retorna todas as linhas de vendas do dia
	public double getTabelaDia(Connection con, DefaultTableModel diaModel) {

		String queryIni = "Select V.CODESTO,P.CODBARRA,P.DESCRICAO,V.QUANTI,V.VALORUNI,V.VALORDINHEIRO,V.VALORCARTAO,V.VALORTOT,V.TIPOPAGAMENTO,V.HORA,P.IDPROD FROM VENDAS V"
				+ " INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD WHERE V.CONTROLECAIXA_IDCAIXA = ?";
		Object[] prod = new Object[11];
		double valorTotProd = 0;
		double valorTotColumn = 0;
		try {
			ps = con.prepareStatement(queryIni);
			ps.setInt(1, MainVenda.IdCaixa);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				prod[0] = rs.getInt("CODESTO");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTI");
				prod[4] = rs.getDouble("VALORUNI");
				prod[5] = rs.getDouble("VALORDINHEIRO");
				prod[6] = rs.getDouble("VALORCARTAO");
				valorTotProd = rs.getDouble("VALORTOT");
				prod[7] = valorTotProd;
				prod[8] = rs.getString("TIPOPAGAMENTO");
				prod[9] = rs.getTime("HORA").toLocalTime();
				prod[10] = rs.getInt("IDPROD");
				valorTotColumn = valorTotColumn + valorTotProd;
				diaModel.addRow(prod);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o foi possivel obter Vendas");
		}
		return valorTotColumn;
	}

	// Insere na tabela VENDAAPAGA uma linha deletada das vendas
	public void insertVendaApagada(Connection con, String motivo, JTable tableVendaDia) {
		int[] selectRows = tableVendaDia.getSelectedRows();
		for (int i = 0; i < selectRows.length; i++) {
			String query = "INSERT INTO VENDAAPAGA VALUES (NULL, ?, ?,?,?,?,?,?,?,?,?);";
			int quanti = (int) tableVendaDia.getValueAt(selectRows[i], 3);
			double valorDinheiro = (double) tableVendaDia.getValueAt(selectRows[i], 5);
			double valorCartao = (double) tableVendaDia.getValueAt(selectRows[i], 6);
			double valorTot = (double) tableVendaDia.getValueAt(selectRows[i], 7);
			LocalDate date = LocalDate.now();
			LocalTime time = LocalTime.now();
			LocalTime horaVenda = (LocalTime) tableVendaDia.getValueAt(selectRows[i], 9);
			int idProd = (int) tableVendaDia.getValueAt(selectRows[i], 10);
			try {
				ps = con.prepareStatement(query);
				ps.setInt(1, quanti);
				ps.setDouble(2, valorDinheiro);
				ps.setDouble(3, valorCartao);
				ps.setDouble(4, valorTot);
				ps.setDate(5, java.sql.Date.valueOf(date));
				ps.setTime(6, java.sql.Time.valueOf(horaVenda));
				ps.setDate(7, java.sql.Date.valueOf(date));
				ps.setTime(8, java.sql.Time.valueOf(time));
				ps.setString(9, motivo);
				ps.setInt(10, idProd);
				ps.executeUpdate();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o Foi possï¿½vel Apagar");
			}
		}

	}

	// CLasse ControleCaixa
	// Realiza a abertura do Caixa
	public boolean OperacaoAbrirCaixa(Connection con, double valorT, String funcio) {
		try {
			LocalDate data = LocalDate.now();
			// Realiza a criaï¿½áo do Caixa
			String queryInsertControle = "INSERT INTO CONTROLECAIXA VALUES (NULL,?,?);";
			PreparedStatement ps = con.prepareStatement(queryInsertControle);
			ps.setDate(1, java.sql.Date.valueOf(data));
			ps.setString(2, funcio);
			ps.executeUpdate();

			// obtem o valor da chave do caixa aberto
			ps = con.prepareStatement("SELECT IDCAIXA FROM CONTROLECAIXA ORDER BY IDCAIXA DESC");
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("IDCAIXA");
				// Atualiza a tabela systema para armazenar o caixa atualmente aberto e atualiza
				// o valor para caixa aberto
				ps = con.prepareStatement("UPDATE SISTEMA SET VALOR = ?,VALOR2 = ? WHERE IDSYS = 1");
				ps.setInt(1, 1);
				ps.setInt(2, i);
				ps.executeUpdate();
				// Utiliza a chave do caixa para salvar as operacoes do caixa

				String query = "INSERT INTO OPERACOES_CAIXA VALUES (NULL,?,?,0,0,?,?);";
				ps = con.prepareStatement(query);
				ps.setString(1, "Caixa Aberto");
				ps.setDouble(2, valorT);
				ps.setInt(3, i);
				ps.setTime(4, java.sql.Time.valueOf(LocalTime.now()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(null, "Caixa Aberto");
				return true;
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Valor Diditado Invalido");
		}
		return false;
	}

	public double OperacaoRet(Connection con, LocalTime lt) {
		try {
			String valor = JOptionPane.showInputDialog(new JFrame(), "Digite O Valor da Retirada").replace(",", ".");
			double valorD = Double.parseDouble(valor);
			ps = con.prepareStatement("SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1");
			rs = ps.executeQuery();
			if (rs.next() && valorD != 0) {
				int ultChave = rs.getInt("VALOR2");
				String query = "INSERT INTO OPERACOES_CAIXA VALUES (NULL, ?, 0,0,?, ?, ?);";
				ps = con.prepareStatement(query);
				ps.setString(1, "Retirada");
				ps.setDouble(2, valorD);
				ps.setInt(3, ultChave);
				ps.setTime(4, java.sql.Time.valueOf(lt));
				ps.executeUpdate();
				return valorD;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Valor Digitado Invalido");
		}
		return 0;
	}

	public double[] getControleCaixaValues(Connection con) {
		double sangria = 0;
		double valorTroco = 0;
		double valores[] = { 0, 0 };
		// SETVALOR RET
		try {
			ps = con.prepareStatement("SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1;");
			rs = ps.executeQuery();
			if (rs.next()) {
				int chaveUltiCaixa = rs.getInt("VALOR2");
				if (chaveUltiCaixa == 0) {
					return valores;
				}
				// Obtem o valor do ultimo troco salvo
				ps = con.prepareStatement("SELECT TROCOCAIXA FROM OPERACOES_CAIXA "
						+ "WHERE CONTROLECAIXA_IDCAIXA = ? AND OPERACAO = 'Caixa Aberto';");
				ps.setInt(1, chaveUltiCaixa);
				rs = ps.executeQuery();
				if (rs.next()) {
					valorTroco = rs.getDouble("TROCOCAIXA");
				}
				// Obtem o valor de Retiradas
				ps = con.prepareStatement("SELECT VALORDINHEIRO FROM OPERACOES_CAIXA"
						+ " WHERE CONTROLECAIXA_IDCAIXA = ? AND OPERACAO = 'Retirada'");
				ps.setInt(1, chaveUltiCaixa);
				rs = ps.executeQuery();
				while (rs.next()) {
					sangria = sangria + rs.getDouble("VALORDINHEIRO");
				}
				valores[0] = valorTroco;
				valores[1] = sangria;
			}
			return valores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valores;
	}

	public double[] getControleCaixaValuesById(Connection con, Integer id) {
		double valores[] = { 0, 0 ,0,0};
		// SETVALOR RET
		try {

			if (id == 0) {
				return valores;
			}
			// Obtem o valor do ultimo troco salvo
			ps = con.prepareStatement("SELECT TROCOCAIXA,VALORDINHEIRO,VALORCART FROM OPERACOES_CAIXA "
					+ "WHERE CONTROLECAIXA_IDCAIXA = ? AND OPERACAO = 'Caixa Fechado';");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				valores[0] = rs.getDouble("TROCOCAIXA");
				valores[1] = rs.getDouble("VALORCART");
				valores[2] = rs.getDouble("VALORDINHEIRO");
			}
			
			ps = con.prepareStatement("SELECT SUM(V.VALORTOT) AS PIX FROM VENDAS V "
					+ "WHERE CONTROLECAIXA_IDCAIXA = ? AND V.TIPOPAGAMENTO = 'PIX'");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if(rs.next()) {
				valores[3] = rs.getDouble("PIX");
			}
			return valores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valores;
	}

	// Classe Fechamento

	// Insere os valores do caixa na classe movimento
	public int setValoresMovimento(Connection con, Time time, double dinhe, double cart, double troco) {
		try {

			ps = con.prepareStatement("SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1; ");
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt("VALOR2");
				String query = "INSERT INTO OPERACOES_CAIXA VALUES (NULL,?,?,?,?,?,?);";
				ps = con.prepareStatement(query);
				ps.setString(1, "Caixa Fechado");
				ps.setDouble(2, troco);
				ps.setDouble(3, cart);
				ps.setDouble(4, dinhe);
				ps.setInt(5, i);
				ps.setTime(6, java.sql.Time.valueOf(LocalTime.now()));
				ps.executeUpdate();
				return i;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Valores Invï¿½lidos");
		}
		return 0;
	}

	public String getFuncioCaixaAtual(Connection con) {
		try {
			// Obtem a chave do caixa atual e utiliza para obter o funcionario de chave
			// equivalente
			ps = con.prepareStatement("SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1");
			rs = ps.executeQuery();
			if (rs.next()) {
				int chaveCaixaAtual = rs.getInt("VALOR2");

				ps = con.prepareStatement("SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?");
				ps.setInt(1, chaveCaixaAtual);
				rs = ps.executeQuery();
				if (rs.next()) {
					String funcio = rs.getString("FUNCIONARIO");
					return funcio;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Copia todos as vendas do ultimo caixa para o relatorio
	public double convertTable(Connection con, ArrayList<PrintRelatoriosProds> prodTabela, int idCaixa) {
		try {
			System.out.println("IDCAIXA " + idCaixa);
			double somaTot = 0.0;
			String query = "SELECT P.CODBARRA,P.DESCRICAO, V.QUANTI, V.VALORDINHEIRO, V.VALORCARTAO, V.VALORTOT,"
					+ "V.HORA FROM VENDAS V INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
					+ "WHERE CONTROLECAIXA_IDCAIXA = ?;";
			ps = con.prepareStatement(query);
			ps.setInt(1, MainVenda.IdCaixa);
			rs = ps.executeQuery();

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

			ps = con.prepareStatement("UPDATE SISTEMA SET VALOR = null, VALOR2 = null WHERE IDSYS = 1");
			ps.executeUpdate();

			JOptionPane.showMessageDialog(new JFrame(), "Caixa Fechado");
			return somaTot;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Nï¿½o foi Possï¿½vel Salvar, Chece os valores e tente Novamento");
			e.printStackTrace();
			return 0.0;
		}
	}
	/**Obtem todas as recargas para impressï¿½o
	 * @return */
	public ArrayList<Recarga> getRecargas(Connection con,Integer idCaixa) {
		try {
			ArrayList<Recarga> recargas = new ArrayList<Recarga>();
			ps = con.prepareStatement("SELECT RECARGA,NUMERO,VALOR FROM RECARGAS WHERE CONTROLECAIXA_IDCAIXA = ?");
			ps.setInt(1, idCaixa);
			rs = ps.executeQuery();
			while(rs.next()) {
				Recarga recarg = new Recarga();
				recarg.setOperadora(rs.getString("RECARGA"));
				recarg.setNumero(rs.getString("NUMERO"));
				recarg.setValor(rs.getDouble("VALOR"));
				recargas.add(recarg);
			}
			return recargas;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
