package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public class PyramidOutlineRenderer implements OutlineRender
{
    private static final Quaternion ZP_90 = Vector3f.ZP.rotation(Mth.PI / 2F);
    private static final Quaternion ZP_180 = Vector3f.ZP.rotation(Mth.PI);
    private static final Quaternion[] XN_DIR = makeQuaternionArray();

    private final float height;

    public PyramidOutlineRenderer(boolean slab) { this.height = slab ? .5F : 1; }

    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);

        OutlineRender.drawLine(builder, pstack, 0, 0, 0, .5F, height, .5F);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, .5F, height, .5F);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, .5F, height, .5F);
        OutlineRender.drawLine(builder, pstack, 1, 0, 1, .5F, height, .5F);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        if (dir == Direction.DOWN)
        {
            poseStack.mulPose(ZP_180);
        }
        else if (dir != Direction.UP)
        {
            poseStack.mulPose(ZP_90);
            poseStack.mulPose(XN_DIR[dir.get2DDataValue()]);
        }
    }



    private static Quaternion[] makeQuaternionArray()
    {
        Quaternion[] array = new Quaternion[4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = Vector3f.XN.rotationDegrees(dir.toYRot() - 90F);
        }
        return array;
    }
}
