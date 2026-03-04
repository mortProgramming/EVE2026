package org.mort11.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.mort11.configs.constants.PortConstants;

public class Climber extends SubsystemBase {
    private static Climber climber;
    private SparkMax climbMotor;
    private SparkMaxConfig climbConfig;
    private double motorSpeed = 0;

    public Climber() {
        climbMotor = new SparkMax(PortConstants.Climber.CLIMBER_MOTOR, MotorType.kBrushless);
        climbConfig = new SparkMaxConfig();

        climbMotor.configure(climbConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Climber Pos Motor Rotations", getMotorRotationPosition());
        SmartDashboard.putNumber("Climber Motor Speed RPM", getMotorRotationRPM());
    }

    public void setSpeed(double speed){
        climbMotor.set(speed);
    }

    public double getMotorRotationPosition() {
        return climbMotor.getEncoder().getPosition();
    }

    public double getMotorRotationRPM() {
        return climbMotor.getEncoder().getVelocity();
    }

    public static Climber getInstance(){
        if (climber == null){
            climber = new Climber();
        }
        return climber;
    }
}
