/**
 * Created by pthien92 on 9/10/15.
 */
public class StockPile {
    private String stockPileName;
    private double totalTonnes = 0;
    private double[] grades;
    private int travelTime;
    private double error;
    private int time;
    private double scale;
    StockPile() {
        totalTonnes = 0;
        grades = new double[9];
        for (int i = 0; i < grades.length; i++)
            grades[i] = 0;
        travelTime = 0;
        error = 0;
        scale = 1;
        time = 0;
    }

    public String getStockPileName() {
        return stockPileName;
    }

    public void setTotalTonnes(double tonnes) {
        this.totalTonnes = tonnes;
    }
    public void setTravelTime(int time) {
        this.travelTime = time;
    }
    public void setGrades(double[] g) {
        this.grades = g.clone();
    }
    public void setStockPileName(String name) {
        this.stockPileName = name;
    }
    public double[] getGrades() {
        return grades;
    }
    public double getTotalTones() {
        return totalTonnes;
    }
    public int getTravelTime() {
        return travelTime;
    }
    public double computeError(double[] targetGrades) {
        // estimate the error of pile
        double error = 0;
        for (int i = 0; i < targetGrades.length; i++) {
            if (i != 5 ) //ignore F
             error += Math.pow(grades[i] - targetGrades[i], 2);
        }
        return scale*Math.sqrt(error);
    }
    public void setScale(double sc) {
        scale = sc;
    }

    public void serveInTruck(TruckInPit truck, int time) {
        this.time = time;
        totalTonnes += truck.getWeight();

        double[] truckGrades = truck.getGrades();
        for (int i  = 0; i < 9; ++i) {
           grades[i] = (grades[i] * totalTonnes + truck.getWeight() * truckGrades[i]) / (totalTonnes + truck.getWeight());
        }
    }

    public void serveExTruck(TruckExPit truck, int time) {
        this.time = time;
        totalTonnes -= truck.getWeight();
        double[] truckGrades = truck.getGrades();
        for (int i  = 0; i < 9; ++i) {
            grades[i] = (grades[i] * totalTonnes - truck.getWeight() * truckGrades[i]) / (totalTonnes - truck.getWeight());
        }
    }
}
