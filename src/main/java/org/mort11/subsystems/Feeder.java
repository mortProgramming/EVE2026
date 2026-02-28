package org.mort11.subsystems;

import static org.mort11.configs.constants.PortConstants.Feeder.*;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import static org.mort11.configs.constants.PhysicalConstants.Feeder.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feeder extends SubsystemBase {
    private static Feeder feeder;
    private SparkMax feedMotor;
    private SparkMax feedMotor2;
    private SparkMaxConfig feedConfig;
    private SparkMaxConfig feedConfig2;
    private double motorSpeed = 0;

    public Feeder() {
        feedMotor = new SparkMax(FEEDER_MOTOR, MotorType.kBrushless);
        //feedMotor2 = new SparkMax(FEEDER_MOTOR_2, MotorType.kBrushless);

        feedConfig = new SparkMaxConfig();
        feedConfig.smartCurrentLimit(FEEDER_SMART_CURRENT_LIMIT)
          .secondaryCurrentLimit(FEEDER_SECONDARY_CURRENT_LIMIT);

        // feedConfig2 = new SparkMaxConfig();
        // feedConfig2.smartCurrentLimit(FEEDER_SMART_CURRENT_LIMIT)
        //     .secondaryCurrentLimit(FEEDER_SECONDARY_CURRENT_LIMIT)
        //     .follow(feedMotor, false);

        feedMotor.configure(feedConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        //feedMotor2.configure(feedConfig2, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Feeder Pos Motor Rotations", getMotorRotationPosition());
        SmartDashboard.putNumber("Feeder Motor Speed RPM", getMotorRotationRPM());
        SmartDashboard.putNumber("Feeder Motor Current", feedMotor.getOutputCurrent());
    }

    public void setSpeed(double speed){
        feedMotor.set(speed);
    }

    public double getMotorRotationPosition() {
        return feedMotor.getEncoder().getPosition();
    }

    public double getMotorRotationRPM() {
        return feedMotor.getEncoder().getVelocity();
    }

    public static Feeder getInstance(){
        if (feeder == null){
            feeder = new Feeder();
        }
        return feeder;
    }
}

