package com.control;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class PrintRelatorios {

	public DocPrintJob getPrinterJob(String printerToUse) {
		DocPrintJob job = null;
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		System.out.println("Number of print services: " + printServices.length);

		for (PrintService printer : printServices) {
			System.out.println("Printer: " + printer.getName());
			if (printer.getName().equals(printerToUse)) {
				job = printer.createPrintJob();
			}
		}
		return job;
	}

	public void choosePrintType(JTable table, Printable printable) {
		String res = (String) JOptionPane.showInputDialog(null, "Tipo da Impress�o", "Impress�o",
				JOptionPane.PLAIN_MESSAGE, null, new String[] { "Termica", "Impressora" }, "Termica");
		if (res.contentEquals("Impressora")) {
			try {
				table.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(null, "Falha ao Imprimir", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else if (res.contentEquals("Impressora")) {
			if (printable != null) {
				printer(printable, "IMPRESSORA TERMICA");
			} else {
				JOptionPane.showMessageDialog(null, "Modo indisponivel", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void printer(Printable bf, String nomeImpressora) {
		DocPrintJob job = getPrinterJob(nomeImpressora);
		HashDocAttributeSet docAttSet = new HashDocAttributeSet();
		docAttSet.add(MediaSizeName.ISO_A4);
		Book bk = new Book();
		PageFormat pageFormat = new PageFormat();
		Paper p = new Paper();

		double width = 220;
		double height = 100000;
		p.setSize(width, height);
		p.setImageableArea(0, 10, width, height);

		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(p);
		pageFormat.setPaper(p);
		bk.append((Printable) bf, pageFormat);
		SimpleDoc doc = new SimpleDoc(bk, DocFlavor.SERVICE_FORMATTED.PAGEABLE, docAttSet);
		try {
			job.print(doc, null);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "Falha na Impress�o");

			e.printStackTrace();
		}

	}
}
