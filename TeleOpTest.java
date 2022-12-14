package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.modules.DriveTrain;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.util.ButtonHelper;
import org.firstinspires.ftc.teamcode.util.TelemetryWrapper;

import java.util.Arrays;

@SuppressWarnings("FieldCanBeLocal")
@TeleOp(name = "TeleOpTest", group = "opmode")
public class TeleOpTest extends LinearOpMode {
    // Define attributes
    private final String programVer = "1.6";
    private final double speedMultiplier = 0.55;

    // Declare modules
    private ButtonHelper gp1, gp2;
    private DriveTrain driveTrain;
    private Intake intake;


    @Override
    public void runOpMode() {
        TelemetryWrapper.init(telemetry, 16);

        TelemetryWrapper.setLine(1, "TeleOpTest v" + programVer + "\t Initializing");

        // Robot modules initialization
        gp1 = new ButtonHelper(gamepad1);
        gp2 = new ButtonHelper(gamepad2);
        driveTrain = new DriveTrain();
        intake = new Intake();
        driveTrain.init(hardwareMap);
        intake.init(hardwareMap);

        // Wait for start
        TelemetryWrapper.setLine(1, "TeleOpTest v" + programVer + "\t Press start to start >");

        // Move elevator and pivot to starting position while waiting for start
        intake.elevator.startMoveToGround();
        while (!isStarted()) {
            intake.tickBeforeStart();
        }

        while (opModeIsActive()) {
            // Update ButtonHelper
            gp1.update();
            gp2.update();

            // Tick modules
            intake.tick();

            // DriveTrain wheels
            driveTrain.move(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, speedMultiplier);

            intake.elevator.moveUsingEncoder(-gamepad2.left_stick_y);

            // LinearSlide movement
            if (gp2.pressing(ButtonHelper.x)) intake.startPlaceCone(Intake.GROUND);
            else if (gp2.pressing(ButtonHelper.a)) intake.startPlaceCone(Intake.LOW);
            else if (gp2.pressing(ButtonHelper.b)) intake.startPlaceCone(Intake.MID);
            else if (gp2.pressing(ButtonHelper.y)) intake.startPlaceCone(Intake.HIGH);


            // Move the claw
            if (gp2.pressing(ButtonHelper.dpad_up)) {
                intake.toggleClaw();
            }
            if (gp2.pressing(ButtonHelper.dpad_left)) {
                intake.togglePivot();
            }
            if (gp2.pressing(ButtonHelper.dpad_right)) {
                intake.confirmPlacePosition();
            }

            // Update Telemetry
            TelemetryWrapper.setLine(1, "TeleOpT1 v" + programVer);
            TelemetryWrapper.setLine(2, "Other info...");

            TelemetryWrapper.setLine(3, "up" + gp1.pressed(ButtonHelper.dpad_up));
            TelemetryWrapper.setLine(4, "down" + gp1.pressed(ButtonHelper.dpad_down));
            TelemetryWrapper.setLine(5, "left" + gp1.pressed(ButtonHelper.dpad_left));
            TelemetryWrapper.setLine(6, "right" + gp1.pressed(ButtonHelper.dpad_right));

            TelemetryWrapper.setLine(8, "CURRENTS");
            TelemetryWrapper.setLine(9, "DriveTrain Currents:" + driveTrain.getMotorCurrentsString());
            TelemetryWrapper.setLine(10, "LinearSlide Current: " + intake.elevator.getCurrent());
            TelemetryWrapper.setLine(11, "DriveTrain Encoders: " + Arrays.toString(driveTrain.getEncPos()));
            TelemetryWrapper.setLine(12, "LinearSlide EncoderTarget: " + intake.elevator.getCurrentTarget());
            TelemetryWrapper.setLine(14, "LinearSlide Encoder: " + intake.elevator.getEncPos());
            TelemetryWrapper.setLine(13, "Current State: " + intake.getCurrentState());
        }
    }
}
