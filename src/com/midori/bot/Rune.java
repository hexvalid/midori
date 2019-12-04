package com.midori.bot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

public class Rune {


    public static String getRandomUserAgent() {
        return useragentList.get(new Random().nextInt(useragentList.size()));
    }

    public static String getRandomAcceptLanguage() {
        return acceptlanguageList.get(new Random().nextInt(acceptlanguageList.size()));
    }


    final public static String defaultAcceptLanguage = "en-US,en;q=0.5";
    final public static String defaultUserAgent = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";
    final public static String baseBytes = "0123456789abcdef";
    final public static String seedBytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final public static String seedFake = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";


    public static HashMap<String, Integer> timeZoneOffsetMap = new HashMap<String, Integer>() {{
        put("Africa/Abidjan", 0);
        put("Africa/Accra", 0);
        put("Africa/Algiers", -60);
        put("Africa/Bissau", 0);
        put("Africa/Cairo", -120);
        put("Africa/Casablanca", -60);
        put("Africa/Ceuta", -60);
        put("Africa/El_Aaiun", -60);
        put("Africa/Johannesburg", -120);
        put("Africa/Juba", -180);
        put("Africa/Khartoum", -120);
        put("Africa/Lagos", -60);
        put("Africa/Maputo", -120);
        put("Africa/Monrovia", 0);
        put("Africa/Nairobi", -180);
        put("Africa/Ndjamena", -60);
        put("Africa/Sao_Tome", 0);
        put("Africa/Tripoli", -120);
        put("Africa/Tunis", -60);
        put("Africa/Windhoek", -120);
        put("America/Adak", 600);
        put("America/Anchorage", 540);
        put("America/Araguaina", 180);
        put("America/Argentina/Buenos_Aires", 180);
        put("America/Argentina/Catamarca", 180);
        put("America/Argentina/Cordoba", 180);
        put("America/Argentina/Jujuy", 180);
        put("America/Argentina/La_Rioja", 180);
        put("America/Argentina/Mendoza", 180);
        put("America/Argentina/Rio_Gallegos", 180);
        put("America/Argentina/Salta", 180);
        put("America/Argentina/San_Juan", 180);
        put("America/Argentina/San_Luis", 180);
        put("America/Argentina/Tucuman", 180);
        put("America/Argentina/Ushuaia", 180);
        put("America/Asuncion", 180);
        put("America/Atikokan", 300);
        put("America/Bahia", 180);
        put("America/Bahia_Banderas", 360);
        put("America/Barbados", 240);
        put("America/Belem", 180);
        put("America/Belize", 360);
        put("America/Blanc-Sablon", 240);
        put("America/Boa_Vista", 240);
        put("America/Bogota", 300);
        put("America/Boise", -420);
        put("America/Cambridge_Bay", -420);
        put("America/Campo_Grande", 240);
        put("America/Cancun", 300);
        put("America/Caracas", 240);
        put("America/Cayenne", 180);
        put("America/Chicago", 360);
        put("America/Chihuahua", -420);
        put("America/Costa_Rica", 360);
        put("America/Creston", -420);
        put("America/Cuiaba", 240);
        put("America/Curacao", 240);
        put("America/Danmarkshavn", 0);
        put("America/Dawson", 480);
        put("America/Dawson_Creek", -420);
        put("America/Denver", -420);
        put("America/Detroit", 300);
        put("America/Edmonton", -420);
        put("America/Eirunepe", 300);
        put("America/El_Salvador", 360);
        put("America/Fort_Nelson", -420);
        put("America/Fortaleza", 180);
        put("America/Glace_Bay", 240);
        put("America/Godthab", 180);
        put("America/Goose_Bay", 240);
        put("America/Grand_Turk", 300);
        put("America/Guatemala", 360);
        put("America/Guayaquil", 300);
        put("America/Guyana", 240);
        put("America/Halifax", 240);
        put("America/Havana", 300);
        put("America/Hermosillo", -420);
        put("America/Indiana/Indianapolis", 300);
        put("America/Indiana/Knox", 360);
        put("America/Indiana/Marengo", 300);
        put("America/Indiana/Petersburg", 300);
        put("America/Indiana/Tell_City", 360);
        put("America/Indiana/Vevay", 300);
        put("America/Indiana/Vincennes", 300);
        put("America/Indiana/Winamac", 300);
        put("America/Inuvik", -420);
        put("America/Iqaluit", 300);
        put("America/Jamaica", 300);
        put("America/Juneau", 540);
        put("America/Kentucky/Louisville", 300);
        put("America/Kentucky/Monticello", 300);
        put("America/La_Paz", 240);
        put("America/Lima", 300);
        put("America/Los_Angeles", 480);
        put("America/Maceio", 180);
        put("America/Managua", 360);
        put("America/Manaus", 240);
        put("America/Martinique", 240);
        put("America/Matamoros", 360);
        put("America/Mazatlan", -420);
        put("America/Menominee", 360);
        put("America/Merida", 360);
        put("America/Metlakatla", 540);
        put("America/Mexico_City", 360);
        put("America/Miquelon", 180);
        put("America/Moncton", 240);
        put("America/Monterrey", 360);
        put("America/Montevideo", 180);
        put("America/Nassau", 300);
        put("America/New_York", 300);
        put("America/Nipigon", 300);
        put("America/Nome", 540);
        put("America/Noronha", 120);
        put("America/North_Dakota/Beulah", 360);
        put("America/North_Dakota/Center", 360);
        put("America/North_Dakota/New_Salem", 360);
        put("America/Ojinaga", -420);
        put("America/Panama", 300);
        put("America/Pangnirtung", 300);
        put("America/Paramaribo", 180);
        put("America/Phoenix", -420);
        put("America/Port-au-Prince", 300);
        put("America/Port_of_Spain", 240);
        put("America/Porto_Velho", 240);
        put("America/Puerto_Rico", 240);
        put("America/Punta_Arenas", 180);
        put("America/Rainy_River", 360);
        put("America/Rankin_Inlet", 360);
        put("America/Recife", 180);
        put("America/Regina", 360);
        put("America/Resolute", 360);
        put("America/Rio_Branco", 300);
        put("America/Santarem", 180);
        put("America/Santiago", 180);
        put("America/Santo_Domingo", 240);
        put("America/Sao_Paulo", 180);
        put("America/Scoresbysund", 60);
        put("America/Sitka", 540);
        put("America/St_Johns", 210);
        put("America/Swift_Current", 360);
        put("America/Tegucigalpa", 360);
        put("America/Thule", 240);
        put("America/Thunder_Bay", 300);
        put("America/Tijuana", 480);
        put("America/Toronto", 300);
        put("America/Vancouver", 480);
        put("America/Whitehorse", 480);
        put("America/Winnipeg", 360);
        put("America/Yakutat", 540);
        put("America/Yellowknife", -420);
        put("Antarctica/Casey", -480);
        put("Antarctica/Davis", -420);
        put("Antarctica/DumontDUrville", -600);
        put("Antarctica/Macquarie", -660);
        put("Antarctica/Mawson", -300);
        put("Antarctica/Palmer", 180);
        put("Antarctica/Rothera", 180);
        put("Antarctica/Syowa", -180);
        put("Antarctica/Troll", 0);
        put("Antarctica/Vostok", -360);
        put("Asia/Almaty", -360);
        put("Asia/Amman", -120);
        put("Asia/Anadyr", -720);
        put("Asia/Aqtau", -300);
        put("Asia/Aqtobe", -300);
        put("Asia/Ashgabat", -300);
        put("Asia/Atyrau", -300);
        put("Asia/Baghdad", -180);
        put("Asia/Baku", -240);
        put("Asia/Bangkok", -420);
        put("Asia/Barnaul", -420);
        put("Asia/Beirut", -120);
        put("Asia/Bishkek", -360);
        put("Asia/Brunei", -480);
        put("Asia/Chita", -540);
        put("Asia/Choibalsan", -480);
        put("Asia/Colombo", -330);
        put("Asia/Damascus", -120);
        put("Asia/Dhaka", -360);
        put("Asia/Dili", -540);
        put("Asia/Dubai", -240);
        put("Asia/Dushanbe", -300);
        put("Asia/Famagusta", -120);
        put("Asia/Gaza", -120);
        put("Asia/Hebron", -120);
        put("Asia/Ho_Chi_Minh", -420);
        put("Asia/Hong_Kong", -480);
        put("Asia/Hovd", -420);
        put("Asia/Irkutsk", -480);
        put("Asia/Jakarta", -420);
        put("Asia/Jayapura", -540);
        put("Asia/Jerusalem", -120);
        put("Asia/Karachi", -300);
        put("Asia/Khandyga", -540);
        put("Asia/Kolkata", -330);
        put("Asia/Krasnoyarsk", -420);
        put("Asia/Kuala_Lumpur", -480);
        put("Asia/Kuching", -480);
        put("Asia/Macau", -480);
        put("Asia/Magadan", -660);
        put("Asia/Makassar", -480);
        put("Asia/Manila", -480);
        put("Asia/Nicosia", -120);
        put("Asia/Novokuznetsk", -420);
        put("Asia/Novosibirsk", -420);
        put("Asia/Omsk", -360);
        put("Asia/Oral", -300);
        put("Asia/Pontianak", -420);
        put("Asia/Pyongyang", -540);
        put("Asia/Qatar", -180);
        put("Asia/Qostanay", -360);
        put("Asia/Qyzylorda", -300);
        put("Asia/Riyadh", -180);
        put("Asia/Sakhalin", -660);
        put("Asia/Samarkand", -300);
        put("Asia/Seoul", -540);
        put("Asia/Shanghai", -480);
        put("Asia/Singapore", -480);
        put("Asia/Srednekolymsk", -660);
        put("Asia/Taipei", -480);
        put("Asia/Tashkent", -300);
        put("Asia/Tbilisi", -240);
        put("Asia/Thimphu", -360);
        put("Asia/Tokyo", -540);
        put("Asia/Tomsk", -420);
        put("Asia/Ulaanbaatar", -480);
        put("Asia/Urumqi", -360);
        put("Asia/Ust-Nera", -600);
        put("Asia/Vladivostok", -600);
        put("Asia/Yakutsk", -540);
        put("Asia/Yekaterinburg", -300);
        put("Asia/Yerevan", -240);
        put("Atlantic/Azores", 60);
        put("Atlantic/Bermuda", 240);
        put("Atlantic/Canary", 0);
        put("Atlantic/Cape_Verde", 60);
        put("Atlantic/Faroe", 0);
        put("Atlantic/Madeira", 0);
        put("Atlantic/Reykjavik", 0);
        put("Atlantic/South_Georgia", 120);
        put("Atlantic/Stanley", 180);
        put("Australia/Brisbane", -600);
        put("Australia/Currie", -660);
        put("Australia/Hobart", -660);
        put("Australia/Lindeman", -600);
        put("Australia/Lord_Howe", -660);
        put("Australia/Melbourne", -660);
        put("Australia/Perth", -480);
        put("Australia/Sydney", -660);
        put("Europe/Amsterdam", -60);
        put("Europe/Andorra", -60);
        put("Europe/Astrakhan", -240);
        put("Europe/Athens", -120);
        put("Europe/Belgrade", -60);
        put("Europe/Berlin", -60);
        put("Europe/Brussels", -60);
        put("Europe/Bucharest", -120);
        put("Europe/Budapest", -60);
        put("Europe/Chisinau", -120);
        put("Europe/Copenhagen", -60);
        put("Europe/Dublin", 0);
        put("Europe/Gibraltar", -60);
        put("Europe/Helsinki", -120);
        put("Europe/Istanbul", -180);
        put("Europe/Kaliningrad", -120);
        put("Europe/Kiev", -120);
        put("Europe/Kirov", -180);
        put("Europe/Lisbon", 0);
        put("Europe/London", 0);
        put("Europe/Luxembourg", -60);
        put("Europe/Madrid", -60);
        put("Europe/Malta", -60);
        put("Europe/Minsk", -180);
        put("Europe/Monaco", -60);
        put("Europe/Moscow", -180);
        put("Europe/Oslo", -60);
        put("Europe/Paris", -60);
        put("Europe/Prague", -60);
        put("Europe/Riga", -120);
        put("Europe/Rome", -60);
        put("Europe/Samara", -240);
        put("Europe/Saratov", -240);
        put("Europe/Simferopol", -180);
        put("Europe/Sofia", -120);
        put("Europe/Stockholm", -60);
        put("Europe/Tallinn", -120);
        put("Europe/Tirane", -60);
        put("Europe/Ulyanovsk", -240);
        put("Europe/Uzhgorod", -120);
        put("Europe/Vienna", -60);
        put("Europe/Vilnius", -120);
        put("Europe/Volgograd", -240);
        put("Europe/Warsaw", -60);
        put("Europe/Zaporozhye", -120);
        put("Europe/Zurich", -60);
        put("Indian/Chagos", -360);
        put("Indian/Christmas", -420);
        put("Indian/Kerguelen", -300);
        put("Indian/Mahe", -240);
        put("Indian/Maldives", -300);
        put("Indian/Mauritius", -240);
        put("Indian/Reunion", -240);
        put("Pacific/Bougainville", -660);
        put("Pacific/Chuuk", -600);
        put("Pacific/Easter", 300);
        put("Pacific/Efate", -660);
        put("Pacific/Galapagos", 360);
        put("Pacific/Gambier", 540);
        put("Pacific/Guadalcanal", -660);
        put("Pacific/Guam", -600);
        put("Pacific/Honolulu", 600);
        put("Pacific/Kosrae", -660);
        put("Pacific/Palau", -540);
        put("Pacific/Pitcairn", 480);
        put("Pacific/Pohnpei", -660);
        put("Pacific/Port_Moresby", -600);
        put("Pacific/Rarotonga", 600);
        put("Pacific/Tahiti", 600);
        put("UTC", 0);
    }};


    public static int timeZoneOffsetSolver(String s) {
        int result = 0;
        for (Map.Entry<String, Integer> entry : timeZoneOffsetMap.entrySet()) {
            if (entry.getKey().equals(s)) {
                result = entry.getValue();
                break;
            }
        }
        return result;
    }

    final private static List<String> acceptlanguageList = Arrays.asList(
            "en-US,en;q=0.5",
            "en-US,en;q=0.9",
            "en-US",
            "en-ca,en;q=0.8,en-us;q=0.6,de-de;q=0.4,de;q=0.2",
            "zh, en-us; q=0.8, en; q=0.6",
            "en-US;q=0.8",
            "tr-TR"
    );

    final private static List<String> useragentList = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 7.1.2; AFTMM Build/NS6265; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/70.0.3538.110 Mobile Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:68.0) Gecko/20100101 Firefox/68.0",
            "Mozilla/5.0 (Android 9; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0",
            "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36 OPR/43.0.2442.991",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 OPR/56.0.3051.52",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Safari/605.1.15",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.2 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 10.0; rv:68.0) Gecko/20100101 Firefox/68.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.2 Safari/605.1.15",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36 OPR/64.0.3417.92",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36 OPR/63.0.3368.107",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.2 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64; rv:68.0) Gecko/20100101 Firefox/68.0",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.1 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.2 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.1 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.1 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:70.0) Gecko/20100101 Firefox/70.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 YaBrowser/19.10.2.195 Yowser/2.5 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:55.0) Gecko/20100101 Firefox/55.0",
            "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:69.0) Gecko/20100101 Firefox/69.0",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 YaBrowser/19.10.3.281 Yowser/2.5 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3835.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0 Waterfox/56.2.14",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36 OPR/64.0.3417.92");
}

