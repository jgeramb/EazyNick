package net.dev.eazynick.sql;

import java.sql.*;
import java.util.concurrent.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;

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
		//Make sure no connection is open
		if(!(isConnected())) {
			try {
				//Open connection
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&characterEncoding=utf8&useUnicode=true&interactiveClient=true", username, password);
				
				Bukkit.getLogger().info(PREFIX + "Connected to database successfully!");
			} catch (SQLException ex) {
				Bukkit.getLogger().log(Level.WARNING, PREFIX + "Connection to database failed!");
				Bukkit.getLogger().log(Level.WARNING, PREFIX);
				Bukkit.getLogger().log(Level.WARNING, PREFIX + "Connection Properties:");
				Bukkit.getLogger().log(Level.WARNING, PREFIX + " Address: " + host + ":" + port);
				Bukkit.getLogger().log(Level.WARNING, PREFIX + " Database: " + database);
				Bukkit.getLogger().log(Level.WARNING, PREFIX + " Username: " + username);
				
				String censoredPassword = "";
				
				for (int i = 0; i < password.length(); i++)
					censoredPassword += "*";
				
				Bukkit.getLogger().log(Level.WARNING, PREFIX + " Password: " + censoredPassword);
				Bukkit.getLogger().log(Level.WARNING, PREFIX);
				Bukkit.getLogger().log(Level.WARNING, PREFIX + "---------- Stack trace START ----------");
				Bukkit.getLogger().log(Level.WARNING, "");
				
				ex.printStackTrace();
				
				Bukkit.getLogger().log(Level.WARNING, "");
				Bukkit.getLogger().log(Level.WARNING, PREFIX + "---------- Stack trace  END  ----------");
			}
		}
	}

	public void disconnect() {
		//Check if connection is open
		if(isConnected()) {
			try {
				//Close connection
				con.close();
				
				Bukkit.getLogger().info(PREFIX + "Connection to database closed successfully!");
			} catch (SQLException ex) {
				Bukkit.getLogger().log(Level.WARNING, PREFIX + "Could not close Connection to database!");
			}
		}
	}

	public boolean isConnected() {
		try {
			return ((con != null) && !(con.isClosed()));
		} catch (SQLException ignore) {
		}
		
		return false;
	}

	public void update(String sql) {
		//Check if connection is open
		if(isConnected()) {
			new FutureTask<>(() -> {
				try {
					//Execute update
					Statement s = con.createStatement();
					s.executeUpdate(sql);
					s.close();
				} catch (SQLException ex) {
					String msg = ex.getMessage();
					
					if(msg.contains("The driver has not received any packets from the server.")) {
						try {
							con.close();
						} catch (SQLException ex1) {
						}
						
						con = null;
						
						connect();
					} else
						Bukkit.getLogger().log(Level.WARNING, PREFIX + "An error occured while executing mysql update (" + msg + ")!");
				}
			}, 1).run();
		}
	}

	public ResultSet getResult(String qry) {
		//Check if connection is open
		if(isConnected()) {
			try {
				final FutureTask<ResultSet> task = new FutureTask<ResultSet>(new Callable<ResultSet>() {
					
					@Override
					public ResultSet call() {
						
						try {
							//Execute query
							Statement s = con.createStatement();
							ResultSet rs = s.executeQuery(qry);
							
							return rs;
						} catch (SQLException ex) {
							String msg = ex.getMessage();
							
							if(msg.contains("The driver has not received any packets from the server.")) {
								try {
									con.close();
								} catch (SQLException ex1) {
								}
								
								con = null;
								
								connect();
							} else
								Bukkit.getLogger().log(Level.WARNING, PREFIX + "An error occured while executing mysql update (" + msg + ")!");
						}
						
						return null;
					}
				});
				
				task.run();
			
				return task.get();
			} catch (InterruptedException | ExecutionException ex) {
			}
		}
		
		return null;
	}

	public Connection getConnection() {
		return con;
	}

}