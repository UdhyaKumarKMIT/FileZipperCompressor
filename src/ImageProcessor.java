import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageProcessor {
    public BufferedImage loadImage(String imagePath) throws IOException {
        return ImageIO.read(new File(imagePath));
    }

    public void saveImage(BufferedImage image, String path) throws IOException {
        File outputFile = new File(path);
        ImageIO.write(image, "jpg", outputFile);
    }
}
