package org.mort11.subsystems;

import static edu.wpi.first.units.Units.Millimeters;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Value;

import com.revrobotics.servohub.ServoChannel;
import com.revrobotics.servohub.ServoHub;
import com.revrobotics.servohub.config.ServoHubConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.configs.constants.PortConstants;

public class Hood extends SubsystemBase {

    private static final Distance kServoLength =
        Millimeters.of(PhysicalConstants.Hood.SERVO_LENGTH_MM);
    private static final LinearVelocity kMaxServoSpeed =
        Millimeters.of(PhysicalConstants.Hood.MAX_SERVO_SPEED_MM_PER_SEC).per(Second);
    private static final double kMinPosition = PhysicalConstants.Hood.MIN_POSITION;
    private static final double kMaxPosition = PhysicalConstants.Hood.MAX_POSITION;
    private static final double kPositionTolerance = PhysicalConstants.Hood.POSITION_TOLERANCE;

    private final ServoHub servoHub;
    private final ServoChannel leftServo;
    private final ServoChannel rightServo;

    private double currentPosition = 0.5;
    private double targetPosition = 0.5;
    private Time lastUpdateTime = Seconds.of(0);

    public Hood() {
        servoHub = new ServoHub(PortConstants.Hood.SERVO_HUB);

        leftServo = servoHub.getServoChannel(PortConstants.Hood.LEFT_SERVO_CHANNEL);
        rightServo = servoHub.getServoChannel(PortConstants.Hood.RIGHT_SERVO_CHANNEL);

        ServoHubConfig config = new ServoHubConfig();
        config.channel1.pulseRange(
            PhysicalConstants.Hood.MIN_PULSE_WIDTH_SERVO,
            PhysicalConstants.Hood.MIDDLE_PULSE_WIDTH_SERVO,
            PhysicalConstants.Hood.MAX_PULSE_WIDTH_SERVO
        );
        config.channel2.pulseRange(
            PhysicalConstants.Hood.MIN_PULSE_WIDTH_SERVO,
            PhysicalConstants.Hood.MIDDLE_PULSE_WIDTH_SERVO,
            PhysicalConstants.Hood.MAX_PULSE_WIDTH_SERVO
        );
        servoHub.configure(config, ServoHub.ResetMode.kResetSafeParameters);

        servoHub.setBankPulsePeriod(PortConstants.Hood.SERVOBANK, PhysicalConstants.Hood.PULSE_PERIOD_WIDTH_SERVO);

        leftServo.setPowered(true);
        rightServo.setPowered(true);
        leftServo.setEnabled(true);
        rightServo.setEnabled(true);

        setPosition(currentPosition);
        SmartDashboard.putData(this);
    }

    

    /** Expects a position between 0.0 and 1.0 */
    public void setPosition(double position) {
        final double clamped = MathUtil.clamp(position, kMinPosition, kMaxPosition);
        final int pulseUs = positionToPulseUs(clamped);
        leftServo.setPulseWidth(pulseUs);
        rightServo.setPulseWidth(pulseUs);
        targetPosition = clamped;
    }

    /** Maps 0.0–1.0 linearly to MIN_PULSE_WIDTH_SERVO–MAX_PULSE_WIDTH_SERVO */
    private int positionToPulseUs(double position) {
        return (int) MathUtil.interpolate(
            PhysicalConstants.Hood.MIN_PULSE_WIDTH_SERVO,
            PhysicalConstants.Hood.MAX_PULSE_WIDTH_SERVO,
            position
        );
    }


    public boolean isPositionWithinTolerance() {
        return MathUtil.isNear(targetPosition, currentPosition, kPositionTolerance);
    }

    private void updateCurrentPosition() {
        final Time currentTime = Seconds.of(Timer.getFPGATimestamp());
        final Time elapsedTime = currentTime.minus(lastUpdateTime);
        lastUpdateTime = currentTime;

        if (isPositionWithinTolerance()) {
            currentPosition = targetPosition;
            return;
        }

        final Distance maxDistanceTraveled = kMaxServoSpeed.times(elapsedTime);
        final double maxPercentageTraveled = maxDistanceTraveled.div(kServoLength).in(Value);

        currentPosition = targetPosition > currentPosition
            ? Math.min(targetPosition, currentPosition + maxPercentageTraveled)
            : Math.max(targetPosition, currentPosition - maxPercentageTraveled);
    }

        public void adjustPosition(double delta) {
            setPosition(targetPosition + delta);
        }

        public double getCurrentPosition() {
            return currentPosition;
        }

    @Override
    public void periodic() {
        updateCurrentPosition();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addStringProperty(
            "Command",
            () -> getCurrentCommand() != null ? getCurrentCommand().getName() : "null",
            null
        );
        builder.addDoubleProperty("Current Position", () -> currentPosition, null);
        builder.addDoubleProperty("Target Position", () -> targetPosition, this::setPosition);
    }
}