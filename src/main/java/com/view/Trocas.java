package com.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import com.model.DBOperations;
import com.model.DbGetter;
import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;

import net.miginfocom.swing.MigLayout;

public class Trocas extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con;
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// Visuais
	private JTextField txtCodBarra = new JTextField();
	private JTextField txtDescricao = new JTextField();
	private JTextField txtValorCompra = new JTextField("0.00");
	private JFormattedTextField txtData;
	private JTextField txtChave = new JTextField();
	private JRadioButton rdnDefeito = new JRadioButton("Defeito");
	private JLabel lblProd = new JLabel("Descri\u00E7\u00E3o");
	private JPanel panelSaida = new JPanel();
	private JLabel lblCodBarra = new JLabel("Codigo de Barras");
	private JLabel lblDataCompra = new JLabel("Data da Compra");
	private JLabel lblValorCompra = new JLabel("Valor do Produto");
	private JLabel lblDescricao = new JLabel("Descri\u00E7\u00E3o");
	private JScrollPane scrollPane = new JScrollPane();
	private JTextArea txtAreaDescricao = new JTextArea();
	private JLabel lblChave = new JLabel("ID");
	private JButton btnBuscar = new JButton("Buscar");
	private JRadioButton rdnDinheiro = new JRadioButton("Dinheiro de Volta");
	private JButton btnFinalizar = new JButton("Finalizar");
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	public Trocas(Connection con) {
		super("Trocar");
		this.con = con;
		createAndShowGUI();
		setListeners();
	}

	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[327px][4px][91px,grow]", "[367px][23px]"));
		getContentPane().add(tabbedPane, "cell 0 0 3 1,grow");
		tabbedPane.addTab("Troca", null, panelSaida, null);
		setFormattedText();
		panelSaida.setLayout(new MigLayout("", "[227.00][grow]", "[][][][][][][][][grow]"));
		panelSaida.add(lblCodBarra, "cell 0 0");
		panelSaida.add(txtCodBarra, "flowx,cell 0 1 2 1,alignx left");
		txtCodBarra.setColumns(10);
		panelSaida.add(lblProd, "cell 0 2");
		panelSaida.add(txtDescricao, "cell 0 3 2 1,alignx left");
		txtDescricao.setColumns(20);
		panelSaida.add(lblDataCompra, "cell 0 4");
		panelSaida.add(lblValorCompra, "cell 1 4");
		panelSaida.add(txtData, "flowx,cell 0 5,alignx left");
		txtData.setColumns(10);
		panelSaida.add(txtValorCompra, "cell 1 5,alignx left");
		txtValorCompra.setColumns(7);
		panelSaida.add(rdnDefeito, "flowx,cell 0 6");
		panelSaida.add(lblDescricao, "cell 0 7");
		panelSaida.add(scrollPane, "cell 0 8 2 1,grow");
		scrollPane.setViewportView(txtAreaDescricao);
		panelSaida.add(lblChave, "cell 1 1,alignx right");
		txtChave.setEditable(false);
		panelSaida.add(txtChave, "cell 1 1,alignx right");
		txtChave.setColumns(10);
		panelSaida.add(btnBuscar, "cell 0 1 2 1");
		panelSaida.add(rdnDinheiro, "cell 0 6");
		getContentPane().add(btnFinalizar, "cell 2 1,alignx right");
		btnFinalizar.setEnabled(true);
		txtAreaDescricao.setLineWrap(true);
		setSize(400, 438);
		setLocationRelativeTo(null);
		setVisible(false);

	}

	public void setFormattedText() {
		try {
			MaskFormatter mask = new MaskFormatter("##/##/####");
			mask.setPlaceholderCharacter('_');
			txtData = new JFormattedTextField(mask);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setListeners() {

		btnFinalizar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DBOperations.startTransaction(con);
					Integer id = Integer.parseInt(txtChave.getText());
					Double valor = Double.parseDouble(txtValorCompra.getText());
					String data = txtData.getText();
					String desc = txtAreaDescricao.getText();
					LocalDate dataAgora = LocalDate.now();
					LocalTime horaAgora = LocalTime.now();
					if (rdnDefeito.isSelected()) {
						trocaComDefeito(id, valor, data, desc, dataAgora, horaAgora);
						if (rdnDinheiro.isSelected()) {
							atualizarVenda(id, valor, data, desc, dataAgora, horaAgora, "Troca dinheiro Devolvido");
						} else {
							atualizarVenda(id, valor, data, desc, dataAgora, horaAgora, "Troca");
						}

					} else if (!rdnDefeito.isSelected()) {
						trocaSemDefeito(id, valor, data, desc, dataAgora, horaAgora);
						if (rdnDinheiro.isSelected()) {
							atualizarVenda(id, valor, data, desc, dataAgora, horaAgora, "Troca dinheiro Devolvido");
						} else {
							atualizarVenda(id, valor, data, desc, dataAgora, horaAgora, "Troca");
						}
					}
					DBOperations.commit(con);
					dispose();
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Valores inv�lidos","Erro",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
					DBOperations.rollBack(con);
				} catch (ClassCastException e1) {
					JOptionPane.showMessageDialog(null, "Valores inv�lidos","Erro",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
					DBOperations.rollBack(con);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "Falha ao salvar","Erro",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
					DBOperations.rollBack(con);
				}
			}
		});
		btnBuscar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buscarProdEstoque();
			}
		});
	}

	private void buscarProdEstoque() {
		JFrame frame = new JFrame();
		JTable tabelaEstoque = new JTable();
		JTextField txtBusca = new JTextField();
		JScrollPane scrollPane = new JScrollPane();
		JButton btnConfirmar = new JButton("Confirmar");
		DefaultModels modelEstoque = new DefaultModels(
				new String[] { "ID", "CodBarra", "Descri��o", "Quantidade", "Valor Venda" },
				new boolean[] { false, false, false, false, false },
				new Class<?>[] { Integer.class, String.class, String.class, Integer.class, Double.class });
		TableRowSorter<DefaultModels> sorter = new TableRowSorter<DefaultModels>(modelEstoque);
		try {
			DBOperations.appendAnyTable(con,
					"SELECT IDPROD, CODBARRA, DESCRICAO, VLR_ULT_VENDA, QUANTIDADE, ITEN_ATIVO FROM PRODUTOS WHERE ITEN_ATIVO = 1;",
					modelEstoque);
		} catch (ClassCastException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		tabelaEstoque.setModel(modelEstoque);
		tabelaEstoque.getColumnModel().getColumn(2).setPreferredWidth(400);
		tabelaEstoque.getColumnModel().getColumn(0).setPreferredWidth(60);
		tabelaEstoque.getColumnModel().getColumn(4).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));
		frame.getContentPane().add(txtBusca, "flowx,cell 0 0,growx");
		tabelaEstoque.setRowSorter(sorter);
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
				String id = modelEstoque.getValueAtStr(modelRow, 0);
				String codBarra = modelEstoque.getValueAtStr(modelRow, 1);
				String descricao = modelEstoque.getValueAtStr(modelRow, 2);
				txtChave.setText(id);
				txtCodBarra.setText(codBarra);
				txtDescricao.setText(descricao);
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
			public void removeUpdate(DocumentEvent arg0) {
				String text = txtBusca.getText();
				if (txtBusca.getText().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text))); // Ordena rows com a flag
																								// de
					tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				String text = txtBusca.getText();
				if (txtBusca.getText().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text))); // Ordena rows com a flag
																								// de
					tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				String text = txtBusca.getText();
				if (txtBusca.getText().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text))); // Ordena rows com a flag
																								// de
					tabelaEstoque.getSelectionModel().setSelectionInterval(0, 0);
				}
			}
		});
	}

	/**
	 * Troca com defeito.
	 *
	 * @param id the id
	 * @param valor the valor
	 * @param data the data
	 * @param desc the desc
	 * @param dataAgora the data agora
	 * @param horaAgora the hora agora
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException the SQL exception
	 */
	private void trocaComDefeito(Integer id, Double valor, String data, String desc, LocalDate dataAgora,
			LocalTime horaAgora) throws ClassCastException, SQLException {
		DBOperations.DmlSql(con, "INSERT INTO TROCAS VALUES (NULL, ?, ?, ?, ?, ?, ?)", valor, id,
				LocalDate.parse(data, format), dataAgora, horaAgora, desc);

	}

	/**
	 * Troca sem defeito.
	 *
	 * @param id the id
	 * @param valor the valor
	 * @param data the data
	 * @param desc the desc
	 * @param dataAgora the data agora
	 * @param horaAgora the hora agora
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException the SQL exception
	 */
	private void trocaSemDefeito(Integer id, Double valor, String data, String desc, LocalDate dataAgora,
			LocalTime horaAgora) throws  SQLException {
		String funcio = DBOperations.selectSql1Dimen(con, "SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?", new String[0], MainVenda.IdCaixa)[0];
		DBOperations.DmlSql(con, "INSERT INTO ENTRADAS VALUES (NULL, ?,?, ?, ?, ?, ?,?,?)", 1, 0.0, 0.0, id, dataAgora,
				horaAgora, funcio, 1);
		DBOperations.DmlSql(con, "UPDATE PRODUTOS SET QUANTIDADE = QUANTIDADE + ? WHERE IDPROD = ?", 1, id);
		DBOperations.DmlSql(con, "INSERT INTO TROCAS VALUES (NULL, ?, ?, ?, ?, ?, ?)", valor, id,
				LocalDate.parse(data, format), dataAgora, horaAgora, desc);
	}

	private void atualizarVenda(Integer id, Double valor, String data, String desc, LocalDate dataAgora,
			LocalTime horaAgora, String opera) throws SQLException {
		DbGetter prod = new DbGetter(id, null, opera, 1, -valor, -valor, -valor,0.0, 0, "T");
		DBOperations.DmlSql(con, "INSERT INTO VENDAS VALUES(NULL, ?, ?, ?, ?, ?, ?, ?,?,?);",
				prod.getCodBarra(), prod.getValorUni(), prod.getValorDinheiro(), prod.getValorCartao(),
				prod.getValorTotal(), prod.getTipoPagamento(), LocalTime.now(), prod.getIdEstoque(),
				MainVenda.IdCaixa);
	}
}
