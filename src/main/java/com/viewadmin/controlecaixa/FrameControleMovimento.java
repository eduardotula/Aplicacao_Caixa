package com.viewadmin.controlecaixa;

import model.DBVendas;
import model.DefaultModels;
import tablerenders_editor.TableEditorCurrency;
import control.TableOperations;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.Connection;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.miginfocom.swing.MigLayout;


public class FrameControleMovimento extends JFrame{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private TableRowSorter<TableModel> tableSorter;
	private ArrayList<Point> arrayCordBd = new ArrayList<Point>();
	private DBVendas dbVendas = new DBVendas();
	private DefaultModels modelMovimento;
	private TableOperations tableOpera = new TableOperations();
	private String[] columnNames = new String[]{
		"Chave", "Opera��o","Troco Caixa", "Valor Dinheiro","Valor Cart", "Hora"};
	private String[] columnNamesDB = new String[]{
			"ID_OPERACOES", "OPERACAO","TROCOCAIXA", "VALORDINHEIRO","VALORCART", "HORA"};
	private String nomeTabelaBd = "OPERACOES_CAIXA";
	private boolean[] columnEditables = new boolean[] {false,true,true,true,true,true};

	private Class<?>[] classesTable = new Class<?>[] {Integer.class, String.class, 
		Double.class, Double.class, Double.class, LocalTime.class};

	//Objetos visuais
	private JTable tableMovimento = new JTable();
	private  JScrollPane scrollPane;
	private JButton print = new JButton("Imprimir");
	private JTextField txtBuscaItem = new JTextField();
	private JLabel lblBuscar = new JLabel("Buscar Item");
	private JButton btnSalvar = new JButton("Salvar");

	
	public FrameControleMovimento(Connection con, int chaveSele) {
		
		//Janela
		super("Controle de Caixa");
		scrollPane = new JScrollPane(tableMovimento);
		setMinimumSize(new Dimension(500,500));
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[190.00,grow][60,grow]", "[][][255.00,grow]"));
		lblBuscar.setFont(new Font("Tahoma", Font.BOLD, 13));
		setVisible(true);
		getContentPane().add(lblBuscar, "flowx,cell 0 0");
		getContentPane().add(print, "cell 0 1");
		getContentPane().add(scrollPane, "cell 0 2 2 1,grow");
		scrollPane.setViewportView(tableMovimento);
		
		
		modelMovimento = new DefaultModels(columnNames, columnEditables, classesTable);
		dbVendas.addRowTableCaixa(con, chaveSele, modelMovimento);
		tableMovimento.setModel(modelMovimento);
		tableSorter = new TableRowSorter<TableModel>(modelMovimento);
		tableMovimento.setRowSorter(tableSorter);
		getContentPane().add(txtBuscaItem, "cell 0 0");
		txtBuscaItem.setColumns(15);
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
		//Configura o editor para colunas double
		tableMovimento.getColumnModel().getColumn(2).setCellEditor(new TableEditorCurrency());
		tableMovimento.getColumnModel().getColumn(3).setCellEditor(new TableEditorCurrency());
		tableMovimento.getColumnModel().getColumn(4).setCellEditor(new TableEditorCurrency());
		//Configura o tamnho das colunas
		tableMovimento.getColumnModel().getColumn(0).setPreferredWidth(20);
		tableMovimento.getColumnModel().getColumn(1).setPreferredWidth(80);
		tableMovimento.getColumnModel().getColumn(2).setPreferredWidth(60);
		tableMovimento.getColumnModel().getColumn(3).setPreferredWidth(60);
		tableMovimento.getColumnModel().getColumn(4).setPreferredWidth(60);
		tableMovimento.getColumnModel().getColumn(4).setPreferredWidth(60);

		//Listners
		//Salvar
		//Os dados que forem atualizados suas cordenadas ser�o armazenadas em um array cord
		//que em seguida � armazenado em um arrayList
		btnSalvar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null, "Deseja Salvar os valores alterados?");
				if(res == 0 && arrayCordBd.size() > 0) {
					for(int i = 0; i < arrayCordBd.size();i++) {
						//Checa se o valor a ser editado � uma data ou hora e o converte de String
						//para sua respectiva classe
						
						Point cord = arrayCordBd.get(i);
						Object dado = modelMovimento.getValueAt(cord.x,cord.y); //obtem o dado editado da tabela
						System.out.println("tipo do dado" + dado.getClass());
						System.out.println("dados "+dado);
						System.out.println(cord.x + " cordSalva " + cord.y);
						String nomeColunaBd = columnNamesDB[cord.y]; //nome da coluna no bd equivalente na tabela
						int chave = (int) modelMovimento.getValueAt(cord.x, 0); //valor da chave da linha editada
						String chaveNome = columnNamesDB[0]; //Nome da coluna chave no banco
						tableOpera.UpdateTabelaEditado(con, dado, chave, chaveNome, nomeTabelaBd, nomeColunaBd, cord);
					}
					arrayCordBd.clear();
				}
			}
		});
		//Table Listner
		//Valores Editados
		modelMovimento.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				Point cords = new Point(e.getFirstRow(),e.getColumn());
				arrayCordBd.add(cords);
			}
		});
		//Hora Editada
		tableMovimento.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2 && tableMovimento.getSelectedColumn() == 6) {
					String h = JOptionPane.showInputDialog("Insira um novo Valor");
					if(h != null && h.length() > 0) {
						int row = tableMovimento.getSelectedRow();
						int column = tableMovimento.getSelectedColumn();
						modelMovimento.setValueAt(h, row, column);
					}
				}
			}
		});
		print.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tableMovimento.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});
		//Adds
		

	}

	
	//Realiza o filtro de strings dentro da tabela
	private void codeSearch(String busca) {
		if(busca.length() == 0) {
			tableSorter.setRowFilter(null);
		}else {
			tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + busca)); //Ordena rows com a flag de Case-insensitivity
			tableMovimento.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
}


