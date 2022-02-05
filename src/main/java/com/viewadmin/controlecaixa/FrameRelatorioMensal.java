package com.viewadmin.controlecaixa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

import com.model.DBOperations;
import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;
import com.viewadmin.FrameMenuAdmin;

import net.miginfocom.swing.MigLayout;

public class FrameRelatorioMensal extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JButton btnExportar = new JButton("ExportarCSV");
	private String[] columnName = new String[] {"Data", "V.Dinheiro","V.Cartão","V.Total","V.Recargas"};
	private boolean[] columnEditables = new boolean[] {false,false,false,false,false};
	private Class<?>[] classesTableEsto = new Class<?>[] {LocalDate.class,Double.class,Double.class,Double.class,Double.class};
	private DefaultModels model = new DefaultModels(columnName, columnEditables, classesTableEsto);
	private JTextField txtRecargas;
	private JTextField txtTotal;
	private JTextField txtCart;
	private JTextField txtDinheiro;
	private DecimalFormat df = new DecimalFormat("R$0.###");
	
	public FrameRelatorioMensal(LocalDate[] dataF) {
		super("Relatorio Mensal");
		createAndShowGUI();
		setList();
		refreshTable(dataF);
	}
	public void createAndShowGUI() {
		setVisible(true);
		setAlwaysOnTop(true);
		setSize(700,400);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][]"));
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 0,grow");
		table = new JTable();
		scrollPane.setViewportView(table);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		table.setModel(model);
		table.setAutoCreateRowSorter(true);
		
		JLabel lblNewLabel_3 = new JLabel("Soma Dinheiro");
		getContentPane().add(lblNewLabel_3, "flowx,cell 0 1");
		
		txtDinheiro = new JTextField();
		getContentPane().add(txtDinheiro, "cell 0 1,alignx right");
		txtDinheiro.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Soma Cart");
		getContentPane().add(lblNewLabel_2, "cell 0 1,alignx right");
		
		txtCart = new JTextField();
		getContentPane().add(txtCart, "cell 0 1,alignx right");
		txtCart.setColumns(10);
		
		JLabel lblTotal = new JLabel("Soma Total");
		getContentPane().add(lblTotal, "cell 0 1,alignx right");
		
		txtTotal = new JTextField();
		getContentPane().add(txtTotal, "cell 0 1,alignx right");
		txtTotal.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Soma Recargas");
		getContentPane().add(lblNewLabel, "cell 0 1,alignx right");
		
		txtRecargas = new JTextField();
		getContentPane().add(txtRecargas, "cell 0 1,alignx right");
		txtRecargas.setColumns(10);
		menuBar.add(btnExportar);
	}
	
	public void setList() {
		btnExportar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.exportarCSV();
			}
		});
	}
	
	public void refreshTable(LocalDate[] dataF) {
		model.removeAllRows();
		String query;
		if(dataF != null) {
			query = "SELECT C.DATA,SUM(V.VALORDINHEIRO),SUM(V.VALORCARTAO),SUM(V.VALORTOT), "
					+ " (SELECT SUM(R.VALOR) FROM RECARGAS R WHERE R.DATA = C.DATA) "
					+ "FROM CONTROLECAIXA C "
					+ "INNER JOIN VENDAS V ON C.IDCAIXA = V.CONTROLECAIXA_IDCAIXA "
					+ "WHERE C.DATA BETWEEN ? AND ? "
					+ "GROUP BY C.DATA ";
		}else {
			query = "SELECT C.DATA,SUM(V.VALORDINHEIRO),SUM(V.VALORCARTAO),SUM(V.VALORTOT), "
					+ " (SELECT SUM(R.VALOR) FROM RECARGAS R WHERE R.DATA = C.DATA) "
					+ "FROM CONTROLECAIXA C "
					+ "INNER JOIN VENDAS V ON C.IDCAIXA = V.CONTROLECAIXA_IDCAIXA "
					+ "GROUP BY C.DATA ";
		}
		try {
			DBOperations.appendAnyTable(FrameMenuAdmin.con, query, model, dataF[0],dataF[1]);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha ao atualizar","Erro",JOptionPane.ERROR_MESSAGE);
		} 
		txtDinheiro.setText(df.format(model.sumColumn(1)));
		txtCart.setText(df.format(model.sumColumn(2)));
		txtTotal.setText(df.format(model.sumColumn(3)));
		txtRecargas.setText(df.format(model.sumColumn(4)));
		TableColumnModel m = table.getColumnModel();
		m.getColumn(0).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		m.getColumn(1).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(2).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(3).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
	}
}
