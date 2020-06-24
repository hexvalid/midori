package com.midori.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSetTools {
    public static String SET_PROXY_USERNAME = "midori_user";
    public static String SET_PROXY_PASSWORD = "midori_pass";
    public static String SET_ANTICAPTCHA_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static String SET_AI_SERVER = "127.0.0.1:4200";


    public static void SaveSettingsToDB() throws SQLException {
        System.out.println(SET_PROXY_USERNAME);
        setSet("SET_PROXY_USERNAME", SET_PROXY_USERNAME);
        setSet("SET_PROXY_PASSWORD", SET_PROXY_PASSWORD);
        setSet("SET_ANTICAPTCHA_KEY", SET_ANTICAPTCHA_KEY);
        setSet("SET_AI_SERVER", SET_AI_SERVER);

    }

    public static void GetSettingsFromDB() throws SQLException {
        SET_PROXY_USERNAME = getSet("SET_PROXY_USERNAME");
        SET_PROXY_PASSWORD = getSet("SET_PROXY_PASSWORD");
        SET_ANTICAPTCHA_KEY = getSet("SET_ANTICAPTCHA_KEY");
        SET_AI_SERVER = getSet("SET_AI_SERVER");
    }


    private static void setSet(String key, String value) throws SQLException {
        PreparedStatement stmt = DBCon.conn.prepareStatement(DBCon.sqlSetSetting);
        stmt.setString(1, key);
        stmt.setString(2, value);
        stmt.executeUpdate();
    }

    private static String getSet(String key) throws SQLException {
        String value = "";
        PreparedStatement stmt = DBCon.conn.prepareStatement(DBCon.sqlGetSetting);
        stmt.setString(1, key);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            value = rs.getString("value");
        }
        rs.close();
        return value;
    }
}
