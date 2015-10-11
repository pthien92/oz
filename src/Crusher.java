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

    public double getTotalTonnes() {
        return totalTonnes;
    }

    public void setTotalTonnes(double totalTonnes) {
        this.totalTonnes = totalTonnes;
    }

    private double totalTonnes;

    public ArrayList<Double> getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(ArrayList<Double> averageGrade) {
        this.averageGrade = averageGrade;
    }

    private ArrayList<Double> averageGrade;
    private ArrayList<TruckInPit> exTruckServed;
    private ArrayList<TruckExPit> inTruckServed;
    private double timeElapsed;

    public Crusher() {
//        totalTonnes = 5407;
        totalTonnes = 0;
        averageGrade = new ArrayList<Double>(9);
//        averageGrade.add(new Double(0.33));
//        averageGrade.add(new Double(0.10));
//        averageGrade.add(new Double(0.75));
//        averageGrade.add(new Double(4.86));
//        averageGrade.add(new Double(0.24));
//        averageGrade.add(new Double(0));
//        averageGrade.add(new Double(0.07));
//        averageGrade.add(new Double(43.01));
//        averageGrade.add(new Double(573.57));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        averageGrade.add(new Double(0));
        timeElapsed = 1;
    };

    // False if it violate
    public boolean hourlyCheck(TruckInPit truck) {

        // Check on grades
        ArrayList<Double> newAverageGrade = new ArrayList<Double>();
        double[] truckGrade = truck.getGrades();
        double weight = truck.getWeight();

        for(int i = 0; i < 9; ++i) {
            newAverageGrade.add(i, (averageGrade.get(i) * totalTonnes + truckGrade[i] * weight) / (totalTonnes + weight));
        }

        for(int i = 0; i < 9; ++i) {
            double grade = newAverageGrade.get(i);
            if (HourLowerLimit[i] != 1000 && grade < Target[i] * ( 1 + HourLowerLimit[i])) {
                return false;
            }

            if (HourUpperLimit[i] != 1000 && grade > Target[i] * ( 1 + HourUpperLimit[i])) {
                return false;
            }
        }

        // Checks on weight
//        double newTotalTonnes = totalTonnes;
//        double millFeedRate = newTotalTonnes / timeElapsed;
//        if (millFeedRate > 1700 || millFeedRate < 1416.67) {
//            return false;
//        }

        return true;
    };

    // False if it violate
    public boolean dailyCheck(Truck truck) {
        // Check on grades
        ArrayList<Double> newAverageGrade = new ArrayList<Double>();
        double[] truckGrade = truck.getGrades();
        double weight = truck.getWeight();

        for(int i = 0; i < 9; ++i) {
            newAverageGrade.add(i, (averageGrade.get(i) * totalTonnes + truckGrade[i] * weight) / (totalTonnes + weight));
        }

        for(int i = 0; i < 9; ++i) {
            double grade = newAverageGrade.get(i);
            if (DailyLowerLimit[i] != 1000 && grade < Target[i] + Target[i] * DailyLowerLimit[i]) {
                return false;
            }

            if (DailyUpperLimit[i] != 1000 && grade > Target[i] + Target[i] * DailyUpperLimit[i]) {
                return false;
            }
        }

//        double newTotalTonnes = totalTonnes + truck.getWeight();
//        double millFeedRate = newTotalTonnes / (timeElapsed / (24 * 60 * 60));
//        if (millFeedRate > 40800 || millFeedRate < 34000) {
//            return false;
//        }

        return true;
    };

    public void serveTruck(Truck truck) {
        double[] truckGrade = truck.getGrades();
        double weight = truck.getWeight();

        for(int i = 0; i < 9; ++i) {
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
        System.out.println("\nResult\n====================================\n");
        for(int i = 0; i < 9; ++i) {
            System.out.println((char)('A'+i) + " : " + averageGrade.get(i));
        }

        System.out.println("Total tonnes per day = " + totalTonnes / (timeElapsed / (24 * 60 * 60)));
    }
}
