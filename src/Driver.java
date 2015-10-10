import java.io.BufferedReader;



import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by pthien92 on 10/10/15.
 */
public class Driver {
    private static ArrayList<TruckInPit> truckInPits;
    private static double[] movingAvg;
    private static int crushCount;
    private static double[] goal = new double[]{0.33,0.1,0.75,4.86,0.24,0,0.07,43.01, 573.57};
    private static double scale;

    Driver() {
        movingAvg = new double[]{0,0,0,0,0,0,0,0,0};
        crushCount = 0;
        scale = 1;

        String dataFile = "data/open_pit.csv";
        BufferedReader br = null;
        String line = "";
        String csvSplit = ",";
        truckInPits = new ArrayList<TruckInPit>();
        try {
            br = new BufferedReader(new FileReader(dataFile));
            line = br.readLine(); //dump the label
            while ((line = br.readLine()) != null) {
                String[] truckInstance = line.split(csvSplit);
                TruckInPit temp = new TruckInPit();
                temp.setLoadTime(Double.parseDouble(truckInstance[14])); //in unix times
                temp.setDumpTime(Double.parseDouble(truckInstance[15])); //in unix times
                temp.setWeight(Double.parseDouble(truckInstance[4]));
                temp.setGrades(new double[]{
                        Double.parseDouble(truckInstance[5]), //grade A
                        Double.parseDouble(truckInstance[6]), // B
                        Double.parseDouble(truckInstance[7]), // C
                        Double.parseDouble(truckInstance[8]), // D
                        Double.parseDouble(truckInstance[9]), // E
                        Double.parseDouble(truckInstance[10]), // F
                        Double.parseDouble(truckInstance[11]), // G
                        Double.parseDouble(truckInstance[12]), // H
                        Double.parseDouble(truckInstance[13])  // I
                });
                truckInPits.add(temp);
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("here");
           e.printStackTrace();
        }

    }


    public static void main(String args[]) {
        double timeElapsed = 0;
        Driver dr = new Driver();
        Crusher cr = new Crusher();

        //double ticks = truckInPits.get(0).getLoadTime();
        for (int i = 0; i < truckInPits.size(); i++)
            System.out.println(testError(truckInPits.get(i).getGrades()));
        for (int i = 0; i < movingAvg.length; i++)
            System.out.println("\n" + movingAvg[i]);


        int origin = (int)truckInPits.get(0).getLoadTime();
        int tick = origin;
        int nextTruckId = 0;
        LinkedList<TruckInPit> currentQueue = new LinkedList<TruckInPit>();

        while (tick - origin < 60 * 60 * 24 * 7) {
            if (truckInPits.get(nextTruckId).getLoadTime() == tick) {
                currentQueue.add(truckInPits.get(nextTruckId));
                nextTruckId++;
            }

            TruckInPit topTruck = currentQueue.peek();
            if (topTruck != null && tick == topTruck.getLoadTime() + 54 * 6) {
                currentQueue.poll();
                // Make decision for this truck, direct tip by default
                cr.serveTruck(topTruck);
            }

            ++tick;

            cr.setTimeElapsed(tick - origin);
            System.out.println(tick - origin);
        }

        // Report after 7 days
        cr.report();
    }

    public ArrayList<TruckInPit> getTruckExPits() {
        return truckInPits;
    }

    public static double testError(double[] grades) {
        if (crushCount == 0) {
            crushCount++;
            movingAvg = grades.clone();
            return computeError(grades);
        }
        for (int i = 0; i < grades.length; i++) {
            movingAvg[i] = movingAvg[i] + (grades[i] - movingAvg[i])/(crushCount+1);
        }
        crushCount++;
        return computeError(movingAvg);
    }

    public static double computeError(double[] new_grades) {
        double error = 0;
        double[] coefficients = calculateNormaliseCoefficients(goal);
        double[] normalisedNewGrades = normaliseGrades(new_grades, coefficients);
        double[] normalisedGoal = normaliseGrades(goal, coefficients);
        for (int i = 0; i < new_grades.length; i++) {
            if (i != 5 ) //ignore F
                error += Math.pow(normalisedNewGrades[i] - normalisedGoal[i], 2);
        }
        return scale*Math.sqrt(error);
    }
    public static double[] normaliseGrades(double[] grades, double[] coeffs) {
        double[] normGrades = new double[]{0,0,0,0,0,0,0,0,0};
        for (int i = 0; i < grades.length; i++) {
            normGrades[i] = coeffs[0]*grades[i] - coeffs[1];
        }
        return normGrades;
    }
    public static double[] calculateNormaliseCoefficients(double[] grades) {
        double max_g = findMaxGrades(grades);
        double min_g = findMinGrades(grades);
        if (max_g == min_g)
            return new double[]{0,0};
        double a = 1.0 /(max_g- min_g);
        double b = -min_g/(max_g-min_g);
        //System.out.println(a + " - " + b);
        return new double[]{a,b};
    }

    public static double findMaxGrades(double[] grades) {
        double max = grades[0];
        for (int i = 1; i< grades.length; i++) {
            if (max < grades[i])
                max = grades[i];
        }
        return max;
    }
    public static double findMinGrades(double[] grades) {
        double min = grades[0];
        for (int i = 1; i < grades.length; i++) {
            if (min > grades[i]) {
                min = grades[i];
            }
        }
        return min;
    }
}