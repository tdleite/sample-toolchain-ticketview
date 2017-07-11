package com.ibm.retain.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO {

	private String connectionString = "jdbc:oracle:thin:@localhost:1521:maximo";
	private String user = "ticketview";
	private String password = "ticketview";
	private Connection connection;

	public void connect() throws ClassNotFoundException, SQLException {
		if (connection == null) {
			Class.forName("oracle.jdbc.OracleDriver");
			connection = DriverManager.getConnection(connectionString, user, password);
		}
	}

	public ResultSet execute(String sql, ArrayList<String> params) throws SQLException {
		if (connection != null) {
			PreparedStatement stmt = connection.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					stmt.setString(i+1, params.get(i));
				}
			}
			return stmt.executeQuery();
		}
		return null;
	}

	public void commit() throws SQLException {
		if (connection != null) {
			connection.commit();
		}
	}

	public void rollback() throws SQLException {
		if (connection != null) {
			connection.rollback();
		}
	}

	public void disconnect() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

}
