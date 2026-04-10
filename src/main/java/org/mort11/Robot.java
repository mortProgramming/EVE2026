// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.mort11;

import org.mort11.subsystems.Limelight;

import org.mort11.subsystems.LimelightRewindManager;
import org.mort11.subsystems.LimelightRewindNT;
import org.mort11.subsystems.Vision;

import com.ctre.phoenix6.HootAutoReplay;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    private final RobotContainer m_robotContainer;

    // Use your Limelight NT table name:
    // often "limelight" or "limelight-<name>" (e.g. "limelight-three")
    private final LimelightRewindNT rewind = new LimelightRewindNT("limelight-three");
    private final LimelightRewindManager rewindManager = new LimelightRewindManager(rewind);

    /* log and replay timestamp and joystick data */
    private final HootAutoReplay m_timeAndJoystickReplay = new HootAutoReplay()
        .withTimestampReplay()
        .withJoystickReplay();

    public Robot() {
        m_robotContainer = new RobotContainer();
        
    }

    @Override
    public void robotPeriodic() {
        m_timeAndJoystickReplay.update();
        CommandScheduler.getInstance().run(); 
    }

    @Override
    public void disabledInit() {
        // Update telemetry and LEDs to show throttled state right away
        Vision.getInstance().updateLimelightTelemetry(true);
    }

    @Override
    public void disabledPeriodic() {
        // Keep telemetry live and LEDs blinking while disabled.
        Vision.getInstance().updateLimelightTelemetry(true); // true = throttled state
    }

    @Override
    public void disabledExit() {
        // Update telemetry and restore LEDs to pipeline control (not throttled)
        Vision.getInstance().updateLimelightTelemetry(false);
    }

    @Override
    public void autonomousInit() {
        // Set Limelight throttling to false
        Vision.getInstance().updateLimelightTelemetry(false);
        
        if(DriverStation.isFMSAttached()){
            new Limelight("limelight-three").startNewRewind();
        }
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();
        if (m_autonomousCommand != null) {
            CommandScheduler.getInstance().schedule(m_autonomousCommand);
        }
    }

    @Override
    public void autonomousPeriodic() {
        
    }

    @Override
    public void autonomousExit() {

    }

    @Override
    public void teleopInit() {
        if (m_autonomousCommand != null) {
        CommandScheduler.getInstance().cancel(m_autonomousCommand);
        }
        RobotContainer.getSwerveDrivetrain().resetOperatorPerspective();
    }

    @Override
    public void teleopPeriodic() {}

    @Override
    public void teleopExit() {}

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {}

    @Override
    public void testExit() {}

    @Override
    public void simulationPeriodic() {}
}
