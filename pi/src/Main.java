public class Main {
    static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static void main(String[] args) {
        double result = 0.0;
        for (int i = 0 ;; i++) {
            double value = 1.0 / (i * 2 + 1);
            result += isEven(i) ? value : -value;

            double precision = Math.pow(10, 8);
            double truncated = Math.floor(precision * (result * 4.0)) / precision;

            System.out.println(truncated);

            if (truncated == 3.14159265) {
                System.out.println(i + 1 + " terms needed");
                break;
            }
        }
    }
}
