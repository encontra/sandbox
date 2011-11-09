package pt.inevo.encontra.server;

import at.lux.imageanalysis.ScalableColorImpl;
import at.lux.imageanalysis.VisualDescriptor;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import pt.inevo.encontra.client.EnContRAService;
import pt.inevo.encontra.shared.DescriptorExtractor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EnContRAServiceImpl extends RemoteServiceServlet implements
        EnContRAService {

    private String getDescriptors(){
        String result = "";

        try {
            File file = new File("smiley.png");

            BufferedImage img = ImageIO.read(file);
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            g.drawImage(img, 0, 0, null);

            List<VisualDescriptor> descriptors =  new DescriptorExtractor().extract(bi);
            for(VisualDescriptor descriptor : descriptors){
                result += descriptor.getStringRepresentation() + "<br>";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String index(String input) throws IllegalArgumentException {

        String result = getDescriptors();
        String match = result.equals(input)? "OK":"NOT OK";
        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");

        // Escape data from the client to avoid cross-site script vulnerabilities.
        userAgent = escapeHtml(userAgent);

        return "Got : " + input + "<br>"+ "" +
                "Expected : " + result + "<br>"+
                "Everything is " + match + "<br>" +
                "<br>I am running " + serverInfo
                + ".<br><br>It looks like you are using:<br>" + userAgent;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     *
     * @param html the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }
}
