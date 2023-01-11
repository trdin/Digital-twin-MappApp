package eu.smltg.mapapp.utils;

public class PixelPosition {
    public int x;
    public int y;

    public PixelPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static final int TOUCH_RANGE_PX = 24;

    public boolean inTouchRange(PixelPosition pixelPosition) {
        return this.x > pixelPosition.x - TOUCH_RANGE_PX && this.x < pixelPosition.x + TOUCH_RANGE_PX &&
             this.y > pixelPosition.y - TOUCH_RANGE_PX && this.y < pixelPosition.y + TOUCH_RANGE_PX;
    }
}
