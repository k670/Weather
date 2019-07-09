
import org.jsoup.Connection;
import org.jsoup.Jsoup;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.util.concurrent.TimeUnit.SECONDS;

public class WeatherTry4 {
    public static void main(String[] args) throws IOException {
///
///
///TCP
///
///
       Socket s = new Socket();
        String host = "weather.bigmir.net";
        PrintWriter s_out = null;
        BufferedReader s_in = null;

        try
        {
            s.connect(new InetSocketAddress(host , 80),1000*60*60);
            s.setSoTimeout(60*60*1000);
            s_out = new PrintWriter( s.getOutputStream(), true);
            s_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        catch (UnknownHostException e)
        {
            System.err.println("Don't know about host : " + host);
            System.exit(1);
        }


        s_out.print("GET /ukraine/ HTTP/1.1\r\n");
        s_out.print("Host: weather.bigmir.net\r\n\r\n");
        //s_out.println( message );
        s_out.println("Accept-Language: ua");
        s_out.println();
        s_out.flush();

        System.out.println("Message send");

        String response;
        StringBuilder htmlDoc = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            s_in.readLine();
        }
        while ((response = s_in.readLine()) != null)
        {
            htmlDoc.append(response);

        }


        String fileName = "D:\\test.txt";
        ConcurrentLinkedQueue<Element> linkList = new ConcurrentLinkedQueue<Element>();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(150);


        try {
            Singleton.getInstance().deleteAndCreateFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            Document doc = Jsoup.parse(htmlDoc.toString());
            //Document doc = Jsoup.connect("http://weather.bigmir.net/ukraine/").timeout(1000*60*60).get();
            Elements newsHeadlines = doc.select("div.fl.W_col2");

            for (Element headline : newsHeadlines) {

                for (Element newElem:headline.select("li") ) {

                    linkList.add(newElem);
                }
            }

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }finally {

            while (linkList.size()>0) {

                executorService.scheduleAtFixedRate((new WeatherReader(linkList, fileName )),0,1000,TimeUnit.MILLISECONDS);
           }


            /*try {
                executorService.awaitTermination(60*20, SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        }

        executorService.shutdown();
    }
}