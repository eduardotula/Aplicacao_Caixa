package com.view.recarga;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.view.MainVenda;

import net.miginfocom.swing.MigLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class Recargas extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>(new String[] {"Operadora", "VIVO", "TIM", "CLARO", "OI", "OUTRA"});
	private RelatorioRecarga recarga = new RelatorioRecarga(MainVenda.con);
	private JTextField 	txtNumero = new JTextField();
	private JTextField	txtValor = new JTextField("R$0.00");
	private JComboBox<String> comboBox = new JComboBox<String>();
	private JLabel lblOperadora = new JLabel("Operadora");
	private JLabel lblNumero = new JLabel("Numero");
	private JLabel lblValor = new JLabel("Valor");
	private JButton btnRelatorios = new JButton("Relatorios");
	private JButton btnConfirmar = new JButton("Confirmar");
	private final JTextField txtOutra = new JTextField();
	private final JLabel lblPagamento = new JLabel("Pagamento");
	private final JRadioButton rdnCartao = new JRadioButton("Cart\u00E3o");
	private final JRadioButton rdnDinheiro = new JRadioButton("Dinheiro");
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public Recargas() {
		super("Recargas");
		txtOutra.setColumns(10);
		createAndShowGUI();
		setListeners();
	}
	
	public void createAndShowGUI() {
		setSize(296,233);
		setLocationRelativeTo(null);
		setVisible(false);
		getContentPane().setLayout(new MigLayout("", "[142.00,grow][grow]", "[][][][][][][]"));
		getContentPane().add(lblOperadora, "cell 0 0 2 1");
		getContentPane().add(comboBox, "cell 0 1 2 1,growx");
		getContentPane().add(txtOutra, "cell 0 2 2 1,growx");
		getContentPane().add(lblNumero, "flowx,cell 0 3");
		getContentPane().add(txtNumero, "cell 0 4,growx");
		txtNumero.setColumns(10);
		getContentPane().add(lblValor, "cell 1 3");
		getContentPane().add(txtValor, "cell 1 4,growx");
		txtValor.setColumns(10);
		comboBox.setModel(comboModel);
		txtOutra.setEditable(false);
		
		getContentPane().add(lblPagamento, "flowx,cell 0 5");
		buttonGroup.add(rdnDinheiro);
		
		getContentPane().add(rdnDinheiro, "cell 1 5");
		getContentPane().add(btnRelatorios, "cell 0 6");
		getContentPane().add(btnConfirmar, "cell 1 6,alignx right");
		buttonGroup.add(rdnCartao);
		getContentPane().add(rdnCartao, "cell 0 5");
		recarga.setLocation(this.getLocation().x-300, this.getLocation().y);
	}
	public void setListeners() {
		addWindowListener(new WindowAdapter() {
			 public void windowClosing(WindowEvent e) {
				 recarga.setVisible(false);
			 }
			
		});
		btnRelatorios.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				recarga.setVisible(true);
				recarga.refreshTable();
				recarga.requestFocus();
				recarga.toFront();
			}
		});
		txtValor.addMouseListener(new MouseAdapter() {
			 public void mouseReleased(MouseEvent e) {
				 txtValor.selectAll();
			 }
		});
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBox.getSelectedIndex() == 5) {
					txtOutra.setEditable(true);
				}else {
					txtOutra.setEditable(false);
				}
			}
		});
		btnConfirmar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(comboBox.getSelectedIndex() != 0) {
						if(rdnCartao.isSelected() || rdnDinheiro.isSelected()) {
							String opera = (String) comboBox.getSelectedItem();
							String numero = txtNumero.getText();
							Double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
							String pagamento = null;
							if(rdnCartao.isSelected()) { pagamento = "C";}else if(rdnDinheiro.isSelected()) { pagamento = "D";}
							salvarRecarga(MainVenda.con,opera, numero, valor, pagamento);
							resetText();
							fechar();
						}else {
							JOptionPane.showMessageDialog(null, "Selecione Metodo de Pagamento");
						}
					}else {
						JOptionPane.showMessageDialog(null, "Selecione uma Operadora");
						}
				}catch (Exception e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "Valores Inv�lidos");
				}
			}
		});
	}
	
	public void salvarRecarga(Connection con, String opera, String numero, Double valor, String pagamento) throws Exception{
			PreparedStatement ps = con.prepareStatement("INSERT INTO RECARGAS VALUES(NULL, ?,?,?,?,?,?,?)");
			ps.setString(1, opera);
			ps.setString(2, numero);
			ps.setDouble(3, valor);
			ps.setTime(4, java.sql.Time.valueOf(LocalTime.now()));
			ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
			ps.setString(6, pagamento);
			ps.setInt(7, MainVenda.IdCaixa);
			ps.executeUpdate();
	}
	public void resetText(){
		comboBox.setSelectedIndex(0);
		txtNumero.setText(null);
		txtOutra.setText(null);
		txtOutra.setEditable(false);
		txtValor.setText("R$0.00");
		rdnCartao.setSelected(false);
		rdnCartao.setSelected(false);
		rdnDinheiro.setSelected(false);
		rdnDinheiro.setSelected(false);
	}
	public void fechar() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
