/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wget;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author 
 */
public class Main {
    public static void main(String[] args) {
        String url;
        Scanner sc= new Scanner(System.in);
        System.out.println("Teclea la URL");
        url = sc.nextLine();
        System.out.println("Inicia la busqueda de urls");
        URLS urls = new URLS(url);
        ArrayList<String> urlsF = urls.getUrlsF();
        urlsF.add(url);
        System.out.println("Descargando el contenido...");
        Pool hilos = new Pool(urlsF);
        hilos.startDownload();
    }
}
