package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.render.OutlineRenderer;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public class PrismOutlineRenderer implements OutlineRenderer
{
    protected static final Quaternionf[] YN_DIR = makeQuaternionArray();

    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        // Base edges
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);

        // Back triangle
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 1, .5F, .5F, 1);
        OutlineRenderer.drawLine(builder, pstack, .5F, .5F, 1, 1, 0, 1);

        drawCenterAndTriangle(pstack, builder);
    }

    protected void drawCenterAndTriangle(PoseStack pstack, VertexConsumer builder)
    {
        // Center line
        OutlineRenderer.drawLine(builder, pstack, .5F, .5F, 0, .5F, .5F, 1);

        // Front triangle
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, .5F, .5F, 0);
        OutlineRenderer.drawLine(builder, pstack, .5F, .5F, 0, 1, 0, 0);
    }

    @Override
    public void rotateMatrix(PoseStack pstack, BlockState state)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        Direction facing = dirAxis.direction();
        Direction.Axis axis = dirAxis.axis();

        if (Utils.isY(facing))
        {
            if (facing == Direction.DOWN)
            {
                pstack.mulPose(Quaternions.ZP_180);
            }
            if (axis == Direction.Axis.X)
            {
                pstack.mulPose(Quaternions.YP_90);
            }
        }
        else
        {
            if (facing != Direction.SOUTH)
            {
                pstack.mulPose(YN_DIR[facing.get2DDataValue()]);
            }
            if (axis != Direction.Axis.Y)
            {
                pstack.mulPose(Quaternions.ZP_90);
            }
            pstack.mulPose(Quaternions.XP_90);
        }
    }



    private static Quaternionf[] makeQuaternionArray()
    {
        Quaternionf[] array = new Quaternionf[4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = Axis.YN.rotation(Mth.PI / 2F * dir.get2DDataValue());
        }
        return array;
    }
}
