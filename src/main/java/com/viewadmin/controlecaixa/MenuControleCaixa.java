package com.viewadmin.controlecaixa;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import tablerenders_editor.TableRenderColor;
import tablerenders_editor.TableRendererDate;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.viewadmin.FrameFiltroData;
import com.viewadmin.relatorios.MenuRelatorios;

import model.DBVendas;
import model.DefaultModels;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MenuControleCaixa extends JFrame{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private DBVendas dbVendas = new DBVendas();
	private LocalDate[] dataF = null;
	private TableRowSorter<TableModel> tableSorter;
	//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private String[] columnNames = new String[]{
			"Id Opera��o", "Data","Ver Vendas", "Ver Opera��es","Funcionario"};
		private boolean[] columnEditables = new boolean[] {false,false,false,false,false};

		private Class<?>[] classesTable = new Class<?>[] {Integer.class, LocalDate.class, 
			String.class, String.class, String.class};
	private String query = "SELECT IDCAIXA, DATA, FUNCIONARIO FROM CONTROLECAIXA;";
	private static DecimalFormat df = new DecimalFormat("R$0.###");
	//Visuais
	private JTable table = new JTable();
	private JScrollPane scrollPane = new JScrollPane();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu menuFilto = new JMenu("Filtrar");
	private final JMenuItem mntmFiltroData = new JMenuItem("Filtrar por Data");
	private final JMenuItem mntmSomaVenda = new JMenuItem("Exibir Soma de Vendas");
	private final JMenuItem mntmSomaFecha = new JMenuItem("Exibir Soma de Fechamento");
	private final JButton btnLimpar = new JButton("Limpar Filtros");
	private final JButton btnRelacao = new JButton("Rela\u00E7\u00E3o Mensal");


	public MenuControleCaixa(Connection con) {
		super("Controle Movimento");
		setBackground(Color.WHITE);
		setResizable(false);
		setVisible(false);
		refreshTable(con, this.query);
		scrollPane.setViewportView(table);
		
		
		getContentPane().setLayout(new MigLayout("", "[][][51.00][grow]", "[][][][][grow]"));
		getContentPane().add(scrollPane, "cell 0 1 4 4,grow");
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		setJMenuBar(menuBar);
		menuBar.add(menuFilto);
		menuFilto.add(mntmFiltroData);
		menuFilto.add(mntmSomaVenda);
		menuFilto.add(mntmSomaFecha);
		
		menuBar.add(btnRelacao);
		menuBar.add(btnLimpar);
		
		btnRelacao.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new FrameRelatorioMensal(dataF);
			}
		});
		btnLimpar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable(con, query);
			}
		});
		mntmFiltroData.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameFiltroData d = new FrameFiltroData();
				d.startGUIFiltroEsto();
				dataF = d.getData();
				String queryD = String.format("SELECT IDCAIXA, DATA, FUNCIONARIO FROM CONTROLECAIXA WHERE DATA BETWEEN '%s' AND '%s';", dataF[0], dataF[1]);
				refreshTable(con, queryD);
			}
		});
		mntmSomaFecha.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dataF != null) {
					String querySoma = String.format("SELECT SUM(TROCOCAIXA) AS SUMT, SUM(VALORCART) AS SUMC,SUM(VALORDINHEIRO) AS SUMD FROM OPERACOES_CAIXA"
							+ " INNER JOIN CONTROLECAIXA ON CONTROLECAIXA.IDCAIXA = OPERACOES_CAIXA.CONTROLECAIXA_IDCAIXA"
							+ " WHERE DATA BETWEEN '%s' AND '%s';",dataF[0],dataF[1]);
					double[] somas = dbVendas.getFechamentoSoma(con, querySoma);
					frameSoma(somas);
				}else {
					JOptionPane.showMessageDialog(null, "� Necess�rio utilizar a fun��o de 'Filtrar por Data' antes de exibir a soma");
					}
			}
		});
		mntmSomaVenda.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dataF != null) {
					String querySoma = String.format("SELECT SUM(V.VALORTOT) AS SUMV FROM VENDAS V "
							+ "INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
							+ "WHERE C.DATA BETWEEN '%s' AND '%s'", dataF[0], dataF[1]);
					System.out.println(querySoma);
					double soma = dbVendas.getVendasSoma(con, querySoma);
					frameSomaVendas(soma);
				}else {
					JOptionPane.showMessageDialog(null, "� Necess�rio utilizar a fun��o de 'Filtrar por Data' antes de exibir a soma");
				}
			}
		});
		//Listener Tabela
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					LocalDate dataNow = LocalDate.now();
					int row = table.getSelectedRow();
					int chaveSele = (int) table.getValueAt(row, 0);
					switch (table.getSelectedColumn()) {
					case 2:
						frameVendasDia(dataNow, con, chaveSele);
						break;
					case 3:
						frameRelato(dataNow, con, chaveSele);
						break;
					}
				}
			}
		});
	}
	
	private void frameVendasDia(LocalDate dataNow, Connection con, int chaveSele) {
		String queryV = "Select V.CODESTO,P.CODBARRA,P.DESCRICAO,V.QUANTI,V.VALORUNI,V.VALORDINHEIRO,V.VALORCARTAO,V.VALORTOT,V.TIPOPAGAMENTO,C.DATA,V.HORA,V.IDPROD FROM VENDAS V"
				+ " INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
				+ "INNER JOIN CONTROLECAIXA C ON V.CONTROLECAIXA_IDCAIXA = C.IDCAIXA "
				+ "WHERE V.CONTROLECAIXA_IDCAIXA = '" + chaveSele + "';";
		new MenuRelatorios(con, queryV).setLocationRelativeTo(null);;
	}
	private void frameRelato(LocalDate data, Connection con, int chaveSele) {
		System.out.println("ab");
		new FrameControleMovimento(con, chaveSele);
	}
	public void refreshTable(Connection con, String query) {
		DefaultModels model = new DefaultModels(columnNames, columnEditables, classesTable);
		dbVendas.addRowTableControle(con, model, query);
		table.setModel(model);
		tableSorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(tableSorter);
		//Renders e Editores
		table.getColumnModel().getColumn(1).setCellRenderer(TableRendererDate.getDateTimeRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new TableRenderColor());
		table.getColumnModel().getColumn(3).setCellRenderer(new TableRenderColor());
	}
	private void frameSoma(double[] somas) {
		JFrame frame = new  JFrame("Soma de Fechamentos");
		frame.getContentPane().setLayout(new MigLayout("", "[][]", "[][][]"));
		JTextField txtTro = new JTextField(Double.toString(somas[0]));
		JTextField txtCart = new JTextField(Double.toString(somas[1]));
		JTextField txtDinh = new JTextField(Double.toString(somas[2]));
		JLabel lblCar = new JLabel("Soma Cart\u00E3o");
		JLabel lblSomaTroco = new JLabel("Soma Troco");
		JLabel lblDinh = new JLabel("Soma Dinheiro");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.getContentPane().add(lblSomaTroco, "cell 0 0,alignx trailing");
		frame.getContentPane().add(txtTro, "cell 1 0,growx");
		txtTro.setColumns(10);
		txtCart.setEditable(false);
		txtCart.setEditable(false);
		txtDinh.setEditable(false);
		frame.getContentPane().add(lblCar, "cell 0 1,alignx trailing");
		frame.getContentPane().add(txtCart, "cell 1 1,growx");
		frame.getContentPane().add(lblDinh, "cell 0 2,alignx trailing");
		frame.getContentPane().add(txtDinh, "cell 1 2,growx");
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	private void frameSomaVendas(double soma) {
		JFrame frame = new  JFrame("Soma de Vendas");
		frame.getContentPane().setLayout(new MigLayout("", "[][]", "[][][]"));
		JTextField txtTro = new JTextField(df.format(soma));
		txtTro.setEditable(false);
		JLabel lblSomaTroco = new JLabel("Soma Vendas");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.getContentPane().add(lblSomaTroco, "cell 0 0,alignx trailing");
		frame.getContentPane().add(txtTro, "cell 1 0,growx");
		txtTro.setColumns(10);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
}
