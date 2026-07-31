public class JavaPlagiarized1 {
    // This function adds up all elements in an integer array
    public int computeSum(int[] values) {
        int result = 0;
        for (int idx = 0; idx < values.length; idx++) {
            result = result + values[idx];
        }
        return result;
    }

    /* Calculates average value of array elements */
    public double findAverage(int[] arr) {
        if (arr.length == 0) {
            return 0.0;
        }
        int runningTotal = computeSum(arr);
        double avg = (double) runningTotal / arr.length;
        return avg;
    }
}
