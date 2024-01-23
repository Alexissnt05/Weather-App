package org.alexis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Calendar;
import java.util.Locale;

public class WeatherData extends WeatherApiService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode node;
    private int currentHumidity, currentTemperature;
    private int currentMaxTemp, currentMinTemp;
    private String currentWeatherDescription, currentWeatherCondition;
    private String currentSunrise, currentSunset, cityName, iconName;
    private final DailyForecastData dailyForecastData = new DailyForecastData();

    public WeatherData() {
        setJsonNode();
        populateCurrentWeatherAttributes();
    }

    public void populateCurrentWeatherAttributes() {
        setHumidity();
        setTemperature();
        setMaxTemp();
        setMinTemp();
        setWeatherCondition();
        setWeatherDescription();
        setSunrise();
        setSunset();
        setCityName();
        setIconName();
    }

    public void setJsonNode() { //
        try {
            node = objectMapper.readTree(getCurrentWeatherJsonString()); //reads the json string
        } catch (JsonProcessingException ignored) {
        }
    }

    public void setCityName() {
        this.cityName = node.get("resolvedAddress").asText();
    }

    public String getCityName() {
        return cityName;
    }

    public void setHumidity() {
        this.currentHumidity = node.get("currentConditions").get("humidity").asInt(); //travels through json node and retrieves the humidity as integer
    }

    public void setTemperature() {
        this.currentTemperature = node.get("currentConditions").get("temp").asInt(); //travels through json node and retrieves the temperature as integer
    }

    public int getCurrentHumidity() {
        return currentHumidity;
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }


    public void setMaxTemp() {
        this.currentMaxTemp = node.get("days").get(0).get("tempmax").asInt();
    }

    public int getCurrentMaxTemp() {
        return currentMaxTemp;
    }

    public void setMinTemp() {
        this.currentMinTemp = node.get("days").get(0).get("tempmin").asInt();
    }

    public int getCurrentMinTemp() {
        return currentMinTemp;
    }

    public void setWeatherDescription() {
        this.currentWeatherDescription = node.get("days").get(0).get("description").asText();
    }

    public String getCurrentWeatherDescription() {
        return currentWeatherDescription;
    }

    public void setWeatherCondition() {
        this.currentWeatherCondition = node.get("currentConditions").get("conditions").asText();
    }

    public String getCurrentWeatherCondition() {
        return currentWeatherCondition;
    }

    public void setSunrise() {
        this.currentSunrise = node.get("currentConditions").get("sunrise").asText();
    }

    public String getCurrentSunrise() {
        return currentSunrise;
    }

    public void setSunset() {
        this.currentSunset = node.get("currentConditions").get("sunset").asText();
    }

    public String getCurrentSunset() {
        return currentSunset;
    }

    public void setIconName() {
        this.iconName = node.get("currentConditions").get("icon").asText() + ".png";
    }

    public String getIconName() {
        return iconName;
    }

    public DailyForecastData getDailyForecastData() {
        return dailyForecastData;
    }

    class DailyForecastData { //inner class for 7-day forecast
        JsonNode node;
        int[] minTemps, maxTemps;
        String[] dailyForecastIconNames, daysOfTheWeek;

        public DailyForecastData() {
            setJsonString();
            setDaysOfTheWeek();
            setMinTemps();
            setMaxTemps();
            setIconNames();
        }

        public void setJsonString() {
            try {
                node = objectMapper.readTree(getDailyForecastJsonString());
            } catch (JsonProcessingException ignored) {
                System.out.println("NULL JSON");
            }
        }

        public void setDaysOfTheWeek() { //initializes the array to hold 7 elements and then initialize every index to a
            //certain day of the week
            daysOfTheWeek = new String[7];
            Calendar calendar = Calendar.getInstance(Locale.US);
            for (int i = 0; i < daysOfTheWeek.length; i++) {
                String date = node.get("days").get(i).get("datetime").asText(); //travels through the node and returns the datetime
                String[] dateArray = date.split("-"); //splits date at the - and returns a String array
                calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0])); //sets the year to the calendar
                calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1); //sets the month. Subtracted by one because January has an index of 0
                calendar.set(Calendar.DATE, Integer.parseInt(dateArray[2])); //sets the day to the calendar
                daysOfTheWeek[i] = calendar.getTime().toString().substring(0,3); //gets the first 3 characters of the string
                //and assign each to an index.
            }
        }

        public void setMinTemps() {
            minTemps = new int[7];
            for (int i = 0; i < minTemps.length; i++) {
                minTemps[i] = node.get("days").get(i).get("tempmin").asInt();
            }
        }

        public void setMaxTemps() {
            maxTemps = new int[7];
            for (int i = 0; i < maxTemps.length; i++) {
                maxTemps[i] = node.get("days").get(i).get("tempmax").asInt();
            }
        }

        public void setIconNames() {
            dailyForecastIconNames = new String[7];
            for (int i = 0; i < dailyForecastIconNames.length; i++) {
                dailyForecastIconNames[i] = node.get("days").get(i).get("icon").asText() + "128.png";
            }
        }
    }
}
