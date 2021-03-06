package com.camptocamp.android.utils;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.camptocamp.android.gis.BaseMap;
import com.nutiteq.utils.IOUtils;

public class Downloader {

    public static final String TAG = BaseMap.D + "Downloader";
    public static final String USERAGENT_KEY = "User-Agent";
    public static final String USERAGENT = "Android GIS (http://camptocamp.com)";

    public static String getStringResponse(String url) throws IOException {
        String sb;
        HttpConnection conn = null;
        try {
            conn = (HttpConnection) Connector.open(url, Connector.READ);
            conn.setRequestProperty(USERAGENT_KEY, USERAGENT);
            sb = new String(IOUtils.readFullyAndClose(conn.openInputStream()));
        }
        finally {
            IOUtils.closeConnection(conn);
        }
        return sb;
    }
}
