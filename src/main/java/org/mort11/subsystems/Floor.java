package org.mort11.subsystems;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.mort11.configs.constants.PhysicalConstants;
import org.mort11.configs.constants.PortConstants.FloorConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Floor extends SubsystemBase{
    public TalonFX motor;
    public TalonFXConfiguration motorConfigure;
    public static Floor floor;

    private Floor(){
        motor=new TalonFX(FloorConstants.FLOOR_MOTOR);
        motorConfigure=new TalonFXConfiguration().withMotorOutput(new MotorOutputConfigs()
        .withInverted(InvertedValue.Clockwise_Positive) //What direction of rotation should be positive for motor.
        .withNeutralMode(NeutralModeValue.Coast))   //What the motor should do when 0 speed or disabled
        .withCurrentLimits(new CurrentLimitsConfigs()
            .withStatorCurrentLimit(Amps.of(PhysicalConstants.Feeder.FEEDER_STATOR_CURRENT_LIMIT)) // reuse 120A, or add a Floor class
            .withStatorCurrentLimitEnable(true)
            .withSupplyCurrentLimit(Amps.of(PhysicalConstants.Feeder.FEEDER_SUPPLY_CURRENT_LIMIT)) // 50A
            .withSupplyCurrentLimitEnable(true)); 
        motor.getConfigurator().apply(motorConfigure);
    }

    public void setSpeed(double speed){
        motor.set(speed);
    }

    public void setVoltage(double voltage){
        motor.setVoltage(voltage);
    }

    public double getSpeed(){
        return motor.get();
    }

    public double getVoltage(){
        return motor.getMotorVoltage().getValueAsDouble();
    }

    public double getPosition(){
        return motor.getPosition().getValueAsDouble();
    }
    
    @Override
    public void periodic(){
        SmartDashboard.putNumber("Floor Voltage", getVoltage());
        SmartDashboard.putNumber("Floor Position", getPosition());
    }
    
    public static Floor getInstance(){
        if(floor==null){
            floor=new Floor();
        }
        return floor;
    }
}
