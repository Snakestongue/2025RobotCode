// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.SwerveConstants;
import frc.robot.commands.TeleopSwerve;
import frc.robot.subsystems.AlgaeIntake;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.GroundIntake;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Outtake;
import frc.robot.subsystems.Swerve;
import frc.robot.util.LogUtil;
import com.pathplanner.lib.auto.NamedCommands;
import frc.robot.util.PersistentSendableChooser;

public class RobotContainer {
  private final Elevator elevator = new Elevator();
  private final Arm arm = new Arm();
  private final Indexer indexer = new Indexer();
  private final Outtake outtake = new Outtake();
  private final GroundIntake groundIntake = new GroundIntake();
  private final Telemetry logger = new Telemetry(15);
  private final AlgaeIntake algaeIntake = new AlgaeIntake();
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

  private PersistentSendableChooser<String> batteryChooser;
  private SendableChooser<Command> autoChooser;

  private final CommandXboxController driverController = new CommandXboxController(0);
  private final CommandJoystick operatorStick = new CommandJoystick(1);

  public final Swerve drivetrain = frc.robot.util.TunerConstants.createDrivetrain();

  private final PowerDistribution powerDistribution = new PowerDistribution();

<<<<<<< HEAD
<<<<<<< HEAD
  private Trigger intakeLaserBroken = new Trigger(groundIntake::intakeLaserBroken());
  private Trigger outakeLaserBroken = new Trigger(indexer::outakeLaserBroken);
=======
  private Trigger intakeLaserBroken = new Trigger(groundIntake::intakeLaserBroken);
=======
>>>>>>> bae50d8cd504db551bb672f071f787e27348d379
  private Trigger outtakeLaserBroken = new Trigger(outtake::outtakeLaserBroken);

>>>>>>> 0a1b3a605242d37e56815bafa1fe854ccd591f77
  private Trigger buttonTrigger = new Trigger(elevator::buttonPressed);
  private Trigger armMode = operatorStick.button(OperatorConstants.armModeButton);

  // Just put a bunch of instantcommands as placeholders for now
  Command outtakePrematch = new InstantCommand();
  Command algaeIntakePrematch = new InstantCommand();
  Command armPrematch = new InstantCommand();
  Command elevatorPrematch = new InstantCommand();
  Command groundIntakePrematch = groundIntake.buildPrematch();
  Command indexerPrematch = indexer.buildPrematch();
  Command swervePrematch = new InstantCommand();

  public RobotContainer() {

    configureDriverBindings();
    configureOperatorBindings();
    configureAutoChooser();
    configureBatteryChooser();

    NamedCommands.registerCommand("Stop ground Intake", groundIntake.runIntake().asProxy());
    NamedCommands.registerCommand("Stop ground Intake", groundIntake.intakeLaserBroken().asProxy());
    NamedCommands.registerCommand("Stop ground Intake", groundIntake.stopGroundIntake().asProxy());

    NamedCommands.registerCommand("Start elevator", elevator.().asProxy());
    NamedCommands.registerCommand("Start elevator", elevator.outakeLaserBroken().asProxy());

    NamedCommands.registerCommand("indexer", indexer.buttonTrigger().asProxy());
    NamedCommands.registerCommand("indexer", indexer.stopIndexer().asProxy());


    SmartDashboard.putData("Power Distribution", powerDistribution);
    SmartDashboard.putData("Command Scheduler", CommandScheduler.getInstance());

    NamedCommands.registerCommand("Start Indexer", indexer.runIndexer().asProxy());
    NamedCommands.registerCommand("Elevator: L4", elevator.moveToPosition(ElevatorConstants.L4Height).withTimeout(2.5).asProxy());
    NamedCommands.registerCommand("Auto Outtake", outtake.autoOuttake().withTimeout(1.5).asProxy());
    NamedCommands.registerCommand("Outtake", outtake.runOuttake().withTimeout(1.5).asProxy());
    NamedCommands.registerCommand("Elevator: Bottom", elevator.downPosition().withTimeout(2.5).asProxy());
    NamedCommands.registerCommand("OuttakeUntilBeamBreak", outtake.outtakeUntilBeamBreak().withTimeout(5).asProxy());



    // intakeLaserBroken
    //     .whileTrue(indexer.runIndexer())
    //     .onFalse(
    //         Commands.race(Commands.waitUntil(outtakeLaserBroken), Commands.waitSeconds(4))
    //             .andThen(indexer::stopIndexer));

    new Trigger(outtakeLaserBroken)
        .onTrue(
            Commands.sequence(
                Commands.runOnce(
                    () -> driverController.getHID().setRumble(RumbleType.kBothRumble, 1)),
                Commands.waitSeconds(2),
                Commands.runOnce(
                    () -> driverController.getHID().setRumble(RumbleType.kBothRumble, 0.0))));
  }
  
  private void configureDriverBindings() {
    Trigger slowMode = driverController.leftTrigger();

    drivetrain.setDefaultCommand(
        new TeleopSwerve(
            driverController::getLeftY,
            driverController::getLeftX,
            driverController::getRightX,
            () -> {
              if (slowMode.getAsBoolean()) {
                return SwerveConstants.slowModeMaxTranslationalSpeed;
              }
              return SwerveConstants.maxTranslationalSpeed;
            },
            drivetrain));

    driverController.b().whileTrue(drivetrain.applyRequest(() -> brake));
    driverController
        .a()
        .whileTrue(
            drivetrain.applyRequest(
                () ->
                    point.withModuleDirection(
                        new Rotation2d(
                            -driverController.getLeftY(), -driverController.getLeftX()))));

    // driverController.L1().whileTrue(drivetrain.ReefAlign(true));
    // driverController.R1().whileTrue(drivetrain.ReefAlign(false));

    // driverController.R2().whileTrue(new TurnToReef(drivetrain));
    driverController.rightTrigger().whileTrue(drivetrain.humanPlayerAlign());

    driverController
        .leftBumper()
        .whileTrue(
            Commands.sequence(
                // drivetrain.pathFindToSetup(),
                // new TurnToReef(drivetrain),
                // Commands.waitSeconds(.08),
                drivetrain.ReefAlign(true)));
    driverController
        .rightBumper()
        .whileTrue(
            Commands.sequence(
                // drivetrain.pathFindToSetup(),
                // new TurnToReef(drivetrain),
                // Commands.waitSeconds(.08),
                drivetrain.ReefAlign(false)));

    // driverController.leftBumper().whileTrue(drivetrain.ReefAlignNoVision(true));

    // driverController.rightBumper().whileTrue(drivetrain.ReefAlignNoVision(false));

    // reset the field-centric heading on left bumper press
    driverController
        .back()
        .and(driverController.start())
        .onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric).ignoringDisable(true));

    drivetrain.registerTelemetry(logger::telemeterize);
  }

  private void configureElevatorBindings() {

    elevator.setDefaultCommand(elevator.holdPosition());

    operatorStick
        .button(OperatorConstants.L4HeightButton)
        .and(armMode.negate())
        .onTrue(elevator.moveToPosition(ElevatorConstants.L4Height));
    operatorStick
        .button(OperatorConstants.L3HeightButton)
        .and(armMode.negate())
        .onTrue(elevator.moveToPosition(ElevatorConstants.L3Height));
    operatorStick
        .button(OperatorConstants.L2HeightButton)
        .and(armMode.negate())
        .onTrue(elevator.moveToPosition(ElevatorConstants.L2Height));

    operatorStick
        .button(OperatorConstants.elevatorDownButton)
        .and(armMode.negate())
        .onTrue(elevator.downPosition());

    operatorStick
        .button(OperatorConstants.homeElevatorButon)
        .and(armMode.negate())
        .whileTrue(elevator.homeElevator())
        .onFalse(elevator.runOnce(() -> elevator.stopElevator()));

    operatorStick
        .button(OperatorConstants.elevatorManualDown)
        .and(armMode.negate())
        .whileTrue(elevator.downSpeed(.1))
        .onFalse(elevator.runOnce(() -> elevator.stopElevator()));

    operatorStick
        .button(OperatorConstants.elevatorManualUp)
        .and(armMode.negate())
        .whileTrue(elevator.upSpeed(.1))
        .onFalse(elevator.runOnce(() -> elevator.stopElevator()));
  }

  private void configureArmBindings() {
    operatorStick
        .button(OperatorConstants.groundIntakeButton)
        .and(armMode)
        .whileTrue(groundIntake.runIntake())
        .onFalse(groundIntake.stop());

    operatorStick
        .button(OperatorConstants.armManualOuttakeButton)
        .and(armMode)
        .whileTrue(groundIntake.run(groundIntake::manualOuttake))
        .onFalse(groundIntake.stop());

    // Button to raise arm manual up

    // button to raise arm manual down

    // arm to pick up button

    // arm to L1 height button
  }

  private void configureOuttakeBindings() {
    operatorStick
        .button(OperatorConstants.outtakeButton) 
        .and(armMode.negate())
        .whileTrue(outtake.runOuttake())//outtake.autoOuttake()
        .onFalse(outtake.stopOuttakeMotor());
  }

  private void configureIndexerBindings() {
    operatorStick
        .button(OperatorConstants.indexerButton)
        .and(armMode.negate())
        .whileTrue(indexer.runIndexer())
        .onFalse(indexer.stop());

    operatorStick
        .button(OperatorConstants.indexerButton)
        .and(armMode.negate())
        // .and(outtakeLaserBroken.negate())
        .whileTrue(outtake.outtakeUntilBeamBreak())
        .onFalse(outtake.stopOuttakeMotor());

    operatorStick
        .button(OperatorConstants.outtakeIndexerButton)
        .whileTrue(indexer.outtakeIndexer())
        .onFalse(indexer.stop());
  }

  private void configureAlgaeIntakeBindings() {
    operatorStick
        .button(OperatorConstants.algaeIntakeUp)
        .whileTrue(algaeIntake.run(algaeIntake::algaeIntakeUp))
        .onFalse(algaeIntake.runOnce(algaeIntake::stopAlgaeIntake));
    operatorStick
        .button(OperatorConstants.algaeIntakeDown)
        .whileTrue(algaeIntake.run(algaeIntake::algaeIntakeDown))
        .onFalse(algaeIntake.runOnce(algaeIntake::stopAlgaeIntake));
  }

  private void configureOperatorBindings() {
    configureAlgaeIntakeBindings();
    configureArmBindings();
    configureElevatorBindings();
    configureIndexerBindings();
    configureOuttakeBindings();

    operatorStick
        .button(OperatorConstants.startingConfigButton)
        .whileTrue(
            elevator
                .downPosition()
                .andThen(arm.armBottom())
                .andThen(
                    Commands.sequence(
                        algaeIntake.run(algaeIntake::algaeIntakeUp),
                        Commands.waitSeconds(1.5),
                        algaeIntake.runOnce(algaeIntake::stopAlgaeIntake))))
        .onFalse(
            algaeIntake
                .runOnce(algaeIntake::stopAlgaeIntake)
                .andThen(elevator.runOnce(elevator::stopElevator))
                .andThen(arm.runOnce(arm::stopArm)));
  }

  private void configureAutoChooser() {
    autoChooser = AutoBuilder.buildAutoChooser();

    autoChooser.addOption(
        "[SysID] Quasistatic Steer Forward", drivetrain.sysIdQuasistaticSteer(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Quasistatic Steer Reverse", drivetrain.sysIdQuasistaticSteer(Direction.kReverse));
    autoChooser.addOption(
        "[SysID] Dynamic Steer Forward", drivetrain.sysIdDynamicSteer(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Dynamic Steer Reverse", drivetrain.sysIdDynamicSteer(Direction.kReverse));

    autoChooser.addOption(
        "[SysID] Quasistatic Translation Forward",
        drivetrain.sysIdQuasistaticTranslation(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Quasistatic Translation Reverse",
        drivetrain.sysIdQuasistaticTranslation(Direction.kReverse));
    autoChooser.addOption(
        "[SysID] Dynamic Translation Forward",
        drivetrain.sysIdDynamicTranslation(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Dynamic Translation Reverse",
        drivetrain.sysIdDynamicTranslation(Direction.kReverse));

    autoChooser.addOption(
        "[SysID] Quasistatic Rotation Forward",
        drivetrain.sysIdQuasistaticRotation(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Quasistatic Rotation Reverse",
        drivetrain.sysIdQuasistaticRotation(Direction.kReverse));
    autoChooser.addOption(
        "[SysID] Dynamic Rotation Forward", drivetrain.sysIdDynamicRotation(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Dynamic Rotation Reverse", drivetrain.sysIdDynamicRotation(Direction.kReverse));

    autoChooser.addOption(
        "[SysID] Elevator Quasistatic Forward",
        elevator.sysIdQuasistaticElevator(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Elevator Quasistatic Reverse",
        elevator.sysIdQuasistaticElevator(Direction.kReverse));
    autoChooser.addOption(
        "[SysID] Elevator Dynamic Forward", elevator.sysIdDynamicElevator(Direction.kForward));
    autoChooser.addOption(
        "[SysID] Elevator Dynamic Reverse", elevator.sysIdDynamicElevator(Direction.kReverse));

    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public void configureBatteryChooser() {
    batteryChooser = new PersistentSendableChooser<>("Battery Number");

    batteryChooser.addOption("2019 #3", "Daniel");
    batteryChooser.addOption("2020 #2", "Gary");
    batteryChooser.addOption("2022 #1", "Lenny");
    batteryChooser.addOption("2024 #1", "Ian");
    batteryChooser.addOption("2024 #2", "Nancy");
    batteryChooser.addOption("2024 #3", "Perry");
    batteryChooser.addOption("2024 #4", "Quincy");
    batteryChooser.addOption("2024 #5", "Richard");
    batteryChooser.addOption("2025 #1", "Josh");

    if (batteryChooser.getSelectedName() != null && !batteryChooser.getSelectedName().equals("")) {
      LogUtil.recordMetadata("Battery Number", batteryChooser.getSelectedName());
      LogUtil.recordMetadata("Battery Nickname", batteryChooser.getSelected());
    }

    batteryChooser.onChange(
        (nickname) -> {
          LogUtil.recordMetadata("Battery Number", batteryChooser.getSelectedName());
          LogUtil.recordMetadata("Battery Nickname", nickname);
        });

    SmartDashboard.putData("Battery Chooser", batteryChooser);
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
