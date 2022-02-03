package com.tablerenders_editor;

import java.text.Format;
import java.text.NumberFormat;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class TableRendererCurrency extends DefaultTableCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Format formatter;
	/*
	 *  Use the specified number formatter and right align the text
	 */
	public TableRendererCurrency(Format formatter)
	{
		this.formatter = formatter;
		setHorizontalAlignment( SwingConstants.RIGHT );
	}
	public void setValue(Object value)
	{
		//  Format the Object before setting its value in the renderer

		try
		{
			if (value != null)
				value = formatter.format(value);
		}
		catch(IllegalArgumentException e) {}

		super.setValue(value);
	}
	/*
	 *  Use the default currency formatter for the default locale
	 */
	public static TableRendererCurrency getCurrencyRenderer()
	{
		return new TableRendererCurrency( NumberFormat.getCurrencyInstance() );
	}

	/*
	 *  Use the default integer formatter for the default locale
	 */
	public static TableRendererCurrency getIntegerRenderer()
	{
		return new TableRendererCurrency( NumberFormat.getIntegerInstance() );
	}

	/*
	 *  Use the default percent formatter for the default locale
	 */
	public static TableRendererCurrency getPercentRenderer()
	{
		return new TableRendererCurrency( NumberFormat.getPercentInstance() );
	}
}