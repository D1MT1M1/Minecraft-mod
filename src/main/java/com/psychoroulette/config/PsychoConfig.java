package com.psychoroulette.config;

public class PsychoConfig {
    // 5 minutes = 300 seconds = 6000 ticks (changeable via /psychoroulette interval <sec>)
    public static int cycleDurationTicks = 20 * 300;
    public static int calmDurationTicks = 20 * 60;
    public static double hiddenDiagnosisChance = 0.12;
    public static boolean enableOverdriveCombo = true;
    public static boolean giveSurvivalRewards = true;
}
