import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class Driver {
    private static ArrayList<TruckInPit> truckInPits;
    private static double[] movingAvg;
    private static int crushCount;
    private static double[] goal = new double[]{0.33,0.1,0.75,4.86,0.24,0,0.07,43.01, 573.57};
    private static double scale;
    private static JobQueue jobQueue;

    Driver() {
        movingAvg = new double[]{0,0,0,0,0,0,0,0,0};
        crushCount = 0;
        scale = 1;
        jobQueue = new JobQueue();
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
                temp.setTruckName(truckInstance[0]);
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

                Job truckInPitsJob = new Job(temp.getLoadTime() + 324 /*In-pit truck arrived at depot B*/
                                            , temp.getLoadTime() + 324, Job.EventType.TRUCK_IN_PIT_ARRIVE, temp);
                jobQueue.add(truckInPitsJob);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        int directTip = 0;

        Driver dr = new Driver();
        AllStockPiles stockPiles = new AllStockPiles("data/underground_stockpile_1May.csv");
        ArrayList<StockPile> piles = stockPiles.getStockPiles();

        jobQueue.sort(); //sort in ascending order of end time
        jobQueue.setFleetCount(0);
        double timeElapsed = 0;
        Crusher cr = new Crusher();

        int origin = (int)jobQueue.get(jobQueue.size() - 1).getEndTime();
        int tick = origin;

        int freeTimeCount = 0;
        int myMin = tick;

        while (!jobQueue.isEmpty()) {
            boolean newFleetAdded = false;

            Job crrJob = jobQueue.get(jobQueue.size() - 1);

            myMin = (int)crrJob.getEndTime();

            if (((int) crrJob.getEndTime()) == tick) {

                if (crrJob.getTruck() instanceof  TruckInPit) {
                    double crushErr = testError(cr, crrJob.getTruck());

                    ArrayList<Double> stockpileErrs = new ArrayList<Double>();
                    for (int i = 0; i < piles.size(); ++i) {
                        stockpileErrs.add(stockpilesError(piles.get(i), crrJob.getTruck()));
                    }
                    double minErr = crushErr;

                    int minPile = -1;
                    for (int i = 0; i < stockpileErrs.size(); ++i) {
                        if (minErr > stockpileErrs.get(i)) {
                            minErr = stockpileErrs.get(i);
                            minPile = i;
                        }
                    }

                    if (minPile != -1) {
//                        if (crrJob.getTruck() instanceof TruckInPit) {
                        piles.get(minPile).serveInTruck((TruckInPit) crrJob.getTruck(), crrJob.getTruck().getEndTime() - origin);
//                        } else {
//                            piles.get(minPile).serveExTruck((TruckExPit) crrJob.getTruck(), crrJob.getTruck().getEndTime() - origin);
//                        }
                    } else {
                        cr.serveTruck((TruckInPit) crrJob.getTruck());
                        cr.setTimeElapsed(tick - origin);
                        directTip++;

//                        if (crrJob.getTruck() instanceof TruckInPit) {
//                            cr.serveTruck((TruckInPit) crrJob.getTruck());
//                            cr.setTimeElapsed(tick - origin);
//                        } else {
//                            directTip++;
//                            jobQueue.setFleetCount(jobQueue.getFleetCount() - 1);
//                        }
                    }
                    jobQueue.remove(jobQueue.size() - 1);
                } else {
                    cr.serveTruck((TruckExPit) crrJob.getTruck());
                    cr.setTimeElapsed(tick - origin);
                    jobQueue.setFleetCount(jobQueue.getFleetCount() - 1);
                    jobQueue.remove(jobQueue.size() - 1);
                }
            } else if (jobQueue.getFleetCount() < 3) {

                TruckExPit fleet = new TruckExPit();
                fleet.setStartTime(tick);

                // Select pile here
                int pileId = -1;
                double minError = 100000;
                ArrayList<Double> errors = new ArrayList<Double>();
                for (int i = 0; i < piles.size(); ++i) {
                    errors.add(pileToCrusherError(cr, piles.get(i).getGrades()));
                }

                for (int i = 0; i < piles.size(); ++i) {
                    if (minError > errors.get(i) && piles.get(i).getTotalTones() >= 138) {
                       minError = errors.get(i);
                        pileId = i;
                    }
                }
                if (pileId != -1) {
                    fleet.setEndTime(tick + piles.get(pileId).getTravelTime());
                    fleet.setWeight(138);
                    fleet.setGrades(piles.get(pileId).getGrades());

                    piles.get(pileId).serveExTruck(fleet, tick);
                    Job job = new Job(tick, fleet.getEndTime(), null, fleet);

                    jobQueue.add(job);
                    jobQueue.sort();

                    jobQueue.setFleetCount(jobQueue.getFleetCount() + 1);
                }
            }

            if (!jobQueue.isEmpty() && jobQueue.get(jobQueue.size() - 1).getEndTime() != tick) {
                ++tick;
            }

            if ((tick - origin) % (60*60*24) == 0) {
                int day = ((tick - origin) / (60 * 60 * 24) % 7 + 1);
                stockPiles.resetUndergroundPiles(day);
            }
        }

        // Report after 7 days
        cr.report();
        System.out.println(directTip);
    }

    public ArrayList<TruckInPit> getTruckExPits() {
        return truckInPits;
    }

    public static double testError(Crusher crusher, Truck truck) {
        double[] movingAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] newGrades = truck.getGrades();
        ArrayList<Double> crusherGrades = crusher.getAverageGrade();
        for (int i = 0; i < 9; ++i) {
            movingAvg[i] = (crusherGrades.get(i) * crusher.getTotalTonnes() + newGrades[i] * truck.getWeight()) / (crusher.getTotalTonnes() + truck.getWeight());
        }

        return computeError(movingAvg);
    }

    public static double pileToCrusherError(Crusher crusher, double newGrades[]) {
        double[] movingAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        ArrayList<Double> crusherGrades = crusher.getAverageGrade();
        for (int i = 0; i < 9; ++i) {
            movingAvg[i] = (crusherGrades.get(i) * crusher.getTotalTonnes() + newGrades[i] * 138) / (crusher.getTotalTonnes() + 138);
        }

        return computeError(movingAvg);
    }

    public static double stockpilesError(StockPile stockpile, Truck truck) {
        double[] movingAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] newGrades = truck.getGrades();
        double[] stockpileGrades = stockpile.getGrades();
        for (int i = 0; i < 9; ++i) {
            movingAvg[i] = (stockpileGrades[i] * stockpile.getTotalTones() + newGrades[i] * truck.getWeight()) / (stockpile.getTotalTones() + truck.getWeight());
        }

        return computeError(movingAvg);
    }

    public static double computeError(double[] new_grades) {
        double error = 0;
//        double[] coefficients = calculateNormaliseCoefficients(goal);
//        double[] normalisedNewGrades = normaliseGrades(new_grades, coefficients);
//        double[] normalisedGoal = normaliseGrades(goal, coefficients);
        for (int i = 0; i < new_grades.length; i++) {
            if (i != 5 ) //ignore F
//                error += Math.pow(normalisedNewGrades[i] - normalisedGoal[i], 2);
            error += Math.pow(goal[i] - new_grades[i], 2);
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
