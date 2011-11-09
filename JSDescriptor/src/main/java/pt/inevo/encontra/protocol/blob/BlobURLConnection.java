package pt.inevo.encontra.protocol.blob;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class BlobURLConnection extends URLConnection {
    public BlobURLConnection(final URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        return;
    }
}
