package com.midori.bot;

import com.midori.ui.Log;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    final private static DecimalFormat df = new DecimalFormat("0");

    //its mean valid socks address
    public static boolean CheckProxyTypo(String address) {
        return (StringUtils.countMatches(address, '.') == 3 &&
                StringUtils.countMatches(address, ':') == 2 &&
                StringUtils.countMatches(address, '/') == 2);
    }

    public static String safeDouble(double d) {
        df.setMaximumFractionDigits(340);
        return df.format(d);
    }
}
