package pt.inevo.encontra.drawing.sample.model;

import pt.inevo.encontra.drawing.Drawing;
import pt.inevo.encontra.index.annotation.Indexed;
import pt.inevo.encontra.storage.IEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SampleDrawing implements IEntity<Long>{

    @Id
    @GeneratedValue
    private Long id;

    private Drawing drawing;
    private DrawingCategory category;
    private String description;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
       this.id=id;
    }

    @Indexed
    public Drawing getDrawing(){
       return drawing;
    }

    @Indexed
    public String getDescription(){
       return description;
    }

    public DrawingCategory getCategory() {
       return category; 
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    public void setCategory(DrawingCategory category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
