package java.io;

public class File {
    private String url;
    
    public File(String url){
        this.url = url;
    }
    
    public String toURL(){
        return url;
    }
}
