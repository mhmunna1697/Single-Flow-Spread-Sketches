# **Implementation of Single-Flow Spread Sketches (Bitmap, Probabilistic Bitmap, and HyperLogLog)**

## **Overview**
This repository contains Java implementations of three cardinality estimation techniques widely used in data streaming and network measurement systems:

- **Bitmap**: A basic probabilistic data structure that uses a bit array to estimate distinct elements.
- **Probabilistic Bitmap**: Enhances the bitmap by introducing sampling probability `p`, reducing memory and computation.
- **HyperLogLog (HLL)**: A widely used cardinality estimator offering high accuracy with compact memory using stochastic averaging.

Each estimator was tested on flows of various spread sizes and wrote the results to output files.

---

## **How They Work**

### **1. Bitmap**
- Uses a fixed-size bit array (e.g., 10,000 bits).
- For each distinct item, a bit position is randomly set to 1.
- After processing, estimates spread using:
  
  `Estimated = -m * ln(V/m)`
  where `V` is the number of zero bits.

### **2. Probabilistic Bitmap**
- Similar to Bitmap, but only samples items with probability `p` (e.g., 0.1).
- Reduces overhead by subsampling.
- Estimation formula:

  `Estimated = -(m/p) * ln(V/m)`

### **3. HyperLogLog (HLL)**
- Uses `m` registers (e.g., 256), each 5 bits.
- Hashes elements and tracks the number of leading zeros in hash suffixes.
- Aggregates estimates across registers with a bias correction factor:

  `E = alpha_m * m^2 / sum(2^(-R[i]))`

- Applies **small range correction** when necessary.

---

## **Compilation & Execution**
```bash
javac Bitmap.java ProbabilisticBitmap.java HyperLogLog.java
```

### **Run Bitmap**
```bash
java Bitmap
```

### **Run Probabilistic Bitmap**
```bash
java ProbabilisticBitmap
```

### **Run HyperLogLog**
```bash
java HyperLogLog
```

---

## **Input Configuration**
Each implementation simulates flows with true spreads:
```
100
1,000
10,000
100,000
1,000,000
```

### HyperLogLog uses:
- `m = 256` registers

### Bitmap & Probabilistic Bitmap use:
- `m = 10,000` bits
- `p = 0.1` (for Probabilistic Bitmap)

---

## **Output Format**
Each program writes results to its own `.txt` file:
- `bitmap_output.txt`
- `probabilistic_bitmap_output.txt`
- `hyperloglog_output.txt`

Each line in the output has:
```
<true_spread> <estimated_spread>
```

### Example:
```
10000 9958.77
100000 92731.52
```

---

## **Performance Summary**
| Method               | Memory Efficiency | Accuracy (Small Spread) | Accuracy (Large Spread) | Supports Sampling |
|----------------------|-------------------|--------------------------|--------------------------|-------------------|
| Bitmap               | ✅ High           | ✅ Good                  | 🔸 Degrades              | ❌ No              |
| Probabilistic Bitmap | ✅ High           | 🔸 Approximate           | 🔸 Approximate           | ✅ Yes             |
| HyperLogLog (HLL)    | ✅ Very High      | ✅ Excellent             | ✅ Excellent             | ❌ No              |

---
