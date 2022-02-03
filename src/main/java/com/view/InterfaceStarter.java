package com.view;

import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.control.Conexao;
import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

public class InterfaceStarter {
	private static MenuStarter menu;
	static Conexao cone = new Conexao();
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new AcrylLookAndFeel());
			Locale.setDefault(Locale.getDefault());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				menu = new MenuStarter();
			}
		});

		
		
}
	public static void setMainVisi(boolean visi) {
		menu.setVisible(visi);
	}
}