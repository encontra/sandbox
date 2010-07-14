package pt.inevo.encontra.drawing.sample;

import pt.inevo.encontra.drawing.Drawing;
import pt.inevo.encontra.drawing.sample.model.DrawingCategory;
import pt.inevo.encontra.drawing.sample.model.SampleDrawing;
import pt.inevo.encontra.engine.Engine;
import pt.inevo.encontra.image.Image;
import pt.inevo.encontra.index.*;
import pt.inevo.encontra.index.search.CombinedSearcher;
import pt.inevo.encontra.index.search.Searcher;
import pt.inevo.encontra.index.search.SimpleCombinedSearcher;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.lucene.index.LuceneDescriptorExtractor;
import pt.inevo.encontra.lucene.index.LuceneEncontraIndex;
import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;

public class SampleApplication {
    public static void main(){

        DrawingCategory category=new DrawingCategory("icons");

        String filename="test.svg";
        Drawing drawing=new Drawing(filename);

        SampleDrawing sample=new SampleDrawing();
        sample.setDrawing(drawing);
        sample.setCategory(category);
        sample.setDescription("A very simple drawing to see if this stuff works!");

        SimpleIndex drawingIndex=new SimpleIndex();
        drawingIndex.setEntryFactory(new DescriptorIndexEntryFactory(SimpleIndexEntry.class));

        
        LuceneEncontraIndex imageIndex=new LuceneEncontraIndex("images");
        imageIndex.setEntryFactory(new LuceneIndexEntryFactory(new LuceneDescriptorExtractor()));


        CombinedSearcher searcher=new SimpleCombinedSearcher();
        searcher.add(drawingIndex);
        searcher.add(imageIndex);

        Engine<SampleDrawing> engine=new Engine<SampleDrawing>();
        engine.registerSearcher(Drawing.class,drawingIndex);
        //engine.registerSearcher(Image.class,imageIndex);
        //engine.registerSearcher(String.class,drawingIndex);



        engine.registerIndex(Drawing.class, drawingIndex);
        
        //CriteriaQuery<SampleDrawing> criteria = builder.createQuery( SampleDrawing.class );

        //criteria.where( builder.similar( personRoot.get( Person_.eyeColor ), "brown" ) );
    }

}
