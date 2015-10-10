import com.intellij.execution.impl.statistics.TemporaryRunConfigurationTypeUsagesCollector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class Driver {
    private static ArrayList<TruckExPit> truckExPits;
    public Driver() {
        String dataFile = "data/open_pit.csv";
        BufferedReader br = null;
        String line = "";
        String csvSplit = ",";
        truckExPits = new ArrayList<TruckExPit>();
        try {
            br = new BufferedReader(new FileReader(dataFile));
            line = br.readLine(); //dump the label
            while ((line = br.readLine()) != null) {
                String[] truckInstance = line.split(csvSplit);
                TruckExPit temp = new TruckExPit();
                temp.setLoadTime(Double.parseDouble(truckInstance[14]));
                temp.setDumpTime(Double.parseDouble(truckInstance[15]));
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
                truckExPits.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static boolean validate(TruckExPit truck) {
       return false;
    }

    private static void process(ArrayList<TruckExPit> truckQueue) {
        for(int i = 0; i < truckQueue.size(); ++i) {
            if(validate(truckQueue.get(i)))) {
            }
        }
    }

    public static void main(String args[]) {
        Driver dr = new Driver();
        System.out.println(truckExPits.size());

        // Simulate
        double currentTime = 0;
        ArrayList<TruckExPit> currentQueue = new ArrayList<TruckExPit>();

        if (truckExPits.size() > 0) {
            currentTime = truckExPits.get(0).getLoadTime() + 5.4;
        };

        for (int i = 0; i < truckExPits.size(); ++i) {
            if (truckExPits.get(i).getLoadTime() + 5.4 <= currentTime + 60) {
                currentQueue.add(truckExPits.get(i));
                process(currentQueue);
            } else {
                currentTime = truckExPits.get(i).getLoadTime() + 5.4;
                currentQueue.clear();
            }
        }
    }
}