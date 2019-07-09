import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.file.StandardOpenOption;

public class WeatherReader implements Runnable{
    ConcurrentLinkedQueue<org.jsoup.nodes.Element> list;
    String fileName;
    WeatherReader(ConcurrentLinkedQueue<Element> list, String fileName){
        this.list = list;
        this.fileName = fileName;
    }

    public synchronized void run() {




            Element element = list.poll();
            if (element==null){
                return;
            }else {
                String data = "\n"+element.select("a").attr("title");


                Document doc = null;
                try {
                    //doc = Jsoup.connect("http://weather.bigmir.net"+element.attr("href")).timeout(1000*60*60).get();

                    ///
                    ///
                    //TCP Try
                    ///
                    ///
                    Socket s = new Socket();
                    String host = "weather.bigmir.net"+element.attr("href");
                    PrintWriter s_out = null;
                    BufferedReader s_in = null;

                    try
                    {
                        s.connect(new InetSocketAddress(host , 80),1000*60*60);
                        s.setSoTimeout(1000*60*60);

                        s_out = new PrintWriter( s.getOutputStream(), true);
                        s_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    }

                    catch (UnknownHostException e)
                    {
                        System.err.println("Don't know about host : " + host);
                        System.exit(1);
                    }

                    s_out.print("GET /"+element.attr("href")+" HTTP/1.1\r\n");
                    s_out.print("Host: weather.bigmir.net\r\n\r\n");
                    //s_out.println( message );
                    s_out.println("Accept-Language: ru-ru");
                    s_out.println();
                    s_out.flush();

                    String response;
                    StringBuilder htmlDoc = new StringBuilder();
                    for (int i = 0; i < 11; i++) {
                        s_in.readLine();
                    }
                    while ((response = s_in.readLine()) != null)
                    {
                        htmlDoc.append(response);

                    }

                 
                    doc = Jsoup.parse(htmlDoc.toString());


                    Date dt = new Date();
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.setTime(dt);

                    Elements newsHeadlines = doc.select("td.text_center");
                    for (int i=0;i<newsHeadlines.size()-2;i++) {
                        String sEl = newsHeadlines.get(i).select("h2").toString().replace("<h2>","").replace("</h2>","").replace("</span>","");
                        //data+="\n Data: "+"\tday = "+sEl.substring(0,sEl.indexOf('&'))+"\tnight = "+sEl.substring(sEl.indexOf('>')+1)+"\t";

                        data+="\n Data: "+sdf.format(c.getTime())+"\tday = "+sEl.substring(0,sEl.indexOf('&'))+"\tnight = "+sEl.substring(sEl.indexOf('>')+1)+"\t";
                        c.add(Calendar.DATE, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                   Singleton.getInstance().writeToFile(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }


}




