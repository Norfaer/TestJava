import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CoinServiceTest {
    private CoinService service;

    @Provide
    Arbitrary<Integer> howMuchGen() {
        return Arbitraries.integers().between(50, 300);
    }

    @Provide
    Arbitrary<Integer> coinGen() { return Arbitraries.integers().between(2, 100); }

    @Provide
    Arbitrary<int[]> coinsArray() {
        return coinGen().array(int[].class).ofMinSize(5).ofMaxSize(9).uniqueElements().map(anArray -> {
            Arrays.sort(anArray);
            anArray[0] = 1; // Make sure we have 1 in the begining of array
            return anArray;
        });
    }

    @Property
    @Report(Reporting.GENERATED)
    void testGenerativeChange(@ForAll("howMuchGen") int howMuch, @ForAll("coinsArray") int[] coinsArray) throws CoinServiceException {
        this.service = new CoinService(coinsArray);
        Assertions.assertEquals(Arrays.stream(this.service.getExchange(howMuch)).sum(), howMuch);
    }

    @Test
    void testExceptionNegativeCoin() {
        int[] invalidInput = { 1, 2, -3, 4 };
        Throwable exception = assertThrows(CoinServiceException.class, ()-> this.service = new CoinService(invalidInput));
        assertEquals("Only positive non-zero values allowed.", exception.getMessage());
    }

    @Test
    void testExceptionWithoutOneCoin() {
        int[] invalidInput = { 2, 3, 5, 8 };
        Throwable exception = assertThrows(CoinServiceException.class, ()-> this.service = new CoinService(invalidInput));
        assertEquals("Coin with denomination 1 is needed.", exception.getMessage());
    }

    @Test
    void testExceptionNegativeExchange() throws CoinServiceException {
        int[] validInput = { 1, 2, 3, 5, 8 };
        int invalidHowMuch = -90;
        this.service = new CoinService(validInput);
        Throwable exception = assertThrows(CoinServiceException.class, ()-> this.service.getExchange(invalidHowMuch));
        assertEquals("Only positive amount is allowed.", exception.getMessage());
    }

}
