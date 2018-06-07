/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Emiliano
 */
public class URLS {

    public ArrayList<String> urlsF = new ArrayList<String>();

    public URLS (String u){
        getURL(u);
        fin();
    }
    
    public void inicio(String url){
        getURL(url);
        fin();
    }

    public ArrayList<String> getUrlsF() {
        return urlsF;
    }
    
    
    public void getURL(String urlEntrante){
        System.out.println("Obteniendo url's...");
        ArrayList<String> urlsL = new ArrayList<String>();
        String url=urlEntrante;
        if(url.charAt(url.length()-1) == '/'){
            url=url.substring(0,url.length()-1);
        }
        String s,host=null,protocol=null,path=null;
        String urls[]=new String[1000];
        int hrefTotales=0,srcTotales=0,cont=0,tope=0;
        //System.out.println("URL INICIAL: " + url);
        for(;;){
            try{
                URL u = new URL(url);
                host=u.getHost();
                path=u.getPath();
                protocol=u.getProtocol();
                try(InputStream is = u.openStream()){
                    BufferedReader dis = new BufferedReader(new InputStreamReader(is));
                    while ((s = dis.readLine()) != null){
                        int conthp=0,contsrc=0;
                        //System.out.println(s);
                        //Guardamos los href
                        if(s.contains("href=")){
                            int indexEndHref[] = new int[10000];
                            //Checamos cuantos href contiene
                            Pattern p = Pattern.compile("href=");
                            Matcher m = p.matcher(s);
                            while (m.find()) {
                                conthp++;
                                indexEndHref[conthp]=m.end();
                                hrefTotales++;
                            }
                            String urlTemp=s;
                            for (int i = 0; i < conthp; i++) {
                                urlTemp=s.substring(0,indexEndHref[conthp-i]);
                                urls[cont]=s.substring(indexEndHref[conthp-i]);
                                urls[cont]=urls[cont].substring(1);
                                tope=urls[cont].indexOf('"');
                                if(tope != -1)
                                    urls[cont]= urls[cont].substring(0 , tope);
                                cont++;
                            }
                        }
                        
                        //Guardamos los src
                        if(s.contains("src=")){
                            int indexEndSrc[] = new int[10000];
                            //Checamos cuantos href contiene
                            Pattern p = Pattern.compile("src=");
                            Matcher m = p.matcher(s);
                            while (m.find()) {
                                contsrc++;
                                indexEndSrc[contsrc]=m.end();
                                srcTotales++;
                            }
                            String urlTemp=s;
                            for (int i = 0; i < contsrc; i++) {
                                urlTemp=s.substring(0,indexEndSrc[contsrc-i]);
                                urls[cont]=s.substring(indexEndSrc[contsrc-i]);
                                urls[cont]=urls[cont].substring(1);
                                tope=urls[cont].indexOf('"');
                                if(tope != -1)
                                    urls[cont]= urls[cont].substring(0 , tope);
                                cont++;
                            }
                        }
                    }
                    hrefTotales=0;
                    srcTotales=0;
                    break;
                }catch (MalformedURLException mue){
                    System.err.println("Ouch - a MalformedURLException happened.");
                    mue.printStackTrace();
                    break;
                }
                catch (IOException ioe){
                   System.out.println("No se pudo acceder a la URL: " + url);
                  break;
                }
            }catch(Exception e){
                e.printStackTrace();
                break;
            }
        }
        
        //Analizamos los contenidos de los href's y src's
        if(cont<1){
            System.out.println("No tiene URL's anidadas");
        }else{
            System.out.println("Se analizaran las URL's anidadas");
            for (int i = 0; i < cont; i++) {
                if(urls[i].length()>0 ){
                    if(!(urls[i].contains("'")) || !(urls[i].contains(","))){
                        if(urls[i].contains("http")){
                            if(newURL(urls[i])){
                                urlsF.add(urls[i]);
                                urlsL.add(urls[i]);
                            }
                        }else{
                            if(urls[i].contains(".com") || urls[i].contains(".mx")){
                                if(newURL("http://"+urls[i])){
                                    urlsF.add("http://"+urls[i]);
                                    urlsL.add("http://"+urls[i]);
                                }
                            }else{
                                if(urls[i].charAt(0) == '/'){
                                    if(newURL(protocol+"://"+host+urls[i])){
                                        urlsF.add(protocol+"://"+host+urls[i]);
                                        urlsL.add(protocol+"://"+host+urls[i]);
                                    }
                                }else{
                                    if(urls[i].contains(".")){
                                        if(newURL(protocol+"://"+host+path+"/"+urls[i])){
                                            urlsF.add(protocol+"://"+host+path+"/"+urls[i]);
                                            urlsL.add(protocol+"://"+host+path+"/"+urls[i]);
                                        }
                                    }else{
                                        if(urls[i].charAt(0) == '#'){
                                            //System.out.println("Entra aqui #");
                                        }else{
                                            if(urls[i].charAt(urls[i].length()-1) == '/'){
                                                if(newURL(protocol+"://"+host+path+"/"+urls[i])){
                                                    urlsF.add(protocol+"://"+host+path+"/"+urls[i]);
                                                    urlsL.add(protocol+"://"+host+path+"/"+urls[i]);
                                                }
                                            }else{
                                                if(newURL(protocol+"://"+host+"/"+urls[i])){
                                                    urlsF.add(protocol+"://"+host+"/"+urls[i]);
                                                    urlsL.add(protocol+"://"+host+"/"+urls[i]);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //Funcion Recursiva
        for (int i = 0; i < urlsL.size(); i++) {
            int in=urlsL.get(i).lastIndexOf("/");
            String archivo=urlsL.get(i).substring(in+1);
            if(!archivo.contains(".js") && !archivo.contains(".css") 
            && !archivo.contains(".png") && !archivo.contains(".jpg") 
            && !archivo.contains(".gif") && !archivo.contains(".pdf") ){
                getURL(urlsL.get(i));
            }
        }
    
    }
    
    public void fin(){
        System.out.println("Busqueda Finalizada");
        //urlsF.forEach(System.out::println);
    }
    
    public boolean newURL(String urlPrev){
        String ur=urlPrev;
        for (int i = 0; i < urlsF.size(); i++) {
            if(urlsF.get(i).equals(ur)){
                return false;
            }
        }
        return true;
    }
}
