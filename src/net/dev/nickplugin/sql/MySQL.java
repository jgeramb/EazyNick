package net.dev.nickplugin.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			
			PreparedStatement ps;
			
			@Override
			public void run() {
				try {
					ps = con.prepareStatement(sql);

					ps.executeUpdate();
					ps.close();
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
				
				PreparedStatement ps;
				
				@Override
				public ResultSet call() throws Exception {
					ps = con.prepareStatement(qry);
					
					return ps.executeQuery();
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
		if(!(isConnected())) {
			connect();
		}
	}

	public Connection getConnection() {
		return con;
	}

}