package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
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
    public void draw(BlockState state, MatrixStack matrixStack, IVertexBuilder builder)
    {
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  0, 1, 0,  0);
        OutlineRender.drawLine(builder, matrixStack, 0, 0, .5, 1, 0, .5);
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  0, 0, 0, .5);
        OutlineRender.drawLine(builder, matrixStack, 1, 0,  0, 1, 0, .5);
        OutlineRender.drawLine(builder, matrixStack, 0, 0, .5, 0, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 1, 0, .5, 1, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 0, 0,  0, 0, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 1, 0,  0, 1, 1, .5);
        OutlineRender.drawLine(builder, matrixStack, 0, 1, .5, 1, 1, .5);
    }

    @Override
    public void rotateMatrix(MatrixStack matrixStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(matrixStack, state);

        Rotation rotation = state.getValue(PropertyHolder.ROTATION);
        matrixStack.mulPose(ROTATIONS.get(rotation));

        if (!state.getValue(PropertyHolder.FRONT))
        {
            matrixStack.translate(0, 0, .5);
        }
    }
}
