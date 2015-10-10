import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class UndergroundStockPile {
    private ArrayList<StockPile> stockPiles;

    UndergroundStockPile(String path) {
        stockPiles = new ArrayList<StockPile>(21);
        String dataFile = path;
        BufferedReader br = null;
        String line = "";
        String csvSplit = ",";
        try {
            while ((line = br.readLine()) != null) {
                String[] stockPileInstance = line.split(csvSplit);
                int pileNum = Integer.parseInt(stockPileInstance[0]);
                stockPiles.get(pileNum).setStockPileName(stockPileInstance[0]);
                stockPiles.get(pileNum).setTotalTonnes(Double.parseDouble(stockPileInstance[1]));
                stockPiles.get(pileNum).setGrades(new double[]{
                        Double.parseDouble(stockPileInstance[2]), //A
                        Double.parseDouble(stockPileInstance[3]), //B
                        Double.parseDouble(stockPileInstance[4]), //C
                        Double.parseDouble(stockPileInstance[5]), //D
                        Double.parseDouble(stockPileInstance[6]), //E
                        Double.parseDouble(stockPileInstance[7]), //F
                        Double.parseDouble(stockPileInstance[8]), //G
                        Double.parseDouble(stockPileInstance[9]), //H
                        Double.parseDouble(stockPileInstance[10]) //I
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStockPileAt(StockPile stPile, int index) {
        stockPiles.add(index, stPile);
    }

    public StockPile getStockPile(int stockPileNumber) {
        return stockPiles.get(stockPileNumber - 1);
    }
    public StockPile getMinimumErrorStockPile() {
        //return the minimum error stockpile
        return new StockPile();
    }
}
