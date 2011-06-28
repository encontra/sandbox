package pt.inevo.encontra.drawing.sample;

import pt.inevo.encontra.drawing.Drawing;

import pt.inevo.encontra.drawing.descriptors.DrawingTopology;
import pt.inevo.encontra.drawing.geometry.descriptors.DrawingGeometry;
import pt.inevo.encontra.drawing.sample.dejaVista.DejaVistaModel;
import pt.inevo.encontra.engine.AnnotatedIndexedObjectFactory;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.IndexedObjectFactory;

import java.util.ArrayList;
import java.util.List;


public class DrawingObjectFactory extends AnnotatedIndexedObjectFactory {

    public DejaVistaModel createModel(Drawing drawing){
        DejaVistaModel model=new DejaVistaModel();
        model.setGeometry(new DrawingGeometry(drawing));
        model.setTopology(new DrawingTopology(drawing));
        return model;
    }

    @Override
    protected List<IndexedObject> createObjects(List<IndexedObjectFactory.IndexedField> indexedFields) {
        List<IndexedObject> result = new ArrayList<IndexedObject>();
        for(IndexedObjectFactory.IndexedField field : indexedFields){
            if(field.object instanceof Drawing){
                DejaVistaModel model=createModel((Drawing) field.object);

                result.add(new IndexedObject(field.id, field.name, model, field.boost));
            } else {
                result.add(new IndexedObject(field.id, field.name, field.object, field.boost));
            }
        }
        return result;
    }


}
