package pt.inevo.encontra.demo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import pt.inevo.encontra.demo.utils.FileUtil;

/**
 * Loader for Objects of the type: ImageModel.
 * @author Ricardo
 */
public class ImageModelLoader {

    protected String imagesPath = "";
    protected static Long idCount = 0l;
    protected List<File> imagesFiles;
    protected Iterator<File> it;
    protected HashMap<String, String> annotations = new HashMap<String, String>();

    public ImageModelLoader() {
    }

    public ImageModelLoader(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    private ImageModel loadImage(File image) {

        //for now only sets the filename
        ImageModel im = new ImageModel(image.getAbsolutePath(), "", null);
        im.setId(idCount++);

        //get the description
        String name = image.getName();
        im.setDescription(annotations.get(name));

        //get the bufferedimage
        try {
            BufferedImage bufImg = ImageIO.read(image);
            im.setImage(bufImg);
        } catch (IOException ex) {
            System.out.println("Error: ");
            //skip this image because i just can't read it
            return null;
        }

        return im;
    }

    public void scan() {
        scan(imagesPath);
    }

    public void scan(String path) {
        try {
            File root = new File(path);
            String[] extensions = {"jpg", "png"};
            imagesFiles = FileUtil.findFilesRecursively(root, extensions);
            it = imagesFiles.iterator();
            File annot = new File(path + "\\annotation.txt");
            if (annot.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(annot));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    String[] name = parts[0].split("/");
                    annotations.put(name[1] + ".jpg", line);
                }
            }
        } catch (IOException ex) {
            System.out.println("Could not read the annotations");
        }
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public ImageModel next() {
        return loadImage(it.next());
    }
}
