package com.midori.bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Boost {

    public static List<Boost> BOOSTS = new ArrayList<Boost>(
            Arrays.asList(
                    new Boost("fp_bonus_1000", Type.FP, 3200, "1000% BONUS"),
                    new Boost("fp_bonus_500", Type.FP, 1600, "500% BONUS"),
                    new Boost("fp_bonus_100", Type.FP, 320, "100% BONUS"),
                    new Boost("fp_bonus_50", Type.FP, 160, "50% BONUS"),
                    new Boost("fp_bonus_10", Type.FP, 32, "10% BONUS"),
                    new Boost("free_points_100", Type.RP, 1200, "100 REWARD POINTS / ROLL"),
                    new Boost("free_points_50", Type.RP, 600, "50 REWARD POINTS / ROLL"),
                    new Boost("free_points_25", Type.RP, 300, "25 REWARD POINTS / ROLL"),
                    new Boost("free_points_10", Type.RP, 120, "10 REWARD POINTS / ROLL"),
                    new Boost("free_points_1", Type.RP, 12, "1 REWARD POINT / ROLL")
            ));


    public enum Type {RP, FP}

    public String id;
    public Type type;
    public int cost;
    public String name;

    public Boost(String id, Type type, int cost, String name) {
        this.id = id;
        this.type = type;
        this.cost = cost;
        this.name = name;

    }
}

