package com.camptocamp.android.gis.providers;

//http://trac.openlayers.org/browser/trunk/openlayers/lib/OpenLayers/Util.js#L1259

import java.io.IOException;
import java.util.HashMap;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.camptocamp.android.gis.layer.ProjectedUnstreamedMap;
import com.camptocamp.android.gis.proj.CH1903;
import com.nutiteq.components.MapPos;

public class SwisstopoMap extends ProjectedUnstreamedMap {

    // private static final String TAG = Map.D + "SwisstopoMap";
    protected static final int MIN_ZOOM = 14;
    protected static final int MAX_ZOOM = 24;
    private static final int TILESIZE = 256;
    private static final String EXT = ".jpeg";
    private String baseUrl;
    private int y;
    private int x;
    private int zoom;
    private Image missingTile;
    // private final Random rand = new Random(156321);
    // private final int MIN = 5;
    // private final int MAX = 9;

    // Swisstopo data MAX_EXTEND
    protected static double MIN_X = 420000;
    protected static double MAX_X = 900000;
    protected static double MIN_Y = 30000;
    protected static double MAX_Y = 350000;

    public SwisstopoMap(String baseUrl, String copyright, final int initialZoom) {
        super(copyright, TILESIZE, MIN_ZOOM, MAX_ZOOM, new CH1903(new HashMap<Integer, Double>() {
            private static final long serialVersionUID = 1L;
            {
                put(14, 650.0);
                put(15, 500.0);
                put(16, 250.0);
                put(17, 100.0);
                put(18, 50.0);
                put(19, 20.0);
                put(20, 10.0);
                put(21, 5.0);
                put(22, 2.5);
                put(23, 2.0);
                put(24, 1.5);
                // put(25, 1.0);
                // put(26, 0.5);
            }
        }));
        this.baseUrl = baseUrl;
        this.zoom = initialZoom;
        setValues();
    }

    public String buildPath(int mapX, int mapY, int zoom) {
        x = (int) Math.ceil(mapX / TILESIZE);
        y = (int) Math.ceil((getMapHeight(zoom) - TILESIZE - mapY) / TILESIZE);
        // int r = rand.nextInt(MAX - MIN + 1) + MIN;
        int r = 5;
        return String.format(baseUrl, r, zoom, (int) (x / 1000000), (int) (x / 1000) % 1000,
                (int) (x % 1000), (int) (y / 1000000), (int) (y / 1000) % 1000, (int) (y % 1000),
                EXT);
    }

    public MapPos zoom(final MapPos middlePoint, final int zoomSteps) {
        zoom = middlePoint.getZoom();
        double xx = PIXtoCHx(middlePoint.getX());
        double yy = PIXtoCHy(middlePoint.getY());
        zoom += zoomSteps;
        setValues();
        return new MapPos((int) Math.round(CHxtoPIX(xx)), (int) Math.round(CHytoPIX(yy)), zoom);
    }

    public double getResolution(int zoom) {
        return ((CH1903) projection).getResolution(zoom);
    }

    public double PIXtoCHx(double pixel) {
        return ((CH1903) projection).PIXtoCHx(pixel, zoom);
    }

    public double PIXtoCHy(double pixel) {
        return ((CH1903) projection).PIXtoCHy(pixel, zoom);
    }

    public double CHxtoPIX(double metre) {
        return ((CH1903) projection).CHxtoPIX(metre, zoom);
    }

    public double CHytoPIX(double metre) {
        return ((CH1903) projection).CHytoPIX(metre, zoom);
    }

    @Override
    public int getMapWidth(final int zoom) {
        return (int) (Math.ceil((MAX_X - MIN_X) / getResolution(zoom) / TILESIZE) * TILESIZE);
    }

    @Override
    public int getMapHeight(final int zoom) {
        // Rounded up to TileSize
        return (int) (Math.ceil(getRealMapHeight(zoom) / TILESIZE) * TILESIZE);
    }

    public double getRealMapHeight(final int zoom) {
        return (MAX_Y - MIN_Y) / getResolution(zoom);
    }

    protected void setValues() {
        if (zoom < MIN_ZOOM || zoom > MAX_ZOOM) {
            zoom = MIN_ZOOM;
        }
        ((CH1903) projection).setyShift(getMapHeight(zoom) - getRealMapHeight(zoom));
    }

    @Override
    public Image getMissingTileImage() {
        if (missingTile == null) {
            try {
                missingTile = Image.createImage("/images/notile.png");
            }
            catch (IOException e) {
                missingTile = Image.createImage(getTileSize(), getTileSize());
                final Graphics g = missingTile.getGraphics();
                g.setColor(0xFFFF0000);
                g.fillRect(0, 0, getTileSize(), getTileSize());
            }
        }
        return missingTile;
    }

}
