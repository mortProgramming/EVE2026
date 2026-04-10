package org.mort11.robotcontainer;

import org.mort11.RobotContainer;
import org.mort11.subsystems.CommandSwerveDrivetrain;

import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class Inputs {

    public static CommandPS5Controller driveControllerPS5;

    public static CommandXboxController driveControllerXbox;

    public static CommandXboxController endeffectorController;

	public static CommandXboxController manualController;

    public static CommandJoystick joystick;

	private static CommandJoystick throttle;

	public static CommandSwerveDrivetrain drivetrain;

    public Inputs(){
        //two ways to get drivetrain, not sure which one is better
        drivetrain = RobotContainer.getSwerveDrivetrain();
        // drivetrain = RobotContainer.drivetrain;

    }

}
