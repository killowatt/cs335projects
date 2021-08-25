import java.util.Random;

public class Main {
    static Random random = new Random();

    static int rollDiceMax(int amount) {
        int maxedCount = 0;
        for (int i = 0; i < amount; i++) {
            int twelveSidedDie = random.nextInt(12) + 1;
            int twentySidedDie = random.nextInt(20) + 1;

            if ((twelveSidedDie + twentySidedDie) >= 32)
                maxedCount++;
        }

        return maxedCount;
    }

    public static void main(String[] args) {
        System.out.println("Dice");

        System.out.println("100: " + rollDiceMax(100) + " max out");
    }
}
