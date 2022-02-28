package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.data.PropertyHolder;

public interface OutlineRender
{
    default void draw(BlockState state, World world, BlockPos pos, MatrixStack poseStack, IVertexBuilder builder)
    {
        draw(state, poseStack, builder);
    }

    void draw(BlockState state, MatrixStack poseStack, IVertexBuilder builder);

    default Direction getRotationDir(BlockState state) { return state.getValue(PropertyHolder.FACING_HOR); }

    default void rotateMatrix(MatrixStack poseStack, BlockState state)
    {
        Direction dir = getRotationDir(state);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(dir.toYRot()));
    }

    static void drawLine(IVertexBuilder builder, MatrixStack mstack, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        float nX = (float)(x2 - x1);
        float nY = (float)(y2 - y1);
        float nZ = (float)(z2 - z1);
        float nLen = MathHelper.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        builder.vertex(mstack.last().pose(), (float)x1, (float)y1, (float)z1).color(0.0F, 0.0F, 0.0F, 0.4F).normal(mstack.last().normal(), nX, nY, nZ).endVertex();
        builder.vertex(mstack.last().pose(), (float)x2, (float)y2, (float)z2).color(0.0F, 0.0F, 0.0F, 0.4F).normal(mstack.last().normal(), nX, nY, nZ).endVertex();
    }
}