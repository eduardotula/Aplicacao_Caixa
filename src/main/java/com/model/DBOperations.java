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
	 * @param sets array contendo parametros á serem inseridos na query
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException       the SQL exception
	 */
	private static void setPrepared(PreparedStatement ps, Object... sets) throws ClassCastException, SQLException {
		if (ps == null || ps.isClosed())
			throw new SQLException("PreparedStatement is closed or null");

		if (sets != null && sets.length > 0) {
			int co = 1;
			for (int i = 0; i < sets.length; i++) {
				if (sets[i] instanceof LocalDate) {
					ps.setDate(co, java.sql.Date.valueOf((LocalDate) sets[i]));
				} else if (sets[i] instanceof LocalTime) {
					ps.setTime(co, java.sql.Time.valueOf((LocalTime) sets[i]));
				} else {
					ps.setObject(i, sets[i]);
				}
				co++;
			}
		}
	}

	/**
	 * Append qualquer tabela.
	 *
	 * @param ps          the {@link PreparedStatement}
	 * @param tableModels o modelo da tabela que será populada
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
	 * @param query       á ser executada
	 * @param tableModels o modelo da tabela que será populada
	 * @param sets        array contendo parametros á serem inseridos na query
	 * @throws SQLException
	 * @throws ClassCastException
	 * @see DefaultModels
	 */
	public static void appendAnyTable(Connection con, String query, DefaultModels tableModels, Object... sets) throws ClassCastException, SQLException {
			PreparedStatement ps = con.prepareStatement(query);
			setPrepared(ps, sets);
			appendAnyTable(ps, tableModels);
	}

	/**
	 * Select sql rs.
	 *
	 * @param con   the con
	 * @param query the query
	 * @param sets  array contendo parametros á serem inseridos na query
	 * @return the result set
	 * @throws SQLException       the SQL exception
	 * @throws ClassCastException the class cast exception
	 */
	public static ResultSet selectSqlRs(Connection con, String query, Object... sets)
			throws SQLException, ClassCastException {
		PreparedStatement ps = con.prepareStatement(query);
		setPrepared(ps, sets);
		return ps.executeQuery();
	}

	/**
	 * <p>
	 * Realiza uma busca no banco utilizando o parametro query um array de 2
	 * dimensões será retornado, neste array esta contido varios tipos de objetos
	 * será necessario um processamento extra para realizar um cast no array ou um
	 * conhecimento previo dos tipo de objetos contido neste array Eg: Se uma query
	 * for realizada "SELECT ID,NOME FROM TABELA" um array Object será retornado,
	 * mas não é possível realizar um cast String[] s = (String[]) Object[] pois
	 * este metodo cria uma array de objetos genericos será necessario realizar um
	 * cast individualmente nos objetos
	 * 
	 * <p>
	 * Os tipos {@link Date}, {@link Time} e {@link BigDecimal} serão convertidos
	 * para: {@link LocalDate}, {@link LocalTime} e {@link Double} respectivamente
	 *
	 * 
	 * @param con           o objeto de conexão
	 * @param query         a query a ser executada
	 * @param sets          array contendo parametros á serem inseridos na query
	 * @param quantiColunas a quantidade de colunas contida na query
	 * @return Object[][] o array de objetos generico
	 * @throws SQLException
	 * @throws ClassCastException
	 */
	public static Object[][] selectSql2Dimen(Connection con, String query, int quantiColunas, Object... sets)
			throws SQLException, ClassCastException {
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
	 * @param <T>   the generic type
	 * @param con   a sessão de coneção com o banco
	 * @param query query
	 * @param sets  array contendo parametros á serem inseridos na query
	 * @param type  o tipo do array de objetos a ser retornado
	 * @return the object[]
	 * @throws SQLException       the SQL exception
	 * @throws ClassCastException the class cast exception
	 */
	public static <T> T[] selectSql1Dimen(Connection con, String query, T[] type, Object... sets)
			throws SQLException, ClassCastException {
		return selectSqlList(con, query, sets).toArray(type);
	}

	/**
	 * Retorna uma lista contendo os dados buscados na query, esta lista retorna um
	 * objeto generico e pode retornar uma lista que não contem elementos
	 *
	 * @param con   the con
	 * @param query the query
	 * @param sets  the sets
	 * @return the list
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException       the SQL exception
	 */
	public static List<Object> selectSqlList(Connection con, String query, Object... sets)
			throws ClassCastException, SQLException {
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
	 * Executa operações DML como (INSERT, UPDATE or DELETE)
	 *
	 * @param con   a sessão de coneção com o banco
	 * @param query the query
	 * @param sets  the sets
	 * @throws ClassCastException the class cast exception
	 * @throws SQLException       the SQL exception
	 */
	public static void DmlSql(Connection con, String query, Object... sets) throws ClassCastException, SQLException {
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
	 * Executa um rollback da ultima operação e ativa autoCommit = true
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