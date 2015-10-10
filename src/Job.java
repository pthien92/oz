import java.awt.*;

/**
 * Created by pthien92 on 10/10/15.
 */
public class Job {
    private enum EventType {TRUCK_IN_PIT_ARRIVE,
                        TRUCK_EX_PIT_ARRIVE,
                        CRUSHER_IS_CRUSHING,
                        }
    private double startTime;
    private double endTime;
    private EventType event;
    Job(double startTime_, double endTime_, EventType e_) {
        startTime = startTime_;
        endTime = endTime_;
        event = e_;
    }
    Job() {
        startTime = 0;
        endTime = 0;
        event = EventType.TRUCK_IN_PIT_ARRIVE;
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
}
