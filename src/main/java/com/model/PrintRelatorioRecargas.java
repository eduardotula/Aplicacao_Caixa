package com.model;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PrintRelatorioRecargas implements Printable{
	private ArrayList<Recarga> recargas = new ArrayList<Recarga>();
	private LocalTime lc;
	private String funcio;
	public void passArrayList(LocalTime hora,String funcio,ArrayList<Recarga> recargas) {
		this.funcio = funcio;
		this.lc = hora;
		this.recargas = recargas;
	}
	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		Paper p = new Paper();
	    double bodyHeight =  10.0;   //bHeight  
	    double headerHeight = 5.0;                  
	    double footerHeight = 5.0;        
	    double width = cm_to_pp(50); 
	    double height = cm_to_pp(headerHeight+bodyHeight+footerHeight); 
	    p.setSize(width, height);
	    p.setImageableArea(0,10,width,height - cm_to_pp(1));  
	            
	    pf.setOrientation(PageFormat.PORTRAIT);  
	    pf.setPaper(p); 
	    
		LocalDate data = java.time.LocalDate.now();
		String date = data.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		NumberFormat nf = NumberFormat.getInstance();
		DecimalFormat df = new DecimalFormat("R$0.##");
		nf.setMaximumFractionDigits(2);
	      int result = NO_SUCH_PAGE;    
	        if (page == 0) {                    
	            Graphics2D g2d = (Graphics2D) g;                    
	            double widths = pf.getImageableWidth();                       
	            System.out.println(widths);
	            System.out.println(pf.getImageableHeight());
	            g2d.translate((int) pf.getImageableX(),(int) pf.getImageableY());

	            Double soma = 0.0;
	        
	        try{
	            int y=20;
	            int yShift = 10;
	            
	                
	            g2d.setFont(new Font("Monospaced",Font.BOLD,8));
	            g2d.drawString("   Vendas de: "+ funcio,-3,y);y+=yShift;
	            g2d.drawString("   Relatorio Gerado no Dia: "+ date,-3,y);y+=yShift;
	            g2d.drawString("   Relatorio Gerado em: "+ lc,-3,y);y+=yShift;

	            g2d.drawLine(-3, y, 190, y);y+=yShift;


	            for(Recarga recarga : recargas) {
	            	
		            g2d.drawString(String.format("   %s   %s  %s", recarga.getOperadora(),recarga.getNumero(),
		            		df.format(recarga.getValor())), -3, y);y+=yShift;
		            soma = soma + recarga.getValor();
	            }
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.drawString("   				Soma: "+ df.format(soma),-3,y);y+=yShift;
	            g2d.drawString("   ", -3, y);y+=yShift+10;
	           


	            

	    }
	    catch(Exception e){
	    e.printStackTrace();
	    }

	              result = PAGE_EXISTS;    
	          }    
	          return result;
	}
    protected static double cm_to_pp(double cm)
    {            
	        return toPPI(cm * 0.393600787);            
    }
 
protected static double toPPI(double inch)
    {            
	        return inch * 72d;            
    }
}

