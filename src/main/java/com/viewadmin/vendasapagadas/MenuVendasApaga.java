package com.viewadmin.vendasapagadas;


import java.awt.Color;
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
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.control.TableOperations;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.tablerenders_editor.TableEditorCurrency;
import com.tablerenders_editor.TableEditorDateTime;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;

import net.miginfocom.swing.MigLayout;

public class MenuVendasApaga extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	//Colunas e Classes de colunas
	private String[] columnNames = new String[] {
			"Chave", "Cod Devolvido", "Produto Devolvido","Quanti", "Dinheiro","Cartão", "Valor Total", "Data Venda","Hora Venda",
			"Data Apagado","Hora Apagado", "Motivo"};
	private String[] columnNamesDB = new String[]{
			"IDPRODS", "Cod Devolvido", "Produto Devolvido","QUANTI" ,"VALORDINHEIRO","VALORCARTAO","VALORTOT","DATAVENDA","HORAVENDA",
			"DATAAPA","HORAAPA", "MOTIVO"};
	private String nomeTabelaBd = "VENDAAPAGA";
	private boolean[] columnEditables = new boolean[] {false,false,false,true,true,true,true,true,true,true,true,true};
	private Class<?>[] classesTable = new Class<?>[] {Integer.class, String.class, String.class,
		Integer.class, Double.class, Double.class,Double.class, LocalDate.class, LocalTime.class, LocalDate.class,LocalTime.class,
		String.class};
	private TableRowSorter<TableModel> tableSorter;
	private DefaultModels apagaModel;
	private DBOperations dbVendas = new DBOperations();
	private TableOperations tableOpera = new TableOperations();
	private ArrayList<Point> arrayCordBd = new ArrayList<Point>();

	
	//Objetos visuais
	private JTable tableVendas = new JTable();
	private  JScrollPane scrollPane;
	private JButton print = new JButton("Imprimir");
	private JTextField txtBuscaItem = new JTextField();
	private JLabel lblBuscar = new JLabel("Buscar Item");
	private JButton btnApagar = new JButton("Apagar");
	private final JButton btnSalvar = new JButton("Salvar");
	private final JMenuBar menuBar = new JMenuBar();
	
	public MenuVendasApaga(Connection con) {
		
		//Janela
		super("Vendas Apagadas");
		refreshTable(con);
		setVisible(false);
		arrayCordBd.clear();
		//Table Listner
		//Valores Editados
		apagaModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				Point cords = new Point(e.getFirstRow(),e.getColumn());
				arrayCordBd.add(cords);
			}
		});
		getContentPane().setLayout(new MigLayout("", "[425px,grow]", "[][-5.00][][][grow]"));
		//txtBusca
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
				getContentPane().add(lblBuscar, "cell 0 2");
				lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
				getContentPane().add(txtBuscaItem, "flowx,cell 0 3");
		
				txtBuscaItem.setColumns(10);
						scrollPane = new JScrollPane(tableVendas);
						getContentPane().add(scrollPane, "cell 0 4,grow");
						getContentPane().add(btnSalvar, "cell 0 3");
				
						//Salvar
						//Os dados que forem atualizados suas cordenadas serão armazenadas em um array cord
						//que em seguida á armazenado em um arrayList
						btnSalvar.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								int res = JOptionPane.showConfirmDialog(null, "Deseja Salvar os valores alterados?");
								if(res == 0 && arrayCordBd.size() > 0) {
									for(int i = 0; i < arrayCordBd.size();i++) {
										//Checa se o valor a ser editado á uma data ou hora e o converte de String
										//para sua respectiva classe
										
										Point cord = arrayCordBd.get(i);
										Object dado = apagaModel.getValueAt(cord.x,cord.y); //obtem o dado editado da tabela
										System.out.println("tipo do dado" + dado.getClass());
										System.out.println("dados "+dado);
										System.out.println(cord.x + " cordSalva " + cord.y);
										String nomeColunaBd = columnNamesDB[cord.y]; //nome da coluna no bd equivalente na tabela
										int chave = (int) apagaModel.getValueAt(cord.x, 0); //valor da chave da linha editada
										String chaveNome = columnNamesDB[0]; //Nome da coluna chave no banco
										tableOpera.UpdateTabelaEditado(con, dado, chave, chaveNome, nomeTabelaBd, nomeColunaBd, cord);
									}
									arrayCordBd.clear();
								}
							}
						});
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		setJMenuBar(menuBar);
		menuBar.add(btnApagar);
		menuBar.add(print);
		//Imprimir a tabela
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
		//Apaga Produtos Selecionados
		btnApagar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int respo = JOptionPane.showConfirmDialog(new JFrame(), "Deseja apagar os produtos selecionados?");
				int firstRow = tableVendas.getSelectedRow();
				if(respo == 0 && firstRow != -1) {
					String query = "DELETE FROM VENDAAPAGA WHERE IDPRODS = ?";
					tableOpera.ApagarSelecioTabela(con, apagaModel, tableVendas, query);
					refreshTable(con);
				}
			}
		});
	}
	public void refreshTable(Connection con) {
		apagaModel = new DefaultModels(columnNames, columnEditables, classesTable);
		dbVendas.addRowTableVendasApaga(con, apagaModel);
		tableVendas.setModel(apagaModel);
		tableSorter = new TableRowSorter<TableModel>(apagaModel);
		tableVendas.setRowSorter(tableSorter);
		tableVendas.getColumnModel().getColumn(0).setPreferredWidth(30);
		tableVendas.getColumnModel().getColumn(1).setPreferredWidth(60);
		tableVendas.getColumnModel().getColumn(2).setPreferredWidth(400);
		tableVendas.getColumnModel().getColumn(3).setPreferredWidth(40);
		tableVendas.getColumnModel().getColumn(4).setPreferredWidth(61);
		tableVendas.getColumnModel().getColumn(5).setPreferredWidth(61);
		tableVendas.getColumnModel().getColumn(6).setPreferredWidth(50);
		tableVendas.getColumnModel().getColumn(7).setPreferredWidth(50);
		setRenders();
	}
	
	
	private void codeSearch(String busca) {
		if(busca.length() == 0) {
			tableSorter.setRowFilter(null);
		}else {
			tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + busca,0,1,2)); //Ordena rows com a flag de Case-insensitivity
			tableVendas.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	private void setRenders() {
		TableColumnModel m = tableVendas.getColumnModel();
		m.getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(5).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(6).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(4).setCellEditor(new TableEditorCurrency());
		m.getColumn(5).setCellEditor(new TableEditorCurrency());
		m.getColumn(6).setCellEditor(new TableEditorCurrency());
		m.getColumn(7).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		m.getColumn(9).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		m.getColumn(7).setCellEditor(new TableEditorDateTime());
		m.getColumn(8).setCellEditor(new TableEditorDateTime());
		m.getColumn(9).setCellEditor(new TableEditorDateTime());
		m.getColumn(10).setCellEditor(new TableEditorDateTime());
	}
}


