// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.util.ExpandedSubsystem;

@Logged
public class Elevator extends ExpandedSubsystem {
  /** Creates a new Elevator. */
  private TalonFX elevatorMainMotor;

  private TalonFX elevatorFollowerMotor;

  private final MotionMagicVoltage motionMagicRequest = new MotionMagicVoltage(0);
  private final VoltageOut voltageRequest = new VoltageOut(0.0);

  private DigitalInput buttonSwitch = new DigitalInput(ElevatorConstants.buttonSwitchID);
  private boolean isZeroed = false;
  private Alert elevatorAlert;
  private boolean lastButtonState = false;

  public Elevator() {
    elevatorMainMotor = new TalonFX(ElevatorConstants.elevatorMainMotorID);
    elevatorFollowerMotor = new TalonFX(ElevatorConstants.elevatorFollowerMotorID);
    // follower = new Follower(ElevatorConstants.elevatorMainMotorID, false);

    // currentPosition.setUpdateFrequency(50);

    elevatorMainMotor.getConfigurator().apply(ElevatorConstants.elevatorConfigs);
    elevatorFollowerMotor.getConfigurator().apply(ElevatorConstants.elevatorConfigs);

    // elevatorFollowerMotor.setControl(follower);

    elevatorAlert = new Alert("Elevator is not Zeroed!", AlertType.kWarning);
    // elevatorMainMotor.setPosition(0.0);
    // elevatorFollowerMotor.setPosition(0.0);
  }

  public boolean buttonPressed() {
    return !buttonSwitch.get();
  }

  public Command homeElevator() {
    return downSpeed(0.2)
        .until(this::buttonPressed)
        .unless(this::buttonPressed)
        .finallyDo(this::stopElevator);
  }

  public void stopElevator() {
    elevatorMainMotor.set(0);
    elevatorFollowerMotor.set(0);
  }

  public Command upSpeed(double speed) {
    return run(
        () -> {
          elevatorMainMotor.set(speed);
          elevatorFollowerMotor.set(speed);
        });
  }

  public Command downSpeed(double speed) {
    return run(() -> {
          elevatorMainMotor.set(-speed);
          elevatorFollowerMotor.set(-speed);
        })
        .until(this::buttonPressed)
        .unless(this::buttonPressed);
  }

  public Command moveToPosition(double height) {
    return runOnce(
            () -> {
              elevatorMainMotor.setControl(motionMagicRequest.withPosition(height));
              elevatorFollowerMotor.setControl(motionMagicRequest.withPosition(height));
            })
        .onlyIf(() -> isZeroed);
  }

  public Command downPosition() {
    return moveToPosition(ElevatorConstants.downHeight).until(this::buttonPressed);
  }

  public Command holdPosition() {
    return startRun(
            () -> {
              elevatorMainMotor.setControl(
                  motionMagicRequest.withPosition(
                      elevatorMainMotor.getPosition().getValueAsDouble()));
              elevatorFollowerMotor.setControl(
                  motionMagicRequest.withPosition(
                      elevatorFollowerMotor.getPosition().getValueAsDouble()));
            },
            () -> elevatorMainMotor.setControl(motionMagicRequest))
        .withName("Elevator Hold");
  }

  private final SysIdRoutine elevatorSysIdRoutine =
      new SysIdRoutine(
          new SysIdRoutine.Config(
              Volts.of(1.0).per(Second), // Use default ramp rate (1 V/s)
              Volts.of(4.0), // Reduce dynamic step voltage to 4 V to prevent brownout
              null, // Use default timeout (10 s)
              // Log state with SignalLogger class
              state -> SignalLogger.writeString("SysIdElevator_State", state.toString())),
          new SysIdRoutine.Mechanism(
              (volts) -> elevatorMainMotor.setControl(voltageRequest.withOutput(volts.in(Volts))),
              null,
              this));

  public Command sysIdQuasistaticElevator(SysIdRoutine.Direction direction) {
    return elevatorSysIdRoutine.quasistatic(direction);
  }

  public Command sysIdDynamicElevator(SysIdRoutine.Direction direction) {
    return elevatorSysIdRoutine.dynamic(direction);
  }

  @Override
  public void periodic() {

    SmartDashboard.putNumber(
        "Elevator Main Position",
        Units.metersToInches(elevatorMainMotor.getPosition().getValueAsDouble()));
    SmartDashboard.putNumber(
        "Elevator Follower Position",
        Units.metersToInches(elevatorFollowerMotor.getPosition().getValueAsDouble()));
    SmartDashboard.putBoolean("Button Pressed", buttonPressed());

    boolean currentButtonState = buttonPressed();

    if (currentButtonState && !lastButtonState) {
      elevatorMainMotor.setPosition(0, 0);
      isZeroed = true;
    }

    lastButtonState = currentButtonState;

    if (!isZeroed && buttonPressed()) {
      elevatorMainMotor.setPosition(0, 0);
      isZeroed = true;
    }

    if (isZeroed
        && Units.metersToInches(elevatorMainMotor.getPosition().getValueAsDouble()) < 1
        && !buttonPressed()) {
      isZeroed = false;
    }

    elevatorAlert.set(!isZeroed);
  }

public Command outakeLaserBroken() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'outakeLaserBroken'");
}
}
