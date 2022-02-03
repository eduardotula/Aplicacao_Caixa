package com.tablerenders_editor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.table.DefaultTableCellRenderer;

/*
 *	Use a formatter to format the cell Object
 */
public class TableRendererDate extends DefaultTableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");


	/*
	 *   Use the specified formatter to format the Object
	 */
	public TableRendererDate()
	{}

	public void setValue(Object value)
	{
		//  Format the Object before setting its value in the renderer

		try
		{
			if (value != null && value instanceof LocalDate) {
				value = ((LocalDate)value).format(formatters);
			}
		}
		catch(IllegalArgumentException e) {e.printStackTrace();}
		super.setValue(value);
	}

	/*
	 *  Use the default date/time formatter for the default locale
	 */
	public static TableRendererDate getDateTimeRenderer()
	{
		return new TableRendererDate();
	}

	/*
	 *  Use the default time formatter for the default locale
	 */

}
