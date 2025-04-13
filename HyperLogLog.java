import java.io.*;
import java.util.*;

public class HyperLogLog {
    private static final int M = 256; // number of registers
    private static final int REGISTER_BITS = 5; // each register is 5 bits
    private int[] registers;
    private Random random;
    private static final double ALPHA_M = 0.7213 / (1 + 1.079 / M); // bias correction factor

    public HyperLogLog() {
        registers = new int[M];
        random = new Random();
    }

    public void reset() {
        Arrays.fill(registers, 0);
    }

    private int hash(int x) {
        int hash = Integer.hashCode(x ^ random.nextInt());
        hash ^= (hash >>> 16);
        return hash;
    }

    public void record(int element) {
        int hashValue = hash(element);
        int index = hashValue >>> (Integer.SIZE - 8); // first log2(M)=8 bits for index (M=256)
        int remaining = (hashValue << 8) | (1 << 7); // shift out index bits and ensure at least one 1 bit
        int leadingZeros = Integer.numberOfLeadingZeros(remaining) + 1; // rank estimation
        registers[index] = Math.max(registers[index], leadingZeros);
    }

    public double estimate() {
        double sum = 0.0;
        for (int reg : registers) {
            sum += 1.0 / (1 << reg);
        }
        double rawEstimate = ALPHA_M * M * M / sum;

        // Small range correction
        if (rawEstimate <= (REGISTER_BITS / 2.0) * M) {
            int zeros = 0;
            for (int reg : registers) {
                if (reg == 0) zeros++;
            }
            if (zeros != 0) {
                rawEstimate = M * Math.log((double) M / zeros);
            }
        }

        return rawEstimate;
    }

    
    public static void main(String[] args) {
        HyperLogLog hll = new HyperLogLog();
        int[] spreads = {1000, 10000, 100000, 1000000};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("hyperloglog_output.txt"))) {
            for (int spread : spreads) {
                hll.reset();
                for (int i = 0; i < spread; i++) {
                    hll.record(i);
                }
                double estimate = hll.estimate();
                writer.write(spread + " " + String.format("%.2f", estimate) + "\n");
            }
            System.out.println("Output written to hyperloglog_output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
