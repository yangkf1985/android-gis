package com.camptocamp.android.gis;

import android.os.Handler;
import android.os.Message;
import android.view.animation.DecelerateInterpolator;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.WgsPoint;

public class C2CMapComponent extends BasicMapComponent {

    // private static final String TAG = Map.D + "MyMapComponent";
    private static final String KEY = "182be0c5cdcd5072bb1864cdee4d3d6e4c593f89365962.70956542";
    private static final int MOVE = 0;
    private static final long DELAY = 24; // ms
    private static final long DOUBLETAP_DELTA = 500; // ms
    private static final int DOUBLETAP_RADIUS = 50; // px
    private static final int DOUBLETAP_PAN = 2; // px

    private int lastposx = 0;
    private int lastposy = 0;
    private int[] lastpanx = new int[2];
    private int[] lastpany = new int[2];
    private long lasttouch;

    public C2CMapComponent(WgsPoint middlePoint, int zoom) {
        super(KEY, Map.VDR, Map.APP, 1, 1, middlePoint, zoom);
    }

    @Override
    public void panMap(int panX, int panY) {
        int zoom = getZoom();
        MapPos pos = getInternalMiddlePoint();

        // Don't pan outside of map size
        if ((pos.getX() > displayedMap.getMapWidth(zoom) && panX > 0)
                || (pos.getX() < 0 && panX < 0)) {
            panX = 0;
        }
        if ((pos.getY() > displayedMap.getMapHeight(zoom) && panY > 0)
                || (pos.getY() < 0 && panY < 0)) {
            panY = 0;
        }

        // Save last two pan value
        lastpanx[1] = lastpanx[0];
        lastpany[1] = lastpany[0];
        lastpanx[0] = panX;
        lastpany[0] = panY;

        super.panMap(panX, panY);
    }

    @Override
    public void pointerPressed(final int x, final int y) {
        super.pointerPressed(x, y);
        // Reset Easing
        mHandler.removeMessages(MOVE);
        posx = 0;
        posy = 0;
        current = 0f;
    }

    @Override
    public void pointerReleased(final int x, final int y) {
        super.pointerReleased(x, y);

        int panx = lastpanx[0] + lastpanx[1];
        int pany = lastpany[0] + lastpany[1];

        // Initiate Easing
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MOVE, panx, pany), DELAY);

        // Double Tap ZoomIn
        long now = System.currentTimeMillis();
        if (now - lasttouch <= DOUBLETAP_DELTA && Math.abs(lastposx - x) < DOUBLETAP_RADIUS
                && Math.abs(lastposy - y) < DOUBLETAP_RADIUS && Math.abs(panx) < DOUBLETAP_PAN
                && Math.abs(pany) < DOUBLETAP_PAN) {
            panMap(x - (getWidth() / 2), y - (getHeight() / 2));
            zoomIn();
        }
        lasttouch = now;
        lastposx = x;
        lastposy = y;
    }

    // Easing Handler and variables
    DecelerateInterpolator di = new DecelerateInterpolator(1.0f);
    int posx = 0;
    int posy = 0;
    float ip = 0f;
    float current = 0f;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (current <= 1.0f) {
                ip = di.getInterpolation(current);
                int x = (int) Math.floor(((ip * msg.arg1) - posx));
                int y = (int) Math.floor(((ip * msg.arg2) - posy));
                panMap(x, y);
                posx += x;
                posy += y;
                current += 0.1;
                mHandler
                        .sendMessageDelayed(mHandler.obtainMessage(MOVE, msg.arg1, msg.arg2), DELAY);
            }
        }
    };

}
