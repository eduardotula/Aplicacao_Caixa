package com.viewadmin.estoque;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import com.model.DBVendas;
import com.model.DefaultModels;
import com.model.ObjetoProdutoImport;

import net.miginfocom.swing.MigLayout;

public class FrameEstoqueImport extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String[] columnNamesImpo = new String[] {"Chave", "Cod Barra", "Descriï¿½áo",
			"Quantidade", "V.Custo", "V.Venda"};
	private static String[] columnNamesEsto = new String[] {"Chave","Codigo", "Produto",
			"Quantidade", "V.Custo", "V.Venda"};
	private boolean[] columnEditablesImpo = new boolean[] {false,false,false,false,false, false};
	private static boolean[] columnEditablesEsto = new boolean[] {false,false,false,false,false, false};
	private static Class<?>[] classesTableEsto = new Class<?>[] {Integer.class, String.class, 
		String.class, Integer.class, Double.class, Double.class};
	private Class<?>[] classesTableImpo = new Class<?>[] {Integer.class, String.class,
		String.class, Integer.class, Double.class, Double.class};
	private static DBVendas dbVendas = new DBVendas();
	private NumberFormat nf = NumberFormat.getInstance();
	private static DefaultModels estomodel;
	private static TableRowSorter<DefaultModels> rowSorterEsto;
	private int idFornecedor;
	  
	//Visuais 
	JScrollPane scrollPane = new JScrollPane();
	JButton btnImportar = new JButton("Importar");
	JButton btnCancelar = new JButton("Cancelar");
	private JTable tableImpor = new JTable();
	private JScrollPane scrollImpor = new JScrollPane();
	private final JButton btnApagarLi = new JButton("ApagarLinha");
	private final JScrollPane scrollEsto = new JScrollPane();
	private final static JTable tableEsto = new JTable();
	private final JLabel lblBusca = new JLabel("Buscar");
	private final JTextField txtBusca = new JTextField();
	private final JLabel lblQuanti = new JLabel("Quantidade");
	private final JTextField txtQuanti = new JTextField();
	private final JLabel lblValor = new JLabel("Valor de Venda");
	private final JTextField txtValor = new JTextField();
	private final JButton btnAdicionar = new JButton("Adicionar");
	private final JLabel lblProdEsto = new JLabel("Produtos em Estoque");
	private final JLabel lblImport = new JLabel("Produtos para Importar");
	private final JButton btnCadas = new JButton("Cadastrar Novo Produto");
	private final JLabel lblValorCusto = new JLabel("Valor de Custo");
	private final JTextField txtValorCusto = new JTextField();
	private final JLabel lblFornecedor = new JLabel("Fornecedor");
	private final JTextField txtFornecedor = new JTextField();
	private final JButton btnForneced = new JButton("Selecionar Fornecedor");

	
	public FrameEstoqueImport(Connection con, List<ObjetoProdutoImport> arrayList, String[] fornece) {
		super("Importar");
		txtFornecedor.setEditable(false);
		txtFornecedor.setColumns(10);
		txtValorCusto.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtValorCusto.setColumns(5);
		getContentPane().setBackground(SystemColor.window);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		txtValor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtValor.setColumns(5);
		txtBusca.setFont(new Font("Tahoma", Font.PLAIN, 17));
		txtBusca.setColumns(10);
		limparCampos();
		setSize(1100,600);
		scrollImpor.setViewportView(tableImpor);
		scrollEsto.setViewportView(tableEsto);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		DefaultModels importModel = new DefaultModels(columnNamesImpo, columnEditablesImpo, classesTableImpo);
		tableImpor.setModel(importModel);
		estomodel = new DefaultModels(columnNamesEsto, columnEditablesEsto, classesTableEsto);
		tableEsto.setModel(estomodel);
		rowSorterEsto = new TableRowSorter<DefaultModels>(estomodel);
		tableEsto.setRowSorter(rowSorterEsto);
		dbVendas.addRowTableEstoqueImport(con, estomodel, "SELECT IDPROD, CODBARRA, DESCRICAO , QUANTIDADE, VLR_ULT_VENDA, PRECO_CUSTO FROM PRODUTOS");
		
		if(arrayList.size() > 0) {
			FrameCadasProds frameCadas = new FrameCadasProds(con, new ObjetoProdutoImport[] {},fornece);
			for(int i = 0;i<arrayList.size();i++) {
				ObjetoProdutoImport prodEntra = arrayList.get(i);
				ObjetoProdutoImport prodEst = dbVendas.searchCodEstoque(con, prodEntra.getCodBa());
				if(prodEst == null) {
					prodEst = dbVendas.searchNomeEstoque(con, prodEntra.getProd());
				}
				if(prodEst != null) {
					prodEst.setQuanti(prodEntra.getQuanti());
					prodEst.setValorCusto(prodEntra.getValorCusto());
					importModel.addRow(prodEst.getBasic());
				}else {
					frameCadas.addRowModel(prodEntra.getCodBa(), prodEntra.getProd(), prodEntra.getQuanti(),prodEntra.getValorCusto().toString(),
							prodEntra.getValorUltV().toString());
				}
			}
			if(frameCadas.getModelRowCount() > 0) {
				frameCadas.setVisible(true);
			}else {
				frameCadas.dispose();
			}
		}
		//Liteners
		
		//Double Click na tabela estoque
		tableEsto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					JTable t = (JTable) arg0.getSource();
					int row = t.getSelectedRow();
					String aValue = (String) t.getValueAt(row, 2);
					setTextBusca(row, aValue); //Se a ultima venda deste produto for != 0 o valor sera adicionado no campo de valor
				}
			}
		});
		btnCadas.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				new FrameCadasProds(con, new ObjetoProdutoImport[] {}, null).addRowModel("", "", 0, "","");
				
			}
		});
		//txtValor tecla enter
		txtValor.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {//key enter
					btnAdicionar.requestFocus();
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}});
		
		btnForneced.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameSelecionarFornecedor forne = new FrameSelecionarFornecedor();
				try {
					String nome = forne.getNome();
					idFornecedor = forne.getId();
					txtFornecedor.setText(nome);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnAdicionar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowSele = tableEsto.getSelectedRow();
				int chave = (int)tableEsto.getValueAt(rowSele, 0);
				String cod = (String)tableEsto.getValueAt(rowSele, 1);
				String desc = (String)tableEsto.getValueAt(rowSele, 2);
				try {
					int quanti = Integer.parseInt(txtQuanti.getText());
					Double valor = Double.parseDouble(txtValor.getText().replace(',', '.'));
					Double valorC = Double.parseDouble(txtValorCusto.getText().replace(',', '.'));
					ObjetoProdutoImport prod = new  ObjetoProdutoImport(cod, desc, quanti, valor, valorC, chave);
					importModel.addRow(prod.getBasic()); //Adiciona uma nova linha na tabela tabelaimport com o formato str codBa, str prod, int quanti, double valorUltV, int chave
					txtBusca.requestFocus();
					limparCampos();
				}catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Quantidade ou Valor Invï¿½lido");
				}
			}
		});
		btnApagarLi.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int seleRow = tableImpor.getSelectedRow();
			if(seleRow != -1) {
				importModel.removeRow(seleRow);
			}else {
				JOptionPane.showMessageDialog(null, "Nenhuma Linha Selecionada");
			}}});
		
		
		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}});
		
		btnImportar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int tam = tableImpor.getRowCount();
				if(tableImpor.getCellEditor()!=null){	tableImpor.getCellEditor().stopCellEditing();}
				try {
					if(txtFornecedor.getText().length() > 0){
						for(int i = 0;i < tam;i++) {
							int chave = (int)tableImpor.getValueAt(0, 0);
							String cod = (String)tableImpor.getValueAt(0, 1);
							String desc = (String)tableImpor.getValueAt(0, 2);
							int quanti = (int)tableImpor.getValueAt(0, 3);
							double preco = (double) tableImpor.getValueAt(0, 5);
							Double valorC = (double) tableImpor.getValueAt(0, 4);
							System.out.println(chave);
							ObjetoProdutoImport prod = new ObjetoProdutoImport(cod, desc, quanti, preco, valorC, chave);
								String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ? ,QUANTIDADE = QUANTIDADE + ?, PRECO_CUSTO = ? WHERE IDPROD = ?;";
								boolean v = dbVendas.UpdateItemBd(con, query, prod.getChave(), prod.getCodBa(),
										prod.getProd(), prod.getValorUltV(), prod.getQuanti(), prod.getValorCusto());
								boolean f = dbVendas.addProdEntradas(con, quanti,valorC,preco, chave, LocalDate.now(), LocalTime.now(), "Administrador",idFornecedor);
								if(v && f) {
									importModel.removeRow(0);
								}else {
									throw new Exception();
								}
						}
						JOptionPane.showMessageDialog(null, "Produtos Importados com Sucesso");
					}else {
						JOptionPane.showMessageDialog(null, "Fornecedor nï¿½o selecionado");
					}

					
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Erro, Cheque a primeira linha");
				}
				refreshEstoque(con);
				}});
		
		//listener para tecla enter
		txtBusca.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {//key enter
					int row = tableEsto.getSelectionModel().getLeadSelectionIndex();
					String aValue = (String) tableEsto.getValueAt(row, 2);
					setTextBusca(row, aValue); //Se a ultima venda deste produto for != 0 o valor sera adicionado no campo de valor
					txtQuanti.requestFocus();
				}else if(e.getKeyCode() == 38) { //key para baixo
					int row = tableEsto.getSelectionModel().getLeadSelectionIndex()-1;
					tableEsto.getSelectionModel().setSelectionInterval(row, row);
				}else if(e.getKeyCode() == 40) {  //key para cima
					int row = tableEsto.getSelectionModel().getLeadSelectionIndex()+1;
					tableEsto.getSelectionModel().setSelectionInterval(row, row);
				}
			}
		});
		//Busca o texto na tabela estoque
		txtBusca.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				codeSearch(txtBusca.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				codeSearch(txtBusca.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				codeSearch(txtBusca.getText());
			}
		});
		
		getContentPane().setLayout(new MigLayout("", "[582.00,grow][grow]", "[33.00][][][grow][]"));
		lblBusca.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblImport.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		getContentPane().add(lblImport, "cell 1 0,alignx center");
		lblProdEsto.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		getContentPane().add(lblProdEsto, "cell 0 2,alignx center");
		
		getContentPane().add(btnCadas, "flowx,cell 0 4");
		getContentPane().add(btnCancelar, "cell 1 4,alignx right");
		getContentPane().add(lblBusca, "flowx,cell 0 0,growy");
		getContentPane().add(scrollImpor, "cell 1 1 1 3,grow");
		getContentPane().add(scrollEsto, "cell 0 3,grow");
		getContentPane().add(btnApagarLi, "flowx,cell 1 4,alignx right");
		getContentPane().add(btnImportar, "cell 1 4,alignx right");
		getContentPane().add(txtBusca, "cell 0 0,grow");
		lblValorCusto.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		getContentPane().add(lblValorCusto, "flowx,cell 0 1");
		
		getContentPane().add(txtValorCusto, "cell 0 1,growy");
		lblValor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().add(lblValor, "cell 0 1,growy");
		getContentPane().add(txtValor, "cell 0 1,growy");
		getContentPane().add(btnAdicionar, "cell 0 1,alignx left,growy");
		lblQuanti.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblQuanti.setForeground(Color.BLACK);
		getContentPane().add(lblQuanti, "cell 0 0,alignx left,growy");
		txtQuanti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtQuanti.setColumns(4);
		//txtQuantidade tecla enter
		txtQuanti.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {//key enter
					txtValor.requestFocus();
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}});
		getContentPane().add(txtQuanti, "cell 0 0,alignx left,growy");
		
		getContentPane().add(lblFornecedor, "cell 0 4");
		
		getContentPane().add(txtFornecedor, "cell 0 4,growx");
		
		getContentPane().add(btnForneced, "cell 0 4");
		tableImpor.getColumnModel().getColumn(0).setMinWidth(30);
		tableImpor.getColumnModel().getColumn(0).setMaxWidth(30);
		tableImpor.getColumnModel().getColumn(1).setMinWidth(100);
		tableImpor.getColumnModel().getColumn(2).setPreferredWidth(3000);
		tableImpor.getColumnModel().getColumn(3).setMinWidth(60);
		tableImpor.getColumnModel().getColumn(4).setMinWidth(60);
		tableImpor.getColumnModel().getColumn(5).setMinWidth(60);
		tableEsto.getColumnModel().getColumn(0).setMinWidth(0);
		tableEsto.getColumnModel().getColumn(0).setMaxWidth(0);
		tableEsto.getColumnModel().getColumn(0).setWidth(0);
		tableEsto.getColumnModel().getColumn(1).setMinWidth(100);
		tableEsto.getColumnModel().getColumn(2).setPreferredWidth(3000);
		tableEsto.getColumnModel().getColumn(3).setMinWidth(70);
		tableEsto.getColumnModel().getColumn(4).setMinWidth(70);
		tableEsto.getColumnModel().getColumn(5).setMinWidth(70);
	}
	//Atualiza o Campo de texto com base na String de busca
	private void setTextBusca(int row, String desc) {
		double vlr = 0.0;
		//converte a row ta table para o seu valor no modelo
		int modelRow = tableEsto.convertRowIndexToModel(row);
		
		if(tableEsto.getValueAt(row, 5) != null) {
			vlr = (double) tableEsto.getValueAt(row, 5);
		}
		if(tableEsto.getValueAt(row, 4) != null) {
			txtValorCusto.setText(nf.format(tableEsto.getValueAt(row, 4)));
		}
		txtBusca.setText(desc);
		int viewRow = tableEsto.convertRowIndexToView(modelRow);
		tableEsto.getSelectionModel().setSelectionInterval(viewRow, viewRow);
		txtValor.setText(nf.format(vlr));
		txtQuanti.requestFocus();
		
	}
	private void codeSearch(String busca) {
		if(busca.length() == 0) {
			rowSorterEsto.setRowFilter(null);
		}else {
			
			rowSorterEsto.setRowFilter(RowFilter.regexFilter("(?i)"+ Pattern.quote(busca))); //Ordena rows com a flag de Case-insensitivity
			tableEsto.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	private void limparCampos() {
		txtBusca.setText("");
		txtQuanti.setText("1");
		txtValor.setText("");
		txtValorCusto.setText("");
		txtFornecedor.setText("");
		idFornecedor = 0;
	}
	public static void refreshEstoque(Connection con) {
		estomodel = new DefaultModels(columnNamesEsto, columnEditablesEsto, classesTableEsto);
		tableEsto.setModel(estomodel);
		rowSorterEsto = new TableRowSorter<DefaultModels>(estomodel);
		tableEsto.setRowSorter(rowSorterEsto);
		dbVendas.addRowTableEstoqueImport(con, estomodel, "SELECT IDPROD, CODBARRA, DESCRICAO , QUANTIDADE, VLR_ULT_VENDA, PRECO_CUSTO FROM PRODUTOS");
		tableEsto.getColumnModel().getColumn(0).setMinWidth(0);
		tableEsto.getColumnModel().getColumn(0).setMaxWidth(0);
		tableEsto.getColumnModel().getColumn(0).setWidth(0);
		tableEsto.getColumnModel().getColumn(1).setMinWidth(100);
		tableEsto.getColumnModel().getColumn(2).setPreferredWidth(3000);
		tableEsto.getColumnModel().getColumn(3).setMinWidth(60);
		tableEsto.getColumnModel().getColumn(4).setMinWidth(60);
		tableEsto.getColumnModel().getColumn(5).setMinWidth(60);
	}
}
