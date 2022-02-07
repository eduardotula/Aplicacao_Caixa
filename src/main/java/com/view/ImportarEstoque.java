package com.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import com.model.CustomSQL;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.model.ObjetoProdutoImport;

import net.miginfocom.swing.MigLayout;

public class ImportarEstoque extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNamesImpo = new String[] { "Chave", "Cod Barra", "Descrição", "Quantidade", "V.Venda" };
	private static String[] columnNamesEsto = new String[] { "Chave", "Codigo", "Produto", "Quantidade", "V.Venda" };
	private boolean[] columnEditablesImpo = new boolean[] { false, false, false, false, false };
	private static boolean[] columnEditablesEsto = new boolean[] { false, false, false, false, false };
	private static Class<?>[] classesTableEsto = new Class<?>[] { Integer.class, String.class, String.class,
			Integer.class, Double.class };
	private Class<?>[] classesTableImpo = new Class<?>[] { Integer.class, String.class, String.class, Integer.class,
			Double.class };
	private NumberFormat nf = NumberFormat.getInstance();
	private static DefaultModels estomodel;
	private static TableRowSorter<DefaultModels> rowSorterEsto;

	// Visuais
	JScrollPane scrollPane = new JScrollPane();
	JButton btnImportar = new JButton("Importar");
	JButton btnCancelar = new JButton("Cancelar");
	private JTable tableImpor = new JTable();
	private JScrollPane scrollImpor = new JScrollPane();
	private final JButton btnApagarLi = new JButton("ApagarLinha");
	private final JScrollPane scrollEsto = new JScrollPane();
	private final static JTable tableEsto = new JTable();
	private final JLabel lblBusca = new JLabel("Buscar");
	private final JTextField txtBusca = new JTextField();
	private final JLabel lblQuanti = new JLabel("Quantidade");
	private final JTextField txtQuanti = new JTextField();
	private final JLabel lblValor = new JLabel("Valor de Venda");
	private final JTextField txtValor = new JTextField();
	private final JButton btnAdicionar = new JButton("Adicionar");
	private final JLabel lblProdEsto = new JLabel("Produtos em Estoque");
	private final JLabel lblImport = new JLabel("Produtos para Importar");
	private final JButton btnCadas = new JButton("Cadastrar Novo Produto");
	private final JButton btnEditarNome = new JButton("Editar Nome");

	public ImportarEstoque(Connection con) {
		super("Importar");
		getContentPane().setBackground(SystemColor.window);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		txtValor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtValor.setColumns(6);
		txtBusca.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtBusca.setColumns(10);
		limparCampos();
		setSize(1000, 600);
		scrollImpor.setViewportView(tableImpor);
		scrollEsto.setViewportView(tableEsto);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(false);
		DefaultModels importModel = new DefaultModels(columnNamesImpo, columnEditablesImpo, classesTableImpo);
		tableImpor.setModel(importModel);
		estomodel = new DefaultModels(columnNamesEsto, columnEditablesEsto, classesTableEsto);
		tableEsto.setModel(estomodel);
		rowSorterEsto = new TableRowSorter<DefaultModels>(estomodel);
		tableEsto.setRowSorter(rowSorterEsto);
		addRowTableEstoqueImport(con, estomodel,
				"SELECT IDPROD, CODBARRA, DESCRICAO , QUANTIDADE, VLR_ULT_VENDA, PRECO_CUSTO FROM PRODUTOS");

		// Liteners

		// Double Click na tabela estoque
		tableEsto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					JTable t = (JTable) arg0.getSource();
					int row = t.getSelectedRow();
					String aValue = (String) t.getValueAt(row, 2);
					setTextBusca(row, aValue); // Se a ultima venda deste produto for != 0 o valor sera adicionado no
												// campo de valor
				}
			}
		});

		btnEditarNome.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = tableImpor.getSelectedRow();
				if (row > -1) {
					int modelRow = tableImpor.convertRowIndexToModel(row);
					String novoNome = JOptionPane.showInputDialog("Insira um novo nome",
							(String) importModel.getValueAtStr(modelRow, 2));
					if (novoNome != null && novoNome.length() > 0) {
						if (novoNome.length() <= 80) {

							importModel.setValueAt(novoNome, modelRow, 2);

						} else {
							JOptionPane.showMessageDialog(null, "Nome não pode exeder 80 caracteres");
						}
					} else {
						JOptionPane.showMessageDialog(null, "Nome não pode ser vazio");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Nenhum Produto selecionado");
				}
			}
		});
		btnCadas.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new FrameCadasProds(con, new ObjetoProdutoImport[] {}).addRowModel("", "", 0, "0.00", "0.00");
				;

			}
		});
		// txtValor tecla enter
		txtValor.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					btnAdicionar.requestFocus();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		btnAdicionar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int rowSele = tableEsto.getSelectedRow();
				int chave = (int) tableEsto.getValueAt(rowSele, 0);
				String cod = (String) tableEsto.getValueAt(rowSele, 1);
				String desc = (String) tableEsto.getValueAt(rowSele, 2);
				try {
					int quanti = Integer.parseInt(txtQuanti.getText());
					Double valor = Double.parseDouble(txtValor.getText().replace(',', '.'));
					importModel.addRow(new Object[] { chave, cod, desc, quanti, valor });
					txtBusca.requestFocus();
					limparCampos();
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Quantidade ou Valor Invãlido");
				}
			}
		});
		btnApagarLi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int seleRow = tableImpor.getSelectedRow();
				if (seleRow != -1) {
					importModel.removeRow(seleRow);
				} else {
					JOptionPane.showMessageDialog(null, "Nenhuma Linha Selecionada");
				}
			}
		});

		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnImportar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int tam = tableImpor.getRowCount();
				if (tableImpor.getCellEditor() != null) {
					tableImpor.getCellEditor().stopCellEditing();
				}
				int linha = 0;
				try {
					DBOperations.startTransaction(con);
					for (int i = 0; i < tam; i++) {
						linha = i;
						linha++;
						int chave = (int) tableImpor.getValueAt(0, 0);
						String cod = (String) tableImpor.getValueAt(0, 1);
						String desc = (String) tableImpor.getValueAt(0, 2);
						int quanti = (int) tableImpor.getValueAt(0, 3);
						double preco = (double) tableImpor.getValueAt(0, 4);

						ObjetoProdutoImport prod = new ObjetoProdutoImport(cod, desc, quanti, preco, 0.0, chave);
						CustomSQL.UpdateItemBd(con, prod.getChave(), prod.getCodBa(), prod.getProd(),
								prod.getValorUltV(), prod.getQuanti(), prod.getValorCusto());

						if (quanti == 0) {
							// Busca pelo ulti valor de venda de um produto
							Double valorProd = (Double) DBOperations.selectSqlList(con,
									"SELECT VLR_ULT_VENDA FROM PRODUTOS WHERE IDPROD = ?", prod.getChave()).get(0);
							String funcio = DBOperations.selectSql1Dimen(con, "SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?", new String[0], MainVenda.IdCaixa)[0];
							// Insere um novo registro de mudança de preco do produto
							DBOperations.DmlSql(con, "INSERT INTO MUDANCA_PRECO VALUES(NULL,?,?,?,?,?,?)",
									new Object[] { valorProd, prod.getValorUltV(), funcio,
											prod.getChave() });

						} else {
							//Busca pelo nome do funcionario do caixa atual
							Integer[] idCaixa = DBOperations.selectSql1Dimen(con, "SELECT VALOR2 FROM SISTEMA WHERE IDSYS = 1", new Integer[0]);
							if(idCaixa.length == 0) throw new Exception("idCaixa = null");
							String funcio = DBOperations.selectSql1Dimen(con, "SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?", new String[0], idCaixa[0])[0];
							
							DBOperations.DmlSql(con, "INSERT INTO ENTRADAS VALUES (NULL, ?,?, ?, ?, ?, ?,?,?)",
									new Object[] { quanti, 0.0, preco, prod.getChave(), LocalDate.now(),
											LocalTime.now(), funcio, 1 });

						}
					}
					DBOperations.commit(con);
					JOptionPane.showMessageDialog(null, "Produtos Importados com Sucesso");
					refreshEstoque(con);
					MainVenda.refreshEstoque();
				} catch (ClassCastException e1) {
					e1.printStackTrace();
					DBOperations.rollBack(con);
					JOptionPane.showMessageDialog(null, "Falha ao importar produtos, por favor cheque os campos da linha " + linha);
				} catch (SQLException e2) {
					DBOperations.rollBack(con);
					JOptionPane.showMessageDialog(null,
							String.format("Falha ao importar produtos Erro: %s", e2.getMessage()));
					e2.printStackTrace();
				} catch (Exception e3) {
					DBOperations.rollBack(con);
					JOptionPane.showMessageDialog(null,
							String.format("Falha ao importar produtos Erro: %s", e3.getMessage()));
					e3.printStackTrace();
				}

			}
		});

		// listener para tecla enter
		txtBusca.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					int row = tableEsto.getSelectionModel().getLeadSelectionIndex();
					String aValue = (String) tableEsto.getValueAt(row, 2);
					setTextBusca(row, aValue); // Se a ultima venda deste produto for != 0 o valor sera adicionado no
												// campo de valor
					txtQuanti.requestFocus();
				} else if (e.getKeyCode() == 38) { // key para baixo
					int row = tableEsto.getSelectionModel().getLeadSelectionIndex() - 1;
					tableEsto.getSelectionModel().setSelectionInterval(row, row);
				} else if (e.getKeyCode() == 40) { // key para cima
					int row = tableEsto.getSelectionModel().getLeadSelectionIndex() + 1;
					tableEsto.getSelectionModel().setSelectionInterval(row, row);
				}
			}
		});
		// Busca o texto na tabela estoque
		txtBusca.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				codeSearch(txtBusca.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				codeSearch(txtBusca.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				codeSearch(txtBusca.getText());
			}
		});

		getContentPane().setLayout(new MigLayout("", "[400.00,grow][]", "[31.00][][][grow][]"));
		lblBusca.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblImport.setFont(new Font("Tahoma", Font.BOLD, 15));

		getContentPane().add(lblImport, "cell 1 0,alignx center");
		lblProdEsto.setFont(new Font("Tahoma", Font.BOLD, 15));

		getContentPane().add(lblProdEsto, "cell 0 2,alignx center");

		getContentPane().add(btnCadas, "flowx,cell 0 4");
		getContentPane().add(btnCancelar, "flowx,cell 1 4,alignx right");
		getContentPane().add(lblBusca, "flowx,cell 0 0,growy");
		getContentPane().add(scrollImpor, "cell 1 1 1 3,grow");
		getContentPane().add(scrollEsto, "cell 0 3,grow");

		getContentPane().add(btnEditarNome, "cell 1 4,alignx right");
		getContentPane().add(btnApagarLi, "cell 1 4,alignx right");
		getContentPane().add(btnImportar, "cell 1 4,alignx right");
		getContentPane().add(txtBusca, "cell 0 0,grow");
		lblValor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().add(lblValor, "flowx,cell 0 1,growy");
		getContentPane().add(txtValor, "cell 0 1,growy");
		getContentPane().add(btnAdicionar, "cell 0 1,alignx left,growy");
		lblQuanti.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblQuanti.setForeground(Color.BLACK);
		getContentPane().add(lblQuanti, "cell 0 0,growy");
		txtQuanti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtQuanti.setColumns(4);
		// txtQuantidade tecla enter
		txtQuanti.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					txtValor.requestFocus();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		getContentPane().add(txtQuanti, "cell 0 0,growy");
		tableImpor.getColumnModel().getColumn(0).setMinWidth(30);
		tableImpor.getColumnModel().getColumn(0).setMaxWidth(30);
		tableImpor.getColumnModel().getColumn(1).setMinWidth(100);
		tableImpor.getColumnModel().getColumn(2).setPreferredWidth(3000);
		tableImpor.getColumnModel().getColumn(3).setMinWidth(70);
		tableImpor.getColumnModel().getColumn(4).setMinWidth(70);
		tableEsto.getColumnModel().getColumn(0).setMinWidth(0);
		tableEsto.getColumnModel().getColumn(0).setMaxWidth(0);
		tableEsto.getColumnModel().getColumn(0).setWidth(0);
		tableEsto.getColumnModel().getColumn(1).setMinWidth(100);
		tableEsto.getColumnModel().getColumn(2).setPreferredWidth(3000);
		tableEsto.getColumnModel().getColumn(3).setMinWidth(70);
		tableEsto.getColumnModel().getColumn(4).setMinWidth(70);
	}

	// Atualiza o Campo de texto com base na String de busca
	private void setTextBusca(int row, String desc) {
		double vlr = 0.0;
		// converte a row ta table para o seu valor no modelo
		int modelRow = tableEsto.convertRowIndexToModel(row);

		if (tableEsto.getValueAt(row, 4) != null) {
			vlr = (double) tableEsto.getValueAt(row, 4);
		}
		txtBusca.setText(desc);
		int viewRow = tableEsto.convertRowIndexToView(modelRow);
		tableEsto.getSelectionModel().setSelectionInterval(viewRow, viewRow);
		txtValor.setText(nf.format(vlr));
		txtQuanti.requestFocus();

	}

	private void codeSearch(String busca) {
		if (busca.length() == 0) {
			rowSorterEsto.setRowFilter(null);
		} else {

			rowSorterEsto.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca))); // Ordena rows com a flag
																								// de Case-insensitivity
			tableEsto.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	private void limparCampos() {
		txtBusca.setText("");
		txtQuanti.setText("0");
		txtValor.setText("");
	}

	public static void refreshEstoque(Connection con) {
		estomodel = new DefaultModels(columnNamesEsto, columnEditablesEsto, classesTableEsto);
		tableEsto.setModel(estomodel);
		rowSorterEsto = new TableRowSorter<DefaultModels>(estomodel);
		tableEsto.setRowSorter(rowSorterEsto);
		addRowTableEstoqueImport(con, estomodel,
				"SELECT IDPROD, CODBARRA, DESCRICAO , QUANTIDADE, VLR_ULT_VENDA FROM PRODUTOS");
		tableEsto.getColumnModel().getColumn(0).setMinWidth(0);
		tableEsto.getColumnModel().getColumn(0).setMaxWidth(0);
		tableEsto.getColumnModel().getColumn(0).setWidth(0);
		tableEsto.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableEsto.getColumnModel().getColumn(2).setPreferredWidth(200);
		tableEsto.getColumnModel().getColumn(3).setWidth(40);
		tableEsto.getColumnModel().getColumn(4).setWidth(40);
	}

	private static void addRowTableEstoqueImport(Connection con, DefaultModels modelProds, String query) {
		Object[] prod = new Object[5];

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				prod[0] = rs.getInt("IDPROD");
				prod[1] = rs.getString("CODBARRA");
				prod[2] = rs.getString("DESCRICAO");
				prod[3] = rs.getInt("QUANTIDADE");
				prod[4] = rs.getDouble("VLR_ULT_VENDA");
				modelProds.addRow(prod);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
