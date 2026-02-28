package org.mort11.subsystems;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import org.mort11.configs.constants.PortConstants;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeArmRight extends SubsystemBase{
    public static IntakeArmRight intake;
    private SparkMax intakeMotor;
    private SparkMaxConfig intakeConfig;
    private ProfiledPIDController positionController;
    private SparkClosedLoopController pidControl;
    private double setpoint;
    private double motorSpeed = 0;
    private final double KG = 0.1;
    
    IntakeArmRight() {
        intakeMotor = new SparkMax(PortConstants.IntakeArmRight.sparkIntakeRight, MotorType.kBrushless);
        intakeConfig = new SparkMaxConfig();
        
        intakeConfig.closedLoop
                .p(2)
                .i(0)
                .d(0);

        intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        pidControl = intakeMotor.getClosedLoopController();
        // positionController = new ProfiledPIDController(2, 0, 0, null);
        // positionController.setTolerance(0.01);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Intake Arm Right Pos Motor Rotations", getMotorRotationPosition());
        SmartDashboard.putNumber("Intake Arm Right Speed RPM", getMotorRotationRPM());
    }

    public void setSpeed(double speed){
        intakeMotor.set(speed);
    }
    
    public void setSetpoint(double setpoint){
        this.setpoint = setpoint;
    }

    public double getPosition(){
        return intakeMotor.getEncoder().getPosition();
    }
    
    public ProfiledPIDController getPIDController(){
        return positionController;
    }
    
    public void setArmPosition(double targetPosition){
        pidControl.setSetpoint(targetPosition, SparkMax.ControlType.kPosition, ClosedLoopSlot.kSlot0, 0);
    }

    public void setMotorPercent(double motorSpeed){
        this.motorSpeed=motorSpeed+KG;
    }

    public double getMotorRotationPosition() {
        return intakeMotor.getEncoder().getPosition();
    }

    public double getMotorRotationRPM() {
        return intakeMotor.getEncoder().getVelocity();
    }

    public static IntakeArmRight getInstance(){
        if (intake == null){
            intake = new IntakeArmRight();
        }
        return intake;
    }
}
