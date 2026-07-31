public class JavaOriginal1 {
    public int sumArray(int[] numbers) {
        // Calculate the sum of elements
        int total = 0;
        for (int i = 0; i < numbers.length; i++) {
            total = total + numbers[i];
        }
        return total;
    }

    public double calculateMean(int[] numbers) {
        if (numbers.length == 0) {
            return 0.0;
        }
        int sum = sumArray(numbers);
        return (double) sum / numbers.length;
    }
}
