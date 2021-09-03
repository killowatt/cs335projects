public class Main {
    // Abundant values are greater than 0
    public static int abundant(int number) {
        int sum = 0;

        // We loop up to half of number because we know that numbers that multiply by two
        // to something higher than our input cannot be a part of our properly divisible set
        for (int i = 1; i <= number / 2; i++) {
            if (number % i == 0) // Only properly divisible numbers
                sum += i;
        }
        return sum - number;
    }

    public static void main(String[] args) {
        // Numbers from 1 up to this number (non-inclusive)
        final int count = 10000;

        // The total running count of abundant numbers
        int abundantCount = 0;

        // The max abundance value and the corresponding number responsible for that value
        int maxAbundance = 0;
        int maxAbundanceIndex = 0;

        // Non-inclusive up to 10,000 ("less than 10,000")
        for (int i = 0; i < count; i++)
        {
            int abundance = abundant(i);

            // If our number is abundant, add one to count
            if (abundance > 0)
                abundantCount++;

            // If this number exceeds the current max abundance, update values accordingly
            // This preserves the lowest number that reaches a max value
            if (abundance > maxAbundance) {
                maxAbundance = abundance;
                maxAbundanceIndex = i;
            }
        }

        System.out.printf("There are %d abundant numbers less than 10,000.\n", abundantCount);
        System.out.printf("The smallest abundant number (less than 10,000) with highest abundance is %d\n",
                maxAbundanceIndex);
    }
}
