// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.*;

public class Drivetrain extends SubsystemBase {
  private CANSparkMax FrontRight = new CANSparkMax(Constants.CAN_RIGHT_FRONT_WHEEL, MotorType.kBrushless);
	private CANSparkMax FrontLeft = new CANSparkMax(Constants.CAN_LEFT_FRONT_WHEEL, MotorType.kBrushless);
	private CANSparkMax RearRight = new CANSparkMax(Constants.CAN_RIGHT_BACK_WHEEL, MotorType.kBrushless);
	private CANSparkMax RearLeft = new CANSparkMax(Constants.CAN_LEFT_BACK_WHEEL, MotorType.kBrushless);

	private CANPIDController rightPID = FrontRight.getPIDController();
	private CANPIDController leftPID = FrontLeft.getPIDController();
	private CANEncoder rightENC = FrontRight.getEncoder();
	private CANEncoder leftENC = FrontLeft.getEncoder();

	Solenoid gearShiftSolenoid = new Solenoid(Constants.SOLENOID_PORT, Constants.PCM_GEAR);
	public DifferentialDrive differentialDrive;

	private boolean stopArcadeDrive;
	private boolean reverse = false;

	public Drivetrain() {
		FrontRight.restoreFactoryDefaults();
		RearRight.restoreFactoryDefaults();
		FrontLeft.restoreFactoryDefaults();
		RearLeft.restoreFactoryDefaults();
		
		// Set the rear drives to follow the left and right front drives
		RearLeft.follow(FrontLeft);
		RearRight.follow(FrontRight);

    differentialDrive = new DifferentialDrive(FrontLeft, FrontRight);

		gyro.calibrate();

		//Resets Encoder Position to 0
		leftENC.setPosition(0);
		rightENC.setPosition(0);

		//Sets the motors to brake mode
		FrontRight.setIdleMode(IdleMode.kBrake);
		RearRight.setIdleMode(IdleMode.kBrake);
		FrontLeft.setIdleMode(IdleMode.kBrake);
		RearLeft.setIdleMode(IdleMode.kBrake);
		FrontLeft.setOpenLoopRampRate(0.1);
		FrontRight.setOpenLoopRampRate(0.1);

		reverse = false;
		differentialDrive.setSafetyEnabled(false);
	}
	
	public void driveWithCurve(double speed, double turn, boolean isQuickTurn) {
		differentialDrive.curvatureDrive(speed, turn, isQuickTurn);
	}
	
	public void arcadeDrive(double forward, double rotation) {
		if(reverse == false){
			differentialDrive.arcadeDrive(-forward, rotation);
		}
		else{
			differentialDrive.arcadeDrive(forward , rotation);
    }
	}

	public void getJoystickValues(Joystick joystick) {
		System.out.println("Y VALUE: " + joystick.getY() + " X VALUE: " + joystick.getX());
	}
	
	public void tankDrive(double leftSpeed, double rightSpeed) {
		differentialDrive.tankDrive(-leftSpeed, -rightSpeed);
	}
	
	public void resetEncoders() {
		leftENC.setPosition(0);
		rightENC.setPosition(0);

		if (getLeftEncoderValue() != 0) {
			System.out.println("ERROR - Could not reset Left encoder!!");
		}

		if (getRightEncoderValue() != 0) {
			System.out.println("ERROR - Could not reset Right encoder!!");
		}
	}

	public void setGear(GearShiftState state) {
	   System.out.println("Trying to shift to gear state " + state);
	   gearShiftSolenoid.set(state==GearShiftState.HI?false:true);
	}

	public void highGear() {
		gearShiftSolenoid.set(true);
	}

	public void lowGear() {
		gearShiftSolenoid.set(false);
	}

	public void reverseDrive(boolean state) {
		reverse = state;
	}
	
	public void resetGyro() {
		gyro.reset();
	}
	
	public CANSparkMax getFrontLeftMotor() {
		return FrontLeft;
	}

	public CANSparkMax getRearLeftMotor() {
	//public TalonSRX getRearLeftMotor() {
		return RearLeft;
	}

	public CANSparkMax getFrontRightMotor() {
		return FrontRight;
	}

	public CANSparkMax getRearRightMotor() {
	//public TalonSRX getRearRightMotor() {
		return RearRight;
	}

	public double getLeftEncoderValue() {
		return leftENC.getPosition();
	}

	public double getRightEncoderValue() {
		return rightENC.getPosition();
	}
	public void zero() {
		startPosition = (int)getLeftEncoderValue();
	}

    public void	setStopArcadeDrive(boolean value) {
		stopArcadeDrive = value;
	}

	public void enableReverse(){
		reverse = true;
	}
	public void disableReverse(){
		reverse= false;
	}

    /**
     * This function is called periodically by Scheduler.run
     */
	@Override
	public void periodic() {
		SmartDashboard.putNumber("Position", getLeftEncoderValue());
	}

   /**
    * Reset the robots sensors to the zero states.
    */
    public void reset() {
      setGear(GearShiftState.LO);
      
      resetEncoders();
      resetGyro();

      stopArcadeDrive = false;

      RearRight.setInverted(false);
      FrontRight.setInverted(false);
      
      zero();
    }

}
