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
                        Engine.Home(account);
                        this.account.set_Status("Rolling");
                        Engine.Roll(account);
                        this.account.set_Status("Updating");
                        DBAccTools.UpdateAccountForRoll(this.account);
                    } else {
                        this.account.set_Status("Waiting");
                        Thread.sleep(account.remaingMillisForRoll());
                    }

                } catch (InterruptedException e) {
                    this.account.set_Status("Stopped");
                    Thread.currentThread().interrupt();
                    return;
                } catch (IOException | URISyntaxException | SQLException | Engine.RollError e) {
                    this.account.set_Status("Error: " + e.getMessage());
                    Log.Print(Log.t.ERR, account.logDomain() +  e.getMessage());
                    e.printStackTrace();
                    this.account.stopExecuter();
                    return;
                }
            }
        }
    }


}
