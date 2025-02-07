// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.AbsoluteEncoderConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.ArmConstants;
import frc.robot.util.ExpandedSubsystem;

public class Arm extends ExpandedSubsystem {
  private SparkMax armMotor;
  private SparkClosedLoopController armPIDController;
  private SparkAbsoluteEncoder armAbsoluteEncoder;

  /** Creates a new CoralArmIntake. */
  public Arm() {
    armMotor = new SparkMax(47, MotorType.kBrushless);
    armAbsoluteEncoder = armMotor.getAbsoluteEncoder();
    armPIDController = armMotor.getClosedLoopController();
    SparkMaxConfig armConfig = new SparkMaxConfig();
    AbsoluteEncoderConfig encoderConfig = new AbsoluteEncoderConfig();

    encoderConfig.inverted(false).positionConversionFactor(1).zeroOffset(0);


    armConfig
        .inverted(false)
        .idleMode(IdleMode.kBrake)
        .smartCurrentLimit(10)
        .secondaryCurrentLimit(15);
    // armConfig.absoluteEncoder.positionConversionFactor(0).velocityConversionFactor(0);
    armConfig
        .closedLoop
        // .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
        .outputRange(-1, 1, ClosedLoopSlot.kSlot0)
        .p(0)
        .i(0)
        .d(0);
    armConfig
        .closedLoop
        .maxMotion
        .maxVelocity(ArmConstants.armMaxVelocity)
        .maxAcceleration(ArmConstants.armMaxAcceleration);
    armConfig
        .softLimit
        .forwardSoftLimit(50)
        .forwardSoftLimitEnabled(true)
        .reverseSoftLimit(-50)
        .reverseSoftLimitEnabled(true);

    armConfig.absoluteEncoder.apply(encoderConfig);

    armMotor.configure(armConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public Command armBottom() {
    return runOnce(()-> armPIDController.setReference(0, ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0));
   
  }

  public Command armTop() {
    return runOnce(()-> armPIDController.setReference(1, ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0));
  }

  public void armSpeed(double speed) {
    armMotor.set(speed);
  }

  public void stopArm() {
    armMotor.set(0);
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Arm Rotations", armAbsoluteEncoder.getPosition());
  }
}
