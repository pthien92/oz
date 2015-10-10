import java.util.ArrayList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class UndergroundStockPile {
    private ArrayList<StockPile> stockPiles;

    UndergroundStockPile() {
        stockPiles = new ArrayList<StockPile>(21);
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
