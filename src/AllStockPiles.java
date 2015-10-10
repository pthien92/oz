import java.util.ArrayList;

/**
 * Created by pthien92 on 10/10/15.
 */
public class AllStockPiles {
    private ArrayList<StockPile> stockPiles;
    public AllStockPiles(String path_to_under_ground) {
        //copy stockpiles from ROM stockPile
        //copy stockpiles from UndergroundStockPile
        //Overwrite previous stockpiles with any of UndergroundStockPile
        // 21 stockpiles in total
        // Underground: 7 stockpiles (1,13,14,15,16,17,18)
        // ROM stockpiles : 14 (2,3,4,5,6,7,8,9,10,11,12,19,20,21)
        int[] underGroundIndex = new int[]{1,13,14,15,16,17,18};
        int[] romIndex = new int[]{2,3,4,5,6,7,8,9,10,11,12,19,20,21};
        stockPiles = new ArrayList<StockPile>(21);
        ROMStockPile romStockPile = new ROMStockPile();
        stockPiles = romStockPile.getAllStockPiles();
        UndergroundStockPile undergroundStockPile = new UndergroundStockPile(path_to_under_ground);
        for (int i = 0; i < underGroundIndex.length; i++) {
            stockPiles.remove(underGroundIndex[i]-1);
            stockPiles.add(underGroundIndex[i]-1, undergroundStockPile.getStockPile(i));
        }
    }
}
