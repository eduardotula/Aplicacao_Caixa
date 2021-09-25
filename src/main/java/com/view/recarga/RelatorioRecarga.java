package com.view.recarga;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import tablerenders_editor.TableRendererCurrency;
import tablerenders_editor.TableRendererDate;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;

import control.PrintRelatorios;
import control.TableOperations;
import model.DBFrenteCaixa;
import model.DefaultModels;
import model.PrintRelatorioRecargas;
import model.Recarga;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class RelatorioRecarga extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con;
	private DefaultModels tableModel = new DefaultModels(new String[] {"ID","Recarga", "Numero", "Valor", "Hora", "Data"}, 
			new boolean[] {false,false,false,false,false},
			new Class<?>[] {Integer.class,String.class,String.class, Double.class, LocalTime.class, LocalTime.class}); 
	private DBFrenteCaixa dbFrente = new DBFrenteCaixa();
	private TableRowSorter<DefaultModels> rowSorter = new TableRowSorter<DefaultModels>(tableModel);
	private DecimalFormat df = new DecimalFormat("R$0.###");
	private TableOperations to = new TableOperations();
	//Visuais
	private JTable table = new JTable();
	private JTextField txtSoma = new JTextField();
	private JButton btnApagar = new JButton("Apagar");
	private JButton btnImprimir = new JButton("Imprimir");
	private JScrollPane scrollPane = new JScrollPane();
	private JLabel lblSoma = new JLabel("Soma Total");
	
	public RelatorioRecarga(Connection con) {
		super("Recargas");
		this.con = con;
		createAndShowGUI();
		setListeners();
		refreshTable();
		setRenders();
	}
	
	
	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[200.00][167.00,grow]", "[20.00][163.00,grow][]"));
		getContentPane().add(btnImprimir, "cell 0 0,alignx left");
		getContentPane().add(btnApagar, "cell 1 0,alignx right");
		getContentPane().add(scrollPane, "cell 0 1 2 1,grow");
		scrollPane.setViewportView(table);
		getContentPane().add(lblSoma, "flowx,cell 1 2,alignx right");
		getContentPane().add(txtSoma, "cell 1 2,alignx right");
		txtSoma.setColumns(7);
		table.setModel(tableModel);
		table.setRowSorter(rowSorter);
		setSize(505,459);
		setVisible(false);
	}
	
	public void setListeners() {
		btnApagar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null, "Deseja apagar as Recargas Selcionadas");
				if(res == 0 && table.getSelectedColumns().length > 0) {
					to.ApagarSelecioTabela(con, tableModel, table, "DELETE FROM RECARGAS WHERE ID = ?");
					refreshTable();
				}else {
					JOptionPane.showMessageDialog(null, "Nenhuma linha Selecionada");
				}
			}
		});
		
		btnImprimir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintRelatorios pr = new PrintRelatorios();
			    PrintRelatorioRecargas bf = new PrintRelatorioRecargas();
			    ArrayList<Recarga> recargas = dbFrente.getRecargas(con, MainVenda.IdCaixa);
			    bf.passArrayList(LocalTime.now(), dbFrente.getFuncioCaixaAtual(con), recargas);
			    PrintRelatorios printRelatorios = new PrintRelatorios();
			    printRelatorios.printer(bf,dbFrente.getImpressora(con));

				pr.choosePrintType(table, bf);
			}
		});
	}
	
	public void refreshTable() {
		if(MainVenda.IdCaixa != null) {
			tableModel.removeAllRows();
			txtSoma.setText(df.format(dbFrente.getRecargas(con, tableModel, MainVenda.IdCaixa)));
			table.getColumnModel().getColumn(0).setMinWidth(0);
			table.getColumnModel().getColumn(0).setMaxWidth(0);
			table.getColumnModel().getColumn(1).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(30);
		}else {
			tableModel.removeAllRows();
		}
	}
	public void setRenders() {
		table.getColumnModel().getColumn(3).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(TableRendererDate.getDateTimeRenderer());
	}
}
