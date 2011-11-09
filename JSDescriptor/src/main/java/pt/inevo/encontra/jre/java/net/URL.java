package java.net;


public class URL {
    private String url;

    public URL(String url) throws MalformedURLException {
        this.url = url;
    }
    
    @Override
    public String toString(){
        return url;
    }
}
