package com.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.firebirdsql.jdbc.field.TypeConversionException;

// TODO: Auto-generated Javadoc
/**
 * The Class DBVendas.
 *
 * @author eduar
 */
public class DBOperations {

	/** The sd. */
	SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");

	/**
	 * Insere os parametros contidos em sets no PreparedStatement ps.
	 *
	 * @param ps   o preparedstatement
	 * @param sets array contendo parametros � serem inseridos na query
	 * @throws SQLException       the SQL exception
	 */
	private static void setPrepared(PreparedStatement ps, Object... sets) throws ClassCastException , SQLException {
		if (ps == null || ps.isClosed())
			throw new SQLException("PreparedStatement is closed or null");
		if (sets != null && sets.length > 0) {
			int co = 1;
			try {
				for (int i = 0; i < sets.length; i++) {
					if (sets[i] instanceof LocalDate) {
						ps.setDate(co, java.sql.Date.valueOf((LocalDate) sets[i]));
					} else if (sets[i] instanceof LocalTime) {
						ps.setTime(co, java.sql.Time.valueOf((LocalTime) sets[i]));
					} else {
						ps.setObject(co, sets[i]);
					}
					co++;
				}
			} catch (TypeConversionException e) {
				e.printStackTrace();
				throw new ClassCastException("Falha ao converter objeto java para sql de indice " + co--);
			}
		}
	}

	/**
	 * Append qualquer tabela.
	 *
	 * @param ps          the {@link PreparedStatement}
	 * @param tableModels o modelo da tabela que ser� populada
	 * @throws SQLException 
	 * @see DefaultModels
	 */
	public static void appendAnyTable(PreparedStatement ps, DefaultModels tableModels) throws SQLException {
		ResultSet rs = ps.executeQuery();
		Object[] a = new Object[tableModels.getColumnCount()];
		int columnCount = tableModels.getColumnCount();
		while (rs.next()) {
			int co = 1;
			for (int i = 0; i < columnCount; i++) {
				if (rs.getObject(co) instanceof java.sql.Date) {
					a[i] = ((Date) rs.getObject(co)).toLocalDate();
				} else if (rs.getObject(co) instanceof java.sql.Time) {
					a[i] = ((Time) rs.getObject(co)).toLocalTime();
				} else if (rs.getObject(co) instanceof BigDecimal) {
					a[i] = ((BigDecimal) rs.getObject(co)).doubleValue();
				} else {
					a[i] = rs.getObject(co);
				}
				co++;
			}
			tableModels.addRow(a);
		}
	}

	/**
	 * Append any table.
	 *
	 * @param con         the con
	 * @param query       � ser executada
	 * @param tableModels o modelo da tabela que ser� populada
	 * @param sets        array contendo parametros � serem inseridos na query
	 * @throws SQLException
	 * @see DefaultModels
	 */
	public static void appendAnyTable(Connection con, String query, DefaultModels tableModels, Object... sets) throws SQLException,ClassCastException {
			PreparedStatement ps = con.prepareStatement(query);
			setPrepared(ps, sets);
			appendAnyTable(ps, tableModels);
	}

	/**
	 * Select sql rs.
	 *
	 * @param con   the con
	 * @param query the query
	 * @param sets  array contendo parametros � serem inseridos na query
	 * @return the result set
	 * @throws SQLException       the SQL exception
	 */
	public static ResultSet selectSqlRs(Connection con, String query, Object... sets)
			throws SQLException,ClassCastException {
		PreparedStatement ps = con.prepareStatement(query);
		setPrepared(ps, sets);
		return ps.executeQuery();
	}

	/**
	 * <p>
	 * Realiza uma busca no banco utilizando o parametro query um array de 2
	 * dimens�es ser� retornado, neste array esta contido varios tipos de objetos
	 * ser� necessario um processamento extra para realizar um cast no array ou um
	 * conhecimento previo dos tipo de objetos contido neste array Eg: Se uma query
	 * for realizada "SELECT ID,NOME FROM TABELA" um array Object ser� retornado,
	 * mas n�o � poss�vel realizar um cast String[] s = (String[]) Object[] pois
	 * este metodo cria uma array de objetos genericos ser� necessario realizar um
	 * cast individualmente nos objetos
	 * 
	 * <p>
	 * Os tipos {@link Date}, {@link Time} e {@link BigDecimal} ser�o convertidos
	 * para: {@link LocalDate}, {@link LocalTime} e {@link Double} respectivamente
	 *
	 * 
	 * @param con           o objeto de conex�o
	 * @param query         a query a ser executada
	 * @param sets          array contendo parametros � serem inseridos na query
	 * @param quantiColunas a quantidade de colunas contida na query
	 * @return Object[][] o array de objetos generico
	 * @throws SQLException
	 */
	public static Object[][] selectSql2Dimen(Connection con, String query, int quantiColunas, Object... sets)
			throws SQLException {
		ResultSet rs = selectSqlRs(con, query, sets);
		LinkedList<Object[]> list = new LinkedList<Object[]>();
		Object[] a = new Object[quantiColunas];
		while (rs.next()) {
			int co = 1;
			for (int i = 0; i < quantiColunas; i++) {
				if (rs.getObject(co) instanceof java.sql.Date) {
					a[i] = ((Date) rs.getObject(co)).toLocalDate();
				} else if (rs.getObject(co) instanceof java.sql.Time) {
					a[i] = ((Time) rs.getObject(co)).toLocalTime();
				} else if (rs.getObject(co) instanceof BigDecimal) {
					a[i] = ((BigDecimal) rs.getObject(co)).doubleValue();
				} else {
					a[i] = rs.getObject(co);
				}
				co++;
				list.add(a);
			}
		}
		Object[][] ret = new Object[list.size()][quantiColunas];
		for (int i = 0; i < list.size(); i++)
			ret[i] = list.get(i);
		return ret;
	}

	/**
	 * Select sql 1 dimen.
	 *
	 * <p>Os tipos {@link Date}, {@link Time} e {@link BigDecimal} ser�o convertidos
	 * para: {@link LocalDate}, {@link LocalTime} e {@link Double} respectivamente
	 * @param <T>   the generic type
	 * @param con   a sess�o de cone��o com o banco
	 * @param query query
	 * @param sets  array contendo parametros � serem inseridos na query
	 * @param type  o tipo do array de objetos a ser retornado
	 * @return the object[]
	 * @throws SQLException       the SQL exception
	 */
	public static <T> T[] selectSql1Dimen(Connection con, String query, T[] type, Object... sets)
			throws SQLException {
		return selectSqlList(con, query, sets).toArray(type);
	}

	/**
	 * Retorna uma lista contendo os dados buscados na query, esta lista retorna um
	 * objeto generico e pode retornar uma lista que n�o contem elementos
	 * 
	 * <p>Os tipos {@link Date}, {@link Time} e {@link BigDecimal} ser�o convertidos
	 * para: {@link LocalDate}, {@link LocalTime} e {@link Double} respectivamente
	 *
	 * @param con   the con
	 * @param query the query
	 * @param sets  the sets
	 * @return the list
	 * @throws SQLException       the SQL exception
	 */
	public static List<Object> selectSqlList(Connection con, String query, Object... sets)
			throws  SQLException {
		ResultSet rs = selectSqlRs(con, query, sets);
		LinkedList<Object> list = new LinkedList<Object>();
		while (rs.next()) {
			int co = 1;
			if (rs.getObject(co) instanceof java.sql.Date) {
				list.add(((Date) rs.getObject(co)).toLocalDate());
			} else if (rs.getObject(co) instanceof java.sql.Time) {
				list.add(((Time) rs.getObject(co)).toLocalTime());
			} else if (rs.getObject(co) instanceof BigDecimal) {
				list.add(((BigDecimal) rs.getObject(co)).doubleValue());
			} else {
				list.add(rs.getObject(co));
				co++;
			}
		}
		return list;
	}

	/**
	 * Executa opera��es DML como (INSERT, UPDATE or DELETE)
	 * 
	 * <p>Os tipos {@link Date}, {@link Time} e {@link BigDecimal} ser�o convertidos
	 * para: {@link LocalDate}, {@link LocalTime} e {@link Double} respectivamente.
	 *
	 * @param con   a sess�o de cone��o com o banco
	 * @param query the query
	 * @param sets  the sets
	 * @throws SQLException       the SQL exception
	 * @throws ClassCastException the class cast exception
	 */
	public static void DmlSql(Connection con, String query, Object... sets) throws  SQLException,ClassCastException {
		PreparedStatement ps = con.prepareStatement(query);
		setPrepared(ps, sets);
		ps.executeUpdate();
	}

	/**
	 * Inicia uma nova transaction no caso desativa autoCommit
	 *
	 * @param con the con
	 * @throws SQLException the SQL exception
	 */
	public static void startTransaction(Connection con) throws SQLException {
		if (!con.getAutoCommit())
			rollBack(con);
		con.setAutoCommit(false);
	}

	/**
	 * Executa um commit da ultima operacao e ativa autoCommit = true
	 *
	 * @param con the con
	 * @throws SQLException the SQL exception
	 */
	public static void commit(Connection con) {
		try {
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executa um rollback da ultima opera��o e ativa autoCommit = true
	 *
	 * @param con the con
	 * @throws SQLException the SQL exception
	 */
	public static void rollBack(Connection con) {
		try {
			con.rollback();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}