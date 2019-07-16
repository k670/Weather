
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.concurrent.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.lang.Thread.sleep;


public class WeatherTry4 {

    private static ConcurrentLinkedQueue<Element> linkList = new ConcurrentLinkedQueue<Element>();


     boolean WeatherReadToFileExecutor(String fileName, int countThreads, int timeIntervalMilliseconds){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(countThreads);

        if(CreateFile(fileName)&&LoadLinkList(0)) {
            while (linkList.size() > 0) {

                executorService.scheduleAtFixedRate((new WeatherWriter(linkList, fileName)), 0, timeIntervalMilliseconds, TimeUnit.MILLISECONDS);
            }
        }
        executorService.shutdown();
        return true;
    }

     boolean WeatherReadToFileForkJoin(String fileName, int countThreads, int timeIntervalMilliseconds){

        ForkJoinPool pool = new ForkJoinPool(countThreads);
        if(CreateFile(fileName)&&LoadLinkList(0)) {
            while (linkList.size()>0){
                for (int j = 0; j < countThreads; j++) {

                    pool.execute((RecursiveAction) (new WeatherWriter(linkList,fileName)));
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
         try {
             pool.awaitTermination(35,TimeUnit.SECONDS);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
        return pool.isTerminated();
    }

      private boolean LoadLinkList(int timeout){
        Document doc = null;
        try {
            doc = Jsoup.connect("http://weather.bigmir.net/ukraine/").timeout(timeout).get();
            Elements newsHeadlines = doc.select("div.fl.W_col2");

            for (Element headline : newsHeadlines) {

                linkList.addAll(headline.select("li"));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean CreateFile(String fileName){
        try {
            WorkWithFileSingleton.getInstance().deleteAndCreateFile(fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}