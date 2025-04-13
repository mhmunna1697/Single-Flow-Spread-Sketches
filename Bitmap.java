import java.io.*;
import java.util.*;

public class Bitmap {
    private static final int M = 10000; // number of bits in bitmap
    private BitSet bitmap;
    private Random random;

    public Bitmap() {
        bitmap = new BitSet(M);
        random = new Random();
    }

    public void reset() {
        bitmap.clear();
    }

    public void recordFlow(int spread) {
        for (int i = 0; i < spread; i++) {
            int hash = Math.abs(Integer.hashCode(i ^ random.nextInt()) % M);
            bitmap.set(hash);
        }
    }

    public double estimateSpread() {
        int zeroBits = M - bitmap.cardinality();
        if (zeroBits == 0) {
            return -M * Math.log(1.0 / M);
        }
        double V = (double) zeroBits / M;
        return -M * Math.log(V);
    }

    
    public static void main(String[] args) {
        Bitmap bm = new Bitmap();
        int[] spreads = {100, 1000, 10000, 100000, 1000000};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bitmap_output.txt"))) {
            for (int spread : spreads) {
                bm.reset();
                bm.recordFlow(spread);
                double estimated = bm.estimateSpread();
                writer.write(spread + " " + String.format("%.2f", estimated) + "\n");
            }
            System.out.println("Output written to bitmap_output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
