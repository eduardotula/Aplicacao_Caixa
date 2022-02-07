package com.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;

import com.control.PrintRelatorios;
import com.control.TableOperations;
import com.model.Alerts;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.model.PrintRelatorioRecargas;
import com.model.Recarga;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;

import net.miginfocom.swing.MigLayout;

public class RelatorioRecarga extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con;
	private DefaultModels tableModel = new DefaultModels(new String[] {"ID","Recarga", "Numero", "Valor", "Hora", "Data"}, 
			new boolean[] {false,false,false,false,false},
			new Class<?>[] {Integer.class,String.class,String.class, Double.class, LocalTime.class, LocalTime.class}); 
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
			    ArrayList<Recarga> recargas = new ArrayList<>();
			    try {
					ResultSet rs = DBOperations.selectSqlRs(con, "SELECT RECARGA,NUMERO,VALOR FROM RECARGAS WHERE CONTROLECAIXA_IDCAIXA = ?", MainVenda.IdCaixa);
					while(rs.next()) {
						Recarga recarg = new Recarga();
						recarg.setOperadora(rs.getString("RECARGA"));
						recarg.setNumero(rs.getString("NUMERO"));
						recarg.setValor(rs.getDouble("VALOR"));
						recargas.add(recarg);
					}
					bf.passArrayList(LocalTime.now(), DBOperations.selectSql1Dimen(con, "SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?", new String[0])[0], recargas);
					PrintRelatorios printRelatorios = new PrintRelatorios();
					pr.choosePrintType(table, bf);
					printRelatorios.printer(bf,PrintRelatorios.getImpressora());
					
				} catch (Exception e1) {
					e1.printStackTrace();
					Alerts.showError("Falha ao obter regargas", getTitle());
				}

				
			}
		});
	}
	
	public void refreshTable() {
		if(MainVenda.IdCaixa > 0) {
			tableModel.removeAllRows();
			try {
				DBOperations.appendAnyTable(con, "SELECT ID,RECARGA,NUMERO, VALOR, HORA, DATA FROM RECARGAS WHERE CONTROLECAIXA_IDCAIXA = ?", tableModel, MainVenda.IdCaixa);
				txtSoma.setText(df.format(tableModel.sumColumn(3)));
			} catch (SQLException e) {
				Alerts.showError("Falha ao obter recargas", "Erro");
				e.printStackTrace();
			}
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
