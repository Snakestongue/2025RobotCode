// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.CANBus;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  private CANBus canBus = new CANBus("Cannie");

  public Robot() {
    double startTime = Timer.getFPGATimestamp();

    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog());

    m_robotContainer = new RobotContainer();

    double startupTimeSeconds = Timer.getFPGATimestamp() - startTime;
    DataLogManager.log("Startup Time (ms): " + startupTimeSeconds * 1000.0);
  }

  @Override
  public void robotPeriodic() {
    double startTime = Timer.getFPGATimestamp();

    CommandScheduler.getInstance().run();

    SmartDashboard.putNumber("CANivore Utilization %", canBus.getStatus().BusUtilization * 100.0);
    SmartDashboard.putNumber(
        "RIO CAN Utilization %", RobotController.getCANStatus().percentBusUtilization * 100.0);
    SmartDashboard.putNumber("Voltage", RobotController.getBatteryVoltage());
    SmartDashboard.putNumber("CPU Temperature", RobotController.getCPUTemp());
    SmartDashboard.putBoolean("RSL", RobotController.getRSLState());
    SmartDashboard.putNumber("Match Time", DriverStation.getMatchTime());
    SmartDashboard.putNumber("Input Current", RobotController.getInputCurrent());

    double codeRuntime = (Timer.getFPGATimestamp() - startTime) * 1000.0;
    SmartDashboard.putNumber("Code Runtime (ms)", codeRuntime);
  }

  @Override
  public void disabledInit() {
    System.gc();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}
}
