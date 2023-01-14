package eu.smltg.mapapp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import eu.smltg.mapapp.locations.Bar;
import eu.smltg.mapapp.locations.Dorm;
import eu.smltg.mapapp.locations.Faculty;
import eu.smltg.mapapp.locations.Park;
import eu.smltg.mapapp.locations.People;
import eu.smltg.mapapp.locations.Restaurant;
import eu.smltg.mapapp.locations.Wifi;

public class Buttons {
    private static final int PX_PER_CHAR = 27;
    private static Table buttonTable;

    public static Actor createUi(Skin skin) {
        Table table = new Table();
        table.defaults().pad(0);

        TextButton barButton = newUiButton("Bars", new Runnable() {
            public void run() {
                Bar.locationFilter = !Bar.locationFilter;
            }
        }, skin);

        TextButton restaurantButton = newUiButton("Restaurants", new Runnable() {
            public void run() {
                Restaurant.locationFilter = !Restaurant.locationFilter;
            }
        }, skin);

        TextButton wifiButton = newUiButton("Wi-Fi", new Runnable() {
            public void run() {
                Wifi.locationFilter = !Wifi.locationFilter;
            }
        }, skin);

        TextButton dormButton = newUiButton("Dorms", new Runnable() {
            public void run() {
                Dorm.locationFilter = !Dorm.locationFilter;
            }
        }, skin);

        TextButton facultyButton = newUiButton("Faculties", new Runnable() {
            public void run() {
                Faculty.locationFilter = !Faculty.locationFilter;
            }
        }, skin);

        TextButton parkButton = newUiButton("Parks", new Runnable() {
            public void run() {
                Park.locationFilter = !Park.locationFilter;
            }
        }, skin);
        TextButton peopleButton = newUiButton("People", new Runnable() {
            public void run() {
                People.locationFilter = !People.locationFilter;
            }
        }, skin);

        buttonTable = new Table();
        buttonTable.defaults().width(80).pad(5);

        addUiBtnToMainTable(barButton);
        addUiBtnToMainTable(wifiButton);
        addUiBtnToMainTable(facultyButton);
        buttonTable.row();
        addUiBtnToMainTable(dormButton);
        addUiBtnToMainTable(parkButton);
        addUiBtnToMainTable(restaurantButton);
        addUiBtnToMainTable(peopleButton);

        buttonTable.center();

        table.add(buttonTable);
        table.left().bottom();
        table.setFillParent(true);
        table.pack();

        return table;

    }

    private static TextButton newUiButton(String text, final Runnable onClickEvent, Skin skin) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClickEvent.run();
            }
        });
        return button;
    }

    private static void addUiBtnToMainTable(TextButton button) {
        final int buttonWidth = button.getText().length() * PX_PER_CHAR;
        buttonTable.add(button).width(buttonWidth);
    }

}
