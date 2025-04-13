import java.io.*;
import java.util.*;

public class ProbabilisticBitmap {
    private static final int M = 10000; // number of bits in bitmap
    private static final double P = 0.1; // sampling probability
    private BitSet bitmap;
    private Random random;

    public ProbabilisticBitmap() {
        bitmap = new BitSet(M);
        random = new Random();
    }

    public void reset() {
        bitmap.clear();
    }

    public void recordFlow(int spread) {
        for (int i = 0; i < spread; i++) {
            if (random.nextDouble() < P) { // sample with probability p
                int hash = Math.abs(Integer.hashCode(i ^ random.nextInt()) % M);
                bitmap.set(hash);
            }
        }
    }

    public double estimateSpread() {
        int zeroBits = M - bitmap.cardinality();
        if (zeroBits == 0) {
            return -(M / P) * Math.log(1.0 / M);
        }
        double V = (double) zeroBits / M;
        return -(M / P) * Math.log(V);
    }

    
    public static void main(String[] args) {
        ProbabilisticBitmap pbm = new ProbabilisticBitmap();
        int[] spreads = {100, 1000, 10000, 100000, 1000000};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("probabilistic_bitmap_output.txt"))) {
            for (int spread : spreads) {
                pbm.reset();
                pbm.recordFlow(spread);
                double estimated = pbm.estimateSpread();
                writer.write(spread + " " + String.format("%.2f", estimated) + "\n");
            }
            System.out.println("Output written to probabilistic_bitmap_output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
