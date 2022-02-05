package com.model;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrintPedidos implements Printable {
	private LocalTime lc;

	private List<Object[]> pedidos; // Formato String:Descricao, String:Nome, String:Numero, LocalDate:Data

	public void passArrayList(LocalTime hora, List<Object[]> pedidos) {
		this.lc = hora;
		this.pedidos = pedidos;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		Paper p = new Paper();
		double bodyHeight = 10.0; // bHeight
		double headerHeight = 5.0;
		double footerHeight = 5.0;
		double width = cm_to_pp(50);
		double height = cm_to_pp(headerHeight + bodyHeight + footerHeight);
		p.setSize(width, height);
		p.setImageableArea(0, 10, width, height - cm_to_pp(1));

		pf.setOrientation(PageFormat.PORTRAIT);
		pf.setPaper(p);

		LocalDate data = java.time.LocalDate.now();
		String date = data.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		int result = NO_SUCH_PAGE;
		if (page == 0) {
			Graphics2D g2d = (Graphics2D) g;
			double widths = pf.getImageableWidth();
			System.out.println(widths);
			System.out.println(pf.getImageableHeight());
			g2d.translate((int) pf.getImageableX(), (int) pf.getImageableY());


			try {
				int y = 20;
				int yShift = 10;

				g2d.setFont(new Font("Monospaced", Font.BOLD, 8));
				g2d.drawString(String.format("   Lista de %s: %s", "Pedido", date), -3, y);
				y += yShift;
				g2d.drawString("   Relatorio Gerado as: " + lc, -3, y);
				y += yShift;

				g2d.drawLine(-3, y, 190, y);
				y += yShift;

				for (Object[] pedido : pedidos) {
					g2d.drawString(String.format("%s", (String) pedido[0]), -3, y);
					y += yShift;
					g2d.drawString(String.format("%s %s", (String) pedido[1], (String) pedido[2]), -3, y);
					y += yShift;
					g2d.drawLine(-3, y, 190, y);
					y += yShift;

				}

				g2d.drawLine(-3, y, 190, y);
				y += yShift;
				g2d.drawString("   ", -3, y);
				y += yShift + 10;

			} catch (Exception e) {
				e.printStackTrace();
			}

			result = PAGE_EXISTS;
		}
		return result;
	}

	protected static double cm_to_pp(double cm) {
		return toPPI(cm * 0.393600787);
	}

	protected static double toPPI(double inch) {
		return inch * 72d;
	}

	public static class PrintLista implements Printable {

		private List<Object[]> lista; // Formato String:CodBarra, String:Descrição, String:Observação, LocalDate:Data
		private LocalTime lc;

		public void passArrayList(LocalTime hora, List<Object[]> lista) {
			this.lc = hora;
			this.lista = lista;
		}

		@Override
		public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
			Paper p = new Paper();
			double bodyHeight = 10.0; // bHeight
			double headerHeight = 5.0;
			double footerHeight = 5.0;
			double width = cm_to_pp(50);
			double height = cm_to_pp(headerHeight + bodyHeight + footerHeight);
			p.setSize(width, height);
			p.setImageableArea(0, 10, width, height - cm_to_pp(1));

			pf.setOrientation(PageFormat.PORTRAIT);
			pf.setPaper(p);

			LocalDate data = java.time.LocalDate.now();
			String date = data.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			int result = NO_SUCH_PAGE;
			if (page == 0) {
				Graphics2D g2d = (Graphics2D) g;
				double widths = pf.getImageableWidth();
				System.out.println(widths);
				System.out.println(pf.getImageableHeight());
				g2d.translate((int) pf.getImageableX(), (int) pf.getImageableY());


				try {
					int y = 20;
					int yShift = 10;

					g2d.setFont(new Font("Monospaced", Font.BOLD, 8));
					g2d.drawString(String.format("   Lista de %s: %s", "Lista", date), -3, y);
					y += yShift;
					g2d.drawString("   Relatorio Gerado as: " + lc, -3, y);
					y += yShift;

					g2d.drawLine(-3, y, 190, y);
					y += yShift;

					for (Object[] list : lista) {
						g2d.drawString(String.format("%s %s", (String) list[0],(String)list[1]), -3, y);
						y += yShift;
						g2d.drawString(String.format("%s", (String) list[2]), -3, y);
						y += yShift;
						g2d.drawLine(-3, y, 190, y);
						y += yShift;

					}

					g2d.drawLine(-3, y, 190, y);
					y += yShift;
					g2d.drawString("   ", -3, y);
					y += yShift + 10;

				} catch (Exception e) {
					e.printStackTrace();
				}

				result = PAGE_EXISTS;
			}
			return result;
		}

		public static PrintLista printLista() {
			return new PrintLista();
		}
	}
}
