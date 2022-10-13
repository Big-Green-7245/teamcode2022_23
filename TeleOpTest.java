package org.firstinspires.ftc.teamcode;

// Standard Lib

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

// Class import
import org.firstinspires.ftc.teamcode.modules.DriveTrain;
import org.firstinspires.ftc.teamcode.util.ButtonHelper;
import org.firstinspires.ftc.teamcode.util.TelemetryWrapper;


@TeleOp(name = "TeleOpTest", group = "opmode")
public class TeleOpTest extends LinearOpMode {
    // Define attributes
    final String programVer = "1.0";
    final double speedMultiplier = 1.0;

    // Declare modules
    DriveTrain driveTrain;
    ButtonHelper gp1, gp2;
    ColorSensor colorSensor;

    @Override
    public void runOpMode() {
        // Robot modules initialization
        driveTrain = new DriveTrain();
        gp1 = new ButtonHelper(gamepad1);
        gp2 = new ButtonHelper(gamepad2);
        colorSensor = hardwareMap.get(ColorSensor.class, "color");

        driveTrain.init(hardwareMap);
        TelemetryWrapper.init(telemetry, 16);

        // Wait for start
        TelemetryWrapper.setLine(1, "TeleOpTest v" + programVer + "\t Press start to start >");
        waitForStart();

        while (opModeIsActive()) {
            // Update ButtonHelper
            gp1.update();
            gp2.update();

            // DriveTrain wheels
            int red = colorSensor.red();
            int blue = colorSensor.blue();
            int green = (int) Math.floor(colorSensor.green() * 0.75);
            int max = Math.max(red, Math.max(blue, green));
            if (red == max) TelemetryWrapper.setLine(5, "Red");
            if (blue == max) TelemetryWrapper.setLine(5, "Blue");
            if (green == max) TelemetryWrapper.setLine(5, "Green");


            // Display data for telemetry
            TelemetryWrapper.setLine(1, "TeleOpT1 v" + programVer);
            TelemetryWrapper.setLine(2, "Red: " + colorSensor.red());
            TelemetryWrapper.setLine(3, "Blue: " + colorSensor.blue());
            TelemetryWrapper.setLine(4, "Green: " + colorSensor.green());
            TelemetryWrapper.setLine(6, "Other info...");
        }

    }
}
