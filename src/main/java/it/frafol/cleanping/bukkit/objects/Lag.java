package it.frafol.cleanping.bukkit.objects;

public class Lag implements Runnable {

    private static int TICK_COUNT = 0;
    private static final long[] TICKS = new long[600];

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks) {
        if (TICK_COUNT < ticks) return 20.0;
        int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];
        return ticks / (elapsed / 1000.0);
    }

    public void run() {
        TICKS[TICK_COUNT % TICKS.length] = System.currentTimeMillis();
        TICK_COUNT++;
    }
}