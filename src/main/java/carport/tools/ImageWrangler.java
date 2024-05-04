package carport.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * ImageWrangler
 */
public class ImageWrangler {
    public static BufferedImage BytesToImg(byte[] data){
        BufferedImage img = null;
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        try {
            img = ImageIO.read(dataStream);
        } catch (IOException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            System.err.println(thisMethodName + "::failed to read datastream");
        }
        return img;
    }
}
