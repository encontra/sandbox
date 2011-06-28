package pt.inevo.encontra.drawing.sample;

import pt.inevo.encontra.common.DefaultResultProvider;
import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.common.ResultSet;
import pt.inevo.encontra.drawing.Drawing;
import pt.inevo.encontra.drawing.descriptors.TopologyDescriptorExtractor;
import pt.inevo.encontra.drawing.geometry.descriptors.GeometryDescriptorExtractor;
import pt.inevo.encontra.drawing.sample.dejaVista.DejaVistaEngine;
import pt.inevo.encontra.drawing.sample.model.SampleDrawing;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Path;
import pt.inevo.encontra.query.QueryProcessorDefaultImpl;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.query.criteria.StorageCriteria;
import pt.inevo.encontra.storage.IEntry;
import pt.inevo.encontra.storage.JPAObjectStorage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SampleApplication {
    public class DrawingStorage extends JPAObjectStorage<Long, SampleDrawing> {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("manager");
        EntityManager em = emf.createEntityManager(); // Retrieve an application managed entity manager

        public DrawingStorage() {
            super();
            setEntityManager(em);
        }
    }

    public class MyEngine extends AbstractSearcher<SampleDrawing> {

        @Override
        protected Result<SampleDrawing> getResultObject(Result<IEntry> entryresult) {
            Object result = storage.get(Long.parseLong(entryresult.getResultObject().getId().toString()));
            return new Result<SampleDrawing>((SampleDrawing) result);
        }
    }
    private MyEngine engine;
    private DejaVistaEngine drawingEngine;
    private DrawingStorage storage;



    private void setupEngine(){

        System.out.println("Configuring the Retrieval Engine...");
        engine = new MyEngine();
        storage = new DrawingStorage();

        drawingEngine = new DejaVistaEngine();

        engine.setObjectStorage(storage);
        engine.setQueryProcessor(new QueryProcessorDefaultImpl());  // QueryProcessorDefaultParallelImpl
       // TODO - Make IndexedObjectFactory work throughout the engine tree !
        engine.getQueryProcessor().setIndexedObjectFactory(new DrawingObjectFactory()); //new SimpleIndexedObjectFactory());
        engine.getQueryProcessor().setTopSearcher(engine);
        engine.setResultProvider(new DefaultResultProvider());

        engine.getQueryProcessor().setSearcher("drawing", drawingEngine);
    }

    private ResultSet<SampleDrawing> knnQuery(String keywords,Drawing drawing) {
        System.out.println("Creating a knn query...");

        //Creating a combined query for the results
        CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
        CriteriaQuery<SampleDrawing> criteriaQuery = cb.createQuery(SampleDrawing.class);

        //Create the Model/Attributes Path
        Path<SampleDrawing> model = criteriaQuery.from(SampleDrawing.class);

        Path drawingModel =  model.get("drawing"); //indexedObjectFactory.getTopologyPath();


        String storageQuery = "";
        if (!keywords.equals("")) {
            String [] keywordsSplit = keywords.split(",");
            for (int i = 0; i < keywordsSplit.length ; i++) {
                storageQuery += "category like '%" + keywordsSplit[i].toLowerCase() + "%' ";
                if (i+1 < keywordsSplit.length)
                    storageQuery += "and ";
            }
        }



        CriteriaQuery query = cb.createQuery().where(
                cb.similar(drawingModel, drawing)
        ).distinct(true).limit(10);
        ResultSet<SampleDrawing> results = null;

        //if (!keywords.equals("")) {
        //    query.setCriteria(new StorageCriteria(storageQuery));
        //}

        results = engine.search(query);
        System.out.println(engine.getBenchmark());
        System.out.println("...done! Query returned: " + results.getSize() + " results.");
        return results;
    }

    public void start() {
        setupEngine();

        String filename=SampleApplication.class.getResource("/svg/radioactive.svg").getPath();
        Drawing drawing=new Drawing(filename);

        SampleDrawing sample=new SampleDrawing();
        sample.setDrawing(drawing);
        sample.setCategory("icons");
        sample.setDescription("A very simple drawing to see if this stuff works!");

        engine.insert(sample);

        knnQuery("icons", drawing);
    }

    public static void main(String [] args){
        new SampleApplication().start();
    }

}
