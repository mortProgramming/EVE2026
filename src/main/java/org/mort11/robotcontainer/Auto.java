package org.mort11.robotcontainer;

import static org.mort11.configs.constants.PhysicalConstants.CommandSwerveDrivetrainPhysicalConstants.*;

import org.mort11.RobotContainer;
import org.mort11.commands.autons.pathplanner.PathplannerCommands;
import org.mort11.commands.autons.timed.TaxiCenterDepot;
import org.mort11.commands.autons.timed.TaxiLSide;
import org.mort11.subsystems.CommandSwerveDrivetrain;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.ModuleConfig;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.util.PathPlannerLogging;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;


public class Auto {

    private static CommandSwerveDrivetrain drivetrain;

    private static Field2d field;

    private static SendableChooser<Command> autoChooser;
	private static SendableChooser<Command> pathAutoChooser;

    public Auto(){
        drivetrain = RobotContainer.getSwerveDrivetrain();
        field = new Field2d();
        // drivetrain = RobotContainer.drivetrain; //some of the ways to get the drivetrain, not sure which one is better
        configureAutoBuilder();
		addAutoOptions();
        
            }
        
    public static void addAutoOptions(){
        autoChooser = new SendableChooser<Command>();

        SmartDashboard.putData("Auto Chooser", autoChooser);
        SmartDashboard.putData("Field", field);

        autoChooser.setDefaultOption("nothing", null);


        //Pathplanner autos
        autoChooser.addOption("Depot", new PathPlannerAuto("Depot"));
        autoChooser.addOption("Hps", new PathPlannerAuto("Hps"));
        autoChooser.addOption("Left Sweep", new PathPlannerAuto("Blue Left Right Sweep"));
        autoChooser.addOption("Left In-out", new PathPlannerAuto("Left In-out"));
        autoChooser.addOption("Left In-Out-In", new PathPlannerAuto("Left In-Out-In"));
        autoChooser.addOption("Left In-out x 2", new PathPlannerAuto("Left In-out x 2"));
        autoChooser.addOption("Right In-out", new PathPlannerAuto("Right In-out"));
        autoChooser.addOption("Right In-out x 2", new PathPlannerAuto("Right In-out x 2"));
        autoChooser.addOption("Right Sweep", new PathPlannerAuto("Right Left Sweep"));
        autoChooser.addOption("Left Close Sweep", new PathPlannerAuto("Closer Left Sweep"));
        autoChooser.addOption("Right Close Sweep", new PathPlannerAuto("Closer Right Sweep"));
        
        //Timed Autos
        //Recode the timed autos to be singleton and use commands for each thing instead of parameters
        // autoChooser.addOption("Timed Center Depot", new TaxiCenterDepot(intakeArm, intakeRoller, shooter, hood, odometry));
        // autoChooser.addOption("Taxi Left Side", new TaxiLSide(intakeArm, intakeRoller, shooter, hood, odometry));
        
        //Odometry Autos

        //Logging
        PathPlannerLogging.setLogCurrentPoseCallback((pose) -> field.setRobotPose(pose));
        PathPlannerLogging.setLogTargetPoseCallback((pose) -> field.getObject("target pose").setPose(pose));
        PathPlannerLogging.setLogActivePathCallback((poses) -> field.getObject("path").setPoses(poses));
    }
        
    

    
    public static void configureAutoBuilder(){
        drivetrain.configureAutoBuilder();
    }
    

        // figure out why ChaSSIS SPEED supplier isnt working after moving this method from CommandSwerveDrivetrain to here, also make sure the method in CommandSwerveDrivetrain  
    // public void configureAutoBuilder(){
    //     AutoBuilder.configure(
    //         () -> drivetrain.getPose(), // Robot pose supplier
    //         (Pose2d pose) -> drivetrain.resetPose(pose), // Method to reset odometry (will be called if your auto has a starting pose)
    //         () -> drivetrain.getRobotRelativeSpeeds(), // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
    //         (speeds, feedforwards) -> drivetrain.driveRelativeAutobuilder(speeds), // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also optionally outputs individual module feedforwards
    //         new PPHolonomicDriveController( // PPHolonomicController is the built in path following controller for holonomic drive trains
    //                 new PIDConstants(5.0,0,0.0), // Translation PID constants (these are not known rn because robot isnt working?)
    //                 //previously 5 but we changed it to 2.5
    //                 new PIDConstants(2.5, 0.0, 0) // Rotation PID constants
    //         ),
    //         new RobotConfig( //all of these are wrong fix them 
    //             ROBOT_MASS,
    //             ROBOT_MOMENT_OF_INERTIA,
    //             new ModuleConfig(
    //                 WHEEL_DIAMETER / 2, //0.0508 is magic number for now wheel diamter 
    //                 MAX_SPEED,
    //                 WHEEL_COEFFICIENT_OF_FRICTION,
    //                 // DCMotor.getKrakenX60(1).withReduction(5.472),
    //                 DCMotor.getKrakenX60(1).withReduction(1 / DRIVE_REDUCTION),
    //                 DRIVE_MOTOR_CURRENT_LIMIT,
    //                 1
    //             ),
    //             DRIVETRAIN_WHEELBASE_METERS
    //         ), // The robot configuration
    //         () -> {
    //           // Boolean supplier that controls when the path will be mirrored for the red alliance
    //           // This will flip the path being followed to the red side of the field.
    //           // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

    //           var alliance = DriverStation.getAlliance();
    //           if (alliance.isPresent()) {
    //             return alliance.get() == DriverStation.Alliance.Red;
    //           }
    //           return false;
    //         },
    //         this // Reference to this subsystem to set requirements
    //     );
    // }
    
    
    //Change setCommands to not use the subsystems as parameters and instead Commands in commands 
    // public static Command getPlanned(String plan) {
		// PathplannerCommands.setCommands();

	// 	return new PathPlannerAuto(plan);
	// }

	public static Command getAutonomousCommand () {
		return autoChooser.getSelected();
	}
}
