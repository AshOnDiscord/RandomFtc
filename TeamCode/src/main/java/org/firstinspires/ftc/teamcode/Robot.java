package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Robot {
    HardwareMap hwMap;
    Gamepad gamepad1;
    DriveTrain driveTrain;
    Lift lift;

    public Robot(HardwareMap hwMap, Gamepad gamepad1) {
        this.hwMap = hwMap;
        this.gamepad1 = gamepad1;
        this.driveTrain = new DriveTrain(hwMap);
        this.lift = new Lift(hwMap);
    }

    public static class DriveTrain {
        public DcMotorEx frontLeft;
        public DcMotorEx frontRight;
        public DcMotorEx backRight;
        public DcMotorEx backLeft;

        public DriveTrain(HardwareMap hwMap) {
            frontLeft = hwMap.get(DcMotorEx.class, "frontLeft");
            frontRight = hwMap.get(DcMotorEx.class, "frontRight");
            backRight = hwMap.get(DcMotorEx.class, "backRight");
            backLeft = hwMap.get(DcMotorEx.class, "backLeft");
        }

        public void setPower(double fl, double fr, double br, double bl) {
            frontLeft.setPower(fl);
            frontRight.setPower(fr);
            backRight.setPower(br);
            backLeft.setPower(bl);
        }
    }

    public static class Lift {
        public DcMotorEx left;
        public DcMotorEx right;
        public double gravity = 0.1;
        public double[] setLines = {300, 500, 700};

        public Lift(HardwareMap hwMap) {
            left = hwMap.get(DcMotorEx.class, "leftLift");
            right = hwMap.get(DcMotorEx.class, "rightLift");
        }

        public void setPower(double power) {
            left.setPower(power);
            right.setPower(power);
        }

        public double getHeight() {
            return left.getCurrentPosition();
        }

        public void hold() {
            this.setPower(this.gravity);
        }

        public void setMode(DcMotor.RunMode mode) {
            left.setMode(mode);
            right.setMode(mode);
        }

        public void setZero(DcMotor.ZeroPowerBehavior mode) {
            left.setZeroPowerBehavior(mode);
            right.setZeroPowerBehavior(mode);
        }

        public void brake(boolean brake) {
            this.setZero(brake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }
}