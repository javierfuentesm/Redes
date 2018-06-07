package wget;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author 
 */
public class Pool {
    private ArrayList<String> urls;
    
    public Pool(ArrayList<String> urls){
        this.urls = urls;
    }
    
    public void startDownload(){
        ExecutorService es = Executors.newFixedThreadPool(30);
        int i = 1;
        for (int j = 0; j < urls.size(); j++) {
            try {
                URL url = new URL(urls.get(j));
                Runnable descargar = new Descarga(url,i);
                Thread t1 = new Thread(descargar);
                //t1.start();
                es.execute(t1);
                t1.join();  
                i++;
            }catch (MalformedURLException ex) {
                System.out.println("Url inválida "+ex.getMessage());
            }catch (InterruptedException ex) {
                System.out.println("Error "+ex.getMessage());
            }
        }
        es.shutdown();
        while (!es.isTerminated()) {
        }
        System.out.println("La descarga ha finalizado");
    }
    
}
