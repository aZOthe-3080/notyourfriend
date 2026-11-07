package net.azothe.notyourfriend;

public class Util {
    public static boolean pointInRegion(double px, double py, double rx1, double ry1, double rx2, double ry2) {
        return px >= Math.min(rx1, rx2) &&
                px <= Math.max(rx1, rx2) &&
                py >= Math.min(ry1, ry2) &&
                py <= Math.max(ry1, ry2);
    }
}
