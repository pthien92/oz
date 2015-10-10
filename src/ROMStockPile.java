import java.util.ArrayList;

/**
 * Created by pthien92 on 9/10/15.
 */
public class ROMStockPile {
    private ArrayList<StockPile> stockPiles;

    ROMStockPile() {
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
