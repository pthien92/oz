import java.util.ArrayList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class Crusher {
    private static final double[] HourUpperLimit = {0.2, 0.5, 1000, 1000, 1000, 1000, 1000, 0.2, 0.5};
    private static final double[] HourLowerLimit = {-0.2, -0.5, 1000, 1000, 1000, 1000, 1000, -0.2, -0.5};
    private static final double[] DailyUpperLimit = {.05, .1, .2, .2, .2, 1000, .2, .05, .1};
    private static final double[] DailyLowerLimit = {-.05, -.1, -.2, -.2, -.2, -1000, -.2, -.05, -.1};

    private static final double[] Target = {.33, .1, .75, 4.86, 0.24, 1000, 0.07, 43.01, 573.57};

    private double totalTonnes;
    private ArrayList<Double> averageGrade;
    private ArrayList<TruckInPit> exTruckServed;
    private ArrayList<TruckExPit> inTruckServed;
    private double timeElapsed;

    public Crusher() {
        totalTonnes = 0;
        averageGrade = new ArrayList<Double>(8);
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        timeElapsed = 0;
    };

    // False if it violate
    public boolean hourlyCheck(TruckInPit truck) {

        // Check on grades
        ArrayList<Double> newAverageGrade = new ArrayList<Double>();
        double[] truckGrade = truck.getGrades();
        double weight = truck.getWeight();

        for(int i = 0; i < 8; ++i) {
            newAverageGrade.set(i, (averageGrade.get(i) * totalTonnes + truckGrade[i] * weight) / (totalTonnes + weight));
        }

        for(int i = 0; i < 8; ++i) {
            double grade = newAverageGrade.get(i);
            if (HourLowerLimit[i] != 1000 && grade < Target[i] + HourLowerLimit[i]) {
                return false;
            }

            if (HourUpperLimit[i] != 1000 && grade > Target[i] + HourUpperLimit[i]) {
                return false;
            }
        }

        // Checks on weight
        double newTotalTonnes = totalTonnes;
        double millFeedRate = newTotalTonnes / timeElapsed;
        if (millFeedRate > 1700 || millFeedRate < 1416.67) {
            return false;
        }

        return true;
    };

    // False if it violate
    public boolean dailyCheck(TruckInPit truck) {
        // Check on grades
        ArrayList<Double> newAverageGrade = new ArrayList<Double>();
        double[] truckGrade = truck.getGrades();
        double weight = truck.getWeight();

        for(int i = 0; i < 8; ++i) {
            newAverageGrade.set(i, (averageGrade.get(i) * totalTonnes + truckGrade[i] * weight) / (totalTonnes + weight));
        }

        for(int i = 0; i < 8; ++i) {
            double grade = newAverageGrade.get(i);
            if (DailyLowerLimit[i] != 1000 && grade < Target[i] + DailyLowerLimit[i]) {
                return false;
            }

            if (DailyUpperLimit[i] != 1000 && grade > Target[i] + DailyUpperLimit[i]) {
                return false;
            }
        }

        double newTotalTonnes = totalTonnes + truck.getWeight();
        double millFeedRate = newTotalTonnes / timeElapsed;
        if (millFeedRate > 40800 || millFeedRate < 34000) {
            return false;
        }

        return true;
    };

    public void serveTruck(TruckInPit truck) {
        double[] truckGrade = truck.getGrades();
        double weight = truck.getWeight();

        for(int i = 0; i < 8; ++i) {
            averageGrade.set(i, (averageGrade.get(i) * totalTonnes + truckGrade[i] * weight) / (totalTonnes + weight));
        }

        totalTonnes += weight;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void report() {
        for(int i = 0; i < 8; ++i) {
            System.out.println(i + ":" + averageGrade.get(i));
        }

        System.out.println(totalTonnes);
    }
};
