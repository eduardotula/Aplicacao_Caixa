package com.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.control.Conexao;
import com.control.PrintRelatorios;
import com.model.DBFrenteCaixa;
import com.model.DBOperations;
import com.model.DbGetter;
import com.model.DefaultModels;
import com.model.PrintBillFormat;
import com.model.PrintPixFormat;

import net.miginfocom.swing.MigLayout;

public class MainVenda extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Componentes
	private static DefaultModels modelVenda;
	private static DefaultModels modelProds;
	public static ArrayList<DbGetter> objSetterDb = new ArrayList<DbGetter>(); // Array de Objetos para adicionar no BD
	private static DbGetter prod;
	private Conexao cone = new Conexao();
	public static Connection con;
	private DBFrenteCaixa dbFrente = new DBFrenteCaixa();
	private TableRowSorter<TableModel> sorterProds;
	private String prodRow[] = new String[4];
	private String codBarra;
	private double ValorTotCupom = 0;
	private double valorTotCupomDinhe = 0;
	private double valorTotCupomCart = 0;
	private String tipoPagamento = null;
	public static DecimalFormat df = new DecimalFormat("R$0.###");
	private NumberFormat nf = NumberFormat.getInstance();
	private static String query = "SELECT IDPROD, CODBARRA, DESCRICAO, QUANTIDADE, VLR_ULT_VENDA, ITEN_ATIVO FROM PRODUTOS WHERE ITEN_ATIVO = 1;";
	public static int valorCaixaAberto;
	public static Integer IdCaixa;
	private String[] columnNamesEsto = new String[] { "Chave", "Codigo", "Produto", "Quantidade", "Valor" };
	private boolean[] columnEditablesEsto = new boolean[] { false, false, false, false, false };

	private Class<?>[] classesTableEsto = new Class<?>[] { Integer.class, String.class, String.class, Integer.class,
			Double.class };

	private String[] columnNameVenda = new String[] { "Quantidade", "Produto", "Valor Uni", "Valor Total" };
	private Class<?>[] classesTableVenda = new Class<?>[] { Integer.class, String.class, String.class, String.class };
	// Componentes Visuais

	private static JTable tabelaEstoque = new JTable();
	private JTextField txtBusca = new JTextField();
	private JTable tabelaVendas = new JTable();
	private JButton btnConfirmar = new JButton("Confirmar Venda");
	private JButton btnCancelar = new JButton("Cancelar Cupom");
	private JScrollPane panelEstoque;
	private JScrollPane panelVenda;
	private final JTextField txtValor = new JTextField();
	private final JLabel lblValor = new JLabel("Valor Uni:");
	private final JButton btnAdicionar = new JButton("Adicionar");
	private final JTextField txtQuanti = new JTextField();
	private final JLabel lblQuanti = new JLabel("Quantidade:");
	private static JTextField txtValorTot = new JTextField();
	private final JLabel lblValorTot = new JLabel("Valor Total:");
	private final ButtonGroup PagamentoRadio = new ButtonGroup();
	private JRadioButton rdnDinheiro = new JRadioButton("Dinheiro");
	private JRadioButton rdnCartao = new JRadioButton("Cart\u00E3o");
	private JButton btnRelatorio = new JButton();
	private final JTextField txtDesconto = new JTextField();
	private final JLabel lblNewLabel_1 = new JLabel("Desconto:");
	private JButton btnDesconto = new JButton("Aplicar Desconto");
	private final JButton btnControle = new JButton();
	private final JButton btnTrocas = new JButton();
	private JButton btnSair = new JButton("             Sair             ");
	private JButton btnApagarS = new JButton("Apagar Selecionado");
	private final JTextField txtDesc = new JTextField();
	private final JTextField txtQua = new JTextField();
	private final Component horizontalStrut = Box.createHorizontalStrut(20);
	private final JTextField txtVa = new JTextField();
	private final Component horizontalStrut_1 = Box.createHorizontalStrut(20);
	private final JTextField txtVaDes = new JTextField();
	private final JButton btnPagamentoOutro = new JButton("Outro");
	private final JTextField txtValorDinheiro = new JTextField("R$0.00");
	private final JLabel lblValorDinheiro = new JLabel("Valor Dinheiro:");
	private final JTextField txtValorCart = new JTextField("R$0.00");
	private final JLabel llbValorCartao = new JLabel("Valor Cart\u00E3o:");
	private final JButton btnImportar = new JButton();
	private final JButton btnRecarga = new JButton();
	private final JRadioButton rdnPix = new JRadioButton("Pix");
	private final JButton btnPedidos = new JButton();

	public MainVenda(HashMap<String, String> config) {
		// Janela
		super("Vender");
		setExtendedState(MAXIMIZED_BOTH);
		setMinimumSize(new Dimension(800, 400));
		txtValorCart.setColumns(6);
		txtValorDinheiro.setColumns(6);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		InterfaceStarter.setMainVisi(false);
		setSize(screenSize);
		setIcons();
		con = cone.getCone(config);
		txtVaDes.setColumns(10);
		txtVa.setColumns(10);
		txtQua.setColumns(10);
		txtDesc.setColumns(10);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtDesconto.setColumns(5);
		lblValorTot.setFont(new Font("Tahoma", Font.PLAIN, 13));
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/cashier-machine.png")));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);
		setLocationRelativeTo(null);
		modelProds = new DefaultModels(columnNamesEsto, columnEditablesEsto, classesTableEsto);
		try {
			DBOperations.appendAnyTable(con, query, modelProds);
		} catch (ClassCastException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		sorterProds = new TableRowSorter<TableModel>(modelProds);
		tabelaEstoque.setRowSorter(sorterProds);
		tabelaEstoque.setModel(modelProds);
		lblValor.setAlignmentY(Component.TOP_ALIGNMENT);
		Trocas trocas = new Trocas(con);
		RelatorioDia relatorio = new RelatorioDia(con);
		ControleCaixa controleCaixa = new ControleCaixa(con);
		ImportarEstoque importarEstoque = new ImportarEstoque(con);
		PedidosLista pedidos = new PedidosLista();
		Recargas recargas = new Recargas();
		txtValorTot.setText(df.format(0));
		df.setMaximumFractionDigits(2);
		modelVenda = new DefaultModels(columnNameVenda, classesTableVenda);
		tabelaVendas.setModel(modelVenda);
		resetTxts();
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		ValorTotCupom = 0.0;
		txtValorTot.setText("R$0,0");
		valorCaixaAberto = dbFrente.getCaixaAberto(con);
		IdCaixa = dbFrente.getIdCaixa(con);
		// Cores e Bordas

		txtQuanti.setHorizontalAlignment(SwingConstants.CENTER);
		txtQuanti.setForeground(Color.WHITE);
		txtQuanti.setFont(new Font("Arial", Font.PLAIN, 24));
		txtQuanti.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtQuanti.setBackground(Color.LIGHT_GRAY);
		txtQuanti.setColumns(3);
		lblValor.setFont(new Font("Dialog", Font.BOLD, 12));
		txtValor.setAlignmentY(Component.TOP_ALIGNMENT);
		txtValor.setFont(new Font("Arial", Font.PLAIN, 24));
		txtValor.setColumns(4);
		getContentPane().setFont(new Font("Arial", Font.PLAIN, 42));
		getContentPane().setLayout(new MigLayout("", "[575.00][137.00,grow][grow]",
				"[79.00][24.00][50.00][][367.00,grow][][][][5.00][67.00]"));

		// textField.setColumns(10);
		tabelaEstoque.setFont(new Font("Arial", Font.PLAIN, 18));
		tabelaEstoque.setBackground(Color.LIGHT_GRAY);
		tabelaEstoque.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		tabelaEstoque.getColumnModel().getColumn(0).setMinWidth(0);
		tabelaEstoque.getColumnModel().getColumn(0).setMaxWidth(0);
		tabelaEstoque.getColumnModel().getColumn(3).setPreferredWidth(50);
		tabelaEstoque.getColumnModel().getColumn(0).setMinWidth(50);
		tabelaEstoque.getColumnModel().getColumn(3).setMaxWidth(70);
		tabelaEstoque.getColumnModel().getColumn(4).setMaxWidth(50);
		txtBusca.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		txtBusca.setAlignmentY(Component.TOP_ALIGNMENT);
		txtBusca.setForeground(Color.WHITE);
		txtBusca.setFont(new Font("Arial", Font.PLAIN, 50));
		txtBusca.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtBusca.setBackground(Color.LIGHT_GRAY);
		txtBusca.setColumns(10);
		tabelaVendas.getColumnModel().getColumn(0).setResizable(false);
		tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(50);
		tabelaVendas.getColumnModel().getColumn(0).setMinWidth(50);
		tabelaVendas.getColumnModel().getColumn(1).setResizable(false);
		tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(200);
		tabelaVendas.getColumnModel().getColumn(1).setMinWidth(200);
		tabelaVendas.getColumnModel().getColumn(2).setResizable(false);
		tabelaVendas.getColumnModel().getColumn(2).setMinWidth(60);
		tabelaVendas.getColumnModel().getColumn(3).setResizable(false);
		tabelaVendas.getColumnModel().getColumn(3).setMinWidth(60);
		tabelaEstoque.getColumnModel().getColumn(1).setPreferredWidth(90);
		tabelaEstoque.getColumnModel().getColumn(2).setPreferredWidth(100);
		tabelaEstoque.getColumnModel().getColumn(2).setMinWidth(340);
		tabelaVendas.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelEstoque = new JScrollPane(tabelaEstoque);
		panelEstoque.setAutoscrolls(true);
		panelEstoque.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		tabelaVendas.setBackground(Color.LIGHT_GRAY);
		panelVenda = new JScrollPane(tabelaVendas);
		panelVenda.setOpaque(false);
		panelVenda.setAutoscrolls(true);
		panelVenda.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelVenda.setBackground(new Color(253, 240, 231));
		lblQuanti.setFont(new Font("Dialog", Font.BOLD, 12));
		btnConfirmar.setBackground(Color.GREEN);
		btnCancelar.setBackground(Color.RED);
		setVisible(true);

		btnRecarga.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (IdCaixa != null) {
					recargas.setVisible(true);
					recargas.toFront();
					recargas.requestFocus();
				} else {
					JOptionPane.showMessageDialog(null, "Favor abrir o caixa");
				}
			}
		});
		// Btn Pedidos
		btnPedidos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pedidos.setVisible(true);
				pedidos.toFront();
				pedidos.requestFocus();
				pedidos.setLocationRelativeTo(null);
				pedidos.setLocation(pedidos.getLocationOnScreen().x + 300, pedidos.getLocationOnScreen().y);
			}
		});

		// Btn importar
		btnImportar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (valorCaixaAberto == 1) {
					importarEstoque.setVisible(true);
					importarEstoque.toFront();
					importarEstoque.requestFocus();
				} else {
					JOptionPane.showMessageDialog(null, "Favor Abrir o Caixa");
				}
			}
		});
		// Btn Outro
		btnPagamentoOutro.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tabelaVendas.getRowCount() == 1) {
					try {
						JPanel panel = getPanel();
						JOptionPane.showConfirmDialog(getFrame(), panel, "Valor", JOptionPane.OK_CANCEL_OPTION);
						JTextField valorD = (JTextField) panel.getComponent(1);
						JTextField valorC = (JTextField) panel.getComponent(3);
						valorTotCupomCart = Double.parseDouble(valorC.getText().replace(",", "."));
						valorTotCupomDinhe = Double.parseDouble(valorD.getText().replace(",", "."));
						DbGetter prod = objSetterDb.get(0);
						prod.setValorCartao(valorTotCupomCart);
						prod.setValorDinheiro(valorTotCupomDinhe);
						txtValorCart.setText(nf.format(valorTotCupomCart));
						txtValorDinheiro.setText(nf.format(valorTotCupomDinhe));
						PagamentoRadio.clearSelection();
						JOptionPane.showMessageDialog(null, "Pagamento CD selecionado");
						tipoPagamento = "CD";
						rdnCartao.setEnabled(false);
						rdnDinheiro.setEnabled(false);
						btnDesconto.setEnabled(false);
						btnConfirmar.setEnabled(true);
						btnAdicionar.setEnabled(false);
						rdnPix.setEnabled(false);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Valores Invãlidos");
						e.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Para Utilizar esta função apenas 1 produto á permitido no cupom");
				}
			}

			private JPanel getPanel() {
				JPanel panel = new JPanel();
				panel.setLayout(new MigLayout("", "[grow]", "[][][]"));
				JLabel lblValorDinheiro = new JLabel("Valor Dinheiro");
				panel.add(lblValorDinheiro, "flowx,cell 0 0,alignx center");
				JTextField txtValorDinhe = new JTextField();
				panel.add(txtValorDinhe, "cell 0 0,alignx center");
				txtValorDinhe.setColumns(10);
				JLabel lblValorCart = new JLabel("Valor Cart\u00E3o  ");
				panel.add(lblValorCart, "flowx,cell 0 1,alignx center");
				JTextField txtValorCart = new JTextField();
				panel.add(txtValorCart, "cell 0 1,alignx center");
				txtValorCart.setColumns(10);
				return panel;

			}

			private JFrame getFrame() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout(0, 0));
				frame.setSize(177, 124);
				return frame;
			}
		});
		// btn Trocas
		btnTrocas.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (valorCaixaAberto == 1) {
					trocas.setVisible(true);
					trocas.toFront();
					trocas.requestFocus();
				} else {
					JOptionPane.showMessageDialog(null, "Favor Abrir o Caixa");
				}
			}
		});

		// btn Apagar Selecionado
		btnApagarS.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ro = tabelaVendas.getSelectedRow();
				if (objSetterDb.size() != 0 && ro != -1) {
					int res = JOptionPane.showConfirmDialog(null, "Deseja Apagar o Produto Selecionado?");
					if (res == 0) {
						DbGetter prod = objSetterDb.get(ro);
						double valorP = prod.getValorUn();
						objSetterDb.remove(ro);
						modelVenda.removeRow(ro);
						ValorTotCupom = ValorTotCupom - valorP;
						txtValorTot.setText(df.format(ValorTotCupom));
					}
				} else {
					JOptionPane.showMessageDialog(null, "Nenhum Produto Selecionado");
				}
			}
		});
		// txtValor tecla enter
		txtValor.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					btnAdicionar.requestFocus();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		// txtQuanti tecla enter
		txtQuanti.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					txtValor.requestFocus();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		// btndesconto
		btnDesconto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (txtDesconto.getText().length() != 0 && tabelaVendas.getRowCount() > 0) {
					int tblSize = tabelaVendas.getRowCount();
					try {
						double desconto = Double.parseDouble(txtDesconto.getText().replace(",", "."));
						descontoVenda(tblSize, desconto, ValorTotCupom);
						txtDesconto.setText("");
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(new JFrame(), "Desconto invalido");
					}
				} else {
					txtDesconto.setText("");
				}
			}
		});
		// Pressiona Botao Cancelar Venda

		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rowCount = tabelaVendas.getRowCount();
				txtValorTot.setText(df.format(0));
				txtDesconto.setText("");
				tipoPagamento = null;
				ValorTotCupom = 0;
				for (int i = 0; rowCount > i; i++) {
					modelVenda.removeRow(0);
				}
				objSetterDb.clear();
				resetTxts();

			}
		});
		// Pressiona Botao Sair Venda

		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleCaixa.dispose();
				relatorio.dispose();
				trocas.dispose();
				importarEstoque.dispose();
				recargas.dispose();
				pedidos.disposeAll();
				btnCancelar.doClick();
				try {
					con.close();
					dispose();
					InterfaceStarter.setMainVisi(true);
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "Não Foi possãvel finalizar");
				}
			}
		});
		// Pressiona Botao confirmar Venda

		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (objSetterDb.size() > 0 && valorCaixaAberto == 1) {
					LocalDate date = LocalDate.now();
					LocalTime time = LocalTime.now();

					try {
						for (DbGetter prod : objSetterDb) {
							System.out.println(prod.getValorUn());
							System.out.println(prod.getValorDinheiro());
							System.out.println(prod.getValorDinheiro());
							System.out.println("Tipo pagamento: " + tipoPagamento + LocalDateTime.now());
							if ((tipoPagamento != null && tipoPagamento.compareTo("C") == 0)
									|| rdnCartao.isSelected()) {
								prod.setValorCartao(prod.getValorTot());
								prod.setTipoPagamento("C");
							} else if ((tipoPagamento != null && tipoPagamento.compareTo("D") == 0)
									|| rdnDinheiro.isSelected()) {
								prod.setValorDinheiro(prod.getValorTot());
								prod.setTipoPagamento("D");
							} else if ((tipoPagamento != null && tipoPagamento.compareTo("Pix") == 0)
									|| rdnPix.isSelected()) {
								prod.setValorCartao(prod.getValorTot());
								prod.setTipoPagamento("Pix");
							} else if (tipoPagamento != null && tipoPagamento.compareTo("CD") == 0) {
								prod.setTipoPagamento("CD");
							} else {
								new Exception();
								JOptionPane.showMessageDialog(null, "Metodo de pagamento invalido", "Erro",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						if (tipoPagamento.equals("Pix")) {
							String cnpj = JOptionPane.showInputDialog("Chave utilizada");
							printer(date, time, ValorTotCupom, cnpj);
						}
						tipoPagamento = null;

						System.out.println(prod.getValorUn());
						System.out.println(prod.getValorDinheiro());
						System.out.println(prod.getValorDinheiro());
						dbFrente.updateVendas(objSetterDb, con, date, time);

						int conf = JOptionPane.showConfirmDialog(new JFrame(), "Imprimir Cupom?");
						if (conf == 0) {
							printerCupom(objSetterDb, date, time, txtDesconto.getText());
						}

						PagamentoRadio.clearSelection();
						txtValorTot.setText(df.format(0));
						ValorTotCupom = 0;
						txtDesconto.setText("");
						resetTxts();
						objSetterDb.clear();
						btnConfirmar.setEnabled(false);
						int rowCount = tabelaVendas.getRowCount();
						for (int i = 0; rowCount > i; i++) {
							modelVenda.removeRow(0);
						}
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(new JFrame(), "Falha na Venda");
					}

				} else {
					if (valorCaixaAberto == 0) {
						JOptionPane.showMessageDialog(new JFrame(), "Favor Abrir o Caixa");
					} else if (objSetterDb.size() < 0) {
						JOptionPane.showMessageDialog(new JFrame(), "Nenhum Produto em Venda");
					}
				}
			}
		});

		// Double Click na tabela estoque
		tabelaEstoque.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					JTable t = (JTable) arg0.getSource();
					int row = t.getSelectedRow();
					String aValue = (String) t.getValueAt(row, 2);
					setTextBusca(row, aValue); // Se a ultima venda deste produto for != 0 o valor sera adicionado no
												// campo de valor
				}
			}
		});
		// Click enter Tabela Estoque
		tabelaEstoque.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					int row = tabelaEstoque.getSelectionModel().getLeadSelectionIndex();
					String aValue = (String) tabelaEstoque.getValueAt(row, 2);
					setTextBusca(row, aValue); // Se a ultima venda deste produto for != 0 o valor sera adicionado no
												// campo de valor
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		// btn Controle Caixa
		btnControle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleCaixa.setVisible(true);
				controleCaixa.toFront();
				controleCaixa.requestFocus();
				controleCaixa.updateFrame(con);
				valorCaixaAberto = dbFrente.getCaixaAberto(con);
				// Checa se o caixa esta aberto atualiza o status
				if (MainVenda.valorCaixaAberto == 1) {
					controleCaixa.lblStatus.setText("Aberto");
					controleCaixa.lblStatus.setForeground(Color.green);
				} else if (MainVenda.valorCaixaAberto == 0) {
					controleCaixa.lblStatus.setText("Fechado");
					controleCaixa.lblStatus.setForeground(Color.red);
				}

			}
		});

		// Listner para tecla enter ou keyup ou keydown
		txtBusca.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {// key enter
					int row = tabelaEstoque.getSelectionModel().getLeadSelectionIndex();
					String aValue = (String) tabelaEstoque.getValueAt(row, 2);
					setTextBusca(row, aValue); // Se a ultima venda deste produto for != 0 o valor sera adicionado no
												// campo de valor
				} else if (e.getKeyCode() == 38) { // key para baixo
					int row = tabelaEstoque.getSelectionModel().getLeadSelectionIndex() - 1;
					tabelaEstoque.getSelectionModel().setSelectionInterval(row, row);
				} else if (e.getKeyCode() == 40) { // key para cima
					int row = tabelaEstoque.getSelectionModel().getLeadSelectionIndex() + 1;
					tabelaEstoque.getSelectionModel().setSelectionInterval(row, row);
				}
			}
		});
		// Tecla enter Busca de Texto
		// Busca o texto na tabela estoque
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

		// Botão Adicionar
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = tabelaEstoque.getSelectionModel().getLeadSelectionIndex();
				String quanti = txtQuanti.getText();
				String valorUnis = txtValor.getText().replace(",", ".");

				if (quanti.length() != 0 || valorUnis.length() != 0) {
					try {
						codBarra = (String) tabelaEstoque.getValueAt(row, 1);
						String produto = (String) tabelaEstoque.getValueAt(row, 2);

						valorUnis = valorUnis.replace(",", ".");
						int idEsto = (int) tabelaEstoque.getValueAt(row, 0);
						int quantInt = Math.abs(Integer.parseInt(quanti));
						double valoruni = Double.parseDouble(valorUnis);
						double valorTot = valoruni * quantInt;
						double valorTotCup = ValorTotCupom + valorTot;
						txtValorTot.setText(df.format(valorTotCup));
						prod = new DbGetter();
						prod.setChaveEsto(idEsto);
						prod.quantVSetter(quantInt);
						prod.codVSetter(codBarra);
						prod.descVSetter(produto);
						prod.valorUniSetter(valoruni);
						prod.valorTotalSetter(valorTot);
						prodRow[0] = Integer.toString(quantInt);
						prodRow[1] = produto;
						prodRow[2] = df.format(valoruni);
						prodRow[3] = df.format(valorTot);
						objSetterDb.add(prod);
						modelVenda.addRow(prodRow);
						ValorTotCupom = valorTotCup;
						resetTxts();
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(new JFrame(), "Quantidade ou Valor Invãlido");
					}
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Quantidade ou Valor Invãlido");
				}
				txtBusca.requestFocus();
			}
		});
		// Botoes Radio
		rdnDinheiro.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnConfirmar.setEnabled(true);
				tipoPagamento = "D";
				System.out.println("Pagamento dinheiro Selecionado" + LocalDateTime.now());
			}
		});

		rdnCartao.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnConfirmar.setEnabled(true);
				tipoPagamento = "C";
				System.out.println("Pagamento cartao Selecionado" + LocalDateTime.now());

			}
		});

		rdnPix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnConfirmar.setEnabled(true);
				tipoPagamento = "Pix";
				System.out.println("Pagamento pix Selecionado " + LocalDateTime.now());

			}
		});

		// Layout
		txtValorTot.setColumns(6);
		getContentPane().add(txtBusca, "cell 0 0 3 1,grow");
		getContentPane().add(lblQuanti, "flowx,cell 0 1,alignx left,growy");
		getContentPane().add(txtQuanti, "flowx,cell 0 2,alignx left,growy");
		getContentPane().add(panelEstoque, "cell 0 4 1 6,grow");
		panelEstoque.setViewportView(tabelaEstoque);
		getContentPane().add(panelVenda, "cell 2 4,grow");
		panelVenda.setViewportView(tabelaVendas);

		getContentPane().add(btnPagamentoOutro, "flowx,cell 2 7,alignx right");
		PagamentoRadio.add(rdnPix);

		getContentPane().add(rdnPix, "cell 2 7");
		getContentPane().add(rdnCartao, "cell 2 7,alignx right");

		PagamentoRadio.add(rdnCartao);
		getContentPane().add(rdnDinheiro, "cell 2 7,alignx right");
		PagamentoRadio.add(rdnDinheiro);
		getContentPane().add(btnApagarS, "flowx,cell 2 5,alignx right");
		getContentPane().add(lblNewLabel_1, "cell 2 5,alignx right");
		getContentPane().add(txtDesconto, "cell 2 5,alignx right");
		getContentPane().add(horizontalStrut, "cell 0 2");
		getContentPane().add(txtValor, "cell 0 2,growy");
		getContentPane().add(horizontalStrut_1, "cell 0 1");
		getContentPane().add(lblValor, "cell 0 1,alignx left,growy");

		getContentPane().add(btnPedidos, "flowx,cell 2 3,alignx right");

		getContentPane().add(btnRecarga, "cell 2 3,alignx right");

		getContentPane().add(btnImportar, "cell 2 3,alignx right");
		getContentPane().add(btnTrocas, "cell 2 3,alignx right");
		getContentPane().add(btnControle, "cell 2 3,alignx right");
		getContentPane().add(btnDesconto, "cell 2 6,alignx right");

		getContentPane().add(llbValorCartao, "flowx,cell 2 8,alignx right");

		getContentPane().add(txtValorCart, "cell 2 8");

		getContentPane().add(lblValorDinheiro, "cell 2 8,alignx left");

		getContentPane().add(txtValorDinheiro, "cell 2 8");
		getContentPane().add(lblValorTot, "cell 2 8,alignx right");
		getContentPane().add(txtValorTot, "cell 2 8,alignx right");
		getContentPane().add(btnSair, "flowx,cell 2 9,alignx right,growy");
		getContentPane().add(btnCancelar, "cell 2 9,alignx right,growy");
		getContentPane().add(btnConfirmar, "cell 2 9,alignx right,growy");
		getContentPane().add(btnAdicionar, "cell 0 2,growy");
		txtValorTot.setEditable(false);
		txtValorCart.setEditable(false);
		txtValorDinheiro.setEditable(false);
		btnConfirmar.setEnabled(false);
		// Botao Relatorio
		btnRelatorio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				relatorio.setVisible(true);
				relatorio.updateTable(con);
				relatorio.toFront();
				relatorio.requestFocus();
			}
		});
		getContentPane().add(btnRelatorio, "cell 2 3,alignx right");

	}

	public static void refreshEstoque() throws ClassCastException, SQLException {
		modelProds.removeAllRows();
		DBOperations.appendAnyTable(con, query, modelProds);
		tabelaEstoque.repaint();
	}

	private void codeSearch(String busca) {
		if (busca.length() == 0) {
			sorterProds.setRowFilter(null);
		} else {

			sorterProds.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca))); // Ordena rows com a flag de
																							// Case-insensitivity
			tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	public void setNewProdVenda(Connection con, Object[] prod, double valor) {
		ValorTotCupom = ValorTotCupom + valor;

		txtValorTot.setText(df.format(ValorTotCupom));
		modelVenda.addRow(prod);
	}

	public void setProdsDbSetter(String codBarra, String produto, int quanti, double precoUn, double precoTot) {
		prod = new DbGetter();
		prod.codVSetter(codBarra);
		prod.descVSetter(produto);
		prod.quantVSetter(quanti);
		prod.valorUniSetter(precoUn);
		prod.valorTotalSetter(precoTot);
		objSetterDb.add(prod);

	}

	public void resetTxts() {
		txtBusca.setText("");
		txtQuanti.setText("1");
		txtValor.setText("");
		rdnCartao.setEnabled(true);
		rdnDinheiro.setEnabled(true);
		btnDesconto.setEnabled(true);
		btnAdicionar.setEnabled(true);
		rdnPix.setEnabled(true);
		valorTotCupomCart = 0;
		valorTotCupomDinhe = 0;
		tipoPagamento = null;
		txtValorCart.setText("R$0.00");
		txtValorDinheiro.setText("R$0.00");
	}

	public void descontoVenda(int tblSize, double desconto, double valorTotCupom2) {
		double valorTotCupo = 0;
		double valorDescontado = valorTotCupom2 - desconto;
		double porce = (valorDescontado / valorTotCupom2);
		for (int t = 0; tblSize > t; t++) {
			try {
				DbGetter prod = objSetterDb.get(t);
				double valorUn = prod.getValorUn();
				double valorTot = prod.getValorTot();
				double descontado = valorUn * porce;
				double precoTotCon = ((valorTot * porce));
				tabelaVendas.setValueAt(nf.format(descontado), t, 2);
				tabelaVendas.setValueAt(nf.format(precoTotCon), t, 3);
				valorTotCupo = valorTotCupo + precoTotCon;
				prod.valorUniSetter(descontado);
				prod.valorTotalSetter(precoTotCon);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		ValorTotCupom = Double.parseDouble(nf.format(valorTotCupo));
		txtValorTot.setText(df.format(valorTotCupo));
	}

	private void printerCupom(ArrayList<DbGetter> setterDb, LocalDate date, LocalTime time, String string) {
		try {
			PrintRelatorios printRela = new PrintRelatorios();
			DocPrintJob job = printRela.getPrinterJob((String) DBOperations.selectSql1Dimen(con,
					"SELECT IMPRESSORA FROM CADASTRO_LOJA WHERE ID = 1", new String[0])[0]);
			DocAttributeSet das = new HashDocAttributeSet();
			das.add(PrintQuality.HIGH);
			das.add(MediaSizeName.ISO_A4);
			DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			PrintBillFormat bf = new PrintBillFormat();
			Object[] dados =  DBOperations.selectSql2Dimen(con, "SELECT RAZAO,CNPJ,ENDERECO,CIDADE,NUMERO FROM CADASTRO_LOJA WHERE ID = 1", 5)[0];
			String[] dadosS = new String[dados.length];
			for(int i = 0;i<dados.length;i++) dadosS[i] = (String) dados[i];
			bf.passArrayList(dadosS, setterDb, date, time, string);
			SimpleDoc doc = new SimpleDoc(bf, flavor, das);
			job.print(doc, null);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Falha na Impressão");
			PagamentoRadio.clearSelection();
			txtValorTot.setText(df.format(0));
			ValorTotCupom = 0;
			txtDesconto.setText("");
			resetTxts();
			objSetterDb.clear();
			btnConfirmar.setEnabled(false);
			int rowCount = tabelaVendas.getRowCount();
			for (int i = 0; rowCount > i; i++) {
				modelVenda.removeRow(0);
			}
			e.printStackTrace();
		}
	}

	private void printer(LocalDate date, LocalTime time, double valor, String cnpj) {
		PrintRelatorios printRela = new PrintRelatorios();
		PrintPixFormat px = new PrintPixFormat();
		px.passArrayList(date, time, valor, cnpj);
		try {
			String printer = (String) DBOperations.selectSql1Dimen(con,
					"SELECT IMPRESSORA FROM CADASTRO_LOJA WHERE ID = 1", new String[0])[0];
			printRela.printer(px, printer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTextBusca(int row, String desc) {
		double vlr = 0.0;
		int modelRow = tabelaEstoque.convertRowIndexToModel(row);
		if (tabelaEstoque.getValueAt(row, 4) != null) {
			vlr = (double) tabelaEstoque.getValueAt(row, 4);
		}
		txtBusca.setText(desc);
		int viewRow = tabelaEstoque.convertRowIndexToView(modelRow);
		tabelaEstoque.getSelectionModel().setSelectionInterval(viewRow, viewRow);
		if (vlr != 0) {
			txtValor.setText(nf.format(vlr));
			btnAdicionar.requestFocus();
		} else {
			txtValor.requestFocus();
		}
	}

	private void setIcons() {
		try {
			btnTrocas.setIcon(new ImageIcon(getClass().getResource("/trocas.png")));
			btnRelatorio.setIcon(new ImageIcon(getClass().getResource("/relatorio.png")));
			btnControle.setIcon(new ImageIcon(getClass().getResource("/controlecaixa.png")));
			btnImportar.setIcon(new ImageIcon(getClass().getResource("/importar.png")));
			btnRecarga.setIcon(new ImageIcon(getClass().getResource("/recarga.png")));
			btnPedidos.setIcon(new ImageIcon(getClass().getResource("/pedido-online.png")));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha em selecionar icones");
		}
	}
}
