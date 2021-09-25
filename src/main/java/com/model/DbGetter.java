package com.model;

public class DbGetter {
	
	private int idEstoque;
	private String codBarra;
	private String descricaoProd;
	private int quanti;
	private double valorUni;
	private double valorDinheiro;
	private double valorCartao;
	private double valorTota;
	private String tipoPagamento;
	

	//Venda Setters

	
	
	public void setChaveEsto(int chaveEsto) {
		this.idEstoque = chaveEsto;
	}
	public DbGetter() {
	}
	public DbGetter(int idEstoque, String codBarra, String descricaoProd, int quanti, double valorTota,
			double valorUni, double valorDinheiro, double valorCartao, String tipoPagamento) {
		super();
		this.idEstoque = idEstoque;
		this.codBarra = codBarra;
		this.descricaoProd = descricaoProd;
		this.quanti = quanti;
		this.valorTota = valorTota;
		this.valorUni = valorUni;
		this.valorDinheiro = valorDinheiro;
		this.valorCartao = valorCartao;
		this.tipoPagamento = tipoPagamento;
	}
	
	public double getValorDinheiro() {
		return valorDinheiro;
	}
	public void setValorDinheiro(double valorDinheiro) {
		this.valorDinheiro = valorDinheiro;
	}
	public double getValorCartao() {
		return valorCartao;
	}
	public void setValorCartao(double valorCartao) {
		this.valorCartao = valorCartao;
	}
	public void codVSetter(String codBarra) {
		this.codBarra = codBarra;
	}
	
	public void descVSetter(String descVenda) {
		this.descricaoProd = descVenda;
	}
	
	public void quantVSetter(int quanti) {
		this.quanti = quanti;
	}
	
	public void valorTotalSetter(double precoTotCon) {
		this.valorTota = precoTotCon;
	}
	
	
	public String getTipoPagamento() {
		return tipoPagamento;
	}
	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}
	public void valorUniSetter(double descontado) {
		this.valorUni = descontado;
	}

	
	//Venda Getters

	public double getValorUn() {
		return valorUni;
	}
	public String getCodBarra(){
		return codBarra;
	}
	
	public String getDescricao(){
		return descricaoProd;
	}
	
	public int getQuant(){
		
		return quanti;
	}
	
	public double getValorTot(){
		return valorTota;
	}

	public int getChaveEsto() {
		return idEstoque;
	}





	
}

	

