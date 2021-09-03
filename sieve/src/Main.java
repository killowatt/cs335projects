import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Primes up to this number (non-inclusive) are recorded
        final int count = 10000;

        // Create our array of primes and fill it with true values
        boolean primes[] = new boolean[count];
        Arrays.fill(primes, true);

        // Starting from 2 as per sieve algorithm spec
        int primeCount = 0;
        for (int i = 2; i < count; i++) {
            // Skip values that we've already set to false
            if (!primes[i])
                continue;

            // Set all multiples of the current number to false, checking array bounds
            int multiple = i;
            while (true) {
                // Next multiple of i
                multiple += i;

                // If the multiple exceeds our bounds, stop
                if (multiple >= count)
                    break;

                // All multiples are set to false, and we know we're in bounds now
                primes[multiple] = false;
            }

            // Print out this number since we know its prime, increasing the count as well
            primeCount++;
            System.out.printf("%d, ", i);
        }

        System.out.printf("\nThere are %d primes less than 10,000", primeCount);
    }
}
