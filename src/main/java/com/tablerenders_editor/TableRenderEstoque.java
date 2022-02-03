package com.tablerenders_editor;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.model.DefaultModels;


public class TableRenderEstoque extends DefaultTableCellRenderer{
	
	/**
	 * 
	 */
	private  DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		DefaultModels model = (DefaultModels) table.getModel();
		row = table.convertRowIndexToModel(row);
		int quanti = (int) model.getValueAt(row, 3);
		
		if (value != null && value instanceof LocalDate) {
			value = ((LocalDate)value).format(formatters);
			this.setValue(value);
		}
		
		if(quanti < 0 && model.getDadoAtivo(row) == 1) {
			cell.setForeground(Color.red);
		}else if(model.getDadoAtivo(row) == 0){
			cell.setForeground(Color.LIGHT_GRAY);
		}else {
			cell.setForeground(Color.black);
		}
		return cell;
		
	}
}
