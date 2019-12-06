package com.midori.bot;


import com.midori.database.DBAccTools;
import com.midori.ui.Log;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Tasks {
    public static class FPTask implements Runnable {
        Account account;

        public FPTask(Account account) {
            this.account = account;
        }

        public void run() {
            this.account.set_Status("Running");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (account.isReadyForRoll()) {
                        this.account.set_Status("Homing");
                        Engine.Home(account, null, Engine.URL.home);
                        this.account.set_Status("Rolling");
                        Engine.Roll(account);
                        System.out.println("OK");
                    } else {
                        this.account.set_Status("Waiting");
                        Thread.sleep(account.remaingMillisForRoll());
                    }

                } catch (InterruptedException e) {
                    this.account.set_Status("Stopped");
                    Thread.currentThread().interrupt();
                    return;
                } catch (IOException e) {
                    this.account.set_Status("Error: " + e.getMessage());
                    Log.Print(Log.t.WRN, account.logDomain() + "Account got unknown error. Will be wait 10 seconds...");
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException ex) {
                        this.account.set_Status("Stopped");
                        Thread.currentThread().interrupt();
                        return;
                    }
                } catch (Engine.RollError e) {
                    this.account.set_Status("Error: " + e.getMessage());
                    Log.Print(Log.t.ERR, account.logDomain() + e.getMessage());
                    if (e.getMessage().contains("Captcha is incorrect")) {
                        System.out.println("ERR");
                        Log.Print(Log.t.WRN, account.logDomain() + "Account got roll (captcha) error. Trying again...");
                    } else {
                        Log.Print(Log.t.WRN, account.logDomain() + "Account got roll error. Will be wait 1 minutes...");
                        try {
                            Thread.sleep(1 * 60 * 1000);
                        } catch (InterruptedException ex) {
                            this.account.set_Status("Stopped");
                            Thread.currentThread().interrupt();
                            return;
                        }
                        e.printStackTrace();
                    }
                } finally {
                    try {
                        DBAccTools.UpdateAccountForRoll(this.account);
                    } catch (SQLException e) {
                        Log.Print(Log.t.ERR, account.logDomain() + e.getMessage());
                        e.printStackTrace();
                        this.account.stopExecuter();
                    }
                }
            }
        }
    }


}
