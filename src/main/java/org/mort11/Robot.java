// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.mort11;

import org.mort11.subsystems.LimelightHelpers;
import org.mort11.subsystems.Vision;

import com.ctre.phoenix6.HootAutoReplay;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    private final RobotContainer m_robotContainer;

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
        // Force throttle on ALL limelights immediately when the robot is disabled.
        // This fires before disabledPeriodic() runs its first cycle, so there is no gap.
        // for (String name : Vision.getLimelights()) {
        //     LimelightHelpers.SetThrottle(name, Vision.LIMELIGHT_THROTTLE_VALUE);
        // }
        // Update telemetry and LEDs to show throttled state right away
        Vision.getInstance().updateLimelightTelemetry(true);
    }

    @Override
    public void disabledPeriodic() {
        // Keep telemetry live and LEDs blinking while disabled.
        Vision.getInstance().updateLimelightTelemetry(true); // true = throttled state

        // Vision.getInstance().updateLimelightThrottle(); // Throttle limelights if they get too hot while disabled (Use: Schmitt Trigger)
    }

    @Override
    public void disabledExit() {
        // Remove throttle from ALL limelights when the robot enables.
        // Full camera speed is restored for autonomous and teleop.
        // for (String name : Vision.getLimelights()) {
        //     LimelightHelpers.SetThrottle(name, 0);
        // }
        // Update telemetry and restore LEDs to pipeline control (not throttled)
        Vision.getInstance().updateLimelightTelemetry(false);
        
        // // Remove throttle when robot enables so vision is fully active during match
        // for (String name : Vision.getLimelights()) {
        //     LimelightHelpers.SetThrottle(name, 0);
        //     LimelightHelpers.setLEDMode_PipelineControl(name); // restore LED to pipeline
        // }
    }

    @Override
    public void autonomousInit() {
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
