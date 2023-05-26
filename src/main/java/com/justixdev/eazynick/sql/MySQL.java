package com.justixdev.eazynick.sql;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class MySQL {

    private static final String PREFIX = "[DB] ";
    private static final Logger LOGGER = Bukkit.getLogger();

    private Connection connection;
    private final String host, port, database, username, password;

    public void connect() {
        // Make sure no connection is open
        if(isConnected()) return;

        try {
            // Open connection
            connection = DriverManager.getConnection(
                    "jdbc:mysql://"
                            + host + ":" + port
                            + "/" + database
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
        if (!this.isConnected())
            return;

        try {
            // Close connection
            connection.close();

            LOGGER.info(PREFIX + "Connection to database closed successfully!");
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, PREFIX + "Could not close connection to database!");
        }

        this.connection = null;
    }

    public boolean isConnected() {
        if(this.connection == null)
            return false;

        try {
            return !this.connection.isClosed();
        } catch (SQLException ignore) {
            return false;
        }
    }

    private void setParameters(PreparedStatement statement, Object... parameters) {
        for (int i = 0; i < parameters.length; i++) {
            int index = i + 1;

            try {
                Object parameter = parameters[i];

                if (parameter == null)
                    statement.setNull(index, Types.VARCHAR);
                else if (parameter instanceof String)
                    statement.setString(index, (String) parameter);
                else if (parameter instanceof Integer)
                    statement.setInt(index, (Integer) parameter);
                else if (parameter instanceof Long)
                    statement.setLong(index, (Long) parameter);
                else if (parameter instanceof Float)
                    statement.setFloat(index, (Float) parameter);
                else if (parameter instanceof Double)
                    statement.setDouble(index, (Double) parameter);
                else if (parameter instanceof Array)
                    statement.setArray(index, (Array) parameter);
                else if (parameter instanceof Date)
                    statement.setDate(index, (Date) parameter);
                else if (parameter instanceof Boolean)
                    statement.setBoolean(index, (Boolean) parameter);
                else
                    statement.setString(index, parameter.toString());
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not set parameter at position " + index + ": " + ex.getMessage());
            }
        }
    }

    public void update(String sql, Object... parameters) {
        if(!this.isConnected())
            return;

        new FutureTask<>(() -> {
            PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.setParameters(statement, parameters);

            try {
                statement.executeUpdate();
            } catch (SQLException ex) {
                System.err.println("Could not execute update \"" + sql + "\": " + ex.getMessage());
            }

            return null;
        }).run();
    }

    public ResultSet updateAndGet(String sql, Object... parameters) throws SQLException {
        if(this.isConnected()) {
            FutureTask<ResultSet> task = new FutureTask<>(() -> {
                PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                this.setParameters(statement, parameters);
                statement.executeUpdate();

                return statement.getGeneratedKeys();
            });
            task.run();

            try {
                return task.get();
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println("Could not execute update \"" + sql + "\": " + ex.getMessage());
                return null;
            }
        }

        throw new SQLException("not connected");
    }

    public ResultSet getResult(String query, Object... parameters) throws SQLException {
        if(this.isConnected()) {
            FutureTask<ResultSet> task = new FutureTask<>(() -> {
                PreparedStatement statement = this.connection.prepareStatement(query);
                this.setParameters(statement, parameters);

                return statement.executeQuery();
            });
            task.run();

            try {
                return task.get();
            } catch (InterruptedException | ExecutionException ex) {
                System.err.println("Could not execute query \"" + query + "\": " + ex.getMessage());
                return null;
            }
        }

        throw new SQLException("not connected");
    }

}