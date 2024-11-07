import java.awt.image.BufferedImage;

public class DCTProcessor {
    private static final int BLOCK_SIZE = 8; // 8x8 block size

    public double[][] dct2d(double[][] block) {
        int N = BLOCK_SIZE;
        double[][] dct = new double[N][N];

        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0;
                for (int x = 0; x < N; x++) {
                    for (int y = 0; y < N; y++) {
                        sum += block[x][y] * Math.cos(((2 * x + 1) * u * Math.PI) / (2 * N)) * Math.cos(((2 * y + 1) * v * Math.PI) / (2 * N));
                    }
                }
                double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                dct[u][v] = 0.25 * cu * cv * sum;
            }
        }
        return dct;
    }

    public double[][] idct2d(double[][] block) {
        int N = BLOCK_SIZE;
        double[][] idct = new double[N][N];

        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                double sum = 0;
                for (int u = 0; u < N; u++) {
                    for (int v = 0; v < N; v++) {
                        double cu = (u == 0) ? 1 / Math.sqrt(2) : 1;
                        double cv = (v == 0) ? 1 / Math.sqrt(2) : 1;
                        sum += cu * cv * block[u][v] * Math.cos(((2 * x + 1) * u * Math.PI) / (2 * N)) * Math.cos(((2 * y + 1) * v * Math.PI) / (2 * N));
                    }
                }
                idct[x][y] = 0.25 * sum;
            }
        }
        return idct;
    }
}
