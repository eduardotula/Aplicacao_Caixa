package com.control;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class SangriaFormatPrint implements Printable{
	private LocalTime lc;
	private LocalDate ld;
	private double sang;
	
	public void passArrayList(LocalDate date, LocalTime time, double Sangria) {
		this.lc = time;
		this.ld = date;
		this.sang = Sangria;
	}
	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		

		String date = ld.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String time = lc.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		DecimalFormat df = new DecimalFormat("R$0.##");
		df.setMaximumFractionDigits(3);
	      int result = NO_SUCH_PAGE;    
	        if (page == 0) {                    
	        
	            Graphics2D g2d = (Graphics2D) g;                    
	            double width = pf.getImageableWidth();                       
	            System.out.println(width);
	            System.out.println(pf.getImageableHeight());
	            g2d.translate((int) pf.getImageableX(),(int) pf.getImageableY());



	        
	        try{
	            int y=20;
	            int yShift = 10;
	            
	                
                
            g2d.setFont(new Font("Monospaced",Font.BOLD,8));
            g2d.drawString("   Sangria Gerada em: "+ date,-3,y);y+=yShift;
            g2d.drawString("   Sangria Gerada as: "+ time,-3,y);y+=yShift;

            g2d.drawLine(-3, y, 190, y);y+=yShift;
            g2d.drawString("  Valor: "+ df.format(sang),-3,y);y+=yShift+10;
            g2d.drawString("  Descri��o: ",-3,y);y+=yShift+50;
            g2d.drawString("  ..",-3,y);y+=yShift+10;

            
           
	           

	    }
	    catch(Exception e){
	    e.printStackTrace();
	    }

	              result = PAGE_EXISTS;    
	          }    
	          return result;
	}

}