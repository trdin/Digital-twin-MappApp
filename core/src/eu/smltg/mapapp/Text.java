package eu.smltg.mapapp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

import eu.smltg.mapapp.locations.Location;

public class Text {
    private static final int PX_PER_CHAR = 27;
    static TextField latField;
    static TextField lonField;
    static TextField displayNameField;

    public static Actor createUi(Skin skin) {
        Table table = new Table();
        table.defaults().pad(0);

        latField = new TextField("latitude", skin);
        latField.setDisabled(true);
        latField.setAlignment(Align.center);

        lonField = new TextField("longitute", skin);
        lonField.setDisabled(true);
        lonField.setAlignment(Align.center);

        displayNameField = new TextField("Click on an icon", skin);
        displayNameField.setDisabled(true);
        displayNameField.setAlignment(Align.center);


        Table geoTable = new Table();
        geoTable.defaults().width(80).pad(5);

        geoTable.add(latField).width(DataVisualiserMap.SCREEN_WIDHT * .25f);
        geoTable.row();
        geoTable.add(lonField).width(DataVisualiserMap.SCREEN_WIDHT * .25f);

        geoTable.left().top();
        geoTable.setTransform(true);
        geoTable.setScale(.7f);
        geoTable.setOrigin(0, geoTable.getPrefHeight());

        Table textTable = new Table();
        textTable.setTransform(true);
        textTable.defaults().width(80).pad(0);
        textTable.setOrigin(textTable.getPrefWidth(), textTable.getPrefHeight());

        textTable.right().top();

        textTable.add(displayNameField).width(DataVisualiserMap.SCREEN_WIDHT * .70f).fillY().fillX();

        table.left().top();
        table.add(geoTable).fillY().fillX();
        table.add(textTable).fillY().fillX();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    public static void updateGeolocation(Location location) {
        latField.setText(String.valueOf(location.getLat()).substring(0,9));
        lonField.setText(String.valueOf(location.getLon()).substring(0,9));
    }
    public static void updateDisplayName(String str) {
        displayNameField.setText(str.replace("š", "s").replace("č", "c").replace("ž", "z"));
    }
    public static void resetDisplayName() {
        displayNameField.setText("Click on an icon");
    }

}
