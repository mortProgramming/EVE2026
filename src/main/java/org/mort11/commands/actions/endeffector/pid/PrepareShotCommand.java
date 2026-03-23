package org.mort11.commands.actions.endeffector.pid;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.interpolation.InterpolatingTreeMap;
import edu.wpi.first.math.interpolation.Interpolator;
import edu.wpi.first.math.interpolation.InverseInterpolator;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import org.mort11.RobotContainer;
import org.mort11.configs.constants.LookUpTableConstants;
import org.mort11.subsystems.CommandSwerveDrivetrain;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

public class PrepareShotCommand extends Command {

    //maps distance + RPM + hood position
    private static final InterpolatingTreeMap<Distance, Shot> distanceToShotMap = new InterpolatingTreeMap<>(
        (startValue, endValue, q) ->
            InverseInterpolator.forDouble()
                .inverseInterpolate(startValue.in(Meters), endValue.in(Meters), q.in(Meters)),
        (startValue, endValue, t) ->
            new Shot(
                Interpolator.forDouble().interpolate(startValue.shooterRPM, endValue.shooterRPM, t),
                Interpolator.forDouble().interpolate(startValue.hoodPosition, endValue.hoodPosition, t)
            )
    );
//mpa distnace
    private static final InterpolatingTreeMap<Distance, Double> distanceToTimeMap = new InterpolatingTreeMap<>(
        (startValue, endValue, q) ->
            InverseInterpolator.forDouble()
                .inverseInterpolate(startValue.in(Meters), endValue.in(Meters), q.in(Meters)),
        (startValue, endValue, t) ->
            Interpolator.forDouble().interpolate(startValue, endValue, t)
    );

    static {
        for (double[] entry : LookUpTableConstants.SHOOTER_SUPERSYSTEM) {
            distanceToShotMap.put(Meters.of(entry[0]), new Shot(entry[1], entry[2]));
        }
        for (double[] entry : LookUpTableConstants.timeInAir) {
            distanceToTimeMap.put(Meters.of(entry[0]), entry[1]);
        }
    }

    private final Shooter shooter;
    private final Hood hood;
    private final OdometryHelper odometry;
    private final CommandSwerveDrivetrain drivetrain;

    public PrepareShotCommand(Shooter shooter, Hood hood, OdometryHelper odometry) {
        this.shooter = shooter;
        this.hood = hood;
        this.odometry = odometry;
        this.drivetrain = RobotContainer.getSwerveDrivetrain();
        addRequirements(shooter, hood);
    }

    public boolean isReadyToShoot() {
        return shooter.isVelocityWithinTolerance() && hood.isPositionWithinTolerance();
    }

    private Distance getNewDistance() {
        // robots CURRENT position and the position of the hub
        Translation2d robotPos = odometry.getPose().getTranslation();
        Translation2d hub = odometry.getHubTarget().getTranslation();

        //current distance of robot to use as input to time of flight lookup table
        double currentDistance = robotPos.getDistance(hub);
        double timeOfFlight = distanceToTimeMap.get(Meters.of(currentDistance));

        //robot velocity
        ChassisSpeeds fieldSpeeds = drivetrain.getRobotRelativeSpeeds();
        //convers roborelative to fieldrelative usingthe current heading of the robotform odometry
        double heading = odometry.getPose().getRotation().getRadians();
        double fieldVx = fieldSpeeds.vxMetersPerSecond * Math.cos(heading)
                       - fieldSpeeds.vyMetersPerSecond * Math.sin(heading);
        double fieldVy = fieldSpeeds.vxMetersPerSecond * Math.sin(heading)
                       + fieldSpeeds.vyMetersPerSecond * Math.cos(heading);

        //robot position forward by time of fflight of ball
        Translation2d projectedRobotPos = new Translation2d(
            robotPos.getX() + fieldVx * timeOfFlight,
            robotPos.getY() + fieldVy * timeOfFlight
        );

        //distance from position to hub
        double newDistance = projectedRobotPos.getDistance(hub);

        SmartDashboard.putNumber("Raw Distance to Hub (m)", currentDistance);
        SmartDashboard.putNumber("Compensated Distance to Hub (m)", newDistance);
        SmartDashboard.putNumber("Time of Flight (s)", timeOfFlight);
        SmartDashboard.putNumber("Robot Vx field (m/s)", fieldVx);
        SmartDashboard.putNumber("Robot Vy field (m/s)", fieldVy);

        return Meters.of(newDistance);
    }

    @Override
    public void execute() {
        final Distance newDistance = getNewDistance();
        final Shot shot = distanceToShotMap.get(newDistance);

        shooter.setRPM(shot.shooterRPM);
        hood.setPosition(shot.hoodPosition);

        SmartDashboard.putNumber("Distance to Hub (inches)", newDistance.in(Inches));

        if (isReadyToShoot()) {
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0.5);
        } else {
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
        RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
    }

    public static class Shot {
        public final double shooterRPM;
        public final double hoodPosition;

        public Shot(double shooterRPM, double hoodPosition) {
            this.shooterRPM = shooterRPM;
            this.hoodPosition = hoodPosition;
        }
    }
}