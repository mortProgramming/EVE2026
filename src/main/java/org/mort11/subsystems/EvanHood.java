package org.mort11.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.controller.ProfiledPIDController;

import static org.mort11.configs.constants.PhysicalConstants.Hood.*;
import static org.mort11.configs.constants.PIDConstants.Hood.*;
import static org.mort11.configs.constants.PortConstants.Hood.*;

import org.mort11.configs.LookUpTable;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.ResetMode;
import com.revrobotics.servohub.*;
import com.revrobotics.servohub.config.ServoChannelConfig;
import com.revrobotics.servohub.config.ServoHubConfig;

public class EvanHood extends SubsystemBase {

    private static EvanHood hood;

    private ServoHub servoHub;
    private ServoHubConfig servoHubConfig;
    private ServoChannel servoChannel1;
    private ServoChannel servoChannel2;

    private CANcoder hoodEncoder;
    private CANcoderConfiguration hoodEncoderconfig;

    private double servoSpeed, hoodPosition, rotationsCompleted;

    private ProfiledPIDController controller;

    private EvanHood() {
        hoodEncoder = new CANcoder(HOOD_CANCODER);
        hoodEncoderconfig = new CANcoderConfiguration();

        hoodEncoder.getConfigurator().apply(hoodEncoderconfig);

        servoHub = new ServoHub(SERVO_HUB);
        servoHubConfig = new ServoHubConfig();

        servoHubConfig
            .channel0.pulseRange(MIN_PULSE_WIDTH_SERVO, MIDDLE_PULSE_WIDTH_SERVO, MAX_PULSE_WIDTH_SERVO)
            .disableBehavior(ServoChannelConfig.BehaviorWhenDisabled.kSupplyPower);

        servoHub.configure(servoHubConfig, ResetMode.kResetSafeParameters);

        servoChannel1 = servoHub.getServoChannel(SERVO_CHANNEL1);
        servoChannel2 = servoHub.getServoChannel(SERVO_CHANNEL2);

        servoHub.setBankPulsePeriod(SERVOBANKS, PULSE_PERIOD_WIDTH_SERVO);

        servoChannel1.setPowered(true);
        servoChannel2.setPowered(true);

        servoChannel1.setEnabled(true);
        servoChannel2.setEnabled(true);

        servoChannel1.setPulseWidth(MIDDLE_PULSE_WIDTH_SERVO);
        servoChannel2.setPulseWidth(MIDDLE_PULSE_WIDTH_SERVO);

        hoodPosition = 0;
        rotationsCompleted = HOOD_DEG_OFFSET / DEG_PER_ROTATION;

        controller = new ProfiledPIDController(ROT_KP, ROT_KI, ROT_KD, ROT_CONSTRAINTS);
        controller.setTolerance(ROT_TOLERANCE, ROT_SPEED_TOLERANCE);
    }

    @Override
    public void periodic() {
        servoChannel1.setPulseWidth((int) LookUpTable.limitedMap(servoSpeed, -1, 1, MIN_PULSE_WIDTH_SERVO, MAX_PULSE_WIDTH_SERVO));
        servoChannel2.setPulseWidth((int) LookUpTable.limitedMap(-servoSpeed, -1, 1, MIN_PULSE_WIDTH_SERVO, MAX_PULSE_WIDTH_SERVO));

        hoodPosition = calculateHoodPosition();

        SmartDashboard.putNumber("Hood Pos Deg", getHoodPositionDeg());
        SmartDashboard.putNumber("Hood Speed Deg / sec", getHoodVelocityDeg());
        SmartDashboard.putNumber("Servo Speed", LookUpTable.limitedMap(-servoSpeed, -1, 1, MIN_PULSE_WIDTH_SERVO, MAX_PULSE_WIDTH_SERVO));
    }

    //takes in -1 to 1
    public void setHoodSpeed(double servoSpeed) {
        this.servoSpeed = servoSpeed;
    }



    public double calculateHoodPosition() {
        double degFound = (getAbsoluteEncoderPositionRotations() + rotationsCompleted) * DEG_PER_ROTATION;
        if((degFound - hoodPosition) > MAXIMUM_DEG_CHANGE) {
            rotationsCompleted -= 1;
        }

        if((hoodPosition - degFound) > MAXIMUM_DEG_CHANGE) {
            rotationsCompleted += 1;
        }

        return (getAbsoluteEncoderPositionRotations() + rotationsCompleted) * DEG_PER_ROTATION;
    }



    //may need 1 - 
    public double getAbsoluteEncoderPositionRotations() {
        return hoodEncoder.getPosition().getValueAsDouble();
    }

    public double getHoodPositionDeg() {
        return hoodPosition;
        // return getRelativeHoodPosition();
    }

    public double getHoodVelocityDeg() {
        return hoodEncoder.getVelocity().getValueAsDouble() * DEG_PER_ROTATION;
    }

    public double getServoSpeed(){
        return servoSpeed;
    }

    public ProfiledPIDController getPIDController() {
        return controller;
    }

    // public double getRelativeHoodPosition() {
    //     return (getAbsoluteEncoderPositionRotations() * DEG_PER_ROTATION) + HOOD_START_HEIGHT;
    // }

    public static EvanHood getInstance() {
        if (hood == null) {
            hood = new EvanHood();
        }
        return hood;
    }
}