package net.dev.eazynick.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MySQL {

	public Connection con;
	public String host;
	public String port;
	public String database;
	public String username;
	public String password;
	
	public MySQL(String host, String port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public void connect() {
		if(!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
				System.out.println("[MySQL] Connected successfully to database!");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MySQL] Connection failed!");
			}
		}
	}

	public void disconnect() {
		if(isConnected()) {
			try {
				con.close();
				System.out.println("[MySQL] Connection closed successfully!");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[MySQL] Connection couldn't be closed!");
			}
		}
	}

	public boolean isConnected() {
		return (con == null ? false : true);
	}

	public void update(String sql) {
		checkConnection();
		
		new FutureTask<>(new Runnable() {
			
			@Override
			public void run() {
				try {
					Statement s = con.createStatement();
					s.executeUpdate(sql);
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, 1).run();
	}

	public ResultSet getResult(String qry) {
		checkConnection();
		
		try {
			final FutureTask<ResultSet> task = new FutureTask<ResultSet>(new Callable<ResultSet>() {
				
				@Override
				public ResultSet call() throws Exception {
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(qry);
					
					return rs;
				}
			});
			
			task.run();
		
			return task.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void checkConnection() {
		if(!(isConnected()))
			connect();
	}

	public Connection getConnection() {
		return con;
	}

}