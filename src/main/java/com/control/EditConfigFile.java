package com.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class EditConfigFile {

	private String file;
	private HashMap<String, String> configs = new HashMap<String, String>();

	public EditConfigFile(String file) {
		this.file = file;
	}

	public void salvarConfig(String key, String value) {
		try {
			readConfig();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			int lineCount = 0;
			for (Map.Entry<String, String> entry : configs.entrySet()) {

				if (key.contentEquals(entry.getKey())) {
					writer.write(String.format("%s=%s", entry.getKey(), value));
				} else {
					writer.write(String.format("%s=%s", entry.getKey(), entry.getValue()));
				}
				lineCount++;
				if (lineCount != configs.size()) {
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public HashMap<String, String> readConfig() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("config.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("/")) {
					String[] a = line.split("=");
					if (a.length == 2) {
						configs.put(a[0], a[1]);
					} else {
						configs.put(a[0], "");
					}
				}
			}
			reader.close();
			return configs;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erro ao ler arquivo de configurações", "Erro",
					JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, "Para gerar um novo arquivo com configurações abra administração");
			return null;
		}
	}

	/**
	 * Realiza uma copia do banco de dados para o path que esta no arquivo config
	 * Apenas uma copia do banco á permitida por dia
	 */
	public void backupDia(HashMap<String, String> config) {
		String pathSave = config.getOrDefault("backuppath", null);
		String backup = config.getOrDefault("backup", null);
		String bancoPath = config.getOrDefault("bancofullpath", null);
		if (backup != null && backup.contentEquals("true") && pathSave != null && bancoPath != null) {
			pathSave = String.format("%s%s%s%s%s", pathSave, File.separator, config.getOrDefault("loja", "VENDASN"),
					LocalDate.now().toString(),".fdb");
			Path pToSave = Paths.get(pathSave);
			Path pathBd = Paths.get(bancoPath);
			try {
				Files.copy(pathBd, pToSave);
				JOptionPane.showMessageDialog(null, "Backup concluido");
			} catch (FileAlreadyExistsException e) {
				System.out.println("Backup já realizado");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Falha ao Fazer backup do banco", "Erro",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Cria um novo arquivo contendo parametros padrão*/
	public void createConfigFile() {
		
	}

}
