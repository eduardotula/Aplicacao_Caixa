package com.model;

import javax.swing.JOptionPane;

// TODO: Auto-generated Javadoc
/**
 * The Class Alerts.
 */
public class Alerts {

	
	/**
	 * Exibe uma mensagem de informação
	 *
	 * @param message a mensagem
	 * @param title o titulo da janela
	 */
	public static void showMessage(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Exibe um mensagem de erro
	 *
	 * @param message a mensagem
	 * @param title o titulo da janela
	 */
	public static void showError(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
}
