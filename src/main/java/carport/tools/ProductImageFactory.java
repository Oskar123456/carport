package carport.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import carport.entities.ProductImage;

/**
 * PictureFactory
 */
public class ProductImageFactory {
    /**
     * @param Url
     * @return
     */
    public static ProductImage FromURL(String Url){
        ProductImage pic = null;
        try {
            URI uri = new URI(Url);
            URL url = uri.toURL();
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();

            c.setRequestMethod("GET");
            c.setRequestProperty("User-Agent", "Mozilla/5.0");
            c.connect();

            int responseCode = c.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
               byte[] data = c.getInputStream().readAllBytes();
                String imgFormat = c.getContentType();
                if (imgFormat.contains("image") && imgFormat.split("/").length == 2){
                    pic = new ProductImage(0, url.getFile(), Url, data, imgFormat.split("/")[1]);
                }
                else {
                    String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
                    System.err.println(thisMethodName + "::unknown image format (" + imgFormat + ")");
                }
            }

        } catch (URISyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
        return pic;
    }
    /**
     * @param Pic
     * @param NewHeight
     * @return
     */
    public static ProductImage Resize(ProductImage Pic, int Width){

        BufferedImage imgOriginal = ImageWrangler.BytesToImg(Pic.Data());
        BufferedImage imgResized = Scalr.resize(imgOriginal, Method.AUTOMATIC, Width, Width);

        byte[] imgResizedAsBytes = null;
        ByteArrayOutputStream imgToBytes = null;
        try {
            imgToBytes = new ByteArrayOutputStream();
            ImageIO.write(imgResized, Pic.Format(), imgToBytes);
            ByteArrayInputStream bytesToArray = new ByteArrayInputStream(imgToBytes.toByteArray());
            imgResizedAsBytes = bytesToArray.readAllBytes();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new ProductImage(0, Pic.Name(), Pic.Source(), imgResizedAsBytes, Pic.Format());
    }
}
