package com.viewadmin;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.DefaultModels;

import javax.swing.JTable;
import javax.swing.JTextArea;

public class FrameCustomSQL extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtQuery;
	private JTable table;
	private JButton btnCSV = new JButton("ExportarCSV");
	private JButton btnBuscar = new JButton("Executar");
	private DefaultModels model;
	
	public FrameCustomSQL() {
		super("Custom query");
		createAndShowGUI();
		setList();
		
	}
	
	private void createAndShowGUI() {
		setVisible(true);
		setSize(700,800);
		setLocationRelativeTo(null);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(btnCSV);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][grow]"));
		JLabel lblQuery = new JLabel("Query:");
		getContentPane().add(lblQuery, "flowx,cell 0 0");
		
		getContentPane().add(btnBuscar, "cell 0 1");
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 2,grow");
		table = new JTable();
		scrollPane.setViewportView(table);
		txtQuery = new JTextField();
		getContentPane().add(txtQuery, "cell 0 0,growx");
		txtQuery.setColumns(10);
	}
	
	private void setList() {
		btnCSV.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model != null && model.getRowCount() > 0) {
					model.exportarCSV();
				}
			}
		});
		btnBuscar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTable(txtQuery.getText());
			}
		});
	}
	private void updateTable(String query) {
		Connection con = FrameMenuAdmin.con;
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			int colCount = rs.getMetaData().getColumnCount();
			System.out.println(colCount);
			String[] columnNames = new String[colCount];
			int colIdex = 1;
			for(int i =0;i<colCount;i++) {
				columnNames[i] = rs.getMetaData().getColumnName(colIdex);
				System.out.println(columnNames[i]);
				colIdex++;
			}
			model = new DefaultModels(columnNames, null);
			while(rs.next()) {
				colIdex = 1;
				Object[] row = new Object[colCount];
				for(int i = 0;i<colCount;i++) {
					row[i] = rs.getObject(colIdex);
					colIdex++;
				}
				model.addRow(row);
			}
			table.setModel(model);
			table.setAutoCreateRowSorter(true);
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Query invalida","Erro",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JTextArea area = new JTextArea();
			area.setText(sw.toString());
			area.setLineWrap(true);
			JScrollPane scroll = new JScrollPane(area);
			
			JOptionPane.showConfirmDialog(new JFrame(), scroll);
		}
		
	}
}
