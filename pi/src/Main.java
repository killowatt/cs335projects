// CS335 William Yates
// Exercise 00 - "Pi"

public class Main {
    // Use modulo to determine even-ness.
    static boolean isEven(int number) {
        return number % 2 == 0;
    }

    /* This will print the approximate amount of terms needed to reach 8 digits
     * of accuracy of PI. The reason this value is approximate is that we are
     * not perfectly truncating our double values. This could be resolved with
     * something like BigDecimal or a formatting operation, but these methods are
     * slow and thus resulted in a not insignificant amount of extra computing time.  */
    public static void main(String[] args) {
        // The precision we require the sequence to reach
        final double precision = Math.pow(10, 8);

        // Current value of the sequence
        double result = 0.0;
        for (int i = 0; ; i++) {
            // Calculate the value of this term then add or subtract, alternating on i
            double value = 1.0 / (i * 2 + 1);
            result += isEven(i) ? value : -value;

            // Truncate the value, so we can compare with our desired precision value
            double truncated = Math.floor(precision * (result * 4.0)) / precision;

            // Once we reach 8 digits of accuracy, exit the loop
            if (truncated == 3.14159265) {
                System.out.printf("~%d terms needed\n", i + 1);
                break;
            }
        }
    }
}
