package com.viewadmin.estoque;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.model.DefaultModels;
import com.viewadmin.FrameMenuAdmin;

import net.miginfocom.swing.MigLayout;

public class FrameSelecionarFornecedor{

	private JTable table;
	private JTextField txtBusca;
	private DefaultModels model = new DefaultModels(new String[]{"Id","Razão Social","CNPJ"},
			new boolean[] {false,false,false,false},
			new Class<?>[] {Integer.class,String.class,String.class});
	private TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
	private JPanel panel = new JPanel();
	
	public FrameSelecionarFornecedor() {
		super();
		createAndShowGUI();
		setList();
		refreshTable();
		JOptionPane.showConfirmDialog(null, panel, "Fornecedor", JOptionPane.PLAIN_MESSAGE);

	}
	
	private void createAndShowGUI() {
		panel.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		JLabel lblBuscar = new JLabel("Buscar");
		panel.add(lblBuscar, "flowx,cell 0 0");
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, "cell 0 1,grow");
		table = new JTable();
		scrollPane.setViewportView(table);
		txtBusca = new JTextField();
		panel.add(txtBusca, "cell 0 0,growx");
		txtBusca.setColumns(10);
		table.setRowSorter(sorter);
		table.setModel(model);
		panel.setVisible(true);
	}
	
	private void setList() {
		txtBusca.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				codeSearch(txtBusca.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				codeSearch(txtBusca.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				codeSearch(txtBusca.getText());
			}
		});
	}
	
	private void refreshTable() {
		model.removeAllRows();
		try {
			PreparedStatement ps = FrameMenuAdmin.con.prepareStatement("SELECT ID,NOME,CNPJ FROM FORNECEDORES");
			ResultSet rs = ps.executeQuery();
			Object[] row = new Object[3];
			while(rs.next()) {
				row[0] = rs.getInt("ID");
				row[1] = rs.getString("NOME");
				row[2] = rs.getString("CNPJ");
				model.addRow(row);
			}
			
			System.out.println(row[1]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public Integer getId()throws Exception {
		int row = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(row);
		return model.getValueAtInt(modelRow, 0);
	}
	public String getNome() throws Exception{
		int row = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(row);
		return model.getValueAtStr(modelRow, 1);
	}
	
	private void codeSearch(String busca) {
		if(busca.length() == 0) {
			sorter.setRowFilter(null);
		}else {
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(busca))); //Ordena rows com a flag de Case-insensitivity
			table.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
}
