package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.lights.LightsManager;
import com.bylazar.lights.PanelsLights;
import com.bylazar.lights.RGBIndicator;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utils.GoBildaIndicatorLight;

@Configurable
@Disabled
@TeleOp(name = "GoBilda Indicator Light Test", group = "Tests")
public class GoBildaIndicatorLightTest extends OpMode {
    private GoBildaIndicatorLight light;
    private LightsManager lightManager;
    private RGBIndicator lightRbgIndicator;
    public static GoBildaIndicatorLight.Color color = GoBildaIndicatorLight.Color.OFF;
    private JoinedTelemetry joinedTelemetry;

    @Override
    public void init() {
        light = new GoBildaIndicatorLight(hardwareMap, "armLeft");

        lightManager = PanelsLights.INSTANCE.getLights();
        lightRbgIndicator = new RGBIndicator("light");
        lightManager.initLights(lightRbgIndicator);
        lightManager.update();

        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);
    }

    @Override
    public void loop() {
        light.setColor(color);
        lightRbgIndicator.update(light.getPositionFromColor(color));

        joinedTelemetry.addLine("Color: " + color.toString());
        joinedTelemetry.addLine("Set Position: " + light.getPositionFromColor(color));
        joinedTelemetry.update();
    }
}
