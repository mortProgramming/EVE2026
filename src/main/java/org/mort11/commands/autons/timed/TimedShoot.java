package org.mort11.commands.autons.timed;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.interpolation.InterpolatingTreeMap;
import edu.wpi.first.math.interpolation.Interpolator;
import edu.wpi.first.math.interpolation.InverseInterpolator;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

import org.mort11.configs.constants.LookUpTableConstants;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;

/**
 * Spins up the shooter and sets the hood using the same distance-based
 * lookup table as PrepareShotCommand, but runs for a fixed duration.
 *
 * Use in auton paired with TimedFeed in a ParallelCommandGroup.
 */
public class TimedShoot extends Command {

    // Mirrors the Shot inner class from PrepareShotCommand
    private static class Shot {
        final double shooterRPM;
        final double hoodPosition;

        Shot(double shooterRPM, double hoodPosition) {
            this.shooterRPM = shooterRPM;
            this.hoodPosition = hoodPosition;
        }
    }

    // Same lookup table setup as PrepareShotCommand
    private static final InterpolatingTreeMap<Distance, Shot> distanceToShotMap =
        new InterpolatingTreeMap<>(
            (startValue, endValue, q) ->
                InverseInterpolator.forDouble()
                    .inverseInterpolate(startValue.in(Meters), endValue.in(Meters), q.in(Meters)),
            (startValue, endValue, t) ->
                new Shot(
                    Interpolator.forDouble().interpolate(startValue.shooterRPM, endValue.shooterRPM, t),
                    Interpolator.forDouble().interpolate(startValue.hoodPosition, endValue.hoodPosition, t)
                )
        );

    static {
        for (double[] entry : LookUpTableConstants.SHOOTER_SUPERSYSTEM) {
            distanceToShotMap.put(Meters.of(entry[0]), new Shot(entry[1], entry[2]));
        }
    }

    private final Shooter shooter;
    private final Hood hood;
    private final OdometryHelper odometry;
    private final Timer timer;
    private final double time;

    /**
     * @param time     How long to spin up / shoot (seconds)
     * @param shooter  Shooter subsystem instance
     * @param hood     Hood subsystem instance
     * @param odometry OdometryHelper used to look up distance → RPM + hood position
     */
    public TimedShoot(double time, Shooter shooter, Hood hood, OdometryHelper odometry) {
        this.shooter = shooter;
        this.hood = hood;
        this.odometry = odometry;
        this.time = time;
        this.timer = new Timer();
        addRequirements(shooter, hood);
    }

    @Override
    public void initialize() {
        timer.reset();
        timer.start();
    }

    @Override
    public void execute() {
        Translation2d robotPos = odometry.getPose().getTranslation();
        Translation2d hub = odometry.getHubTarget().getTranslation();
        double distanceMeters = robotPos.getDistance(hub);

        Shot shot = distanceToShotMap.get(Meters.of(distanceMeters));

        shooter.setRPM(shot.shooterRPM);
        hood.setPosition(shot.hoodPosition);
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
    }

    @Override
    public boolean isFinished() {
        return timer.get() > time;
    }
}