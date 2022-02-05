package com.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.control.PrintRelatorios;
import com.control.TableOperations;
import com.model.DBFrenteCaixa;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.model.PrintDiaFormat;
import com.model.PrintRelatoriosProds;
import com.tablerenders_editor.TableRendererCurrency;

public class RelatorioDia extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double valorTotColumn = 0.0;
	private TableRowSorter<TableModel> sorterDia;
	private DecimalFormat nf = new DecimalFormat("R$0.##");
	private DefaultModels model = new DefaultModels(new String[] {
			"ID", "Codigo ", "Descrição", "Quantidade","Unitario", "Dinheiro", "Cartão", "Total","Tipo", "Hora","IDEstoque"}
	, new boolean[] {false,false,false,false,false,false,false,false,false,false},
	new Class<?>[] {Integer.class,String.class,String.class,Integer.class,Double.class,Double.class,Double.class,Double.class,String.class,LocalTime.class,Integer.class});
	private TableOperations tableOpera = new TableOperations();
	private DBFrenteCaixa dbFrente = new DBFrenteCaixa();
	//Objetos Visuais
	private JTable tableVendaDia = new JTable();
	JScrollPane scrollPane = new JScrollPane();
	private JPanel bottomPanel = new JPanel();
	private JTextField txtValorTot = new JTextField();
	private final JPanel topPanel = new JPanel();
	private JLabel lblValorTot = new JLabel("Valor Total:");
	private JButton btnDelete = new JButton("Deletar Venda");
	private JButton btnImprim = new JButton("Imprimir");
	private final JTextField txtSomaCart = new JTextField();
	private final JLabel lblSomaCart = new JLabel("Soma Cart\u00E3o");
	private final JTextField txtSomaDinhe = new JTextField();
	private final JLabel lblSomaDinh = new JLabel("Soma Dinheiro");
	
	public RelatorioDia(Connection con) {
		super("Relatorio do Dia");
		txtSomaDinhe.setColumns(10);
		txtSomaCart.setColumns(10);
		setSize(860,500);
		setLocationRelativeTo(null);
		setVisible(false);
		updateTable(con);
		//Listners 
		btnImprim.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PrintRelatorios pr = new PrintRelatorios();
				ArrayList<PrintRelatoriosProds> prodTabela = new ArrayList<PrintRelatoriosProds>();
				for(int i = 0;i < tableVendaDia.getRowCount();i++) {
					int modelRow = tableVendaDia.convertRowIndexToModel(i);
					PrintRelatoriosProds prod = new PrintRelatoriosProds(
							(String)tableVendaDia.getValueAt(modelRow, 1),
							(String)tableVendaDia.getValueAt(modelRow, 2),
							(int)tableVendaDia.getValueAt(modelRow, 3),
							(double)tableVendaDia.getValueAt(modelRow, 4),
							(double)tableVendaDia.getValueAt(modelRow, 7),
							(String)tableVendaDia.getValueAt(modelRow, 9).toString());
					prodTabela.add(prod);
				}
				PrintDiaFormat bf = new PrintDiaFormat();
				bf.passArrayList(prodTabela, model.sumColumn(7));
				pr.choosePrintType(tableVendaDia, bf);
			}
		});
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int respo = JOptionPane.showConfirmDialog(new JFrame(), "Deseja apagar os produtos selecionados?");
					int firstRow = tableVendaDia.getSelectedRow();
					if(respo == 0 && firstRow != -1) {
						String motivo = JOptionPane.showInputDialog(new JFrame(), "Descreva o Motivo do Deletamento");
						String queryDelete = "DELETE FROM VENDAS WHERE CODESTO = ?";
						tableOpera.ApagarSelecioTabela(con, model, tableVendaDia, queryDelete);
						dbFrente.insertVendaApagada(con, motivo, tableVendaDia);
						for(int row : tableVendaDia.getSelectedRows()) {
							int modelRow = tableVendaDia.convertRowIndexToModel(row);
							int id = (int) model.getValueAt(modelRow, 10);
							int quanti = (int) model.getValueAt(modelRow, 3);
							DBOperations.DmlSql(con, "UPDATE PRODUTOS SET QUANTIDADE = QUANTIDADE + ? WHERE IDPROD = ?", quanti,id);
						}
						updateTable(con);
					}
				}catch(ArrayIndexOutOfBoundsException a) {
					a.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame("Erro"), "Nenhum Item Selecionado");
				} catch (ClassCastException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame("Erro"), "Falha ao deletar");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(new JFrame("Erro"), "Falha ao deletar");
					e1.printStackTrace();
				}
			}
		});
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		txtValorTot.setColumns(10);
		
		GroupLayout gl_bottomPanel = new GroupLayout(bottomPanel);
		gl_bottomPanel.setHorizontalGroup(
			gl_bottomPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_bottomPanel.createSequentialGroup()
					.addContainerGap(409, Short.MAX_VALUE)
					.addComponent(lblSomaDinh)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtSomaDinhe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSomaCart)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtSomaCart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblValorTot)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtValorTot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_bottomPanel.setVerticalGroup(
			gl_bottomPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_bottomPanel.createSequentialGroup()
					.addGroup(gl_bottomPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtValorTot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblValorTot)
						.addComponent(txtSomaCart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSomaCart)
						.addComponent(txtSomaDinhe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSomaDinh))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		getContentPane().add(topPanel, BorderLayout.NORTH);
		
		
		GroupLayout gl_topPanel = new GroupLayout(topPanel);
		gl_topPanel.setHorizontalGroup(
			gl_topPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_topPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnDelete)
					.addPreferredGap(ComponentPlacement.RELATED, 574, Short.MAX_VALUE)
					.addComponent(btnImprim)
					.addContainerGap())
		);
		gl_topPanel.setVerticalGroup(
			gl_topPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_topPanel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_topPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDelete)
						.addComponent(btnImprim)))
		);
		topPanel.setLayout(gl_topPanel);
		bottomPanel.setLayout(gl_bottomPanel);
		scrollPane.setViewportView(tableVendaDia);
		
	}

	public void updateTable(Connection con) {
		model.removeAllRows();
		if(MainVenda.IdCaixa != null) {
			valorTotColumn = dbFrente.getTabelaDia(con, model);
		}else {
			valorTotColumn = 0.0;
		}
		txtValorTot.setText(nf.format(valorTotColumn));
		txtSomaCart.setText(nf.format(model.sumColumn(6)));
		txtSomaDinhe.setText(nf.format(model.sumColumn(5)));
		tableVendaDia.setModel(model);
		sorterDia = new TableRowSorter<TableModel>(model);
		tableVendaDia.setRowSorter(sorterDia);
		tableVendaDia.getColumnModel().getColumn(0).setPreferredWidth(20);
		tableVendaDia.getColumnModel().getColumn(1).setPreferredWidth(100);
		tableVendaDia.getColumnModel().getColumn(2).setPreferredWidth(300);
		tableVendaDia.getColumnModel().getColumn(3).setPreferredWidth(40);
		tableVendaDia.getColumnModel().getColumn(4).setPreferredWidth(50);
		tableVendaDia.getColumnModel().getColumn(5).setPreferredWidth(50);
		tableVendaDia.getColumnModel().getColumn(6).setPreferredWidth(50);
		tableVendaDia.getColumnModel().getColumn(7).setPreferredWidth(50);
		tableVendaDia.getColumnModel().getColumn(8).setPreferredWidth(40);
		tableVendaDia.getColumnModel().getColumn(9).setPreferredWidth(60);
		tableVendaDia.getColumnModel().getColumn(10).setMinWidth(0);
		tableVendaDia.getColumnModel().getColumn(10).setMaxWidth(0);
		TableColumnModel m = tableVendaDia.getColumnModel();
		m.getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(5).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(6).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(7).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());

	}
}
