package com.model;

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
import java.util.ArrayList;

public class PrintBillFormat implements Printable{
	private ArrayList<DbGetter> prodsArray = new ArrayList<DbGetter>();
	private LocalTime lc;
	private LocalDate ld;
	private String desconto;
	private String[] dados;
	
	public void passArrayList(String[] dados,ArrayList<DbGetter> prods, LocalDate date, LocalTime time, String string) {
		this.dados = dados;
		this.prodsArray = prods;
		this.lc = time;
		this.ld = date;
		this.desconto = string;
	}
	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		

		String date = ld.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String time = lc.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		DecimalFormat df = new DecimalFormat("0.##");
		df.setMaximumFractionDigits(3);

		if(desconto.length() == 0) {
			desconto = "0";
		}
		double valorTOt = 0.0;
	      int r= prodsArray.size();
	      int result = NO_SUCH_PAGE;    
	        if (page == 0) {                    
	        
	            Graphics2D g2d = (Graphics2D) g;                    
	            double width = pf.getImageableWidth();                       
	            System.out.println(width);
	            System.out.println(pf.getImageableHeight());
	            g2d.translate((int) pf.getImageableX(),(int) pf.getImageableY());



	          //  FontMetrics metrics=g2d.getFontMetrics(new Font("Arial",Font.BOLD,7));
	        
	        try{
	            int y=20;
	            int yShift = 10;
	            
	                
	            g2d.setFont(new Font("Monospaced",Font.BOLD,8));
	            g2d.drawString("    **CUPOM SEM VALOR FISCAL**      ",-3,y);y+=yShift;
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.drawString("    "+dados[0]+"     ",-3,y);y+=yShift;
	            g2d.drawString("    CNPJ:"+dados[1]+" ",-3,y);y+=yShift;
	            g2d.drawString("    "+dados[2]+" ",-3,y);y+=yShift;
	            g2d.drawString("    "+dados[3]+"            ",-3,y);y+=yShift;
	            g2d.drawString("    "+dados[4]+"            ",-3,y);y+=yShift;
	            g2d.drawString("    "+ date + " " + time +"        ",-3,y);y+=yShift;

	            g2d.drawLine(-3, y, 190, y);y+=yShift;

	            g2d.drawString(" NOME DO ITEM                 PRE�O   ",-3,y);y+=yShift;
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.setFont(new Font("Monospaced",Font.BOLD,7));
	            for(int s=0; s<r; s++)
	            {
	            	DbGetter prod = prodsArray.get(s);
	            g2d.drawString(" "+prod.getDescricao()+"                            ",-3,y);y+=yShift;
	            g2d.drawString("      "+prod.getQuant()+" UN X "+df.format(prod.getValorUn()),70,y); g2d.drawString(""+prod.getValorTot()+"",160,y);y+=yShift;
	            valorTOt = valorTOt + prod.getValorTot();
	            }
	            g2d.setFont(new Font("Monospaced",Font.BOLD,9));
	            g2d.drawLine(-3, y, 190, y);y+=yShift;
	            g2d.drawString(" DESCONTO:                  R$-"+desconto+"   ",-3,y);y+=yShift;
	            g2d.drawString(" VALOR TOTAL:               R$"+valorTOt+"   ",-3,y);y+=yShift;
	            
	            g2d.drawString("*************************************",-3,y);y+=yShift;
	            g2d.drawString("       OBRIGADO VOLTE SEMPRE           ",-3,y);y+=yShift;
	            g2d.drawString("*************************************",-3,y);y+=yShift;
	           

	    }
	    catch(Exception e){
	    e.printStackTrace();
	    }

	              result = PAGE_EXISTS;    
	          }    
	          return result;
	}

}
