package pt.inevo.encontra.demo;

import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import javax.imageio.ImageIO;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.engine.SimpleEngine;
import junit.framework.TestCase;
import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.image.descriptors.ColorLayoutDescriptor;
import pt.inevo.encontra.index.*;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.nbtree.index.BTreeIndex;
import pt.inevo.encontra.nbtree.index.NBTreeSearcher;
import pt.inevo.encontra.query.*;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.storage.*;

/**
 * Demo of the EnContRA Framework API.
 * Loading a sample database and extracting simple descriptors from it, and then
 * perform a similarity query.
 * @author ricardo
 */
public class DemoTest extends TestCase {

    /*
     * Internal properties
     */
    protected EntityManagerFactory emf;
    protected EntityManager em;
    protected BTreeIndex filenameIndex, contentIndex, descriptionIndex;
    protected SimpleEngine<ImageModel> e;
    protected Properties props;

    protected String DATABASE_FOLDER = "";
    protected String QUERY_IMAGE_PATH = "";
    protected String QUERY_IMAGE_PATH2 = "";

    public DemoTest(String testName) {
        super(testName);
        props = new Properties();
        try {
            props.load(new FileInputStream("src/test/resources/demo.properties"));
            DATABASE_FOLDER = props.getProperty("database_folder");
            QUERY_IMAGE_PATH = props.getProperty("query_image_path");
            QUERY_IMAGE_PATH2 = props.getProperty("query_image_path2");
        } catch (IOException ex) {
            System.out.println("Error, couldn't load the properties file.\nMessage:" + ex.getMessage());
        }

        //Creating the EntityStorage for saving the objects
        ImageModelStorage storage = new ImageModelStorage();
        emf = Persistence.createEntityManagerFactory("manager");
        em = emf.createEntityManager();
        storage.setEntityManager(em);

        //Creating the descriptor extractor for strings
        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);

        //Creating the engine
        System.out.println("Creating the Retrieval Engine...");
        e = new SimpleEngine<ImageModel>();
        e.setObjectStorage(storage);
        e.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());

        //A searcher for the filenameIndex
        SimpleSearcher filenameSearcher = new SimpleSearcher();
        filenameSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        filenameIndex = new BTreeIndex("filenameSearcher", StringDescriptor.class);
        filenameSearcher.setIndex(filenameIndex);
        filenameSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());

        //A searcher for the descriptionIndex
        SimpleSearcher descriptionSearcher = new SimpleSearcher();
        descriptionSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        descriptionSearcher.setIndex(new BTreeIndex("descriptionSearcher", StringDescriptor.class));
        descriptionSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());

        //A searcher for the image contentIndex (using only one type of descriptor
        NBTreeSearcher imageSearcher = new NBTreeSearcher();
        //using a single descriptor
        imageSearcher.setDescriptorExtractor(new ColorLayoutDescriptor<IndexedObject>());
        //using a BTreeIndex
        imageSearcher.setIndex(new BTreeIndex("contentSearcher", ColorLayoutDescriptor.class));
        imageSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());

        e.getQueryProcessor().setSearcher("filename", filenameSearcher);
        e.getQueryProcessor().setSearcher("description", descriptionSearcher);
        e.getQueryProcessor().setSearcher("image", imageSearcher);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public class ImageModelStorage extends JPAObjectStorage<Long, ImageModel> {}

    //EnContRA Demo Test
    public void testDemo() {
        long timeBefore = 0, timeAfter = 0;

        File f = new File("contentSearcher.db");
        if (!f.exists()) {  //don't load the database again...must fix this
            System.out.println("Loading some objects to the test indexes...");
            ImageModelLoader loader = new ImageModelLoader(DATABASE_FOLDER);
            loader.scan();

            timeBefore = Calendar.getInstance().getTimeInMillis();
            for (int i = 0; loader.hasNext(); i++) {
                ImageModel im = loader.next();
                if (im != null) {
                    e.insert(im);
                }
            }

            timeAfter = Calendar.getInstance().getTimeInMillis();
            System.out.println("Insertion/load took: " + (timeAfter - timeBefore));
        }

        try {
            timeBefore = Calendar.getInstance().getTimeInMillis();

            System.out.println("Creating a knn query...");
            BufferedImage image = ImageIO.read(new File(QUERY_IMAGE_PATH));
            BufferedImage image2 = ImageIO.read(new File(QUERY_IMAGE_PATH2));

            CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
            CriteriaQuery<ImageModel> query = cb.createQuery(ImageModel.class);
            Path imagePath = query.from(ImageModel.class).get("image");
            query = query.where(
                    cb.or(
                        cb.similar(imagePath, image),
                        cb.similar(imagePath, image2))).distinct(true);

            ResultSet<ImageModel> results = e.search(query);

            timeAfter = Calendar.getInstance().getTimeInMillis();

            System.out.println("Search took: " + (timeAfter - timeBefore));

            System.out.println("Number of retrieved elements: " + results.size());
            for (Result<ImageModel> r : results) {
                System.out.print("Retrieved element: " + r.getResult().toString() + "\t");
                System.out.println("Similarity: " + r.getSimilarity());
            }
        } catch (IOException ex) {
            System.out.println("[Error] Couldn't load the query image. Possible reason: " + ex.getMessage());
        }
    }
}