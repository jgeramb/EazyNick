package net.dev.eazynick.sql;

import java.sql.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class MySQL {

	private Logger logger;
	private Connection connection;
	private String host, port, database, username, password;
	private final String PREFIX = "[MySQL] ";
	
	public MySQL(String host, String port, String database, String username, String password) {
		this.logger = Bukkit.getLogger();
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
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&characterEncoding=utf8&useUnicode=true&interactiveClient=true&useSSL=false", username, password);
				
				logger.info(PREFIX + "Connected to database successfully!");
			} catch (SQLException ex) {
				logger.log(Level.WARNING, PREFIX + "Connection to database failed!");
				logger.log(Level.WARNING, PREFIX);
				logger.log(Level.WARNING, PREFIX + "Connection Properties:");
				logger.log(Level.WARNING, PREFIX + " Address: " + host + ":" + port);
				logger.log(Level.WARNING, PREFIX + " Database: " + database);
				logger.log(Level.WARNING, PREFIX + " Username: " + username);
				
				String censoredPassword = "";
				
				for (int i = 0; i < password.length(); i++)
					censoredPassword += "*";
				
				logger.log(Level.WARNING, PREFIX + " Password: " + censoredPassword);
				logger.log(Level.WARNING, PREFIX);
				logger.log(Level.WARNING, PREFIX + "---------- Stack trace START ----------");
				logger.log(Level.WARNING, "");
				
				ex.printStackTrace();
				
				logger.log(Level.WARNING, "");
				logger.log(Level.WARNING, PREFIX + "---------- Stack trace  END  ----------");
			}
		}
	}

	public void disconnect() {
		//Check if connection is open
		if(isConnected()) {
			try {
				//Close connection
				connection.close();
				
				logger.info(PREFIX + "Connection to database closed successfully!");
			} catch (SQLException ex) {
				logger.log(Level.WARNING, PREFIX + "Could not close Connection to database!");
			}
		}
	}

	public boolean isConnected() {
		try {
			return ((connection != null) && !(connection.isClosed()));
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
					Statement s = connection.createStatement();
					s.executeUpdate(sql);
					s.close();
				} catch (SQLException ex) {
					String msg = ex.getMessage();
					
					if(msg.contains("The driver has not received any packets from the server.") || msg.contains("The last packet successfully received from the server was")) {
						try {
							connection.close();
						} catch (SQLException ignore) {
						}
						
						connection = null;
						
						connect();
					} else
						logger.log(Level.WARNING, PREFIX + "An error occured while executing mysql update (" + msg + ")!");
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
							Statement s = connection.createStatement();
							ResultSet rs = s.executeQuery(qry);
							
							return rs;
						} catch (SQLException ex) {
							String msg = ex.getMessage();
							
							if(msg.contains("The driver has not received any packets from the server.") || msg.contains("The last packet successfully received from the server was")) {
								try {
									connection.close();
								} catch (SQLException ignore) {
								}
								
								connection = null;
								
								connect();
							} else
								logger.log(Level.WARNING, PREFIX + "An error occured while executing mysql update (" + msg + ")!");
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
		return connection;
	}

}