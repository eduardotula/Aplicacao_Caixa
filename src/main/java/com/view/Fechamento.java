package com.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import com.control.PrintRelatorios;
import com.model.CustomSQL;
import com.model.DBOperations;
import com.model.PrintRelatoriosFechaFormat;
import com.model.PrintRelatoriosProds;
import com.model.Recarga;

import net.miginfocom.swing.MigLayout;

public class Fechamento extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PrintRelatorios printRelatorios = new PrintRelatorios();
	// Visuais
	private JButton btnConfirmar = new JButton("Confirmar");
	private JButton btnCancelar = new JButton("Cancelar");
	private JLabel lblDinheiro = new JLabel("Retirada em Dinheiro");
	private JLabel lblCart = new JLabel("Retirada em Cart\u00E3o");
	private MaskFormatter mascaraValor;
	private JTextField txtTroco = new JTextField();
	private JLabel lblTroco = new JLabel("Troco em Caixa");
	private JTextField txtPix;

	public Fechamento(Connection con) {
		super("Fechar Caixa");

		setLocationRelativeTo(null);
		setResizable(false);
		JFormattedTextField txtDinheiro = new JFormattedTextField(mascaraValor);
		JFormattedTextField txtCart = new JFormattedTextField(mascaraValor);
		txtDinheiro.setColumns(10);
		txtCart.setColumns(10);
		txtTroco.setColumns(10);
		txtCart.setText("0.0");
		txtDinheiro.setText("0.0");
		txtTroco.setText("0.0");

		btnConfirmar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double dinhe = Double.parseDouble(txtDinheiro.getText().replace(",", "."));
					double cart = Double.parseDouble(txtCart.getText().replace(",", "."));
					double troco = Double.parseDouble(txtTroco.getText().replace(",", "."));
					double pix = Double.parseDouble(txtPix.getText().replace(",", "."));
					String func = DBOperations.selectSql1Dimen(con,
							"SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?", new String[0],
							MainVenda.IdCaixa)[0];
					Time time = java.sql.Time.valueOf(LocalTime.now());
					DBOperations.DmlSql(con, "INSERT INTO OPERACOES_CAIXA VALUES (NULL,?,?,?,?,?,?);", "Caixa Fechado",
							troco, cart, dinhe, MainVenda.IdCaixa, LocalTime.now());
					
					ArrayList<PrintRelatoriosProds> prodTabela = new ArrayList<PrintRelatoriosProds>();
					ArrayList<Recarga> recargas = new ArrayList<Recarga>();
					ResultSet rs = DBOperations.selectSqlRs(con,
							"SELECT RECARGA,NUMERO,VALOR FROM RECARGAS WHERE CONTROLECAIXA_IDCAIXA = ?", MainVenda.IdCaixa);
					while (rs.next()) {
						Recarga recarg = new Recarga();
						recarg.setOperadora(rs.getString("RECARGA"));
						recarg.setNumero(rs.getString("NUMERO"));
						recarg.setValor(rs.getDouble("VALOR"));
						recargas.add(recarg);
					}
					CustomSQL.operacaoFecharCaixa(con,prodTabela,MainVenda.IdCaixa);
					printer(prodTabela, recargas, time, dinhe, troco, cart, pix, func);
					MainVenda.valorCaixaAberto = 0;
					MainVenda.IdCaixa = -1;
					dispose();
				} catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Dados Invãlidos");
				}
			}
		});

		txtPix = new JTextField();
		txtPix.setText("0.0");
		txtPix.setColumns(10);

		JLabel lblRetiradaEmPix = new JLabel("Retirada em Pix");
		getContentPane().setLayout(new MigLayout("", "[49.00px][-32.00px][74px]", "[20px][18.00px][20px][20px][23px]"));
		getContentPane().add(lblDinheiro, "cell 0 0,alignx left,aligny center");
		getContentPane().add(lblTroco, "cell 0 3,alignx left,aligny center");
		getContentPane().add(lblRetiradaEmPix, "cell 0 2,growx,aligny center");
		getContentPane().add(lblCart, "cell 0 1,alignx left,aligny center");
		getContentPane().add(txtTroco, "cell 2 3,growx,aligny top");
		getContentPane().add(txtCart, "cell 2 1,growx,aligny top");
		getContentPane().add(txtDinheiro, "cell 2 0,growx,aligny top");
		getContentPane().add(txtPix, "cell 2 2,growx,aligny top");
		getContentPane().add(btnConfirmar, "cell 0 4,alignx left,aligny top");
		btnCancelar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(btnCancelar, "cell 2 4,alignx left,aligny top");
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(225, 168);
	}

	private void printer(ArrayList<PrintRelatoriosProds> setterDb, ArrayList<Recarga> recargas, Time time, double dinhe,
			double troco, double cart, double pix, String func) {
		PrintRelatoriosFechaFormat bf = new PrintRelatoriosFechaFormat();
		bf.passArrayList(setterDb, time, func, troco, dinhe, cart, pix, recargas);
		printRelatorios.printer(bf, PrintRelatorios.getImpressora());

	}
}
