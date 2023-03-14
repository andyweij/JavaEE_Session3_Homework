package com.training.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnectionFactory {
		
	/**
	 * 取得 MySql 資料庫連線
	 * @return Connection
	 */
	public static Connection getMySqlDBConnection() {
		Connection connection = null;
		try {
			Context ctx = new InitialContext();
			// Context loop JNDI DB Resource
			// 名稱須與 server.xml Resource name(jdbc/mySql) 相同
			DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/mySql");
			connection = dataSource.getConnection();			
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
	/**
	 * 取得 Oracle 資料庫連線
	 * @return Connection
	 */
	public static Connection getOracleDBConnection() {
		Connection connection = null;
		try {
			Context ctx = new InitialContext();
			// Context loop JNDI DB Resource
			// 名稱須與 server.xml Resource name(jdbc/oracle) 相同
			DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
			connection = dataSource.getConnection();			
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
}
