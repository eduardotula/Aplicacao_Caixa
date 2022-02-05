package com.viewadmin.relatorios;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.control.TableOperations;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;
import com.viewadmin.FrameFiltroData;

import net.miginfocom.swing.MigLayout;

public class FrameRelatorioRecargas extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con;
	private DefaultModels tableModel = new DefaultModels(new String[] {"ID","Recarga", "Valor", "Hora", "Data", "Pagamento", "Funcionario"}, 
			new boolean[] {false,false,false,false,false,false,false},
			new Class<?>[] {Integer.class, String.class,Double.class, LocalTime.class, LocalDate.class, String.class, String.class}); 
	private DBOperations dbVendas = new DBOperations();
	private DefaultComboBoxModel<String> operaModel = new DefaultComboBoxModel<String>(new String[] {"", "Vivo", "Tim", "Claro", "Oi", "Google Play"});
	private TableRowSorter<DefaultModels> rowSorter = new TableRowSorter<DefaultModels>(tableModel);
	public DecimalFormat df = new DecimalFormat("R$0.###");
	TableOperations op = new TableOperations();
	private String query = "SELECT R.ID, R.RECARGA, R.VALOR, R.HORA, R.DATA,R.PAGAMENTO, C.FUNCIONARIO FROM RECARGAS R INNER JOIN CONTROLECAIXA C "
			+ "ON R.CONTROLECAIXA_IDCAIXA = C.IDCAIXA";
	
	//Visuais
	private JTable table = new JTable();
	private JTextField txtSoma = new JTextField();
	private JButton btnApagar = new JButton("Apagar");
	private JButton btnImprimir = new JButton("Imprimir");
	private JScrollPane scrollPane = new JScrollPane();
	private JLabel lblSoma = new JLabel("Soma Total");
	private final JButton btnExportar = new JButton("Exportar em CSV");
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnFiltrar = new JMenu("Filtros");
	private final JMenuItem mntmFiltrarPorData = new JMenuItem("Filtrar por Data");
	private final JLabel lblBuscar = new JLabel("Buscar");
	private final JTextField txtBusca = new JTextField();
	private final JComboBox<String> comboOperadora = new JComboBox<String>();
	private final JRadioButton rdnDinheiro = new JRadioButton("Filtrar Dinheiro");
	private final JRadioButton rdnCart = new JRadioButton("Filtrar Cart\u00E3o");
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final JButton btnFiltroData = new JButton("Filtrar Data");
	
	public FrameRelatorioRecargas(Connection con) {
		super("Recargas");
		txtBusca.setColumns(10);
		this.con = con;
		
		setJMenuBar(menuBar);
		
		
		mnFiltrar.add(mntmFiltrarPorData);
		
		menuBar.add(comboOperadora);
		
		menuBar.add(btnFiltroData);
		buttonGroup.add(rdnDinheiro);
		menuBar.add(rdnDinheiro);
		buttonGroup.add(rdnCart);
		menuBar.add(rdnCart);
		setListeners();
		createAndShowGUI();
		setRender();
		refreshTable(query);
	}
	
	
	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[200.00][167.00,grow]", "[20.00][163.00,grow][]"));
		
		getContentPane().add(lblBuscar, "flowx,cell 0 0");
		getContentPane().add(btnImprimir, "flowx,cell 1 0,alignx right");
		getContentPane().add(btnExportar, "cell 1 0,alignx right");
		getContentPane().add(btnApagar, "cell 1 0,alignx right");
		getContentPane().add(scrollPane, "cell 0 1 2 1,grow");
		scrollPane.setViewportView(table);
		getContentPane().add(lblSoma, "flowx,cell 1 2,alignx right");
		getContentPane().add(txtSoma, "cell 1 2,alignx right");
		txtSoma.setColumns(7);
		table.setModel(tableModel);
		getContentPane().add(txtBusca, "cell 0 0");
		comboOperadora.setModel(operaModel);
		table.setRowSorter(rowSorter);
		table.getColumnModel().getColumn(0).setMinWidth(60);
		table.getColumnModel().getColumn(0).setMinWidth(80);
		table.getColumnModel().getColumn(0).setMinWidth(40);
		table.getColumnModel().getColumn(0).setMinWidth(80);
		table.getColumnModel().getColumn(0).setMinWidth(80);
		setSize(536,459);
		setLocationRelativeTo(null);
		setVisible(true);
		setOnTop(true);
	}
	
	private void setRender() {
		TableColumnModel m = table.getColumnModel();
		m.getColumn(2).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(4).setCellRenderer(TableRendererDate.getDateTimeRenderer());
	}
	public void setListeners() {
		
		btnFiltroData.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e2) {
				FrameFiltroData ff = new FrameFiltroData();
				setOnTop(false);
				ff.startGUIFiltroEsto();
				LocalDate[] a = ff.getData();
				String queryD = String.format("SELECT R.ID, R.RECARGA, R.VALOR, R.HORA, R.DATA,R.PAGAMENTO, C.FUNCIONARIO FROM RECARGAS R INNER JOIN CONTROLECAIXA C "
						+ "ON R.CONTROLECAIXA_IDCAIXA = C.IDCAIXA WHERE C.DATA BETWEEN '%s' AND '%s';", a[0].toString(),a[1].toString());
				refreshTable(queryD);
				setOnTop(true);
			}
		});
		rdnCart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rowSorter.setRowFilter(RowFilter.regexFilter("C", 5));
				txtSoma.setText(df.format(tableModel.sumColumn(2)));
			}
		});
		rdnDinheiro.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rowSorter.setRowFilter(RowFilter.regexFilter("D", 5));
				txtSoma.setText(df.format(tableModel.sumColumn(2)));
			}
		});
		comboOperadora.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int id = comboOperadora.getSelectedIndex();
				System.out.println(id);
				switch (id) {
				case 0:
					rowSorter.setRowFilter(null);
					break;
				case 1:
					rowSorter.setRowFilter(RowFilter.regexFilter("VIVO", 1));
					txtSoma.setText(df.format(tableModel.sumColumn(2)));
					break;
				case 2:
					rowSorter.setRowFilter(RowFilter.regexFilter("TIM", 1));
					txtSoma.setText(df.format(tableModel.sumColumn(2)));
					break;
				case 3:
					rowSorter.setRowFilter(RowFilter.regexFilter("CLARO", 1));
					txtSoma.setText(df.format(tableModel.sumColumn(2)));
					break;
				case 4:
					rowSorter.setRowFilter(RowFilter.regexFilter("OI", 1));
					txtSoma.setText(df.format(tableModel.sumColumn(2)));
					break;
				case 5:
					rowSorter.setRowFilter(RowFilter.regexFilter("PLAY", 1));
					txtSoma.setText(df.format(tableModel.sumColumn(2)));
					break;
				default:
					break;
				}
			}
		});
		btnApagar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(null, "Deseja apagar as recargas selecionadas");
				if(res == JOptionPane.YES_OPTION) {
					op.ApagarSelecioTabela(con, tableModel, table, "DELETE FROM RECARGAS WHERE ID = ?");
				}
			}
		});
		
		btnImprimir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					table.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnExportar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				op.exportarTabela(table, tableModel);
			}
		});
	}
	
	public void refreshTable(String query) {
		tableModel.removeAllRows();
		txtSoma.setText(df.format(dbVendas.getRecargas(con, tableModel, query)));
	}

	public void setOnTop(boolean bool) {
		setAlwaysOnTop(bool);
	}
}
