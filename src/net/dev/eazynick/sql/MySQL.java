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
	public String host, port, database, username, password;
	private final String PREFIX = "[MySQL] ";
	
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
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&characterEncoding=utf8&useUnicode=true&interactiveClient=true", username, password);
				
				System.out.println(PREFIX + "Connected to database successfully!");
			} catch (SQLException e) {
				System.out.println(PREFIX + "Connection to database failed (" + e.getMessage() + ")!");
				System.out.println();
				System.out.println(PREFIX + "Connection Properties:");
				System.out.println(PREFIX + " Address: " + host + ":" + port);
				System.out.println(PREFIX + " Database: " + database);
				System.out.println(PREFIX + " Username: " + username);
				System.out.println(PREFIX + " Password: " + password);
			}
		}
	}

	public void disconnect() {
		if(isConnected()) {
			try {
				con.close();
				
				System.out.println(PREFIX + "Connection to database closed successfully!");
			} catch (SQLException e) {
			}
		}
	}

	public boolean isConnected() {
		try {
			return ((con != null) && !(con.isClosed()));
		} catch (SQLException e) {
		}
		
		return false;
	}

	public void update(String sql) {
		checkConnection();
		
		if(isConnected()) {
			new FutureTask<>(new Runnable() {
				
				@Override
				public void run() {
					try {
						Statement s = con.createStatement();
						s.executeUpdate(sql);
						s.close();
					} catch (SQLException e) {
						System.out.println(PREFIX + "An error occured while executing mysql update (" + e.getMessage() + ")!");
					}
				}
			}, 1).run();
		}
	}

	public ResultSet getResult(String qry) {
		checkConnection();
		
		if(isConnected()) {
			try {
				final FutureTask<ResultSet> task = new FutureTask<ResultSet>(new Callable<ResultSet>() {
					
					@Override
					public ResultSet call() throws SQLException {
						Statement s = con.createStatement();
						ResultSet rs = s.executeQuery(qry);
						
						return rs;
					}
				});
				
				task.run();
			
				return task.get();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println(PREFIX + "An error occured while executing mysql query (" + e.getMessage() + ")!");
			}
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