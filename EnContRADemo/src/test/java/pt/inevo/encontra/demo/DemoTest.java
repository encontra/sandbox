package pt.inevo.encontra.demo;

import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
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
 * Smoke test: testing the creation of a simple engine, two indexes and the
 * execution of a similarity query (testing also the combination of the queries).
 * @author ricardo
 */
public class DemoTest extends TestCase {

    EntityManagerFactory emf;
    EntityManager em;
    BTreeIndex filenameIndex, contentIndex, descriptionIndex;

    public DemoTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        em.close();
        emf.close(); //close at application end
        super.tearDown();
    }

    public class ImageModelStorage extends JPAObjectStorage<Long,ImageModel> {}

//    public void test1() {
//        //Creating the EntityStorage for saving the objects
//        ImageModelStorage storage = new ImageModelStorage();
//        emf = Persistence.createEntityManagerFactory("manager");
//        em = emf.createEntityManager();
//        storage.setEntityManager(em);
//
//        //Creating the descriptor extractor for strings
//        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);
//
//        //Creating the engine
//        System.out.println("Creating the Retrieval Engine...");
//        SimpleEngine<ImageModel> e = new SimpleEngine<ImageModel>();
//        e.setObjectStorage(storage);
//        e.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
//        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());
//
//        //A searcher for the filenameIndex
//        SimpleSearcher filenameSearcher = new SimpleSearcher();
//        filenameSearcher.setDescriptorExtractor(stringDescriptorExtractor);
//        filenameIndex = new BTreeIndex("filenameSearcher", StringDescriptor.class);
//        filenameSearcher.setIndex(filenameIndex);
//        filenameSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
//
//        //A searcher for the descriptionIndex
//        SimpleSearcher descriptionSearcher = new SimpleSearcher();
//        descriptionSearcher.setDescriptorExtractor(stringDescriptorExtractor);
//        descriptionIndex = new BTreeIndex("descriptionSearcher", StringDescriptor.class);
//        descriptionSearcher.setIndex(descriptionIndex);
//        descriptionSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
//
//        //A searcher for the image contentIndex (using only one type of descriptor
//        NBTreeSearcher imageSearcher = new NBTreeSearcher();
//        //using a single descriptor
//        imageSearcher.setDescriptorExtractor(new ColorLayoutDescriptor<IndexedObject>());
//        //using a BTreeIndex
//        contentIndex = new BTreeIndex("contentSearcher", ColorLayoutDescriptor.class);
//        imageSearcher.setIndex(contentIndex);
//        imageSearcher.setQueryProcessor(new QueryProcessorDefaultParallelImpl());
//
//        e.getQueryProcessor().setSearcher("filename", filenameSearcher);
//        e.getQueryProcessor().setSearcher("description", descriptionSearcher);
//        e.getQueryProcessor().setSearcher("image", imageSearcher);
//
//        long timeBefore = 0, timeAfter = 0;
//
//        System.out.println("Loading some objects to the test indexes...");
////        ImageModelLoader loader = new ImageModelLoader("C:\\Users\\Ricardo\\Desktop\\WashingtonUniversityDatabase");
//        ImageModelLoader loader = new ImageModelLoader("C:\\Users\\Ricardo\\Desktop\\testcases\\test\\database");
//        loader.scan();
//
//        timeBefore = Calendar.getInstance().getTimeInMillis();
//
//        for (int i = 0; loader.hasNext(); i++) {
//            ImageModel im = loader.next();
//            if (im != null) {
//                e.insert(im);
//            }
//        }
//
//        timeAfter = Calendar.getInstance().getTimeInMillis();
//
//        System.out.println("Insertion/load took: " + (timeAfter - timeBefore));
//
//        try {
//
//            timeBefore = Calendar.getInstance().getTimeInMillis();
//
//            System.out.println("Creating a knn query...");
//            BufferedImage image = ImageIO.read(new File("C:\\Users\\Ricardo\\Desktop\\testcases\\28\\28004.jpg"));
//
//            CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
//            CriteriaQuery<ImageModel> query = cb.createQuery(ImageModel.class);
//            Path imagePath = query.from(ImageModel.class).get("image");
//            query = query.where(cb.similar(imagePath, image));
//
//            ResultSet<ImageModel> results = e.search(query);
//
//            timeAfter = Calendar.getInstance().getTimeInMillis();
//
//            System.out.println("Search took: " + (timeAfter - timeBefore));
//
//            System.out.println("Number of retrieved elements: " + results.size());
//            for (Result<ImageModel> r : results) {
//                System.out.print("Retrieved element: " + r.getResult().toString() + "\t");
//                System.out.println("Similarity: " + r.getSimilarity());
//            }
//        } catch (IOException ex) {
//            System.out.println("[Error] Couldn't load the query image. Possible reason: " + ex.getMessage());
//        }
//    }

    public void test2() {
        //Creating the EntityStorage for saving the objects
        ImageModelStorage storage = new ImageModelStorage();
        emf = Persistence.createEntityManagerFactory("manager");
        em = emf.createEntityManager();
        storage.setEntityManager(em);

        //Creating the descriptor extractor for strings
        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);

        //Creating the engine
        System.out.println("Creating the Retrieval Engine...");
        SimpleEngine<ImageModel> e = new SimpleEngine<ImageModel>();
        e.setObjectStorage(storage);
        e.setQueryProcessor(new QueryProcessorDefaultImpl());
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

        long timeBefore = 0, timeAfter = 0;

        try {
            timeBefore = Calendar.getInstance().getTimeInMillis();

            System.out.println("Creating a knn query...");
            BufferedImage image = ImageIO.read(new File("C:\\Users\\Ricardo\\Desktop\\testcases\\28\\28004.jpg"));

            CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
            CriteriaQuery<ImageModel> query = cb.createQuery(ImageModel.class);
            Path imagePath = query.from(ImageModel.class).get("image");
            query = query.where(cb.similar(imagePath, image));

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
