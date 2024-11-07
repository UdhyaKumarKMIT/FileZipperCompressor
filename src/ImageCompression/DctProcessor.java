package ImageCompression;
public class DctProcessor {
    private final int blockSize;

    public DctProcessor(int blockSize) {
        this.blockSize = blockSize;
    }
    public double[][] dct(double[][] block) {
        double[][] result = new double[blockSize][blockSize];

        for (int u = 0; u < blockSize; u++) {
            for (int v = 0; v < blockSize; v++) {
                double sum = 0;
                for (int x = 0; x < blockSize; x++) {
                    for (int y = 0; y < blockSize; y++) {
                        sum += block[x][y] * Math.cos((2 * x + 1) * u * Math.PI / (2 * blockSize)) *
                                Math.cos((2 * y + 1) * v * Math.PI / (2 * blockSize));
                    }
                }

                // Apply normalization factors
                double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                result[u][v] = 0.25 * cu * cv * sum;
            }
        }

        return result;
    }

    // Apply inverse DCT (IDCT)
    public double[][] idct(double[][] block) {
        double[][] result = new double[blockSize][blockSize];

        for (int x = 0; x < blockSize; x++) {
            for (int y = 0; y < blockSize; y++) {
                double sum = 0;
                for (int u = 0; u < blockSize; u++) {
                    for (int v = 0; v < blockSize; v++) {
                        double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                        double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                        sum += cu * cv * block[u][v] *
                                Math.cos((2 * x + 1) * u * Math.PI / (2 * blockSize)) *
                                Math.cos((2 * y + 1) * v * Math.PI / (2 * blockSize));
                    }
                }
                result[x][y] = 0.25 * sum;
            }
        }

        return result;
    }

    // Quantize DCT coefficients based on quality factor
    public void quantize(double[][] block, float quality) {
        int[][] quantizationTable = createQuantizationTable();

        for (int u = 0; u < blockSize; u++) {
            for (int v = 0; v < blockSize; v++) {
                block[u][v] = Math.round(block[u][v] / (quantizationTable[u][v] * (quality / 2.0f)));

            }
        }
    }

    // Create quantization table (standard JPEG-like table)
    private int[][] createQuantizationTable() {
        int[][] quantTable = new int[blockSize][blockSize];

        int[] defaultQuantization = {
                16, 11, 10, 16, 24, 40, 51, 61,
                12, 12, 14, 19, 26, 58, 60, 55,
                14, 13, 16, 24, 40, 57, 69, 56,
                14, 17, 22, 29, 51, 87, 80, 62,
                18, 22, 37, 56, 68, 109, 103, 77,
                24, 35, 55, 64, 81, 104, 113, 92,
                49, 64, 78, 87, 103, 121, 120, 101,
                72, 92, 95, 98, 112, 100, 103, 99
        };

        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                quantTable[i][j] = defaultQuantization[i * blockSize + j];
            }
        }

        return quantTable;
    }
}
