package pt.inevo.encontra.drawing.sample.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DrawingCategory {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
       this.id=id;
    }

    public DrawingCategory(String name){
        this.name=name;
    }
}
