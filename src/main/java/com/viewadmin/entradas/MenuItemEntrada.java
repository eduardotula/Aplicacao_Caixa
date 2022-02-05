package com.viewadmin.entradas;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.model.DBOperations;
import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;
import com.viewadmin.FrameMenuAdmin;

import net.miginfocom.swing.MigLayout;

public class MenuItemEntrada extends JFrame{
	private static final long serialVersionUID = 1L;
	
	//Colunas e Classes de colunas
	private String[] columnNames = new String[] {"ID", "Cod Barra", "Descrição",
			"Quantidade","V.Custo","V.Venda", "Operador"};
	private  boolean[] columnEditables = new boolean[] {false,false,false,false,false,false,false};
	private Class<?>[] classesTable = new Class<?>[] {Integer.class, String.class, String.class,
		Integer.class,Double.class,Double.class,String.class};

	private DefaultModels entradaModel;
	private DBOperations dbVendas = new DBOperations();
	private TableRowSorter<DefaultModels> sorter;
	private String query;
	//Visuais
	private JTable 	tableEntrada = new JTable();
	private JScrollPane scrollPane = new JScrollPane();
	private JMenuBar menuBar = new JMenuBar();
	private final JTextField txtBusca = new JTextField();
	private final JButton mntmExportrarCSV = new JButton("Exportar em CSV");
	private final JPanel panel = new JPanel();
	private final JLabel lblBuscar = new JLabel("Buscar");
	private final JLabel lblFuncio = new JLabel("Funcionario");
	private final JPanel panel_1 = new JPanel();
	private final JLabel lblData = new JLabel("Data");
	private final JTextField txtData = new JTextField();
	private final JLabel lblNewLabel = new JLabel("Hora");
	private final JTextField txtHora = new JTextField();
	private final JTextField txtFuncionario = new JTextField();

	public MenuItemEntrada(String query, String data, String hora, String funcionario) {
		super("Entradas");

		this.query = query;
		createAndShowGUI(data,hora,funcionario);
		setList();
		refreshTable();
	}
	
	public void createAndShowGUI(String data, String hora, String funcionario) {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][grow]"));
		getContentPane().add(panel_1, "cell 0 0,grow");
		getContentPane().add(panel, "cell 0 1,grow");
		panel_1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel.setLayout(new MigLayout("", "[][grow]", "[]"));
		panel_1.setLayout(new MigLayout("", "[]", "[][]"));
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		
		lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel_1.add(lblData, "flowx,cell 0 0,alignx trailing");
		
		panel_1.add(txtData, "cell 0 0,alignx left");
		
		
		panel_1.add(lblNewLabel, "cell 0 0");
		txtFuncionario.setEditable(false);
		txtFuncionario.setColumns(10);
		txtHora.setEditable(false);
		txtHora.setColumns(10);
		txtData.setEditable(false);
		txtData.setColumns(10);
		panel_1.add(txtHora, "cell 0 0");
		
		
		panel_1.add(txtFuncionario, "cell 0 1,alignx left");

		
		panel.add(lblBuscar, "cell 0 0,alignx trailing");
		panel.add(txtBusca, "flowx,cell 1 0,growx");
		txtBusca.setColumns(10);
		getContentPane().add(scrollPane, "cell 0 2 1 2,grow");
		scrollPane.setViewportView(tableEntrada);
		
		setJMenuBar(menuBar);
		menuBar.add(mntmExportrarCSV);
		sorter = new TableRowSorter<DefaultModels>();
		tableEntrada.setRowSorter(sorter);
		txtData.setText(data);
		txtHora.setText(hora);
		txtFuncionario.setText(funcionario);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		setMinimumSize(new Dimension(300,400));
		setSize(800,600);
		setLocationRelativeTo(null);
		

	}
	public void setList() {
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
	}
	public void setRenders() {
		TableColumnModel m = tableEntrada.getColumnModel();
		m.getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(5).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
	}

	
	public void refreshTable() {
		entradaModel = new DefaultModels(columnNames, columnEditables, classesTable);
		try {
			DBOperations.appendAnyTable(FrameMenuAdmin.con, query, entradaModel);
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		panel.add(lblFuncio, "cell 1 0,alignx left,aligny bottom");
		sorter = new TableRowSorter<DefaultModels>(entradaModel);
		tableEntrada.setModel(entradaModel);
		tableEntrada.setRowSorter(sorter);
		TableColumnModel  m = tableEntrada.getColumnModel();
		m.getColumn(0).setMinWidth(60);
		m.getColumn(1).setMinWidth(100);
		m.getColumn(2).setPreferredWidth(400);
		m.getColumn(3).setMinWidth(60);
		m.getColumn(4).setMinWidth(80);
		m.getColumn(5).setMinWidth(80);
		m.getColumn(6).setMinWidth(80);
		setRenders();
	}
	private void codeSearch(String busca) {
		if (busca.length() == 0) {
			sorter.setRowFilter(null);
		} else {
			try {
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca))); // Ordena rows com a flag de
																							// Case-insensitivity
				tableEntrada.getSelectionModel().setSelectionInterval(0, 0);
			} catch (PatternSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
}
