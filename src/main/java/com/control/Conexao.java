package com.control;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.firebirdsql.jdbc.FBDriver;

/* 
 * Database:VENDAS.FDB 
 * Local:C:/VENDAS.FDB
 * User:SYSDBA
 * Password:masterkey
 * Conexão:localhost
 * Porta:3050
 */

public class Conexao {
	

	public Connection getCone(HashMap<String, String> config){
		Properties props = new Properties();
		props.setProperty("user", "SYSDBA");
		props.setProperty("password", "masterkey");
		props.setProperty("encoding", "UTF8");
		
		try {
			DriverManager.deregisterDriver(new FBDriver());
			return  DriverManager.getConnection("jdbc:firebirdsql://"+config.get("bancopath"), props);
		
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Falha Ao obter Conexão com Database:");
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}
}
