package com.midori.bot;

import com.midori.ui.Log;
import org.apache.commons.lang3.StringUtils;

public class Utils {


    //its mean valid socks address
    public static boolean CheckProxyTypo(String address) {
        return (StringUtils.countMatches(address, '.') == 3 &&
                StringUtils.countMatches(address, ':') == 2 &&
                StringUtils.countMatches(address, '/') == 2);
    }
}
