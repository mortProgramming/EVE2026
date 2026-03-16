package org.mort11.configs.constants;

import com.revrobotics.servohub.ServoHub;
import com.revrobotics.servohub.ServoChannel.ChannelId;
import com.revrobotics.servohub.ServoHub.Bank;

public final class PortConstants {

    public static final class Controller {
        public static final int DRIVE_CONTROLLER = 0;
        public static final int ENDEFFECTOR_CONTROLLER = 1;
        public static final int MANUAL_CONTROLLER = 2;
        public static final int OPERATOR_CONTROLLER = 3;

        public static final double DEAD_BAND = 0.05;
        public static final double TRIGGER_THRESHOLD = 0.2;
        public static final double RUMBLE_AMOUNT = 0.5;
    }

    public static final class Hood {
        public static final int SERVO_HUB = 17; // CAN ID of your ServoHub
        public static final ChannelId LEFT_SERVO_CHANNEL = ChannelId.kChannelId2;
        public static final ChannelId RIGHT_SERVO_CHANNEL = ChannelId.kChannelId1;
        public static final Bank SERVOBANK = ServoHub.Bank.kBank3_5;
    }

    public static final class Intake {
        public static final int INTAKE_PIVOT = 42;  // replace with actual CAN ID
        public static final int INTAKE_ROLLER = 41; // replace with actual CAN ID
    }

    public static final class Feeder {
        public static final int FEEDER_MOTOR = 13;    // replace with actual CAN ID
    }

    public static final class Shooter {
        public static final int SHOOTER_LEFT = 14;   // replace with actual CAN IDs
        public static final int SHOOTER_MIDDLE = 15;
        public static final int SHOOTER_RIGHT = 16;
    }
    public static final class FloorConstants{
        public static final int FLOOR_MOTOR = 40; //Replace with actual CAN IDs
    }
    	public static final class Climber{
		public static final int CLIMBER_MOTOR = 43;
	}
}