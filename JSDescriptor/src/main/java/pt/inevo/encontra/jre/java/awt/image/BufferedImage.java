package java.awt.image;

import java.net.URL;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.LoadEvent;

import java.util.logging.Logger;

public class BufferedImage {

    Logger log = Logger.getLogger("BufferedImage");
        
    public static int TYPE_INT_RGB = 1;
    
    Context2d context;
    Canvas canvas;
    int width;
    int height;
    private WritableRaster raster;
    private boolean _loaded = false;
    
    public BufferedImage(int width, int height, int type){
        canvas = Canvas.createIfSupported();

        if(canvas == null){
            // Not supported!
        }


        canvas.setWidth(width + "px");
        canvas.setHeight(height + "px");

        context = canvas.getContext2d();
        
        ImageData data = context.getImageData(0, 0, width, height);
        raster = new WritableRaster(data);
    }
    
    public BufferedImage(final URL url){
        this(url.toString());
    }
    
    public BufferedImage(final String url){
         final Image img = new Image();
         img.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                width = img.getWidth();
                height = img.getHeight();

                ImageElement imgEl = ImageElement.as(img.getElement());
                //ScalableColorImpl descriptor = new ScalableColorImpl();

                canvas = Canvas.createIfSupported();

                if(canvas == null){
                    // Not supported!
                }


                canvas.setWidth(width + "px");
                canvas.setHeight(height + "px");

                context = canvas.getContext2d();
                context.drawImage(imgEl, 0, 0);

                ImageData data = context.getImageData(0, 0, width, height);
                raster = new WritableRaster(data);
                
                _loaded = true;

            }
        });
        img.setUrl(url);



    }

    public BufferedImage(ImageElement imgEl){
       
        
        width = imgEl.getWidth();
        height = imgEl.getHeight();
        
         log.info("Creating BufferedImage from ImageElement (" + width + "," + height + ")");
                 
        canvas = Canvas.createIfSupported();

        if(canvas == null){
            // Not supported!
        }


        canvas.setWidth(width + "px");
        canvas.setHeight(height + "px");

        context = canvas.getContext2d();
        context.drawImage(imgEl, 0, 0);

        ImageData data = context.getImageData(0, 0, width, height);
        raster = new WritableRaster(data);
        
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public WritableRaster getRaster() {
        return raster;
    }
    
    public int	getRGB(int x, int y){
        int [] pixel = new int[3];
        int res = 0;
        raster.getPixel(x, y, pixel);
        return getRGB(pixel);
    }
    
    private int getRGB(int [] pixel){
        return ((pixel[0] & 0xFF) << 16) | ((pixel[1] & 0xFF) << 8) | (pixel[0] & 0xFF);
    }
    
    public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize){
        int yoff  = offset;
        int off;
        int [] pixel = new int[3];
        
        if(rgbArray == null) {
            rgbArray = new int[offset+h*scansize];
        }
        
        for (int y = startY; y < startY+h; y++, yoff+=scansize) {
             off = yoff;
             for (int x = startX; x < startX+w; x++) {
                 raster.getPixel(x, y, pixel);
                 rgbArray[off++] = getRGB(pixel);
             }
         }
 
         return rgbArray;
        
    }
}
