package com.viewadmin.estoque;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.model.DBVendas;
import com.model.DefaultModels;
import com.model.ObjetoProdutoImport;

import net.miginfocom.swing.MigLayout;

public class FrameCadasProds extends JFrame{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNamesCadas = new String[] {"Cod Barra", "Descriï¿½áo", "Quantidade", "V.Custo","V.Venda"};
	private boolean[] columnEditablesCadas = new boolean[] {true,true,true,true,true};
	private Class<?>[] classesTableEsto = new Class<?>[] {String.class, String.class, Integer.class, String.class, String.class};
	private DefaultModels cadasModel = new DefaultModels(columnNamesCadas, columnEditablesCadas, classesTableEsto);
	private DBVendas dbVendas = new DBVendas();
	private Connection con;
	private int idFornece;
	//Visuais
	private JTable tableCadas = new JTable();
	private JScrollPane scrollPane = new JScrollPane();
	private JLabel lblNovos = new JLabel("Novos Produtos Encontrados");
	private JButton btnCadastrar = new JButton("Cadastrar");
	private final JButton btnCriarCodigo = new JButton("Gerar Codigos");
	private final JLabel lblForne = new JLabel("Fornecedor");
	private final JTextField txtFornece = new JTextField();
	private final JButton btnSelecionarForne = new JButton("Fornecedores");
	
	
	public FrameCadasProds(Connection con, ObjetoProdutoImport[] prod, String[] fornece) {
		super("Cadastrar");
		txtFornece.setColumns(10);
		this.con = con;
		createAndShowGUI();
		setList();
		for(int i = 0;i<prod.length;i++) {
			cadasModel.addRow(prod[i].getBasic());
		}
		if(fornece != null) {
			try {
				idFornece = dbVendas.getFornecedorIdByCNPJ(fornece[1]);
				txtFornece.setText(dbVendas.getFornecedorNomeByCNPJ(fornece[1]));
			} catch (Exception e) {
				idFornece = dbVendas.getDefaultFornecedor();
				txtFornece.setText(dbVendas.getFornecedorNomeById(idFornece));
				e.printStackTrace();
			}
		}


		

	}
	private void createAndShowGUI() {
		tableCadas.setModel(cadasModel);
		
		getContentPane().setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(new MigLayout("", "[][][][][grow][]", "[][][grow][][]"));
		lblNovos.setForeground(Color.WHITE);
		lblNovos.setFont(new Font("Tahoma", Font.BOLD, 18));
		getContentPane().add(lblNovos, "cell 4 0,alignx center");
		getContentPane().add(scrollPane, "cell 0 1 6 3,grow");
		tableCadas.setBackground(Color.LIGHT_GRAY);
		scrollPane.setViewportView(tableCadas);
		tableCadas.getColumnModel().getColumn(0).setMinWidth(100);
		tableCadas.getColumnModel().getColumn(1).setPreferredWidth(3000);
		tableCadas.getColumnModel().getColumn(2).setMinWidth(70);
		tableCadas.getColumnModel().getColumn(3).setMinWidth(70);
		tableCadas.getColumnModel().getColumn(4).setMinWidth(70);
		lblForne.setForeground(Color.WHITE);
		lblForne.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		getContentPane().add(lblForne, "flowx,cell 4 4,alignx right");
		txtFornece.setEditable(false);
		getContentPane().add(txtFornece, "cell 4 4,growx");
		
		getContentPane().add(btnSelecionarForne, "cell 4 4");
		getContentPane().add(btnCriarCodigo, "cell 4 4,alignx right");
		getContentPane().add(btnCadastrar, "cell 5 4,alignx right");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(700,300);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	private void setList() {
		
		btnCadastrar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tableCadas.getCellEditor()!=null){	tableCadas.getCellEditor().stopCellEditing();}
				try {
					if(txtFornece.getText().length() > 0) {
						while(cadasModel.getRowCount() > 0) {
							
							String cod = (String)cadasModel.getValueAt(0, 0);
							String desc = (String)cadasModel.getValueAt(0, 1);
							Integer quanti = (int) cadasModel.getValueAt(0, 2);
							Double valorC = Double.parseDouble( ((String) cadasModel.getValueAt(0, 3)).replace(',', '.'));
							Double valorV = Double.parseDouble( ((String) cadasModel.getValueAt(0, 4)).replace(',', '.'));
							ObjetoProdutoImport prod = new ObjetoProdutoImport(cod, desc, quanti,valorV,valorC);
							if(cod == null || cod.isEmpty()) {
								throw new Exception();
							}
							System.out.println(cadasModel.getRowCount());
							boolean a = dbVendas.adicionarItemBd(con, prod);
							if(a) {
								int chave = dbVendas.getLasGeneratedKey(con);
								dbVendas.addProdEntradas(con, quanti,valorC,valorV, chave, LocalDate.now(), LocalTime.now(), "Administrador",idFornece);
								cadasModel.removeRow(0);
							}
						}
					}else {
						JOptionPane.showMessageDialog(null, "Fornecedor nï¿½o selecionado");
					}

				}catch (NumberFormatException e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Favor checar os dados da primeira linha");
				}catch(Exception e3) {
					e3.printStackTrace();
					JOptionPane.showMessageDialog(null, "Favor criar um codigo para o primeiro produto");
				}
				if(cadasModel.getRowCount() == 0) {
					JOptionPane.showMessageDialog(null, "Produtos Cadastrados");
					dispose();
					FrameEstoqueImport.refreshEstoque(con);
				}
			}
		});
		
		btnCriarCodigo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Integer indice = Integer.parseInt(getLastCodigo().substring(2));
				for(int i = 0;i<cadasModel.getRowCount();i++) {
					String cod = (String) cadasModel.getValueAt(i, 0);
					if(cod == null || cod.isEmpty()) {
						indice ++;
						cadasModel.setValueAt("HC" + indice, i, 0);
					}
				}
			}
		});
		btnSelecionarForne.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameSelecionarFornecedor forne = new FrameSelecionarFornecedor();
				try {
					String nome = forne.getNome();
					idFornece = forne.getId();
					txtFornece.setText(nome);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	

	
	public void addRowModel(String codB, String desc, int quanti, String valorC, String valorV) {
		cadasModel.addRow(new Object[] {codB,desc,quanti,valorC,valorV});
	}
	public int getModelRowCount() {
		return cadasModel.getRowCount();
	}
	public String getLastCodigo() {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT CODBARRA FROM PRODUTOS WHERE CODBARRA LIKE 'HC%' ORDER BY IDPROD DESC");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
}
