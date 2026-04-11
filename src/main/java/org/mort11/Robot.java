// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.mort11;

import org.mort11.subsystems.Vision;

import java.sql.Driver;

import org.mort11.subsystems.LimelightRewindManager;

import com.ctre.phoenix6.HootAutoReplay;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    private final RobotContainer m_robotContainer;

    private final LimelightRewindManager m_limelightRewind;

    /* log and replay timestamp and joystick data */
    private final HootAutoReplay m_timeAndJoystickReplay = new HootAutoReplay()
        .withTimestampReplay()
        .withJoystickReplay();

    public Robot() {
        m_robotContainer = new RobotContainer();
        m_limelightRewind = new LimelightRewindManager();
    }

    @Override
    public void robotPeriodic() {
        m_timeAndJoystickReplay.update();
        m_limelightRewind.periodic();
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
        SmartDashboard.putNumber("Remaining Phase Time",DriverStation.getMatchTime());
        SmartDashboard.putString("Current Phase", "Autonomous");
    }

    @Override
    public void autonomousExit() {

    }

    @Override
    public void teleopInit() {
        Vision.getInstance().updateLimelightTelemetry(false);

        if (m_autonomousCommand != null) {
        CommandScheduler.getInstance().cancel(m_autonomousCommand);
        }
        RobotContainer.getSwerveDrivetrain().resetOperatorPerspective();
    }
    
    @Override
    public void teleopPeriodic() {
        final int[] phaseStartTimes = {140, 130, 105, 80, 55, 30, 0}; // in seconds
        final String[] phaseNames = {"Transition Shift", "Phase 1", "Phase 2", "Phase 3", "Phase 4", "Endgame", "End of Match"};
        for(int i=0; i<phaseStartTimes.length-1; i++){
            if(DriverStation.getMatchTime() <= phaseStartTimes[i] && DriverStation.getMatchTime() > phaseStartTimes[i+1]){
                SmartDashboard.putNumber("Remaining Phase Time", DriverStation.getMatchTime()-phaseStartTimes[i+1]);
                SmartDashboard.putString("Current Phase", phaseNames[i]);
                break;
            }
        }
    }

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
