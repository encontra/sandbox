package java.awt.image;

import com.google.gwt.canvas.dom.client.ImageData;

public class WritableRaster extends Raster {

    public WritableRaster(ImageData imgData){
        super(imgData);
    }

    public void setPixel(int x, int y, int[] pixel){
        imgData.setRedAt(pixel[0], x, y);
        imgData.setGreenAt(pixel[1], x, y);
        imgData.setBlueAt(pixel[2], x, y);
    } 
}
