package com.source;

import java.util.Locale;

import javax.swing.SwingUtilities;

import com.control.Conexao;
import com.formdev.flatlaf.FlatLightLaf;

public class InterfaceStarter {
	private static menuStarter menu;
	static Conexao cone = new Conexao();
	public static void main(String[] args) {
		FlatLightLaf.setup();
		Locale.setDefault(Locale.getDefault());
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				menu = new menuStarter();
			}
		});

		
		
}
	public static void setMainVisi(boolean visi) {
		menu.setVisible(visi);
	}
}