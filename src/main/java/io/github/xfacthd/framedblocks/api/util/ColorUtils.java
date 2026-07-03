package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.util.ARGB;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

/// Provides various helpers for handling colors.
public final class ColorUtils {
    private static final MapColor[] CLOSEST_AVG_MAP_COLORS = computeClosestAverageMapColors();

    /// {@return the average of the two provided map colors}
    ///
    /// @param colOne The first map color
    /// @param colTwo The second map color
    public static MapColor average(MapColor colOne, MapColor colTwo) {
        return CLOSEST_AVG_MAP_COLORS[avgIndex(colOne, colTwo)];
    }

    /// {@return the squared distance between the two provided RGB colors}
    ///
    /// @param colOne The first color
    /// @param colTwo The second color
    /// [Adapted from RGBBlocks](https://github.com/PlatinPython/RGBBlocks/blob/1.21.1/src/main/java/platinpython/rgbblocks/util/Color.java#L161-L166)
    public static int distanceSquare(int colOne, int colTwo) {
        int dr = ARGB.red(colTwo) - ARGB.red(colOne);
        int dg = ARGB.green(colTwo) - ARGB.green(colOne);
        int db = ARGB.blue(colTwo) - ARGB.blue(colOne);
        return dr * dr + dg * dg + db * db;
    }

    private static int avgIndex(MapColor colOne, MapColor colTwo) {
        return colOne.id << 6 | colTwo.id;
    }

    private static MapColor[] computeClosestAverageMapColors() {
        MapColor[] avgColors = new MapColor[64 * 64];
        Arrays.fill(avgColors, MapColor.NONE);
        for (int i = 1; i < 64; i++) {
            MapColor colOne = MapColor.byId(i);
            if (colOne == MapColor.NONE) continue;

            avgColors[avgIndex(colOne, colOne)] = colOne;

            for (int j = i + 1; j < 64; j++) {
                MapColor colTwo = MapColor.byId(j);
                if (colTwo == MapColor.NONE) continue;

                int avg = ARGB.average(colOne.col, colTwo.col);
                MapColor avgCol = IntStream.range(0, 64)
                        .mapToObj(MapColor::byId)
                        .filter(col -> col != MapColor.NONE)
                        .min(Comparator.comparingInt(col -> distanceSquare(col.col, avg)))
                        .orElse(MapColor.NONE);
                avgColors[avgIndex(colOne, colTwo)] = avgCol;
                avgColors[avgIndex(colTwo, colOne)] = avgCol;
            }
        }
        return avgColors;
    }

    private ColorUtils() { }
}
