package com.control;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.model.DefaultModels;

public class TableOperations {


	public void UpdateTabelaEditado(Connection con, Object dado, int chave,String chaveNome, String tabelaNome, String columnNome, Point cords) {
		//con : Conexao com o banco
		//dado : dado a ser inserido
		//chave : chave do campo atualizado
		//chaveNome : nome da coluna chave no banco
		//tabelaNome : nome da tabela � ser atualizado
		//columnNome : nome da coluna a ser atualizada no banco
		//cords : cordendas do valo na tabela
		String query = "UPDATE " + tabelaNome + " SET " + columnNome +" = ? WHERE " + chaveNome + " = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			if(dado instanceof String) {
				ps.setString(1, (String) dado);
			} else if (dado instanceof Integer) {
				ps.setInt(1, (int) dado);
			} else if (dado instanceof Double) {
				ps.setDouble(1,(double) dado);
			}else if (dado instanceof LocalDate) {
				ps.setDate(1, java.sql.Date.valueOf((LocalDate)dado));
			}else if (dado instanceof LocalTime) {
				ps.setTime(1, java.sql.Time.valueOf((LocalTime)dado));
			}
			ps.setInt(2, chave);
			ps.executeUpdate();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Falha em salvar dados em linha:"+cords.x +" coluna:"+ cords.y);
			e1.printStackTrace();
		}
			
	}
	public void exportarTabela(JTable table, DefaultModels model) {
		String path = new File("").getAbsolutePath();
		path = path + "\\Relatorios CSV.CSV";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			String nom = "";
			for(String nome : model.getColumnNames()) {
				nom = nom + "," + nome;
			}
			writer.write(nom.substring(1));
			writer.newLine();
	    	for(int i = 0;i<table.getRowCount();i++) {
	    		String l = "";
	    		for(int j = 0;j<table.getColumnCount();j++) {
	    			l = l +"," + table.getValueAt(i, j);
	    		}
	    		writer.write(l.substring(1));
	    		writer.newLine();
	    	}
	    	writer.close();
	    	JOptionPane.showMessageDialog(null, "Relatorio exportado para: " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
