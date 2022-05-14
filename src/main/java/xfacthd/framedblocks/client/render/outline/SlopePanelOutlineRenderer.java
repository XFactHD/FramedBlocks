package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.Rotation;

import java.util.EnumMap;
import java.util.Map;

public class SlopePanelOutlineRenderer implements OutlineRender
{
    public static final Map<Rotation, Quaternion> ROTATIONS = Util.make(new EnumMap<>(Rotation.class), map ->
    {
        map.put(Rotation.UP, Quaternion.ONE);
        map.put(Rotation.DOWN, Vector3f.ZP.rotationDegrees(180));
        map.put(Rotation.RIGHT, Vector3f.ZP.rotationDegrees(90));
        map.put(Rotation.LEFT, Vector3f.ZP.rotationDegrees(-90));
    });

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 1, 0,  0);
        OutlineRender.drawLine(builder, poseStack, 0, 0, .5, 1, 0, .5);
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 0, 0, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0,  0, 1, 0, .5);
        OutlineRender.drawLine(builder, poseStack, 0, 0, .5, 0, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0, .5, 1, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 0, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0,  0, 1, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 0, 1, .5, 1, 1, .5);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        Rotation rotation = state.getValue(PropertyHolder.ROTATION);
        poseStack.mulPose(ROTATIONS.get(rotation));

        if (!state.getValue(PropertyHolder.FRONT))
        {
            poseStack.translate(0, 0, .5);
        }
    }
}
