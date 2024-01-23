package org.alexis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeatherPanel extends JPanel {

    DailyForecastPanel dailyForecastPanel = new DailyForecastPanel();
    private final JLabel currentWeatherCityLabel = new JLabel();
    private final JLabel currentWeatherIcon = new JLabel();
    private final JLabel currentTemperatureLabel = new JLabel();
    private final JLabel currentDescriptionLabel = new JLabel();
    private final JLabel currentConditionLabel = new JLabel();
    private final JLabel currentSunriseLabel = new JLabel();
    private final JLabel currentSunsetLabel = new JLabel();
    private final JLabel currentHumidityLabel = new JLabel();
    private final JLabel currentMaxTempLabel = new JLabel();
    private final JLabel CurrentMinTempLabel = new JLabel();
    private final Font currentDataFont = new Font("Arial", Font.BOLD, 15); //font for humidity, sunrise, sunset, and description
    private final WeatherIconManager iconManager = new WeatherIconManager();


    public WeatherPanel() {
        this.setBackground(new Color(214, 228, 240)); //soft blue
        this.setLayout(null); //null layout to manually arrange components
        addLabelsToCurrentWeatherPanel(); //adds all the labels to the current weather panel
        setCurrentWeatherLabelTextColor();
        addSearchButtonActionListener();
        addDailyForecastPanel();
    }

    public void addCityLabel() {
        currentWeatherCityLabel.setFont(new Font("Arial", Font.BOLD, 20));
        this.add(currentWeatherCityLabel).setBounds(10, 0, 1000, 30);
    }

    public void addTemperatureLabel() {
        currentTemperatureLabel.setFont(new Font("Arial", Font.BOLD, 85));
        this.add(currentTemperatureLabel).setBounds(330, 60, 300, 300);
    }

    public void addConditionsLabel() {
        currentConditionLabel.setFont(new Font("Arial", Font.BOLD, 15));
        this.add(currentConditionLabel).setBounds(355, 230, 150, 90);
    }

    public void addDescriptionLabel() {
        currentDescriptionLabel.setFont(currentDataFont);
        this.add(currentDescriptionLabel).setBounds(15, 350, 800, 90);
    }

    public void addHumidityLabel() {
        currentHumidityLabel.setFont(currentDataFont);
        this.add(currentHumidityLabel).setBounds(600, 20, 300, 100);
    }

    public void addSunriseLabel() {
        currentSunriseLabel.setFont(currentDataFont);
        this.add(currentSunriseLabel).setBounds(600, 60, 300, 100);
    }

    public void addSunsetLabel() {
        currentSunsetLabel.setFont(currentDataFont);
        this.add(currentSunsetLabel).setBounds(600, 100, 300, 100);
    }

    public void addMinTempLabel() {
        CurrentMinTempLabel.setFont(currentDataFont);
        this.add(CurrentMinTempLabel).setBounds(600, 140, 300, 100);
    }

    public void addMaxTempLabel() {
        currentMaxTempLabel.setFont(currentDataFont);
        this.add(currentMaxTempLabel).setBounds(600, 180, 300, 100);
    }

    public void addWeatherIcon() {
        currentWeatherIcon.setBounds(15, 60, 300, 300);
        this.add(currentWeatherIcon);
    }
    public void setCurrentWeatherLabelTextColor() {
        Color color = new Color(0, 51, 102);
        for (Component c : this.getComponents()) {
            c.setForeground(color);
        }
        currentTemperatureLabel.setForeground(new Color(255, 127, 80)); //orange
        currentDescriptionLabel.setForeground(new Color(255, 127, 80));
    }
    public void addLabelsToCurrentWeatherPanel() {
        addCityLabel();
        addWeatherIcon();
        addTemperatureLabel();
        addConditionsLabel();
        addDescriptionLabel();
        addHumidityLabel();
        addSunriseLabel();
        addSunsetLabel();
        addMinTempLabel();
        addMaxTempLabel();
    }

    public void addSearchButtonActionListener() { //adds an action listener to the search button of the SearchPanel class that allows you to search for a city.
        SearchPanel.searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherData weatherData = new WeatherData(); //creates a CurrentWeatherData instance every time the search button is pressed
                setCurrentWeatherIcons(weatherData); //calls the addWeatherIcon which sets the icon.
                populateCurrentWeatherPanel(weatherData); //populates the weather panel with weather data from CurrentWeatherData instance
                dailyForecastPanel.populateDailyForecastPanel(weatherData);
            }
        });
    }

    public void setCurrentWeatherIcons(WeatherData weatherData) {
        //sets the image from an image path returned by the getFilePath method
        ImageIcon newIcon = new ImageIcon(iconManager.getFilePath(weatherData.getIconName()));
        Image resizedImage = newIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); //Image instance takes a resizedImage
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        this.currentWeatherIcon.setIcon(resizedIcon); //The resizedIcon is set to the "weatherIcon" attribute of WeatherPanel
    }

    public void populateCurrentWeatherPanel(WeatherData data) {
        //This method takes a CurrentWeatherData instance and sets each label text according to the current weather data
        currentTemperatureLabel.setText(data.getCurrentTemperature() + "°F");
        currentWeatherCityLabel.setText(data.getCityName());
        currentDescriptionLabel.setText(data.getCurrentWeatherDescription());
        currentConditionLabel.setText(data.getCurrentWeatherCondition());
        currentMaxTempLabel.setText("Max Temperature: " + data.getCurrentMaxTemp() + "°F");
        CurrentMinTempLabel.setText("Min Temperature: " + data.getCurrentMinTemp() + "°F");
        currentSunsetLabel.setText("Sunset: " + data.getCurrentSunset());
        currentSunriseLabel.setText("Sunrise: " + data.getCurrentSunrise());
        currentHumidityLabel.setText("Humidity: " + data.getCurrentHumidity() + "%");
    }

    public void addDailyForecastPanel() {
        this.add(dailyForecastPanel).setBounds(10,430,765,170);
    }

    class DailyForecastPanel extends JPanel { //inner class to add a 7-day daily forecast panel to WeatherPanel class

        JLabel[] minTempLabels = new JLabel[7];
        JLabel[] maxTempLabels = new JLabel[7];
        JLabel[] icons = new JLabel[7];
        JLabel[] daysOfTheWeekLabels = new JLabel[7];
        Font dataFont = new Font("Arial", Font.BOLD, 20);

        public DailyForecastPanel() {
            this.setBackground(new Color(255, 249, 196));
            this.setLayout(null); //null to manually arrange components
            addDaysOfTheWeekLabels();
            addIconLabels();
            addMaxTempLabels();
            addMinTempLabels();
        }

        public void addDaysOfTheWeekLabels() {
            int xCoordinate = 60;
            for (int i = 0; i < daysOfTheWeekLabels.length; i++) {
                daysOfTheWeekLabels[i] = new JLabel();
                daysOfTheWeekLabels[i].setFont(dataFont);
                daysOfTheWeekLabels[i].setForeground(new Color(101, 67, 33));
                this.add(daysOfTheWeekLabels[i]).setBounds(xCoordinate, -40, 100, 100); //ads label and set its location
                xCoordinate += 100; //increases x coordinate by 100 to leave a space between labels
            }
        }

        public void setDaysOfTheWeek(WeatherData weatherData) {
            String[] daysOfTheWeek = weatherData.getDailyForecastData().daysOfTheWeek;
            for (int i = 0; i < daysOfTheWeek.length; i++) {
                daysOfTheWeekLabels[i].setText(daysOfTheWeek[i]);
            }
        }

        public void addMaxTempLabels() {
            int xCoordinate = 60;
            for (int i = 0; i < maxTempLabels.length; i++) {
                maxTempLabels[i] = new JLabel();
                maxTempLabels[i].setFont(dataFont);
                maxTempLabels[i].setForeground(new Color(255, 165, 0));
                this.add(maxTempLabels[i]).setBounds(xCoordinate, 80, 100, 100);
                xCoordinate += 100;
            }
        }

        public void setMaxTempLabels(WeatherData weatherData) { //takes a WeatherData instance to access address of DailyForecastData instance
            int[] maxTemps = weatherData.getDailyForecastData().maxTemps; //returns integer array of DailyForecastData instance
            for (int i = 0; i < maxTemps.length; i++) {
                maxTempLabels[i].setText(maxTemps[i] + "°F"); //access each maxTempLabel and sets text to maxTemp of a particular day
            }
        }

        public void addIconLabels() {
            int xCoordinate = 20;
            for (int i = 0; i < icons.length; i++) {
                icons[i] = new JLabel();
                icons[i].setIcon(new ImageIcon("src/main/resources/condition-icons/overcast-day.png"));
                this.add(icons[i]).setBounds(xCoordinate, 20, 150, 100);
                xCoordinate += 100;
            }
        }

        public void addMinTempLabels() {
            int xCoordinate = 60;
            for (int i = 0; i < minTempLabels.length; i++) {
                minTempLabels[i] = new JLabel();
                minTempLabels[i].setFont(dataFont);
                minTempLabels[i].setForeground(new Color(255, 165, 0));
                this.add(minTempLabels[i]).setBounds(xCoordinate, 110, 100, 100);
                xCoordinate += 100;
            }
        }

        public void setMinTempLabels(WeatherData weatherData) {
            int[] minTemps = weatherData.getDailyForecastData().minTemps;
            for (int i = 0; i < minTempLabels.length; i++) {
                minTempLabels[i].setText(minTemps[i] + "°F");
            }
        }

        public void setIcons(WeatherData weatherData) {
            String[] iconNames = weatherData.getDailyForecastData().dailyForecastIconNames; //stores icon names from dailyForecastIconNames array
            for (int i = 0; i < icons.length; i++) {
                String iconPath = iconManager.getFilePath(iconNames[i]);
                ImageIcon imageIcon = new ImageIcon(iconPath);
                icons[i].setIcon(imageIcon);
            }
        }

        public void populateDailyForecastPanel(WeatherData data) { //sets the data for maxTemp, minTemps, icons, and daysOfTheWeek labels
            setDaysOfTheWeek(data);
            setMaxTempLabels(data);
            setMinTempLabels(data);
            setIcons(data);
        }
    }
}