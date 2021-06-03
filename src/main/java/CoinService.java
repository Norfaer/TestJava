import java.util.Arrays;
import java.util.HashMap;

public class CoinService {
    private int[] availableCoins;
    private HashMap<Integer,Integer> cacheMin = new HashMap<Integer, Integer>();

    public CoinService(int[] availableCoins) {
        this.availableCoins = availableCoins;
    }

    private int indexAvailableOf(int coin) {
        for(int i=0;i<this.availableCoins.length;i++) {
            if(this.availableCoins[i] == coin) {
                return i;
            }
        }
        return -1;
    }

    private int countMinExchange(int howMuch) {
        int minCoins = howMuch;
        int idx = this.indexAvailableOf(howMuch);
        if (idx != -1) {
            this.cacheMin.put(howMuch, 1);
            return 1;
        }
        if (this.cacheMin.containsKey(howMuch)) {
            return this.cacheMin.get(howMuch);
        }
        for (int coin : Arrays.stream(availableCoins).filter(x -> x <= howMuch).toArray()) {
            int numCoins = 1 + countMinExchange(howMuch - coin);
            if (numCoins < minCoins) {
                minCoins = numCoins;
                this.cacheMin.put(howMuch,minCoins);
            }
        }
        return minCoins;
    }

    public int[] getExchange(int howMuch) {
        int[] resultSet = new int[this.countMinExchange(howMuch)];

        int rest = howMuch;
        int resultIndex = 0;
        while (rest != 0) {
            int minCoins = rest;
            int nextCoin = 0;
            for (int coin : Arrays.stream(availableCoins).filter(x -> x <= howMuch).toArray()) {
                if (coin == rest) {
                    nextCoin = coin;
                    break;
                } else {
                    int minCoinsNext = countMinExchange(rest - coin);
                    if (minCoins > minCoinsNext) {
                        minCoins = minCoinsNext;
                        nextCoin = coin;
                    }
                }
            }
            resultSet[resultIndex] = nextCoin;
            rest -= nextCoin;
            resultIndex++;
        }
        return resultSet;
    }

    public static void main(String[] args) {
        int[] testArray = {1,3,4,5};
        CoinService cs = new CoinService(testArray);

        System.out.println( Arrays.toString(cs.getExchange(7)));
    }
}
