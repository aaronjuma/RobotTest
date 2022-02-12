/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.utils.GamepadRightAxis;

//Subsystems Imports
import frc.subsystems.*;

//Commands Imports
import frc.commands.DefaultDrive;
import frc.commands.ShootSpeed;
import frc.commands.intakeArm;
import frc.commands.AlignVision;
import frc.commands.Shoot;
import frc.commands.IntakeTrigger;
import frc.commands.IntakeAndElevator;

/**
 * Add your docs here.
 */
public class RobotContainer {    
    //Joystick and Gamepad
    public final Joystick joystick = new Joystick(0); 
    public final Joystick gamepad = new Joystick(1);
   
    //Subsystems
    private final Drivetrain drivetrain = new Drivetrain();
    private final ShooterWheel shooterwheel = new ShooterWheel();
    private final Elevator elevator = new Elevator();
    private final VisionArduino vision = new VisionArduino();
    private final Intake intake = new Intake();

    private UsbCamera camera;
    private GamepadRightAxis rightAxis = new GamepadRightAxis(gamepad);
    
    //Joystick and Gamepad buttons
    //joystick
    private final JoystickButton jsButnIntakePowerCell           = new JoystickButton(this.joystick, 1);
    private final JoystickButton gpButnRunPixyCam                = new JoystickButton(this.joystick, 2);
    private final JoystickButton jsButnRaiseAndLowerControlPanel = new JoystickButton(this.joystick, 3);
    private final JoystickButton jsButnRotationControl           = new JoystickButton(this.joystick, 4);
    private final JoystickButton jsButnReverse                   = new JoystickButton(this.joystick, 5);
    private final JoystickButton jsButnDetectColour              = new JoystickButton(this.joystick, 6);
    private final JoystickButton jsButnRaiseClimb                = new JoystickButton(this.joystick, 7);
    private final JoystickButton jsButnLowerClimb                = new JoystickButton(this.joystick, 8);
    private final JoystickButton jsButnShifter                   = new JoystickButton(this.joystick, 12);
  
    //gamepad
    private final JoystickButton gpButnShoot                     = new JoystickButton(this.gamepad, 1);
    private final JoystickButton gpButnManualElevator            = new JoystickButton(this.gamepad, 2);
    private final JoystickButton gpButnIntakeDownAndUp           = new JoystickButton(this.gamepad, 4);
    private final JoystickButton gpButnShooter                   = new JoystickButton(this.gamepad, 6);
    private final Trigger        gpButnIntake                    = new Trigger(rightAxis::get);

    public RobotContainer(){
        configureButtons();
        camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(160, 120);
        camera.setFPS(30);
        
    }


    private void configureButtons() {
        //Instant commands
        /*GEAR SHIFTER*/
        this.jsButnShifter.whenPressed(new InstantCommand(drivetrain::highGear, drivetrain));
        this.jsButnShifter.whenReleased(new InstantCommand(drivetrain::lowGear, drivetrain));
        this.jsButnReverse.whenPressed(new InstantCommand(drivetrain::resetEncoders, drivetrain));

        /*SHOOTER*/
        this.gpButnShoot.whileHeld(new InstantCommand(shooterwheel::shoot, shooterwheel)); //Manual
        this.gpButnShoot.whenReleased(new InstantCommand(shooterwheel::stopMotor, shooterwheel));
        this.gpButnShooter.whileHeld(new Shoot(elevator, shooterwheel)); //With the elevator
        
        /*ELEVATOR*/
        this.gpButnManualElevator.whileHeld(new InstantCommand(elevator::startMotor, elevator)); //Manual
        this.gpButnManualElevator.whenReleased(new InstantCommand(elevator::stopMotor, elevator));

        /*INTAKE*/
        // Removed this, this is a trigger now
        this.gpButnIntake.whileActiveOnce(new IntakeAndElevator(elevator, gamepad, intake));
        this.gpButnIntakeDownAndUp.whenPressed(new intakeArm(intake));

        /*PIXYCAM*/
        this.gpButnRunPixyCam.whileHeld(new AlignVision(drivetrain, vision));
    }


    public void enableDefaultCommands() {
        this.drivetrain.setDefaultCommand(new DefaultDrive(drivetrain, joystick));
        this.shooterwheel.setDefaultCommand(new ShootSpeed(this.shooterwheel, this.gamepad));
    }

}
