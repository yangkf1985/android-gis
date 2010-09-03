package com.camptocamp.android.gis;

import android.util.Log;

import com.nutiteq.maps.UnstreamedMap;

public class SwisstopoMap extends CH1903 implements UnstreamedMap {

    private static final String TAG = Map.D + "SwisstopoMap";
    private String baseUrl;
    private String format;
    private int tileSize;

    public SwisstopoMap(String baseUrl, String format, int tileSize, int minZoom, int maxZoom,
            String copyright, final int initialZoom) {
        super(copyright, tileSize, minZoom, maxZoom, initialZoom);
        this.baseUrl = baseUrl;
        this.format = format;
        this.tileSize = tileSize;
    }

    public String buildPath(int mapX, int mapY, int zoom) {
        Log.v(TAG, "mapX=" + mapX + ", mapY=" + mapY);
        int x = (int) Math.floor((mapX - ch_pixel_x) / tileSize);
        int y = (int) Math.floor((-mapY - ch_pixel_y) / tileSize);
        // Log.v(TAG, "x=" + x + ", y=" + y);
        return String.format(baseUrl, zoom, 0, 0, x, 0, 0, y, format);
        // return String.format(baseUrl, zoom, (int) (x / 1000000), (int) (x /
        // 1000) % 1000,
        // (int) (x % 1000), (int) (y / 1000000), (int) (y / 1000) % 1000, (int)
        // (y % 1000),
        // format);
    }
}
