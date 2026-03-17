package org.mort11.subsystems;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.configs.constants.PortConstants;

public class Feeder extends SubsystemBase {

    private static Feeder instance;

    public static Feeder getInstance() {
        if (instance == null) {
            instance = new Feeder();
        }
        return instance;
    }

    public enum Speed {
        FEED(PhysicalConstants.Feeder.FEED_SPEED),
        OUTTAKE(-PhysicalConstants.Feeder.FEED_SPEED);

        private final double percent;

        private Speed(double percent) {
            this.percent = percent;
        }

        public double getPercent() {
            return percent;
        }
    }

    private final TalonFX motor;
    private final VoltageOut voltageRequest = new VoltageOut(0);

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
            );

        motor.getConfigurator().apply(config);
        SmartDashboard.putData(this);
    }

    public void set(Speed speed) {
        motor.setControl(
            voltageRequest.withOutput(Volts.of(speed.getPercent() * PhysicalConstants.ROBOT_VOLTAGE))
        );
    }

    public void stop() {
        motor.setControl(voltageRequest.withOutput(Volts.of(0)));
    }

    public Command feedCommand() {
        return startEnd(() -> set(Speed.FEED), this::stop);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addStringProperty("Command", () -> getCurrentCommand() != null ? getCurrentCommand().getName() : "null", null);
        builder.addDoubleProperty("Stator Current", () -> motor.getStatorCurrent().getValue().in(Amps), null);
        builder.addDoubleProperty("Supply Current", () -> motor.getSupplyCurrent().getValue().in(Amps), null);
    }
}