package com.tablerenders_editor;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class TableEditorDateTime extends DefaultCellEditor
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object value;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
    public TableEditorDateTime()
    {
        super( new JTextField() );
        //((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
    }

    @Override
    public Object getCellEditorValue()
    {
        return value;
    }

    @Override
    public boolean stopCellEditing()
    {
        try
        {
        	//recebe o input do usuario
        	
            String editingValue = (String)super.getCellEditorValue();
            if(editingValue.contains(":")) {
            	value = LocalTime.parse(editingValue);
            }else {
            	value = LocalDate.parse(editingValue, formatter);
            }
        }
        catch(DateTimeParseException exception)
        {
            JTextField textField = (JTextField)getComponent();
            textField.setBorder(new LineBorder(Color.red));
            JOptionPane.showMessageDialog(null, "Insira um Valor Vï¿½lido");
            return false;
        }

        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column)
    {
    	
    	//retorna a string que á exibido no campo de ediï¿½áo
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        JTextField textField = (JTextField)c;
        textField.setBorder( new LineBorder(Color.BLACK) );

        String text = textField.getText();
        if(text.contains("-")) {
        	 LocalDate l = LocalDate.parse(text);
        	 textField.setText(l.format(formatter));
        }
        textField.selectAll();

        //return string
        return c;
    }
}

