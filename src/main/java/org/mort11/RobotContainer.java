// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.mort11;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
// import com.pathplanner.lib.commands.PathPlannerAuto; commented out bc pathplanner errors

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import org.mort11.commands.actions.endeffector.manual.moveFeeder;
import org.mort11.commands.actions.endeffector.manual.moveLeftIntake;
import org.mort11.commands.actions.endeffector.manual.moveLeftRoller;
import org.mort11.commands.actions.endeffector.manual.moveRightIntake;
import org.mort11.commands.actions.endeffector.manual.moveRightRoller;
// import org.mort11.commands.actions.endeffector.manual.moveHood;
import org.mort11.commands.actions.endeffector.manual.PercentShoot;
import org.mort11.commands.actions.endeffector.pid.SetEvanHood;
import org.mort11.commands.actions.endeffector.pid.SetHoodShooter;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.actions.endeffector.pid.SetSuperShooter;
import org.mort11.commands.actions.endeffector.pid.SetTurret;
// import org.mort11.commands.actions.endeffector.pid.setHood;
import org.mort11.commands.actions.endeffector.pid.setIntakeLeft;
import org.mort11.commands.actions.endeffector.pid.setIntakeRight;
import org.mort11.commands.actions.endeffector.manual.MoveTurret;
import org.mort11.commands.actions.endeffector.manual.Climb;
import org.mort11.commands.actions.endeffector.manual.MoveEvanHood;
import org.mort11.commands.autons.apriltag.Angle2AprilTag;
// import org.mort11.commands.autons.BasicCommands; commented out for now bc pathplanner errors
import org.mort11.commands.autons.apriltag.LimelightTest;
import org.mort11.commands.autons.timed.Taxi;
import static org.mort11.configs.constants.PhysicalConstants.Turret.*;
import org.mort11.configs.LookUpTable;
import org.mort11.configs.constants.TunerConstants;
import org.mort11.subsystems.Climber;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.EvanHood;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.IntakeArmLeft;
import org.mort11.subsystems.IntakeArmRight;
import org.mort11.subsystems.IntakeRollerLeft;
import org.mort11.subsystems.IntakeRollerRight;
import org.mort11.subsystems.Shooter;
import org.mort11.subsystems.Turret;
import org.mort11.subsystems.Vision;

import com.pathplanner.lib.path.PathPlannerPath;


import static edu.wpi.first.units.Units.*;





import static org.mort11.configs.constants.PortConstants.Controller.*;
import static org.mort11.configs.constants.PhysicalConstants.*;

public class RobotContainer {
    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(1.25).in(RadiansPerSecond); // 1.25 of a rotation per second max angular velocity
    private double currentSpeed = MaxSpeed;
    private double currentAngularRate = MaxAngularRate;

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private static final CommandPS5Controller driveController = new CommandPS5Controller(DRIVE_CONTROLLER);
    private static final CommandXboxController endeffectorController = new CommandXboxController(ENDEFFECTOR_CONTROLLER);
    private static final CommandXboxController manualController = new CommandXboxController(MANUAL_CONTROLLER);

    
    public final static CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    
    // private PathPlannerPath pathPlannerTest = PathPlannerPath.fromPathFile("DriveVertical");
    // private Command autonomousCommand = AutoBuilder.followPath(pathPlannerTest);

   
    private final Vision vision = Vision.getInstance();
    
    
    public static SendableChooser<Command> autoChooser;

    public AutoBuilder autoBuilder;
        public RobotContainer() {
            drivetrain.configureAutoBuilder();
            configureBindings();
            configureAuto();
        }
    
        private void configureBindings() {
            Climber.getInstance();
            EvanHood.getInstance();
            Feeder.getInstance();
            IntakeArmLeft.getInstance();
            IntakeArmRight.getInstance();
            IntakeRollerLeft.getInstance();
            IntakeRollerRight.getInstance();
            Shooter.getInstance();
            Turret.getInstance();
            Vision.getInstance();
            // Note that X is defined as forward according to WPILib convention,
            // and Y is defined as to the left according to WPILib convention.
            drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() ->
                    drive.withVelocityX(-driveController.getLeftY() * currentSpeed) // Drive forward with negative Y (forward)
                        .withVelocityY(-driveController.getLeftX() * currentSpeed) // Drive left with negative X (left)
                        .withRotationalRate(-driveController.getRightX() * currentAngularRate) // Drive counterclockwise with negative X (left)
                )
            );
    
            // Idle while the robot is disabled. This ensures the configured
            // neutral mode is applied to the drive motors while disabled.
            final var idle = new SwerveRequest.Idle();
            RobotModeTriggers.disabled().whileTrue(
                drivetrain.applyRequest(() -> idle).ignoringDisable(true)
            );
    
            driveController.cross().whileTrue(drivetrain.applyRequest(() -> brake));
            driveController.circle().whileTrue(drivetrain.applyRequest(() ->
                point.withModuleDirection(new Rotation2d(-driveController.getLeftY(), -driveController.getLeftX()))
            ));
            driveController.R2().whileTrue(Commands.runOnce(() -> {
                currentSpeed = 0.3 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
                currentAngularRate = RotationsPerSecond.of(0.5).in(RadiansPerSecond);
            }));

            driveController.triangle().onTrue(Commands.runOnce(() -> {
                currentSpeed = MaxSpeed;
                currentAngularRate = MaxAngularRate;
            }));


            // uehheh ehfeuguuegeufuhehehfuef
            // Run SysId routines when holding back/start and X/Y.
            // Note that each routine should be run exactly once in a single log.
            // driveController.back().and(driveController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
            // driveController.back().and(driveController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
            // driveController.start().and(driveController.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
            // driveController.start().and(driveController.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));
            
            // Reset the field-centric heading on left bumper press.
            driveController.L1().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));
    
            drivetrain.registerTelemetry(logger::telemeterize);
            
            driveController.square().whileTrue(new Angle2AprilTag(0));



            //Subsystem commands for the endeffector, binded to the operator controller
            //Intake Arms
            new Trigger(() -> manualController.getLeftY() < -DEAD_BAND).whileTrue(new moveLeftIntake(manualController));
            new Trigger(() -> manualController.getLeftY() > DEAD_BAND).whileTrue(new moveLeftIntake(manualController));

            new Trigger(() -> manualController.getRightY() > -DEAD_BAND).whileTrue(new moveRightIntake(manualController));
            new Trigger(() -> manualController.getRightY() < DEAD_BAND).whileTrue(new moveRightIntake(manualController));

            endeffectorController.y().whileTrue(new moveRightIntake(-0.2));
            endeffectorController.a().whileTrue(new moveRightIntake(0.2));
            endeffectorController.povUp().whileTrue(new moveLeftIntake(0.2));
            endeffectorController.povDown().whileTrue(new moveLeftIntake(-0.2));
        
            //Intake Roller

            //left
            manualController.x().whileTrue(new moveLeftRoller(0.7));
            manualController.leftBumper().onTrue(new moveLeftRoller(0.5));
            endeffectorController.leftBumper().whileTrue(new moveLeftRoller(0.85));
            //right
            manualController.b().whileTrue(new moveRightRoller(-0.7));
            manualController.rightBumper().whileTrue(new moveRightRoller(0.5));
            endeffectorController.rightBumper().whileTrue(new moveRightRoller(-0.85));

            //Set Intake
            manualController.a().onTrue(setIntakeLeft.intake());
            manualController.b().onTrue(setIntakeLeft.up());
            //endeffectorController.pov(180).onTrue(setIntakeLeft.intake());
            //endeffectorController.pov(0).onTrue(setIntakeLeft.up());

            manualController.x().onTrue(setIntakeRight.intake());
            manualController.y().onTrue(setIntakeRight.up());
         //endeffectorController.a().onTrue(setIntakeRight.intake());
            //endeffectorController.y().onTrue(setIntakeRight.up());
            

            

            //Feeder
            manualController.pov(0).whileTrue(new moveFeeder(0.5));
            manualController.pov(180).whileTrue(new moveFeeder(-1)); //moves the correct way
            endeffectorController.rightTrigger(TRIGGER_THRESHOLD).whileTrue(new moveFeeder(-1));

            //Turret
            manualController.pov(90).whileTrue(new MoveTurret(-MANUAL_SPEED));
            manualController.pov(270).whileTrue(new MoveTurret(MANUAL_SPEED));
            //endeffectorController.a().whileTrue(new SetTurret(45));
            new Trigger(() -> endeffectorController.getRightX() > DEAD_BAND)
                .whileTrue(new MoveTurret(endeffectorController.getRightX() * MANUAL_SPEED));
            new Trigger(() -> endeffectorController.getRightX() < -DEAD_BAND)
                .whileTrue(new MoveTurret(endeffectorController.getRightX() * MANUAL_SPEED));

            //hood
            manualController.back().whileTrue(new MoveEvanHood(1));
            manualController.start().whileTrue(new MoveEvanHood(-1));

            //Shooter
            manualController.y().whileTrue(new PercentShoot(0.25));

            endeffectorController.leftTrigger(TRIGGER_THRESHOLD).whileTrue(new SetShooter(2500));
            // endeffectorController.x().whileTrue(new SetShooter(3000));
            // LookUpTable.getNeededHoodAngle(-1);
            // LookUpTable.getNeededHoodAngle(3);
            // LookUpTable.getNeededHoodAngle(300);

            endeffectorController.x().whileTrue(
                new SetHoodShooter(
                    () -> LookUpTable.getNeededShooterRPM(3),
                    () -> LookUpTable.getNeededHoodAngle(3)
                )
            );
            // endeffectorController.x().whileTrue(new SetSuperShooter(
            //     () -> LookUpTable.getNeededShooterRPM(3), 
            //     () -> 0, 
            //     () -> LookUpTable.getNeededHoodAngle(3)
            // ));

            //Climber
            manualController.leftBumper().whileTrue(new Climb(0.5));
            manualController.rightBumper().whileTrue(new Climb(-0.5));
        }
        
        public Command getPathPlannerCommand(){
            try{
        // Load the path you want to follow using its name in the GUI
                PathPlannerPath path = PathPlannerPath.fromPathFile("DriveVertical");
        // Create a path following command using AutoBuilder. This will also trigger event markers.
                return AutoBuilder.followPath(path);
            } 
            catch (Exception e) {
                DriverStation.reportError("Big oops: " + e.getMessage(), e.getStackTrace());
                return Commands.none();
            }
        }

        public Command getAutonomousCommand() {
            // Simple drive forward auton
            return autoChooser.getSelected();
            // final var idle = new SwerveRequest.Idle();
            // return Commands.sequence(
            //     // Reset our field centric heading to match the robot
            //     // facing away from our alliance station wall (0 deg).
            //     drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)),
            //     // Then slowly drive forward (away from us) for 5 seconds.
            //     drivetrain.applyRequest(() ->
            //         drive.withVelocityX(0.5)
            //             .withVelocityY(0)
            //             .withRotationalRate(0)
            //     )
            //     .withTimeout(5.0),
            //     // Finally idle for the rest of auton
            //     drivetrain.applyRequest(() -> idle)
            // );
        }
    
        public void configureAuto() {
        final var idle = new SwerveRequest.Idle();
        autoChooser = new SendableChooser<Command>();
        autoChooser.setDefaultOption("nothing", null);
        autoChooser.addOption("Pathplanner Rotate", new PathPlannerAuto("RotationAuto"));
        autoChooser.addOption("Pathplanner Vertical", new PathPlannerAuto("DriveAuto"));
        autoChooser.addOption("Pathplanner ZigZag", new PathPlannerAuto("ZigZagAuto"));
        autoChooser.addOption("Timed Taxi", new Taxi());
        autoChooser.addOption("Limelight Test", new LimelightTest(drivetrain, vision, 0));
        autoChooser.addOption("Drive forward nopathplan",Commands.sequence(
                // Reset our field centric heading to match the robot
                // facing away from our alliance station wall (0 deg).
                drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)),
                // Then slowly drive forward (away from us) for 5 seconds.
                drivetrain.applyRequest(() ->
                    drive.withVelocityX(0.5)
                        .withVelocityY(0)
                        .withRotationalRate(0)
                )
                .withTimeout(5.0),
                // Finally idle for the rest of auton
                drivetrain.applyRequest(() -> idle)));
        // Pathplanner autos WIP
        // autoChooser.addOption("LimelightTest", new PathPlannerAuto("Please Work")); 
    
                // drivetrain.applyRequest(() -> idle)
            // );
        }
    
    //     public void configureAuto() {
    //     autoChooser = new SendableChooser<Command>();
    //     autoChooser.setDefaultOption("nothing", null);
    //     autoChooser.addOption("Timed Taxi", new Taxi());
    //     autoChooser.addOption("LimelightTest", new PathPlannerAuto("Please Work"));
    //     SmartDashboard.putData("Auton Chooser", autoChooser);
    // }

    //     public static Command getPlanned(String plan) {
    //         BasicCommands.setCommands();
    //         return new PathPlannerAuto(plan);
    // } incorrect thing
    
    public static CommandPS5Controller getDriverController() {
        return driveController;
    }
    
    public static CommandXboxController getEndeffectorController() {
        return endeffectorController;
    }

    public static CommandXboxController getManualController() {
        return manualController;
    }

    public static CommandSwerveDrivetrain getSwerveDrivetrain() {
        return drivetrain;
    }
}
