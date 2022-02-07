package com.model;

public class DbGetter {
	
	private int idEstoque;
	private String codBarra;
	private String descricaoProd;
	private int quanti;
	private double valorUni;
	private double valorDinheiro;
	private double valorCartao;
	private double valorTotal;
	private double valorCusto;
	private String tipoPagamento;
	

	//Venda Setters

	
	
	public void setChaveEsto(int chaveEsto) {
		this.idEstoque = chaveEsto;
	}
	public DbGetter() {
	}

	
	public DbGetter(int idEstoque, String codBarra, String descricaoProd, int quanti, double valorUni,
			double valorDinheiro, double valorCartao, double valorTotal, double valorCusto, String tipoPagamento) {
		super();
		this.idEstoque = idEstoque;
		this.codBarra = codBarra;
		this.descricaoProd = descricaoProd;
		this.quanti = quanti;
		this.valorUni = valorUni;
		this.valorDinheiro = valorDinheiro;
		this.valorCartao = valorCartao;
		this.valorTotal = valorTotal;
		this.valorCusto = valorCusto;
		this.tipoPagamento = tipoPagamento;
	}
	public int getIdEstoque() {
		return idEstoque;
	}
	public void setIdEstoque(int idEstoque) {
		this.idEstoque = idEstoque;
	}
	public String getCodBarra() {
		return codBarra;
	}
	public void setCodBarra(String codBarra) {
		this.codBarra = codBarra;
	}
	public String getDescricaoProd() {
		return descricaoProd;
	}
	public void setDescricaoProd(String descricaoProd) {
		this.descricaoProd = descricaoProd;
	}
	public int getQuanti() {
		return quanti;
	}
	public void setQuanti(int quanti) {
		this.quanti = quanti;
	}
	public double getValorUni() {
		return valorUni;
	}
	public void setValorUni(double valorUni) {
		this.valorUni = valorUni;
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
	public double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public double getValorCusto() {
		return valorCusto;
	}
	public void setValorCusto(double valorCusto) {
		this.valorCusto = valorCusto;
	}
	public String getTipoPagamento() {
		return tipoPagamento;
	}
	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}
	


	
}

	

