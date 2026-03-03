package org.mort11.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.filter.MedianFilter;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.mort11.configs.constants.PhysicalConstants.ROBOT_VOLTAGE;

import static org.mort11.configs.constants.PhysicalConstants.Shooter.*;
import static org.mort11.configs.constants.PortConstants.Shooter.*;
import static org.mort11.configs.constants.PIDConstants.Shooter.*;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.controls.Follower;

public class Shooter extends SubsystemBase {
    private static Shooter shooter;
    private final TalonFX shooterLeader;
    private final TalonFX shooterFollowerA;
    private final TalonFX shooterFollowerB;

    private SimpleMotorFeedforward feedforward;
    private SlewRateLimiter slewLimiter;
    
    private MedianFilter RPMAverager; 

    // private final VelocityVoltage velocityRequest =
    //     new VelocityVoltage(0).withSlot(0);

    private double shooterSpeed = 0;

    public Shooter() {
        shooterLeader = new TalonFX(SHOOTER_LEADER);
        shooterFollowerA = new TalonFX(SHOOTER_FOLLOWER_A);
        shooterFollowerB = new TalonFX(SHOOTER_FOLLOWER_B);

        TalonFXConfiguration config = new TalonFXConfiguration();
        // config.Slot0.kP = 0.15;
        // config.Slot0.kI = 0.0;
        // config.Slot0.kD = 0.01;
        // config.Slot0.kV = 0.12;

        shooterLeader.getConfigurator().apply(config);
        shooterFollowerA.getConfigurator().apply(config);
        shooterFollowerB.getConfigurator().apply(config);

        shooterFollowerA.setControl(new Follower(SHOOTER_LEADER, MotorAlignmentValue.Opposed));
        shooterFollowerB.setControl(new Follower(SHOOTER_LEADER, MotorAlignmentValue.Opposed));

        feedforward = new SimpleMotorFeedforward(RPM_KS, RPM_KV, RPM_KA);
        slewLimiter = new SlewRateLimiter(SLEW_RATE_LIMIT);

        RPMAverager = new MedianFilter(35);
    }

    @Override
    public void periodic() {
        shooterLeader.setVoltage(slewLimitedSpeed(shooterSpeed) * ROBOT_VOLTAGE);

        SmartDashboard.putNumber("Shooter Speed RPM", RPMAverager.calculate(getShooterRPM()));
        
    }

    public double slewLimitedSpeed(double shooterSpeed) {
        return slewLimiter.calculate(shooterSpeed);
    }

    // public void setShooterSpeed(double speed) {
    //     velocityRequest.withVelocity(speed);
    //     shooterLeader.setControl(velocityRequest);
    //     shooterFollowerA.setControl(velocityRequest);
    //     shooterFollowerB.setControl(velocityRequest);
    // }

    public void setShooterPercent(double percent) {
        shooterSpeed = percent;
    }

    public void setShooterRPM(double RPM) {
        shooterSpeed = (RPM / MAX_SHOOTER_RPM) + 
        (feedforward.calculate(RPM) / ROBOT_VOLTAGE);
    }

    public double getShooterSpeedRPS() {
        return shooterLeader.getVelocity().getValueAsDouble();
    }

    public double getShooterRPM() {
        return getShooterSpeedRPS() * 60.0;
    }

   public boolean isAtTargetRPM(double targetRPM) {
    if (targetRPM == 0) return true;
    return Math.abs(getShooterRPM() - targetRPM) / targetRPM < SHOOTER_SPEED_BUZZ_TOLERANCE;
}   

    public static Shooter getInstance(){
        if (shooter == null){
            shooter = new Shooter();
        }
        return shooter;
    }
}

