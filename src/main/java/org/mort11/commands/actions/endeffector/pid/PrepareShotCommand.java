package org.mort11.commands.actions.endeffector.pid;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.interpolation.InterpolatingTreeMap;
import edu.wpi.first.math.interpolation.Interpolator;
import edu.wpi.first.math.interpolation.InverseInterpolator;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import org.mort11.configs.constants.LookUpTableConstants;
import org.mort11.subsystems.Hood;
import org.mort11.subsystems.OdometryHelper;
import org.mort11.subsystems.Shooter;
public class PrepareShotCommand extends Command {
    private static final InterpolatingTreeMap<Distance, Shot> distanceToShotMap = new InterpolatingTreeMap<>(
        (startValue, endValue, q) ->
            InverseInterpolator.forDouble()
                .inverseInterpolate(startValue.in(Meters), endValue.in(Meters), q.in(Meters)),
        (startValue, endValue, t) ->
            new Shot(
                Interpolator.forDouble()
                    .interpolate(startValue.shooterRPM, endValue.shooterRPM, t),
                Interpolator.forDouble()
                    .interpolate(startValue.hoodPosition, endValue.hoodPosition, t)
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

    public PrepareShotCommand(Shooter shooter, Hood hood2, OdometryHelper odometry) {
        this.shooter = shooter;
        this.hood = hood2;
        this.odometry = odometry;
        addRequirements(shooter, hood2);
    }

    public boolean isReadyToShoot() {
        return shooter.isVelocityWithinTolerance() && hood.isPositionWithinTolerance();
    }

    private Distance getDistanceToHub() {
        return Meters.of(odometry.getDistanceToHub());
    }

    @Override
    public void execute() {
        final Distance distanceToHub = getDistanceToHub();
        final Shot shot = distanceToShotMap.get(distanceToHub);
        shooter.setRPM(shot.shooterRPM);
        hood.setPosition(shot.hoodPosition);
        SmartDashboard.putNumber("Distance to Hub (inches)", distanceToHub.in(Inches));
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
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