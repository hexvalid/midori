package com.midori.bot;

import com.midori.database.DBAccTools;
import com.midori.database.DBCon;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws InterruptedException, IOException, Engine.LoginError, URISyntaxException, SQLException {
        System.out.println( DigestUtils.sha256Hex("20331396cebe016691c792cf1417efaa4de27f5af9f76bd31c943da13ae8fca6"));


        //20331396cebe016691c792cf1417efaa4de27f5af9f76bd31c943da13ae8fca6
        //a847e1da40dae9bdafaf08985cfb5ff1c38f1930f0c51492796db2170eaadb1b


        System.exit(1);
/*     Login : erkanmdr@gmail.com

    Password : aey38BId2uOyMVWd


    40.114.250.110:1081
*/

        //const maxRpCost = 8


        DBCon.Connect();
        DBAccTools.GetAllAccounts();
        Account account = new Account("erkanmdr@gmail.com", "aey38BId2uOyMVWd", false, "", -180,
                "Mozilla/5.0 (X11; Linux x86_64; rv:71.0) Gecko/20100101 Firefox/71.0", "en-US,en;q=0.5", "off");

        Engine.Login(account);

        Engine.Home(account);

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
