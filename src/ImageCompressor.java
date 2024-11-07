import java.awt.image.*;
import java.io.*;

public class ImageCompressor {
    private DCTProcessor dctProcessor;
    private ImageProcessor imageProcessor;

    public ImageCompressor(DCTProcessor dctProcessor, ImageProcessor imageProcessor) {
        this.dctProcessor = dctProcessor;
        this.imageProcessor = imageProcessor;
    }

    public File compressImage(BufferedImage image, String compressedImagePath) throws IOException {
        // Convert image to grayscale for simplicity
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int color = image.getRGB(i, j);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int gray = (r + g + b) / 3;
                grayscaleImage.setRGB(i, j, (gray << 16) | (gray << 8) | gray);
            }
        }

        // Divide image into blocks, apply DCT, and quantize
        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();
        File compressedFile = new File(compressedImagePath);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(compressedFile))) {
            for (int i = 0; i < width; i += 8) {
                for (int j = 0; j < height; j += 8) {
                    double[][] block = new double[8][8];
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            int pixelColor = grayscaleImage.getRGB(i + x, j + y);
                            block[x][y] = pixelColor & 0xFF;
                        }
                    }

                    // Apply DCT
                    double[][] dctBlock = dctProcessor.dct2d(block);
                    // Save the DCT coefficients (this is where compression happens)
                    for (int u = 0; u < 8; u++) {
                        for (int v = 0; v < 8; v++) {
                            dos.writeDouble(dctBlock[u][v]);
                        }
                    }
                }
            }
        }

        return compressedFile;
    }

    public File decompressImage(String compressedImagePath, String decompressedImagePath) throws IOException {
        // Read compressed file and reconstruct the image
        BufferedImage decompressedImage = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY);

        try (DataInputStream dis = new DataInputStream(new FileInputStream(compressedImagePath))) {
            int width = decompressedImage.getWidth();
            int height = decompressedImage.getHeight();
            for (int i = 0; i < width; i += 8) {
                for (int j = 0; j < height; j += 8) {
                    double[][] block = new double[8][8];
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            block[x][y] = dis.readDouble();
                        }
                    }

                    // Apply Inverse DCT
                    double[][] idctBlock = dctProcessor.idct2d(block);
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            int gray = (int) Math.round(idctBlock[x][y]);
                            if (gray < 0) gray = 0;
                            if (gray > 255) gray = 255;
                            decompressedImage.setRGB(i + x, j + y, (gray << 16) | (gray << 8) | gray);
                        }
                    }
                }
            }
        }

        // Save decompressed image
        imageProcessor.saveImage(decompressedImage, decompressedImagePath);
        return new File(decompressedImagePath);
    }
}
