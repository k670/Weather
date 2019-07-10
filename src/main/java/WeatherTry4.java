
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.concurrent.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WeatherTry4 {
    public static void main(String[] args) throws IOException {

        String fileName = "D:\\test.txt";
        ConcurrentLinkedQueue<Element> linkList = new ConcurrentLinkedQueue<Element>();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(320);

        try {
            Singleton.getInstance().deleteAndCreateFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {


            Document doc = Jsoup.connect("http://weather.bigmir.net/ukraine/")
                    .timeout(0).get();
            Elements newsHeadlines = doc.select("div.fl.W_col2");

            for (Element headline : newsHeadlines) {

                linkList.addAll(headline.select("li"));
            }

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }finally {

            while (linkList.size()>0) {

                executorService.scheduleAtFixedRate((new WeatherReader(linkList, fileName )),0,1000,TimeUnit.MILLISECONDS);
            }

        }

        executorService.shutdown();
    }
}