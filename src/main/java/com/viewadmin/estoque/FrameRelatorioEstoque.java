package com.viewadmin.estoque;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.model.DBOperations;
import com.model.DefaultModels;

import net.miginfocom.swing.MigLayout;

public class FrameRelatorioEstoque extends JFrame{
	private static final long serialVersionUID = 1L;
	private DefaultModels model = new DefaultModels(new String[] {"IDPROD","Codigo", "Produto","Entrada","Saida","Diferenca",}, 
			new boolean[] {false,false,false,false,false,false},
			new Class<?>[] {Integer.class,String.class,String.class,Integer.class,Integer.class,Integer.class});
	private TableRowSorter<DefaultModels> sorter = new TableRowSorter<DefaultModels>(model);
	private DBOperations dbVendas = new DBOperations();
	//Visuais
	private final JTable tabelaMovimento = new JTable();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnExportar = new JMenu("Exportar");
	private final JMenuItem mntmImprimir = new JMenuItem("Imprimir");
	private final JMenuItem mntmExportarCsv = new JMenuItem("Exportar para CSV");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JLabel lblBuscar = new JLabel("Buscar");
	private final JTextField txtBuscar = new JTextField();

	public FrameRelatorioEstoque(Connection con,LocalDate[] datas) {
		super("Movimento Estoque");
		txtBuscar.setColumns(10);
		createAndShowGUI();
		setListeners();
		dbVendas.addRelatorioEstoque(con, model, datas);
	}
	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][150.00,grow]"));
		getContentPane().add(scrollPane, "cell 0 0 1 2,grow");
		scrollPane.setViewportView(tabelaMovimento);
		tabelaMovimento.setModel(model);
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setJMenuBar(menuBar);
		menuBar.add(mnExportar);
		mnExportar.add(mntmImprimir);
		mnExportar.add(mntmExportarCsv);
		lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		menuBar.add(lblBuscar);
		
		menuBar.add(txtBuscar);
		tabelaMovimento.setRowSorter(sorter);
		TableColumnModel m = tabelaMovimento.getColumnModel();
		m.getColumn(2).setPreferredWidth(400);
		m.getColumn(0).setMinWidth(60);
		m.getColumn(3).setMinWidth(60);
		m.getColumn(4).setMinWidth(60);
		m.getColumn(5).setMinWidth(60);
		setSize(800,600);
		setLocationRelativeTo(null);
		setVisible(true);
		setAlwaysOnTop(true);
	}
	public void setListeners() {
		mntmImprimir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tabelaMovimento.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});
		mntmExportarCsv.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.exportarCSV();
			}
		});
		//Tecla enter Busca de Texto
		//Busca o texto na tabela estoque
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscar.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscar.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscar.getText());
			}
		});
	}
	private void codeSearch(String busca) {
		if(busca.length() == 0) {
			sorter.setRowFilter(null);
		}else {
		
			sorter.setRowFilter(RowFilter.regexFilter("(?i)"+ Pattern.quote(busca))); //Ordena rows com a flag de Case-insensitivity
			tabelaMovimento.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
}
