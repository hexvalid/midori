package com.midori.bot;

import com.midori.database.DBAccTools;
import com.midori.database.DBCon;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) throws InterruptedException, IOException, Engine.LoginError, URISyntaxException, SQLException, GeneralSecurityException {

        System.out.println(OTP.generatePIN("MJ7NSD6KEFS6BC6X"));
        System.out.println(RandomStringUtils.random(32, Rune.baseBytes));
        System.out.println(RandomUtils.nextLong(1111111111L, 9999999999L));
        System.exit(1);
/*     Login : erkanmdr@gmail.com

    Password : aey38BId2uOyMVWd


    40.114.250.110:1081
*/

        //const maxRpCost = 8


        DBCon.Connect();
        DBAccTools.GetAllAccounts();
        Account account = new Account("erkanmdr@gmail.com", "aey38BId2uOyMVWd", 0, false, "", -180,
                "Mozilla/5.0 (X11; Linux x86_64; rv:71.0) Gecko/20100101 Firefox/71.0", "en-US,en;q=0.5", "off");

        Engine.Login(account, false);

        Engine.Home(account, null, Engine.URL.home);

        DBAccTools.InsertAccount(account);

        DBCon.Disconnect();

        Engine.PurchaseLottTickets(account, 1);





     /*   Engine.Login(account);
        System.out.println(Base64.getEncoder().encodeToString(SerializationUtils.serialize(account.cookieStore)));*/


        //per 66 sec loop:
        //https://freebitco.in/stats_new_private/?u=12283987&p=94a022cabf8d50c63d1f7f2b7e89b25e97afcc040bd0ff2b9c7e0162b84426b3&f=user_stats&csrf_token=XBBulai7i2AD


        //per 15 min loop:
        //https://freebitco.in/cf_stats_public/?f=updating2&csrf_token=XBBulai7i2AD


    }

}
