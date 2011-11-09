package java.awt.image;


import com.google.gwt.canvas.dom.client.ImageData;
//import com.google.gwt.core.client.JsArray;
//import pt.inevo.encontra.client.array.Uint8Array;

public class Raster {

    public static int NUM_COLORS = 4;
    protected ImageData imgData;
    private int width;
    private int height;
    private int pixelCount;

    static{
        exportFunctions();
    }

    public Raster(ImageData imgData){
        this.width = imgData.getWidth();
        this.height = imgData.getHeight();
        this.pixelCount = width * height;
        this.imgData = imgData ;
    }

    public static native void exportFunctions() /*-{
        $wnd.rgb2hsv = function( r, g, b) {
            var h = null; var s = null; var v = null;
            var min = Math.min(r, g, b);

            v = Math.max(r, g, b);
            var delta = v - min;
            // Calculate saturation (0 if r, g and b are all 0)
            s = (v == 0) ? 0 : delta/v;
            if (s == 0) {
                // Achromatic: when saturation is, hue is undefined
                h = 0;
            }
            else {
                // Chromatic
                if (r == v) {
                    // Between yellow and magenta
                    h = 60 * (g - b) / delta;
                }
                else {
                    if (g == v) {
                        // Between cyan and yellow
                        h = 120 + 60 * (b - r) / delta;
                    }
                    else {
                        if (b == v) {
                            // Between magenta and cyan
                            h = 240 + 60 * (r - g) / delta;
                        }
                    }
                }
                if (h <= 0) { h += 360; }
            }
            s = s * 100;
            v = (v / 255) * 100;

            h = Math.round(h);
            s = Math.round(s);
            v = Math.round(v);

            return [h, s, v];
        };
    }-*/;

//    public native Uint8Array getHSVData() /*-{
//
//
//        var NUM_COLORS = @Raster::NUM_COLORS;
//        var count = NUM_COLORS * this.@pt.inevo.encontra.client.emulation.Raster::pixelCount;
//        var data = this.@pt.inevo.encontra.client.emulation.Raster::imgData.data;
//
//        var buf = new $wnd.Uint8Array(count*3);
//        var pos = 0;
//        var hsv;
//        var start = (new Date).getTime();
//        for( var i = 0; i < count; i += NUM_COLORS){
//            hsv = $wnd.rgb2hsv(data[i] || 0, data[i + 1] || 0, data[i + 2] || 0);
//            buf[pos] = hsv[0];
//            buf[++pos] = hsv[1];
//            buf[++pos] = hsv[2];
//        }
//        var diff = (new Date).getTime() - start;
//        console.log("Done in " + diff + "ms");
//        return buf;
//    }-*/;


    public void getPixel(int x, int y, int[] pixel) {
        pixel[0] = imgData.getRedAt(x, y);
        pixel[1] = imgData.getGreenAt(x, y);
        pixel[2] = imgData.getBlueAt(x, y);
        //pixel[3] = imgData.getAlphaAt(x, y);
    }


}
