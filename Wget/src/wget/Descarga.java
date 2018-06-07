package wget;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 
 */
public class Descarga implements Runnable {
    
    private URL url;
    private int i;
    public Descarga (URL url, int i){
        this.url = url;
        this.i = i;
    }
    
    private static String obtenerNombre(URL u){
        String nombre = "";
        String f =u.toExternalForm();
        String partes[] = f.split("/");
        int max = partes.length;
        nombre = partes[max-1];
        
        String subPartes[] = nombre.split("\\.");
        
        //Validado los archivos que no tienen extension
        if(subPartes.length!=2){
            String contentType = "";
            try {
                contentType = u.openConnection().getContentType();
            } catch (IOException ex) {
                Logger.getLogger(Descarga.class.getName()).log(Level.SEVERE, null, ex);
            }
            nombre = partes[max-1]+"."+MimeTypes.getExtension(contentType);
        }
        //Verificando que la extension no sea otra cosa EJ archivo 2.2
        if(subPartes.length == 2){
            String contentType = "";
            String extension = "";
            String verificandoExtension = MimeTypes.lookupMimeType(subPartes[1]);
            if(verificandoExtension==null){
                try {
                contentType = u.openConnection().getContentType();
                } catch (IOException ex) {
                Logger.getLogger(Descarga.class.getName()).log(Level.SEVERE, null, ex);
                }
                extension = MimeTypes.getExtension(contentType);
                nombre = nombre+"."+extension;
            }
               
        }
        if(nombre.contains("/") || nombre.contains("\\\\") || nombre.contains("*") || nombre.contains("'") || 
                nombre.contains("?") || nombre.contains("<") || nombre.contains(">") || nombre.contains("\\|") || nombre.contains(":")){
            nombre = nombre.replace("/", "");
            nombre = nombre.replace("\\\\", "");
            nombre = nombre.replace("*","");
            nombre = nombre.replace("'", "");
            nombre = nombre.replace("?", "");
            nombre = nombre.replace("<", "");
            nombre = nombre.replace(">", "");
            nombre = nombre.replaceAll("\\|", "");
            nombre = nombre.replace(":", "");
             String contentType = "";
            try {
                contentType = u.openConnection().getContentType();
            } catch (IOException ex) {
                Logger.getLogger(Descarga.class.getName()).log(Level.SEVERE, null, ex);
            }
            nombre = nombre + "."+MimeTypes.getExtension(contentType);
        }
                
        
        return nombre;
    }
    
    @Override
    public void run() {
           
        URLConnection urlc;
        ReentrantLock rl = new ReentrantLock();
        
        rl.lock();        
        try {

            urlc = url.openConnection();
            urlc.setConnectTimeout(10*60*1000);
            urlc.setReadTimeout(10*60*1000);
            String contentType = urlc.getContentType();
            String archivo = ""+obtenerNombre(url);
            System.out.println("Se descargó el archivo: "+archivo);
            DataInputStream dis = new DataInputStream (urlc.getInputStream());
            
            //ByteArrayInputStream bais = new ByteArrayInputStream (url.openStream());
            File f = new File("Archivos/"+obtenerNombre(url));
            FileOutputStream fos = new FileOutputStream(f);
            int t = 0;
            t = dis.read();
            while(t!=-1){
                fos.write(t);
                fos.flush();
                t = dis.read();
            }  
            fos.close();
            dis.close();
            
        } catch (IOException ex) {
            //Logger.getLogger(Descarga.class.getName()).log(Level.SEVERE, null, ex);
            //System.out.println("Error"+ex.getMessage());
        }finally{
           rl.unlock();
        }
        
    }
    
}
