import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class JobQueue extends LinkedList<Job> {
    public JobQueue() {
        super();
    }

    public boolean add(Job job) {
        super.add(job);
        return true;
    }
    public void remove(Job job) {
        this.remove();
    }
    public void sort() {
        Collections.sort(this, new Comparator<Job>() {
            @Override
            public int compare(Job job1, Job job2) {
                return (int)(job2.getEndTime() - job1.getEndTime());
            }
        });
    }

}
