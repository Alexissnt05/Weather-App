package sant.alexis.weather_app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class WeatherApp {
    private final CurrentWeatherData currentWeatherData;
    private final HourlyWeatherData hourlyWeatherData;
    private final DailyWeatherData dailyWeatherData;
    private final AttractionsPanel attractionsPanel;
    private final String apiKey;
    private final String tripAdvisorKey;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

    @FXML
    private TextField searchBar;
    @FXML
    private Button searchButton, refreshButton;
    @FXML
    private Label currentTemperatureLabel, currentWeatherDescription, currentHumidityLabel, currentSunsetLabel, currentSunriseLabel,
    currentWindSpeedLabel, currentUVIndex, currentLocationName;
    @FXML
    private ImageView currentWeatherIcon;
    @FXML
    private LineChart<?, ?> hourlyChart;
    private XYChart.Series series;
    private ObservableList<XYChart.Data> chartData;
    private ArrayList<String> attractionNames;
    private ArrayList<String> attractionIds;
    private ArrayList<String> attractionPhotos;
    @FXML
    private HBox daysOfTheWeek, weatherIcons, lowTemperatures, highTemperatures;
    @FXML
    private VBox attractionsVBox;



    public WeatherApp() {
        currentWeatherData = new CurrentWeatherData();
        hourlyWeatherData = new HourlyWeatherData();
        dailyWeatherData = new DailyWeatherData();
        attractionsPanel = new AttractionsPanel();
        apiKey = System.getenv("OpenWeather-apiKey");
        tripAdvisorKey = System.getenv("TripAdvisor-key");
        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
        series = new XYChart.Series();
        chartData = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize() {
        setRefreshButtonIcon();
        hourlyChart.getData().add(series);
        searchCity();
        refreshWeatherData();
        //Sets the background color of the LineChart plot
        hourlyChart.lookup(".chart-plot-background").setStyle("-fx-background-color: #fcf4d9");
    }

    public void searchCity() {
        searchButton.setOnAction(event -> {
            try {
                currentWeatherData.setCurrentWeatherData();
                hourlyWeatherData.addDataToChart();
                dailyWeatherData.getDailyWeather();
                attractionsPanel.populateArrays();
                attractionsPanel.populateAttractionsPanel();
                attractionsPanel.clearArrays();
            } catch (IOException e) {
                System.out.println("Error!");
            }
        });
    }

    //uses the Geocoding API from OpenWeather to return the coordinates (lat, lon) given the name of a location
    public String getCoordinates(String cityName) {
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&appid=" + apiKey)
                .build();
        try(Response response = client.newCall(request).execute()) {
            String jsonString = response.body().string();
            JsonNode node = objectMapper.readTree(jsonString);
            double lat = node.get(0).get("lat").asDouble();
            double lon = node.get(0).get("lon").asDouble();
            return "lat=" + lat + "&lon=" + lon;
        } catch (IOException | NullPointerException e){}
        return "Exception";
    }

    public String getCoordinates(String cityName, String limit) {
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=" + limit + "&appid=" + apiKey)
                .build();
        try(Response response = client.newCall(request).execute()) {
            String jsonString = response.body().string();
            JsonNode node = objectMapper.readTree(jsonString);
            double lat = node.get(0).get("lat").asDouble();
            double lon = node.get(0).get("lon").asDouble();
            return lat + ","+ lon;
        } catch (IOException | NullPointerException e){}
        return "IO Exception";
    }

    public void setRefreshButtonIcon() {
        String url = "https://img.icons8.com/?size=32&id=14296&format=png&color=FD7E14";
        refreshButton.setGraphic(new ImageView(url));
    }

    public void refreshWeatherData() {
        refreshButton.setOnAction(event -> {
            try {
                currentWeatherData.setCurrentWeatherData();
                hourlyWeatherData.addDataToChart();
                dailyWeatherData.getDailyWeather();
            } catch (IOException ignored) {
            }
        });
    }

    //inner class representing current weather
    class CurrentWeatherData {
        private final String DATE_FORMAT;
        private final DateTimeFormatter formatter;
        private JsonNode jsonNode;

        public CurrentWeatherData() {
            DATE_FORMAT = "hh:mm:ss a";
            formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        }

        public int getHumidity()  {
            return jsonNode.get("current").get("humidity").asInt();
        }

        public String getTemperature() throws IOException {
            jsonNode = objectMapper.readTree(getCurrentWeatherJson());
            return jsonNode.get("current").get("temp").asInt() + "°F";
        }

        public String getUvIndex()  {
            return jsonNode.get("current").get("uvi").asInt() + "";
        }
        public int getWindSpeed()  {
            return jsonNode.get("current").get("wind_speed").asInt();
        }

        public String getCurrentWeatherDescription()  {
            return jsonNode.get("current").get("weather").get(0).get("description").asText();
        }

        public String getCurrentWeatherIcon()  {
            String iconCode = jsonNode.get("current").get("weather").get(0).get("icon").asText();
            return "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
        }

        public String getCurrentWeatherJson() {
            Request request = new Request.Builder()
                    .url("https://api.openweathermap.org/data/3.0/onecall?" + getCoordinates(searchBar.getText()) + "&exclude=hourly,daily,minutely&units=imperial&appid=" + apiKey)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            } catch (IOException e) {
                return "IO Exception";
            }
        }

        public String getSunrise() throws IOException {
            jsonNode = objectMapper.readTree(getCurrentWeatherJson());
            long sunrise = jsonNode.get("current").get("sunrise").asLong(); //assigns to the variable the sunrise in unix time
            String timeZone = jsonNode.get("timezone").asText();
            ZoneId zoneId = ZoneId.of(timeZone);
            //parses the UTC time to a date with time zone
            ZonedDateTime utcDate = ZonedDateTime.parse(Instant.ofEpochSecond(sunrise).toString());
            // Converts the UTC date and time to the corresponding date and time in the given timezone,
            // formats it using the specified formatter, and returns the formatted string.
            return utcDate.withZoneSameInstant(zoneId).format(formatter);
        }

        public String getSunset() throws IOException {
            jsonNode = objectMapper.readTree(getCurrentWeatherJson());
            long sunset = jsonNode.get("current").get("sunset").asLong();
            String timeZone = jsonNode.get("timezone").asText();
            ZoneId zoneId = ZoneId.of(timeZone);
            ZonedDateTime utcDate = ZonedDateTime.parse(Instant.ofEpochSecond(sunset).toString());
            return utcDate.withZoneSameInstant(zoneId).format(formatter);
        }

        public String getLocationName() {
            return searchBar.getText().toUpperCase();
        }

        public void setCurrentWeatherData() throws IOException,NullPointerException {
            try {
                currentLocationName.setText(getLocationName());
                currentTemperatureLabel.setText(getTemperature());
                currentHumidityLabel.setText(getHumidity() + "%");
                currentWindSpeedLabel.setText(getWindSpeed() + " m/h");
                currentUVIndex.setText(getUvIndex());
                currentWeatherDescription.setText(getCurrentWeatherDescription());
                currentWeatherIcon.setImage(new Image(getCurrentWeatherIcon()));
                currentSunriseLabel.setText(getSunrise());
                currentSunsetLabel.setText(getSunset());
                setHumidityIcon();
                setSunriseIcon();
                setSunsetIcon();
                setUvIndexIcon();
                setWindSpeedIcon();
            } catch (Exception ignored) {
            }
        }

        private void setHumidityIcon() {
            currentHumidityLabel.setGraphic(new ImageView("https://img.icons8.com/?size=32&id=19395&format=png&color=000000"));
        }

        private void setSunriseIcon() {
            currentSunriseLabel.setGraphic(new ImageView("https://img.icons8.com/?size=32&id=15374&format=png&color=000000"));
        }

        private void setSunsetIcon() {
            currentSunsetLabel.setGraphic(new ImageView("https://img.icons8.com/?size=32&id=15368&format=png&color=000000"));
        }

        private void setWindSpeedIcon() {
            currentWindSpeedLabel.setGraphic(new ImageView("https://img.icons8.com/?size=32&id=pLiaaoa41R9n&format=png&color=000000"));
        }

        private void setUvIndexIcon() {
            currentUVIndex.setGraphic(new ImageView("https://img.icons8.com/?size=32&id=UUSnvHxh3KgS&format=png&color=000000"));
        }

    }

    //inner class representing hourly weather
    class HourlyWeatherData {
        private final String DATE_FORMAT;
        private JsonNode jsonNode;
        private final DateTimeFormatter formatter;

        public HourlyWeatherData() {
            DATE_FORMAT = "hh:mm: a";
            formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        }


        public void addDataToChart() throws IOException {
            try {
                chartData.clear(); //clears the ObservableList to add new data
                jsonNode = objectMapper.readTree(getHourlyWeatherJson());
                String timeZone = jsonNode.get("timezone").asText();
                ZoneId zoneId = ZoneId.of(timeZone);
                long date;
                ZonedDateTime utcDate;
                int temp;
                for (int i = 0; i < 12; i++) {
                    temp = jsonNode.get("hourly").get(i).get("temp").asInt();
                    date = jsonNode.get("hourly").get(i).get("dt").asLong();
                    utcDate = ZonedDateTime.parse(Instant.ofEpochSecond(date).toString());
                    //adds data to the series instance with a time and specific temperature
                    XYChart.Data<String, Integer> data = new XYChart.Data<>();
                    data.setXValue(utcDate.withZoneSameInstant(zoneId).format(formatter));
                    data.setYValue(temp);
                    chartData.add(data);
                    series.setData(chartData);
                }
            } catch (Exception ignored) {
            }
        }

        public String getHourlyWeatherJson() throws IOException {
            Request request = new Request.Builder()
                    .url("https://api.openweathermap.org/data/3.0/onecall?" + getCoordinates(searchBar.getText()) + "&exclude=current,daily,minutely&units=imperial&appid=" + apiKey)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

    class DailyWeatherData {
        private JsonNode jsonNode;

        private void setDaysOfTheWeek() throws IOException {
            jsonNode = objectMapper.readTree(getDailyWeatherJson());
            String timeZone = jsonNode.get("timezone").asText();
            ZoneId zoneId = ZoneId.of(timeZone);
            long date;
            ZonedDateTime utcDate;
            int index = 0;
            for (Node node : daysOfTheWeek.getChildren()) {

                if (node instanceof Label) {
                    date = jsonNode.get("daily").get(index).get("dt").asLong();
                    utcDate = ZonedDateTime.parse(Instant.ofEpochSecond(date).toString());
                    ((Label) node).setText(utcDate.withZoneSameInstant(zoneId).getDayOfWeek().toString());
                    index++;
                }
            }
        }

        private void setDailyWeatherIcon() throws IOException {
            int index = 0;
            for (Node node : weatherIcons.getChildren()) {
                if (node instanceof Label) {
                    String iconCode = jsonNode.get("daily").get(index).get("weather").get(0).get("icon").asText();
                    String url = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    ((Label) node).setGraphic(new ImageView(url));
                    index++;
                }
            }
        }

        private void setHighTemperatures() {
            int index = 0;
            for (Node node : highTemperatures.getChildren()) {
                if (node instanceof Label) {
                    int highTemp = jsonNode.get("daily").get(index).get("temp").get("max").asInt();
                    ((Label)node).setText(highTemp + "°");
                    index++;
                }
            }
        }

        private void setLowTemperatures() {
            int index = 0;
            for (Node node : lowTemperatures.getChildren()) {
                if (node instanceof Label) {
                    int minTemp = jsonNode.get("daily").get(index).get("temp").get("min").asInt();
                    ((Label)node).setText(minTemp + "°");
                    index++;
                }
            }
        }

        public void getDailyWeather() throws IOException {
            try {
                setDaysOfTheWeek();
                setDailyWeatherIcon();
                setHighTemperatures();
                setLowTemperatures();
            } catch (Exception ignored) {
            }

        }


        public String getDailyWeatherJson() throws IOException {
            Request request = new Request.Builder()
                    .url("https://api.openweathermap.org/data/3.0/onecall?" + getCoordinates(searchBar.getText()) + "&exclude=hourly,current,minutely&units=imperial&appid=" + apiKey)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }


    class AttractionsPanel {
        private JsonNode jsonNode;

        public AttractionsPanel() {
            attractionIds = new ArrayList<>();
            attractionNames = new ArrayList<>();
            attractionPhotos = new ArrayList<>();
        }

        //returns the Json String representation of nearby attractions
        private String getSearchNearbyJson() throws IOException {
            Request request = new Request.Builder()
                    .url("https://api.content.tripadvisor.com/api/v1/location/nearby_search?latLong=" + getCoordinates(searchBar.getText(), "1") + "&key=" + tripAdvisorKey + "&category=attractions&language=en")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        //returns the Json String representation of the location photos
        private String getLocationPhotoJson(String locationID) throws IOException {
            Request request = new Request.Builder()
                    .url("https://api.content.tripadvisor.com/api/v1/location/" + locationID + "/photos?key=" + tripAdvisorKey + "&language=en&limit=1")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        private void addLocationIds() throws IOException {
            jsonNode = objectMapper.readTree(getSearchNearbyJson());
            for (int i = 0; i < 10; i++) {
                attractionIds.add(jsonNode.get("data").get(i).get("location_id").asText());
            }
        }

        private void addLocationNames() {
            for (int i = 0; i < 10; i++) {
                attractionNames.add(jsonNode.get("data").get(i).get("name").asText());
            }
        }

        //populates the AttractionPhotos array with several location photos from TripAdvisor users
        private void addLocationPhoto() throws IOException {
            for (int i = 0; i < 10; i++) {
                jsonNode = objectMapper.readTree(getLocationPhotoJson(attractionIds.get(i)));
                try {
                    String photoUrl = jsonNode.get("data").get(0).get("images").get("medium").get("url").asText();
                    attractionPhotos.add(photoUrl);
                } catch (NullPointerException e) {
                    //adds the string "No Picture" to the array if the location does not have one
                    attractionPhotos.add("No Picture");
                }
            }

        }

        public void populateArrays() throws IOException {
            try {
                addLocationIds();
                addLocationNames();
                addLocationPhoto();
            } catch (Exception ignored) {
            }
        }

        public void populateAttractionsPanel() {
            clearAttractionLabels();
            int index = 0;
            for (Node n : attractionsVBox.getChildren()) {
                if (n instanceof Label) {
                    try {
                        ((Label) n).setGraphic(new ImageView(attractionPhotos.get(index)));
                        ((Label) n).setText(attractionNames.get(index));
                    } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {}
                }
                index++;
            }
        }

        public void clearArrays() {
            attractionIds.clear();
            attractionNames.clear();
            attractionPhotos.clear();
        }

        private void clearAttractionLabels() {
            for (Node node : attractionsVBox.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setText("");
                    ((Label) node).setGraphic(null);
                }
            }
        }
    }
}
