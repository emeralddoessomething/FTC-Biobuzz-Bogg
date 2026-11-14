package org.firstinspires.ftc.teamcode.Utils;

public class Round {
    public static double round(double input, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(input * scale) / scale;
    }
}
