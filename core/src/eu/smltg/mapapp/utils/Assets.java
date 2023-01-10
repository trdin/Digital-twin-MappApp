package eu.smltg.mapapp.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    public static class Paths {
        public static final String UI_PATH = "ui/craftacular-ui.json";
    }

    public static class Descriptors {
        public static final AssetDescriptor<Skin> UI =
                new AssetDescriptor<>(Paths.UI_PATH, Skin.class);
    }
}
