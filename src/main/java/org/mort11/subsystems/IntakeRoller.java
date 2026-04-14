package org.mort11.subsystems;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.configs.constants.PortConstants;

public class IntakeRoller extends SubsystemBase {

    public enum Speed {

        STOP(0),
        INTAKE(PhysicalConstants.Intake.INTAKE_SPEED),
        OUTTAKE(PhysicalConstants.Intake.OUTTAKE_SPEED);

        private final double percentOutput;

        private Speed(double percentOutput) {
            this.percentOutput = percentOutput;
        }

        public Voltage voltage() {
            return Volts.of(percentOutput * 12.0);
        }
    }

    private final TalonFX rollerMotorLeader;
    private final TalonFX rollerMotorFollower;

    private final VoltageOut leaderVoltageRequest   = new VoltageOut(0);
    private final VoltageOut followerVoltageRequest = new VoltageOut(0).withEnableFOC(false);

    public IntakeRoller() {
        rollerMotorLeader   = new TalonFX(PortConstants.Intake.INTAKE_ROLLER_LEFT);
        rollerMotorFollower = new TalonFX(PortConstants.Intake.INTAKE_ROLLER_RIGHT);

        configureRollerMotors();
        SmartDashboard.putData(this);
    }

    private void configureRollerMotors() {
        final TalonFXConfiguration leaderConfig = new TalonFXConfiguration()
            .withMotorOutput(
                new MotorOutputConfigs()
                    .withInverted(InvertedValue.Clockwise_Positive)
                    .withNeutralMode(NeutralModeValue.Coast)
            )
            .withCurrentLimits(
                new CurrentLimitsConfigs()
                    .withStatorCurrentLimit(Amps.of(PhysicalConstants.Intake.ROLLER_STATOR_CURRENT_LIMIT))
                    .withStatorCurrentLimitEnable(true)
                    .withSupplyCurrentLimit(Amps.of(PhysicalConstants.Intake.ROLLER_SUPPLY_CURRENT_LIMIT))
                    .withSupplyCurrentLimitEnable(true)
            );

        // Inverted to match leader's physical spin direction (motors face opposite directions)
        final TalonFXConfiguration followerConfig = new TalonFXConfiguration()
            .withMotorOutput(
                new MotorOutputConfigs()
                    .withInverted(InvertedValue.CounterClockwise_Positive)
                    .withNeutralMode(NeutralModeValue.Coast)
            )
            .withCurrentLimits(
                new CurrentLimitsConfigs()
                    .withStatorCurrentLimit(Amps.of(PhysicalConstants.Intake.ROLLER_STATOR_CURRENT_LIMIT))
                    .withStatorCurrentLimitEnable(true)
                    .withSupplyCurrentLimit(Amps.of(PhysicalConstants.Intake.ROLLER_SUPPLY_CURRENT_LIMIT))
                    .withSupplyCurrentLimitEnable(true)
            );

        rollerMotorLeader.getConfigurator().apply(leaderConfig);
        rollerMotorFollower.getConfigurator().apply(followerConfig);
    }

    @Override
    public void periodic() {
    }

    public void setRoller(Speed speed) {
        rollerMotorLeader.setControl(leaderVoltageRequest.withOutput(speed.voltage()));
        rollerMotorFollower.setControl(followerVoltageRequest.withOutput(speed.voltage()));
    }

    public void stop() {
        setRoller(Speed.STOP);
    }

    @Override
    public void initSendable(SendableBuilder builder) {

        builder.addStringProperty(
            "Command",
            () -> getCurrentCommand() != null ? getCurrentCommand().getName() : "null",
            null
        );

        builder.addDoubleProperty(
            "Leader RPM",
            () -> rollerMotorLeader.getVelocity().getValue().in(RPM),
            null
        );

        builder.addDoubleProperty(
            "Follower RPM",
            () -> rollerMotorFollower.getVelocity().getValue().in(RPM),
            null
        );

        builder.addDoubleProperty(
            "Leader Supply Current",
            () -> rollerMotorLeader.getSupplyCurrent().getValue().in(Amps),
            null
        );

        builder.addDoubleProperty(
            "Follower Supply Current",
            () -> rollerMotorFollower.getSupplyCurrent().getValue().in(Amps),
            null
        );
    }
}