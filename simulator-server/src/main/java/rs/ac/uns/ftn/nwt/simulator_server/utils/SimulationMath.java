package rs.ac.uns.ftn.nwt.simulator_server.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimulationMath {

    private static final Random random = new Random();

    public static double linearlyIncrease(double start, double end, int stepNumber, int numberOfSteps, double probabilityOfDecrease) {
        boolean decreaseTemperature = Math.random() < probabilityOfDecrease;

        double increment = (end - start) / numberOfSteps;
        if (decreaseTemperature) return start + increment * stepNumber - 2 * (increment);
        else return start + increment * stepNumber;

    }

    public static double linearlyDecrease(double start, double end, int stepNumber, int numberOfSteps, double probabilityOfIncrease) {
        boolean increaseTemperature = Math.random() < probabilityOfIncrease;

        double decrement = (start - end) / numberOfSteps;
        if(increaseTemperature) return start - decrement * stepNumber - 2*(decrement);
        else return start - decrement * stepNumber;
    }

    public static double generateRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static int generateRandomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
