package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PID {
    public double kP;
    public double kI;
    public double kD;
    public double prevTarget = 0;
    public double lastError = 0;
    public double integralSum = 0;
    ElapsedTime timer = new ElapsedTime();

    public PID(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public double calc(double target, double current) {
        if (target != prevTarget) {
            this.integralSum = 0;
        }

        double error = target - current;
        this.integralSum = this.integralSum + (kI * error * timer.seconds());
        double derivative = (error - lastError) / timer.seconds();

        double output = kP * error + integralSum + kD * derivative;

        this.lastError = error;
        this.prevTarget = target;
        timer.reset();

        return output;
    }
}
