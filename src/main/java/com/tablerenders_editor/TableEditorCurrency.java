package com.tablerenders_editor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class TableEditorCurrency extends DefaultCellEditor
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object value;

    public TableEditorCurrency()
    {
        super( new JTextField() );
        ((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
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
            String editingValue = (String)super.getCellEditorValue();

            //  Don't allow user to enter "."

            if (editingValue.contains("."))
            {
                JTextField textField = (JTextField)getComponent();
                textField.setBorder(new LineBorder(Color.red));
                return false;
            }

            // Replace local specific character

            int offset = editingValue.lastIndexOf(",");

            if (offset != -1)
            {
                StringBuilder sb = new StringBuilder(editingValue);
                sb.setCharAt(offset, '.');
                editingValue = sb.toString();
            }

            value = Double.parseDouble( editingValue );
        }
        catch(NumberFormatException exception)
        {
            JTextField textField = (JTextField)getComponent();
            textField.setBorder(new LineBorder(Color.red));
            return false;
        }

        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column)
    {
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        JTextField textField = (JTextField)c;
        textField.setBorder( new LineBorder(Color.BLACK) );

        String text = textField.getText();
        int offset = text.lastIndexOf(".");

        // Display local specific character

        if (offset != -1)
        {
            StringBuilder sb = new StringBuilder(text);
            sb.setCharAt(offset, ',');
            textField.setText( sb.toString() );
        }

        return c;
    }
}

