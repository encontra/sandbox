package pt.inevo.encontra.client;

import at.lux.imageanalysis.*;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.*;

import org.mortbay.util.StringUtil;
import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import pt.inevo.encontra.shared.DescriptorExtractor;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class JSDescriptor implements EntryPoint {

    Logger log = Logger.getLogger("JSDescriptor");

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final EnContRAServiceAsync encontraService = GWT
            .create(EnContRAService.class);

    @UiField
    Button resetBtn;
    @UiField
    Button browseBtn;
    @UiField
    DropPanel dropPanel;
    @UiField
    FileUploadExt fileUpload;
    @UiField
    FileUploadExt customUpload;
    @UiField
    FlowPanel imagePanel;

    DialogBox dialogBox;
    HTML serverResponseLabel;
    Button closeButton;

    interface JSDescriptorBinder extends UiBinder<FlowPanel, JSDescriptor> {
    }
    private static JSDescriptorBinder binder = GWT.create(JSDescriptorBinder.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        //if(!GWT.isScript()){
        //    pt.inevo.encontra.protocol.blob.Handler.register();
        //}
        FlowPanel flowPanel = binder.createAndBindUi(this);

        final Style style = dropPanel.getElement().getStyle();
        style.setWidth(150, Unit.PX);
        style.setHeight(150, Unit.PX);
        style.setBorderStyle(BorderStyle.SOLID);
        RootLayoutPanel.get().add(flowPanel);
        createDialog();
    }

    private void createDialog(){
        // Create the popup dialog box
        dialogBox = new DialogBox();
        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);
        closeButton = new Button("Close");
        // We can set the id of a widget by accessing its Element
        closeButton.getElement().setId("closeButton");
        final Label textToServerLabel = new Label();
        serverResponseLabel = new HTML();
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });

    }
    /**
     * Send the name from the nameField to the server and wait for a response.
     */
    private void sendDescriptorsToServer(List<VisualDescriptor> descriptors) {
        String str = "";
        // First, we validate the input.
        for(VisualDescriptor descriptor : descriptors){
            str += descriptor.getStringRepresentation() + "<br>";
        }


        encontraService.index(str,
                new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        // Show the RPC error message to the user
                        dialogBox
                                .setText("Remote Procedure Call - Failure");
                        serverResponseLabel
                                .addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(SERVER_ERROR);
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onSuccess(String result) {
                        dialogBox.setText("Remote Procedure Call");
                        serverResponseLabel
                                .removeStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(result);
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }
                });
    }


    private void setBorderColor(String color) {
        dropPanel.getElement().getStyle().setBorderColor(color);
    }

    private void processFiles(FileList files) {
        GWT.log("length=" + files.getLength());
        for (org.vectomatic.file.File file : files) {
            GWT.log("name=" + file.getName());
            GWT.log("urn=" + file.getUrn());
            String type = file.getType();
            GWT.log("type=" + type);
            if ("image/png".equals(type)) {
                /*
                String fileUrl = createObjectURL(file);

                log.info("FileURL = " + fileUrl);
                final Image img = new Image();
                img.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        analyse(img);
                    }
                });
                img.setUrl(fileUrl);
                imagePanel.add(img);*/

                analyse(file);
            }
        }
    }

    private void analyse(Image img){
        ImageElement imgEl = ImageElement.as(img.getElement());



        BufferedImage bufImg = null;
        URL url = null;
        try {
            url = new URL(img.getUrl());

            //try {
            bufImg = new BufferedImage(imgEl); // TODO - Make this work : ImageIO.read(url);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}

            List<VisualDescriptor> descriptors = new DescriptorExtractor().extract(bufImg);

            sendDescriptorsToServer(descriptors);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }// catch (IOException e) {
        //    e.printStackTrace();
        //}
    }


    private void analyse(org.vectomatic.file.File file){

        final FileReader reader = new FileReader();
        reader.addLoadEndHandler(new LoadEndHandler() {

            @Override
            public void onLoadEnd(LoadEndEvent event) {

                String result = reader.getResult();

                final String dataURL = "data:image/png;base64," + base64encode(result);
//final URL url = new URL(dataURL);
//java.io.File f = new java.io.File(dataURL);

                final Image img = new Image();

                img.addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        log.info("Image Loaded");

                        ScalableColorImpl descriptor = new ScalableColorImpl();
                        BufferedImage bufImg = null;
                        ImageElement imgEl = ImageElement.as(img.getElement());
                        bufImg = new BufferedImage(imgEl); // TODO - Make this work : ImageIO.read(url);
//} catch (IOException e) {
//    e.printStackTrace();
//}


                        List<VisualDescriptor> descriptors = new DescriptorExtractor().extract(bufImg);

                        sendDescriptorsToServer(descriptors);
                    }
                });
                imagePanel.add(img);
                img.setUrl(dataURL);



            }
        });
        reader.readAsBinaryString(file);
    }
    public final native String createObjectURL(JavaScriptObject blob) /*-{
        $wnd.URL = $wnd.URL || $wnd.webkitURL;
        return $wnd.URL.createObjectURL(blob);
    }-*/;

    private static native void debug(Object o) /*-{
        return $wnd.console.debug(o);
    }-*/;

    private static native void log(String msg) /*-{
        return $wnd.console.log(msg);
    }-*/;

    private static native String base64encode(String str) /*-{
        return $wnd.btoa(str);
    }-*/;

    @UiHandler("browseBtn")
    public void browse(ClickEvent event) {
        customUpload.click();
    }

    @UiHandler("resetBtn")
    public void reset(ClickEvent event) {
        for (int i = imagePanel.getWidgetCount() - 1; i >= 0; i--) {
            imagePanel.remove(i);
        }
    }

    @UiHandler("fileUpload")
    public void uploadFile1(ChangeEvent event) {
        processFiles(fileUpload.getFiles());
    }

    @UiHandler("customUpload")
    public void uploadFile2(ChangeEvent event) {
        processFiles(customUpload.getFiles());
    }

    @UiHandler("dropPanel")
    public void onDragOver(DragOverEvent event) {
        // Mandatory handler, otherwise the default
        // behavior will kick in and onDrop will never
        // be called
        event.stopPropagation();
        event.preventDefault();
    }

    @UiHandler("dropPanel")
    public void onDragEnter(DragEnterEvent event) {
        setBorderColor("#ff0000");
        event.stopPropagation();
        event.preventDefault();
    }

    @UiHandler("dropPanel")
    public void onDragLeave(DragLeaveEvent event) {
        setBorderColor("#000000");
        event.stopPropagation();
        event.preventDefault();
    }

    @UiHandler("dropPanel")
    public void onDrop(DropEvent event) {
        processFiles(event.getDataTransfer().<DataTransferExt>cast().getFiles());
        setBorderColor("#000000");
        event.stopPropagation();
        event.preventDefault();
    }
}
