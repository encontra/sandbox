package pt.inevo.encontra.shared;

import at.lux.imageanalysis.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class DescriptorExtractor {
    Logger log = Logger.getLogger("DescriptorExtractor");
    List<VisualDescriptor> descriptors;

    public DescriptorExtractor(){
        descriptors = new ArrayList<VisualDescriptor>();
        descriptors.add(new ScalableColorImpl());
        descriptors.add(new ColorLayoutImpl());
        descriptors.add(new ColorStructureImplementation());
        descriptors.add(new EdgeHistogramImplementation());
    }
    public List<VisualDescriptor> extract(BufferedImage img){
        Date start = new Date();
        ((ScalableColorImpl) descriptors.get(0)).extract(img);
        log.info("ScalableColorImpl Extraction took " + (new Date().getTime() - start.getTime()) + " ms");

        start = new Date();
        ((ColorLayoutImpl) descriptors.get(1)).extract(img);
        log.info("ColorLayoutImpl Extraction took " + (new Date().getTime() - start.getTime()) + " ms");

        start = new Date();
        try {
            ((ColorStructureImplementation) descriptors.get(2)).extractFeature(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("ColorStructureImplementation Extraction took " + (new Date().getTime() - start.getTime()) + " ms");

        start = new Date();
        ((EdgeHistogramImplementation) descriptors.get(3)).extract(img);
        log.info("Extraction took " + (new Date().getTime() - start.getTime()) + " ms");

        return descriptors;
    }
}
