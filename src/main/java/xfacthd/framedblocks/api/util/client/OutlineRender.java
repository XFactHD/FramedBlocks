package xfacthd.framedblocks.api.util.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.FramedProperties;

public interface OutlineRender
{
    Quaternion[] YN_DIR = makeQuaternionArray();

    default void draw(BlockState state, Level level, BlockPos pos, PoseStack poseStack, VertexConsumer builder)
    {
        draw(state, poseStack, builder);
    }

    void draw(BlockState state, PoseStack poseStack, VertexConsumer builder);

    default Direction getRotationDir(BlockState state) { return state.getValue(FramedProperties.FACING_HOR); }

    default void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        Direction dir = getRotationDir(state);
        poseStack.mulPose(YN_DIR[dir.get2DDataValue()]);
    }

    /**
     * Mirrors the {@link PoseStack} around the horizontal plane
     * @param pstack The {@code PoseStack} used for rendering
     * @param rotY90 Whether the {@code PoseStack} needs to be rotated -90 degrees around the y-axis,
     *               needed for un-symmetric shapes like corners
     */
    static void mirrorHorizontally(PoseStack pstack, boolean rotY90)
    {
        pstack.translate(.5, .5, .5);
        pstack.mulPose(Quaternions.ZP_180);
        if (rotY90)
        {
            pstack.mulPose(Quaternions.YN_90);
        }
        pstack.translate(-.5, -.5, -.5);
    }

    static void drawLine(VertexConsumer builder, PoseStack mstack, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        float nX = (float)(x2 - x1);
        float nY = (float)(y2 - y1);
        float nZ = (float)(z2 - z1);
        float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        builder.vertex(mstack.last().pose(), (float)x1, (float)y1, (float)z1).color(0.0F, 0.0F, 0.0F, 0.4F).normal(mstack.last().normal(), nX, nY, nZ).endVertex();
        builder.vertex(mstack.last().pose(), (float)x2, (float)y2, (float)z2).color(0.0F, 0.0F, 0.0F, 0.4F).normal(mstack.last().normal(), nX, nY, nZ).endVertex();
    }



    static Quaternion[] makeQuaternionArray()
    {
        Quaternion[] array = new Quaternion[4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = Vector3f.YN.rotationDegrees(dir.toYRot());
        }
        return array;
    }
}