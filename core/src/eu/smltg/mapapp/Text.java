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

    public static Actor createUi(Skin skin) {
        Table table = new Table();
        table.defaults().pad(0);

        latField = new TextField("latitude", skin);
        latField.setDisabled(true);
        latField.setAlignment(Align.center);

        lonField = new TextField("longitute", skin);
        lonField.setDisabled(true);
        lonField.setAlignment(Align.center);


        Table textTable = new Table();
        textTable.defaults().width(80).pad(5);

        textTable.add(latField).width(DataVisualiserMap.SCREEN_WIDHT * .25f);
        textTable.row();
        textTable.add(lonField).width(DataVisualiserMap.SCREEN_WIDHT * .25f);

        textTable.left().top();
        textTable.setTransform(true);
        textTable.setScale(.7f);
        textTable.setOrigin(0, textTable.getPrefHeight());


        table.left().top();
        table.add(textTable);
        table.setFillParent(true);
        table.pack();

        return table;
    }

    public static void updateGeolocation(Location location) {
        latField.setText(String.valueOf(location.getLat()).substring(0,9));
        lonField.setText(String.valueOf(location.getLon()).substring(0,9));
    }

}
