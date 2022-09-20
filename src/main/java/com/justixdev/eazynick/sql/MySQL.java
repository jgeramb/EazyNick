package com.justixdev.eazynick.sql;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {

	private static final String PREFIX = "[MySQL] ";
	private static final Logger LOGGER = Bukkit.getLogger();
	private Connection connection;
	private final String host, port, database, username, password;

	public MySQL(String host, String port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;

		connect();
	}

	public void connect() {
		// Make sure no connection is open
		if(isConnected()) return;

		try {
			// Open connection
			connection = DriverManager.getConnection(
					"jdbc:mysql://"
							+ host
							+ ":"
							+ port
							+ "/"
							+ database
							+ "?autoReconnect=true&characterEncoding=utf8&useUnicode=true&interactiveClient=true&useSSL=false",
					username,
					password
			);

			LOGGER.info(PREFIX + "Connected to database successfully!");
		} catch (SQLException ex) {
			LOGGER.log(Level.WARNING, PREFIX + "Connection to database failed!");
			LOGGER.log(Level.WARNING, PREFIX);
			LOGGER.log(Level.WARNING, PREFIX + "Connection Properties:");
			LOGGER.log(Level.WARNING, PREFIX + " Address: " + host + ":" + port);
			LOGGER.log(Level.WARNING, PREFIX + " Database: " + database);
			LOGGER.log(Level.WARNING, PREFIX + " Username: " + username);

			StringBuilder censoredPassword = new StringBuilder();

			for (int i = 0; i < password.length(); i++)
				censoredPassword.append("*");

			LOGGER.log(Level.WARNING, PREFIX + " Password: " + censoredPassword);
			LOGGER.log(Level.WARNING, PREFIX);
			LOGGER.log(Level.WARNING, PREFIX + "---------- Stack trace START ----------");
			LOGGER.log(Level.WARNING, "");

			ex.printStackTrace();

			LOGGER.log(Level.WARNING, "");
			LOGGER.log(Level.WARNING, PREFIX + "---------- Stack trace  END  ----------");
		}
	}

	public void disconnect() {
		//Check if connection is open
		if(!(isConnected())) return;

		try {
			//Close connection
			connection.close();

			LOGGER.info(PREFIX + "Connection to database closed successfully!");
		} catch (SQLException ex) {
			LOGGER.log(Level.WARNING, PREFIX + "Could not close Connection to database!");
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
		// Check if connection is open
		if(!(isConnected())) return;

		new FutureTask<>(() -> {
			try {
				// Execute update
				Statement s = connection.createStatement();
				s.executeUpdate(sql);
				s.close();
			} catch (SQLException ex) {
				String msg = ex.getMessage();

				if(msg.contains("The driver has not received any packets from the server.")
						|| msg.contains("The last packet successfully received from the server was")) {
					try {
						connection.close();
					} catch (SQLException ignore) {
					}

					connection = null;

					connect();
				} else
					LOGGER.log(
							Level.WARNING,
							PREFIX + "An error occurred while executing mysql update (" + msg + ")!"
					);
			}
		}, 1).run();
	}

	public ResultSet getResult(String query) {
		// Check if connection is open
		if(!(isConnected())) return null;

		try {
			final FutureTask<ResultSet> task = new FutureTask<>(() -> {
				try {
					// Execute query
					return connection.createStatement().executeQuery(query);
				} catch (SQLException ex) {
					String msg = ex.getMessage();

					if (msg.contains("The driver has not received any packets from the server.")
							|| msg.contains("The last packet successfully received from the server was")) {
						try {
							connection.close();
						} catch (SQLException ignore) {
						}

						connection = null;

						connect();
					} else
						LOGGER.log(
								Level.WARNING,
								PREFIX + "An error occurred while executing mysql update (" + msg + ")!"
						);
				}

				return null;
			});

			task.run();

			return task.get();
		} catch (InterruptedException | ExecutionException ignore) {
		}
		
		return null;
	}

	public Connection getConnection() {
		return connection;
	}

}