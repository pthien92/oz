/**
 * Created by pthien92 on 9/10/15.
 */
public class StockPile {
    private String stockPileName;
    private double totalTonnes = 0;
    private double[] grades;
    private int travelTime;
    private double error;

    StockPile() {
        totalTonnes = 0;
        grades = new double[9];
        for (int i = 0; i < grades.length; i++)
            grades[i] = 0;
        travelTime = 0;
        error = 0;
    }


    public void setTotalTonnes(int tonnes) {
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
        return error;
    }
}
