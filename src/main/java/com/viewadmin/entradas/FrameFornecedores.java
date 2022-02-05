package com.viewadmin.entradas;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.control.TableOperations;
import com.model.DBOperations;
import com.model.DefaultModels;
import com.viewadmin.FrameMenuAdmin;

import net.miginfocom.swing.MigLayout;

public class FrameFornecedores extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DefaultModels model = new DefaultModels(new String[] { "ID", "Razão", "CNPJ" },
			new boolean[] { false, false, false }, new Class<?>[] { Integer.class, String.class, String.class });
	private JTable tableFornece;
	private DBOperations dbVendas = new DBOperations();
	private JButton btnRemover = new JButton("Remover");
	private JButton btnAdicionar = new JButton("Adicionar");

	public FrameFornecedores() {
		super("Fornecedores");

		createAndShowGUI();
		setList();
		setVisible(true);
		setSize(525, 400);
		refreshTable();
		setLocationRelativeTo(null);
	}

	public void createAndShowGUI() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 1,grow");

		tableFornece = new JTable();
		scrollPane.setViewportView(tableFornece);
		getContentPane().add(btnAdicionar, "flowx,cell 0 0");
		getContentPane().add(btnRemover, "cell 0 0");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		tableFornece.setModel(model);

	}

	public void setList() {

		btnRemover.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] rows = tableFornece.getSelectedRows();
				if (rows.length > 0) {
					for (int row : rows) {
						int modelRow = tableFornece.convertRowIndexToModel(row);
						TableOperations to = new TableOperations();
						to.ApagarSelecioTabela(FrameMenuAdmin.con, model, tableFornece, "DELETE FROM FORNECEDORES WHERE ID = ?");
						System.out.println(modelRow);
					}
					refreshTable();
				} else {
					JOptionPane.showMessageDialog(null, "Nenhuma linha selecionada");
				}
			}
		});
		btnAdicionar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					JPanel pane = new JPanel();
					pane.setLayout(new MigLayout("", "[78.00][120]", "[][][][]"));
					pane.add(new JLabel("Razão Social"), "cell 0 0");
					JTextField raza = new JTextField();
					pane.add(raza, "cell 1 0,growx");
					pane.add(new JLabel("CNPJ"), "cell 0 1");
					JTextField cnp = new JTextField();
					pane.add(cnp, "cell 1 1,growx");
					JOptionPane.showConfirmDialog(new JFrame(), pane, "Cadastro", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					String razao = raza.getText();
					String cnpj = cnp.getText();
					if (razao != null && !razao.isEmpty() && cnpj != null && !cnpj.isEmpty()) {
						DBOperations.DmlSql(FrameMenuAdmin.con, "INSERT INTO FORNECEDORES VALUES(NULL,?,?)", razao, cnpj);
						
					} else {
						JOptionPane.showMessageDialog(null, "Campos não podem estar vazios");
					}
					refreshTable();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 

			}
		});
	}

	private void refreshTable() {
		model.removeAllRows();
		try {
			DBOperations.appendAnyTable(FrameMenuAdmin.con, "SELECT ID,NOME,CNPJ FROM FORNECEDORES", model);
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
