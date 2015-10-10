import java.awt.*;

/**
 * Created by pthien92 on 10/10/15.
 */
public class Job {
    public enum EventType {TRUCK_IN_PIT_ARRIVE,
                        TRUCK_EX_PIT_ARRIVE,
                        CRUSHER_IS_CRUSHING,
                        }
    private double startTime;
    private double endTime;

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    private Truck truck;
    private EventType event;
    Job(double startTime_, double endTime_, EventType e_, Truck truck) {
        startTime = startTime_;
        endTime = endTime_;
        event = e_;
        this.truck = truck;
    }
    Job() {
        startTime = 0;
        endTime = 0;
        event = EventType.TRUCK_IN_PIT_ARRIVE;
        truck = new Truck();
    }
    public void setStartTime(double time) {
        startTime = time;
    }
    public void setEndTime(double time) {
        endTime = time;
    }
    public void setEvent(EventType e) {
        event = e;
    }

    public double getEndTime() {
        return endTime;
    }
    public double getStartTime() {
        return startTime;
    }
}
