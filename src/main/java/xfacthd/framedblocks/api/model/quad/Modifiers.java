package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.lang.Math;

@SuppressWarnings("unused")
public final class Modifiers
{
    private static final QuadModifier.Modifier NOOP_MODIFIER = data -> true;
    private static final float SCALE_ROTATION_45 = 1.0F / (float) Math.cos(Math.PI / 4D) - 1.0F;
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float) Math.cos(Math.PI / 8F) - 1.0F;
    private static final Vector3f ONE = new Vector3f(1, 1, 1);
    private static final Vector3f CENTER = new Vector3f(.5F, .5F, .5F);
    private static final Vector3f BOTTOM_CENTER = new Vector3f(.5F, 0, .5F);
    private static final Vector3f TOP_CENTER = new Vector3f(.5F, 1, .5F);
    private static final float PRISM_TILT_ANGLE = (float) Math.toDegrees(Math.atan(.5D));
    private static final Vector3f[] PRISM_DIR_TO_ORIGIN_VECS = new Vector3f[]
    {
            new Vector3f(1F, 0F, 0F), //North, bottom left corner
            new Vector3f(0F, 0F, 1F), //South, bottom left corner
            new Vector3f(0F, 0F, 0F), //West,  bottom left corner
            new Vector3f(1F, 0F, 1F), //East,  bottom left corner
            new Vector3f(1F, 1F, 0F), //North, top left corner
            new Vector3f(0F, 1F, 1F), //South, top left corner
            new Vector3f(0F, 1F, 0F), //West,  top left corner
            new Vector3f(1F, 1F, 1F)  //East,  top left corner
    };

    /**
     * Cuts the quad at the edge given by the given {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cut(Direction cutDir, float length)
    {
        if (Mth.equal(length, 1F))
        {
            return NOOP_MODIFIER;
        }
        return cut(cutDir, length, length);
    }

    /**
     * Cuts the quad at the edge given by the given {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param lengthRightTop The target length of the right corner (cut direction rotated clockwise) from the starting
     *                       edge for quads with vertical direction or vertical cut directions or the target
     *                       length of the top corner for horizontal cut directions on quads with horizontal direction
     * @param lengthLeftBottom The target length of the left corner (cut direction rotated counter-clockwise) from the
     *                         starting edge for quads with vertical direction or vertical cut directions or the target
     *                         length of the bottom corner for horizontal cut directions on quads with horizontal direction
     */
    public static QuadModifier.Modifier cut(Direction cutDir, float lengthRightTop, float lengthLeftBottom)
    {
        return data -> cut(data, cutDir, lengthRightTop, lengthLeftBottom);
    }

    private static boolean cut(QuadData data, Direction cutDir, float lengthRightTop, float lengthLeftBottom)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be prependicular to the quad direction");

        if (Utils.isY(quadDir))
        {
            return cutTopBottom(data, cutDir, lengthRightTop, lengthLeftBottom);
        }
        else if (Utils.isY(cutDir))
        {
            return cutSideUpDown(data, cutDir == Direction.DOWN, lengthRightTop, lengthLeftBottom);
        }
        else
        {
            return cutSideLeftRight(data, cutDir == quadDir.getClockWise(), lengthRightTop, lengthLeftBottom);
        }
    }

    /**
     * Cuts the quad pointing upwards or downwards at the edge given by the given {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutTopBottom(Direction cutDir, float length)
    {
        if (Mth.equal(length, 1F))
        {
            return NOOP_MODIFIER;
        }
        return cutTopBottom(cutDir, length, length);
    }

    /**
     * Cuts the quad pointing upwards or downwards at the edge given by the given {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param lengthRight The target length of the right corner (cut direction rotated clockwise) from the starting edge
     * @param lengthLeft The target length of the left corner (cut direction rotated counter-clockwise) from the starting edge
     */
    public static QuadModifier.Modifier cutTopBottom(Direction cutDir, float lengthRight, float lengthLeft)
    {
        Preconditions.checkState(!Utils.isY(cutDir), "Cut direction must be horizontal");
        return data -> cutTopBottom(data, cutDir, lengthRight, lengthLeft);
    }

    private static boolean cutTopBottom(QuadData data, Direction cutDir, float lengthR, float lengthL)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(Utils.isY(quadDir), "Quad direction must be vertical");
        Preconditions.checkState(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be prependicular to the quad direction");

        boolean xAxis = Utils.isX(cutDir);
        boolean positive = Utils.isPositive(cutDir);
        boolean up = quadDir == Direction.UP;

        // Mirror targets so left and right are actually left and right of the cut direction
        if (cutDir == Direction.NORTH || (!up && cutDir == Direction.EAST) || (up && cutDir == Direction.WEST))
        {
            float temp = lengthR;
            lengthR = lengthL;
            lengthL = temp;
        }

        int idxR = xAxis ? (positive ? 2 : 1) : ((up == positive) ? 1 : 0);
        int idxL = xAxis ? (positive ? 3 : 0) : ((up == positive) ? 2 : 3);

        Direction perpDir = cutDir.getCounterClockWise();
        boolean perpX = Utils.isX(perpDir);
        float factorR = perpX ? data.pos(idxR, 0) : (up ? (1F - data.pos(idxR, 2)) : data.pos(idxR, 2));
        float factorL = perpX ? data.pos(idxL, 0) : (up ? (1F - data.pos(idxL, 2)) : data.pos(idxL, 2));

        float targetR = Mth.lerp(factorR, positive ? lengthR : 1F - lengthR, positive ? lengthL : 1F - lengthL);
        float targetL = Mth.lerp(factorL, positive ? lengthR : 1F - lengthR, positive ? lengthL : 1F - lengthL);

        int vertIdxR = xAxis ? (positive ? 1 : 3) : (up ? (positive ? 0 : 2) : (positive ? 1 : 3));
        int vertIdxL = xAxis ? (positive ? 0 : 2) : (up ? (positive ? 3 : 1) : (positive ? 2 : 0));
        int coordIdx = xAxis ? 0 : 2;

        if (positive && (Utils.isHigher(data.pos(vertIdxR, coordIdx), targetR) || Utils.isHigher(data.pos(vertIdxL, coordIdx), targetL)))
        {
            return false;
        }
        if (!positive && (Utils.isLower(data.pos(vertIdxR, coordIdx), targetR) || Utils.isLower(data.pos(vertIdxL, coordIdx), targetL)))
        {
            return false;
        }

        float xz1 = data.pos(idxR, coordIdx);
        float xz2 = data.pos(idxL, coordIdx);

        float toXZ1 = positive ? Math.min(xz1, targetR) : Math.max(xz1, targetR);
        float toXZ2 = positive ? Math.min(xz2, targetL) : Math.max(xz2, targetL);

        boolean rotated = data.uvRotated();
        boolean mirrored = data.uvMirrored();
        TextureAtlasSprite sprite = data.quad().getSprite();

        if (xAxis)
        {
            ModelUtils.remapUV(quadDir, sprite, data.pos(1, coordIdx), data.pos(2, coordIdx), toXZ1, data, 1, 2, idxR, false, false, rotated, mirrored);
            ModelUtils.remapUV(quadDir, sprite, data.pos(0, coordIdx), data.pos(3, coordIdx), toXZ2, data, 0, 3, idxL, false, false, rotated, mirrored);
        }
        else
        {
            ModelUtils.remapUV(quadDir, sprite, data.pos(1, coordIdx), data.pos(0, coordIdx), toXZ1, data, 0, 1, idxR, true, !up, rotated, mirrored);
            ModelUtils.remapUV(quadDir, sprite, data.pos(2, coordIdx), data.pos(3, coordIdx), toXZ2, data, 3, 2, idxL, true, !up, rotated, mirrored);
        }

        data.pos(idxR, coordIdx, toXZ1);
        data.pos(idxL, coordIdx, toXZ2);

        return true;
    }

    /**
     * Cuts the quad pointing horizontally at the top and bottom edge
     * @param length The target length from either starting edge
     */
    public static QuadModifier.Modifier cutSideUpDown(float length)
    {
        return data -> cutSideUpDown(data, false, length, length) && cutSideUpDown(data, true, length, length);
    }

    /**
     * Cuts the quad pointing horizontally at the top or bottom edge given by {@code downwards}
     * @param downwards Whether the starting edge should be top (true) or bottom (false)
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutSideUpDown(boolean downwards, float length)
    {
        if (Mth.equal(length, 1F))
        {
            return NOOP_MODIFIER;
        }
        return cutSideUpDown(downwards, length, length);
    }

    /**
     * Cuts the quad pointing horizontally at the top or bottom edge given by {@code downwards}
     * @param downwards Whether the starting edge should be top (true) or bottom (false)
     * @param lengthRight The target length of the right corner (cut direction rotated clockwise) from the starting edge
     * @param lengthLeft The target length of the left corner (cut direction rotated counter-clockwise) from the starting edge
     */
    public static QuadModifier.Modifier cutSideUpDown(boolean downwards, float lengthRight, float lengthLeft)
    {
        return data -> cutSideUpDown(data, downwards, lengthRight, lengthLeft);
    }

    private static boolean cutSideUpDown(QuadData data, boolean downwards, float lengthRight, float lengthLeft)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

        Direction quadDirRot = quadDir.getCounterClockWise();
        boolean x = Utils.isX(quadDirRot);
        boolean positive = Utils.isPositive(quadDirRot);

        float factorR = positive ? data.pos(0, x ? 0 : 2) : (1F - data.pos(0, x ? 0 : 2));
        float factorL = positive ? data.pos(3, x ? 0 : 2) : (1F - data.pos(3, x ? 0 : 2));

        float targetR = Mth.lerp(factorR, downwards ? 1F - lengthRight : lengthRight, downwards ? 1F - lengthLeft : lengthLeft);
        float targetL = Mth.lerp(factorL, downwards ? 1F - lengthRight : lengthRight, downwards ? 1F - lengthLeft : lengthLeft);

        if (downwards && (Utils.isLower(data.pos(0, 1), targetR) || Utils.isLower(data.pos(3, 1), targetL)))
        {
            return false;
        }
        if (!downwards && (Utils.isHigher(data.pos(1, 1), targetR) || Utils.isHigher(data.pos(2, 1), targetL)))
        {
            return false;
        }

        int idx1 = downwards ? 1 : 0;
        int idx2 = downwards ? 2 : 3;

        float y1 = data.pos(idx1, 1);
        float y2 = data.pos(idx2, 1);

        float toY1 = downwards ? Math.max(y1, targetR) : Math.min(y1, targetR);
        float toY2 = downwards ? Math.max(y2, targetL) : Math.min(y2, targetL);

        boolean rotated = data.uvRotated();
        boolean mirrored = data.uvMirrored();
        TextureAtlasSprite sprite = data.quad().getSprite();
        ModelUtils.remapUV(quadDir, sprite, data.pos(1, 1), data.pos(0, 1), toY1, data, 0, 1, idx1, true, !mirrored, rotated, mirrored);
        ModelUtils.remapUV(quadDir, sprite, data.pos(2, 1), data.pos(3, 1), toY2, data, 3, 2, idx2, true, !mirrored, rotated, mirrored);

        data.pos(idx1, 1, toY1);
        data.pos(idx2, 1, toY2);

        return true;
    }

    /**
     * Cuts the quad pointing horizontally at the edge given by {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutSideLeftRight(Direction cutDir, float length)
    {
        return cutSideLeftRight(cutDir, length, length);
    }

    /**
     * Cuts the quad pointing horizontally at the edge given by {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param lengthTop The target length of the right corner (cut direction rotated clockwise) from the starting edge
     * @param lengthBottom The target length of the left corner (cut direction rotated counter-clockwise) from the starting edge
     */
    public static QuadModifier.Modifier cutSideLeftRight(Direction cutDir, float lengthTop, float lengthBottom)
    {
        Preconditions.checkState(!Utils.isY(cutDir), "Cut direction must be horizontal");
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            return cutSideLeftRight(data, cutDir == quadDir.getClockWise(), lengthTop, lengthBottom);
        };
    }

    /**
     * Cuts the quad pointing horizontally at the left and right edge
     * @param length The target length from either starting edge
     */
    public static QuadModifier.Modifier cutSideLeftRight(float length)
    {
        return data -> cutSideLeftRight(data, false, length, length) && cutSideLeftRight(data, true, length, length);
    }

    /**
     * Cuts the quad pointing horizontally at the left or right edge given by {@code towardsRight}
     * @param towardsRight Whether the starting edge should be the left (true) or right (false) edge
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutSideLeftRight(boolean towardsRight, float length)
    {
        return cutSideLeftRight(towardsRight, length, length);
    }

    /**
     * Cuts the quad pointing horizontally at the left or right edge given by {@code towardsRight}
     * @param towardsRight Whether the starting edge should be the left (true) or right (false) edge
     * @param lengthTop The target length of the top corner
     * @param lengthBottom The target length of the bottom corner
     */
    public static QuadModifier.Modifier cutSideLeftRight(boolean towardsRight, float lengthTop, float lengthBottom)
    {
        return data -> cutSideLeftRight(data, towardsRight, lengthTop, lengthBottom);
    }

    private static boolean cutSideLeftRight(QuadData data, boolean towardsRight, float lengthTop, float lengthBot)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

        boolean positive = Utils.isPositive(towardsRight ? quadDir.getCounterClockWise() : quadDir.getClockWise());
        int coordIdx = Utils.isX(quadDir) ? 2 : 0;
        int vertIdxTop = towardsRight ? 3 : 0;
        int vertIdxBot = towardsRight ? 2 : 1;

        float targetTop = Mth.lerp(1F - data.pos(vertIdxTop, 1), positive ? 1F - lengthTop : lengthTop, positive ? 1F - lengthBot : lengthBot);
        float targetBot = Mth.lerp(1F - data.pos(vertIdxBot, 1), positive ? 1F - lengthTop : lengthTop, positive ? 1F - lengthBot : lengthBot);

        if (positive && (Utils.isLower(data.pos(vertIdxTop, coordIdx), targetTop) || Utils.isLower(data.pos(vertIdxBot, coordIdx), targetBot)))
        {
            return false;
        }
        if (!positive && (Utils.isHigher(data.pos(vertIdxTop, coordIdx), targetTop) || Utils.isHigher(data.pos(vertIdxBot, coordIdx), targetBot)))
        {
            return false;
        }

        int idx1 = towardsRight ? 0 : 3;
        int idx2 = towardsRight ? 1 : 2;

        float xz1 = data.pos(idx1, coordIdx);
        float xz2 = data.pos(idx2, coordIdx);

        float toXZ1 = positive ? Math.max(xz1, targetTop) : Math.min(xz1, targetTop);
        float toXZ2 = positive ? Math.max(xz2, targetBot) : Math.min(xz2, targetBot);

        boolean rotated = data.uvRotated();
        boolean mirrored = data.uvMirrored();
        TextureAtlasSprite sprite = data.quad().getSprite();
        ModelUtils.remapUV(quadDir, sprite, data.pos(0, coordIdx), data.pos(3, coordIdx), toXZ1, data, 0, 3, idx1, false, positive != towardsRight, rotated, mirrored);
        ModelUtils.remapUV(quadDir, sprite, data.pos(1, coordIdx), data.pos(2, coordIdx), toXZ2, data, 1, 2, idx2, false, positive != towardsRight, rotated, mirrored);

        data.pos(idx1, coordIdx, toXZ1);
        data.pos(idx2, coordIdx, toXZ2);

        return true;
    }



    /**
     * Cuts a vertical facing quad to the dimensions given by the min and max coordinates
     * @param minX Minimum X coordinate
     * @param minZ Minimum Z coordinate
     * @param maxX Maximum X coordinate
     * @param maxZ Maximum Z coordinate
     */
    public static QuadModifier.Modifier cutTopBottom(float minX, float minZ, float maxX, float maxZ)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(Utils.isY(quadDir), "Quad direction must be vertical");

            return cutTopBottom(data, Direction.WEST, 1F - minX, 1F - minX) &&
                   cutTopBottom(data, Direction.EAST, maxX, maxX) &&
                   cutTopBottom(data, Direction.NORTH, 1F - minZ, 1F - minZ) &&
                   cutTopBottom(data, Direction.SOUTH, maxZ, maxZ);
        };
    }

    /**
     * Cuts the quad pointing upwards or downwards at both edges given by the given {@code cutAxis}
     * @param cutAxis The axis of the directions towards the cut edges
     * @param length The target length from either starting edge
     */
    public static QuadModifier.Modifier cutTopBottom(Direction.Axis cutAxis, float length)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(Utils.isY(quadDir), "Quad direction must be vertical");
            Preconditions.checkState(quadDir.getAxis() != cutAxis, "Cutting axis must be perpendicular to quad axis");

            Direction posDir = Direction.fromAxisAndDirection(cutAxis, Direction.AxisDirection.POSITIVE);
            Direction negDir = Direction.fromAxisAndDirection(cutAxis, Direction.AxisDirection.NEGATIVE);

            return cutTopBottom(data, posDir, length, length) && cutTopBottom(data, negDir, length, length);
        };
    }

    /**
     * Creates a horizontal facing quad cut to the dimensions given by the min and max coordinates
     * @param minXZ Minimum X or Z coordinate, depending on the quad's facing
     * @param minY Minimum Y coordinate
     * @param maxXZ Maximum X or Z coordinate, depending on the quad's facing
     * @param maxY Maximum Y coordinate
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public static QuadModifier.Modifier cutSide(float minXZ, float minY, float maxXZ, float maxY)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

            boolean rightPositive = Utils.isPositive(quadDir.getClockWise());
            float leftXZ = rightPositive ? (1F - minXZ) : maxXZ;
            float rightXZ = rightPositive ? maxXZ : (1F - minXZ);

            return cutSideLeftRight(data, true, rightXZ, rightXZ) &&
                   cutSideLeftRight(data, false, leftXZ, leftXZ) &&
                   cutSideUpDown(data, true, 1F - minY, 1F - minY) &&
                   cutSideUpDown(data, false, maxY, maxY);
        };
    }

    /**
     * Cuts the quad pointing horizontally at the edge given by {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param lengthCW The target length of the right corner (cut direction rotated clockwise) from the starting edge
     * @param lengthCCW The target length of the left corner (cut direction rotated counter-clockwise) from the starting edge
     */
    public static QuadModifier.Modifier cutSide(Direction cutDir, float lengthCW, float lengthCCW)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");
            Preconditions.checkState(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be prependicular to the quad direction");

            if (Utils.isY(cutDir))
            {
                boolean down = cutDir == Direction.DOWN;
                float lenRight = down ? lengthCW : lengthCCW;
                float lenLeft = down ? lengthCCW : lengthCW;

                return cutSideUpDown(data, down, lenRight, lenLeft);
            }
            else
            {
                boolean right = cutDir == quadDir.getClockWise();
                float lenTop = right ? lengthCW : lengthCCW;
                float lenBottom = right ? lengthCCW : lengthCW;

                return cutSideLeftRight(data, right, lenTop, lenBottom);
            }
        };
    }

    /**
     * Cuts a triangle quad with the tip centered horizontally and pointing up or down from a horizontal quad.
     * The quad will have the right edge pushed back and the tip tilted to the top or bottom left corner
     * @param up Whether the tip should point up or down
     * @param back Whether the tip should tilt forward or backward
     */
    public static QuadModifier.Modifier cutPrismTriangle(boolean up, boolean back)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkArgument(!Utils.isY(quadDir), "Quad direction must not be on the Y axis");

            boolean leftCut = cutSideLeftRight(data, false, up ? .5F : 1, up ? 1 : .5F);
            boolean rightCut = cutSideLeftRight(data, true, up ? .5F : 1, up ? 1 : .5F);
            if (!leftCut && !rightCut)
            {
                return false;
            }

            boolean northeast = quadDir == Direction.NORTH || quadDir == Direction.EAST;

            Vector3f origin = PRISM_DIR_TO_ORIGIN_VECS[quadDir.ordinal() - 2 + (up ? 0 : 4)];
            float angle = back ? PRISM_TILT_ANGLE : -PRISM_TILT_ANGLE;
            if (northeast != up) { angle *= -1F; }
            rotate(data, quadDir.getClockWise().getAxis(), origin, angle, true);
            rotate(data, Direction.Axis.Y, origin, 45, true);

            return true;
        };
    }

    /**
     * Cuts a triangle quad with the tip centered horizontally and pointing up or down from a vertical quad.
     * The quad will have the right edge pushed back and the tip tilted to the top or bottom left corner
     * @param cutDir The direction the triangle should point in the unrotated position
     * @param back Whether the tip should tilt forward or backward
     */
    public static QuadModifier.Modifier cutPrismTriangle(Direction cutDir, boolean back)
    {
        Preconditions.checkArgument(!Utils.isY(cutDir), "Cut direction must be horizontal");
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkArgument(Utils.isY(quadDir), "Quad direction must be on the Y axis");

            boolean leftCut = cutTopBottom(data, cutDir.getCounterClockWise(), .5F, 1);
            boolean rightCut = cutTopBottom(data, cutDir.getClockWise(), 1, .5F);
            if (!leftCut && !rightCut)
            {
                return false;
            }

            boolean up = quadDir == Direction.UP;
            boolean southwest = cutDir == Direction.SOUTH || cutDir == Direction.WEST;

            Vector3f origin;
            if (back)
            {
                origin = PRISM_DIR_TO_ORIGIN_VECS[cutDir.ordinal() - 2 + (!up ? 0 : 4)];
            }
            else
            {
                offset(data, cutDir, .5F);
                origin = up ? TOP_CENTER : BOTTOM_CENTER;
            }
            float angle = up ? PRISM_TILT_ANGLE : -PRISM_TILT_ANGLE;
            angle = (up ? 90F : -90F) - angle;
            if (southwest == back) { angle *= -1F; }
            rotate(data, cutDir.getClockWise().getAxis(), origin, angle, true);

            rotate(data, Direction.Axis.Y, CENTER, 45, true);

            return true;
        };
    }

    /**
     * Cuts a triangle quad with the tip centered on the base edge and half a block above it
     * @param cutDir The direction the triangle should point
     */
    public static QuadModifier.Modifier cutSmallTriangle(Direction cutDir)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkArgument(!Utils.isY(quadDir) || !Utils.isY(cutDir), "Cut direction cannot be along the Y axis for quads pointing along the Y axis");

            if (!cut(data, cutDir, .5F, .5F))
            {
                return false;
            }

            boolean left;
            boolean right;
            if (Utils.isY(cutDir))
            {
                boolean up = cutDir == Direction.UP;
                left = cutSideLeftRight(data, false, up ? 0 : 1, up ? 1 : 0);
                right = cutSideLeftRight(data, true, up ? 0 : 1, up ? 1 : 0);
            }
            else if (Utils.isY(quadDir))
            {
                left = cutTopBottom(data, cutDir.getCounterClockWise(), 0, 1);
                right = cutTopBottom(data, cutDir.getClockWise(), 1, 0);
            }
            else
            {
                boolean cutRight = cutDir == quadDir.getClockWise();
                left = cutSideUpDown(data, false, cutRight ? 0 : 1, cutRight ? 1 : 0);
                right = cutSideUpDown(data, true, cutRight ? 0 : 1, cutRight ? 1 : 0);
            }
            return left || right;
        };
    }

    private static final Vector3f SCALE_HORIZONTAL = new Vector3f(1, 0, 1);
    private static final Vector3f[] HORIZONTAL_ORIGINS = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 1),
            new Vector3f(0, 0, 1),
            new Vector3f(1, 0, 0)
    };

    /**
     * Rotates the quad's edge given by {@code rightEdge}) backwards by the given angle and rescales the quad
     * on the appropriate axis
     * @param rightEdge Whether the right or left edge should be rotated back
     * @param angle The amount the edge should be rotated by
     */
    public static QuadModifier.Modifier makeHorizontalSlope(boolean rightEdge, float angle)
    {
        return data ->
        {
            Direction dir = data.quad().getDirection();
            if (!rightEdge)
            {
                dir = dir.getClockWise();
            }

            Vector3f origin = HORIZONTAL_ORIGINS[dir.ordinal() - 2];
            float rotAngle = rightEdge ? -angle : angle;

            rotate(data, Direction.Axis.Y, origin, rotAngle, true, SCALE_HORIZONTAL);
            return true;
        };
    }

    private static final Vector3f SCALE_VERT_X = new Vector3f(1, 1, 0);
    private static final Vector3f SCALE_VERT_Z = new Vector3f(0, 1, 1);
    private static final Vector3f[] VERTICAL_ORIGINS = new Vector3f[] {
            new Vector3f(0, 1, 0),
            new Vector3f(0, 1, 1),
            new Vector3f(0, 1, 0),
            new Vector3f(1, 1, 0),
            new Vector3f(0, 0, 0),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 0),
    };

    /**
     * Rotates the quad's edge given by {@code rightEdge}) backwards by the given angle and rescales the quad
     * on the appropriate axis
     * @param topEdge Whether the top or bottom edge should be rotated back
     * @param angle The amount the edge should be rotated by
     */
    public static QuadModifier.Modifier makeVerticalSlope(boolean topEdge, float angle)
    {
        return data ->
        {
            Direction dir = data.quad().getDirection();

            Direction.Axis axis = dir.getClockWise().getAxis();
            Vector3f origin = VERTICAL_ORIGINS[dir.ordinal() - 2 + (topEdge ? 4 : 0)];
            float rotAngle = Utils.isPositive(dir.getClockWise()) != topEdge ? -angle : angle;
            Vector3f scaleVec = Utils.isX(dir) ? SCALE_VERT_X : SCALE_VERT_Z;

            rotate(data, axis, origin, rotAngle, true, scaleVec);
            return true;
        };
    }

    /**
     * Rotates the quad's edge pointed towards by {@code edge} downwards by the given angle and rescales the quad
     * on the appropriate axis
     * @param edge The direction towards the edge that should be rotated downwards
     * @param angle The amount the edge should be rotated by
     */
    public static QuadModifier.Modifier makeVerticalSlope(Direction edge, float angle)
    {
        return data ->
        {
            Direction dir = data.quad().getDirection();
            boolean top = dir == Direction.UP;
            Preconditions.checkArgument(Utils.isY(dir), "Quad direction must be on the Y axis");
            Preconditions.checkArgument(!Utils.isY(edge), "Edge direction must be horizontal");

            Direction.Axis axis = edge.getClockWise().getAxis();
            Vector3f origin = VERTICAL_ORIGINS[edge.getOpposite().ordinal() - 2 + (top ? 0 : 4)];
            float rotAngle = Utils.isPositive(edge.getClockWise()) != top ? angle : -angle;
            Vector3f scaleVec = Utils.isX(edge) ? SCALE_VERT_X : SCALE_VERT_Z;

            rotate(data, axis, origin, rotAngle, true, scaleVec);
            return true;
        };
    }



    /**
     * Offsets the quad by the given amount in the given direction
     * @param dir The direction to offset the quad in
     * @param amount The amount the quad should be offset by
     */
    public static QuadModifier.Modifier offset(Direction dir, float amount)
    {
        if (Mth.equal(amount, 0F))
        {
            return NOOP_MODIFIER;
        }

        return data ->
        {
            offset(data, dir, amount);
            return true;
        };
    }

    private static void offset(QuadData data, Direction dir, float amount)
    {
        int idx = dir.getAxis().ordinal();
        float value = Utils.isPositive(dir) ? amount : (-1F * amount);

        for (int i = 0; i < 4; i++)
        {
            data.pos(i, idx, data.pos(i, idx) + value);
        }

    }

    /**
     * Moves the quad to the given value in the quad's facing direction
     * @param posTarget The target position in the quad's facing direction
     */
    public static QuadModifier.Modifier setPosition(float posTarget)
    {
        if (Mth.equal(posTarget, 1F))
        {
            return NOOP_MODIFIER;
        }

        return data ->
        {
            int idx = data.quad().getDirection().getAxis().ordinal();
            float value = Utils.isPositive(data.quad().getDirection()) ? posTarget : 1F - posTarget;

            for (int i = 0; i < 4; i++)
            {
                data.pos(i, idx, value);
            }

            return true;
        };
    }

    /**
     * Moves the individual vertices of the quad to the given values in the quad's facing direction. Vertices which are
     * not on the outer corners of the block face will have their position interpolated between the given target positions
     * @param posTarget The target positions in the quad's facing direction
     * @implNote This does not create the same shape for all vertices when displacing a single one, this is not fixable without extreme effort
     */
    public static QuadModifier.Modifier setPosition(float[] posTarget)
    {
        Preconditions.checkArgument(posTarget.length == 4, "Target position array must contain 4 elements!");

        return data ->
        {
            Direction dir = data.quad().getDirection();
            int idx = dir.getAxis().ordinal();
            boolean positive = Utils.isPositive(dir);
            boolean y = Utils.isY(dir);
            Direction ccwDir = y ? dir : dir.getCounterClockWise();
            boolean ccwPositive = Utils.isPositive(ccwDir);
            int lerpXIdx = y ? 0 : ccwDir.getAxis().ordinal();
            int lerpZIdx = y ? 2 : 1;
            boolean invLerpX = !y && !ccwPositive;
            boolean invLerpZ = !y || !ccwPositive;

            for (int i = 0; i < 4; i++)
            {
                float x0 = invLerpX ? (1F - data.pos(i, lerpXIdx)) : data.pos(i, lerpXIdx);
                float z0 = invLerpZ ? (1F - data.pos(i, lerpZIdx)) : data.pos(i, lerpZIdx);
                float target = (float) Mth.lerp2(x0, z0, posTarget[0], posTarget[3], posTarget[1], posTarget[2]);
                data.pos(i, idx, positive ? target : (1F - target));
            }

            return true;
        };
    }



    /**
     * Rotates the quad on the given axis around the block center
     * @param axis The axis to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Whether the quad should be rescaled or retain its dimensions
     */
    public static QuadModifier.Modifier rotateCentered(Direction.Axis axis, float angle, boolean rescale)
    {
        return rotate(axis, CENTER, angle, rescale);
    }

    /**
     * Rotates the quad on the given axis around the block center
     * @param axis The axis to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Whether the quad should be rescaled or retain its dimensions
     * @param scaleMult Modifier for the scale vector, can be used to inhibit scaling on selected axis
     */
    public static QuadModifier.Modifier rotateCentered(Direction.Axis axis, float angle, boolean rescale, Vector3f scaleMult)
    {
        return rotate(axis, CENTER, angle, rescale, scaleMult);
    }

    /**
     * Rotates the quad on the given axis around the given origin
     * @param axis The axis to rotate around
     * @param origin The point to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Whether the quad should be rescaled or retain its dimensions
     */
    public static QuadModifier.Modifier rotate(Direction.Axis axis, Vector3f origin, float angle, boolean rescale)
    {
        return data ->
        {
            rotate(data, axis, origin, angle, rescale);
            return true;
        };
    }

    private static void rotate(QuadData data, Direction.Axis axis, Vector3f origin, float angle, boolean rescale)
    {
        rotate(data, axis, origin, angle, rescale, ONE);
    }

    /**
     * Rotates the quad on the given axis around the given origin
     * @param axis The axis to rotate around
     * @param origin The point to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Whether the quad should be rescaled or retain its dimensions
     * @param scaleMult Modifier for the scale vector, can be used to inhibit scaling on selected axes
     */
    public static QuadModifier.Modifier rotate(Direction.Axis axis, Vector3f origin, float angle, boolean rescale, Vector3f scaleMult)
    {
        return data ->
        {
            rotate(data, axis, origin, angle, rescale, scaleMult);
            return true;
        };
    }

    private static void rotate(QuadData data, Direction.Axis axis, Vector3f origin, float angle, boolean rescale, Vector3f scaleMult)
    {
        Vector3f axisVec;
        Vector3f scaleVec;
        switch (axis)
        {
            case X ->
            {
                axisVec = new Vector3f(1.0F, 0.0F, 0.0F);
                scaleVec = new Vector3f(0.0F, 1.0F, 1.0F);
            }
            case Y ->
            {
                axisVec = new Vector3f(0.0F, 1.0F, 0.0F);
                scaleVec = new Vector3f(1.0F, 0.0F, 1.0F);
            }
            case Z ->
            {
                axisVec = new Vector3f(0.0F, 0.0F, 1.0F);
                scaleVec = new Vector3f(1.0F, 1.0F, 0.0F);
            }
            default -> throw new IllegalArgumentException("Invalid axis!");
        }

        float angleRad = (float) Math.toRadians(angle);
        Matrix4f transform = new Matrix4f().rotate(new AxisAngle4f(angleRad, axisVec));

        if (rescale)
        {
            float scaleAngle = Mth.abs(angle) > 45F ? (90F - Mth.abs(angle)) : Mth.abs(angle);

            if (scaleAngle == 22.5F)
            {
                scaleVec.mul(SCALE_ROTATION_22_5);
            }
            else if (scaleAngle == 45F)
            {
                scaleVec.mul(SCALE_ROTATION_45);
            }
            else
            {
                float scaleFactor = 1.0F / (float)Math.cos(Math.PI / (180D / (double)scaleAngle)) - 1.0F;
                scaleVec.mul(scaleFactor);
            }
            scaleMult.absolute();
            scaleVec.mul(scaleMult.x(), scaleMult.y(), scaleMult.z());
            scaleVec.add(1.0F, 1.0F, 1.0F);
        }

        for (int i = 0; i < 4; i++)
        {
            Vector4f vector4f = new Vector4f(data.pos(i, 0) - origin.x(), data.pos(i, 1) - origin.y(), data.pos(i, 2) - origin.z(), 1.0F);
            if (rescale)
            {
                vector4f.mul(new Vector4f(scaleVec, 1.0F));
            }
            vector4f.mul(transform);

            data.pos(i, 0, vector4f.x() + origin.x());
            data.pos(i, 1, vector4f.y() + origin.y());
            data.pos(i, 2, vector4f.z() + origin.z());
        }
    }

    public static QuadModifier.Modifier scaleFace(float factor, Vector3f origin)
    {
        return data ->
        {
            Vector3f scaleVec = switch (data.quad().getDirection().getAxis())
            {
                case X -> new Vector3f(0.0F, 1.0F, 1.0F);
                case Y -> new Vector3f(1.0F, 0.0F, 1.0F);
                case Z -> new Vector3f(1.0F, 1.0F, 0.0F);
            };

            scaleVec.mul(factor);

            for (int i = 0; i < 4; i++)
            {
                Vector4f posVec = new Vector4f(data.pos(i, 0) - origin.x(), data.pos(i, 1) - origin.y(), data.pos(i, 2) - origin.z(), 1.0F);
                posVec.mul(new Vector4f(scaleVec, 1.0F));

                data.pos(i, 0, posVec.x() + origin.x());
                data.pos(i, 1, posVec.y() + origin.y());
                data.pos(i, 2, posVec.z() + origin.z());
            }

            return true;
        };
    }

    public static QuadModifier.Modifier applyFullbright()
    {
        return applyLightmap(15, 15);
    }

    public static QuadModifier.Modifier applyLightmap(int light)
    {
        return applyLightmap(light, light);
    }

    public static QuadModifier.Modifier applyLightmap(int blockLight, int skyLight)
    {
        Preconditions.checkArgument(blockLight >= 0 && blockLight < 16, "Invalid block light value");
        Preconditions.checkArgument(skyLight >= 0 && skyLight < 16, "Invalid sky light value");
        return data ->
        {
            for (int i = 0; i < 4; i++)
            {
                data.light(i, LightTexture.pack(blockLight, skyLight));
            }
            return true;
        };
    }



    private Modifiers() {}
}
