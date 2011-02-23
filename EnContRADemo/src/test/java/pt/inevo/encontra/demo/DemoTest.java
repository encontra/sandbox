package pt.inevo.encontra.demo;

import pt.inevo.encontra.common.DefaultResultProvider;
import pt.inevo.encontra.common.Result;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Future;

import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import javax.imageio.ImageIO;

import pt.inevo.encontra.demo.loader.ImageModelLoader;
import pt.inevo.encontra.demo.loader.ImageLoaderActor;
import pt.inevo.encontra.demo.loader.Message;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.engine.SimpleEngine;
import junit.framework.TestCase;
import pt.inevo.encontra.common.ResultSet;
import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.image.descriptors.ColorLayoutDescriptor;
import pt.inevo.encontra.index.*;
import pt.inevo.encontra.index.search.ParallelSimpleSearcher;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.nbtree.index.BTreeIndex;
import pt.inevo.encontra.nbtree.index.NBTreeSearcher;
import pt.inevo.encontra.nbtree.index.ParallelNBTreeSearcher;
import pt.inevo.encontra.query.*;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.storage.*;
import scala.Option;

/**
 * Demo of the EnContRA Framework API.
 * Loading a sample database and extracting simple descriptors from it, and then
 * perform a similarity query.
 *
 * @author Ricardo
 */
public class DemoTest extends TestCase {

    /*
     * Internal properties
     */
    protected EntityManagerFactory emf;
    protected EntityManager em;
    protected SimpleEngine<ImageModel> e;
    protected Properties props;
    protected String DATABASE_FOLDER = "";
    protected String QUERY_IMAGE_PATH = "";
    protected String QUERY_IMAGE_PATH2 = "";

    //load properties file
    private void loadProperties() {
        props = new Properties();
        try {
            props.load(new FileInputStream("src/test/resources/demo.properties"));
            DATABASE_FOLDER = props.getProperty("database_folder");
            QUERY_IMAGE_PATH = props.getProperty("query_image_path");
            QUERY_IMAGE_PATH2 = props.getProperty("query_image_path2");
        } catch (IOException ex) {
            System.out.println("Error, couldn't load the properties file.\nMessage:" + ex.getMessage());
        }
    }

    //initializes all the structures needed for EnContRA to work
    private void initEngine() {
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
        e.setResultProvider(new DefaultResultProvider());

        //A searcher for the filenameIndex
        ParallelSimpleSearcher filenameSearcher = new ParallelSimpleSearcher();
        filenameSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        filenameSearcher.setIndex(new BTreeIndex("filenameSearcher", StringDescriptor.class));
        filenameSearcher.setResultProvider(new DefaultResultProvider());

        //A searcher for the descriptionIndex
        ParallelSimpleSearcher descriptionSearcher = new ParallelSimpleSearcher();
        descriptionSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        descriptionSearcher.setIndex(new BTreeIndex("descriptionSearcher", StringDescriptor.class));
        descriptionSearcher.setResultProvider(new DefaultResultProvider());

        //A searcher for the image contentIndex (using only one type of descriptor
        ParallelNBTreeSearcher imageSearcher = new ParallelNBTreeSearcher();
        //using a single descriptor
        imageSearcher.setDescriptorExtractor(new ColorLayoutDescriptor<IndexedObject>());
        //using a BTreeIndex
        imageSearcher.setIndex(new BTreeIndex("contentSearcher", ColorLayoutDescriptor.class));
        imageSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
        imageSearcher.setResultProvider(new DefaultResultProvider());

        e.getQueryProcessor().setSearcher("filename", filenameSearcher);
        e.getQueryProcessor().setSearcher("description", descriptionSearcher);
        e.getQueryProcessor().setSearcher("image", imageSearcher);
    }

    public DemoTest(String testName) {
        super(testName);
        loadProperties();
        initEngine();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public class ImageModelStorage extends JPAObjectStorage<Long, ImageModel> {
    }

    private void load() {
        long timeBefore = 0, timeAfter = 0;
        //load and index the files only once
        File f = new File("contentSearcher.db");
        if (!f.exists()) {
            System.out.println("Loading some objects to the test indexes...");
            ImageModelLoader loader = new ImageModelLoader(DATABASE_FOLDER);
            loader.scan();

            ActorRef loaderActor = UntypedActor.actorOf(new UntypedActorFactory() {
                @Override
                public UntypedActor create() {
                    return new ImageLoaderActor(e);
                }
            }).start();

            Message m = new Message();
            m.operation = "PROCESSALL";
            m.obj = loader;

            timeBefore = Calendar.getInstance().getTimeInMillis();

            Future future = loaderActor.sendRequestReplyFuture(m, Long.MAX_VALUE, null);
            future.await();
            if (future.isCompleted()) {
                Option resultOption = future.result();
                if (resultOption.isDefined()) {
                    Object result = resultOption.get();
                    System.out.println("Database Processed: " + result);
                } else {
                    System.out.println("There where an error processing the database!");
                }
            }

            timeAfter = Calendar.getInstance().getTimeInMillis();
            System.out.println("Insertion/load took: " + (timeAfter - timeBefore));
        }
    }

    //EnContRA Demo Test
    public void testDemo() {
        long timeBefore = 0, timeAfter = 0;

        load();

        try {
            System.out.println("Creating a knn query...");
            BufferedImage image = ImageIO.read(new File(QUERY_IMAGE_PATH));
            BufferedImage image2 = ImageIO.read(new File(QUERY_IMAGE_PATH2));

            CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
            CriteriaQuery<ImageModel> query = cb.createQuery(ImageModel.class);
            Path imagePath = query.from(ImageModel.class).get("image");

            timeBefore = Calendar.getInstance().getTimeInMillis();

            query = query.where(
                    cb.and(
                            cb.similar(imagePath, image),
                            cb.similar(imagePath, image2))).distinct(true).limit(100);

            ResultSet<ImageModel> results = e.search(query);

            timeAfter = Calendar.getInstance().getTimeInMillis();

            System.out.println("Search took: " + (timeAfter - timeBefore));

            System.out.println("Number of retrieved elements: " + results.getSize());
            for (Result<ImageModel> r : results) {
                System.out.print("Retrieved element: " + r.getResultObject().toString() + "\t");
                System.out.println("Similarity: " + r.getScore());
            }
        } catch (IOException ex) {
            System.out.println("[Error] Couldn't load the query image. Possible reason: " + ex.getMessage());
        }
    }
}