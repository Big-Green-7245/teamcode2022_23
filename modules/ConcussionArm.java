package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ConcussionArm implements Modulable {
    private ElapsedTime runtime = new ElapsedTime();

    public HardwareMap hwMap;
    public DcMotor arm;

    @Override
    public void init(HardwareMap map) {
        hwMap = map;
        arm = hwMap.get(DcMotor.class, "arm");
        arm.setDirection(DcMotor.Direction.FORWARD);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void move(int target, double power) {
        arm.setTargetPosition(arm.getCurrentPosition() + target);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setPower(power);
    }
}

