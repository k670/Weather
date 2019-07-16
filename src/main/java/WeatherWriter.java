import Model.TemperatureModel;
import Model.WeatherModel;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;


public class WeatherWriter extends RecursiveAction implements Runnable{
    ConcurrentLinkedQueue<org.jsoup.nodes.Element> list;
    String fileName;

    WeatherWriter(ConcurrentLinkedQueue<Element> list, String fileName) {
        this.list = list;
        this.fileName = fileName;
    }

    @Override
    protected void compute() {
        this.run();
    }

    public  void run() {
        String baseUrl = "http://weather.bigmir.net";

        WeatherModel data = loadDocJsoup(baseUrl,0);
         try {
            WorkWithFileSingleton.getInstance().writeToFile(WeatherModeltoXml(data), fileName);
         } catch (IOException e) {
            e.printStackTrace();
         }
    }

    private String WeatherModeltoXml(WeatherModel weatherModel){
        JAXBContext context = null;
        StringWriter writer = new StringWriter();
        try {
            context = JAXBContext.newInstance(WeatherModel.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(weatherModel, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }

    private ConcurrentHashMap<Date,TemperatureModel> parsDoc(Document doc) {
        ConcurrentHashMap<Date,TemperatureModel> temperatureModel = new     ConcurrentHashMap<Date,TemperatureModel>();
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(dt);

        Elements newsHeadlines = doc.select("td.text_center");
        for (int i = 0; i < newsHeadlines.size() - 2; i++) {
            String sEl = newsHeadlines.get(i).select("h2").toString().replace("<h2>", "").replace("</h2>", "").replace("</span>", "");
            temperatureModel.put(c.getTime(),new TemperatureModel(Integer.parseInt(sEl.substring(0, sEl.indexOf('&'))),Integer.parseInt(sEl.substring(sEl.indexOf('>') + 1))));
            c.add(Calendar.DATE, 1);
        }
        return temperatureModel;
    }


    private WeatherModel loadDocJsoup(String baseUrl, int timeout) {
        Document doc = null;
        Element element = list.poll();
        Connection.Response response = null;
        int code = 0;
        if (element != null) {
            String title = element.select("a").attr("title");
            try {
                Connection connection = Jsoup.connect(baseUrl + element.attr("href")).timeout(timeout);
                 response = connection.execute();
                 code = response.statusCode();
                 if(code!=200) {
                     return null;
                 }
                doc = response.parse();

                return new WeatherModel(title,parsDoc(doc));
            } catch (IOException e) {
                System.out.println("Status Code = "+response.statusCode()+"\n"+e.getMessage());;
            }
            return new WeatherModel(title, new ConcurrentHashMap<Date, TemperatureModel>());
        }else return null;

    }

}




