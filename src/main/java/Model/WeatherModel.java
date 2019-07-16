package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@XmlRootElement
public class WeatherModel implements Serializable {
    @XmlElement(name = "place")
    String place;
    @XmlElement(name = "temperature")
    ConcurrentHashMap<Date,TemperatureModel> temperature;

    public WeatherModel(String place, ConcurrentHashMap<Date,TemperatureModel> temperature){
        this.place = place;
        this.temperature = temperature;
    }

    public WeatherModel(){
        place = "Place name";
        temperature = new ConcurrentHashMap<Date,TemperatureModel>();
    }
    /*public byte[] getBytes() {
    }*/
}
