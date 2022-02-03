package com.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.viewadmin.FrameMenuAdmin;


public class DBVendas {
	
	PreparedStatement ps;
	ResultSet rs;
	SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");

	//Varios Usos
	
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
			PreparedStatement ps = FrameMenuAdmin.con.prepareStatement(query);
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
	
	public void insertFornecedor(String nome, String cnpj) {
		try {
			PreparedStatement ps = FrameMenuAdmin.con
					.prepareStatement("INSERT INTO FORNECEDORES VALUES(NULL,?,?)");
			ps.setString(1, nome);
			ps.setString(2, cnpj);
			ps.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	public Integer getFornecedorIdByName(String nome) {
		try {
			ps = FrameMenuAdmin.con.prepareStatement("SELECT ID FROM FORNECEDORES WHERE NOME = ?");
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
	
	public Integer getFornecedorIdByCNPJ(String cnpj) {
		try {
			ps = FrameMenuAdmin.con.prepareStatement("SELECT ID FROM FORNECEDORES WHERE CNPJ = ?");
			ps.setString(1, cnpj);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt("ID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getFornecedorNomeByCNPJ(String cnpj) {
		try {
			ps = FrameMenuAdmin.con.prepareStatement("SELECT NOME FROM FORNECEDORES WHERE CNPJ = ?");
			ps.setString(1, cnpj);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString("NOME");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getFornecedorNomeById(int id) {
		try {
			ps = FrameMenuAdmin.con.prepareStatement("SELECT NOME FROM FORNECEDORES WHERE ID = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString("NOME");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getDefaultFornecedor() {
		try {
			ps = FrameMenuAdmin.con.prepareStatement("SELECT ID FROM FORNECEDORES WHERE NOME = ?");
			ps.setString(1, "Loja");
			rs = ps.executeQuery();
			if(rs.next()) {
				int id = rs.getInt("ID");
				if(id == 0) {
					insertFornecedor("Loja", "");
					id = getDefaultFornecedor();
				}else {
					return id;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**Busca o preco de venda de um produto
	 * @return */
	public double getValorProd(Connection con, Integer id) {
		try {
			ps = con.prepareStatement("SELECT VLR_ULT_VENDA FROM PRODUTOS WHERE IDPROD = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getDouble("VLR_ULT_VENDA");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	public String[] getDadosImpressao(Connection con) {
		try {
			String[] dados = new String[6];
			ps = con.prepareStatement("SELECT * FROM CADASTRO_LOJA WHERE ID = 1");
			rs = ps.executeQuery();
			if(rs.next()) {
				dados[0] = rs.getString(2);
				dados[1] = rs.getString(3);
				dados[2] = rs.getString(4);
				dados[3] = rs.getString(5);
				dados[4] = rs.getString(6);
				return dados;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	//Busca impressora salva
	public String getImpressora(Connection con) {
		try {
			ps = con.prepareStatement("SELECT IMPRESSORA FROM CADASTRO_LOJA WHERE ID = 1");
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//Busca o Nome de Todos os funcionarios e retona em um array
	public String[] getFuncionario(Connection con) {
		try {
			java.sql.Statement ps = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = ps.executeQuery("SELECT NOME FROM FUNCIONARIOS");
			int size = 0;
			if (rs.last()) {
				  size = rs.getRow();
				  rs.beforeFirst(); 
				}
			String[] funcio = new String[size];
			int count = 0;
			while(rs.next()) {
				funcio[count] = rs.getString("NOME");
				count++;
			}
			return funcio;
		} catch (SQLException e) {
			e.printStackTrace();
			String[] v = {""};
			return v;
		}
	}
	//Deleta um produto de uma determinada tabela
	public void deletaritem(Connection con, String queryDelete, int item)throws Exception{

		try {
			System.out.println(item);
			ps = con.prepareStatement(queryDelete);
			ps.setInt(1, item);
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Adiciona todos os items do estoque no modelo de tabela
	public void addRowTableEstoque(Connection con, DefaultModels tableModel, String query) {
		Object[] prod = new Object[6];

		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("IDPROD");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTIDADE");
				prod[4] = rs.getDouble("PRECO_CUSTO");
				prod[5] = rs.getDouble("VLR_ULT_VENDA");

				
				tableModel.addRow(prod);
				tableModel.addDadoAtivo(rs.getInt("ITEN_ATIVO"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	//Adiciona todos os items do estoqUE
	public void addRowTableEstoqueVenda(Connection con, DefaultModels tableModel, String query) {
		Object[] prod = new Object[5];

		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("IDPROD");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTIDADE");
				prod[4] = rs.getDouble("VLR_ULT_VENDA");
				tableModel.addRow(prod);
				tableModel.addDadoAtivo(rs.getInt("ITEN_ATIVO"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	//Adiciona todos os items do estoque no modelo de tabela
	public void addRowTableEstoqueImport(Connection con, DefaultModels modelProds, String query) {
		Object[] prod = new Object[6];

		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("IDPROD");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTIDADE");
				prod[4] = rs.getDouble("PRECO_CUSTO");
				prod[5] = rs.getDouble("VLR_ULT_VENDA");
				modelProds.addRow(prod);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Classe Funcionarios
	public boolean getAllFuncion(Connection con, String query, DefaultModels tableModel) {
		try {
			Object[] d = new Object[2]; 
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				d[0] = rs.getInt("IDFUNC");
				d[1] = rs.getString("NOME");
				tableModel.addRow(d);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean removeFuncio(Connection con, int chave) {
		String query = "DELETE FROM FUNCIONARIOS WHERE IDFUNC = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, chave);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean addFuncio(Connection con, String nome) {
		String query = "INSERT INTO FUNCIONARIOS VALUES (NULL, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, nome);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Classe Entradas
	
	public void getAllEntrada(Connection con, DefaultModels model) {
		try {
			Object[] o = new Object[9];
			String query = "SELECT E.ID, P.CODBARRA, P.DESCRICAO, E.QUANTIDADE, E.VALOR_CUSTO,E.VALOR_VENDA, E.DATAENTRADA, E.HORAENTRADA, E.OPERADOR "
					+ "FROM ENTRADAS E INNER JOIN PRODUTOS P ON E.IDPROD = P.IDPROD ORDER BY E.ID DESC;";
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				o[0] = rs.getInt(1);
				o[1] = rs.getString(2);
				o[2] = rs.getString(3);
				o[3] = rs.getInt(4);
				o[4] = rs.getDouble(5);
				o[5] = rs.getDouble(6);
				o[6] = rs.getDate(7).toLocalDate();
				o[7] = rs.getTime(8).toLocalTime();
				o[8] = rs.getString(9);
				model.addRow(o);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getEntradaByDate(Connection con, LocalDate inicial, LocalDate dataFin, DefaultModels model) {
		try {
			String query = 
					"SELECT F.NOME, SUM(E.VALOR_CUSTO) AS SOMA, E.DATAENTRADA, E.HORAENTRADA, OPERADOR FROM ENTRADAS E "
							+ "INNER JOIN FORNECEDORES F ON F.ID = E.ID_FORNECEDOR "
							+ "WHERE E.DATAENTRADA BETWEEN ? AND ? "
							+ "GROUP BY F.NOME, E.DATAENTRADA, E.HORAENTRADA, OPERADOR";
			Object[] o = new Object[5];
			ps = con.prepareStatement(query);
			ps.setDate(1, java.sql.Date.valueOf(inicial));
			ps.setDate(2, java.sql.Date.valueOf(dataFin));
			rs = ps.executeQuery();
			while(rs.next()) {
				o[0] = rs.getString(1);
				o[1] = rs.getDouble(2);
				o[2] = rs.getDate(3).toLocalDate();
				o[3] = rs.getTime(4).toLocalTime();
				o[4] = rs.getString(5);
				model.addRow(o);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean addProdEntradas(Connection con, Integer quanti,Double valorCompra,Double valorVenda,
			Integer idProd, LocalDate data, LocalTime hora, String operador, int idFornece) {
		try {
			String query = "INSERT INTO ENTRADAS VALUES (NULL, ?,?, ?, ?, ?, ?,?,?)";
			ps = con.prepareStatement(query);
			ps.setInt(1, quanti);
			ps.setDouble(2, valorCompra);
			ps.setDouble(3, valorVenda);
			ps.setInt(4, idProd);
			ps.setDate(5, java.sql.Date.valueOf(data));
			ps.setTime(6, java.sql.Time.valueOf(hora));
			ps.setString(7, operador);
			ps.setInt(8, idFornece);
			ps.execute();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	//Classe Estoque
	//Atualiza as propriedades do produtod editado
	public boolean UpdateItemBd(Connection con, String query, int chave, String codBarra, String descr, double valor, Integer quantidade, double valorC) {
		//String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ?,QUANTIDADE = ?, PRECO_CUSTO = ? WHERE IDPROD = ?;";
		//ou
		//	String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ?,QUANTIDADE = QUANTIDADE + ? ,PRECO_CUSTO = ? WHERE IDPROD = ?;";

		try {
			ps = con.prepareStatement(query);
			ps.setString(1, codBarra);
			if(descr.length() >= 80) {descr = descr.substring(0, 80); }
			ps.setString(2, descr);
			ps.setDouble(3, valor);
			ps.setInt(4, quantidade);
			ps.setDouble(5, valorC);
			ps.setInt(6, chave);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/** Adiciona um registro de mudanï¿½a de valor de venda*/
	
	public boolean InsertAlterPreco(Connection con, Double valorAnt, Double valorAtt,String funcionario, Integer idProd) {
		String query = "INSERT INTO MUDANCA_PRECO VALUES(NULL,?,?,?,?,?,?) ";
		try {
			ps = con.prepareStatement(query);
			ps.setDouble(1, valorAnt);
			ps.setDouble(2, valorAtt);
			ps.setTime(3, Time.valueOf(LocalTime.now()));
			ps.setDate(4, Date.valueOf(LocalDate.now()));
			ps.setString(5, funcionario);
			ps.setInt(6, idProd);
			ps.executeUpdate();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**Adiciona altera a quantidade +1*/
	public boolean UpdateItemBdQuantiplus1(Connection con, Integer id) {
		String query = "UPDATE PRODUTOS SET QUANTIDADE = QUANTIDADE + 1 WHERE IDPROD = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**Adiciona altera a quantidade */
	public boolean UpdateItemBdQuantiplus(Connection con, Integer id, Integer quanti) {
		String query = "UPDATE PRODUTOS SET QUANTIDADE = QUANTIDADE + ? WHERE IDPROD = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, quanti);
			ps.setInt(2, id);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public void setItemStatus(Connection con, int chave, int status) {
		String query = "UPDATE PRODUTOS SET ITEN_ATIVO = ? WHERE IDPROD = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, status);
			ps.setInt(2, chave);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getLasGeneratedKey(Connection con) {
		String query = "SELECT MAX(IDPROD) FROM PRODUTOS";
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}
	public void addRelatorioEstoque(Connection con, DefaultModels model, LocalDate[] datas) {
		List<Integer> idProdsListados = new ArrayList<Integer>();
		String query = "SELECT P.IDPROD,P.CODBARRA,P.DESCRICAO, SUM(V.QUANTI) FROM VENDAS V "
				+ "INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
				+ "INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
				+ "WHERE C.DATA BETWEEN ? AND ? "
				+ "GROUP BY P.IDPROD, P.CODBARRA,P.DESCRICAO;";
		
		try {
			con.setAutoCommit(false);
			Object[] a = new Object[6];
			PreparedStatement ps2;
			ResultSet rs2;
			ps = con.prepareStatement(query);
			ps.setDate(1, Date.valueOf(datas[0]));
			ps.setDate(2, Date.valueOf(datas[1]));
			rs = ps.executeQuery();
			while(rs.next()) {
				a[0] = rs.getInt("IDPROD");
				idProdsListados.add((Integer) a[0]);
				a[1] = rs.getString("CODBARRA");
				a[2] = rs.getString("DESCRICAO");
				a[4] = rs.getInt("SUM");
				String query2 = "SELECT E.IDPROD, SUM(E.QUANTIDADE) FROM ENTRADAS E "
						+ "WHERE E.IDPROD = ? AND "
						+ "E.DATAENTRADA between ? AND ? "
						+ "GROUP BY E.IDPROD;";
				ps2 = con.prepareStatement(query2);
				ps2.setInt(1, (int) a[0]);
				ps2.setDate(2, Date.valueOf(datas[0]));
				ps2.setDate(3, Date.valueOf(datas[1]));
				rs2 = ps2.executeQuery();
				if(rs2.next()) {
					a[3] = rs2.getInt("SUM");
				}else {
					a[3] = 0;
				}
				a[5] = (int)a[3] - (int)a[4];
				model.addRow(a);
			}
			//Busca por entradas sem vendas
			ps = con.prepareStatement("SELECT E.IDPROD,P.CODBARRA,P.DESCRICAO, SUM(E.QUANTIDADE) FROM ENTRADAS E "
					+ "INNER JOIN PRODUTOS P ON E.IDPROD = P.IDPROD "
					+ "WHERE E.DATAENTRADA BETWEEN ? AND ? "
					+ "GROUP BY E.IDPROD, P.CODBARRA,P.DESCRICAO;");
			ps.setDate(1, Date.valueOf(datas[0]));
			ps.setDate(2, Date.valueOf(datas[1]));
			rs = ps.executeQuery();
			while(rs.next()) {
				if(!idProdsListados.contains(rs.getInt("IDPROD"))){
					a[0] = rs.getString("IDPROD");
					a[1] = rs.getString("CODBARRA");
					a[2] = rs.getString("DESCRICAO");
					a[3] = rs.getInt("SUM");
					a[4] = 0;
					a[5] = rs.getInt("SUM");
					model.addRow(a);
				}
			}
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addMovimentoProduto(Connection con, DefaultModels model, Integer id) {
		String query = "SELECT V.QUANTI,P.PRECO_CUSTO,V.VALORTOT,C.DATA,V.HORA,C.FUNCIONARIO FROM VENDAS V "
				+ "INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
				+ "INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
				+ "WHERE V.IDPROD = ?";
		try {
			Object[] a = new Object[7];
			ps = con.prepareStatement(query);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()) {
				a[0] = "Venda do Produto";
				a[1] = -rs.getInt(1);
				a[2] =rs.getDouble(2);
				a[3] = rs.getDouble(3);
				a[4] = rs.getDate(4).toLocalDate();
				a[5] = rs.getTime(5).toLocalTime();
				a[6] = rs.getString(6);
				model.addRow(a);
			}
			ps = con.prepareStatement("SELECT QUANTIDADE,VALOR_CUSTO,DATAENTRADA,HORAENTRADA,OPERADOR "
					+ "FROM ENTRADAS WHERE IDPROD = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()) {
				a[0] = "Entrada do Produto";
				a[1] = rs.getInt(1);
				a[2] = rs.getDouble(2);
				a[3] = null;
				a[4] = rs.getDate(3).toLocalDate();
				a[5] = rs.getTime(4).toLocalTime();
				a[6] = rs.getString(5);
				model.addRow(a);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addMovimentoEstoque(Connection con, DefaultModels model, LocalDate dataIni, LocalDate dataFin) {
		String queryQuantiEntra = "SELECT SUM(QUANTIDADE) WHERE DATA BETWEEN ? AND ?;";
		try {
			ps = con.prepareStatement(queryQuantiEntra);
			ps.setDate(1, Date.valueOf(dataIni));
			ps.setDate(2, Date.valueOf(dataFin));
			rs = ps.executeQuery();
			while(rs.next()) {
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Adiciona um novo produto
	public boolean adicionarItemBd(Connection con, ObjetoProdutoImport prod)throws Exception {
		String query = "INSERT INTO PRODUTOS VALUES (NULL,?,?,?,?,NULL, ?, 1);";
		 try {
			ps = con.prepareStatement("SELECT IDPROD FROM PRODUTOS WHERE CODBARRA = ?");
			ps.setString(1, prod.getCodBa());
			rs = ps.executeQuery();
			if(rs.next()) {
				JOptionPane.showMessageDialog(null, "Codigo jï¿½ cadastrado");
				throw new Exception();
			}else {
				ps = con.prepareStatement(query);
				if(prod.getProd().length() >= 80) {prod.setProd(prod.getProd().substring(0, 80)); }
				prod.setProd(prod.getProd().toUpperCase());
				System.out.println(prod.getProd() + " Cadastrado");
				ps.setString(1, prod.getCodBa());
				ps.setString(2, prod.getProd());
				ps.setDouble(3, prod.getValorUltV());
				ps.setInt(4, prod.getQuanti());
				ps.setDouble(5, prod.getValorCusto());
				ps.executeUpdate();
				return true;
			}
		} catch (DataTruncation e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Codigo de Barras Invï¿½lido");
		} catch(SQLException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "Dados Invï¿½lidos");
			
		}
		return false;
	}
	//Adiciona um novo produto sem confirmaï¿½áo de codigo
	public boolean adicionarItemBdsemConfir(Connection con, ObjetoProdutoImport prod)throws Exception {
		String query = "INSERT INTO PRODUTOS VALUES (NULL,?,?,?,?,NULL, ?, 1);";
		 try {
				ps = con.prepareStatement(query);
				if(prod.getProd().length() >= 80) {prod.setProd(prod.getProd().substring(0, 80)); }
				prod.setProd(prod.getProd().toUpperCase());
				System.out.println(prod.getProd() + " Cadastrado");
				ps.setString(1, prod.getCodBa());
				ps.setString(2, prod.getProd());
				ps.setDouble(3, prod.getValorUltV());
				ps.setInt(4, prod.getQuanti());
				ps.setDouble(5, prod.getValorCusto());
				ps.executeUpdate();
				return true;
			
		} catch (DataTruncation e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Codigo de Barras Invï¿½lido");
		} catch(SQLException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "Dados Invï¿½lidos");
			
		}
		return false;
	}
	
	
	//Procura se o CodBarra ja Existe no estoque
	public ObjetoProdutoImport searchCodEstoque(Connection con, String codProd) {
		try {
			ObjetoProdutoImport prod = new ObjetoProdutoImport();
			ps = con.prepareStatement("SELECT IDPROD, CODBARRA,DATA_ULT_VENDA, ITEN_ATIVO,VLR_ULT_VENDA, DESCRICAO FROM PRODUTOS WHERE CODBARRA = ?");
			ps.setString(1, codProd);
			if(codProd == null ) { return null;}
			System.out.println(codProd);
			rs = ps.executeQuery();
			if(rs.next()) {
				prod.setCodBa(rs.getString("CODBARRA"));
				prod.setProd(rs.getString("DESCRICAO"));
				prod.setDataUltVe(rs.getDate("DATA_ULT_VENDA"));
				prod.setValorUltV(rs.getDouble("VLR_ULT_VENDA"));
				prod.setQuanti(0);
				prod.setItenAtivo(rs.getString("ITEN_ATIVO").toCharArray()[0]);
				prod.setChave(rs.getInt("IDPROD"));
				return prod;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	//Procura se o produto ja Existe no estoque de acordo com o nome
	public ObjetoProdutoImport searchNomeEstoque(Connection con, String descricao) {
		try {
			ObjetoProdutoImport prod = new ObjetoProdutoImport();
			ps = con.prepareStatement("SELECT IDPROD, CODBARRA,DATA_ULT_VENDA, ITEN_ATIVO,VLR_ULT_VENDA, DESCRICAO FROM PRODUTOS WHERE DESCRICAO = ?");
			ps.setString(1, descricao);
			if(descricao == null ) { return null;}
			System.out.println(descricao);
			rs = ps.executeQuery();
			if(rs.next()) {
				prod.setCodBa(rs.getString("CODBARRA"));
				prod.setProd(rs.getString("DESCRICAO"));
				prod.setDataUltVe(rs.getDate("DATA_ULT_VENDA"));
				prod.setValorUltV(rs.getDouble("VLR_ULT_VENDA"));
				prod.setQuanti(0);
				prod.setItenAtivo(rs.getString("ITEN_ATIVO").toCharArray()[0]);
				prod.setChave(rs.getInt("IDPROD"));
				return prod;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	//Classe ControleCaixa
	//Preenche tabela agrupada de caixa
	public void addRowTableControle(Connection con, DefaultTableModel tableModel, String query) {
		try {
			Object[] d = new Object[5];
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				d[0] = rs.getInt("IDCAIXA");
				d[1] = rs.getDate("DATA").toLocalDate();
				d[4] = rs.getString("FUNCIONARIO");
				d[2] = "Ver Vendas";
				d[3] = "Ver Operaï¿½áoes";
				tableModel.addRow(d);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public double[] getFechamentoSoma(Connection con, String query) {
		double[] somas = new double[] {0,0,0};
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			if(rs.next()) {
				somas[0] = rs.getDouble("SUMT");
				somas[1] = rs.getDouble("SUMC");
				somas[2] = rs.getDouble("SUMD");
				return somas;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return somas;
	}
	public double getVendasSoma(Connection con, String query) {
		double soma = 0;
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			if(rs.next()) {
				soma = rs.getDouble("SUMV");
				return soma;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return soma;
	}

	//Classe ControleMovimentoCaixa
	//Preenche a tabela de Controle de Caixa
	public void addRowTableCaixa(Connection con,int chaveSele, DefaultModels modelMovimento ) {
		try {
			Object[] prod =  new Object[7];
			String query = "SELECT ID_OPERACOES, OPERACAO, TROCOCAIXA, VALORDINHEIRO, VALORCART"
					+ ", HORA FROM OPERACOES_CAIXA WHERE CONTROLECAIXA_IDCAIXA = " + chaveSele + ";";
			ps = con.prepareStatement(query);
			System.out.println(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("ID_OPERACOES");
				prod[1] = rs.getString("OPERACAO");
				prod[2] = rs.getDouble("TROCOCAIXA");
				prod[3] = rs.getDouble("VALORDINHEIRO");
				prod[4] = rs.getDouble("VALORCART");
				prod[5] = rs.getTime("HORA").toLocalTime();
				modelMovimento.addRow(prod);
			}


		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o foi possivel obter Caixa");
		}
	}


	//Classe Relatorios 
	public Double getRecargas(Connection con, DefaultModels model, String query) {
		Object[] o = new Object[8];
		Double soma = 0.0;
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				o[0] = rs.getInt(1);
				o[1] = rs.getString(2);
				o[2] = rs.getDouble(3);
				soma = soma + rs.getDouble("VALOR");
				o[3] = rs.getTime(4);
				o[4] = rs.getDate(5).toLocalDate();
				o[5] = rs.getString(6);
				o[6] = rs.getString(7);
				model.addRow(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return soma;
	}
	//Preenche a tabela de Produtos Vendidos
	public double addRowTableRelatoriosNoGroup(Connection con, String query, DefaultModels vendasModel) {
		Object prod[] = new Object[12];
		System.out.println(query);
		try {
			Double somaAtual = 0.0;
			Double somaTot = 0.0;
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("CODESTO");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTI");
				prod[4] = rs.getDouble("VALORUNI");
				prod[5] = rs.getDouble("VALORDINHEIRO");
				prod[6] = rs.getDouble("VALORCARTAO");
				somaAtual = rs.getDouble("VALORTOT");
				somaTot = somaAtual + somaTot;
				prod[7] = somaAtual;
				prod[8] = rs.getString("TIPOPAGAMENTO");
				prod[9] = rs.getDate("DATA").toLocalDate();
				prod[10] = rs.getTime("HORA").toLocalTime();
				prod[11] = rs.getInt("IDPROD");
				vendasModel.addRow(prod);
			}
			return somaTot;
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o foi possivel obter Vendas");
			return 0.0;
		}
	}
	public double addRowTableRelatoriosGroup(Connection con, String query, DefaultTableModel vendasModel) {
		try {
			double somaAtual = 0;
			double somaTot = 0;
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			Object prodGroup[] = new Object[7];
			System.out.println("pepega");
				while(rs.next()) {
					prodGroup[0] = rs.getString("CODBARRA");
					prodGroup[1] = rs.getString("DESCRICAO");
					prodGroup[2] = rs.getInt("QUANTI");
					prodGroup[3] = rs.getDouble("VALORDINHEIRO");
					prodGroup[4] = rs.getDouble("VALORCARTAO");
					somaAtual = rs.getFloat("VALORT");
					System.out.println(prodGroup[2]);
					somaTot = somaAtual + somaTot;
					prodGroup[5] = somaAtual;
					prodGroup[6] = rs.getInt("IDPROD");
					vendasModel.addRow(prodGroup);
				}
			return somaTot;
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o foi possivel obter Vendas");
			return 0.0;
		}
	}
	
	//Classe TrocasDevolucao
	public void addRowTableTrocasDevolu(Connection con, DefaultModels trocasModel) {
		String query = "SELECT T.IDTROCA, P.CODBARRA, P.DESCRICAO, T.VALOR, T.DATACOMPRA, T.DATA, T.HORA, T.OBSERVACAO "
				+ "FROM TROCAS T INNER JOIN PRODUTOS P ON T.PRODUTOS_IDPROD = P.IDPROD;";
		try {
			Object[] prod =  new Object[7];
			Object[] obs =  new Object[3];
			ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("IDTROCA");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getDouble("VALOR");
				prod[4] = rs.getDate("DATACOMPRA").toLocalDate();
				prod[5] = rs.getDate("DATA").toLocalDate();
				prod[6] = rs.getTime("HORA").toLocalTime();
				obs[2] = rs.getString("OBSERVACAO");
				obs[0] = "Descriï¿½áo";
				trocasModel.addRow(prod);
				trocasModel.addRow(obs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o foi possivel obter trocas");
		}
	}
	
	public boolean setTroca(Connection con,double valor, Integer id, LocalDate dataCompra, LocalDate data, LocalTime hora, String observa){
		try {
			ps = con.prepareStatement("INSERT INTO TROCAS VALUES (NULL, ?, ?, ?, ?, ?, ?)");
			ps.setDouble(1, valor);
			ps.setDate(2, java.sql.Date.valueOf(dataCompra));
			ps.setDate(3, java.sql.Date.valueOf(data));
			ps.setTime(4, java.sql.Time.valueOf(hora));
			ps.setString(5, observa);
			ps.setInt(6, id);
			ps.executeUpdate();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Classe VendasAPaga
	public void addRowTableVendasApaga(Connection con, DefaultTableModel vendasApaga) {
		String query = "SELECT V.IDPRODS,P.CODBARRA,P.DESCRICAO ,V.QUANTI, V.VALORDINHEIRO, V.VALORCARTAO,"
				+ " V.VALORTOT, V.DATAVENDA,"
				+ "V.HORAVENDA, V.DATAAPA, V.HORAAPA, V.MOTIVO FROM VENDAAPAGA V "
				+ "INNER JOIN PRODUTOS P ON V.PRODUTOS_IDPROD = P.IDPROD;";
		try {
			Object[] prod =  new Object[12];
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("IDPRODS");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTI");
				prod[4] = rs.getDouble("VALORDINHEIRO");
				prod[5] = rs.getDouble("VALORCARTAO");
				prod[6] = rs.getDouble("VALORTOT");
				prod[7] = rs.getDate("DATAVENDA").toLocalDate();
				prod[8] = rs.getTime("HORAVENDA");
				prod[9] = rs.getDate("DATAAPA").toLocalDate();
				prod[10] = rs.getTime("HORAAPA");
				prod[11] = rs.getString("MOTIVO");
				vendasApaga.addRow(prod);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "Nï¿½o foi possivel obter Vendas");
		}
	}

	//Classe ProdsNaoCadas
	public void addRowTableProdsNaoCadas(Connection con, DefaultTableModel prodsModel) {
		Object[] prod = new Object[3];
		String query = "SELECT * FROM CADASPRODS";
		try {
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()) {
				prod[0] = rs.getInt("IDPRODS");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prodsModel.addRow(prod);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	

}
