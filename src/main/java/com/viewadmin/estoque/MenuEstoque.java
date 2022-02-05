package com.viewadmin.estoque;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.control.XmlReaderFornecedor;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.model.ObjetoProdutoImport;
import com.tablerenders_editor.TableRenderEstoque;
import com.viewadmin.FrameFiltroData;

import net.miginfocom.swing.MigLayout;

public class MenuEstoque extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TableRowSorter<TableModel> tableSorterVendas;
	private DefaultModels tableModel;
	private DBOperations dbVendas = new DBOperations();
	private String simpleQuery = "SELECT IDPROD, CODBARRA, DESCRICAO, QUANTIDADE, VLR_ULT_VENDA,DATA_ULT_VENDA,PRECO_CUSTO, ITEN_ATIVO FROM PRODUTOS WHERE ITEN_ATIVO = 1";
	private String[] columnNamesEsto = new String[] {"Chave","Codigo", "Produto",
			"Quantidade", "V.Custo", "V.Venda","Ult.Venda"};
	private boolean[] columnEditablesEsto = new boolean[] {false,false,false,false,false, false,false};

	private Class<?>[] classesTableEsto = new Class<?>[] {Integer.class, String.class, 
		String.class, Integer.class, Double.class, Double.class,LocalDate.class};
	private Integer quantiAnterior = 0;
	public static Connection cone;
	//objetos Visuais
	private JTable tableEstoque = new JTable();
	private JScrollPane scrollPane = new JScrollPane();
	private JTextField txtProduto = new JTextField();
	private JTextField txtCodBarra = new JTextField();
	private JTextField txtBuscar = new JTextField();
	private JLabel lblProduto = new JLabel("Produto");
	private JLabel lblCodBarra = new JLabel("Codigo de Barra");
	private JLabel lblBuscar = new JLabel("Buscar");
	private JButton btnEditar = new JButton();
	private JButton btnSalvar = new JButton();
	private JMenuItem btnDesativa = new JMenuItem("Desativar");
	private final JTextField txtChave = new JTextField();
	private final JLabel lblChave = new JLabel("Chave");
	private JButton btnCancelar = new JButton();
	private JFileChooser jf = new JFileChooser();
	private JTextField txtValor = new JTextField();
	private JLabel lblValorVenda = new JLabel("Valor de Venda");
	private final JLabel lblQuanti = new JLabel("Quantidade");
	private final JTextField txtQuantidade = new JTextField();
	private final JRadioButton rdnExibirDesati = new JRadioButton("Exibir Produtos Desativados");
	private final JMenuItem btnAtivar = new JMenuItem("Ativar");
	private final JButton btnAdicio = new JButton();
	private final JLabel lblValorCusto = new JLabel("Valor Custo");
	private final JTextField txtCusto = new JTextField();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnMovimento = new JMenu("Relatorios");
	private final JMenuItem mntmRelacao = new JMenuItem("Rela\u00E7\u00E3o de Produtos");
	private final JMenuItem mntmMovimento = new JMenuItem("Movimento do Item");
	private final JMenu mnImportar = new JMenu("Importar");
	private final JMenuItem mntmImportarManual = new JMenuItem("Importar Manual");
	private final JMenuItem mntmImportarAuto = new JMenuItem("Importar Auto");
	private final JMenuItem mntmRegistroPrecos = new JMenuItem("Registro de Preco");
	private final JMenu mnProduto = new JMenu("Produto");
	private final JButton btnApagar = new JButton();

	
	public MenuEstoque(Connection con) {
		super("Estoque");
		cone = con;
		scrollPane.setViewportView(tableEstoque);
		refreshFrame(con, simpleQuery);
		limparCampos();
		setVisible(false);
		setIcons();
		for(int i =0;i<tableModel.getColumnCount();i++) {
			tableEstoque.setDefaultRenderer(tableEstoque.getColumnClass(i), new TableRenderEstoque());
		}
		
		mntmRegistroPrecos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tableEstoque.getSelectedRow() != -1&&tableEstoque.getSelectedRows().length == 1) {
					int row = tableEstoque.getSelectedRow();
					new FrameRegistroPreco((Integer) tableEstoque.getValueAt(row, 0));
				}else {
					JOptionPane.showMessageDialog(null, "Por favor Selecinar um Produto");
				}				
			}
		});
		btnApagar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int res = JOptionPane.showConfirmDialog(null, "Deseja apagar permanentemente os produtos selecionados");
					if(res == JOptionPane.OK_OPTION) {
						for(int row : tableEstoque.getSelectedRows()) {
							int modelRow = tableEstoque.convertRowIndexToModel(row);
							PreparedStatement ps = con.prepareStatement("DELETE FROM PRODUTOS WHERE IDPROD = ?");
							ps.setInt(1, (int) tableModel.getValueAt(modelRow, 0));
							ps.executeUpdate();
							refreshFrame(con, simpleQuery);
						}
					}
				}catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Não á possãvel apagar o produto selecionado, Utilize o função de desativar");
				}
			}
		});
		mntmMovimento.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tableEstoque.getSelectedRow() != -1&&tableEstoque.getSelectedRows().length == 1) {
					int row = tableEstoque.getSelectedRow();
					new FrameMovimentacao((Integer) tableEstoque.getValueAt(row, 0));
				}else {
					JOptionPane.showMessageDialog(null, "Por favor Selecinar um Produto");
				}
			}
		});
		//Listners
		mntmRelacao.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FrameFiltroData fr = new FrameFiltroData();
				fr.startGUIFiltroEsto();
				LocalDate[] datas = fr.getData();
				new FrameRelatorioEstoque(con, datas);
				
			}
		});
		//Btn Adicionar
		btnAdicio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String codBarra = txtCodBarra.getText().replace(" ", "");
					String descri = txtProduto.getText();
					double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
					double valorC = Double.parseDouble(txtCusto.getText().replace(",", "."));
					int quanti = Integer.parseInt(txtQuantidade.getText());
					if(descri.length() != 0) {
						if(txtChave.getText().length() == 0) {
							if(codBarra.length() == 0) {codBarra = null;}
							boolean v = dbVendas.adicionarItemBd(con, new ObjetoProdutoImport(codBarra, descri, quanti, valor, valorC));
							if(v) {
								int key = dbVendas.getLasGeneratedKey(con);
								dbVendas.addProdEntradas(con, quanti,valorC,valor, key, LocalDate.now(), LocalTime.now(), "Administrador", dbVendas.getDefaultFornecedor());
								JOptionPane.showMessageDialog(new JFrame(), "Item Adicionado");
								limparCampos();
								refreshFrame(con, simpleQuery);
							}	
						}else { JOptionPane.showMessageDialog(null, "Use o Botão de Salvar");}
					}else {
						JOptionPane.showMessageDialog(new JFrame(), "Dados Invalidos");
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		//Importar Manual
		mntmImportarManual.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new FrameEstoqueImport(con, new ArrayList<ObjetoProdutoImport>(), null);
				dispose();
			}
		});
		//Importar auto
		mntmImportarAuto.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					FileNameExtensionFilter filter = new FileNameExtensionFilter("XML,IMP", "xml","imp");
					jf.setAcceptAllFileFilterUsed(false);
					jf.addChoosableFileFilter(filter);
					int res = jf.showOpenDialog(new JFrame());
					if (res == JFileChooser.APPROVE_OPTION ) {
						String fileName = jf.getSelectedFile().toString();
						int index = fileName.lastIndexOf('.');
						String extension = fileName.substring(index + 1);
						if(extension.compareTo("xml") == 0 || extension.compareTo("imp") == 0) {
							XmlReaderFornecedor xml = new XmlReaderFornecedor();
							xml.loadXml(jf.getSelectedFile());
							
							if(dbVendas.getFornecedorIdByCNPJ(xml.getFornecedor()[1]) == null) {
								dbVendas.insertFornecedor(xml.getFornecedor()[0], xml.getFornecedor()[1]);
							}
							
							new FrameEstoqueImport(con, xml.getMercadorias(), xml.getFornecedor());
							dispose();
						}else {JOptionPane.showMessageDialog(null, "Formato de arquivo invãlido");}
					}else {
						JOptionPane.showMessageDialog(null, "Nenhum arquivo selecionado");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Falha ao abrir arquivo");
					e.printStackTrace();
				}
			}
		});
		//Listners Tabela
		tableEstoque.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					JTable t = (JTable) arg0.getSource();
					int row = t.getSelectedRow();
					txtChave.setText(Integer.toString((int) t.getValueAt(row, 0)));
					txtCodBarra.setText((String) t.getValueAt(row, 1));
					txtProduto.setText((String) t.getValueAt(row, 2));
					txtQuantidade.setText(Integer.toString((int) t.getValueAt(row, 3)));
					txtCusto.setText(Double.toString((double) t.getValueAt(row, 4)));
					txtValor.setText(Double.toString((double)t.getValueAt(row, 5)));
					quantiAnterior = (Integer) tableEstoque.getValueAt(row, 3);

				}
			}
		});
		//btnCancelar
		btnCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				limparCampos();

			}
		});

		//Buscar Listner
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscar.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscar.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				codeSearch(txtBuscar.getText());
			}
		});
		
		getContentPane().setLayout(new MigLayout("", "[60.00][54.00][60.00][89.00,grow][]", "[15.00][][][][][][45.00][grow]"));
		getContentPane().add(lblBuscar, "cell 0 0");
		//rdnExibir
		rdnExibirDesati.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rdnExibirDesati.isSelected()) {
					String query = "SELECT IDPROD, CODBARRA, DESCRICAO, QUANTIDADE, VLR_ULT_VENDA, PRECO_CUSTO, ITEN_ATIVO FROM PRODUTOS;";
					refreshFrame(con, query);
				}else {
					refreshFrame(con, simpleQuery);
				}
			}
		});
		
		getContentPane().add(rdnExibirDesati, "cell 4 0,alignx right");
		txtBuscar.setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().add(txtBuscar, "cell 0 1 4 1,grow");
		getContentPane().add(lblChave, "cell 0 2,aligny bottom");
		getContentPane().add(lblCodBarra, "cell 1 2,alignx left,aligny bottom");
		getContentPane().add(lblProduto, "cell 3 2,aligny bottom");
		getContentPane().add(txtChave, "cell 0 3,grow");
		txtChave.setEditable(false);
		txtChave.setColumns(10);
		getContentPane().add(txtCodBarra, "cell 1 3 2 1,growx");
		txtCodBarra.setColumns(7);
		txtCodBarra.setColumns(10);
		getContentPane().add(txtProduto, "cell 3 3,growx");
		txtProduto.setColumns(10);
		
		getContentPane().add(lblValorCusto, "cell 0 4");
		getContentPane().add(lblValorVenda, "cell 1 4");
		getContentPane().add(lblQuanti, "cell 2 4");
		txtCusto.setColumns(10);
		
		getContentPane().add(txtCusto, "cell 0 5,growx");
		getContentPane().add(txtValor, "cell 1 5,growx");
		txtValor.setColumns(10);
		getContentPane().add(txtQuantidade, "cell 2 5,growx");
		txtQuantidade.setColumns(10);
		getContentPane().add(btnCancelar, "flowx,cell 0 6 3 1,alignx left");
		
		getContentPane().add(btnAdicio, "flowx,cell 4 6,alignx right");
		getContentPane().add(scrollPane, "cell 0 7 5 1,grow");
		lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		getContentPane().add(btnApagar, "cell 4 6,alignx right");
		//btnSalvar
		btnSalvar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String chaves = txtChave.getText();
					String desc = txtProduto.getText();
					if(chaves.length() != 0 && desc.length() != 0) {
						String codBarra = txtCodBarra.getText();
						System.out.println(chaves + desc);

						double valor = Double.parseDouble(txtValor.getText().replace(",","."));
						double valorC = Double.parseDouble(txtCusto.getText().replace(',', '.'));
						Integer quanti = Integer.parseInt(txtQuantidade.getText());
						
						if(codBarra.length() == 0) {
							codBarra = null;
						}else if(txtValor.getText().length() == 0 ) { valor = 0.0; }
						String query = "UPDATE PRODUTOS SET CODBARRA = ?, DESCRICAO = ?, VLR_ULT_VENDA = ?,QUANTIDADE = ?, PRECO_CUSTO  = ? "
								+ "WHERE IDPROD = ?;";
						
						if(quanti != quantiAnterior) {
							int res = JOptionPane.showConfirmDialog(null, "Não á aconselhavel alterar quantidade manualmente, Deseja Prosseguir?");
							System.out.println(res);
							if(res == JOptionPane.YES_OPTION) {
								dbVendas.UpdateItemBd(con, query, Integer.parseInt(chaves), codBarra, desc, valor, quanti, valorC);
								dbVendas.addProdEntradas(con, quanti - quantiAnterior,valorC,valor, Integer.parseInt(chaves), LocalDate.now(), LocalTime.now(), "Administrador",dbVendas.getDefaultFornecedor());
							}else {
								JOptionPane.showMessageDialog(null, "Dados não salvos","Erro",JOptionPane.ERROR_MESSAGE);
							}
							
						}else {
							dbVendas.UpdateItemBd(con, query, Integer.parseInt(chaves), codBarra, desc, valor, quanti, valorC);
						}
						
						refreshFrame(con, simpleQuery);
						limparCampos();
					}else {
						JOptionPane.showMessageDialog(new JFrame(), "Para Adicionar Novo item, clique no Botão Adicionar");
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Dados Incorretos");
					e1.printStackTrace();
				}
			}
		});
		
				//btnEditar
				
				btnEditar.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						int row = tableEstoque.getSelectionModel().getMinSelectionIndex();
						int modelRow = tableEstoque.convertRowIndexToModel(row);
						txtChave.setText(Integer.toString((int) tableModel.getValueAt(modelRow, 0)));
						txtProduto.setText((String) tableModel.getValueAt(modelRow, 2));
						txtCodBarra.setText((String) tableModel.getValueAt(modelRow, 1));
						txtQuantidade.setText(Integer.toString((int) tableModel.getValueAt(modelRow, 3)));
						txtCusto.setText(Double.toString((double) tableModel.getValueAt(modelRow, 4)));
						txtValor.setText(Double.toString((double) tableModel.getValueAt(modelRow, 5)));
						quantiAnterior = (Integer) tableEstoque.getValueAt(modelRow, 3);
						
					}
				});
				getContentPane().add(btnEditar, "cell 0 6 3 1,alignx left");
		getContentPane().add(btnSalvar, "cell 0 6 3 1,alignx left");
		menuBar.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		
		setJMenuBar(menuBar);
		mnImportar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		
		menuBar.add(mnImportar);
		
		mnImportar.add(mntmImportarManual);
		
		mnImportar.add(mntmImportarAuto);
		mnMovimento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		
		menuBar.add(mnMovimento);
		
		mnMovimento.add(mntmRelacao);
		
		mnMovimento.add(mntmMovimento);
		
		mnMovimento.add(mntmRegistroPrecos);
		mnProduto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		
		menuBar.add(mnProduto);
		mnProduto.add(btnAtivar);
		mnProduto.add(btnDesativa);
		//btnDeletar
		btnDesativa.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] rows = tableEstoque.getSelectedRows();

				int respo = JOptionPane.showConfirmDialog(new JFrame(), "Deseja Desativar os produtos selecionados?");
				if(respo == 0 && rows.length > 0) {
					int status = 0;
					for(int i = 0;i<rows.length;i++) {
						setProdStatus(con, status);
					}
					refreshFrame(con, simpleQuery);
					rdnExibirDesati.setSelected(false);
				}
			}
		});
		btnAtivar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = tableEstoque.getSelectedRows();
				int respo = JOptionPane.showConfirmDialog(new JFrame(), "Deseja Ativar os produtos selecionados?");
				if(respo == 0 && rows.length > 0) {
					int status = 1;
					for(int i = 0;i<rows.length;i++) {
						setProdStatus(con, status);
					}
					refreshFrame(con, simpleQuery);
					rdnExibirDesati.setSelected(false);
				}
			}
		});
	}
	private void limparCampos() {
		txtBuscar.setText(null);
		txtChave.setText(null);
		txtCodBarra.setText(null);
		txtProduto.setText(null);
		txtValor.setText("0,0");
		txtQuantidade.setText("0");
		txtCusto.setText("0,0");
		rdnExibirDesati.setSelected(false);
		quantiAnterior = 0;
	}
	private void codeSearch(String busca) {
		if(busca.length() == 0) {
			tableSorterVendas.setRowFilter(null);
		}else {
			tableSorterVendas.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca))); //Ordena rows com a flag de Case-insensitivity
			tableEstoque.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	public void refreshFrame(Connection con, String query) {
		tableModel = new DefaultModels(columnNamesEsto, columnEditablesEsto, classesTableEsto);
		dbVendas.addRowTableEstoque(con, tableModel, query);
		tableSorterVendas = new TableRowSorter<TableModel>(tableModel);
		tableEstoque.setRowSorter(tableSorterVendas);
		tableEstoque.setModel(tableModel);
		tableEstoque.getColumnModel().getColumn(0).setMinWidth(50);
		tableEstoque.getColumnModel().getColumn(1).setMinWidth(100);
		tableEstoque.getColumnModel().getColumn(2).setPreferredWidth(3000);
		tableEstoque.getColumnModel().getColumn(3).setMinWidth(70);
		tableEstoque.getColumnModel().getColumn(4).setMinWidth(80);
		tableEstoque.getColumnModel().getColumn(5).setMinWidth(80);
		tableEstoque.getColumnModel().getColumn(5).setMinWidth(80);
		tableEstoque.getColumnModel().getColumn(6).setMinWidth(80);
	}
	
	public void setProdStatus(Connection con, int status) {
		int[] selectRows = tableEstoque.getSelectedRows();
		int idRow;
		for(int i = 0; i < selectRows.length;i++) {
			int modelRow = tableEstoque.convertRowIndexToModel(selectRows[i]);
			idRow = (int) tableModel.getValueAt(modelRow, 0);
			dbVendas.setItemStatus(con, idRow, status);
		}
	}
	public void setIcons() {
		try {
			btnAdicio.setIcon(new ImageIcon(getClass().getResource("/plus.png")));
			btnEditar.setIcon(new ImageIcon(getClass().getResource("/editar.png")));
			btnSalvar.setIcon(new ImageIcon(getClass().getResource("/salvar.png")));
			btnCancelar.setIcon(new ImageIcon(getClass().getResource("/cancelar.png")));
			btnApagar.setIcon(new ImageIcon(getClass().getResource("/apagar.png")));
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha em carregar icones");
		}
	}
}
