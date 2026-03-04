package org.mort11.configs.constants;

import com.revrobotics.servohub.ServoChannel.ChannelId;
import com.revrobotics.servohub.ServoHub;
import com.revrobotics.servohub.ServoHub.Bank;

public final class PortConstants {

    public static final class Controller {
        public static final int DRIVE_CONTROLLER = 0;
        public static final int ENDEFFECTOR_CONTROLLER = 1;
        public static final int MANUAL_CONTROLLER = 2;
        public static final int OPERATOR_CONTROLLER = 3;

        public static final double DEAD_BAND = 0.05;
        public static final double TRIGGER_THRESHOLD = 0.2;
        public static final double RUMBLE_AMOUNT = 0.5; //betweem 0 and 1
  }

    public static final class CommandSwerveDrivetrain {
	
	}

    public static final class Feeder {
        public static final int FEEDER_MOTOR = 10;
        public static final int FEEDER_MOTOR_2 = 15; 
    }

    public static final class Hood {
        public static final int servoHood = 41;
        public static final int servoHood2 = 42;
        public static final int canCoderHood = 40;

        public static final int SERVO_HUB = 41;
        public static final int HOOD_CANCODER = 40;

        public static final ChannelId SERVO_CHANNEL1 = ChannelId.kChannelId3;
        public static final ChannelId SERVO_CHANNEL2 = ChannelId.kChannelId4;

        public static final Bank SERVOBANKS = ServoHub.Bank.kBank3_5;
    }


    public static final class Turret {
        public static final int TURRET_MOTOR = 60;
    }

    public static final class Shooter {
        public static final int SHOOTER_LEADER = 12;
        public static final int SHOOTER_FOLLOWER_A = 13;
        public static final int SHOOTER_FOLLOWER_B = 14;
    }

    // public static final class IntakeArmRight {        
    //     public static final int sparkIntakeRight = 15;
    // }

    // public static final class IntakeRollerRight {
    //     public static final int sparkRollRight = 16;
    // }

    public static final class IntakeArmLeft {
        public static final int sparkIntakeLeft = 17;

    }

    public static final class IntakeRollerLeft {
        public static final int sparkRollLeft = 18;
    }

    // public static final class Climber{
    //     public static final int CLIMBER_MOTOR = 19;
    // }
}
