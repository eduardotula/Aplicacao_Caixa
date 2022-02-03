package com.viewadmin.estoque;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;

import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;

import net.miginfocom.swing.MigLayout;

public class FrameRegistroPreco extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private DefaultModels model = new DefaultModels(new String[] {"Operaï¿½áo","Produto", "Valor Anterior","Valor Atualizado", "Hora","Data","Operador"}, 
			new boolean[] {false,false,false,false,false,false,false},
			new Class<?>[] {String.class,String.class,Double.class,Double.class,LocalTime.class,LocalDate.class,String.class});
	//Visuais
	private final JTable tabelaMovimento = new JTable();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnExportar = new JMenu("Exportar");
	private final JMenuItem mntmImprimir = new JMenuItem("Imprimir");
	private final JMenuItem mntmExportarCsv = new JMenuItem("Exportar para CSV");
	private final JScrollPane scrollPane = new JScrollPane();

	public FrameRegistroPreco(Integer id) {
		super("Registro de Preï¿½o");
		createAndShowGUI();
		setListeners();
		setRenders();
		getRegistros(id);
		setSorters();
	}
	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][150.00,grow][][]"));
		
		getContentPane().add(scrollPane, "cell 0 0 1 2,grow");
		scrollPane.setViewportView(tabelaMovimento);
		tabelaMovimento.setModel(model);
		
		
		menuBar.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		setJMenuBar(menuBar);
		menuBar.add(mnExportar);
		mnExportar.add(mntmImprimir);
		mnExportar.add(mntmExportarCsv);
		
		setSize(800,310);
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
		m.getColumn(5).setCellRenderer(TableRendererDate.getDateTimeRenderer());
	}
	public void setSorters() {
		tabelaMovimento.getAutoCreateRowSorter();
	}

	public void getRegistros(Integer id) {
		try {
			PreparedStatement ps = MenuEstoque.cone.prepareStatement("SELECT P.DESCRICAO, M.VALOR_ANT,M.VALOR_MUD,M.HORA,M.DATA,M.FUNCIONARIO FROM MUDANCA_PRECO M "
					+ "INNER JOIN PRODUTOS P ON M.IDPROD = P.IDPROD "
					+ "WHERE M.IDPROD = ?");
			ps.setInt(1, id);
			Object[] o = new Object[7];
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				o[0] = "Mudanï¿½a de Preï¿½o";
				o[1] = rs.getString("DESCRICAO");
				o[2] = rs.getDouble("VALOR_ANT");
				o[3] = rs.getDouble("VALOR_MUD");
				o[4] = rs.getTime("HORA");
				o[5] = rs.getDate("DATA");
				if(o[5] != null) { o[5] = rs.getDate("DATA").toLocalDate();}
				o[6] = rs.getString("FUNCIONARIO");
				model.addRow(o);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro ao obter registro","Error",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
	}
}
