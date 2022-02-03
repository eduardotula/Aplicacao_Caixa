package com.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DefaultModels extends DefaultTableModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private Vector<Vector> data;
	private boolean[] columnEditables;
	private String[] columnName;
	private Class<?>[] cl;
	//Armazena um numero inteiro que representa se o produto esta ativo atravez TableRenderEstoque
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final Vector<Integer> dadoAtivo = new Vector();
	

	public DefaultModels(String[] columnName) throws NullPointerException{
		this(columnName, null, null);
	}

	public DefaultModels(String[] columnName, Class<?>[] classesTableEsto) throws NullPointerException{
		this(columnName, null,classesTableEsto);
	}
	
	@SuppressWarnings("unchecked")
	public DefaultModels(String[] colName, boolean[] columnEditables, Class<?>[] classesTableEsto) throws NullPointerException{
		super(colName,0);
		this.columnName = colName;
		this.columnEditables = columnEditables;
		this.cl = classesTableEsto;
		
		if(columnName == null || columnName.length < 1) throw new NullPointerException("Array de nome não pode ser nulo ou vazio");
		if(columnEditables == null) {
			columnEditables = new boolean[columnName.length];
			for(int i = 0;i<columnName.length;i++) columnEditables[i] = false;
		}
		
		if(cl == null) {
			cl = new Class<?>[columnName.length];
			for(int i = 0;i<columnName.length;i++) cl[i] =  Object.class;
		}
		this.data = getDataVector();
	}
	


	public void setColumnClass(Class<?>[] col) {
		this.cl = col;
	}
	@Override
	public Object getValueAt(int row, int col) {
		@SuppressWarnings("rawtypes")
		Vector d = (Vector)dataVector.elementAt(row);
		return d.elementAt(col);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setValueAt(Object aValue, int row, int column) {
		@SuppressWarnings("rawtypes")
		Vector rowVector = (Vector)dataVector.elementAt(row);
		rowVector.setElementAt(aValue, column);
		fireTableCellUpdated(row, column);
	}

	public String getValueAtStr(int row, int col) {
		@SuppressWarnings("rawtypes")
		Vector d = (Vector)dataVector.elementAt(row);
		try {
			if(d.elementAt(col) instanceof String) { return (String) d.elementAt(col);
			}else if (d.elementAt(col) instanceof Integer) { return Integer.toString((Integer)d.elementAt(col));
			}else if (d.elementAt(col) instanceof Double) { return Double.toString((double) d.elementAt(col));}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer getValueAtInt(int row, int col) {
		@SuppressWarnings("rawtypes")
		Vector d = (Vector)dataVector.elementAt(row);
		try {
			if(d.elementAt(col) instanceof Integer) { return (Integer) d.elementAt(col);
			}else if (d.elementAt(col) instanceof String) { return Integer.parseInt((String)d.elementAt(col));}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Double getValueAtDoub(int row, int col) {
		@SuppressWarnings({ "rawtypes" })
		Vector d = (Vector)dataVector.elementAt(row);
		try {
			if(d.elementAt(col) instanceof Double) { return (Double) d.elementAt(col);
			}else if (d.elementAt(col) instanceof String) { return Double.parseDouble((String) d.elementAt(col));}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return cl[column];
		
	}
	@Override
	   public boolean isCellEditable(int row, int column) {
	        return columnEditables[column];
	        }
	
	public void addDadoAtivo(Integer i) {
		dadoAtivo.add(i);
	}
	//retorna se o dado da tabela esta esta ativo
	public int getDadoAtivo(int row) {
		return dadoAtivo.get(row);
	}
	
	
	public Object getValueAtRow(int row) {
		return data.elementAt(row);
	}
	
	public void setValueAtNoFire(Object aValue, int row, int column) {
        @SuppressWarnings("unchecked")
		Vector<Object> rowVector = data.elementAt(row);
        rowVector.setElementAt(aValue, column);
	}
	public void addRowNoFire(Object[] rowData) {
        data.insertElementAt(convertToVector(rowData), getRowCount());
        justifyRows(getRowCount(), getRowCount()+1);
	}
    
	public void removeAllRows() {
		data.clear();
		dadoAtivo.clear();
		fireTableDataChanged();
	}

	public Double sumColumn(Integer column) {
		Double soma = 0.0;
		if(column == null) {
			return soma;
		}
		for(@SuppressWarnings("rawtypes") Vector linha : data) {
			if(linha.get(column)instanceof Double) {
				
				soma = soma+(Double)linha.get(column);
			}
		}
		return soma;
	}
    private void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed
        // instead of the AbstractTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        data.setSize(getRowCount());

        for (int i = from; i < to; i++) {
            if (data.elementAt(i) == null) {
            	data.setElementAt(new Vector<>(), i);
            }
            data.elementAt(i).setSize(getColumnCount());
        }
    }
    @SuppressWarnings("unchecked")
	public void exportarCSV() {
		String path = new File("").getAbsolutePath();
		path = path + "\\Relatorios CSV.CSV";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			String nom = "";
			for(String nome : columnName) {
				nom = nom + "," + nome;
			}
			writer.write(nom.substring(1));
			writer.newLine();
	    	for(Vector<Object> linha : data) {
	    		String l = "";
	    		for(Object o : linha) {
	    			l = l +"," + o;
	    		}
	    		writer.write(l.substring(1));
	    		writer.newLine();
	    	}
	    	writer.close();
	    	JFrame f = new JFrame();
	    	f.setAlwaysOnTop(true);
	    	JOptionPane.showMessageDialog(f, "Relatorio exportado para: " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public String[] getColumnNames() {
    	if(columnName.length > 0) {
        	return columnName;
    	}else {
    		return new String[0];
    	}
    }
}
