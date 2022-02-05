package com.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.model.DBFrenteCaixa;
import com.model.DBOperations;

import net.miginfocom.swing.MigLayout;

public class AbrirCaixa extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DBFrenteCaixa dbFrente = new DBFrenteCaixa();
	//Visuais
	private JTextField txtTroco = new JTextField();
	private JComboBox<String> func = new JComboBox<String>();
	private JLabel lblTroco = new JLabel("Troco");
	private JLabel lblFuncio = new JLabel("Nome");
	private JButton btnAbrir = new JButton("Abrir Caixa");
	
	public AbrirCaixa(Connection con) {
		super("Abrir Caixa");
		setVisible(true);
		setSize(240,157);
		setLocationRelativeTo(null);
		
		btnAbrir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double valorT = Double.parseDouble(txtTroco.getText().replace(",", "."));
					String funcio = (String) func.getSelectedItem();
					if(!funcio.isEmpty() || !txtTroco.getText().isEmpty()) {
						boolean v = dbFrente.OperacaoAbrirCaixa(con, valorT, funcio);
						if(v) {
							MainVenda.valorCaixaAberto = 1;
							MainVenda.IdCaixa = dbFrente.getIdCaixa(con);
							dispose();
						}
					}else {
						JOptionPane.showMessageDialog(null, "Campos invalidos");
					}
				}catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Campos invalidos");
				}
			}
		});
		getFuncionario(con);
		getContentPane().setLayout(new MigLayout("", "[][][grow]", "[][][][27.00][]"));
		getContentPane().add(lblTroco, "cell 0 0");
		txtTroco.setText("0.0");
		getContentPane().add(txtTroco, "cell 0 1,alignx left");
		txtTroco.setColumns(10);
		getContentPane().add(lblFuncio, "cell 0 2");
		getContentPane().add(func, "cell 0 3 2 1,growx,aligny center");
		getContentPane().add(btnAbrir, "cell 2 4,growx,aligny bottom");
	}

	private void getFuncionario(Connection con) {
		String[] funcio;
		try {
			funcio = (String[]) DBOperations.selectSql1Dimen(con, "SELECT NOME FROM FUNCIONARIOS",new String[0]);
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(funcio);
			model.addElement("");
			func.setModel(model);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Falha ao carregar lista de funcionarios","Erro",JOptionPane.ERROR_MESSAGE);
		}

	}
}
