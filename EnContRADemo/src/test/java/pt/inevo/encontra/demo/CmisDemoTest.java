package pt.inevo.encontra.demo;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Future;
import junit.framework.TestCase;
import pt.inevo.encontra.common.DefaultResultProvider;
import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.common.ResultSet;
import pt.inevo.encontra.demo.loader.*;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
import pt.inevo.encontra.engine.SimpleEngine;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.image.descriptors.ColorLayoutDescriptor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.search.ParallelSimpleSearcher;
import pt.inevo.encontra.nbtree.index.BTreeIndex;
import pt.inevo.encontra.nbtree.index.ParallelNBTreeSearcher;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Path;
import pt.inevo.encontra.query.QueryProcessorDefaultParallelImpl;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.query.criteria.StorageCriteria;
import pt.inevo.encontra.storage.CMISObjectStorage;
import pt.inevo.encontra.storage.JPAObjectStorage;
import scala.Option;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Demo of the EnContRA Framework API.
 * Loading a sample database and extracting simple descriptors from it, and then
 * perform a similarity query.
 *
 * @author Ricardo
 */
public class CmisDemoTest extends TestCase {

    /*
     * Internal properties
     */
    protected SimpleEngine<ImageModel> e;
    protected Properties props;
    protected String DATABASE_FOLDER = "";
    protected String QUERY_IMAGE_PATH = "";
    protected String QUERY_IMAGE_PATH2 = "";
    protected Map<String, String> parameter;

    private CMISObjectStorage<CmisImageModel> storage;

    public class CmisImageModelStorage extends CMISObjectStorage<CmisImageModel> {

        CmisImageModelStorage(Map<String, String> parameters) {
            super(parameters);
        }
    }

    //load properties file
    private void loadProperties() {
        props = new Properties();
        try {
            props.load(new FileInputStream("src/test/resources/demo.properties"));
            DATABASE_FOLDER = props.getProperty("database_folder");
            QUERY_IMAGE_PATH = props.getProperty("query_image_path");
            QUERY_IMAGE_PATH2 = props.getProperty("query_image_path2");

            Properties properties = new Properties();
            properties.load(new FileInputStream("src/test/resources/cmis_config.properties"));
            parameter = new HashMap<String, String>();
            Enumeration e = properties.propertyNames();
            while (e.hasMoreElements()) {
                String propertyName = e.nextElement().toString();
                parameter.put(propertyName, properties.getProperty(propertyName));
            }
        } catch (IOException ex) {
            System.out.println("Error, couldn't load the properties file.\nMessage:" + ex.getMessage());
        }
    }

    //initializes all the structures needed for EnContRA to work
    private void initEngine() {
        //Creating the EntityStorage for saving the objects
        CmisImageModelStorage storage = new CmisImageModelStorage(parameter);

        //Creating the descriptor extractor for strings
        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);

        //Creating the engine
        System.out.println("Creating the Retrieval Engine...");
        e = new SimpleEngine<ImageModel>();
        e.setObjectStorage(storage);
        e.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());
        e.getQueryProcessor().setTopSearcher(e);
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

    public CmisDemoTest(String testName) {
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

    private void load() {
        long timeBefore = 0, timeAfter = 0;
        //load and index the files only once
        File f = new File("contentSearcher.db");
        if (!f.exists()) {
            System.out.println("Loading some objects to the test indexes...");
            CmisImageModelLoader loader = new CmisImageModelLoader(DATABASE_FOLDER);
            loader.scan();

            ActorRef loaderActor = UntypedActor.actorOf(new UntypedActorFactory() {
                @Override
                public UntypedActor create() {
                    return new CmisImageLoaderActor(e);
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
            String storageCriteria = "cmis:contentStreamFileName like '%Washington%'";

            timeBefore = Calendar.getInstance().getTimeInMillis();

            query = query.where(
                    cb.and(
                            cb.similar(imagePath, image),
                            cb.similar(imagePath, image2))).distinct(true).limit(100);

            query.setCriteria(new StorageCriteria(storageCriteria));

            ResultSet<CmisImageModel> results = e.search(query);

            timeAfter = Calendar.getInstance().getTimeInMillis();

            System.out.println("Search took: " + (timeAfter - timeBefore));

            System.out.println("Number of retrieved elements: " + results.getSize());
            for (Result<CmisImageModel> r : results) {
                System.out.print("Retrieved element: " + r.getResultObject().toString() + "\t");
                System.out.println("Similarity: " + r.getScore());
            }
        } catch (IOException ex) {
            System.out.println("[Error] Couldn't load the query image. Possible reason: " + ex.getMessage());
        }
    }
}