package org.alexis;

import javax.swing.*;

public class App extends JFrame {

    private final SearchPanel searchPanel = new SearchPanel();
    private final WeatherPanel weatherPanel = new WeatherPanel();
    public App() {
        createWindow();
        addSearchPanel();
        addWeatherPanel();
    }

    public void createWindow() {
        this.setTitle("Weather");
        this.setSize(800,700);
        this.setResizable(false);
        this.setIconImage(new ImageIcon("src/main/resources/App-icon.png").getImage());
        this.setLayout(null); //null to manually arrange components
        this.setLocationRelativeTo(null); //centers the frame.
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); //stops running the program when windows is closed
        this.setVisible(true);
    }

    public void addSearchPanel() {
        this.getContentPane().add(searchPanel).setBounds(0,0,1000,50);
    }

    public void addWeatherPanel() {
        this.getContentPane().add(weatherPanel).setBounds(0,50,1000,650);
    }

}
