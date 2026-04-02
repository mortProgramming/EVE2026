package org.mort11.subsystems;

import static edu.wpi.first.units.Units.Amps;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.configs.constants.PortConstants;
import org.mort11.configs.constants.PIDConstants;

public class Feeder extends SubsystemBase {

    private static Feeder instance;

    public static Feeder getInstance() {
        if (instance == null) {
            instance = new Feeder();
        }
        return instance;
    }

    public enum Speed {
        FEED(PhysicalConstants.Feeder.FEED_RPM),
        OUTTAKE(-PhysicalConstants.Feeder.FEED_RPM);

        private final double rpm;

        private Speed(double rpm) {
            this.rpm = rpm;
        }

        public double getRPM() {
            return rpm;
        }
    }

    private final TalonFX motor;
    private final VelocityVoltage velocityRequest = new VelocityVoltage(0).withSlot(0);

    private Feeder() {
        motor = new TalonFX(PortConstants.Feeder.FEEDER_MOTOR);

        final TalonFXConfiguration config = new TalonFXConfiguration()
            .withMotorOutput(
                new MotorOutputConfigs()
                    .withInverted(InvertedValue.CounterClockwise_Positive)
                    .withNeutralMode(NeutralModeValue.Coast)
            )
            .withCurrentLimits(
                new CurrentLimitsConfigs()
                    .withStatorCurrentLimit(Amps.of(PhysicalConstants.Feeder.FEEDER_STATOR_CURRENT_LIMIT))
                    .withStatorCurrentLimitEnable(true)
                    .withSupplyCurrentLimit(Amps.of(PhysicalConstants.Feeder.FEEDER_SUPPLY_CURRENT_LIMIT))
                    .withSupplyCurrentLimitEnable(true)
            )
            .withSlot0(
                new Slot0Configs()
                    .withKP(PIDConstants.Feeder.KP)
                    .withKI(PIDConstants.Feeder.KI)
                    .withKD(PIDConstants.Feeder.KD)
                    .withKV(PIDConstants.Feeder.KV) 
            );

        motor.getConfigurator().apply(config);
        SmartDashboard.putData(this);
    }

    public void set(Speed speed) {
        setRPM(speed.getRPM());
    }

    public void setRPM(double rpm) {
        double rps = rpm / 60.0; 
        motor.setControl(velocityRequest.withVelocity(rps));
    }

    public void stop() {
        motor.setControl(velocityRequest.withVelocity(0));
    }

    public double getCurrentRPM() {
        return motor.getVelocity().getValueAsDouble() * 60.0;
    }

    public Command feedCommand() {
        return startEnd(() -> set(Speed.FEED), this::stop);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addStringProperty("Command", () -> getCurrentCommand() != null ? getCurrentCommand().getName() : "null", null);
        builder.addDoubleProperty("Current RPM", this::getCurrentRPM, null);
        builder.addDoubleProperty("Stator Current", () -> motor.getStatorCurrent().getValue().in(Amps), null);
        builder.addDoubleProperty("Supply Current", () -> motor.getSupplyCurrent().getValue().in(Amps), null);
    }
}