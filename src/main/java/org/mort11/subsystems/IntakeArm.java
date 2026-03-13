package org.mort11.subsystems;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.configs.constants.PIDConstants;
import org.mort11.configs.constants.PortConstants;

public class IntakeArm extends SubsystemBase {

    public enum Position {
        HOMED(PhysicalConstants.Intake.HOMED_DEG),
        STOWED(PhysicalConstants.Intake.STOWED_DEG),
        INTAKE(PhysicalConstants.Intake.INTAKE_DEG),
        AGITATE(PhysicalConstants.Intake.AGITATE_DEG);

        private final double degrees;

        private Position(double degrees) {
            this.degrees = degrees;
        }

        public Angle angle() {
            return Degrees.of(degrees);
        }
    }

    private static final AngularVelocity kMaxPivotSpeed =
        PhysicalConstants.KrakenX60.kFreeSpeed.div(PhysicalConstants.Intake.PIVOT_REDUCTION);

    private static final Angle kPositionTolerance =
        Degrees.of(PhysicalConstants.Intake.POSITION_TOLERANCE_DEG);

    private final TalonFX pivotMotor;

    private final VoltageOut pivotVoltageRequest = new VoltageOut(0);
    private final MotionMagicVoltage pivotMotionMagicRequest =
        new MotionMagicVoltage(0).withSlot(0);

    private boolean isHomed = false;

    public IntakeArm() {

        pivotMotor = new TalonFX(PortConstants.Intake.INTAKE_PIVOT);

        configurePivotMotor();

        SmartDashboard.putData(this);
    }

    private void configurePivotMotor() {

        final TalonFXConfiguration config = new TalonFXConfiguration()
            .withMotorOutput(
                new MotorOutputConfigs()
                    .withInverted(InvertedValue.CounterClockwise_Positive)
                    .withNeutralMode(NeutralModeValue.Brake)
            )
            .withCurrentLimits(
                new CurrentLimitsConfigs()
                    .withStatorCurrentLimit(Amps.of(PhysicalConstants.Intake.ARM_STATOR_CURRENT_LIMIT))
                    .withStatorCurrentLimitEnable(true)
                    .withSupplyCurrentLimit(Amps.of(PhysicalConstants.Intake.ARM_SUPPLY_CURRENT_LIMIT))
                    .withSupplyCurrentLimitEnable(true)
            )
            .withFeedback(
                new FeedbackConfigs()
                    .withFeedbackSensorSource(FeedbackSensorSourceValue.RotorSensor)
                    .withSensorToMechanismRatio(PhysicalConstants.Intake.PIVOT_REDUCTION)
            )
            .withMotionMagic(
                new MotionMagicConfigs()
                    .withMotionMagicCruiseVelocity(kMaxPivotSpeed)
                    .withMotionMagicAcceleration(kMaxPivotSpeed.per(Second))
            )
            .withSlot0(
                new Slot0Configs()
                    .withKP(PIDConstants.Intake.KP)
                    .withKI(PIDConstants.Intake.KI)
                    .withKD(PIDConstants.Intake.KD)
                    .withKV(12.0 / kMaxPivotSpeed.in(RotationsPerSecond))
            );

        pivotMotor.getConfigurator().apply(config);
    }

    public void setPivot(Position position) {
        pivotMotor.setControl(pivotMotionMagicRequest.withPosition(position.angle()));
    }

    public void setManualPercent(double percent) {
        pivotMotor.setControl(pivotVoltageRequest.withOutput(percent * 12));
    }

public void stop() {

    setManualPercent(0);

}

    public boolean isPositionWithinTolerance() {

        final Angle currentPosition = pivotMotor.getPosition().getValue();
        final Angle targetPosition = pivotMotionMagicRequest.getPositionMeasure();

        return currentPosition.isNear(targetPosition, kPositionTolerance);
    }

    private void setPivotPercentOutput(double percentOutput) {

        pivotMotor.setControl(
            pivotVoltageRequest.withOutput(Volts.of(percentOutput * 12.0))
        );
    }

    public Command homingCommand() {

        return Commands.sequence(

            runOnce(() -> setPivotPercentOutput(0.1)),

            Commands.waitUntil(
                () -> pivotMotor.getSupplyCurrent().getValue().in(Amps) > 6
            ),

            runOnce(() -> {

                pivotMotor.setPosition(Position.HOMED.angle());

                isHomed = true;

                setPivot(Position.STOWED);

            })

        )
        .unless(() -> isHomed)
        .withInterruptBehavior(InterruptionBehavior.kCancelIncoming);
    }

    @Override
    public void initSendable(SendableBuilder builder) {

        builder.addStringProperty(
            "Command",
            () -> getCurrentCommand() != null
                ? getCurrentCommand().getName()
                : "null",
            null
        );

        builder.addDoubleProperty(
            "Angle (degrees)",
            () -> pivotMotor.getPosition().getValue().in(Degrees),
            null
        );

        builder.addDoubleProperty(
            "Pivot Supply Current",
            () -> pivotMotor.getSupplyCurrent().getValue().in(Amps),
            null
        );

        builder.addBooleanProperty(
            "Is Homed",
            () -> isHomed,
            null
        );

        builder.addBooleanProperty(
            "At Position",
            this::isPositionWithinTolerance,
            null
        );
    }
}