package com.model;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.control.EditConfigFile;


public class PrintRelatoriosFechaFormat implements Printable{
	private ArrayList<PrintRelatoriosProds> prodsArray = new ArrayList<PrintRelatoriosProds>();
	private ArrayList<Recarga> recargas = new ArrayList<Recarga>();
	private Time lc;
	private String funcio;
	private double soma;
	private double troco;
	private double dinhe;
	private double cart;
	private double pix;
	public void passArrayList(ArrayList<PrintRelatoriosProds> prods, double soma, Time hora,String funcio, double troco,double dinhe,
			double cart,double pix, ArrayList<Recarga> recargas) {
		this.prodsArray = prods;
		this.soma = soma;
		this.funcio = funcio;
		this.lc = hora;
		this.troco = troco;
		this.dinhe = dinhe;
		this.cart = cart;
		this.pix = pix;
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
	      int r= prodsArray.size();
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
	            g2d.drawString("   Loja: "+ new EditConfigFile("config.txt").readConfig().getOrDefault("loja",null),-3,y);y+=yShift;

	            g2d.drawLine(-3, y, 190, y);y+=yShift;

	            g2d.setFont(new Font("Monospaced",Font.BOLD,7));
	            for(int s=0; s<r; s++)
	            {
	            	PrintRelatoriosProds prod = prodsArray.get(s);
	            g2d.drawString(" "+prod.getDesc()+"                            ",-3,y);y+=yShift;
	            g2d.drawString(" "+ prod.getCod() +" "+ prod.getHora() +" ", -3, y);
	            g2d.drawString("      "+prod.getQuanti()+" UN X "+nf.format(prod.getValorUni()),70,y); g2d.drawString(""+prod.getValorTot()+"",150,y);;y+=yShift;
	            soma += prod.getValorTot();
	            }
	            
	            g2d.setFont(new Font("Monospaced",Font.BOLD,8));
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.drawString("   "+ "Soma Total= "+ df.format(soma) , -3, y);
	            soma = 0.0;
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.drawString("   "+ "Caixa:", -3, y);y+=yShift;
	            g2d.drawString("   "+ "Troco: "+df.format(troco), -3, y);y+=yShift;
	            g2d.drawString("   "+ "Retirada Dinheiro: "+df.format(dinhe), -3, y);y+=yShift;
	            g2d.drawString("   "+ "Retirada Cart�o: "+df.format(cart), -3, y);y+=yShift;
	            g2d.drawString("   "+ "Retirada Pix: "+df.format(pix), -3, y);y+=yShift;
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            for(Recarga recarga : recargas) {
	            	
		            g2d.drawString(String.format("   %s   %s  %s", recarga.getOperadora(),recarga.getNumero(),
		            		df.format(recarga.getValor())), -3, y);y+=yShift;
		            soma = soma + recarga.getValor();
		            
	            }
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.drawString("   "+ "Soma Total: "+df.format(soma), -3, y);y+=yShift;
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

