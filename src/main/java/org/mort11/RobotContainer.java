package org.mort11;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.util.PathPlannerLogging;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import org.mort11.commands.autons.apriltag.RotateToHub;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;

import org.mort11.commands.actions.endeffector.manual.MoveIntakeArm;
import org.mort11.commands.actions.endeffector.manual.MoveIntakeRoller;
import org.mort11.commands.actions.endeffector.pid.AgitateArm;
import org.mort11.commands.actions.endeffector.pid.PrepareShotCommand;
import org.mort11.commands.actions.endeffector.pid.SetArm;
import org.mort11.commands.actions.endeffector.pid.SetFeeder;
import org.mort11.commands.actions.endeffector.pid.SetShooter;
import org.mort11.commands.autons.pathplanner.BasicCommands;
import org.mort11.commands.autons.timed.Taxi;
// import org.mort11.commands.autons.timed.TaxiCenterDepot;
// import org.mort11.commands.autons.timed.TaxiLSide;
// import org.mort11.commands.autons.timed.TaxiLSideAnnoy;
import org.mort11.configs.constants.TunerConstants;

import com.pathplanner.lib.path.PathPlannerPath;

import static edu.wpi.first.units.Units.*;
import static org.mort11.configs.constants.PortConstants.Controller.*;

import org.mort11.subsystems.Climber;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.IntakeArm;
import org.mort11.subsystems.IntakeRoller;
import org.mort11.subsystems.Limelight;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;
import org.mort11.subsystems.Vision;
import org.mort11.commands.actions.endeffector.manual.MoveClimber;
import org.mort11.commands.actions.endeffector.manual.MoveFeeder;
import org.mort11.commands.actions.endeffector.manual.MoveFeederOuttake;
import org.mort11.commands.actions.endeffector.manual.MoveFloor;
import org.mort11.commands.actions.endeffector.manual.MoveHood;
import org.mort11.commands.actions.endeffector.manual.PercentShoot;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;

public class RobotContainer {
    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    private double MaxAngularRate = RotationsPerSecond.of(1.25).in(RadiansPerSecond);
    private double currentSpeed = MaxSpeed;
    private double currentAngularRate = MaxAngularRate;
    private final Field2d m_field = new Field2d();

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1)
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);
    private final Limelight limelightThree = new Limelight("limelight-three");
    private final Limelight limelightBack = new Limelight("limelight-back");



    private static final CommandPS5Controller driveController = new CommandPS5Controller(DRIVE_CONTROLLER);
    private static final CommandXboxController endeffectorController = new CommandXboxController(ENDEFFECTOR_CONTROLLER);
    private static final CommandXboxController manualController = new CommandXboxController(MANUAL_CONTROLLER);

    public final static CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    private final OdometryHelper odometry = new OdometryHelper(drivetrain, limelightThree, limelightBack);

    private final Shooter shooter = new Shooter();
    private final Hood hood = new Hood();
    private final IntakeArm intakeArm = new IntakeArm();
    private final IntakeRoller intakeRoller = new IntakeRoller();
    private final Feeder feeder = Feeder.getInstance();
    private final Floor floor = Floor.getInstance();
    private final Climber climber = Climber.getInstance();

    public static SendableChooser<Command> autoChooser;
    public AutoBuilder autoBuilder;

    public RobotContainer() {
        drivetrain.configureAutoBuilder();
        BasicCommands.setCommands(odometry, shooter, intakeArm, intakeRoller, floor, feeder, hood);
        configureBindings();
        configureAuto();
    }

    private void configureBindings() {
        drivetrain.setDefaultCommand(
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-driveController.getLeftY() * currentSpeed)
                    .withVelocityY(-driveController.getLeftX() * currentSpeed)
                    .withRotationalRate(-driveController.getRightX() * currentAngularRate)
            )
        );

        final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
            drivetrain.applyRequest(() -> idle).ignoringDisable(true)
        );

        //---------------------DRIVE CONTROLLER---------------------------
        driveController.cross().whileTrue(drivetrain.applyRequest(() -> brake));
        driveController.circle().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-driveController.getLeftY(), -driveController.getLeftX()))
        ));
        driveController.R2().whileTrue(Commands.runOnce(() -> {
            currentSpeed = 0.4 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
            currentAngularRate = RotationsPerSecond.of(1).in(RadiansPerSecond);
        }));
        driveController.triangle().onTrue(Commands.runOnce(() -> {
            currentSpeed = MaxSpeed;
            currentAngularRate = MaxAngularRate;
        }));
        driveController.L1().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));
        driveController.R1().whileTrue(new RotateToHub(odometry));
        drivetrain.registerTelemetry(logger::telemeterize);

        driveController.povUp().whileTrue(new MoveClimber(0.3));
        driveController.povDown().whileTrue(new MoveClimber(-0.3));
        

        //-----------------------------MANUAL CONTROLLER------------------------------- 

        //roller
        manualController.rightBumper().whileTrue(new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        manualController.rightBumper().whileTrue(new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        //feeder + floor
        manualController.rightTrigger(TRIGGER_THRESHOLD).whileTrue(new MoveFeeder(feeder, floor));
      
        //shooter
        //manualController.y().whileTrue(new SetShooter(shooter, 4000));
        manualController.a().whileTrue(new PercentShoot(shooter, 0.6));
        //hood
        manualController.povUp().whileTrue(new MoveIntakeArm(intakeArm, () -> 0.3));
        manualController.povDown().whileTrue(new MoveIntakeArm(intakeArm, () -> -0.3));

        //floor and feeder tied together 9same as old spindexer feeder)
        //michale climb button (30%)
        manualController.povRight().whileTrue(new MoveClimber(1));
        manualController.povLeft().whileTrue(new MoveClimber(-1));

        //-----------------------END EFFECTOR CONTROLLER------------------------------------

        //arm
        endeffectorController.a().whileTrue(new AgitateArm(intakeArm));

        endeffectorController.povUp().onTrue(Commands.runOnce(() -> intakeArm.setPivot(IntakeArm.Position.HOMED), intakeArm));
        endeffectorController.povDown().onTrue(Commands.runOnce(() -> intakeArm.setPivot(IntakeArm.Position.INTAKE), intakeArm));


        //roller
        endeffectorController.leftBumper().whileTrue(new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.INTAKE));
        endeffectorController.b().whileTrue(new MoveIntakeRoller(intakeRoller, IntakeRoller.Speed.OUTTAKE));

        //feeder + floor
        //endeffectorController.rightTrigger(TRIGGER_THRESHOLD).whileTrue(new MoveFeeder(feeder, floor));
        endeffectorController.rightBumper().whileTrue(new MoveFeederOuttake(feeder, floor));
        endeffectorController.rightTrigger(TRIGGER_THRESHOLD).whileTrue(new SetFeeder(5800));

        //shooter
        //endeffectorController.leftTrigger().whileTrue(new PercentShoot(shooter, 0.8));
        endeffectorController.y().whileTrue(new SetShooter(4000));

        //hood
        endeffectorController.povLeft().whileTrue(new MoveHood(hood, 1.0));
        endeffectorController.povRight().whileTrue(new MoveHood(hood, -1.0));

        endeffectorController.leftTrigger().whileTrue(new PrepareShotCommand(shooter, hood, odometry));        

    }

    public Command getPathPlannerCommand() {
        try {
            PathPlannerPath path = PathPlannerPath.fromPathFile("DriveVertical");
            return AutoBuilder.followPath(path);
        } catch (Exception e) {
            DriverStation.reportError("Big oops: " + e.getMessage(), e.getStackTrace());
            return Commands.none();
        }
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }

    public void configureAuto() {
        autoChooser = new SendableChooser<Command>();
        SmartDashboard.putData("autoChooser", autoChooser);
        autoChooser.setDefaultOption("nothing", null);
       
        //autoChooser.addOption("Depot", new PathPlannerAuto("Depot"));
        
        autoChooser.addOption("Hps", new PathPlannerAuto("Hps"));
        
        autoChooser.addOption("Left Sweep", new PathPlannerAuto("Blue Left Right Sweep"));
        autoChooser.addOption("Left In-out", new PathPlannerAuto("Left In-out"));
        autoChooser.addOption("Left In-out x 2", new PathPlannerAuto("Left In-out x 2"));
        autoChooser.addOption("Right In-out", new PathPlannerAuto("Right In-out"));
        autoChooser.addOption("Right In-out x 2", new PathPlannerAuto("Right In-out x 2"));
        autoChooser.addOption("Right Sweep", new PathPlannerAuto("Right Left Sweep"));
        autoChooser.addOption("Left Close Sweep", new PathPlannerAuto("Closer Left Sweep"));
        autoChooser.addOption("Right Close Sweep", new PathPlannerAuto("Closer Right Sweep"));
        
        // autoChooser.addOption("Timed Center Depot", new TaxiCenterDepot(intakeArm, intakeRoller, shooter, hood, odometry));
        // autoChooser.addOption("Taxi Left Side", new TaxiLSide(intakeArm, intakeRoller, shooter, hood, odometry));

        


        
        
        

        SmartDashboard.putData("Auto Chooser", autoChooser);
        SmartDashboard.putData("Field", m_field);

        PathPlannerLogging.setLogCurrentPoseCallback((pose) -> m_field.setRobotPose(pose));
        PathPlannerLogging.setLogTargetPoseCallback((pose) -> m_field.getObject("target pose").setPose(pose));
        PathPlannerLogging.setLogActivePathCallback((poses) -> m_field.getObject("path").setPoses(poses));
    }

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