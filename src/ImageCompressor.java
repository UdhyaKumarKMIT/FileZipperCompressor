import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageCompressor {

    public void compressImage(File inputFile, File outputFile, float quality) throws Exception {
        // Read the image
        BufferedImage image = ImageIO.read(inputFile);

        int width = image.getWidth();
        int height = image.getHeight();

        // Convert image to grayscale (for simplicity in DCT compression)
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                grayscaleImage.setRGB(x, y, rgb);
            }
        }

        // Perform DCT and compression
        BufferedImage compressedImage = applyDCTCompression(grayscaleImage, quality);

        // Save compressed image
        ImageIO.write(compressedImage, "jpg", outputFile);
    }

    private BufferedImage applyDCTCompression(BufferedImage image, float quality) {
        int width = image.getWidth();
        int height = image.getHeight();
        int blockSize = 8; // DCT block size

        BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < width; i += blockSize) {
            for (int j = 0; j < height; j += blockSize) {
                // Apply DCT to 8x8 block
                double[][] block = new double[blockSize][blockSize];
                for (int x = 0; x < blockSize; x++) {
                    for (int y = 0; y < blockSize; y++) {
                        if (i + x < width && j + y < height) {
                            block[x][y] = image.getRaster().getSample(i + x, j + y, 0) - 128; // Center around 0
                        }
                    }
                }

                // Forward DCT
                double[][] dctBlock = dct(block);

                // Quantize DCT coefficients based on quality
                quantize(dctBlock, quality);

                // Inverse DCT
                double[][] idctBlock = idct(dctBlock);

                // Store compressed block back into image
                for (int x = 0; x < blockSize; x++) {
                    for (int y = 0; y < blockSize; y++) {
                        if (i + x < width && j + y < height) {
                            int value = (int) Math.round(idctBlock[x][y] + 128); // Reverse center
                            value = Math.min(Math.max(value, 0), 255); // Clamp to byte range
                            compressedImage.getRaster().setSample(i + x, j + y, 0, value);
                        }
                    }
                }
            }
        }

        return compressedImage;
    }

    private double[][] dct(double[][] block) {
        // Implement 8x8 DCT transform
        int n = block.length;
        double[][] dctBlock = new double[n][n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                double sum = 0.0;
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        sum += block[x][y] *
                                Math.cos(((2 * x + 1) * u * Math.PI) / (2 * n)) *
                                Math.cos(((2 * y + 1) * v * Math.PI) / (2 * n));
                    }
                }
                dctBlock[u][v] = sum * (u == 0 ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n))
                        * (v == 0 ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n));
            }
        }
        return dctBlock;
    }

    private void quantize(double[][] dctBlock, float quality) {
        int n = dctBlock.length;
        int[][] quantizationMatrix = {
                {16, 11, 10, 16, 24, 40, 51, 61},
                {12, 12, 14, 19, 26, 58, 60, 55},
                {14, 13, 16, 24, 40, 57, 69, 56},
                {14, 17, 22, 29, 51, 87, 80, 62},
                {18, 22, 37, 56, 68, 109, 103, 77},
                {24, 35, 55, 64, 81, 104, 113, 92},
                {49, 64, 78, 87, 103, 121, 120, 101},
                {72, 92, 95, 98, 112, 100, 103, 99}
        };

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                dctBlock[u][v] = Math.round(dctBlock[u][v] / (quantizationMatrix[u][v] * quality));
            }
        }
    }

    private double[][] idct(double[][] dctBlock) {
        // Implement 8x8 Inverse DCT transform
        int n = dctBlock.length;
        double[][] block = new double[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                double sum = 0.0;
                for (int u = 0; u < n; u++) {
                    for (int v = 0; v < n; v++) {
                        sum += (u == 0 ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n)) *
                                (v == 0 ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n)) *
                                dctBlock[u][v] *
                                Math.cos(((2 * x + 1) * u * Math.PI) / (2 * n)) *
                                Math.cos(((2 * y + 1) * v * Math.PI) / (2 * n));
                    }
                }
                block[x][y] = sum;
            }
        }
        return block;
    }
}
