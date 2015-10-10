/**
 * Created by pthien92 on 10/10/15.
 */
public class TruckExPit {
    private double loadTime;
    private double dumpTime;
    private double weight;
    private double nextCommingAt; //approximately
    private double[] grades;
    private int assignedDump;

    TruckExPit() {
        loadTime = 0;
        dumpTime = 0;
        weight = 0;
        nextCommingAt = 0;
        grades = new double[] {0,0,0,0,0,0,0,0,0};
        assignedDump = 0;
    }

    public void setLoadTime(double time) {
        loadTime = time;
    }
    public void setDumpTime(double time) {
        dumpTime = time;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setNextCommingAt(double time_interval) {
        dumpTime += time_interval;
    }
    public void setGrades(double[] new_grades) {
        this.grades = new_grades.clone();
    }
    public void setAssignedDump(int dumpLocation) {
        assignedDump = dumpLocation;
    }

    public double getWeight() {
        return weight;
    }

    public int getAssignedDump() {
        return assignedDump;
    }

    public double getNextCommingAt() {
        return nextCommingAt;
    }

    public double[] getGrades() {
        return grades;
    }

    public double getDumpTime() {
        return dumpTime;
    }

    public double getLoadTime() {
        return loadTime;
    }

}
