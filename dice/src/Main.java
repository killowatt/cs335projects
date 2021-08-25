// CS335 William Yates
// Exercise 00 - "Dice"

import java.util.Random;

public class Main {
    // Use the same random instance throughout the program
    static Random random = new Random();

    // Rolls a 12 and 20 sided die 'amount' times.
    // Records and then prints how many times the two dice reached a combined total of 32.
    static void rollDice(int amount) {
        int maxedCount = 0;
        for (int i = 0; i < amount; i++) {
            int twelveSidedDie = random.nextInt(12) + 1;
            int twentySidedDie = random.nextInt(20) + 1;

            if ((twelveSidedDie + twentySidedDie) >= 32)
                maxedCount++;
        }

        System.out.printf("%d: %d max out\n", amount, maxedCount);
    }

    public static void main() {
        rollDice(100);
        rollDice(1000);
        rollDice(10000);
        rollDice(100000);
    }
}
