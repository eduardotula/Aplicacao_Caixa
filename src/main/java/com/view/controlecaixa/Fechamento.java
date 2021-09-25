package com.view.controlecaixa;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.MaskFormatter;

import control.PrintRelatorios;
import model.DBFrenteCaixa;
import model.PrintRelatoriosProds;
import model.Recarga;
import model.PrintRelatoriosFechaFormat;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFormattedTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.JTextField;

public class Fechamento extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Connection con;
	private DBFrenteCaixa dbFrente = new DBFrenteCaixa();
	private PrintRelatorios printRelatorios = new PrintRelatorios();
	//Visuais
	private JButton btnConfirmar = new JButton("Confirmar");
	private JButton btnCancelar = new JButton("Cancelar");
	private JLabel lblDinheiro = new JLabel("Retirada em Dinheiro");
	private JLabel lblCart = new JLabel("Retirada em Cart\u00E3o");
	private MaskFormatter mascaraValor;
	private JTextField txtTroco = new JTextField();
	private JLabel lblTroco = new JLabel("Troco em Caixa");

	public Fechamento(Connection con) {
		super("Fechar Caixa");
		this.con = con;
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(240,169);
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
					double dinhe = Double.parseDouble(txtDinheiro.getText().replace(",","." ));
					double cart = Double.parseDouble(txtCart.getText().replace(",", "."));
					double troco = Double.parseDouble(txtTroco.getText().replace(",", "."));
					String func = dbFrente.getFuncioCaixaAtual(con);
					Time time = java.sql.Time.valueOf(LocalTime.now());
					int idCaixa = dbFrente.setValoresMovimento(con, time, dinhe, cart, troco);
					if(idCaixa != 0) {
						ArrayList<PrintRelatoriosProds> prodTabela = new ArrayList<PrintRelatoriosProds>();
						ArrayList<Recarga> recargas = dbFrente.getRecargas(con, idCaixa);
						double somaTot = dbFrente.convertTable(con, prodTabela, idCaixa);
						dbFrente.setCaixaAberto(con, 0);
						System.out.println(prodTabela.size() + somaTot +time.toString()+ dinhe+ troco+cart+ func);
						printer(prodTabela, recargas,somaTot,time, dinhe, troco,cart, func);
						MainVenda.valorCaixaAberto = 0;
						MainVenda.IdCaixa = null;
						dispose();
					}else{
						JOptionPane.showMessageDialog(null, "Falha em Fechar o Caixa: Erro IDCAIXA = 0");
					}
				}catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Dados Inv�lidos");
				}
			}
		});
		btnCancelar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblDinheiro)
								.addComponent(lblCart)
								.addComponent(lblTroco))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtTroco, 0, 0, Short.MAX_VALUE)
								.addComponent(txtCart, 0, 0, Short.MAX_VALUE)
								.addComponent(txtDinheiro, GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnConfirmar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancelar)))
					.addContainerGap(36, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDinheiro)
						.addComponent(txtDinheiro, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCart)
						.addComponent(txtCart, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTroco)
						.addComponent(txtTroco, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConfirmar)
						.addComponent(btnCancelar))
					.addContainerGap(54, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
		
	}
	private void printer(ArrayList<PrintRelatoriosProds> setterDb,ArrayList<Recarga> recargas,double soma, Time time, double dinhe, double troco, double cart, String func) {
	    PrintRelatoriosFechaFormat bf = new PrintRelatoriosFechaFormat();
	    bf.passArrayList(setterDb,soma, time, func, troco, dinhe, cart, recargas);
	    printRelatorios.printer(bf,dbFrente.getImpressora(con));

	}
}
