package org.alexis;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    static TextField searchBar = new TextField();
    static final JButton searchButton = new JButton("Search");

    public SearchPanel() {
        this.setLayout(null); //sets layout to null to manually arrange components
        this.setBackground(new Color(242, 242, 242)); //light Grey
        addSearchBar();
        addSearchButton();
    }

    public void addSearchBar() {
        searchBar.setForeground(new Color(51, 51, 51)); //Dark Grey
        searchBar.setFont(new Font("Arial", Font.PLAIN, 20)); //sets the font of the text field (search bar)
        this.add(searchBar).setBounds(10,10,140,26);
    }

    public void addSearchButton() {
        searchButton.setBackground(new Color(0, 191, 255)); //Sky Blue
        this.add(searchButton).setBounds(160,10,90,26);
    }

}
