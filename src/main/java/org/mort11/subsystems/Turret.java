package org.mort11.subsystems;

import static org.mort11.configs.constants.PhysicalConstants.ROBOT_VOLTAGE;

import static org.mort11.configs.constants.PhysicalConstants.Turret.*;
import static org.mort11.configs.constants.PIDConstants.Turret.*;
import static org.mort11.configs.constants.PortConstants.Turret.*;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
    
    private static Turret turret;

    private SparkMax motor;
    private SparkMaxConfig feedConfig;

    private double motorSpeed;

    private ProfiledPIDController controller;
    private SimpleMotorFeedforward feedforward;

    private Turret() {
        motor = new SparkMax(TURRET_MOTOR, MotorType.kBrushless);
        feedConfig = new SparkMaxConfig();

        motor.configure(feedConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        controller = new ProfiledPIDController(ROT_KP, ROT_KI, ROT_KD, ROT_CONSTRAINTS);
        feedforward = new SimpleMotorFeedforward(ROT_KS, ROT_KV, ROT_KA);
    }

    @Override
    public void periodic() {
        // motor.setVoltage(motorSpeed * ROBOT_VOLTAGE);

        SmartDashboard.putNumber("TurretDeg", getTurretPosDeg());
        SmartDashboard.putNumber("Turret Vel Deg", getTurretVelDeg());
    }

    public void setTurretMotorPercent(double motorSpeed) {
        this.motorSpeed = motorSpeed + feedforward.calculate(getTurretVelDeg());
    }

    public void setTurretPosDeg(double targetDeg){
        targetDeg = Math.max(TURRET_MIN_ANGLE, Math.min(TURRET_MAX_ANGLE, targetDeg));
        double pidOutput = controller.calculate(getTurretPosDeg(), targetDeg);
        double ffOutput = feedforward.calculate(controller.getSetpoint().velocity);
        motor.setVoltage(pidOutput + ffOutput);
    }


    // public boolean goalReached(){
    //     return controller.atGoal();
    // }

    public double getTurretPosDeg() {
        return getMotorRotationPosition() * MOTOR_ROTATIONS_TO_TURRET_DEG + STARTING_POSITION_DEG;
    }

    //deg / sec
    //converts to rotations / sec, then to deg / sec
    public double getTurretVelDeg() {
        return ((getMotorRotationRPM() / 60) * MOTOR_ROTATIONS_TO_TURRET_DEG);
    }

    public double getMotorRotationPosition() {
        return motor.getEncoder().getPosition();
    }

    public double getMotorRotationRPM() {
        return motor.getEncoder().getVelocity();
    }

    public ProfiledPIDController getPIDController() {
        return controller;
    }

    public static Turret getInstance() {
        if (turret == null) {
            turret = new Turret();
        }
        return turret;
    }
}
