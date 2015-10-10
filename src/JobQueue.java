import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class JobQueue extends ArrayList<Job> {
    private boolean crushing;
    public JobQueue() {
        super();
    }

    public boolean add(Job job) {
        super.add(job);
        return true;
    }

    public void sort() {
        Collections.sort(this, new Comparator<Job>() {
            @Override
            public int compare(Job job1, Job job2) {
                return (int)(job2.getStartTime()- job1.getStartTime());
            }
        });
    }

    public boolean isCrusherRunning() {
        return crushing;
    }
    public void setCrusherRunningFlag(boolean flag) {
        crushing = flag;
    }


}
