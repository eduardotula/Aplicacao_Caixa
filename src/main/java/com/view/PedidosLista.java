package com.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.control.PrintRelatorios;
import com.control.TableOperations;
import com.model.Alerts;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.model.PrintPedidos;
import com.model.PrintPedidos.PrintLista;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;

import net.miginfocom.swing.MigLayout;

public class PedidosLista extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtCodBarL;
	private JTextField txtDescriL;
	private JTextField txtClienteP;
	private JTextField txtNumeroP;
	private JLabel lblCodBarrasl;
	private JPanel panelLista;
	private JLabel lblDescrL;
	private JButton BtnBuscarL;
	private JLabel lblOberserL;
	private JScrollPane scrollPane;
	private JTextArea txtOberserL;
	private JButton btnAdicionar;
	private JPanel panelPedido;
	private JLabel lblDescricaoP;
	private JScrollPane scrollPane_1;
	private JTextArea txtDescricaoP;
	private JLabel lblNomeP;
	private JLabel lblNumeroP;
	private JButton btnPedidosP;
	private TableOperations to = new TableOperations();
	private JPanel p;
	private Lista lista = new Lista();
	private Pedidos pedido = new Pedidos();
	private Font textoFonte = new Font("Tahoma", Font.PLAIN, 11);
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	private JPanel panel;

	public PedidosLista() {
		super("Pedidos");
		createAndShowGUI();
		setListners();
	}

	public void createAndShowGUI() {
		setSize(360, 300);
		setVisible(false);
		setLocationRelativeTo(null);
		tabbedPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		panelLista = new JPanel();
		tabbedPane.addTab("Lista", null, panelLista, null);
		panelLista.setLayout(new MigLayout("", "[208.00,grow][grow][grow]", "[][][][][][grow]"));
		lblCodBarrasl = new JLabel("Codigo de Barras");
		panelLista.add(lblCodBarrasl, "cell 0 0");
		txtCodBarL = new JTextField();
		panelLista.add(txtCodBarL, "flowx,cell 0 1,alignx left");
		txtCodBarL.setColumns(12);
		lblDescrL = new JLabel("Descri\u00E7\u00E3o");
		panelLista.add(lblDescrL, "cell 0 2");
		txtDescriL = new JTextField();
		panelLista.add(txtDescriL, "cell 0 3,alignx left");
		txtDescriL.setColumns(25);
		BtnBuscarL = new JButton("Buscar");
		panelLista.add(BtnBuscarL, "cell 0 1,aligny bottom");
		lblOberserL = new JLabel("Observa\u00E7\u00E3o");
		panelLista.add(lblOberserL, "cell 0 4");
		scrollPane = new JScrollPane();
		panelLista.add(scrollPane, "cell 0 5 3 1,grow");
		txtOberserL = new JTextArea();
		scrollPane.setViewportView(txtOberserL);
		panelPedido = new JPanel();
		tabbedPane.addTab("Pedido", null, panelPedido, null);
		panelPedido.setLayout(new MigLayout("", "[grow][grow]", "[][grow][23.00][23.00][][]"));
		lblDescricaoP = new JLabel("Descri\u00E7\u00E3o");
		panelPedido.add(lblDescricaoP, "cell 0 0 2 1");
		scrollPane_1 = new JScrollPane();
		panelPedido.add(scrollPane_1, "cell 0 1 2 1,grow");
		txtDescricaoP = new JTextArea();
		scrollPane_1.setViewportView(txtDescricaoP);
		lblNomeP = new JLabel("Cliente");
		lblNomeP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelPedido.add(lblNomeP, "cell 0 2 2 1");
		txtClienteP = new JTextField();
		txtClienteP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelPedido.add(txtClienteP, "cell 0 3 2 1,alignx left,growy");
		txtClienteP.setColumns(20);
		lblNumeroP = new JLabel("Numero");
		lblNumeroP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panelPedido.add(lblNumeroP, "cell 0 4 2 1");
		txtNumeroP = new JTextField();
		txtNumeroP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelPedido.add(txtNumeroP, "cell 0 5 2 1,alignx left");
		txtNumeroP.setColumns(13);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new MigLayout("", "[][][][][][][grow]", "[]"));
		btnPedidosP = new JButton("Lista");
		panel.add(btnPedidosP, "cell 0 0");
		btnAdicionar = new JButton("Adicionar");
		panel.add(btnAdicionar, "cell 6 0,alignx right");
		txtOberserL.setLineWrap(true);
		txtDescricaoP.setLineWrap(true);
	}

	public void setListners() {

		BtnBuscarL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				JTable tabelaEstoque = new JTable();
				JTextField txtBusca = new JTextField();
				JScrollPane scrollPane = new JScrollPane();
				JButton btnConfirmar = new JButton("Confirmar");
				DefaultModels modelEstoque = new DefaultModels(
						new String[] { "ID", "CodBarra", "Descrição", "Quantidade", "Valor Venda" },
						new boolean[] { false, false, false, false, false },
						new Class<?>[] { Integer.class, String.class, String.class, Integer.class, Double.class });
				TableRowSorter<DefaultModels> sorter = new TableRowSorter<DefaultModels>(modelEstoque);
				try {
					DBOperations.appendAnyTable(MainVenda.con,
							"SELECT IDPROD, CODBARRA, DESCRICAO,QUANTIDADE, VLR_ULT_VENDA, ITEN_ATIVO FROM PRODUTOS WHERE ITEN_ATIVO = 1;",
							modelEstoque);
				} catch (ClassCastException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				tabelaEstoque.setRowSorter(sorter);
				tabelaEstoque.setModel(modelEstoque);
				tabelaEstoque.getColumnModel().getColumn(2).setPreferredWidth(400);
				tabelaEstoque.getColumnModel().getColumn(0).setPreferredWidth(60);
				tabelaEstoque.getColumnModel().getColumn(4)
						.setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
				frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));
				frame.getContentPane().add(txtBusca, "flowx,cell 0 0,growx");
				txtBusca.setColumns(10);
				frame.getContentPane().add(scrollPane, "cell 0 1,grow");
				scrollPane.setViewportView(tabelaEstoque);
				frame.getContentPane().add(btnConfirmar, "cell 0 0");
				frame.setVisible(true);
				frame.setSize(800, 400);
				frame.setLocationRelativeTo(null);
				btnConfirmar.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int row = tabelaEstoque.getSelectedRow();
						int modelRow = tabelaEstoque.convertRowIndexToModel(row);
						String codBarra = modelEstoque.getValueAtStr(modelRow, 1);
						String descricao = modelEstoque.getValueAtStr(modelRow, 2);
						txtCodBarL.setText(codBarra);
						txtDescriL.setText(descricao);
						frame.dispose();
					}
				});

				// Double Click na tabela estoque
				tabelaEstoque.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
						if (arg0.getClickCount() == 2) {
							btnConfirmar.doClick();
						}
					}
				});

				txtBusca.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent e) {
						if (txtBusca.getText().length() == 0) {
							sorter.setRowFilter(null);
						} else {

							sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(txtBusca.getText())));
							tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
						}
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						if (txtBusca.getText().length() == 0) {
							sorter.setRowFilter(null);
						} else {

							sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(txtBusca.getText())));
							tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
						}
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						if (txtBusca.getText().length() == 0) {
							sorter.setRowFilter(null);
						} else {

							sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(txtBusca.getText()))); // Ordena
																													// rows
																													// com
																													// a
																													// flag
																													// de
																													// Case-insensitivity
							tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
						}
					}
				});

			}
		});

		btnPedidosP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex() == 0) {
					Point p = getLocationOnScreen();
					lista.setLocation(p.x - 600, p.y);
					lista.setVisible(true);
					lista.toFront();
					lista.requestFocus();
					lista.refreshTable();
					pedido.setVisible(false);
				} else if (tabbedPane.getSelectedIndex() == 1) {
					Point p = getLocationOnScreen();
					pedido.setLocation(p.x - 600, p.y);
					pedido.setVisible(true);
					pedido.toFront();
					pedido.requestFocus();
					pedido.refreshTable();
					lista.setVisible(false);
				}
			}
		});
		btnAdicionar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex() == 0) {
					try {
						String codBarra = txtCodBarL.getText();
						String descricao = txtDescriL.getText();
						String obser = txtOberserL.getText();
						if (!descricao.isEmpty()) {
							PreparedStatement ps = MainVenda.con
									.prepareStatement("SELECT * FROM LISTACOMPRA WHERE DESCRICAO = ?");
							ps.setString(1, descricao);
							ResultSet rs = ps.executeQuery();
							if (!rs.next()) {
								String query = "INSERT INTO LISTACOMPRA VALUES(NULL,?,?,?,?)";
								ps = MainVenda.con.prepareStatement(query);
								ps.setString(1, codBarra);
								ps.setString(2, descricao);
								ps.setString(3, obser);
								ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
								ps.executeUpdate();
								txtCodBarL.setText(null);
								txtDescriL.setText(null);
								txtOberserL.setText(null);
							} else {
								JOptionPane.showMessageDialog(null, "Produto Jã cadastrado");
							}
						}
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, "Valores Invalidos");
						e2.printStackTrace();
					}
				} else if (tabbedPane.getSelectedIndex() == 1) {
					try {
						String descri = txtDescricaoP.getText();
						String cliente = txtClienteP.getText();
						String numero = txtNumeroP.getText();
						if (!cliente.isEmpty() && !descri.isEmpty()) {
							String query = "INSERT INTO PEDIDOS VALUES(NULL,?,?,?,?)";
							PreparedStatement ps = MainVenda.con.prepareStatement(query);
							ps.setString(1, descri);
							ps.setString(2, cliente);
							ps.setString(3, numero);
							ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
							ps.executeUpdate();
							txtDescricaoP.setText(null);
							txtClienteP.setText(null);
							txtNumeroP.setText(null);
						} else {
							throw new Exception();
						}
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, "Valores Invalidos");
						e2.printStackTrace();
					}

				}
			}
		});
	}

	public void disposeAll() {
		pedido.dispose();
		lista.dispose();
		dispose();
	}

	public class Pedidos extends JFrame {
		private DefaultModels tableModel = new DefaultModels(
				new String[] { "ID", "Descrição", "Cliente", "Numero", "Data" },
				new boolean[] { false, false, false, false, false },
				new Class<?>[] { Integer.class, String.class, String.class, String.class, LocalDate.class });
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTable table;
		private JScrollPane scrollPane;
		private JButton btnExportar;
		private JButton btnApagarTudo;
		private JButton BtnApagar;
		private JButton btnImprimir;

		public Pedidos() {
			super("Pedidos");
			createAndShowGUI();
			setList();
			refreshTable();
		}

		public void createAndShowGUI() {
			setSize(600, 470);
			setVisible(false);
			scrollPane = new JScrollPane();
			JMenuBar menuBar = new JMenuBar();
			table = new JTable();
			BtnApagar = new JButton("Apagar");
			btnApagarTudo = new JButton("ApagarTudo");
			btnExportar = new JButton("Exportar");
			btnImprimir = new JButton("Imprimir");

			table.setModel(tableModel);
			table.setAutoCreateRowSorter(true);

			getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));
			getContentPane().add(scrollPane, "cell 0 0 1 2,grow");
			scrollPane.setViewportView(table);
			setJMenuBar(menuBar);
			menuBar.add(BtnApagar);
			menuBar.add(btnApagarTudo);
			menuBar.add(btnExportar);
			menuBar.add(btnImprimir);
			table.setFont(textoFonte);
		}

		public void setList() {

			// Esta Action detecta quando o tamanho da janela for alterado
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					redrawRowsHight(table, tableModel, 0);
				}
			});
			addWindowFocusListener(new WindowFocusListener() {
				@Override
				public void windowLostFocus(WindowEvent e) {
					if (p == null) {
						setVisible(false);
					}
				}

				@Override
				public void windowGainedFocus(WindowEvent e) {
				}
			});

			btnApagarTudo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					p = new JPanel();
					int res = JOptionPane.showConfirmDialog(null, "Deseja apagar a lista?");
					if (res == JOptionPane.OK_OPTION) {
						apagarTudo("DELETE FROM PEDIDOS");
					}
					p = null;
				}
			});
			BtnApagar.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					p = new JPanel();
					int res = JOptionPane.showConfirmDialog(null, "Deseja apagar as linhas selecionadas?");
					if (res == JOptionPane.OK_OPTION) {
						to.ApagarSelecioTabela(MainVenda.con, tableModel, table, "DELETE FROM PEDIDOS WHERE ID = ?");
						refreshTable();
					}
					p = null;
				}
			});
			btnExportar.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					tableModel.exportarCSV();
				}
			});
			btnImprimir.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PrintRelatorios pr = new PrintRelatorios();
					PrintPedidos pp = new PrintPedidos();
					List<Object[]> lis = new ArrayList<Object[]>();
					for (int i = 0; i < table.getRowCount(); i++) {
						Object[] linha = new Object[3];
						int modelRow = table.convertColumnIndexToModel(i);
						linha[0] = tableModel.getValueAt(modelRow, 0);
						linha[1] = tableModel.getValueAt(modelRow, 1);
						linha[2] = tableModel.getValueAt(modelRow, 2);
						lis.add(linha);
					}
					pp.passArrayList(LocalTime.now(), lis);
					pr.choosePrintType(table, pp);
				}
			});
		}

		public void refreshTable() {
			String query = "SELECT ID,DESCRICAO,NOME,NUMERO, DATA FROM PEDIDOS";
			tableModel.removeAllRows();
			try {
				DBOperations.appendAnyTable(MainVenda.con, query, tableModel);
			} catch (SQLException e) {
				e.printStackTrace();
				Alerts.showError("Falha ao atualizar pedidos", "Erro");
			}
			setSize(600, 470);
			TableColumnModel m = table.getColumnModel();
			m.getColumn(0).setMinWidth(0);
			m.getColumn(0).setMaxWidth(0);
			m.getColumn(1).setPreferredWidth(3000);
			m.getColumn(2).setMinWidth(70);
			m.getColumn(3).setMinWidth(70);
			m.getColumn(4).setMinWidth(80);
			m.getColumn(4).setCellRenderer(TableRendererDate.getDateTimeRenderer());
			m.getColumn(1).setCellRenderer(new TableRender());
			redrawRowsHight(table, tableModel, 0);
		}

	}

	public class Lista extends JFrame {
		private DefaultModels tableModel = new DefaultModels(
				new String[] { "ID", "Cod.Barra", "Produto", "Observação", "Data" },
				new boolean[] { false, false, false, false, false, false, false },
				new Class<?>[] { Integer.class, String.class, String.class, String.class, LocalDate.class });
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTable table;
		private JScrollPane scrollPane;
		private JButton btnExportar;
		private JButton btnApagarTudo;
		private JButton BtnApagar;
		private JButton btnImprimir;

		public Lista() {
			super("Lista");
			createAndShowGUI();
			setList();
			refreshTable();
		}

		public void createAndShowGUI() {
			setSize(600, 470);
			setVisible(false);
			JMenuBar menuBar = new JMenuBar();
			btnApagarTudo = new JButton("ApagarTudo");
			btnExportar = new JButton("Exportar");
			BtnApagar = new JButton("Apagar");
			btnImprimir = new JButton("Imprimir");

			table = new JTable();
			scrollPane = new JScrollPane();

			table.setModel(tableModel);
			table.setAutoCreateRowSorter(true);
			getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));
			getContentPane().add(scrollPane, "cell 0 0 1 2,grow");
			scrollPane.setViewportView(table);
			setJMenuBar(menuBar);
			menuBar.add(BtnApagar);
			menuBar.add(btnApagarTudo);
			menuBar.add(btnExportar);
			menuBar.add(btnImprimir);
			table.setFont(textoFonte);
		}

		public void setList() {

			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					redrawRowsHight(table, tableModel, 2);
				}
			});
			addWindowFocusListener(new WindowFocusListener() {
				@Override
				public void windowLostFocus(WindowEvent e) {
					if (p == null) {
						setVisible(false);
					}
				}

				@Override
				public void windowGainedFocus(WindowEvent e) {
				}
			});

			btnApagarTudo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					p = new JPanel();
					int res = JOptionPane.showConfirmDialog(p, "Deseja apagar a lista?");
					if (res == JOptionPane.OK_OPTION) {
						apagarTudo("DELETE FROM LISTACOMPRAS");
					}
					p = null;
				}
			});
			BtnApagar.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					p = new JPanel();
					int res = JOptionPane.showConfirmDialog(null, "Deseja apagar as linhas selecionadas?");
					if (res == JOptionPane.OK_OPTION) {
						to.ApagarSelecioTabela(MainVenda.con, tableModel, table,
								"DELETE FROM LISTACOMPRA WHERE ID = ?");
						refreshTable();
					}
					p = null;
				}
			});
			btnExportar.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					tableModel.exportarCSV();
				}
			});
			btnImprimir.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PrintRelatorios pr = new PrintRelatorios();
					PrintPedidos.PrintLista pp = new PrintLista();
					List<Object[]> lis = new ArrayList<Object[]>();
					for (int i = 0; i < table.getRowCount(); i++) {
						Object[] linha = new Object[3];
						int modelRow = table.convertColumnIndexToModel(i);
						linha[0] = tableModel.getValueAt(modelRow, 1);
						linha[1] = tableModel.getValueAt(modelRow, 2);
						linha[2] = tableModel.getValueAt(modelRow, 3);
						lis.add(linha);
					}
					pp.passArrayList(LocalTime.now(), lis);
					pr.choosePrintType(table, pp);
				}
			});

		}

		public void refreshTable() {
			tableModel.removeAllRows();
			setSize(600, 470);
			try {
				DBOperations.appendAnyTable(MainVenda.con,
						"SELECT ID,CODBARRA, DESCRICAO,OBSERVA, DATA FROM LISTACOMPRA", tableModel);
			} catch (SQLException e) {
				e.printStackTrace();
				Alerts.showError("Falha ao atualizar lista de compras", "Erro");
			}
			TableColumnModel m = table.getColumnModel();
			m.getColumn(0).setMinWidth(0);
			m.getColumn(0).setMaxWidth(0);
			m.getColumn(1).setMinWidth(80);
			m.getColumn(2).setPreferredWidth(3000);
			m.getColumn(3).setMinWidth(150);
			m.getColumn(4).setMinWidth(80);
			m.getColumn(4).setCellRenderer(TableRendererDate.getDateTimeRenderer());
			m.getColumn(3).setCellRenderer(new TableRender());
			redrawRowsHight(table, tableModel, 2);
		}
	}

	public class TableRender implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JTextArea area = new JTextArea();

			area.setFont(textoFonte);
			if (isSelected) {
				area.setBackground(table.getSelectionBackground());
			}
			if (value != null) {
				area.setText((String) value);
				area.setLineWrap(true);
				return area;
			}
			area.setText("");
			return area;
		}

	}

	/**
	 * Resdesenha o tamanho de todas as linhas de acordo com o tamanho da mensagem
	 */
	public void redrawRowsHight(JTable table, DefaultModels model, int column) {

		for (int row = 0; row < model.getRowCount(); row++) {
			Object desc = model.getValueAt(row, column);
			if (desc != null) {
				int rowHeight = getWrapLine((String) desc, table.getColumnModel().getColumn(column).getWidth());
				if (rowHeight == 1) {
					table.setRowHeight(row, rowHeight * 17);
				} else {
					table.setRowHeight(row, rowHeight * 13);
				}
				table.repaint();
			}
		}
	}

	/**
	 * Obtem a quantidade de linhas necessarias para um JTextArea conter a mensagem
	 */
	private int getWrapLine(String s, int width) {
		int noLines = 1;
		try {
			JTextArea textArea = new JTextArea(s);
			textArea.setFont(textoFonte);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setSize(width, this.getSize().height);
			AttributedString text = new AttributedString(textArea.getText());
			FontRenderContext frc = textArea.getFontMetrics(textArea.getFont()).getFontRenderContext();
			AttributedCharacterIterator charIt = text.getIterator();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
			Insets textAreaInsets = textArea.getInsets();
			float formatWidth = textArea.getWidth() - textAreaInsets.left - textAreaInsets.right;
			lineMeasurer.setPosition(charIt.getBeginIndex());

			noLines = 0;
			while (lineMeasurer.getPosition() < charIt.getEndIndex()) {
				lineMeasurer.nextLayout(formatWidth);
				noLines++;
			}
		} catch (Exception e) {
		}

		return noLines;
	}

	public boolean apagarTudo(String query) {
		try {
			PreparedStatement ps = MainVenda.con.prepareStatement(query);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha ao apagar", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

}
