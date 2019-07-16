package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;


@XmlRootElement
public class TemperatureModel implements Serializable {
    @XmlElement(name = "night")
    int night;
    @XmlElement(name = "day")
    int day;

    public TemperatureModel(){
        night = 0; day = 0;
    }

    public TemperatureModel(int day, int night){
        this.day = day;
        this.night = night;
    }
}
