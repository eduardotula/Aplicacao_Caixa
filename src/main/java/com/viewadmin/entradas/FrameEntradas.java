package com.viewadmin.entradas;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.model.DBOperations;
import com.model.DefaultModels;
import com.tablerenders_editor.TableRendererCurrency;
import com.tablerenders_editor.TableRendererDate;
import com.viewadmin.FrameFiltroData;
import com.viewadmin.FrameMenuAdmin;

import net.miginfocom.swing.MigLayout;

public class FrameEntradas extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultModels model = new DefaultModels(
			new String[] { "Razão Social", "V.Total", "Data", "Hora", "Funcionario" },
			new boolean[] { false, false, false, false, false },
			new Class<?>[] { String.class, Double.class, LocalDate.class, LocalTime.class, String.class });

	private JButton mntmLimpar = new JButton("Limpar Filtros");
	private MenuItemEntrada menuItemEntrada;
	private JButton mntmExportrarCSV = new JButton("Exportar CSV");
	private JButton mntmFiltrarporData = new JButton("Filtrar Data");
	private final JComboBox<String> comboFunciona = new JComboBox<String>();
	private final JLabel lblBuscar = new JLabel("Buscar");
	private DefaultComboBoxModel<String> comboModel;
	private TableRowSorter<DefaultModels> sorter;
	private final JLabel lblFuncionario = new JLabel("Funcionario");
	private final JPanel panel = new JPanel();
	private final JTextField txtBusca = new JTextField();
	private final JButton btnFornecedores = new JButton("Fornecedores");
	private FrameFornecedores forne;

	public FrameEntradas() {
		super("Entradas");
		txtBusca.setColumns(10);
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 15));

		createAndShowGUI();
		setList();
		refreshTable();
	}

	private void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[495px,grow]", "[-38.00][40.00][339px,grow]"));
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		getContentPane().add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[44px][86px,grow]", "[22px]"));
		panel.add(lblBuscar, "cell 0 0,alignx left,aligny center");
		lblBuscar.setFont(new Font("Tahoma", Font.PLAIN, 15));

		panel.add(txtBusca, "flowx,cell 1 0,growx,aligny center");
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 2,grow");
		table = new JTable();
		scrollPane.setViewportView(table);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menuBar.add(mntmFiltrarporData);

		menuBar.add(mntmExportrarCSV);

		menuBar.add(btnFornecedores);

		panel.add(lblFuncionario, "cell 1 0");
		lblFuncionario.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel.add(comboFunciona, "cell 1 0");
		menuBar.add(mntmLimpar);
		sorter = new TableRowSorter<DefaultModels>(model);
		table.setModel(model);
		table.setRowSorter(sorter);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(511, 400));
		setLocationRelativeTo(null);
		setVisible(false);

	}

	private void setList() {
		mntmLimpar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshTable();
				comboFunciona.setSelectedIndex(0);
				txtBusca.setText("");
			}
		});
		mntmExportrarCSV.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.exportarCSV();
			}
		});
		mntmFiltrarporData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FrameFiltroData filtro = new FrameFiltroData();
				filtro.startGUIFiltroEsto();
				LocalDate[] datas = filtro.getData();
				refreshTablebyDate(datas[0], datas[1]);
			}
		});
		// txtBusca
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

		comboFunciona.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String funcio = (String) comboFunciona.getSelectedItem();
				if (comboFunciona.getSelectedIndex() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter(funcio));
				}

			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					if (arg0.getClickCount() == 2) {
						System.out.println(getWidth());
						JTable t = (JTable) arg0.getSource();
						int row = t.getSelectedRow();
						int modelRow = t.convertRowIndexToModel(row);
						LocalDate data = (LocalDate) model.getValueAt(modelRow, 2);
						LocalTime time = (LocalTime) model.getValueAt(modelRow, 3);
						String funcionario = model.getValueAtStr(modelRow, 4);
						String query = String.format(
								"SELECT E.ID, P.CODBARRA,P.DESCRICAO,E.QUANTIDADE,E.VALOR_CUSTO,E.VALOR_VENDA,E.OPERADOR FROM ENTRADAS E "
										+ "INNER JOIN PRODUTOS P ON E.IDPROD = P.IDPROD "
										+ "WHERE E.DATAENTRADA = '%s' AND E.HORAENTRADA = '%s'",
								Date.valueOf(data), Time.valueOf(time));
						if (menuItemEntrada != null) {
							menuItemEntrada.dispose();
						}
						System.out.println(query);
						menuItemEntrada = new MenuItemEntrada(query, data.toString(), time.toString(), funcionario);
						menuItemEntrada.setSize(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
								FrameMenuAdmin.sizeOffset);
						menuItemEntrada.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 17,
								FrameMenuAdmin.btnOffset);
						System.out.println(menuItemEntrada.getWidth());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnFornecedores.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (forne != null) {
					forne.dispose();
					forne = null;
				}
				forne = new FrameFornecedores();

			}
		});
	}

	public void refreshTable() {
		model.removeAllRows();
		comboModel = new DefaultComboBoxModel<String>();
		comboModel.addElement("");
		String[] f;
		try {
			f = DBOperations.selectSql1Dimen(FrameMenuAdmin.con, "SELECT NOME FROM FUNCIONARIOS", new String[0]);
			for (String funcio : f) {
				comboModel.addElement(funcio);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		comboFunciona.setModel(comboModel);
		setRenders();
		comboFunciona.setSelectedIndex(0);
	}

	private void refreshTablebyDate(LocalDate inicial, LocalDate dataFin) {
		model.removeAllRows();
		try {
			DBOperations.appendAnyTable(FrameMenuAdmin.con,
					"SELECT F.NOME, SUM(E.VALOR_CUSTO) AS SOMA, E.DATAENTRADA, E.HORAENTRADA, OPERADOR FROM ENTRADAS E "
							+ "INNER JOIN FORNECEDORES F ON F.ID = E.ID_FORNECEDOR "
							+ "WHERE E.DATAENTRADA BETWEEN ? AND ? "
							+ "GROUP BY F.NOME, E.DATAENTRADA, E.HORAENTRADA, OPERADOR;",
					model, inicial, dataFin);
			comboModel = new DefaultComboBoxModel<String>();
			comboModel.addElement("");

			String[] f;
			f = DBOperations.selectSql1Dimen(FrameMenuAdmin.con, "SELECT NOME FROM FUNCIONARIOS", new String[0]);
			for (String funcio : f) {
				comboModel.addElement(funcio);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		comboFunciona.setModel(comboModel);
		setRenders();
		comboFunciona.setSelectedIndex(0);
	}

	private void setRenders() {
		TableColumnModel m = table.getColumnModel();
		m.getColumn(1).setCellRenderer(TableRendererCurrency.getCurrencyRenderer());
		m.getColumn(2).setCellRenderer(TableRendererDate.getDateTimeRenderer());
	}

	private void codeSearch(String busca) {
		if (busca.length() == 0) {
			sorter.setRowFilter(null);
		} else {
			try {
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca))); // Ordena rows com a flag de
																							// Case-insensitivity
				table.getSelectionModel().setSelectionInterval(0, 0);
			} catch (PatternSyntaxException e) {
				e.printStackTrace();
			}
		}
	}

}
