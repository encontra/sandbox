package pt.inevo.encontra.drawing.sample;

import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.IndexedObjectFactory;

import java.awt.image.BufferedImage;
import java.util.List;


public class SampleDrawingObjecFactory extends AbstractIndexedObjectFactory {

    @Override
    protected List<IndexedObject> createObjects(List<IndexedObjectFactory.IndexedField> indexedFields) {
        for(IndexedObjectFactory.IndexedField field : indexedFields){
            if(field.object instanceof BufferedImage){
                
            }
        }
    }
}
