package Model;

import java.io.Serializable;
import java.util.Date;

public class TemperatureModel implements Serializable {
    int night;
    int day;

    public TemperatureModel(){
        night = 0; day = 0;
    }

    public TemperatureModel(int day, int night){
        this.day = day;
        this.night = night;
    }
}
