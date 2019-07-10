import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
//import java.nio.file.StandardOpenOption;

public class WeatherReader implements Runnable {
    ConcurrentLinkedQueue<org.jsoup.nodes.Element> list;
    String fileName;

    WeatherReader(ConcurrentLinkedQueue<Element> list, String fileName) {
        this.list = list;
        this.fileName = fileName;
    }

    public  void run() {
        String baseUrl = "http://weather.bigmir.net";
       // String host = "weather.bigmir.net";

        String data = loadDocJsoup(baseUrl,0);
         try {
            Singleton.getInstance().writeToFile(data, fileName);
         } catch (IOException e) {
            e.printStackTrace();
         }
    }


    private String parsDoc(Document doc, String title) {
        String data = "\n" + title;

        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(dt);

        Elements newsHeadlines = doc.select("td.text_center");
        for (int i = 0; i < newsHeadlines.size() - 2; i++) {
            String sEl = newsHeadlines.get(i).select("h2").toString().replace("<h2>", "").replace("</h2>", "").replace("</span>", "");
            data += "\n Data: " + sdf.format(c.getTime()) + "\tday = " + sEl.substring(0, sEl.indexOf('&')) + "\tnight = " + sEl.substring(sEl.indexOf('>') + 1) + "\t";
            c.add(Calendar.DATE, 1);
        }

        return data;
    }


    private String loadDocJsoup(String baseUrl, int timeout) {
        Document doc = null;
        Element element = list.poll();
        Connection.Response response = null;
        int code = 0;
        if (element != null) {
            String title = element.select("a").attr("title");
            try {
                Connection connection = Jsoup.connect(baseUrl + element.attr("href")).timeout(0);
                 response = connection.execute();
                 code = response.statusCode();
                 if(code!=200) {
                     //System.out.println(code);
                     return "";
                 }

                doc = response.parse();

                return parsDoc(doc, title);
            } catch (IOException e) {
                System.out.println("Status Code = "+response.statusCode()+"\n"+e.getMessage());;
            }
            return "TIMEOUT "+ title;
        }else return "";

    }

/*
    private Document loadDocSocket(String baseUrl, int timeout, String hostName) {
        Document doc = null;
        Element element = list.poll();

        if (element == null) {
            return doc;
        } else {
            Socket s = new Socket();
            String host = baseUrl + element.attr("href");
            PrintWriter s_out = null;
            BufferedReader s_in = null;


            try {
                s.connect(new InetSocketAddress(host, 80), timeout);
                s.setSoTimeout(timeout);
                s_out = new PrintWriter(s.getOutputStream(), true);
                s_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            s_out.print("GET /" + element.attr("href") + " HTTP/1.1\r\n");
            s_out.print("Host: "+hostName+"\r\n\r\n");
            s_out.println("Accept-Language: ru-ru");
            s_out.println();
            s_out.flush();

            String response;
            StringBuilder htmlDoc = new StringBuilder();
            try {

                for (int i = 0; i < 11; i++)
                    s_in.readLine();

                while ((response = s_in.readLine()) != null)
                    htmlDoc.append(response);


            } catch (IOException e) {
                e.printStackTrace();
            }
            doc = Jsoup.parse(htmlDoc.toString());
            return doc;
        }
    }*/
}




