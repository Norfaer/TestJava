import java.util.Arrays;
import java.util.HashMap;

public class CoinService {
    private int[] availableCoins;
    private HashMap<Integer,Integer> cacheMin = new HashMap<Integer, Integer>();

    /**
     * Create coin service instance
     * @param availableCoins - Array of available coins for exchange
     * @throws CoinServiceException
     */
    public CoinService(int[] availableCoins) throws CoinServiceException {
        if(Arrays.stream(availableCoins).anyMatch(x -> x <= 0)) {
            throw new CoinServiceException("Only positive non-zero values allowed.");
        }
        if (!Arrays.stream(availableCoins).anyMatch(x -> x == 1)) {
            throw new CoinServiceException("Coin with denomination 1 is needed.");
        }
        this.availableCoins = availableCoins;
    }

    /**
     * Get index of the element by value
     * @param coin
     * @return int Result index or -1 if element was not found
     */
    private int indexAvailableOf(int coin) {
        for(int i=0;i<this.availableCoins.length;i++) {
            if(this.availableCoins[i] == coin) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Count minimum coins for exchange of howMuch
     * @param howMuch The amount to exchange
     * @return int
     */
    public int countMinExchange(int howMuch) {
        int minCoins = howMuch;
        int idx = this.indexAvailableOf(howMuch);
        if (idx != -1) {
            this.cacheMin.put(howMuch, 1);
            return 1;
        }
        if (this.cacheMin.containsKey(howMuch)) {
            return this.cacheMin.get(howMuch);
        }
        for (int coin : Arrays.stream(this.availableCoins).filter( x -> x <= howMuch).toArray()) {
            int numCoins = 1 + this.countMinExchange(howMuch - (int)coin);
            if (numCoins < minCoins) {
                minCoins = numCoins;
                this.cacheMin.put(howMuch,minCoins);
            }
        }
        return minCoins;
    }

    /**
     * Get array of coins needed to exchange howMuch
     * @param howMuch The amount to exchange
     * @return int[]
     * @throws CoinServiceException
     */
    public int[] getExchange(int howMuch)  throws CoinServiceException {
        if (howMuch < 1) {
            throw new CoinServiceException("Only positive amount is allowed.");
        }
        int[] resultSet = new int[this.countMinExchange(howMuch)];

        int rest = howMuch;
        int resultIndex = 0;
        while (rest != 0) {
            int minCoins = rest;
            int nextCoin = 0;
            for (int coin : availableCoins) {
                    if (coin == rest) {
                        nextCoin = coin;
                        break;
                    } else if(coin < rest) {
                        int minCoinsNext = this.countMinExchange(rest - coin);
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

}
