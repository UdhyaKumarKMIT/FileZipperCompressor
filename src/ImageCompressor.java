import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageCompressor {

    public void compressImage(File inputFile, File outputFile, float quality) throws Exception {
        // Read the image
        BufferedImage image = ImageIO.read(inputFile);

        int width = image.getWidth();
        int height = image.getHeight();

        // Convert image to grayscale (simpler compression)
        // Apply grayscale with light reduction (slightly dimmer)
      //  BufferedImage grayscaleImage = convertToGrayscale(image, 54325.5f);


        // Perform DCT and compression
        BufferedImage compressedImage = applyDCTCompression(image, quality);

        // Save compressed image
        ImageIO.write(compressedImage, "jpg", outputFile);
    }
    private BufferedImage convertToGrayscale(BufferedImage image, float reductionFactor) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);

                // Extract RGB components
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Convert to grayscale using weighted sum
                int gray = (int) (0.2989 * r + 0.587 * g + 0.114 * b);

                // Apply reduction factor to make the grayscale darker, but lightly
                gray = (int) (gray * reductionFactor);

                // Ensure the value stays within the 0-255 range
                gray = Math.min(255, Math.max(0, gray));

                // Set the pixel to the reduced grayscale value
                grayscaleImage.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
            }
        }

        return grayscaleImage;
    }

    private BufferedImage applyDCTCompression(BufferedImage image, float quality) {
        int width = image.getWidth();
        int height = image.getHeight();
        int blockSize = 8; // DCT block size
        DctProcessor dctProcessor = new DctProcessor(blockSize);

        BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < width; i += blockSize) {
            for (int j = 0; j < height; j += blockSize) {
                // Extract 8x8 block
                double[][] block = new double[blockSize][blockSize];
                for (int x = 0; x < blockSize; x++) {
                    for (int y = 0; y < blockSize; y++) {
                        if (i + x < width && j + y < height) {
                            block[x][y] = image.getRaster().getSample(i + x, j + y, 0) - 128; // Center around 0
                        }
                    }
                }

                // Apply DCT
                double[][] dctBlock = dctProcessor.dct(block);

                // Quantize DCT coefficients based on quality
                dctProcessor.quantize(dctBlock, quality);

                // Inverse DCT
                double[][] idctBlock = dctProcessor.idct(dctBlock);

                // Store the processed block back into the image
                for (int x = 0; x < blockSize; x++) {
                    for (int y = 0; y < blockSize; y++) {
                        if (i + x < width && j + y < height) {
                            int value = (int) (idctBlock[x][y] + 128); // Recenter around 128

                            // Clamping the value between 0 and 255
                            value = Math.min(255, Math.max(0, value));
                            compressedImage.getRaster().setSample(i + x, j + y, 0, value);
                        }
                    }
                }
            }
        }

        return compressedImage;
    }

}
