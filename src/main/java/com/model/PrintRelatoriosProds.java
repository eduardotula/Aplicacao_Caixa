package com.model;


public class PrintRelatoriosProds {
	private String codBarra;
	private String desc;
	private int quanti;
	private double valorUni;
	private double valorTot;
	private String hora;
	
	public PrintRelatoriosProds(String codBarra, String desc, int quanti, double valoruni, double valorto, String hora) {
		this.codBarra = codBarra;
		this.desc = desc;
		this.quanti = quanti;
		this.valorUni = valoruni;
		this.valorTot = valorto;
		this.hora = hora;
	}


	public String getCod() {
		return codBarra;
	}
	public String getDesc() {
		return desc;
	}
	public int getQuanti() {
		return quanti;
	}
	public double getValorUni() {
		return valorUni;
	}
	public double getValorTot() {
		return valorTot;
	}
	public String getHora() {
		return hora;
	}
}
