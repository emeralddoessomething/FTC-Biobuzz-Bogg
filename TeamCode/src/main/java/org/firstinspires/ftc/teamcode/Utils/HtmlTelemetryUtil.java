package org.firstinspires.ftc.teamcode.Utils;

public class HtmlTelemetryUtil {
    public enum Colors {
        red, orange, yellow, green, blue, purple, pink, brown, black, white
    }

    public enum Size {
        h1, h2, h3, h4, h5, h6, p
    }

    public static String constructString(Size size, Colors color, String value) {
        return "<" + size.name() + "><font color='" + color.name() + "'>" + value + "</font></" + size.name() + ">";
    }

    public static String constructString(Size size, String value) {
        return constructString(size, Colors.white, value);
    }

    public static String constructString(Colors color, String value) {
        // Fixed: Use the passed color parameter
        return constructString(Size.p, color, value);
    }
}
