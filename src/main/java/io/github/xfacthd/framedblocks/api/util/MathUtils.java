package io.github.xfacthd.framedblocks.api.util;

import com.google.common.math.IntMath;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class MathUtils {
    public static Vec3 fraction(Vec3 vec) {
        return new Vec3(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    /**
     * Calculate how far into the block the coordinate of the given direction's axis points in the given direction
     */
    public static double fractionInDir(Vec3 vec, Direction dir) {
        double coord = switch (dir.getAxis()) {
            case X -> vec.x;
            case Y -> vec.y;
            case Z -> vec.z;
        };
        coord = coord - Math.floor(coord);
        return DirUtils.isPositive(dir) ? coord : (1D - coord);
    }

    /**
     * Check if the left hand value is lower than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is lower than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isLower(float lhs, float rhs) {
        return !Mth.equal(lhs, rhs) && lhs < rhs;
    }

    /**
     * Check if the left hand value is higher than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is higher than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isHigher(float lhs, float rhs) {
        return !Mth.equal(lhs, rhs) && lhs > rhs;
    }

    /**
     * {@return the least common multiple of the two input values}
     */
    public static long lcm(int a, int b) {
        return (long) a * (long) (b / IntMath.gcd(a, b));
    }

    /// Returns whether the bit at `index` in the provided `bitset` is set to `1`.
    ///
    /// @param bitset The bitset to read from
    /// @param index  The bit index to read
    /// @return Whether the bit is set
    public static boolean readBit(int bitset, int index) {
        return (bitset & (1 << index)) != 0;
    }

    /// Sets or clears the bit at `index` in the provided `bitset` depending on `value`.
    ///
    /// @param bitset The bitset to modify
    /// @param index  The bit index to modify
    /// @param value  Whether the bit should be set (`true`) or cleared (`false`)
    /// @return The modified bitset
    public static int writeBit(int bitset, int index, boolean value) {
        int mask = 1 << index;
        if (value) {
            bitset |= mask;
        } else {
            bitset &= ~mask;
        }
        return bitset;
    }

    public static void swap(int[] arr, int indexOne, int indexTwo) {
        int temp = arr[indexOne];
        arr[indexOne] = arr[indexTwo];
        arr[indexTwo] = temp;
    }

    private MathUtils() { }
}
