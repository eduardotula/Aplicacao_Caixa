package com.view;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.control.EditConfigFile;
import com.viewadmin.FrameMenuAdmin;

public class MenuStarter extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Objetos visuais
	JButton btnCaixa = new JButton("Vender");
	JButton btnVendas = new JButton("Administra\u00E7\u00E3o");
	private PrintStream out = null;
	private MainVenda mainVenda;
	private HashMap<String, String> config = new HashMap<String, String>();
	public static EditConfigFile editConfig = new EditConfigFile("config.txt");

	public MenuStarter() {
		super("Menu");
		setSize(380, 180);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/menu.png")));
		config = editConfig.readConfig();
		editConfig.backupDia(config);



		// Listners
		btnCaixa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (mainVenda == null) {
							mainVenda = new MainVenda(config);
						} else {
							mainVenda.dispose();
							mainVenda = new MainVenda(config);
						}
					}
				});
			}
		});
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				try {
					out.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});
		btnVendas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						JPasswordField pas = new JPasswordField();
						JOptionPane.showConfirmDialog(null, pas, "Digite a Senha", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						char[] sen = pas.getPassword();
						String senha = new String(sen);
						if (senha.compareTo("102030") == 0) {
							new FrameMenuAdmin();
						} else {
							JOptionPane.showMessageDialog(new JFrame(), "Senha Inválida");
						}
					}
				});
			}
		});

		JLabel lblNewLabel = new JLabel("Frente Caixa\r\n");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 29));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
				groupLayout.createSequentialGroup().addGap(50).addGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(btnCaixa, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
								.addComponent(btnVendas, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
						.addGap(39)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addGap(29)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnVendas, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnCaixa, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(33, Short.MAX_VALUE)));
		getContentPane().setLayout(groupLayout);
	}

}
