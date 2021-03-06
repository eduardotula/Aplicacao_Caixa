package com.view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.control.PrintRelatorios;
import com.model.Alerts;
import com.model.CustomSQL;
import com.model.DBOperations;
import com.model.PrintRelatoriosFechaFormat;
import com.model.PrintRelatoriosProds;
import com.model.PrintSangriaFormat;
import com.model.Recarga;

import net.miginfocom.swing.MigLayout;

public class ControleCaixa extends JFrame {

	PrintRelatorios printRelatorios = new PrintRelatorios();
	// Obje Visuais

	/**
	 * 
	 */
	private Connection con;
	private static final long serialVersionUID = 1L;
	private JTextField txtTroco = new JTextField();
	private JTextField txtRetirada = new JTextField();
	private JButton btnAbrir = new JButton("Abrir Caixa");
	private JButton btnRet = new JButton("Sangria");
	private JLabel lblRet = new JLabel("Retirada");
	private JLabel lblValorCx = new JLabel("Valor do Troco");
	private JButton btnFechar = new JButton("Fechar Caixa");
	final JLabel lblStatusCaixa = new JLabel("Status do Caixa:");
	JLabel lblStatus = new JLabel("");
	private final JButton btnReimpre = new JButton("Reimprimir ultimo caixa");

	public ControleCaixa(Connection con) {
		super("Controle de Caixa");
		setSize(288, 163);
		this.con = con;
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[][][]", "[][][][]"));
		lblValorCx.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(lblValorCx, "cell 0 0,alignx left");
		getContentPane().add(txtTroco, "cell 1 0,growx");
		lblRet.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(lblRet, "cell 0 1,alignx left");
		getContentPane().add(txtRetirada, "cell 1 1,growx");
		getContentPane().add(btnRet, "cell 2 1");
		setResizable(false);
		btnRet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Checa se o caixa esta atualmente aberto
				if (MainVenda.valorCaixaAberto == 1) {
					LocalTime lt = LocalTime.now();
					LocalDate ld = LocalDate.now();
					try {
						Integer retirada = Integer.parseInt(JOptionPane
								.showInputDialog(new JFrame(), "Digite O Valor da Retirada").replace(",", "."));
						DBOperations.DmlSql(con, "INSERT INTO OPERACOES_CAIXA VALUES (NULL, ?, 0,0,?, ?, ?);",
								"Retirada", retirada, MainVenda.IdCaixa, LocalTime.now());
						updateFrame(con);
						printer(lt, ld, retirada);
					} catch (NumberFormatException e) {
						Alerts.showError("Valor inv?lido", "Erro");
						e.printStackTrace();
					} catch (SQLException e) {
						Alerts.showError(e.getMessage(), "Erro");
						e.printStackTrace();
					}

				} else {
					JOptionPane.showMessageDialog(null, "Favor Abrir o Caixa");
				}
			}
		});
		getContentPane().add(btnAbrir, "cell 0 2,alignx left");

		getContentPane().add(btnReimpre, "cell 1 2 2 1");
		getContentPane().add(btnFechar, "flowx,cell 0 3,alignx left");
		txtTroco.setEditable(false);
		txtTroco.setColumns(10);
		txtRetirada.setEditable(false);
		txtRetirada.setColumns(10);

		getContentPane().add(lblStatusCaixa, "cell 1 3,alignx right");

		getContentPane().add(lblStatus, "cell 2 3,alignx left");
		btnAbrir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (MainVenda.valorCaixaAberto == 0) {
					new AbrirCaixa(con);
					dispose();
				} else {
					JOptionPane.showMessageDialog(null, "Caixa j? Aberto");
				}
			}
		});

		btnFechar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Checa se o caixa esta atualmente aberto
				if (MainVenda.valorCaixaAberto == 1) {
					new Fechamento(con);
					dispose();
				} else {
					JOptionPane.showMessageDialog(null, "Favor Abrir o Caixa");
				}
			}
		});

		btnReimpre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reimprimirCaixa();
			}
		});
	}

	public void updateFrame(Connection con) {
		DecimalFormat df = new DecimalFormat("R$0.##");
		double[] valores = CustomSQL.getControleCaixaValues(con);
		txtTroco.setText(df.format(valores[0]));
		txtRetirada.setText(df.format(valores[1]));

	}

	private void printer(LocalTime time, LocalDate date, double sangria) {
		PrintSangriaFormat bf = new PrintSangriaFormat();
		bf.passArrayList(date, time, sangria);
		printRelatorios.printer(bf, PrintRelatorios.getImpressora());

	}

	private void reimprimirCaixa() {
		if (MainVenda.IdCaixa > 0) {
			try {
				int idCaixa = MainVenda.IdCaixa;
				if (idCaixa == 1)
					throw new Exception("N?o h? caixas anteriores");
				Double[] valores = DBOperations.selectSql1Dimen(con,
						"SELECT TROCOCAIXA,VALORDINHEIRO,VALORCART FROM OPERACOES_CAIXA "
								+ "WHERE CONTROLECAIXA_IDCAIXA = ? AND OPERACAO = 'Caixa Fechado';",
						new Double[0], idCaixa--);
				ArrayList<PrintRelatoriosProds> setterDb = new ArrayList<PrintRelatoriosProds>();
				ArrayList<Recarga> recargas = new ArrayList<Recarga>();
				ResultSet rs = DBOperations.selectSqlRs(con,
						"SELECT RECARGA,NUMERO,VALOR FROM RECARGAS WHERE CONTROLECAIXA_IDCAIXA = ?", idCaixa);
				while (rs.next()) {
					Recarga recarg = new Recarga();
					recarg.setOperadora(rs.getString("RECARGA"));
					recarg.setNumero(rs.getString("NUMERO"));
					recarg.setValor(rs.getDouble("VALOR"));
					recargas.add(recarg);
				}
				convertTable(con, setterDb, idCaixa);
				PrintRelatoriosFechaFormat bf = new PrintRelatoriosFechaFormat();
				Time time = java.sql.Time.valueOf(LocalTime.now());
				String func = DBOperations.selectSql1Dimen(con,
						"SELECT FUNCIONARIO FROM CONTROLECAIXA WHERE IDCAIXA = ?", new String[0], idCaixa)[0];
				bf.passArrayList(setterDb, time, func, valores[0], valores[2], valores[1], valores[3], recargas);
				printRelatorios.printer(bf, PrintRelatorios.getImpressora());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "Favor abrir o caixa");
		}
	}

	// Copia todos as vendas do ultimo caixa para o relatorio
	public double convertTable(Connection con, ArrayList<PrintRelatoriosProds> prodTabela, int idCaixa) {
		try {
			System.out.println("IDCAIXA " + idCaixa);
			double somaTot = 0.0;
			String query = "SELECT P.CODBARRA,P.DESCRICAO, V.QUANTI, V.VALORDINHEIRO, V.VALORCARTAO, V.VALORTOT,"
					+ "V.HORA FROM VENDAS V INNER JOIN PRODUTOS P ON V.IDPROD = P.IDPROD "
					+ "WHERE CONTROLECAIXA_IDCAIXA = ?;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, idCaixa);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String cod = rs.getString("CODBARRA");
				String desc = rs.getString("DESCRICAO");
				int quanti = (rs.getInt("QUANTI"));
				double valorUni = (rs.getDouble("VALORDINHEIRO") + rs.getDouble("VALORCARTAO"));
				double valorTot = (rs.getDouble("VALORTOT"));
				somaTot = valorTot + somaTot;
				Time hora = (rs.getTime("HORA"));
				PrintRelatoriosProds prodsPrint = new PrintRelatoriosProds(cod, desc, quanti, valorUni, valorTot,
						hora.toString());
				prodTabela.add(prodsPrint);
			}

			return somaTot;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "N?o foi Poss?vel Salvar, Chece os valores e tente Novamento");
			e.printStackTrace();
			return 0.0;
		}
	}
}
