package eu.smltg.mapapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import eu.smltg.mapapp.locations.Bar;
import eu.smltg.mapapp.locations.Dorm;
import eu.smltg.mapapp.locations.Faculty;
import eu.smltg.mapapp.locations.FacultyType;
import eu.smltg.mapapp.locations.Location;
import eu.smltg.mapapp.locations.Noise;
import eu.smltg.mapapp.locations.Park;
import eu.smltg.mapapp.locations.People;
import eu.smltg.mapapp.locations.Restaurant;
import eu.smltg.mapapp.locations.Wifi;
import eu.smltg.mapapp.utils.Assets;
import eu.smltg.mapapp.utils.Geolocation;
import eu.smltg.mapapp.utils.MapRasterTiles;
import eu.smltg.mapapp.utils.PixelPosition;
import eu.smltg.mapapp.utils.ZoomXY;

public class DataVisualiserMap extends ApplicationAdapter implements GestureDetector.GestureListener {


    private Viewport viewport;
    private Stage stage;
    private Skin skin;

    Texture restaurantIcon;
    Texture wifiIcon;
    Texture barIcon;
    Texture facultyIcon;
    Texture peopleIcon;
    Texture noiseIcon;

    private AssetManager assetManager;

    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;
    private SpriteBatch batch;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;

    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile

    private final int NUM_TILES = 3;
    private final int ZOOM = 15;
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.557314, 15.637771);
    private final Geolocation MARKER_GEOLOCATION = new Geolocation(46.559070, 15.638100);
    private final int WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    private final int HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static int SCREEN_WIDHT;
    public static int SCREEN_HEIGHT;

    private Restaurant[] restaurants;
    private Faculty[] faculties;
    private Park[] parks;
    private Wifi[] wifi;
    private Dorm[] dorms;
    private Bar[] bars;
    private People[] people;
    private Noise[] noises;
    private List<Noise> markedNoises;

    private static final Logger log = new Logger(DataVisualiserMap.class.getSimpleName(), Logger.DEBUG);

    @Override
    public void create() {
        SCREEN_WIDHT = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        assetManager = new AssetManager();
        assetManager.load(Assets.Descriptors.UI);
        assetManager.finishLoading();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        viewport = new FitViewport(SCREEN_WIDHT, SCREEN_HEIGHT);
        stage = new Stage(viewport, batch);

        skin = assetManager.get(Assets.Descriptors.UI);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        camera.viewportWidth = WIDTH / 2f;
        camera.viewportHeight = HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        touchPosition = new Vector3();
        inputMultiplexer.addProcessor(new GestureDetector(this));

        stage.addActor(Buttons.createUi(skin));
        stage.addActor(Text.createUi(skin));
        inputMultiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(inputMultiplexer);

        barIcon = new Texture(Gdx.files.internal("java.png"));
        wifiIcon = new Texture(Gdx.files.internal("wifi.png"));
        restaurantIcon = new Texture(Gdx.files.internal("restaurant.png"));
        facultyIcon = new Texture(Gdx.files.internal("faculty.png"));
        peopleIcon = new Texture(Gdx.files.internal("people.png"));
        noiseIcon = new Texture(Gdx.files.internal("noise.png"));

        try {
            restaurants = Restaurant.getRestaurantsAPI();
            faculties = Faculty.getFacultyAPI();
            parks = Park.getParkAPI();
            wifi = Wifi.getWifiAPI();
            dorms = Dorm.getDormAPI();
            bars = Bar.getBarsAPI();
            people = People.getPeopleAPI();
            noises = Noise.getNoiseBlockchainAPI();
            markedNoises = new ArrayList<>();
            parseNoiseDate();
            filterNoises();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(ZOOM, centerTile.x - ((NUM_TILES - 1) / 2), centerTile.y - ((NUM_TILES - 1) / 2));

        } catch (IOException e) {
            e.printStackTrace();
        }

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(NUM_TILES, NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        setPixelPositions();
    }


    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        handleInput();

        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        drawMarkers();
        drawShapes();

        stage.draw();
    }

    // TODO drawing shapes for faculty and buildings
    private void drawShapes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        //PixelPosition marker = MapRasterTiles.getPixelPosition(MARKER_GEOLOCATION.lat, MARKER_GEOLOCATION.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.5f));
        if (Faculty.locationFilter)
            for (Faculty fa : faculties) {
                if (fa.facultyType == FacultyType.POLYGON)
                    shapeRenderer.polygon(fa.getPolygon(MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT));
            }
        shapeRenderer.end();

        shapeRenderer.setColor(new Color(1, 0, 0, 0.5f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (Park.locationFilter)
            for (Park pa : parks) {
                shapeRenderer.polygon(pa.getPolygon(MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT));
            }
        Gdx.gl.glLineWidth(3);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
        shapeRenderer.setColor(new Color(0, 0, 1, 0.5f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (Dorm.locationFilter)
            for (Dorm dorm : dorms) {
                shapeRenderer.polygon(dorm.getPolygon(MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT));
            }
        Gdx.gl.glLineWidth(3);
        shapeRenderer.end();

    }

    private void drawMarkers() {
        //PixelPosition marker = MapRasterTiles.getPixelPosition(restaurants[0].location.coordinates[1], restaurants[0].location.coordinates[0], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        //PixelPosition marker = MapRasterTiles.getPixelPosition(MARKER_GEOLOCATION.lat, MARKER_GEOLOCATION.lng, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        //Texture markerIcon = new Texture(Gdx.files.internal("ic_marker.png"));


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (Restaurant.locationFilter)
            for (Restaurant res : restaurants) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(res.location.coordinates[0], res.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                batch.draw(restaurantIcon, marker.x - 24, marker.y - 24, 48, 48);
            }

        if (Bar.locationFilter)
            for (Bar bar : bars) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(bar.location.coordinates[0], bar.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                batch.draw(barIcon, marker.x - 24, marker.y - 24, 48, 48);
            }

        if (Faculty.locationFilter)
            for (Faculty faculty : faculties) {
//                if (faculty.facultyType == FacultyType.POINT) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(faculty.lat, faculty.lon, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                batch.draw(facultyIcon, marker.x - 24, marker.y - 24, 48, 48);
//                }
            }

        if (People.locationFilter)
            for (People people : people) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(people.location.coordinates[0], people.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                batch.draw(peopleIcon, marker.x - 24, marker.y - 24, 48, 48);
            }

        if (Wifi.locationFilter)
            for (Wifi wifi : wifi) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(wifi.location.coordinates[0], wifi.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                batch.draw(wifiIcon, marker.x - 24, marker.y - 24, 48, 48);
            }
        if (Noise.locationFilter)
            for (Noise noise : markedNoises) {
                PixelPosition marker = MapRasterTiles.getPixelPosition(noise.lat, noise.lon, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
                batch.draw(noiseIcon, marker.x - noiseIcon.getWidth() / 2f, marker.y - noiseIcon.getHeight() / 2f);
            }

        batch.end();
        if (Wifi.locationFilter)
            drawWifiRange();
    }

    private void drawWifiRange() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1);
        for (Wifi wifi : wifi) {
            PixelPosition point = MapRasterTiles.getPixelPosition(wifi.location.coordinates[0], wifi.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
            shapeRenderer.circle(point.x, point.y, 25);
        }
        shapeRenderer.end();
    }

    private void setPixelPositions() {
        for (Restaurant res : restaurants)
            res.pixelPos = MapRasterTiles.getPixelPosition(res.location.coordinates[0], res.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

        for (Bar bar : bars)
            bar.pixelPos = MapRasterTiles.getPixelPosition(bar.location.coordinates[0], bar.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

        for (Faculty faculty : faculties)
            faculty.pixelPos = MapRasterTiles.getPixelPosition(faculty.lat, faculty.lon, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

        for (Wifi wifi : wifi)
            wifi.pixelPos = MapRasterTiles.getPixelPosition(wifi.location.coordinates[0], wifi.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

        for (People people : people)
            people.pixelPos = MapRasterTiles.getPixelPosition(people.location.coordinates[0], people.location.coordinates[1], MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);

        for (Noise noise : markedNoises) {
            noise.pixelPos = MapRasterTiles.getPixelPosition(noise.lat, noise.lon, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        }
    }

    public void parseNoiseDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        if(noises.length != 0) {
            for (Noise noise : noises) {
                noise.dateTime = simpleDateFormat.parse(noise.time);
            }
        }
    }

    public void filterNoises() {

        for (Noise noise : noises) {
            boolean added = false;
            if (!markedNoises.isEmpty()) {
                for (Iterator<Noise> it = markedNoises.iterator(); it.hasNext(); ) {
                    Noise markedNoise = it.next();
                    if (noisesNear(noise, markedNoise)) {
                        if (noise.dateTime.compareTo(markedNoise.dateTime) > 0) {
                            markedNoises.remove(markedNoise);
                            markedNoises.add(noise);
                            added = true;
                            break;
                        }
                    }
                }
            }
            if (!added) {
                markedNoises.add(noise);
            }
        }
    }

    public boolean noisesNear(Noise noise1, Noise noise2) {
        return Math.abs(noise1.lat - noise2.lat) <= 0.0003 && Math.abs(noise1.lon - noise2.lon) <= 0.0003;
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 tmp_unproject = camera.unproject(new Vector3(x, y, 0));
        Location tap_geolocation = MapRasterTiles.getGeolocation((int) tmp_unproject.x, (int) tmp_unproject.y, MapRasterTiles.TILE_SIZE, ZOOM, beginTile.x, beginTile.y, HEIGHT);
        Text.updateGeolocation(tap_geolocation);

        setPixelPositions();
        PixelPosition pixelPosition = new PixelPosition((int) tmp_unproject.x, (int) tmp_unproject.y);
        Text.resetDisplayName();
        if (Restaurant.locationFilter)
            for (Restaurant res : restaurants)
                if (pixelPosition.inTouchRange(res.pixelPos))
                    Text.updateDisplayName(res.name);

        if (Bar.locationFilter)
            for (Bar bar : bars)
                if (pixelPosition.inTouchRange(bar.pixelPos))
                    Text.updateDisplayName(bar.name);

        if (Faculty.locationFilter)
            for (Faculty faculty : faculties)
                if (pixelPosition.inTouchRange(faculty.pixelPos))
                    Text.updateDisplayName(faculty.display_name);

        if (Wifi.locationFilter)
            for (Wifi wifi : wifi)
                if (pixelPosition.inTouchRange(wifi.pixelPos))
                    Text.updateDisplayName(wifi.name);

        if (People.locationFilter)
            for (People people : people)
                if (pixelPosition.inTouchRange(people.pixelPos))
                    Text.updateDisplayName("Number of people: " + people.people);

        if (Noise.locationFilter) {
            for (Noise noise : markedNoises) {
                String noiseDescription;
                if (pixelPosition.inTouchRange(noise.pixelPos)) {
                    if (noise.noise <= 35.0) {
                        noiseDescription = "Exceptional";
                    } else if (noise.noise <= 45.0) {
                        noiseDescription = "Excellent";
                    } else if (noise.noise <= 50.0) {
                        noiseDescription = "Good enough";
                    } else if (noise.noise <= 60.0) {
                        noiseDescription = "Noisy";
                    } else {
                        noiseDescription = "Loud";
                    }
                    Text.updateDisplayName("Study environment: " + noiseDescription);
                }
            }
        }

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, HEIGHT - effectiveViewportHeight / 2f);
    }
}