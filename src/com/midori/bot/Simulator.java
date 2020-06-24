package com.midori.bot;

public class Simulator {
    public static void main(String[] args) {

        int rp_bonus_remain = 0;
        int rp_bonus_point = 0;
        int fp_bonus_remain = 0;
        int fp_bonus_point = 0;

        boolean last_fp_with_rp = false;



        int rp = 0, satoshi = 0;
        int x = 0;

        for (int i = 1; i <= 30; i++) {
            for (int j = 1; j <= 24; j++) {

                // buy
                if (rp_bonus_remain <= 0) {

                    if (rp >= 120) {
                        rp_bonus_point = 10;
                        rp_bonus_remain = 24;
                        rp -= 120;
                    } else if (rp >= 12) {
                        rp_bonus_point = 1;
                        rp_bonus_remain = 24;
                        rp -= 12;
                    }

                    if (rp >= 320) {
                        fp_bonus_point = 25;
                        fp_bonus_remain = 24;
                        rp -= 320;
                    }


                }

                // use
                if (rp_bonus_remain > 0) {
                    rp += rp_bonus_point;
                    rp_bonus_remain--;
                }
                if (fp_bonus_remain > 0) {
                    satoshi += fp_bonus_point;
                    fp_bonus_remain--;
                }

                rp += 4;
                satoshi += 25;


                if (!last_fp_with_rp && fp_bonus_remain > 0 && rp_bonus_remain > 0) {
                    rp -= 2;
                    last_fp_with_rp = true;
                    System.out.println("xxx");
                } else {
                    last_fp_with_rp = false;
                }

                //Day #30, Roll: #24. SATOSHI: 28800, RP: 582, Roll count: 719. COST: 1.3248
                //Day #30, Roll: #24. SATOSHI: 28200, RP: 494, Roll count: 719. COST: 0.94943999999999

                System.out.println("Day #" + i + ", Roll: #" + j + ". SATOSHI: " + satoshi + ", RP: " + rp + ", Roll count: " + x);
                x++;
            }

        }
    }
}
