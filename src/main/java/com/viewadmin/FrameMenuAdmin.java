package com.viewadmin;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JPanel;

import com.source.InterfaceStarter;
import com.viewadmin.config.MenuConfigLoja;
import com.viewadmin.controlecaixa.MenuControleCaixa;
import com.viewadmin.entradas.FrameEntradas;
import com.viewadmin.estoque.MenuEstoque;
import com.viewadmin.funcionarios.MenuFuncionarios;
import com.viewadmin.relatorios.MenuRelatorios;
import com.viewadmin.trocas.MenuTrocasDevolu;
import com.viewadmin.vendasapagadas.MenuVendasApaga;

import control.Conexao;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

import java.awt.Font;
import java.awt.Point;


public class FrameMenuAdmin extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Conexao cone = new Conexao();
	public static int btnOffset;
	public static int sizeOffset;
	public static Connection con;
	private FrameEntradas entrada;
	private MenuEstoque estoque;
	private MenuRelatorios rela;
	private MenuTrocasDevolu trocas;
	private MenuVendasApaga apaga;
	private MenuFuncionarios funcio;
	private MenuControleCaixa caixa;
	private MenuConfigLoja config;
	//Objetos visuais
	JButton btnRelato = new JButton("");
	JButton btnEstoque = new JButton("");
	JPanel topPanel = new JPanel();
	private final JButton btnApaga = new JButton("");
	private final JButton btnTrocas = new JButton("");
	JLabel lblNewLabel = new JLabel("");
	private final JButton btnSair = new JButton("Sair");
	private final JButton btnEntrada = new JButton();
	private final JButton btnFuncion = new JButton();
	private final JLabel lblEstoque = new JLabel("Estoque");
	private final JLabel lblNewLabel_2 = new JLabel("Vendas");
	private final JLabel lblEntradas = new JLabel("Entradas");
	private final JLabel lblNewLabel_2_2 = new JLabel("Devolu\u00E7\u00F5es");
	private final JLabel lblNewLabel_2_3 = new JLabel("Vendas Apagadas");
	private final JLabel lblNewLabel_2_4 = new JLabel("Funcionarios");
	private final JButton btnControleCaixa = new JButton("");
	private final JLabel lblNewLabel_1 = new JLabel("Controle Caixa");
	private final JButton btnConfigLoja = new JButton("");
	private final JLabel lblConfigLoja = new JLabel("Config Loja");
	private final JButton btnQuery = new JButton("Custom Query");

	public FrameMenuAdmin(HashMap<String, String> config2) {
		super("Menu Administrador");
		setExtendedState(MAXIMIZED_BOTH);
		setMinimumSize(new Dimension(800,800));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		con = cone.getCone(config2);
		InterfaceStarter.setMainVisi(false);
		addWindowListener(new WindowAdapter() {
			 public void windowClosing(WindowEvent e) {
				 if(entrada != null) {
					 entrada.dispose();
				 }
				if(estoque != null) {
					estoque.dispose();
				}
				if(rela != null) {
					rela.dispose();
				}
				if(trocas != null) {
					trocas.dispose();
				}
				if(apaga != null) {
					apaga.dispose();
				}
				if(funcio != null) {
					funcio.dispose();
				}
				if(caixa != null) {
					caixa.dispose();
				}
				if(config != null) {
					config.dispose();
				}
			 }
		});
		btnConfigLoja.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(config == null) {
					config = new MenuConfigLoja(con);
					config.setVisible(true);
				}else {
					config.setVisible(true);
					config.toFront();
					config.requestFocus();
				}
			}
		});
		btnQuery.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new FrameCustomSQL();
			}
		});
		btnSair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fechar();
				dispose();
				InterfaceStarter.setMainVisi(true);
				if(con == null) {
					try {
						con.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		btnEntrada.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(entrada == null) {
					entrada = new FrameEntradas();
					entrada.setLocation(0,btnOffset);
					entrada.setSize(Toolkit.getDefaultToolkit().getScreenSize().width/2,sizeOffset);
					entrada.setVisible(true);
				}else {
					entrada.refreshTable();
					entrada.setVisible(true);
					entrada.toFront();
					entrada.requestFocus();
				}
			}
		});
		btnEstoque.setBorderPainted(false);
		btnEstoque.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(estoque == null) {
					estoque = new MenuEstoque(con);
					estoque.setLocation(new Point(0, getLocation().y+btnOffset));
					estoque.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,sizeOffset);
					estoque.setVisible(true);
				}else {
					String simpleQuery = "SELECT IDPROD, CODBARRA, DESCRICAO, QUANTIDADE, VLR_ULT_VENDA,PRECO_CUSTO, ITEN_ATIVO FROM PRODUTOS WHERE ITEN_ATIVO = 1";
					estoque.refreshFrame(con, simpleQuery);
					estoque.setVisible(true);
					estoque.toFront();
					estoque.requestFocus();
				}
			}
		});
		
		btnRelato.setBorderPainted(false);
		
		btnRelato.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rela == null) {
					rela = new MenuRelatorios(con,null);
					rela.setLocation(new Point(0, getLocation().y+btnOffset));
					rela.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,sizeOffset);
					rela.setVisible(true);
				}else {
					rela.setModelNoGroup(con, rela.queryIni);
					rela.setVisible(true);
					rela.toFront();
					rela.requestFocus();
				}
			}
		});
		//icons
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/admin-with-cogwheels.png")));
		btnEstoque.setIcon(new ImageIcon(getClass().getResource("/boxes.png")));
		btnRelato.setIcon(new ImageIcon(getClass().getResource("/business-report.png")));
		btnConfigLoja.setBorder(null);
		btnConfigLoja.setIcon(new ImageIcon(getClass().getResource("/loja.png")));
		btnControleCaixa.setIcon(new ImageIcon(getClass().getResource("/cash.png")));
		
		//Adds
		getContentPane().setLayout(new MigLayout("", "[108.00][91.00][79.00][108.00][49.00][31.00][95.00][][][-571.00][622.00,grow][]", "[][45.00][grow][][-24.00][49.00]"));
		getContentPane().add(btnEstoque, "cell 0 0");
		getContentPane().add(btnRelato, "cell 1 0");
		btnEntrada.setBorderPainted(false);
		btnEntrada.setIcon(new ImageIcon(getClass().getResource("/entrada.png")));
		
		getContentPane().add(btnEntrada, "flowx,cell 2 0");
		btnTrocas.setBorderPainted(false);
		btnControleCaixa.setBorderPainted(false);
		btnControleCaixa.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(caixa == null) {
					caixa = new MenuControleCaixa(con);
					caixa.setLocation(new Point(0, getLocation().y+btnOffset));
					caixa.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,sizeOffset);
					caixa.setVisible(true);
				}else {
					caixa.refreshTable(con, "SELECT IDCAIXA, DATA, FUNCIONARIO FROM CONTROLECAIXA;");
					caixa.setVisible(true);
					caixa.toFront();
					caixa.requestFocus();
				}

			}
		});
		btnTrocas.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(trocas == null) {
					trocas = new MenuTrocasDevolu(con);
					trocas.setLocation(new Point(0, getLocation().y+btnOffset));
					trocas.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,sizeOffset);
					trocas.setVisible(true);
				}else {
					trocas.setModelNoGroup(con);
					trocas.setVisible(true);
					trocas.toFront();
					trocas.requestFocus();
				}
				}
		});
		btnTrocas.setIcon(new ImageIcon(getClass().getResource("/devolucao.png")));
		getContentPane().add(btnTrocas, "flowx,cell 3 0");
		btnFuncion.setBorderPainted(false);
		btnFuncion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(funcio == null) {
					funcio = new MenuFuncionarios(con);
					funcio.setLocation(new Point(0, getLocation().y+btnOffset));
					funcio.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,sizeOffset);
					funcio.setVisible(true);
				}else {
					funcio.refreshTable(con);
					funcio.setVisible(true);
					funcio.toFront();
					funcio.requestFocus();
				}
			}
		});
		btnFuncion.setIcon(new ImageIcon(getClass().getResource("/Funcionario.png")));
		
		getContentPane().add(btnFuncion, "cell 5 0");
		
		getContentPane().add(btnControleCaixa, "cell 6 0");
		
		getContentPane().add(btnConfigLoja, "flowx,cell 7 0,alignx center,aligny center");
		
		lblEstoque.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblEstoque, "cell 0 1,alignx center,aligny top");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblNewLabel_2, "cell 1 1,alignx center,aligny top");
		lblEntradas.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblEntradas, "cell 2 1,alignx center,aligny top");
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblNewLabel_2_2, "cell 3 1,alignx center,aligny top");
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		getContentPane().add(lblNewLabel_2_3, "cell 4 1,alignx center,aligny top");
		lblNewLabel_2_4.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblNewLabel_2_4, "cell 5 1,alignx center,aligny top");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblNewLabel_1, "cell 6 1,alignx center,aligny top");
		lblConfigLoja.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		getContentPane().add(lblConfigLoja, "cell 7 1,alignx center,aligny top");
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/florense.png")));
		getContentPane().add(lblNewLabel, "cell 0 2 12 1,alignx center");
		
		getContentPane().add(btnQuery, "cell 2 3");
		btnSair.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		getContentPane().add(btnSair, "cell 0 5,grow");
		btnApaga.setBorderPainted(false);
		setVisible(true);
		btnOffset = lblEstoque.getLocationOnScreen().y + lblEstoque.getSize().height;
		sizeOffset = (int) (Toolkit.getDefaultToolkit().getScreenSize().height*0.78);
		System.out.println(lblEstoque.getLocationOnScreen().y);
		System.out.println(Toolkit.getDefaultToolkit().getScreenSize().height*0.78);
		btnApaga.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(apaga == null) {
					apaga = new MenuVendasApaga(con);
					apaga.setLocation(new Point(0, getLocation().y+btnOffset));
					apaga.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,sizeOffset);
					apaga.setVisible(true);
				}else {
					apaga.refreshTable(con);
					apaga.setVisible(true);
					apaga.toFront();
					apaga.requestFocus();
				}
			}
		});
		btnApaga.setIcon(new ImageIcon(getClass().getResource("/trash.png")));
		getContentPane().add(btnApaga, "cell 4 0");
	}
	public void fechar() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
