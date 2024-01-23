package org.alexis;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherApiService {
    private String currentWeatherEndPoint;
    private String dailyForecastEndPoint;
    private final String apiKey = System.getenv("WEATHER-API-KEY");
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private String currentWeatherJsonString;
    private String dailyForecastJsonString;

    public WeatherApiService() {
        setCurrentWeatherEndPoint();
        setDailyForecastEndPoint();
        setCurrentWeatherJsonString();
        setDailyForecastJsonString();
    }

    public void setCurrentWeatherEndPoint() {
        currentWeatherEndPoint = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + SearchPanel.searchBar.getText() +  "/today?unitGroup=us&elements=datetime%2CdatetimeEpoch%2Cname%2Caddress%2CresolvedAddress%2Clatitude%2Clongitude%2Ctempmax%2Ctempmin%2Ctemp%2Cfeelslike%2Chumidity%2Cprecip%2Cprecipprob%2Cwindspeed%2Cwinddir%2Cvisibility%2Cuvindex%2Csevererisk%2Csunrise%2Csunset%2Cmoonphase%2Cconditions%2Cdescription%2Cicon&include=current%2Cfcst&key=" + apiKey + "&contentType=json";
    }
    public void setDailyForecastEndPoint() {
        dailyForecastEndPoint = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + SearchPanel.searchBar.getText() + "?unitGroup=us&elements=datetime%2Ctempmax%2Ctempmin%2Cconditions%2Cicon&include=fcst%2Cdays&key=" + apiKey + "&contentType=json";
    }

    public void setDailyForecastJsonString() {
        Request currentWeatherHttpRequest = new Request.Builder()
                .url(dailyForecastEndPoint)
                .build();
        try {
            Response DailyForecastHttpResponse = okHttpClient.newCall(currentWeatherHttpRequest).execute();
            dailyForecastJsonString = DailyForecastHttpResponse.body().string();
        } catch (IOException | NullPointerException ignored) {
        }
    }
    public void setCurrentWeatherJsonString() {
        Request currentWeatherHttpRequest = new Request.Builder()
                .url(currentWeatherEndPoint)
                .build();
        try {
            Response currentWeatherHttpResponse = okHttpClient.newCall(currentWeatherHttpRequest).execute();
            currentWeatherJsonString = currentWeatherHttpResponse.body().string();
        } catch (IOException | NullPointerException ignored) {
        }
    }
    public String getCurrentWeatherJsonString() {
        return currentWeatherJsonString;
    }

    public String getDailyForecastJsonString() {
        return dailyForecastJsonString;
    }

}
