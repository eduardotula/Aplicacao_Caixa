package com.model;

import java.sql.Date;

public class ObjetoProdutoImport {
	private String codBa;
	private String prod;
	private int quanti = 0;
	private Double valorUltV =0.0;
	private Double valorCusto = 0.0;
	private int chave;
	private Date dataUltVe;
	private char itenAtivo;
	
	
	public ObjetoProdutoImport() {
		
	};
	public ObjetoProdutoImport(String codBa, String prod, int quanti, Double valorUltV, double valorCusto , Integer chave) {
		this.codBa = codBa;
		this.prod = prod;
		this.quanti = quanti;
		this.valorUltV = valorUltV;
		this.chave = chave;
		this.valorCusto = valorCusto;
	}
	public ObjetoProdutoImport(String codBa, String prod, int quanti, int chave, double valorUltV, double valorCusto, Date dataUltVe, char itenAtivo) {
		this.codBa = codBa;
		this.prod = prod;
		this.quanti = quanti;
		this.chave = chave;
		this.valorUltV = valorUltV;
		this.valorCusto = valorCusto;
		this.setDataUltVe(dataUltVe);
		this.setItenAtivo(itenAtivo);
	}
	public ObjetoProdutoImport(String cod, String desc, int quanti, double preco,double valorCusto) {
		this.codBa = cod;
		this.prod = desc;
		this.quanti = quanti;
		this.valorUltV = preco;
		this.valorCusto = valorCusto;
	}
	public Object[] getBasic() {
		Object[] p = new Object[] {chave,codBa,prod,quanti,valorCusto, valorUltV};
		return p;
	}
	public Object[] getAll() {
		Object[] p = new Object[] {chave,codBa,prod,quanti,dataUltVe,itenAtivo,valorCusto};
		return p;
	}

	public String getCodBa() {
		return codBa;
	}

	public String getProd() {
		return prod;
	}

	public int getQuanti() {
		return quanti;
	}

	public Integer getChave() {
		return chave;
	}

	public void setCodBa(String codBa) {
		this.codBa = codBa;
	}

	public void setProd(String prod) {
		this.prod = prod;
	}

	public void setQuanti(int quanti) {
		this.quanti = quanti;
	}

	public void setChave(Integer chave) {
		this.chave = chave;
	}
	public Date getDataUltVe() {
		return dataUltVe;
	}
	public void setDataUltVe(Date date) {
		this.dataUltVe = date;
	}
	public char getItenAtivo() {
		return itenAtivo;
	}
	public void setItenAtivo(char itenAtivo) {
		this.itenAtivo = itenAtivo;
	}
	public Double getValorUltV() {
		return valorUltV;
	}
	public void setValorUltV(Double valorUltV) {
		this.valorUltV = valorUltV;
	}
	public Double getValorCusto() {
		return valorCusto;
	}
	public void setValorCusto(Double valorCusto) {
		this.valorCusto = valorCusto;
	}
	
}
