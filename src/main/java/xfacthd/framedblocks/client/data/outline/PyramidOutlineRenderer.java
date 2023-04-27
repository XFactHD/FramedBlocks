package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.render.OutlineRenderer;

public final class PyramidOutlineRenderer implements OutlineRenderer
{
    private static final Quaternionf[] XN_DIR = makeQuaternionArray();

    private final float height;

    public PyramidOutlineRenderer(boolean slab) { this.height = slab ? .5F : 1; }

    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        // Base edges
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);

        // Slopes
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, .5F, height, .5F);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 0, .5F, height, .5F);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 1, .5F, height, .5F);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 1, .5F, height, .5F);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        if (dir == Direction.DOWN)
        {
            poseStack.mulPose(Quaternions.ZP_180);
        }
        else if (dir != Direction.UP)
        {
            poseStack.mulPose(Quaternions.ZP_90);
            poseStack.mulPose(XN_DIR[dir.get2DDataValue()]);
        }
    }



    private static Quaternionf[] makeQuaternionArray()
    {
        Quaternionf[] array = new Quaternionf[4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = Axis.XN.rotationDegrees(dir.toYRot() - 90F);
        }
        return array;
    }
}
