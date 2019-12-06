package com.midori.database;

import com.midori.ui.Log;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCon {
    final static String sqlInitAccounts = "create table accounts(id integer not null constraint accounts_pk primary key, email text, password text, btcAddress text, balance double, rewardPoints integer, referrer integer, lastFPDate date, fingerprint text, fingerprint2 int, timeOffset int, loginDate date, signUpDate date, signUpIP text, fpPlayed int, fpBonusReqCompleted double, fpRpCost integer, emailConfirmed boolean, stats text, disableLottery boolean, tfaEnabled boolean, tfaSecret text, useRPForFP boolean, headerUserAgent text, headerAcceptLanguage text, lastV3Date date, fpLatencyTimes text, boosts text, proxy text, cookieStore blob, logs text);";
    final static String sqlInitSettings1 = "create table settings(key text not null,value text);";
    final static String sqlInitSettings2 = "create unique index settings_key_uindex on settings (key);";
    final static String sqlInsertAccount = "insert into accounts(id, email, password, btcAddress, balance, rewardPoints, referrer, lastFPDate, fingerprint, fingerprint2, timeOffset, loginDate, signUpDate, signUpIP, fpPlayed, fpBonusReqCompleted, fpRpCost, emailConfirmed, stats, disableLottery, tfaEnabled, tfaSecret, useRPForFP, headerUserAgent, headerAcceptLanguage, lastV3Date, fpLatencyTimes, boosts, proxy, cookieStore, logs) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    final static String sqlGetAllAccounts = "select * from accounts";
    final static String sqlUpdateAccountForRoll = "update accounts set balance = ?, rewardPoints = ?, lastFPDate = ?, fpPlayed = ?, fpBonusReqCompleted = ?, fpRpCost  = ?, emailConfirmed = ?, stats = ?, disableLottery = ?, tfaEnabled = ?, lastV3Date = ?, boosts = ?, cookieStore = ? where id = ?;";
    final static String sqlTfaSecret = "update accounts set tfaSecret = ? where id = ?;";
    final static String sqlLastFPDate = "update accounts set lastFPDate = ? where id = ?;";
    final static String sqlGetSetting = "select value from settings where key = ?";
    final static String sqlSetSetting = "replace into settings (key, value) values(?, ?);";

    final private static String fileName = "midori.db";
    final private static String url = "jdbc:sqlite:" + fileName;

    static Connection conn;

    public static void Connect() throws SQLException {
        boolean exists = Files.exists(Paths.get(fileName));
        conn = DriverManager.getConnection(url);
        if (!exists) {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlInitAccounts);
            stmt.execute(sqlInitSettings1);
            stmt.execute(sqlInitSettings2);
            stmt.close();
            DBSetTools.SaveSettingsToDB();
            Log.Print(Log.t.DBG, "New database created");
        }
        Log.Print(Log.t.SCS, "Connected to database");
    }

    public static void Disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }


}
