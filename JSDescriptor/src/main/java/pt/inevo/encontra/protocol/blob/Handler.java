package pt.inevo.encontra.protocol.blob;


import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends java.net.URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new BlobURLConnection(url);
    }

    public static String PACKAGE = Handler.class.getPackage().toString();

    public static void register(){
        // this property controls where java looks for
        // stream handlers - always uses current value.
        final String key = "java.protocol.handler.pkgs";

        String newValue = PACKAGE;

        if ( System.getProperty( key ) != null )
        {
            final String previousValue = System.getProperty( key );
            newValue += "|" + previousValue;
        }
        System.setProperty( key, newValue );
    }
}
