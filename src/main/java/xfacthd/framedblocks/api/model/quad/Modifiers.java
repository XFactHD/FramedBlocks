package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import com.mojang.math.*;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.ModelUtils;

@SuppressWarnings("unused")
public final class Modifiers
{
    private static final QuadModifier.Modifier NOOP_MODIFIER = data -> true;
    private static final float SCALE_ROTATION_45 = 1.0F / (float)Math.cos(Math.PI / 4D) - 1.0F;
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(Math.PI / 8F) - 1.0F;
    private static final Vector3f ONE = new Vector3f(1, 1, 1);
    private static final Vector3f CENTER = new Vector3f(.5F, .5F, .5F);
    private static final float PRISM_TILT_ANGLE = (float)Math.toDegrees(Math.atan(.5D));
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
        if (Mth.equal(length, 1F)) { return NOOP_MODIFIER; }
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

    private static boolean cut(QuadModifier.Data data, Direction cutDir, float lengthRightTop, float lengthLeftBottom)
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
        if (Mth.equal(length, 1F)) { return NOOP_MODIFIER; }
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

    private static boolean cutTopBottom(QuadModifier.Data data, Direction cutDir, float lengthR, float lengthL)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(Utils.isY(quadDir), "Quad direction must be vertical");
        Preconditions.checkState(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be prependicular to the quad direction");

        boolean xAxis = Utils.isX(cutDir);
        boolean positive = Utils.isPositive(cutDir);
        boolean up = quadDir == Direction.UP;

        int idxR = xAxis ? (positive ? 2 : 1) : ((up == positive) ? 1 : 0);
        int idxL = xAxis ? (positive ? 3 : 0) : ((up == positive) ? 2 : 3);

        Direction perpDir = cutDir.getCounterClockWise();
        boolean perpX = Utils.isX(perpDir);
        float[][] pos = data.pos();
        float factorR = perpX ? pos[idxR][0] : (up ? (1F - pos[idxR][2]) : pos[idxR][2]);
        float factorL = perpX ? pos[idxL][0] : (up ? (1F - pos[idxL][2]) : pos[idxL][2]);

        float targetR = Mth.lerp(factorR, positive ? lengthR : 1F - lengthR, positive ? lengthL : 1F - lengthL);
        float targetL = Mth.lerp(factorL, positive ? lengthR : 1F - lengthR, positive ? lengthL : 1F - lengthL);

        int vertIdxR = xAxis ? (positive ? 1 : 3) : (up ? (positive ? 0 : 2) : (positive ? 1 : 3));
        int vertIdxL = xAxis ? (positive ? 0 : 2) : (up ? (positive ? 3 : 1) : (positive ? 2 : 0));
        int coordIdx = xAxis ? 0 : 2;

        if (positive && (pos[vertIdxR][coordIdx] > targetR || pos[vertIdxL][coordIdx] > targetL)) { return false; }
        if (!positive && (pos[vertIdxR][coordIdx] < targetR || pos[vertIdxL][coordIdx] < targetL)) { return false; }

        float xz1 = pos[idxR][coordIdx];
        float xz2 = pos[idxL][coordIdx];

        float toXZ1 = positive ? Math.min(xz1, targetR) : Math.max(xz1, targetR);
        float toXZ2 = positive ? Math.min(xz2, targetL) : Math.max(xz2, targetL);

        float[][] uv = data.uv();
        boolean rotated = ModelUtils.isQuadRotated(uv);
        boolean mirrored = ModelUtils.isQuadMirrored(uv);

        if (xAxis)
        {
            ModelUtils.remapUV(quadDir, pos[1][coordIdx], pos[2][coordIdx], toXZ1, uv, 1, 2, idxR, false, false, rotated, mirrored);
            ModelUtils.remapUV(quadDir, pos[0][coordIdx], pos[3][coordIdx], toXZ2, uv, 0, 3, idxL, false, false, rotated, mirrored);
        }
        else
        {
            ModelUtils.remapUV(quadDir, pos[1][coordIdx], pos[0][coordIdx], toXZ1, uv, 0, 1, idxR, true, !up, rotated, mirrored);
            ModelUtils.remapUV(quadDir, pos[2][coordIdx], pos[3][coordIdx], toXZ2, uv, 3, 2, idxL, true, !up, rotated, mirrored);
        }

        pos[idxR][coordIdx] = toXZ1;
        pos[idxL][coordIdx] = toXZ2;

        return true;
    }

    /**
     * Cuts the quad pointing horizontally at the top and botom edge
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
        if (Mth.equal(length, 1F)) { return NOOP_MODIFIER; }
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

    private static boolean cutSideUpDown(QuadModifier.Data data, boolean downwards, float lengthRight, float lengthLeft)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

        Direction quadDirRot = quadDir.getCounterClockWise();
        boolean x = Utils.isX(quadDirRot);
        boolean positive = Utils.isPositive(quadDirRot);

        float[][] pos = data.pos();

        float factorR = positive ? pos[0][x ? 0 : 2] : (1F - pos[0][x ? 0 : 2]);
        float factorL = positive ? pos[3][x ? 0 : 2] : (1F - pos[3][x ? 0 : 2]);

        float targetR = Mth.lerp(factorR, downwards ? 1F - lengthRight : lengthRight, downwards ? 1F - lengthLeft : lengthLeft);
        float targetL = Mth.lerp(factorL, downwards ? 1F - lengthRight : lengthRight, downwards ? 1F - lengthLeft : lengthLeft);

        if (downwards && (pos[0][1] < targetR || pos[3][1] < targetL)) { return false; }
        if (!downwards && (pos[1][1] > targetR && pos[2][1] > targetL)) { return false; }

        int idx1 = downwards ? 1 : 0;
        int idx2 = downwards ? 2 : 3;

        float y1 = pos[idx1][1];
        float y2 = pos[idx2][1];

        float toY1 = downwards ? Math.max(y1, targetR) : Math.min(y1, targetR);
        float toY2 = downwards ? Math.max(y2, targetL) : Math.min(y2, targetL);

        float[][] uv = data.uv();
        boolean rotated = ModelUtils.isQuadRotated(uv);
        boolean mirrored = ModelUtils.isQuadMirrored(uv);
        ModelUtils.remapUV(quadDir, pos[1][1], pos[0][1], toY1, uv, 0, 1, idx1, true, true, rotated, mirrored);
        ModelUtils.remapUV(quadDir, pos[2][1], pos[3][1], toY2, uv, 3, 2, idx2, true, true, rotated, mirrored);

        pos[idx1][1] = toY1;
        pos[idx2][1] = toY2;

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

    private static boolean cutSideLeftRight(QuadModifier.Data data, boolean towardsRight, float lengthTop, float lengthBot)
    {
        Direction quadDir = data.quad().getDirection();
        Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

        boolean positive = Utils.isPositive(towardsRight ? quadDir.getCounterClockWise() : quadDir.getClockWise());
        int coordIdx = Utils.isX(quadDir) ? 2 : 0;
        int vertIdxTop = towardsRight ? 3 : 0;
        int vertIdxBot = towardsRight ? 2 : 1;

        float[][] pos = data.pos();

        float targetTop = Mth.lerp(1F - pos[vertIdxTop][1], positive ? 1F - lengthTop : lengthTop, positive ? 1F - lengthBot : lengthBot);
        float targetBot = Mth.lerp(1F - pos[vertIdxBot][1], positive ? 1F - lengthTop : lengthTop, positive ? 1F - lengthBot : lengthBot);

        if (positive && (pos[vertIdxTop][coordIdx] < targetTop || pos[vertIdxBot][coordIdx] < targetBot)) { return false; }
        if (!positive && (pos[vertIdxTop][coordIdx] > targetTop || pos[vertIdxBot][coordIdx] > targetBot)) { return false; }

        int idx1 = towardsRight ? 0 : 3;
        int idx2 = towardsRight ? 1 : 2;

        float xz1 = pos[idx1][coordIdx];
        float xz2 = pos[idx2][coordIdx];

        float toXZ1 = positive ? Math.max(xz1, targetTop) : Math.min(xz1, targetTop);
        float toXZ2 = positive ? Math.max(xz2, targetBot) : Math.min(xz2, targetBot);

        float[][] uv = data.uv();
        boolean rotated = ModelUtils.isQuadRotated(uv);
        boolean mirrored = ModelUtils.isQuadMirrored(uv);
        ModelUtils.remapUV(quadDir, pos[0][coordIdx], pos[3][coordIdx], toXZ1, uv, 0, 3, idx1, false, positive != towardsRight, rotated, mirrored);
        ModelUtils.remapUV(quadDir, pos[1][coordIdx], pos[2][coordIdx], toXZ2, uv, 1, 2, idx2, false, positive != towardsRight, rotated, mirrored);

        pos[idx1][coordIdx] = toXZ1;
        pos[idx2][coordIdx] = toXZ2;

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
     * Cuts a triangle quad with the tip centered horizontally and pointing up or down.
     * The quad will have the right edge pushed back and the tip tilted to the top or bottom left corner
     * @param up Wether the tip should point up or down
     * @param back Wether the tip should tilt forward or backward
     */
    public static QuadModifier.Modifier cutPrismTriangle(boolean up, boolean back)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();
            float[][] pos = data.pos();
            float[][] uv = data.uv();

            int coord = Utils.isX(quadDir) ? 2 : 0;
            int checkVert1 = up ? 2 : 3;
            int checkVert2 = up ? 1 : 0;

            boolean vertPos = Utils.isPositive(quadDir.getCounterClockWise());
            float h1 = (up ? pos[checkVert1][1] : 1F - pos[checkVert1][1]) / 2F;
            float h2 = 1F - ((up ? pos[checkVert2][1] : 1F - pos[checkVert2][1]) / 2F);
            float xz1 = vertPos ? pos[checkVert1][coord] : 1F - pos[checkVert1][coord];
            float xz2 = vertPos ? pos[checkVert2][coord] : 1F - pos[checkVert2][coord];

            if (xz1 < h1 || xz2 > h2) { return false; }

            int idxTip1 = up ? 0 : 1;
            int idxTip2 = up ? 3 : 2;
            int idxBase1 = up ? 1 : 0;
            int idxBase2 = up ? 2 : 3;

            float yTip1 =  (up ? pos[idxTip1][1] :  1F - pos[idxTip1][1]) / 2F;
            float yTip2 =  (up ? pos[idxTip2][1] :  1F - pos[idxTip2][1]) / 2F;
            float yBase1 = (up ? pos[idxBase1][1] : 1F - pos[idxBase1][1]) / 2F;
            float yBase2 = (up ? pos[idxBase2][1] : 1F - pos[idxBase2][1]) / 2F;

            boolean northeast = quadDir == Direction.NORTH || quadDir == Direction.EAST;
            if (northeast)
            {
                yTip1 = 1F - yTip1;
                yBase1 = 1F - yBase1;
            }
            else
            {
                yTip2 = 1F - yTip2;
                yBase2 = 1F - yBase2;
            }

            yTip1 =  northeast ? Math.min(yTip1,  pos[idxTip1 ][coord]) : Math.max(yTip1,  pos[idxTip1 ][coord]);
            yTip2 =  northeast ? Math.max(yTip2,  pos[idxTip2 ][coord]) : Math.min(yTip2,  pos[idxTip2 ][coord]);
            yBase1 = northeast ? Math.min(yBase1, pos[idxBase1][coord]) : Math.max(yBase1, pos[idxBase1][coord]);
            yBase2 = northeast ? Math.max(yBase2, pos[idxBase2][coord]) : Math.min(yBase2, pos[idxBase2][coord]);

            boolean rotated = ModelUtils.isQuadRotated(uv);
            boolean mirrored = ModelUtils.isQuadMirrored(uv);

            float[][] uvSrc = new float[4][2];
            for (int i = 0; i < 4; i++) { System.arraycopy(uv[i], 0, uvSrc[i], 0, 2); }

            ModelUtils.remapUV(quadDir, pos[idxTip1 ][coord], pos[idxTip2 ][coord], yTip1,  uvSrc, uv, idxTip1,  idxTip2,  idxTip1,  false, northeast, rotated, mirrored);
            ModelUtils.remapUV(quadDir, pos[idxTip1 ][coord], pos[idxTip2 ][coord], yTip2,  uvSrc, uv, idxTip1,  idxTip2,  idxTip2,  false, northeast, rotated, mirrored);
            ModelUtils.remapUV(quadDir, pos[idxBase1][coord], pos[idxBase2][coord], yBase1, uvSrc, uv, idxBase1, idxBase2, idxBase1, false, northeast, rotated, mirrored);
            ModelUtils.remapUV(quadDir, pos[idxBase1][coord], pos[idxBase2][coord], yBase2, uvSrc, uv, idxBase1, idxBase2, idxBase2, false, northeast, rotated, mirrored);

            pos[idxTip1][coord] = yTip1;
            pos[idxTip2][coord] = yTip2;
            pos[idxBase1][coord] = yBase1;
            pos[idxBase2][coord] = yBase2;

            Vector3f origin = PRISM_DIR_TO_ORIGIN_VECS[quadDir.ordinal() - 2 + (up ? 0 : 4)];
            float angle = back ? PRISM_TILT_ANGLE : -PRISM_TILT_ANGLE;
            if (northeast != up) { angle *= -1F; }
            rotate(data, quadDir.getClockWise().getAxis(), origin, angle, true);
            rotate(data, Direction.Axis.Y, origin, 45, true);

            return true;
        };
    }

    /**
     * Cuts a quarter face size triangle quad with the tip centered on the base edge and the diagonal edge pointing
     * towards the left/bottom or right/top
     * @param cutDir The direction the triangle should point
     * @param rightUp Wether the diagonal edge of the triangle should point to the right in case of vertical quad
     *                direction or vertical cut direction or upwards for horizontal quad direction and cut direction
     */
    public static QuadModifier.Modifier cutSmallTriangle(Direction cutDir, boolean rightUp)
    {
        return data ->
        {
            Direction quadDir = data.quad().getDirection();

            Direction triDir;
            if (Utils.isY(quadDir))
            {
                triDir = rightUp ? cutDir.getClockWise() : cutDir.getCounterClockWise();
            }
            else if (Utils.isY(cutDir))
            {
                triDir = rightUp ? quadDir.getClockWise() : quadDir.getCounterClockWise();
            }
            else
            {
                triDir = rightUp ? Direction.UP : Direction.DOWN;
            }

            float lengthLeftBottom = rightUp ? .5F : 0F;
            float lengthRightTop = rightUp ? 0F : .5F;

            // Cut triangle shape
            if (!cut(data, triDir, lengthLeftBottom, lengthRightTop)) { return false; }

            // Cut to quarter size
            return cut(data, cutDir, .5F, .5F) && cut(data, triDir.getOpposite(), .5F, .5F);
        };
    }



    /**
     * Offsets the quad by the given amount in the given direction
     * @param dir The direction to offset the quad in
     * @param amount The amount the quad should be offset by
     */
    public static QuadModifier.Modifier offset(Direction dir, float amount)
    {
        if (Mth.equal(amount, 0F)) { return NOOP_MODIFIER; }

        return data ->
        {
            int idx = dir.getAxis().ordinal();
            float value = Utils.isPositive(dir) ? amount : (-1F * amount);

            for (int i = 0; i < 4; i++)
            {
                data.pos()[i][idx] += value;
            }

            return true;
        };
    }

    /**
     * Moves the quad to the given value in the quad's facing direction
     * @param posTarget The target position in the quad's facing direction
     */
    public static QuadModifier.Modifier setPosition(float posTarget)
    {
        if (Mth.equal(posTarget, 1F)) { return NOOP_MODIFIER; }

        return data ->
        {
            int idx = data.quad().getDirection().getAxis().ordinal();
            float value = Utils.isPositive(data.quad().getDirection()) ? posTarget : 1F - posTarget;

            for (int i = 0; i < 4; i++)
            {
                data.pos()[i][idx] = value;
            }

            return true;
        };
    }

    /**
     * Moves the individual vertices of the quad to the given values in the quad's facing direction
     * @param posTarget The target positions in the quad's facing direction
     * @param interpolate Whether the vertex positions should be interpolated for quads smaller than the full face
     * @implNote This does not create the same shape for all vertices when displacing a single one, this is not fixable without extreme effort
     */
    public static QuadModifier.Modifier setPosition(float[] posTarget, boolean interpolate)
    {
        Preconditions.checkArgument(posTarget.length == 4, "Target position array must contain 4 elements!");

        return data ->
        {
            //TODO: implement interpolation

            int idx = data.quad().getDirection().getAxis().ordinal();
            boolean positive = Utils.isPositive(data.quad().getDirection());
            for (int i = 0; i < 4; i++)
            {
                data.pos()[i][idx] = positive ? posTarget[i] : 1F - posTarget[i];
            }

            return true;
        };
    }



    /**
     * Rotates the quad on the given axis around the block center
     * @param axis The axis to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
     */
    public static QuadModifier.Modifier rotateCentered(Direction.Axis axis, float angle, boolean rescale)
    {
        return rotate(axis, CENTER, angle, rescale);
    }

    /**
     * Rotates the quad on the given axis around the block center
     * @param axis The axis to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
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
     * @param rescale Wether the quad should be rescaled or retain its dimensions
     */
    public static QuadModifier.Modifier rotate(Direction.Axis axis, Vector3f origin, float angle, boolean rescale)
    {
        return data ->
        {
            rotate(data, axis, origin, angle, rescale);
            return true;
        };
    }

    private static void rotate(QuadModifier.Data data, Direction.Axis axis, Vector3f origin, float angle, boolean rescale)
    {
        rotate(data, axis, origin, angle, rescale, ONE);
    }

    /**
     * Rotates the quad on the given axis around the given origin
     * @param axis The axis to rotate around
     * @param origin The point to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
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

    private static void rotate(QuadModifier.Data data, Direction.Axis axis, Vector3f origin, float angle, boolean rescale, Vector3f scaleMult)
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

        Matrix4f transform = new Matrix4f(new Quaternion(axisVec, angle, true));

        if (rescale)
        {
            float scaleAngle = Mth.abs(angle) > 45F ? (90F - Mth.abs(angle)) : Mth.abs(angle);

            if (scaleAngle == 22.5F) { scaleVec.mul(SCALE_ROTATION_22_5); }
            else if (scaleAngle == 45F) { scaleVec.mul(SCALE_ROTATION_45); }
            else
            {
                float scaleFactor = 1.0F / (float)Math.cos(Math.PI / (180D / (double)scaleAngle)) - 1.0F;
                scaleVec.mul(scaleFactor);
            }
            scaleMult.map(Math::abs);
            scaleVec.mul(scaleMult.x(), scaleMult.y(), scaleMult.z());
            scaleVec.add(1.0F, 1.0F, 1.0F);
        }

        float[][] pos = data.pos();
        for (int i = 0; i < 4; i++)
        {
            Vector4f vector4f = new Vector4f(pos[i][0] - origin.x(), pos[i][1] - origin.y(), pos[i][2] - origin.z(), 1.0F);
            if (rescale) { vector4f.mul(scaleVec); }
            vector4f.transform(transform);

            pos[i][0] = vector4f.x() + origin.x();
            pos[i][1] = vector4f.y() + origin.y();
            pos[i][2] = vector4f.z() + origin.z();
        }
    }



    private Modifiers() {}
}
