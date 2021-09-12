package ru.geekbrains.server.core;

import java.sql.*;

public class SqlClient {

    private static Connection connection;
    private static Statement statement;

    synchronized static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/java/ru/geekbrains/server/chat.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException |  SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static void disconnect() {
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized static String getNickname(String login, String password) {
//        String query = String.format("select nickname from users where login='%s' and password='%s'", login, password);
        String query = String.format("select nickname from new_users where login='%s' and password='%s'", login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next())
                return set.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private void createTable() throws SQLException {
        statement.executeUpdate("create table if not exists new_users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login STRING," +
                "password STRING," +
                "nickname STRING)");
    }
    private void insert(String login, String pass, String nickname) throws SQLException {
        statement.executeUpdate(String.format("insert into new_users(login, password, nickname) values('%s', '%s', '%s')", login, pass, nickname));
    }
    public static void main(String[] args) {
        SqlClient sqlClient = new SqlClient();
        connect();
        try {
            sqlClient.createTable();
            sqlClient.insert("Sacha", "123111", "Straik" );
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}