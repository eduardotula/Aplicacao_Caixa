package com.model;

public class Recarga {
	
	private String operadora;
	private String numero;
	private Double valor;
	private char tipo;
	
	public Recarga() {
		
	}
	
	
	public Recarga(String operadora, String numero, Double valor, char tipo) {
		super();
		this.operadora = operadora;
		this.numero = numero;
		this.valor = valor;
		this.tipo = tipo;
	}


	public String getOperadora() {
		return operadora;
	}
	public void setOperadora(String operadora) {
		this.operadora = operadora;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public char getTipo() {
		return tipo;
	}
	public void setTipo(char tipo) {
		this.tipo = tipo;
	}
	

}
