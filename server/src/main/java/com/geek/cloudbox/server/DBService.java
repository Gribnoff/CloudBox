package com.geek.cloudbox.server;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DBService {
    private static Connection connection;
    private static Statement state;

    static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
        state = connection.createStatement();
    }

    static void disconnect() throws SQLException {
        connection.close();
    }

    static String getNameByLoginAndPass(String login, String pass) throws SQLException {
        String result = null;
        ResultSet rs = state.executeQuery(String.format("SELECT username FROM main WHERE login = '%s' AND password = '%s'", login, pass));
        if (rs.next())
            result = rs.getString("username");

        return result;
    }

    static boolean isLoginTaken(String login) throws SQLException {
        return state.executeQuery(String.format("SELECT login FROM main WHERE login = '%s'", login)).next();
    }

    static boolean isNameTaken(String username) throws SQLException {
        return state.executeQuery(String.format("SELECT username FROM main WHERE nickname = '%s'", username)).next();
    }

    static boolean checkPass(String pass) {
        Pattern pattern = Pattern.compile("\\A(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\p{P}\\p{S}]).{8,}\\z");
        Matcher matcher = pattern.matcher(pass);
        return matcher.matches();
    }


    static void addAccount(String login, String username, String pass) throws SQLException {
        state.execute(String.format("INSERT INTO main (login, password, username) " +
                "VALUES ('%s', '%s', '%s')", login, pass, username));
    }
}
