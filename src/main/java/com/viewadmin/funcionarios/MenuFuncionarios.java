package com.viewadmin.funcionarios;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.model.DBVendas;
import com.model.DefaultModels;

import net.miginfocom.swing.MigLayout;

public class MenuFuncionarios extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table = new JTable();
	private DBVendas dbVendas = new DBVendas();
	private String[] columnNamesFunc = new String[] {"Id","Nome"};
	private boolean[] columnEditables = new boolean[] {false,false};
	private Class<?>[] classesTableFunc = new Class<?>[] {Integer.class, String.class};
	private DefaultModels model;
	private TableRowSorter<TableModel> rowSorter;
	//Visuais
	private JScrollPane scrollPane = new JScrollPane();
	private JButton btnRemover = new JButton("Remover");
	private JButton btnAdicionar = new JButton("Adicionar");
	private final JMenuBar menuBar = new JMenuBar();
	
	public MenuFuncionarios(Connection con) {
		super("Funcionarios");
		setResizable(false);
		refreshTable(con);
		setVisible(false);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[35.00][grow]"));
		getContentPane().add(scrollPane, "cell 0 1,grow");
		scrollPane.setViewportView(table);
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		setJMenuBar(menuBar);
		menuBar.add(btnRemover);
				menuBar.add(btnAdicionar);
		
				//Lisners
				btnAdicionar.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String nome = JOptionPane.showInputDialog("Insira um Nome");
						if(nome != null && nome.length() > 0){
							if(nome.length() > 70) {nome = nome.substring(0, 70);}
							dbVendas.addFuncio(con, nome);
							refreshTable(con);
						}else {
							JOptionPane.showMessageDialog(null, "Nome Inv�lido");
						}
					}
				});
		btnRemover.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int i = JOptionPane.showConfirmDialog(null, "Deseja Apagar a linha Selecionada");
				if(row != -1 && i == 0) {
					row = table.convertRowIndexToModel(row);
					dbVendas.removeFuncio(con, (int)model.getValueAt(row, 0));
					refreshTable(con);
				}else {
					JOptionPane.showMessageDialog(null, "Nenhuma Linha Selecionada");
				}
			}
		});
	}
	public void refreshTable(Connection con) {
		model = new DefaultModels(columnNamesFunc, columnEditables, classesTableFunc);
		dbVendas.getAllFuncion(con, "SELECT IDFUNC, NOME FROM FUNCIONARIOS", model);
		rowSorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(rowSorter);
		table.setModel(model);
		table.getColumnModel().getColumn(0).setMaxWidth(40);;
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
	}

}
