package pt.inevo.encontra.demo;

import java.awt.image.BufferedImage;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import pt.inevo.encontra.index.annotation.Indexed;
import pt.inevo.encontra.storage.IEntity;

@Entity
public class ImageModel implements IEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String filename;
    private String description;

    @Transient
    private BufferedImage image;

    public ImageModel() {
    }

    public ImageModel(String filename, String description, BufferedImage image) {
        this.filename = filename;
        this.description = description;
        this.image = image;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Indexed
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Indexed
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Indexed
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "TestModel{"
                + "id=" + id
                + ", title='" + filename + '\''
                + ", content='" + description + '\''
                + '}';
    }
}
