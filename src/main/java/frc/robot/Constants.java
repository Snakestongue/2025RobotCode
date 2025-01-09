package frc.robot;

import static edu.wpi.first.units.Units.FeetPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;

import com.pathplanner.lib.config.PIDConstants;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Time;

public class Constants {
  public static class SwerveConstants {
    public static final LinearVelocity maxTranslationalSpeed = FeetPerSecond.of(15);
    public static final AngularVelocity maxRotationalSpeed = RotationsPerSecond.of(1);

    public static final Time translationZeroToFull = Seconds.of(0.5);
    public static final Time rotationZeroToFull = Seconds.of(0.5);
  }

  public static class AutoConstants {
    public static final PIDConstants translationPID = new PIDConstants(5.0, 0.0, 0.0);
    public static final PIDConstants rotationPID = new PIDConstants(1.0, 0.0, 0.0);
  }
}
