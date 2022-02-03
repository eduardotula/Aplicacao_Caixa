package com.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.model.ObjetoProdutoImport;

public class XmlReaderFornecedor {

	private DocumentBuilderFactory dbf;
	private Document doc;
	private DocumentBuilder db;

	public XmlReaderFornecedor() {
		dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void loadXml(File document) {
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(document);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtem Raz�o social e CNPJ
	 * @return String[2]{Raz�o social,CNPJ}*/
	public String[] getFornecedor() {
		String[] inf = new String[2];
		System.out.println(doc.getDocumentElement().getNodeName());
		NodeList nodes = doc.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
		Node emitNode =  nodes.item(1);
		inf[1] = findNodeValueByName(emitNode, "CNPJ");
		inf[0] = findNodeValueByName(emitNode, "xNome");
		for(String a : inf) System.out.println(a);
		return inf;
	}

	/**
	 * Obtem uma lista de produtos registrados no XML
	 * @return List<DbGetter>
	 * @throws Exception*/
	
	public List<ObjetoProdutoImport> getMercadorias() throws Exception{
		List<ObjetoProdutoImport> prods = new ArrayList<ObjetoProdutoImport>();
		//System.out.println(doc.getDocumentElement().getNodeName());
		NodeList nodes = doc.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
		for(int i = 3;i<nodes.getLength();i++) {
			if(nodes.item(i).getNodeName().contentEquals("det")) {
				Node prodNode = nodes.item(i).getChildNodes().item(0);
				ObjetoProdutoImport prod = new ObjetoProdutoImport();
				
				prod.setCodBa(findNodeValueByName(prodNode, "cEAN"));
				prod.setProd(findNodeValueByName(prodNode, "xProd"));
				prod.setQuanti((int)Double.parseDouble(findNodeValueByName(prodNode, "qCom")));
				prod.setValorCusto(Double.parseDouble(findNodeValueByName(prodNode, "vUnCom")));
				prods.add(prod);
			}
		}
		return prods;
	}
	
	public String findNodeValueByName(Node node, String nodeName) {
		NodeList nodes = node.getChildNodes();
		for(int i = 0;i<nodes.getLength();i++) {
			Node nodeF = nodes.item(i);
			if(nodeF.getNodeName().contentEquals(nodeName)) {
				return nodeF.getTextContent();
			}
		}
		throw new NullPointerException("Node n�o encontrado");
	}
	
}
