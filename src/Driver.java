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
        Driver dr = new Driver();
        AllStockPiles stockPiles = new AllStockPiles("data/underground_stockpile_1May.csv");
        ArrayList<StockPile> piles = stockPiles.getStockPiles();

        jobQueue.sort(); //sort in ascending order of end time
        double timeElapsed = 0;
        Crusher cr = new Crusher();

        int origin = (int)jobQueue.get(0).getEndTime();
        int tick = origin;

        while (jobQueue.size() > 4) {
            Job crrJob = jobQueue.get(0);

            if ((int) (crrJob.getEndTime()) == tick) {
                double crushErr = testError(cr, crrJob.getTruck());
                ArrayList<Double> stockpileErrs = new ArrayList<Double>();
                for (int i = 0; i < piles.size(); ++i) {
                    stockpileErrs.add(stockpilesError(piles.get(i), crrJob.getTruck()));
                }

                double minErr = crushErr;
                int minPile = -1;
                for (int i = 0; i < stockpileErrs.size(); ++i) {
                    if (minErr > stockpileErrs.get(i) && piles.get(i).getTotalTones() >= 138) {
                        minErr = stockpileErrs.get(i);
                        minPile = i;
                    }
                }

                if (minPile != -1) {
                    if (crrJob.getTruck() instanceof TruckInPit) {
                        piles.get(minPile).serveInTruck((TruckInPit) crrJob.getTruck(), tick - origin);
                        jobQueue.remove(crrJob);
                    } else {
                        piles.get(minPile).serveExTruck((TruckExPit) crrJob.getTruck(), tick - origin);
                        jobQueue.remove(crrJob);
                    }
                } else {
                    cr.serveTruck((TruckInPit)crrJob.getTruck());
                    jobQueue.remove(crrJob);
                    if (crrJob.getTruck() instanceof TruckExPit) {
                        jobQueue.setFleetCount(jobQueue.getFleetCount() - 1);
                    }
                }
            } else if (jobQueue.getFleetCount() < 3) {
                TruckExPit fleet = new TruckExPit();
                fleet.setStartTime(tick);

                // Select pile here
                int pileId = 0;
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

                piles.get(pileId).serveExTruck(fleet, tick);
                fleet.setEndTime(tick + 2 * piles.get(pileId).getTravelTime());

                Job job = new Job(tick, fleet.getEndTime(), null, fleet);

                jobQueue.add(job);
                jobQueue.setFleetCount(jobQueue.getFleetCount() + 1);
            }

            ++tick;

            System.out.println(jobQueue.size());
        }


        // Report after 7 days
        cr.report();
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
