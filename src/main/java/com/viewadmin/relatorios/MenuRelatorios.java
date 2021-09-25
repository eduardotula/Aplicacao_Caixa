package com.viewadmin.relatorios;

import model.DBVendas;
import model.DefaultModels;
import tablerenders_editor.TableRendererCurrency;
import tablerenders_editor.TableRendererDate;
import tablerenders_editor.TableEditorCurrency;
import tablerenders_editor.TableEditorDateTime;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.viewadmin.FrameFiltroData;
import com.viewadmin.FrameMenuAdmin;

import control.TableOperations;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.event.ActionEvent;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JMenuBar;
import javax.swing.border.LineBorder;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class MenuRelatorios extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	// Colunas e Classes de colunas
	private String[] columnNamesNoGroup = new String[] { "Chave", "Cod Barra", "Descri\u00E7\u00E3o", "Quantidade",
			"Valor Uni", "Valor Dinheiro", "Valor Cart�o", "Valor Total", "Pagamento", "Data Venda", "Hora da Venda",
			"IDPROD" };
	private String[] columnNamesDB = new String[] { "CODESTO", "CODBARRA", "DESCRICAO", "QUANTI", "VALORUNI",
			"VALORDINHEIRO", "VALORCARTAO", "VALORTOT", "TIPOPAGAMENTO", "DATA", "HORA", "IDPROD" };
	private String nomeTabelaBd = "VENDAS";
	private boolean[] columnEditablesNoGroup = new boolean[] { false, false, false, true, true, true, true, true, true,
			true, true, true };
	private Class<?>[] classesTableNoGroup = new Class<?>[] { Integer.class, String.class, String.class, Integer.class,
			Double.class, Double.class, Double.class, Double.class, String.class, LocalDate.class, LocalTime.class,
			Integer.class };
	private String[] columnNamesGroup = new String[] { "Cod Barra", "Descri\u00E7\u00E3o", "Soma Quanti",
			"Soma Dinheiro", "Soma Cart�o", "Soma Valor", "IDPROD" };
	private Class<?>[] classesTableGroup = new Class<?>[] { String.class, String.class, Integer.class, Double.class,
			Double.class, Double.class, Integer.class };
	private boolean[] columnEditablesGroup = new boolean[] { false, false, false, false, false, false, false };
	private int[] columnCurrency = new int[3];

	private DBVendas dbVendas = new DBVendas();
	private ArrayList<Point> arrayCordBd = new ArrayList<Point>(); // Array que armazena cordenadas de modifica��es
	private DefaultModels vendasModel;
	private TableOperations tableOpera = new TableOperations();
	private TableRowSorter<TableModel> tableSorter;
	private DecimalFormat nf = new DecimalFormat("R$0.##");
	private HashMap<String, String> filtros = new HashMap<>();
	public String queryIni = "Select FIRST 1000 V.CODESTO,P.CODBARRA,P.DESCRICAO,V.QUANTI,V.VALORUNI,V.VALORDINHEIRO,V.VALORCARTAO,V.VALORTOT,V.TIPOPAGAMENTO,C.DATA,V.HORA, V.IDPROD"
			+ " FROM VENDAS V INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD"
			+ " INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
			+ " WHERE V.CODESTO >=1 ORDER BY V.CODESTO DESC;";

	public String query = "Select V.CODESTO,P.CODBARRA,P.DESCRICAO,V.QUANTI,V.VALORUNI,V.VALORDINHEIRO,V.VALORCARTAO,V.VALORTOT,V.TIPOPAGAMENTO,C.DATA,V.HORA, V.IDPROD"
			+ " FROM VENDAS V INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD"
			+ " INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
			+ " WHERE V.CODESTO >=1 ";
	private FrameRelatorioRecargas recarga;
	private LocalDate[] datas;

	// Objetos visuais
	private JTable tableVendas = new JTable();
	private JScrollPane scrollPane;
	private JButton btnFiltro = new JButton("Filtrar por Data");
	private JButton btnClean = new JButton("Limpar Filtros");
	private JButton print = new JButton("Imprimir");
	private JTextField txtBuscaItem = new JTextField();
	private JLabel lblBuscar = new JLabel("Buscar Item:");
	private JButton btnApagar = new JButton("Apagar");
	private final JPanel bottomPanel = new JPanel();
	private JTextField txtSomaAtual = new JTextField();
	private final JButton btnExport = new JButton("Export CSV");
	private JButton btnSalvar = new JButton("Salvar");
	private JButton btnRecargas = new JButton("Recargas");
	private JTextField txtSomaCart = new JTextField();
	private JTextField txtDinheiro = new JTextField();
	private JLabel lblSomaDinheiro = new JLabel("Soma Dinheiro");
	private JLabel lblSomaCartao = new JLabel("Soma Cart\u00E3o");
	private JLabel lblValorTot = new JLabel("Soma Atual");
	private final JMenuBar menuBar = new JMenuBar();
	private final JComboBox<String> comboPagam = new JComboBox<String>();
	private final JLabel lblPagamento = new JLabel("Pagamento");
	private final JLabel lblFunci = new JLabel("Funcionario");
	private final JComboBox<String> comboFuncionario = new JComboBox<String>();

	public MenuRelatorios(Connection con, String query) {
		// Janela
		super("Vendas");
		if (query != null) {
			this.queryIni = query;
			setbtnStatus(false);
		} else {
			setbtnStatus(true);
		}
		getFuncio();
		filtros.put("Pagamento", null);
		filtros.put("Funcionario", null);
		setLocationRelativeTo(null);
		nf.setMaximumFractionDigits(2);
		setMinimumSize(new Dimension(800, 400));
		setModelNoGroup(con, queryIni);
		tableSorter = new TableRowSorter<TableModel>(vendasModel);
		arrayCordBd.clear();
		setVisible(false);
		setRendersEditors();

		getContentPane().setLayout(new MigLayout("", "[784px,grow]", "[][grow][31px]"));
		getContentPane().add(lblBuscar, "flowx,cell 0 0");
		lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scrollPane = new JScrollPane(tableVendas);
		getContentPane().add(scrollPane, "cell 0 1,grow");
		tableVendas.setRowSorter(tableSorter);
		getContentPane().add(bottomPanel, "cell 0 2,growx,aligny top");
		bottomPanel.setLayout(new MigLayout("", "[grow][][][][][][]", "[20px]"));
		bottomPanel.add(lblSomaDinheiro, "cell 1 0,alignx right,aligny center");
		txtDinheiro.setColumns(10);
		bottomPanel.add(txtDinheiro, "cell 2 0,alignx left,aligny top");
		bottomPanel.add(lblSomaCartao, "cell 3 0,alignx left,aligny center");
		txtSomaCart.setColumns(10);
		bottomPanel.add(txtSomaCart, "cell 4 0,alignx left,aligny top");
		bottomPanel.add(lblValorTot, "cell 5 0,alignx right,aligny center");
		txtSomaAtual.setEditable(false);
		txtSomaAtual.setColumns(10);
		bottomPanel.add(txtSomaAtual, "cell 6 0,alignx right,aligny top");

		getContentPane().add(txtBuscaItem, "cell 0 0");
		txtBuscaItem.setColumns(20);
		getContentPane().add(btnSalvar, "cell 0 0");
		comboPagam
				.setModel(new DefaultComboBoxModel<String>(new String[] { "Todos", "Dinheiro", "Cart\u00E3o", "Pix" }));
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0)));

		setJMenuBar(menuBar);
		menuBar.add(btnFiltro);
		menuBar.add(btnApagar);
		menuBar.add(print);
		menuBar.add(btnClean);
		menuBar.add(btnExport);
		menuBar.add(btnRecargas);
		lblPagamento.setFont(new Font("Tahoma", Font.PLAIN, 14));

		getContentPane().add(lblPagamento, "cell 0 0,alignx left");
		getContentPane().add(comboPagam, "cell 0 0");


		getContentPane().add(lblFunci, "cell 0 0");

		getContentPane().add(comboFuncionario, "cell 0 0");
		// Listners
		// Salvar
		// Os dados que forem atualizados suas cordenadas ser�o armazenadas em um array
		// cord
		// que em seguida � armazenado em um arrayList
		btnSalvar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null, "Deseja Salvar os valores alterados?");
				if (res == 0 && arrayCordBd.size() > 0) {
					for (int i = 0; i < arrayCordBd.size(); i++) {
						// Checa se o valor a ser editado � uma data ou hora e o converte de String
						// para sua respectiva classe

						Point cord = arrayCordBd.get(i);
						Object dado = vendasModel.getValueAt(cord.x, cord.y); // obtem o dado editado da tabela
						System.out.println("tipo do dado" + dado.getClass());
						System.out.println("dados " + dado);
						System.out.println(cord.x + " cordSalva " + cord.y);
						String nomeColunaBd = columnNamesDB[cord.y]; // nome da coluna no bd equivalente na tabela
						int chave = (int) vendasModel.getValueAt(cord.x, 0); // valor da chave da linha editada
						String chaveNome = columnNamesDB[0]; // Nome da coluna chave no banco
						tableOpera.UpdateTabelaEditado(con, dado, chave, chaveNome, nomeTabelaBd, nomeColunaBd, cord);
					}
					arrayCordBd.clear();
					setModelNoGroup(con, queryIni);
				}
			}
		});
		// Listener moeda
		comboPagam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboPagam.getSelectedIndex() != 0) {
					String paga = (String) comboPagam.getSelectedItem();
					if (paga.contentEquals("Dinheiro")) {
						paga = "D";
					} else if (paga.contentEquals("Cart�o")) {
						paga = "C";
					} else if (paga.contentEquals("Pix")) {
						paga = "P";
					}
					addFiltro("Pagamento", String.format("V.TIPOPAGAMENTO = '%s'", paga));
				} else {
					removeFiltro("Pagamento");
				}
			}
		});

		comboFuncionario.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboFuncionario.getSelectedIndex() != comboFuncionario.getItemCount() - 1) {
					String funcio = (String) comboFuncionario.getSelectedItem();
					addFiltro("Funcionario", String.format("C.FUNCIONARIO = '%s'", funcio));
				} else {
					removeFiltro("Funcionario");
				}
			}
		});
		// Botao Recargas
		btnRecargas.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				recarga = new FrameRelatorioRecargas(con);
			}
		});
		// Export CSV
		btnExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				vendasModel.exportarCSV();
			}
		});
		btnClean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setbtnStatus(true);
				comboPagam.setSelectedIndex(0);
				comboFuncionario.setSelectedIndex(comboFuncionario.getItemCount() - 1);
				datas = null;
				setModelNoGroup(con, queryIni);
			}
		});
		print.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tableVendas.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});
		// Table Listner
		// Valores Editados
		vendasModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				Point cords = new Point(e.getFirstRow(), e.getColumn());
				arrayCordBd.add(cords);
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (recarga != null) {
					recarga.dispose();
				}
			}
		});
		// Hora Editada
		tableVendas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2 && tableVendas.getSelectedColumn() == 8) {
					String h = JOptionPane.showInputDialog("Insira um novo Valor");
					if (h != null && h.length() > 0) {
						int row = tableVendas.getSelectedRow();
						int column = tableVendas.getSelectedColumn();
						vendasModel.setValueAt(h, row, column);
					}
				}
			}
		});
		// txtBusca
		txtBuscaItem.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscaItem.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscaItem.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscaItem.getText());
			}
		});
		// Apagar
		btnApagar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int respo = JOptionPane.showConfirmDialog(new JFrame(), "Deseja apagar os produtos selecionados?");
					int[] rows = tableVendas.getSelectedRows();
					if (respo == 0 && rows.length > 0) {
						String query = "DELETE FROM VENDAS WHERE CODESTO = ?";
						tableOpera.ApagarSelecioTabela(con, vendasModel, tableVendas, query);

						setModelNoGroup(con, queryIni);
					}
				} catch (ArrayIndexOutOfBoundsException a) {
					a.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame("Erro"), "Nenhum Item Selecionado");
				}
			}
		});
		btnFiltro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FrameFiltroData filtro = new FrameFiltroData();
				comboPagam.setSelectedIndex(0);
				filtro.setGroupBtn(true);
				filtro.startGUIFiltroEsto();
				datas = filtro.getData();
				boolean group = filtro.getGroup();
				if (datas[0] != null && datas[1] != null) {
					if (group == false) {
						addFiltro("DATA", String.format("C.DATA BETWEEN '%s' AND '%s'", datas[0].toString(),
								datas[1].toString()));
					} else {
						String querygroup = String.format("Select P.CODBARRA,P.DESCRICAO,SUM(V.QUANTI) AS QUANTI "
								+ ",SUM(V.VALORDINHEIRO) AS VALORDINHEIRO,SUM(V.VALORCARTAO) AS VALORCARTAO, "
								+ "SUM(V.VALORTOT) AS VALORT, V.IDPROD "
								+ "FROM VENDAS V INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
								+ "INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
								+ "WHERE C.DATA BETWEEN '%s' AND '%s' " + "GROUP BY P.CODBARRA,P.DESCRICAO,V.IDPROD;",
								datas[0].toString(), datas[1].toString());
						setModelGroup(con, querygroup);
					}
				}

			}
		});
	}

	/*
	 * Adiciona um novo filtro para query padrao
	 * 
	 * @param String filtro para ser adicionado para query Ex. CODBARRA = 7846546
	 */
	private void addFiltro(String key, String filtro) {
		filtros.put(key, filtro);
		setModelNoGroupFiltro();
	}

	private void removeFiltro(String key) {
		filtros.put(key, null);
		setModelNoGroupFiltro();
	}

	private void getFuncio() {
		String[] funcio = dbVendas.getFuncionario(FrameMenuAdmin.con);
		DefaultComboBoxModel<String> com = new DefaultComboBoxModel<>(funcio);
		com.addElement("");
		comboFuncionario.setModel(com);
		comboFuncionario.setSelectedIndex(comboFuncionario.getItemCount() - 1);
	}

	private void setModelNoGroupFiltro() {
		String q = query;
		for (Entry<String, String> filtro : filtros.entrySet()) {
			if (filtro.getValue() != null) {
				q = q + String.format("AND %s ", filtro.getValue());
			}
		}
		q = q + " ORDER BY V.CODESTO DESC";
		setModelNoGroup(FrameMenuAdmin.con, q);
	}

	public void setModelNoGroup(Connection con, String query) {
		vendasModel = new DefaultModels(columnNamesNoGroup, columnEditablesNoGroup, classesTableNoGroup);
		dbVendas.addRowTableRelatoriosNoGroup(con, query, vendasModel);
		tableVendas.setModel(vendasModel);
		tableSorter = new TableRowSorter<TableModel>(vendasModel);
		tableVendas.setRowSorter(tableSorter);
		System.out.println("table size " + tableVendas.getRowCount());
		columnCurrency = new int[] { 5, 6, 7 };
		somarColunaView(columnCurrency);
		setRendersEditors();
		tableVendas.getColumnModel().getColumn(0).setPreferredWidth(30);
		tableVendas.getColumnModel().getColumn(1).setPreferredWidth(60);
		tableVendas.getColumnModel().getColumn(2).setPreferredWidth(500);
		tableVendas.getColumnModel().getColumn(3).setPreferredWidth(40);
		tableVendas.getColumnModel().getColumn(4).setPreferredWidth(61);
		tableVendas.getColumnModel().getColumn(5).setPreferredWidth(61);
		tableVendas.getColumnModel().getColumn(6).setPreferredWidth(50);
		tableVendas.getColumnModel().getColumn(7).setPreferredWidth(50);
		tableVendas.getColumnModel().getColumn(8).setPreferredWidth(30);
		tableVendas.getColumnModel().getColumn(9).setPreferredWidth(50);
		tableVendas.getColumnModel().getColumn(10).setPreferredWidth(30);
	}

	public void setModelGroup(Connection con, String query) {
		vendasModel = new DefaultModels(columnNamesGroup, columnEditablesGroup, classesTableGroup);
		dbVendas.addRowTableRelatoriosGroup(con, query, vendasModel);
		tableVendas.setModel(vendasModel);
		columnCurrency = new int[] { 3, 4, 5 };
		tableSorter = new TableRowSorter<TableModel>(vendasModel);
		tableVendas.setRowSorter(tableSorter);
		setbtnStatus(false);
		somarColunaView(columnCurrency);
		TableColumnModel m = tableVendas.getColumnModel();
		m.getColumn(3).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(5).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		tableVendas.getColumnModel().getColumn(0).setPreferredWidth(30);
		tableVendas.getColumnModel().getColumn(1).setPreferredWidth(500);
		tableVendas.getColumnModel().getColumn(2).setPreferredWidth(60);
		tableVendas.getColumnModel().getColumn(3).setPreferredWidth(40);

	}

	public void setbtnStatus(boolean status) {
		comboPagam.setEnabled(status);
		comboFuncionario.setEnabled(status);
		btnApagar.setEnabled(status);
		btnFiltro.setEnabled(status);
		btnSalvar.setEnabled(status);
	}

	private void codeSearch(String busca) {
		if (busca.length() == 0) {
			tableSorter.setRowFilter(null);
		} else {
			tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca), 0, 1, 2)); // Ordena rows com
																										// a flag de
																										// Case-insensitivity
			tableVendas.getSelectionModel().setSelectionInterval(0, 0);
			somarColunaView(columnCurrency);
		}
	}

	public void deletaritem(Connection con, int item) {
		String query = "DELETE FROM VENDAS WHERE CODESTO = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, item);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}



	public void somarColunaView(int[] columnCurrency) {
		Double somaDinheiro = 0.0;
		Double somaCart = 0.0;
		Double somaTot = 0.0;
		for (int i = 0; i < tableVendas.getRowCount(); i++) {
			int modelRow = tableVendas.convertRowIndexToModel(i);
			somaDinheiro = somaDinheiro + vendasModel.getValueAtDoub(modelRow, columnCurrency[0]);
			somaCart = somaCart + vendasModel.getValueAtDoub(modelRow, columnCurrency[1]);
			somaTot = somaTot + vendasModel.getValueAtDoub(modelRow, columnCurrency[2]);
		}
		txtDinheiro.setText(nf.format(somaDinheiro));
		txtSomaCart.setText(nf.format(somaCart));
		txtSomaAtual.setText(nf.format(somaTot));
	}

	private void setRendersEditors() {
		// Renderers Editors
		TableColumnModel m = tableVendas.getColumnModel();
		m.getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(4).setCellEditor(new TableEditorCurrency());
		m.getColumn(5).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(5).setCellEditor(new TableEditorCurrency());
		m.getColumn(6).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(6).setCellEditor(new TableEditorCurrency());
		m.getColumn(7).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(7).setCellEditor(new TableEditorCurrency());
		m.getColumn(9).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		m.getColumn(9).setCellEditor(new TableEditorDateTime());
		m.getColumn(10).setCellEditor(new TableEditorDateTime());
	}
}
