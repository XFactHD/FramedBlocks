package io.github.xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public static QuadModifier.Modifier noop()
    {
        return NOOP_MODIFIER;
    }

    /**
     * Cut the quad such that the provided cut edge is {@code length} away from the opposite edge's block bound.
     *
     * @param cutEdge The edge of the quad to move
     * @param length  The target length from the starting edge
     */
    public static QuadModifier.Modifier cut(Direction cutEdge, float length)
    {
        return cut(cutEdge, length, length);
    }

    /**
     * Cut the quad such that both edges of the quad specified by the provided cut axis are {@code length} away
     * from the opposite edge's block bound.
     *
     * @param cutAxis The direction towards the cut edge
     * @param length  The target length from the starting edge
     */
    public static QuadModifier.Modifier cut(Direction.Axis cutAxis, float length)
    {
        if (Mth.equal(length, 1F))
        {
            return NOOP_MODIFIER;
        }
        return data -> cut(data, cutAxis.getNegative(), length, length) && cut(data, cutAxis.getPositive(), length, length);
    }

    /**
     * Cut the quad such that the two corners of the provided cut edge are {@code lengthOne} and {@code lengthTwo}
     * away from the opposite edge's block bound.
     * <p>
     * The lengths are assigned to the corners as follows:
     * <ul>
     *     <li>
     *         For vertical-facing quads, {@code lengthOne} is the corner clockwise from the cut edge and
     *         {@code lengthTwo} is the corner counterclockwise from the cut edge as observed from the top down
     *     </li>
     *     <li>
     *         For vertical-facing cut edges on horizontal-facing quads, {@code lengthOne} is the corner clockwise from
     *         the quad's normal dir and {@code lengthTwo} is the corner counterclockwise from the quad's normal dir
     *     </li>
     *     <li>
     *         For horizontal-facing cut edges on horizontal-facing quads, {@code lengthOne} is the top corner and
     *         {@code lengthTwo} is the bottom corner
     *     </li>
     * </ul>
     *
     * @param cutEdge   The edge of the quad to move
     * @param lengthOne The length on the first corner of the specified edge
     * @param lengthTwo The length on the second corner of the specified edge
     */
    public static QuadModifier.Modifier cut(Direction cutEdge, float lengthOne, float lengthTwo)
    {
        if (Mth.equal(lengthOne, 1F) && Mth.equal(lengthTwo, 1F))
        {
            return NOOP_MODIFIER;
        }
        return data -> cut(data, cutEdge, lengthOne, lengthTwo);
    }

    private static boolean cut(QuadData data, Direction cutEdge, float lengthOne, float lengthTwo)
    {
        Direction quadDir = data.direction();
        Preconditions.checkArgument(quadDir.getAxis() != cutEdge.getAxis(), "Cut edge must be perpendicular to quad direction");

        CuttingConfig config = ModifierConfigs.getCuttingConfig(quadDir, cutEdge);
        boolean positive = DirUtils.isPositive(cutEdge);
        boolean invertParallelEdge = config.invertParallelEdge();
        int coordForward = config.forwardCoord();
        int coordParallel = config.parallelCoord();
        CuttingConfig.VertPair cutPair = config.cutEdgeVerts();
        CuttingConfig.VertPair checkPair = config.checkEdgeVerts();

        if (config.swapCornerLengths())
        {
            float temp = lengthOne;
            lengthOne = lengthTwo;
            lengthTwo = temp;
        }

        float factorOne = invertParallelEdge ? 1F - data.pos(cutPair.v1(), coordParallel) : data.pos(cutPair.v1(), coordParallel);
        float factorTwo = invertParallelEdge ? 1F - data.pos(cutPair.v2(), coordParallel) : data.pos(cutPair.v2(), coordParallel);
        float targetOne = Mth.lerp(factorOne, positive ? lengthOne : 1F - lengthOne, positive ? lengthTwo : 1F - lengthTwo);
        float targetTwo = Mth.lerp(factorTwo, positive ? lengthOne : 1F - lengthOne, positive ? lengthTwo : 1F - lengthTwo);

        if (positive && (MathUtils.isHigher(data.pos(checkPair.v1(), coordForward), targetOne) || MathUtils.isHigher(data.pos(checkPair.v2(), coordForward), targetTwo)))
        {
            return false;
        }
        if (!positive && (MathUtils.isLower(data.pos(checkPair.v1(), coordForward), targetOne) || MathUtils.isLower(data.pos(checkPair.v2(), coordForward), targetTwo)))
        {
            return false;
        }

        float posOne = data.pos(cutPair.v1(), coordForward);
        float posTwo = data.pos(cutPair.v2(), coordForward);

        float destPosOne = positive ? Math.min(posOne, targetOne) : Math.max(posOne, targetOne);
        float destPosTwo = positive ? Math.min(posTwo, targetTwo) : Math.max(posTwo, targetTwo);

        if (Mth.equal(posOne, destPosOne) && Mth.equal(posTwo, destPosTwo))
        {
            return true;
        }

        boolean vAxis = config.vAxis();
        boolean rotated = data.uvRotated();

        CuttingConfig.UvSrcVertSet uvVertsOne = config.uvVertsOne();
        ModelUtils.remapUV(
                data,
                data.pos(uvVertsOne.posOne(), coordForward),
                data.pos(uvVertsOne.posTwo(), coordForward),
                destPosOne,
                uvVertsOne.uvOne(),
                uvVertsOne.uvTwo(),
                cutPair.v1(),
                vAxis,
                rotated
        );
        CuttingConfig.UvSrcVertSet uvVertsTwo = config.uvVertsTwo();
        ModelUtils.remapUV(
                data,
                data.pos(uvVertsTwo.posOne(), coordForward),
                data.pos(uvVertsTwo.posTwo(), coordForward),
                destPosTwo,
                uvVertsTwo.uvOne(),
                uvVertsTwo.uvTwo(),
                cutPair.v2(),
                vAxis,
                rotated
        );

        data.pos(cutPair.v1(), coordForward, destPosOne);
        data.pos(cutPair.v2(), coordForward, destPosTwo);

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
            Direction quadDir = data.direction();
            Preconditions.checkArgument(DirUtils.isY(quadDir), "Quad direction must be vertical");

            return cut(data, Direction.WEST, 1F - minX, 1F - minX) &&
                   cut(data, Direction.EAST, maxX, maxX) &&
                   cut(data, Direction.NORTH, 1F - minZ, 1F - minZ) &&
                   cut(data, Direction.SOUTH, maxZ, maxZ);
        };
    }

    /**
     * Creates a horizontal facing quad cut to the dimensions given by the min and max coordinates
     * @param minXZ Minimum X or Z coordinate, depending on the quad's facing
     * @param minY Minimum Y coordinate
     * @param maxXZ Maximum X or Z coordinate, depending on the quad's facing
     * @param maxY Maximum Y coordinate
     */
    public static QuadModifier.Modifier cutSide(float minXZ, float minY, float maxXZ, float maxY)
    {
        return data ->
        {
            Direction quadDir = data.direction();
            Preconditions.checkArgument(!DirUtils.isY(quadDir), "Quad direction must be horizontal");

            boolean rightPositive = DirUtils.isPositive(quadDir.getClockWise());
            float leftXZ = rightPositive ? (1F - minXZ) : maxXZ;
            float rightXZ = rightPositive ? maxXZ : (1F - minXZ);

            return cut(data, quadDir.getClockWise(), rightXZ, rightXZ) &&
                   cut(data, quadDir.getCounterClockWise(), leftXZ, leftXZ) &&
                   cut(data, Direction.DOWN, 1F - minY, 1F - minY) &&
                   cut(data, Direction.UP, maxY, maxY);
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
            Direction quadDir = data.direction();
            Preconditions.checkArgument(!DirUtils.isY(quadDir), "Quad direction must be horizontal");
            Preconditions.checkArgument(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be perpendicular to the quad direction");

            if (DirUtils.isY(cutDir))
            {
                boolean down = cutDir == Direction.DOWN;
                float lenRight = down ? lengthCW : lengthCCW;
                float lenLeft = down ? lengthCCW : lengthCW;

                return cut(data, cutDir, lenRight, lenLeft);
            }
            else
            {
                boolean right = cutDir == quadDir.getClockWise();
                float lenTop = right ? lengthCW : lengthCCW;
                float lenBottom = right ? lengthCCW : lengthCW;

                return cut(data, cutDir, lenTop, lenBottom);
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
            Direction quadDir = data.direction();
            Preconditions.checkArgument(!DirUtils.isY(quadDir), "Quad direction must not be on the Y axis");

            boolean leftCut = cut(data, quadDir.getCounterClockWise(), up ? .5F : 1, up ? 1 : .5F);
            boolean rightCut = cut(data, quadDir.getClockWise(), up ? .5F : 1, up ? 1 : .5F);
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
        Preconditions.checkArgument(!DirUtils.isY(cutDir), "Cut direction must be horizontal");
        return data ->
        {
            Direction quadDir = data.direction();
            Preconditions.checkArgument(DirUtils.isY(quadDir), "Quad direction must be on the Y axis");

            boolean leftCut = cut(data, cutDir.getCounterClockWise(), .5F, 1);
            boolean rightCut = cut(data, cutDir.getClockWise(), 1, .5F);
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
            Direction quadDir = data.direction();
            Preconditions.checkArgument(!DirUtils.isY(quadDir) || !DirUtils.isY(cutDir), "Cut direction cannot be along the Y axis for quads pointing along the Y axis");

            if (!cut(data, cutDir, .5F, .5F))
            {
                return false;
            }

            boolean left;
            boolean right;
            if (DirUtils.isY(cutDir))
            {
                boolean up = cutDir == Direction.UP;
                left = cut(data, quadDir.getCounterClockWise(), up ? 0 : 1, up ? 1 : 0);
                right = cut(data, quadDir.getClockWise(), up ? 0 : 1, up ? 1 : 0);
            }
            else if (DirUtils.isY(quadDir))
            {
                left = cut(data, cutDir.getCounterClockWise(), 0, 1);
                right = cut(data, cutDir.getClockWise(), 1, 0);
            }
            else
            {
                boolean cutRight = cutDir == quadDir.getClockWise();
                left = cut(data, Direction.UP, cutRight ? 0 : 1, cutRight ? 1 : 0);
                right = cut(data, Direction.DOWN, cutRight ? 0 : 1, cutRight ? 1 : 0);
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
            Direction dir = data.direction();
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
            Direction dir = data.direction();

            Direction.Axis axis = dir.getClockWise().getAxis();
            Vector3f origin = VERTICAL_ORIGINS[dir.ordinal() - 2 + (topEdge ? 4 : 0)];
            float rotAngle = DirUtils.isPositive(dir.getClockWise()) != topEdge ? -angle : angle;
            Vector3f scaleVec = DirUtils.isX(dir) ? SCALE_VERT_X : SCALE_VERT_Z;

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
            Direction dir = data.direction();
            boolean top = dir == Direction.UP;
            Preconditions.checkArgument(DirUtils.isY(dir), "Quad direction must be on the Y axis");
            Preconditions.checkArgument(!DirUtils.isY(edge), "Edge direction must be horizontal");

            Direction.Axis axis = edge.getClockWise().getAxis();
            Vector3f origin = VERTICAL_ORIGINS[edge.getOpposite().ordinal() - 2 + (top ? 0 : 4)];
            float rotAngle = DirUtils.isPositive(edge.getClockWise()) != top ? angle : -angle;
            Vector3f scaleVec = DirUtils.isX(edge) ? SCALE_VERT_X : SCALE_VERT_Z;

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
        float value = DirUtils.isPositive(dir) ? amount : (-1F * amount);

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
            int idx = data.direction().getAxis().ordinal();
            float value = DirUtils.isPositive(data.direction()) ? posTarget : 1F - posTarget;

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
            Direction dir = data.direction();
            int idx = dir.getAxis().ordinal();
            boolean positive = DirUtils.isPositive(dir);
            boolean y = DirUtils.isY(dir);
            Direction ccwDir = y ? dir : dir.getCounterClockWise();
            boolean ccwPositive = DirUtils.isPositive(ccwDir);
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
            Vector3f scaleVec = switch (data.direction().getAxis())
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

    public static QuadModifier.Modifier setLightEmission(int emission, boolean increaseOnly)
    {
        return data ->
        {
            int finalEmission = emission;
            if (increaseOnly)
            {
                finalEmission = Math.max(finalEmission, data.lightEmission());
            }
            data.lightEmission(finalEmission);
            return true;
        };
    }

    private Modifiers() {}
}
