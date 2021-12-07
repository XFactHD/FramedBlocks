package xfacthd.framedblocks.api.util.client;

import com.google.common.base.Preconditions;
import com.mojang.math.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class BakedQuadTransformer
{
    private static final float SCALE_ROTATION_45 = 1.0F / (float)Math.cos(Math.PI / 4D) - 1.0F;
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(Math.PI / 8F) - 1.0F;
    private static final float PRISM_TILT_ANGLE = (float)Math.toDegrees(Math.atan(.5D));
    private static final Vector3f ONE = new Vector3f(1, 1, 1);
    private static final Vector3f HALF = new Vector3f(.5F, .5F, .5F);
    private static final Vector3f[] DIR_TO_ORIGIN_VECS = new Vector3f[]
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
     * Creates a sloped quad with the upper or lower edge moved in the opposite direction of the quad's facing
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param up Wether the upper or lower edge should be moved
     */
    public static void createTopBottomSlopeQuad(BakedQuad quad, boolean up)
    {
        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            Direction dir = quad.getDirection();
            int idx = dir.getAxis() == Direction.Axis.X ? 0 : 2;
            boolean invert = (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) == up;

            pos[1][idx] = invert ? 1F - pos[1][1] : pos[1][1];
            pos[2][idx] = invert ? 1F - pos[2][1] : pos[2][1];

            pos[0][idx] = invert ? 1F - pos[0][1] : pos[0][1];
            pos[3][idx] = invert ? 1F - pos[3][1] : pos[3][1];

            return true;
        });
    }

    /**
     * Creates a sloped quad with the right/left edge moved in the opposite direction of the quad's facing
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param rightEdge Wether the right or left edge should be moved back
     */
    public static void createSideSlopeQuad(BakedQuad quad, boolean rightEdge)
    {
        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            Direction dir = quad.getDirection();
            boolean xAxis = dir.getAxis() == Direction.Axis.X;
            boolean invert = (dir.getAxis() == Direction.Axis.Z) == rightEdge;

            int srcCoord =  xAxis ? 2 : 0;
            int destCoord = xAxis ? 0 : 2;

            pos[0][destCoord] = invert ? 1F - pos[0][srcCoord] : pos[0][srcCoord];
            pos[1][destCoord] = invert ? 1F - pos[1][srcCoord] : pos[1][srcCoord];

            pos[3][destCoord] = invert ? 1F - pos[3][srcCoord] : pos[3][srcCoord];
            pos[2][destCoord] = invert ? 1F - pos[2][srcCoord] : pos[2][srcCoord];

            return true;
        });
    }

    /**
     * Creates a triangle quad
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param rightSide Wether the quad is on the right side of the block looking at it from the front
     * @param top Wether the block is placed downwards
     */
    public static boolean createSideTriangleQuad(BakedQuad quad, boolean rightSide, boolean top)
    {
        return ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            int idxTopFront = rightSide ? 0 : 3;
            int idxBotFront = rightSide ? 1 : 2;
            int idxTopBack =  rightSide ? 3 : 0;
            int idxBotBack =  rightSide ? 2 : 1;

            int idxTargetFront = top ? idxBotFront : idxTopFront;
            int idxTargetBack = top ? idxBotBack : idxTopBack;

            Direction dir = rightSide ? quad.getDirection().getCounterClockWise() : quad.getDirection().getClockWise();
            boolean xAxis = dir.getAxis() == Direction.Axis.X;
            boolean neg = dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE;

            float xz = xAxis ? pos[top ? idxTopBack : idxBotBack][0] : pos[top ? idxTopBack : idxBotBack][2];
            if (neg) { xz = 1F - xz; }

            if ((top && pos[idxTopBack][1] >= (1F - xz)) || (!top && pos[idxBotBack][1] <= xz))
            {
                float[][] uvSrc = new float[4][2];
                for (int i = 0; i < 4; i++) { System.arraycopy(uv[i], 0, uvSrc[i], 0, 2); }

                boolean rotated = ModelUtils.isQuadRotated(uv);
                boolean mirrored = ModelUtils.isQuadMirrored(uv);

                xz = xAxis ? pos[idxBotFront][0] : pos[idxBotFront][2];
                if (neg) { xz = 1F - xz; }
                float toY = top ? Math.min(Math.max(pos[idxBotFront][1], 1F - xz), pos[idxTopFront][1]) : Math.max(Math.min(pos[idxTopFront][1], xz), pos[idxBotFront][1]);
                ModelUtils.remapUV(quad.getDirection(), pos[idxBotFront][1], pos[idxTopFront][1], toY, uvSrc, uv, idxTopFront, idxBotFront, idxTargetFront, true, true, rotated, mirrored);
                pos[idxTargetFront][1] = toY;

                xz = xAxis ? pos[idxBotBack][0] : pos[idxBotBack][2];
                if (neg) { xz = 1F - xz; }
                toY = top ? Math.min(Math.max(pos[idxBotBack][1], 1F - xz), pos[idxTopBack][1]) : Math.max(Math.min(pos[idxTopBack][1], xz), pos[idxBotBack][1]);
                ModelUtils.remapUV(quad.getDirection(), pos[idxTopBack][1], pos[idxBotBack][1], toY, uvSrc, uv, idxTopBack, idxBotBack, idxTargetBack, true, true, rotated, mirrored);
                pos[idxTargetBack][1] = toY;

                return true;
            }
            return false;
        });
    }

    /**
     * Creates a triangle quad pointing to the left corner in the given direction
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param dir The placement direction of the block
     */
    public static boolean createTopBottomTriangleQuad(BakedQuad quad, Direction dir)
    {
        return ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            boolean xAxis = dir.getAxis() == Direction.Axis.X;
            boolean up = quad.getDirection() == Direction.UP;
            boolean negTarget = dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
            boolean negModifier = dir.getClockWise().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
            int coordTarget = xAxis ? 0 : 2;
            int coordModifier = xAxis ? 2 : 0;

            //   | Left | Right
            //---+------+-------
            // N | 1->0 | 2->3
            // E | 0->3 | 1->2
            // S | 3->2 | 0->1
            // W | 2->1 | 3->0
            int idxFromRight = xAxis ? dir.getOpposite().get2DDataValue() : dir.get2DDataValue();
            int idxFromLeft = xAxis ? dir.getClockWise().get2DDataValue() : dir.getCounterClockWise().get2DDataValue();
            int idxToRight = xAxis ? dir.getCounterClockWise().get2DDataValue() : dir.getClockWise().get2DDataValue();
            int idxToLeft = !xAxis ? dir.getOpposite().get2DDataValue() : dir.get2DDataValue();

            if (!up && !xAxis)
            {
                int temp = idxFromLeft;
                idxFromLeft = idxToLeft;
                idxToLeft = temp;

                temp = idxFromRight;
                idxFromRight = idxToRight;
                idxToRight = temp;
            }

            float x = pos[idxToLeft][0];
            if ((xAxis && negTarget) || (!xAxis && negModifier)) { x = 1F - x; }
            float z = pos[idxToLeft][2];
            if ((!xAxis && negTarget) || (xAxis && negModifier)) { z = 1F - z; }

            if ((xAxis && (up ? x > z : x >= z)) || (!xAxis && z > x))
            {
                float[][] uvSrc = new float[4][2];
                for (int i = 0; i < 4; i++) { System.arraycopy(uv[i], 0, uvSrc[i], 0, 2); }

                boolean rotated = ModelUtils.isQuadRotated(uv);
                boolean mirrored = ModelUtils.isQuadMirrored(uv);

                float mod = pos[idxFromLeft][coordModifier];
                if (negModifier) { mod = 1F - mod; }
                float xz = pos[idxFromLeft][coordTarget];
                float toXZ = negTarget ? Math.min(xz, Math.max(pos[idxToLeft][coordTarget], 1F - mod)) : Math.max(xz, Math.min(pos[idxToLeft][coordTarget], mod));
                ModelUtils.remapUV(quad.getDirection(), xz, pos[idxToLeft][coordTarget], toXZ, uvSrc, uv, idxFromLeft, idxToLeft, idxFromLeft, !xAxis, !xAxis && !up, rotated, mirrored);
                pos[idxFromLeft][coordTarget] = toXZ;

                mod = pos[idxFromRight][coordModifier];
                if (negModifier) { mod = 1F - mod; }
                xz = pos[idxFromRight][coordTarget];
                toXZ = negTarget ? Math.min(xz, Math.max(pos[idxToRight][coordTarget], 1F - mod)) : Math.max(xz, Math.min(pos[idxToRight][coordTarget], mod));
                ModelUtils.remapUV(quad.getDirection(), xz, pos[idxToRight][coordTarget], toXZ, uvSrc, uv, idxFromRight, idxToRight, idxFromRight, !xAxis, !xAxis && !up, rotated, mirrored);
                pos[idxFromRight][coordTarget] = toXZ;

                return true;
            }
            return false;
        });
    }

    /**
     * Creates a triangle quad with the tip centered horizontally and pointing up or down.
     * The quad will have the right edge pushed back and the tip tilted to the top or bottom left corner
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param up Wether the tip should point up or down
     * @param back Wether the tip should tilt forward or backward
     */
    public static boolean createPrismTriangleQuad(BakedQuad quad, boolean up, boolean back)
    {
        boolean useQuad = ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            int coord = quad.getDirection().getAxis() == Direction.Axis.X ? 2 : 0;
            int checkVert1 = up ? 2 : 3;
            int checkVert2 = up ? 1 : 0;

            boolean vertPos = quad.getDirection().getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            float h1 = (up ? pos[checkVert1][1] : 1F - pos[checkVert1][1]) / 2F;
            float h2 = 1F - ((up ? pos[checkVert2][1] : 1F - pos[checkVert2][1]) / 2F);
            float xz1 = vertPos ? pos[checkVert1][coord] : 1F - pos[checkVert1][coord];
            float xz2 = vertPos ? pos[checkVert2][coord] : 1F - pos[checkVert2][coord];
            if (xz1 >= h1 && xz2 <= h2)
            {
                boolean northeast = quad.getDirection() == Direction.NORTH || quad.getDirection() == Direction.EAST;

                int idxTip1 = up ? 0 : 1;
                int idxTip2 = up ? 3 : 2;
                int idxBase1 = up ? 1 : 0;
                int idxBase2 = up ? 2 : 3;

                float yTip1 =  (up ? pos[idxTip1][1] :  1F - pos[idxTip1][1]) / 2F;
                float yTip2 =  (up ? pos[idxTip2][1] :  1F - pos[idxTip2][1]) / 2F;
                float yBase1 = (up ? pos[idxBase1][1] : 1F - pos[idxBase1][1]) / 2F;
                float yBase2 = (up ? pos[idxBase2][1] : 1F - pos[idxBase2][1]) / 2F;

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

                ModelUtils.remapUV(quad.getDirection(), pos[idxTip1 ][coord], pos[idxTip2 ][coord], yTip1,  uvSrc, uv, idxTip1,  idxTip2,  idxTip1,  false, northeast, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[idxTip1 ][coord], pos[idxTip2 ][coord], yTip2,  uvSrc, uv, idxTip1,  idxTip2,  idxTip2,  false, northeast, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[idxBase1][coord], pos[idxBase2][coord], yBase1, uvSrc, uv, idxBase1, idxBase2, idxBase1, false, northeast, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[idxBase1][coord], pos[idxBase2][coord], yBase2, uvSrc, uv, idxBase1, idxBase2, idxBase2, false, northeast, rotated, mirrored);

                pos[idxTip1][coord] = yTip1;
                pos[idxTip2][coord] = yTip2;
                pos[idxBase1][coord] = yBase1;
                pos[idxBase2][coord] = yBase2;

                return true;
            }
            return false;
        });
        if (!useQuad) { return false; }

        Vector3f origin = DIR_TO_ORIGIN_VECS[quad.getDirection().ordinal() - 2 + (up ? 0 : 4)];
        boolean northeast = quad.getDirection() == Direction.NORTH || quad.getDirection() == Direction.EAST;
        float angle = back ? PRISM_TILT_ANGLE : -PRISM_TILT_ANGLE;
        if (northeast != up) { angle *= -1F; }
        rotateQuadAroundAxis(quad, quad.getDirection().getClockWise().getAxis(), origin, angle, true);
        rotateQuadAroundAxis(quad, Direction.Axis.Y, origin, 45, true);

        return true;
    }

    /**
     * Creates a triangle quad with the tip centered and half a block height above the baseline.
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param dir The direction the triangle should point
     */
    public static boolean createSmallTriangleQuad(BakedQuad quad, TriangleDirection dir)
    {
        if (dir.isVertical())
        {
            if (!createHorizontalSideQuad(quad, dir == TriangleDirection.DOWN, .5F))
            {
                return false;
            }
        }
        else
        {
            Direction cutDir = dir == TriangleDirection.RIGHT ? quad.getDirection().getCounterClockWise() : quad.getDirection().getClockWise();
            if (!createVerticalSideQuad(quad, cutDir, .5F))
            {
                return false;
            }
        }

        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            Direction perpDir = quad.getDirection().getCounterClockWise();
            boolean perpPos = perpDir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            boolean perpXAxis = perpDir.getAxis() == Direction.Axis.X;

            boolean rotated = ModelUtils.isQuadRotated(uv);
            boolean mirrored = ModelUtils.isQuadMirrored(uv);

            float[][] uvSrc = new float[4][2];
            for (int i = 0; i < 4; i++) { System.arraycopy(uv[i], 0, uvSrc[i], 0, 2); }

            if (dir.isVertical())
            {
                boolean up = dir == TriangleDirection.UP;
                int coordTarget = perpXAxis ? 0 : 2;

                float y0 = up ? pos[0][1] : 1F - pos[0][1];
                float y1 = up ? pos[1][1] : 1F - pos[1][1];
                float y2 = up ? 1F - pos[2][1] : pos[2][1];
                float y3 = up ? 1F - pos[3][1] : pos[3][1];

                float xz0 = perpPos ? Math.max(Math.min(y0, pos[3][coordTarget]), pos[0][coordTarget]) : Math.min(Math.max(1F - y0, pos[3][coordTarget]), pos[0][coordTarget]);
                float xz1 = perpPos ? Math.max(Math.min(y1, pos[2][coordTarget]), pos[1][coordTarget]) : Math.min(Math.max(1F - y1, pos[2][coordTarget]), pos[1][coordTarget]);
                float xz2 = perpPos ? Math.min(Math.max(y2, pos[1][coordTarget]), pos[2][coordTarget]) : Math.max(Math.min(1F - y2, pos[1][coordTarget]), pos[2][coordTarget]);
                float xz3 = perpPos ? Math.min(Math.max(y3, pos[0][coordTarget]), pos[3][coordTarget]) : Math.max(Math.min(1F - y3, pos[0][coordTarget]), pos[3][coordTarget]);

                ModelUtils.remapUV(quad.getDirection(), pos[0][coordTarget], pos[3][coordTarget], xz0, uvSrc, uv, 0, 3, 0, false, !perpPos, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[1][coordTarget], pos[2][coordTarget], xz1, uvSrc, uv, 1, 2, 1, false, !perpPos, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[1][coordTarget], pos[2][coordTarget], xz2, uvSrc, uv, 1, 2, 2, false, !perpPos, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[0][coordTarget], pos[3][coordTarget], xz3, uvSrc, uv, 0, 3, 3, false, !perpPos, rotated, mirrored);

                pos[0][coordTarget] = xz0;
                pos[1][coordTarget] = xz1;
                pos[2][coordTarget] = xz2;
                pos[3][coordTarget] = xz3;
            }
            else
            {
                boolean right = dir == TriangleDirection.RIGHT;
                int coordSource = perpXAxis ? 0 : 2;

                float xz0 = (right == perpPos) ? 1F - pos[0][coordSource] : pos[0][coordSource];
                float xz1 = (right == perpPos) ? pos[1][coordSource] : 1F - pos[1][coordSource];
                float xz2 = (right == perpPos) ? pos[2][coordSource] : 1F - pos[2][coordSource];
                float xz3 = (right == perpPos) ? 1F - pos[3][coordSource] : pos[3][coordSource];

                float y0 = Math.min(Math.max(xz0, pos[1][1]), pos[0][1]);
                float y1 = Math.max(Math.min(xz1, pos[0][1]), pos[1][1]);
                float y2 = Math.max(Math.min(xz2, pos[3][1]), pos[2][1]);
                float y3 = Math.min(Math.max(xz3, pos[2][1]), pos[3][1]);

                ModelUtils.remapUV(quad.getDirection(), pos[0][1], pos[1][1], y0, uvSrc, uv, 0, 1, 0, true, true, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[0][1], pos[1][1], y1, uvSrc, uv, 0, 1, 1, true, true, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[2][1], pos[3][1], y2, uvSrc, uv, 2, 3, 2, true, true, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[2][1], pos[3][1], y3, uvSrc, uv, 2, 3, 3, true, true, rotated, mirrored);

                pos[0][1] = y0;
                pos[1][1] = y1;
                pos[2][1] = y2;
                pos[3][1] = y3;
            }

            return true;
        });
        return true;
    }

    /**
     * Creates a quad starting at the top or bottom edge and cut off at a given height
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param top Wether the quad starts from the top or bottom edge
     * @param height The target height from the starting edge
     */
    public static boolean createHorizontalSideQuad(BakedQuad quad, boolean top, float height)
    {
        return createHorizontalSideQuad(quad, top, height, height);
    }

    /**
     * Creates a quad starting at the top or bottom edge and cut off at a given height
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param top Wether the quad starts from the top or bottom edge
     * @param heightR The target height from the starting edge for the right vertex
     * @param heightL The target height from the starting edge for the left vertex
     */
    public static boolean createHorizontalSideQuad(BakedQuad quad, boolean top, float heightR, float heightL)
    {
        return ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            Direction quadDirRot = quad.getDirection().getCounterClockWise();
            boolean x = quadDirRot.getAxis() == Direction.Axis.X;
            boolean positive = quadDirRot.getAxisDirection() == Direction.AxisDirection.POSITIVE;

            float factorR = positive ? pos[0][x ? 0 : 2] : (1F - pos[0][x ? 0 : 2]);
            float factorL = positive ? pos[3][x ? 0 : 2] : (1F - pos[3][x ? 0 : 2]);

            float targetR = Mth.lerp(factorR, top ? 1F - heightR : heightR, top ? 1F - heightL : heightL);
            float targetL = Mth.lerp(factorL, top ? 1F - heightR : heightR, top ? 1F - heightL : heightL);
            if ((top && pos[0][1] >= targetR && pos[3][1] >= targetL) || (!top && pos[1][1] <= targetR && pos[2][1] <= targetL))
            {
                int idx1 = top ? 1 : 0;
                int idx2 = top ? 2 : 3;

                float y1 = pos[idx1][1];
                float y2 = pos[idx2][1];

                float toY1 = top ? Math.max(y1, targetR) : Math.min(y1, targetR);
                float toY2 = top ? Math.max(y2, targetL) : Math.min(y2, targetL);

                boolean rotated = ModelUtils.isQuadRotated(uv);
                boolean mirrored = ModelUtils.isQuadMirrored(uv);
                ModelUtils.remapUV(quad.getDirection(), pos[1][1], pos[0][1], toY1, uv, 0, 1, idx1, true, true, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[2][1], pos[3][1], toY2, uv, 3, 2, idx2, true, true, rotated, mirrored);

                pos[idx1][1] = toY1;
                pos[idx2][1] = toY2;

                return true;
            }
            return false;
        });
    }

    public static boolean createVerticalSideQuad(BakedQuad quad, Direction dir, float length)
    {
        Preconditions.checkArgument(dir == quad.getDirection().getClockWise() || dir == quad.getDirection().getCounterClockWise(),
                "Direction dir must be in the quad's plane!"
        );
        return createVerticalSideQuad(quad, dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE, length);
    }

    /**
     * Creates a quad starting at the positive or negative x/z edge and cut off at a given length
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param positive Wether to start from the positive edge x/z in the quad's plane
     * @param length The target length from the starting edge
     */
    public static boolean createVerticalSideQuad(BakedQuad quad, boolean positive, float length)
    {
        return createVerticalSideQuad(quad, positive, length, length);
    }

    public static boolean createVerticalSideQuad(BakedQuad quad, Direction dir, float lengthTop, float lengthBot)
    {
        Preconditions.checkArgument(dir == quad.getDirection().getClockWise() || dir == quad.getDirection().getCounterClockWise(),
                "Direction dir must be in the quad's plane!"
        );
        return createVerticalSideQuad(quad, dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE, lengthTop, lengthBot);
    }

    /**
     * Creates a quad starting at the positive or negative x/z edge and cut off at a given length
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param positive Wether to start from the positive edge x/z in the quad's plane
     * @param lengthTop The target length from the starting edge for the top vertex
     * @param lengthBot The target length from the starting edge for the bottom vertex
     */
    public static boolean createVerticalSideQuad(BakedQuad quad, boolean positive, float lengthTop, float lengthBot)
    {
        return ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            int coordIdx = quad.getDirection().getAxis() == Direction.Axis.X ? 2 : 0;
            boolean right = (quad.getDirection().getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE) == positive;
            int vertIdxTop = right ? 3 : 0;
            int vertIdxBot = right ? 2 : 1;

            float targetTop = Mth.lerp(1F - pos[vertIdxTop][1], positive ? 1F - lengthTop : lengthTop, positive ? 1F - lengthBot : lengthBot);
            float targetBot = Mth.lerp(1F - pos[vertIdxBot][1], positive ? 1F - lengthTop : lengthTop, positive ? 1F - lengthBot : lengthBot);
            if ((positive && pos[vertIdxTop][coordIdx] >= targetTop && pos[vertIdxBot][coordIdx] >= targetBot) ||
                (!positive && pos[vertIdxTop][coordIdx] <= targetTop && pos[vertIdxBot][coordIdx] <= targetBot)
            )
            {
                int idx1 = right ? 0 : 3;
                int idx2 = right ? 1 : 2;

                float xz1 = pos[idx1][coordIdx];
                float xz2 = pos[idx2][coordIdx];

                float toXZ1 = positive ? Math.max(xz1, targetTop) : Math.min(xz1, targetTop);
                float toXZ2 = positive ? Math.max(xz2, targetBot) : Math.min(xz2, targetBot);

                boolean rotated = ModelUtils.isQuadRotated(uv);
                boolean mirrored = ModelUtils.isQuadMirrored(uv);
                ModelUtils.remapUV(quad.getDirection(), pos[0][coordIdx], pos[3][coordIdx], toXZ1, uv, 0, 3, idx1, false, positive != right, rotated, mirrored);
                ModelUtils.remapUV(quad.getDirection(), pos[1][coordIdx], pos[2][coordIdx], toXZ2, uv, 1, 2, idx2, false, positive != right, rotated, mirrored);

                pos[idx1][coordIdx] = toXZ1;
                pos[idx2][coordIdx] = toXZ2;

                return true;
            }
            return false;
        });
    }

    /**
     * Creates a top/bottom quad starting at the edge opposite of the given cutDir and cut off after the given length
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param cutDir The direction from the starting edge to the cut edge
     * @param length The target length in the direction given by cutDir
     */
    public static boolean createTopBottomQuad(BakedQuad quad, Direction cutDir, float length)
    {
        return createTopBottomQuad(quad, cutDir, length, length);
    }

    /**
     * Creates a top/bottom quad starting at the edge opposite of the given cutDir and cut off after the given length
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param cutDir The direction from the starting edge to the cut edge
     * @param lengthR The target length in the direction given by cutDir for the right vertex
     * @param lengthL The target length in the direction given by cutDir for the left vertex
     */
    public static boolean createTopBottomQuad(BakedQuad quad, Direction cutDir, float lengthR, float lengthL)
    {
        return ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            boolean xAxis = cutDir.getAxis() == Direction.Axis.X;
            boolean positive = cutDir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            boolean up = quad.getDirection() == Direction.UP;

            int idxR = xAxis ? (positive ? 2 : 1) : ((up == positive) ? 1 : 0);
            int idxL = xAxis ? (positive ? 3 : 0) : ((up == positive) ? 2 : 3);

            Direction perpDir = cutDir.getCounterClockWise();
            boolean perpX = perpDir.getAxis() == Direction.Axis.X;
            float factorR = perpX ? pos[idxR][0] : (up ? (1F - pos[idxR][2]) : pos[idxR][2]);
            float factorL = perpX ? pos[idxL][0] : (up ? (1F - pos[idxL][2]) : pos[idxL][2]);

            float targetR = Mth.lerp(factorR, positive ? lengthR : 1F - lengthR, positive ? lengthL : 1F - lengthL);
            float targetL = Mth.lerp(factorL, positive ? lengthR : 1F - lengthR, positive ? lengthL : 1F - lengthL);

            int vertIdxR = xAxis ? (positive ? 1 : 3) : (up ? (positive ? 0 : 2) : (positive ? 1 : 3));
            int vertIdxL = xAxis ? (positive ? 0 : 2) : (up ? (positive ? 3 : 1) : (positive ? 2 : 0));
            int coordIdx = xAxis ? 0 : 2;
            if ((positive && pos[vertIdxR][coordIdx] <= targetR && pos[vertIdxL][coordIdx] <= targetL) ||
                    (!positive && pos[vertIdxR][coordIdx] >= targetR && pos[vertIdxL][coordIdx] >= targetL)
            )
            {
                float xz1 = pos[idxR][coordIdx];
                float xz2 = pos[idxL][coordIdx];

                float toXZ1 = positive ? Math.min(xz1, targetR) : Math.max(xz1, targetR);
                float toXZ2 = positive ? Math.min(xz2, targetL) : Math.max(xz2, targetL);

                boolean rotated = ModelUtils.isQuadRotated(uv);
                boolean mirrored = ModelUtils.isQuadMirrored(uv);

                if (xAxis)
                {
                    ModelUtils.remapUV(quad.getDirection(), pos[1][coordIdx], pos[2][coordIdx], toXZ1, uv, 1, 2, idxR, false, false, rotated, mirrored);
                    ModelUtils.remapUV(quad.getDirection(), pos[0][coordIdx], pos[3][coordIdx], toXZ2, uv, 0, 3, idxL, false, false, rotated, mirrored);
                }
                else
                {
                    ModelUtils.remapUV(quad.getDirection(), pos[1][coordIdx], pos[0][coordIdx], toXZ1, uv, 0, 1, idxR, true, !up, rotated, mirrored);
                    ModelUtils.remapUV(quad.getDirection(), pos[2][coordIdx], pos[3][coordIdx], toXZ2, uv, 3, 2, idxL, true, !up, rotated, mirrored);
                }

                pos[idxR][coordIdx] = toXZ1;
                pos[idxL][coordIdx] = toXZ2;

                return true;
            }
            return false;
        });
    }

    /**
     * Creates a top/bottom quad cut to the dimensions given by the min and max coordinates
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param minX Minimum X coordinate
     * @param minZ Minimum Z coordinate
     * @param maxX Maximum X coordinate
     * @param maxZ Maximum Z coordinate
     */
    public static boolean createTopBottomQuad(BakedQuad quad, float minX, float minZ, float maxX, float maxZ)
    {
        if (minX > 0F && !createTopBottomQuad(quad, Direction.WEST, 1F - minX))
        {
            return false;
        }
        if (maxX < 1F && !createTopBottomQuad(quad, Direction.EAST, maxX))
        {
            return false;
        }

        if (minZ > 0F && !createTopBottomQuad(quad, Direction.NORTH, 1F - minZ))
        {
            return false;
        }
        return !(maxZ < 1F) || createTopBottomQuad(quad, Direction.SOUTH, maxZ);
    }

    /**
     * Creates a side quad cut to the dimensions given by the min and max coordinates
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param minXZ Minimum X or Z coordinate, depending on the quad's facing
     * @param minY Minimum Y coordinate
     * @param maxXZ Maximum X or Z coordinate, depending on the quad's facing
     * @param maxY Maximum Y coordinate
     */
    public static boolean createSideQuad(BakedQuad quad, float minXZ, float minY, float maxXZ, float maxY)
    {
        if (minXZ > 0F && !createVerticalSideQuad(quad, true, 1F - minXZ))
        {
            return false;
        }
        if (maxXZ < 1F && !createVerticalSideQuad(quad, false, maxXZ))
        {
            return false;
        }

        if (minY > 0F && !createHorizontalSideQuad(quad, true, 1F - minY))
        {
            return false;
        }
        return !(maxY < 1F) || createHorizontalSideQuad(quad, false, maxY);
    }

    /**
     * Moves the given quad to the given value in the quad's facing direction
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad if no other processing happened before
     * @param posTarget The target position in the quad's facing direction
     */
    public static void setQuadPosInFacingDir(BakedQuad quad, float posTarget)
    {
        int idx = quad.getDirection().getAxis().ordinal();
        float value = quad.getDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE ? posTarget : 1F - posTarget;
        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            for (int i = 0; i < 4; i++)
            {
                pos[i][idx] = value;
            }
            return true;
        });
    }

    /**
     * Moves the individual vertices of the given quad to the given values in the quad's facing direction
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad if no other processing happened before
     * @param posTarget The target positions in the quad's facing direction
     * @implNote This does not create the same shape for all vertices when displacing a single one - TODO: check if this can be fixed
     */
    public static void setVertexPosInFacingDir(BakedQuad quad, float[] posTarget)
    {
        Preconditions.checkArgument(posTarget.length == 4, "Target position array must contain 4 elements!");

        int idx = quad.getDirection().getAxis().ordinal();
        boolean positive = quad.getDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE;
        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            for (int i = 0; i < 4; i++)
            {
                pos[i][idx] = positive ? posTarget[i] : 1F - posTarget[i];
            }
            return true;
        });
    }

    /**
     * Offsets the given quad by the given amount in the given direction
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad if no other processing happened before
     * @param dir The direction to offset the quad in
     * @param amount The amount the quad should be offset by
     */
    public static void offsetQuadInDir(BakedQuad quad, Direction dir, float amount)
    {
        int idx = dir.getAxis().ordinal();
        float value = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? amount : (-1F * amount);
        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
        {
            for (int i = 0; i < 4; i++)
            {
                pos[i][idx] += value;
            }
            return true;
        });
    }

    /**
     * Rotates the quad on the given axis around the block center
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param axis The axis to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
     */
    public static void rotateQuadAroundAxisCentered(BakedQuad quad, Direction.Axis axis, float angle, boolean rescale)
    {
        rotateQuadAroundAxis(quad, axis, HALF, angle, rescale);
    }

    /**
     * Rotates the quad on the given axis around the block center
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param axis The axis to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
     * @param scaleMult Modifier for the scale vector, can be used to inhibit scaling on selected axes
     */
    public static void rotateQuadAroundAxisCentered(BakedQuad quad, Direction.Axis axis, float angle, boolean rescale, Vector3f scaleMult)
    {
        rotateQuadAroundAxis(quad, axis, HALF, angle, rescale, scaleMult);
    }

    /**
     * Rotates the quad on the given axis around the given origin
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param axis The axis to rotate around
     * @param origin The point to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
     */
    public static void rotateQuadAroundAxis(BakedQuad quad, Direction.Axis axis, Vector3f origin, float angle, boolean rescale)
    {
        rotateQuadAroundAxis(quad, axis, origin, angle, rescale, ONE);
    }

    /**
     * Rotates the quad on the given axis around the given origin
     * @param quad The BakedQuad to manipulate, must be a copy of the original quad
     * @param axis The axis to rotate around
     * @param origin The point to rotate around
     * @param angle The angle of rotation in degrees
     * @param rescale Wether the quad should be rescaled or retain its dimensions
     * @param scaleMult Modifier for the scale vector, can be used to inhibit scaling on selected axes
     */
    public static void rotateQuadAroundAxis(BakedQuad quad, Direction.Axis axis, Vector3f origin, float angle, boolean rescale, Vector3f scaleMult)
    {
        ModelUtils.modifyQuad(quad, (pos, color, uv, light, normal) ->
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
                if (Math.abs(angle) == 22.5F) { scaleVec.mul(SCALE_ROTATION_22_5); }
                else if (Math.abs(angle) == 45F) { scaleVec.mul(SCALE_ROTATION_45); }
                else
                {
                    float scaleFactor = 1.0F / (float)Math.cos(Math.PI / (180D / (double)angle)) - 1.0F;
                    scaleVec.mul(scaleFactor);
                }
                scaleMult.map(Math::abs);
                scaleVec.mul(scaleMult.x(), scaleMult.y(), scaleMult.z());
                scaleVec.add(1.0F, 1.0F, 1.0F);
            }

            for (int i = 0; i < 4; i++)
            {
                Vector4f vector4f = new Vector4f(pos[i][0] - origin.x(), pos[i][1] - origin.y(), pos[i][2] - origin.z(), 1.0F);
                if (rescale) { vector4f.mul(scaleVec); }
                vector4f.transform(transform);

                pos[i][0] = vector4f.x() + origin.x();
                pos[i][1] = vector4f.y() + origin.y();
                pos[i][2] = vector4f.z() + origin.z();
            }

            return true;
        });
    }
}