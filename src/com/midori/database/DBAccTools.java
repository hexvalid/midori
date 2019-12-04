package com.midori.database;

import com.midori.bot.Account;
import org.apache.commons.lang3.SerializationUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBAccTools {
    public static void InsertAccount(Account account) throws SQLException {
        PreparedStatement pstmt = DBCon.conn.prepareStatement(DBCon.sqlInsertAccount);
        pstmt.setInt(1, account.idProperty().get());
        pstmt.setString(2, account.emailProperty().get());
        pstmt.setString(3, account.password);
        pstmt.setString(4, account.btcAddress);
        pstmt.setDouble(5, account.balanceProperty().get());
        pstmt.setInt(6, account.rewardPointsProperty().get());
        pstmt.setInt(7, account.referrerProperty().get());
        pstmt.setDate(8, account.lastFPDate);
        pstmt.setString(9, account.fingerprint);
        pstmt.setLong(10, account.fingerprint2);
        pstmt.setInt(11, account.timeOffset);
        pstmt.setDate(12, account.loginDate);
        pstmt.setDate(13, account.signUpDate);
        pstmt.setString(14, account.signUpIP);
        pstmt.setInt(15, account.fpPlayedProperty().get());
        pstmt.setDouble(16, account.fpBonusReqCompletedProperty().get());
        pstmt.setInt(17, account.fpRpCost);
        pstmt.setBoolean(18, account.emailConfirmed);
        pstmt.setString(19, account.stats);
        pstmt.setBoolean(20, account.disableLottery);
        pstmt.setBoolean(21, account.tfaEnabled);
        pstmt.setString(22, account.tfaSecret);
        pstmt.setBoolean(23, account.useRPForFP);
        pstmt.setString(24, account.headerUserAgent);
        pstmt.setString(25, account.headerAcceptLanguage);
        pstmt.setString(26, account.sleepTimes);
        pstmt.setString(27, account.fpLatencyTimes);
        pstmt.setString(28, account.boostsProperty().get());
        pstmt.setString(29, account.proxyProperty().get());
        pstmt.setBytes(30, SerializationUtils.serialize(account.cookieStore));
        pstmt.setString(31, account.logs);
        pstmt.executeUpdate();
    }

    public static List<Account> GetAllAccounts() throws SQLException {

        List<Account> list = new ArrayList<>();
        Statement stmt = DBCon.conn.createStatement();
        ResultSet rs = stmt.executeQuery(DBCon.sqlGetAllAccounts);

        while (rs.next()) {
            Account account = new Account();
            account.setID(rs.getInt("id"));
            account.setEmail(rs.getString("email"));
            account.password = rs.getString("password");
            account.btcAddress = rs.getString("btcAddress");
            account.setBalance(rs.getDouble("balance"));
            account.setRewardPoints(rs.getInt("rewardPoints"));
            account.setReferrer(rs.getInt("referrer"));
            account.lastFPDate = rs.getDate("lastFPDate");
            account.fingerprint = rs.getString("fingerprint");
            account.fingerprint2 = rs.getLong("fingerprint2");
            account.timeOffset = rs.getInt("timeOffset");
            account.loginDate = rs.getDate("loginDate");
            account.signUpDate = rs.getDate("signUpDate");
            account.signUpIP = rs.getString("signUpIP");
            account.setFpPlayed(rs.getInt("fpPlayed"));
            account.setFpBonusReqCompleted(rs.getDouble("fpBonusReqCompleted"));
            account.fpRpCost = rs.getInt("fpRpCost");
            account.emailConfirmed = rs.getBoolean("emailConfirmed");
            account.stats = rs.getString("stats");
            account.disableLottery = rs.getBoolean("disableLottery");
            account.tfaEnabled = rs.getBoolean("tfaEnabled");
            account.tfaSecret = rs.getString("tfaSecret");
            account.useRPForFP = rs.getBoolean("useRPForFP");
            account.headerUserAgent = rs.getString("headerUserAgent");
            account.headerAcceptLanguage = rs.getString("headerAcceptLanguage");
            account.sleepTimes = rs.getString("sleepTimes");
            account.fpLatencyTimes = rs.getString("fpLatencyTimes");
            account.setBoosts(rs.getString("boosts"));
            account.setProxy(rs.getString("proxy"));
            account.cookieStore = SerializationUtils.deserialize(rs.getBytes("cookieStore"));
            account.logs = rs.getString("logs");
            list.add(account);
        }
        rs.close();
        return list;
    }


    public static void UpdateAccountForRoll(Account account) throws SQLException {
        PreparedStatement pstmt = DBCon.conn.prepareStatement(DBCon.sqlUpdateAccountForRoll);
        pstmt.setDouble(1, account.balanceProperty().get());
        pstmt.setInt(2, account.rewardPointsProperty().get());
        pstmt.setDate(3, account.lastFPDate);
        pstmt.setInt(4, account.fpPlayedProperty().get());
        pstmt.setDouble(5, account.fpBonusReqCompletedProperty().get());
        pstmt.setInt(6, account.fpRpCost);
        pstmt.setBoolean(7, account.emailConfirmed);
        pstmt.setString(8, account.stats);
        pstmt.setBoolean(9, account.disableLottery);
        pstmt.setBoolean(10, account.tfaEnabled);
        pstmt.setBytes(11, SerializationUtils.serialize(account.cookieStore));
        pstmt.setInt(12, account.idProperty().get());
        pstmt.executeUpdate();
    }

    public static void UpdateTFASecret(Account account) throws SQLException {
        PreparedStatement pstmt = DBCon.conn.prepareStatement(DBCon.sqlTfaSecret);
        pstmt.setString(1, account.tfaSecret);
        pstmt.setInt(2, account.idProperty().get());
        pstmt.executeUpdate();
    }

    public static void UpdateLastFPDate(Account account) throws SQLException {
        PreparedStatement pstmt = DBCon.conn.prepareStatement(DBCon.sqlLastFPDate);
        pstmt.setDate(1, account.lastFPDate);
        pstmt.setInt(2, account.idProperty().get());
        pstmt.executeUpdate();
    }
}
