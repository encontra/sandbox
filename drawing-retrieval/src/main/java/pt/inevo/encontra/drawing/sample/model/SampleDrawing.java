package pt.inevo.encontra.drawing.sample.model;

import pt.inevo.encontra.drawing.Drawing;
import pt.inevo.encontra.index.annotation.Indexed;
import pt.inevo.encontra.storage.IEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class SampleDrawing implements IEntity<Long>{

    @Id
    @GeneratedValue
    private Long id;

    @Transient
    private Drawing drawing;

    private String category;
    private String description;

    public String getSVG(){
        return drawing.getSVG();
    }

    public void setSVG(String svg){
        drawing=new Drawing();
        drawing.createFromSVG(svg);
    }
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

    public String getCategory() {
       return category; 
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
