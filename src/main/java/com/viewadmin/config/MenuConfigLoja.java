package com.viewadmin.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class MenuConfigLoja extends JFrame{
	private static final long serialVersionUID = 1L;
	private Connection con;
	private JTextField txtRazao;
	private JTextField txtCnpj;
	private JTextField txtEnder;
	private JTextField txtCidade;
	private JTextField txtWhats;
	private JComboBox<String> comboImpre;
	private JButton btnCancelar = new JButton("Cancelar");
	private JButton btnSalvar = new JButton("Salvar");
	public MenuConfigLoja(Connection con) {
		super("Configuraï¿½áes da Loja");
		this.con = con;
		createAndShowGUI();
		setListeners();
		getPrinters();
		preencherDados();
	}
	
	public void createAndShowGUI() {

		getContentPane().setLayout(new MigLayout("", "[][grow][][]", "[][][][][][][]"));
		JLabel lblNomeLoja = new JLabel("Raz\u00E3o Social:");
		getContentPane().add(lblNomeLoja, "cell 0 0,alignx trailing");
		txtRazao = new JTextField();
		getContentPane().add(txtRazao, "cell 1 0,growx");
		txtRazao.setColumns(10);
		JLabel lblNewLabel_1 = new JLabel("CNPJ:");
		getContentPane().add(lblNewLabel_1, "cell 0 1,alignx trailing");
		txtCnpj = new JTextField();
		getContentPane().add(txtCnpj, "cell 1 1,alignx left");
		txtCnpj.setColumns(15);
		JLabel lblEndereco = new JLabel("Endere\u00E7o:");
		getContentPane().add(lblEndereco, "cell 0 2,alignx trailing");
		txtEnder = new JTextField();
		getContentPane().add(txtEnder, "cell 1 2,growx");
		txtEnder.setColumns(10);
		JLabel lblCidade = new JLabel("Cidade:");
		getContentPane().add(lblCidade, "cell 0 3,alignx trailing");
		txtCidade = new JTextField();
		getContentPane().add(txtCidade, "cell 1 3,growx");
		txtCidade.setColumns(10);
		JLabel lblwhats = new JLabel("WhatsApp:");
		getContentPane().add(lblwhats, "cell 0 4,alignx trailing");
		txtWhats = new JTextField();
		getContentPane().add(txtWhats, "cell 1 4,alignx left");
		txtWhats.setColumns(15);
		JLabel lblImpre = new JLabel("Impressora Padr\u00E3o:");
		getContentPane().add(lblImpre, "cell 0 5,alignx trailing");
		comboImpre = new JComboBox<String>();
		getContentPane().add(comboImpre, "cell 1 5,growx");
		getContentPane().add(btnCancelar, "flowx,cell 2 6,alignx right");
		getContentPane().add(btnSalvar, "cell 3 6,alignx right");
		setSize(547,270);
		setLocationRelativeTo(null);
		setVisible(false);
	}
	
	public void setListeners() {
		btnSalvar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				salvarDados();
			}
		});
		btnCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	public void preencherDados() {
		try {
			String query = "SELECT * FROM CADASTRO_LOJA;";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				txtRazao.setText(rs.getString("razao"));
				txtCnpj.setText(rs.getString("CNPJ"));
				txtEnder.setText(rs.getString("ENDERECO"));
				txtCidade.setText(rs.getString("CIDADE"));
				txtWhats.setText(rs.getString("NUMERO"	));
				comboImpre.setSelectedItem(rs.getString("IMPRESSORA"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void salvarDados() {
		try {
			String query = "UPDATE CADASTRO_LOJA SET RAZAO = ?, CNPJ = ?, ENDERECO = ?,"
					+ "CIDADE = ?, NUMERO = ?, IMPRESSORA = ? WHERE ID = 1";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, txtRazao.getText());
			ps.setString(2, txtCnpj.getText());
			ps.setString(3, txtEnder.getText());
			ps.setString(4, txtCidade.getText());
			ps.setString(5, txtWhats.getText());
			ps.setString(6, (String) comboImpre.getSelectedItem());
			ps.executeUpdate();
			JOptionPane.showMessageDialog(null, "Salvo com sucesso");
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha em salvar dados","Falha",JOptionPane.ERROR_MESSAGE);
		}
	}
	private void getPrinters() {
	    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
	    DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>();
    	comboModel.addElement("");
	    for(PrintService sv : printServices) {
	    	comboModel.addElement(sv.getName());
	    }
	    comboImpre.setModel(comboModel);
	}

}
