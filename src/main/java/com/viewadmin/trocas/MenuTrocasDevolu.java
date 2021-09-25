package com.viewadmin.trocas;

import model.DBVendas;
import model.DefaultModels;
import tablerenders_editor.TableRendererCurrency;
import tablerenders_editor.TableRendererDate;
import tablerenders_editor.TableEditorCurrency;
import tablerenders_editor.TableEditorDateTime;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import control.TableOperations;
import javax.swing.JMenuBar;
import javax.swing.border.LineBorder;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;

public class MenuTrocasDevolu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	// Colunas e Classes de colunas
	private String[] columnNames = new String[] { "Chave", "Cod Barra", "Produto Trocado", "Valor", "Data da Compra",
			"Data da Troca", "Hora da Troca", };
	private String[] columnNamesDB = new String[] { "IDTROCA", "CODBARRA", "DESCRICAO", "VALOR", "DATACOMPRA", "DATA",
			"HORA", "OBSERVACAO" };
	private String nomeTabelaBd = "TROCAS";
	private boolean[] columnEditables = new boolean[] { false, false, false, true, true, true, true };
	private Class<?>[] classesTable = new Class<?>[] { Integer.class, String.class, String.class, Double.class,
			LocalDate.class, LocalDate.class, LocalTime.class, String.class };
	private TableRowSorter<TableModel> tableSorter;
	private DBVendas dbVendas = new DBVendas();
	private DefaultModels trocasModel;
	private ArrayList<Point> arrayCordBd = new ArrayList<Point>(); // Array que armazena cordenadas de modifica��es
	private TableOperations tableOpera = new TableOperations();

	// Objetos visuais
	private JTable tableTrocas = new JTable();
	private JScrollPane scrollPane;
	private JButton print = new JButton("Imprimir");
	private JTextField txtBuscaItem = new JTextField();
	private JLabel lblBuscar = new JLabel("Buscar Item:");
	private final JButton btnApagar = new JButton("Apagar");
	private final JButton btnSalvar = new JButton("Salvar");
	private final JMenuBar menuBar = new JMenuBar();

	public MenuTrocasDevolu(Connection con) {

		// Janela
		super("Trocas");
		setModelNoGroup(con);
		arrayCordBd.clear();
		setVisible(true);
		// Renders e Editores
		TableColumnModel m = tableTrocas.getColumnModel();
		m.getColumn(3).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(3).setCellEditor(new TableEditorCurrency());
		m.getColumn(5).setCellEditor(new TableEditorDateTime());
		m.getColumn(4).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		m.getColumn(4).setCellEditor(new TableEditorDateTime());
		m.getColumn(5).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		// Table Listner
		// Valores Editados
		trocasModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				Point cords = new Point(e.getFirstRow(), e.getColumn());
				arrayCordBd.add(cords);
			}
		});
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0)));

		setJMenuBar(menuBar);
		menuBar.add(btnApagar);
		menuBar.add(print);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));
		getContentPane().add(lblBuscar, "flowx,cell 0 0");
		lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scrollPane = new JScrollPane(tableTrocas);
		getContentPane().add(scrollPane, "cell 0 1,grow");
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
		getContentPane().add(txtBuscaItem, "cell 0 0");

		txtBuscaItem.setColumns(15);
		getContentPane().add(btnSalvar, "cell 0 0");

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
						Object dado = trocasModel.getValueAt(cord.x, cord.y); // obtem o dado editado da tabela
						System.out.println("tipo do dado" + dado.getClass());
						System.out.println("dados " + dado);
						System.out.println(cord.x + " cordSalva " + cord.y);
						String nomeColunaBd = columnNamesDB[cord.y]; // nome da coluna no bd equivalente na tabela
						int chave = (int) trocasModel.getValueAt(cord.x, 0); // valor da chave da linha editada
						String chaveNome = columnNamesDB[0]; // Nome da coluna chave no banco
						tableOpera.UpdateTabelaEditado(con, dado, chave, chaveNome, nomeTabelaBd, nomeColunaBd, cord);
					}
					arrayCordBd.clear();
				}
			}
		});
		print.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tableTrocas.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});

		// Apagar Item
		btnApagar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int respo = JOptionPane.showConfirmDialog(new JFrame(), "Deseja apagar os produtos selecionados?");
				int firstRow = tableTrocas.getSelectedRow();
				if (respo == 0 && firstRow != -1) {
					String query = "DELETE FROM TROCAS WHERE IDTROCA = ?";
					tableOpera.ApagarSelecioTabela(con, trocasModel, tableTrocas, query);
					setModelNoGroup(con);
				}
			}
		});
	}

	public void setModelNoGroup(Connection con) {
		trocasModel = new DefaultModels(columnNames, columnEditables, classesTable);
		dbVendas.addRowTableTrocasDevolu(con, trocasModel);
		tableTrocas.setModel(trocasModel);
		tableSorter = new TableRowSorter<TableModel>(trocasModel);
		tableTrocas.setRowSorter(tableSorter);
		tableTrocas.getColumnModel().getColumn(0).setPreferredWidth(20);
		tableTrocas.getColumnModel().getColumn(1).setPreferredWidth(60);
		tableTrocas.getColumnModel().getColumn(2).setPreferredWidth(300);
		tableTrocas.getColumnModel().getColumn(3).setPreferredWidth(40);
		tableTrocas.getColumnModel().getColumn(4).setPreferredWidth(41);
		tableTrocas.getColumnModel().getColumn(5).setPreferredWidth(61);
		tableTrocas.getColumnModel().getColumn(6).setPreferredWidth(50);

	}

	private void codeSearch(String busca) {
		if (busca.length() == 0) {
			tableSorter.setRowFilter(null);
		} else {
			tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + busca, 0, 1, 2)); // Ordena rows com a flag de
																						// Case-insensitivity
			tableTrocas.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

}
