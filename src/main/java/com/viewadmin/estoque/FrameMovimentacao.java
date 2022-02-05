package com.viewadmin.estoque;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.model.DBOperations;
import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;

import net.miginfocom.swing.MigLayout;

public class FrameMovimentacao extends JFrame{
	private static final long serialVersionUID = 1L;
	private DefaultModels model = new DefaultModels(new String[] {"Operação", "Quantidade","Custo", "Venda Total", "Data","Hora","Operador"}, 
			new boolean[] {false,false,false,false,false,false,false},
			new Class<?>[] {String.class,Integer.class,Double.class,Double.class,LocalDate.class,LocalTime.class,String.class});
	private TableRowSorter<DefaultModels> sorter = new TableRowSorter<DefaultModels>(model);
	private DBOperations dbVendas = new DBOperations();
	private List<RowSorter.SortKey> sorterKeys = new ArrayList<>();
	private int columnIndexToSort = 4;
	//Visuais
	private final JTable tabelaMovimento = new JTable();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnExportar = new JMenu("Exportar");
	private final JMenuItem mntmImprimir = new JMenuItem("Imprimir");
	private final JMenuItem mntmExportarCsv = new JMenuItem("Exportar para CSV");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextField txtQuanti = new JTextField();
	private final JLabel lblSomaQuanti = new JLabel("Soma de Quantidade");

	public FrameMovimentacao(Integer id) {
		super("Movimento");
		txtQuanti.setColumns(10);
		createAndShowGUI();
		setListeners();
		setRenders();
		dbVendas.addMovimentoProduto(MenuEstoque.cone, model, id);
		somarColunaView();
		setSorters();
	}
	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][150.00,grow][][]"));
		
		getContentPane().add(scrollPane, "cell 0 0 1 2,grow");
		scrollPane.setViewportView(tabelaMovimento);
		tabelaMovimento.setModel(model);
		
		getContentPane().add(lblSomaQuanti, "flowx,cell 0 2,alignx right");
		
		getContentPane().add(txtQuanti, "cell 0 2,alignx right");
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setJMenuBar(menuBar);
		menuBar.add(mnExportar);
		mnExportar.add(mntmImprimir);
		mnExportar.add(mntmExportarCsv);
		setExtendedState(MAXIMIZED_BOTH);
		
		setSize(486,310);
		setMinimumSize(new Dimension(486,310));
		TableColumnModel m = tabelaMovimento.getColumnModel();
		m.getColumn(0).setPreferredWidth(80);
		
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
	}
	public void setRenders() {
		TableColumnModel m = tabelaMovimento.getColumnModel();
		m.getColumn(2).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(3).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(4).setCellRenderer(TableRendererDate.getDateTimeRenderer());
	}
	public void setSorters() {
		sorterKeys.add(new SortKey(columnIndexToSort, SortOrder.ASCENDING));
		sorterKeys.add(new SortKey(5, SortOrder.ASCENDING));
		sorter.setSortKeys(sorterKeys);
		sorter.sort();
		tabelaMovimento.setRowSorter(sorter);
	}
	public void somarColunaView() {
		Integer somaQuanti = 0;
		for(int i = 0;i<tabelaMovimento.getRowCount();i++) {
			int modelRow = tabelaMovimento.convertRowIndexToModel(i);
			somaQuanti = somaQuanti + model.getValueAtInt(modelRow, 1);
		}
		txtQuanti.setText(Integer.toString(somaQuanti));
	}
}
