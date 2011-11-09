package javax.imageio;


import com.google.gwt.user.client.ui.Image;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageIO {
    public static BufferedImage read(final File file) throws java.io.IOException {
         return new BufferedImage(file.toURL());
    }
    
    public static BufferedImage read(final URL url) throws java.io.IOException {
        return new BufferedImage(url);
    }


}
