package org.firstinspires.ftc.teamcode.Utils;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class GoBildaIndicatorLight {
    private final Servo light;
    private Color color;

    public static double redPosition = 0.28;
    public static double orangePosition = 0.3;
    public static double yellowPosition = 0.36;
    public static double greenPosition = 0.5;
    public static double bluePosition = 0.6;
    public static double purplePosition = 0.7;
    public static double whitePosition = 0.9;
    public static double offPosition = 0.0;

    public enum Color {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE,
        WHITE,
        CUSTOM,
        OFF
    }

    public GoBildaIndicatorLight(HardwareMap hardwareMap) {
        String hardwareMapName = "light";

        light = hardwareMap.get(Servo.class, hardwareMapName);
        light.setPosition(offPosition);
        color = Color.OFF;
    }

    public GoBildaIndicatorLight(HardwareMap hardwareMap, String hardwareMapName) {
        light = hardwareMap.get(Servo.class, hardwareMapName);
        light.setPosition(offPosition);
        color = Color.OFF;
    }

    public void setColor(Color color) {
        this.color = color;

        switch (color) {
            case RED:
                light.setPosition(redPosition);
                break;
            case ORANGE:
                light.setPosition(orangePosition);
                break;
            case YELLOW:
                light.setPosition(yellowPosition);
                break;
            case GREEN:
                light.setPosition(greenPosition);
                break;
            case BLUE:
                light.setPosition(bluePosition);
                break;
            case PURPLE:
                light.setPosition(purplePosition);
                break;
            case WHITE:
                light.setPosition(whitePosition);
                break;
            case OFF:
                light.setPosition(offPosition);
                break;
        }
    }

    public void setColor(double position) {
        light.setPosition(position);

        color = Color.CUSTOM;
    }

    public Color getColor() {
        return color;
    }

    public double getPositionFromColor(Color color) {
        switch (color) {
            case RED:
                return redPosition;
            case ORANGE:
                return orangePosition;
            case YELLOW:
                return yellowPosition;
            case GREEN:
                return greenPosition;
            case BLUE:
                return bluePosition;
            case PURPLE:
                return purplePosition;
            case WHITE:
                return whitePosition;
            case OFF:
                return offPosition;
        }

        return Double.NaN;
    }
}
