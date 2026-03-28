package io.github.xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.render.Quaternions;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public final class CollapsibleBlockOutlineRenderer implements OutlineRenderer<CollapsibleBlockOutlineRenderer.OutlineData> {
    private static final Quaternionf ROTATION = Axis.YN.rotationDegrees(180);

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state) {
        poseStack.mulPose(ROTATION);

        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face != NullableDirection.NONE) {
            Direction faceDir = face.toDirection().getOpposite();

            if (faceDir == Direction.UP) {
                poseStack.mulPose(Quaternions.XP_180);
            } else if (faceDir != Direction.DOWN) {
                poseStack.mulPose(OutlineRenderer.YN_DIR[faceDir.getOpposite().get2DDataValue()]);
                poseStack.mulPose(Quaternions.XN_90);
            }
        }
    }

    @Override
    public @Nullable OutlineData extractOutlineData(BlockState state, Level level, BlockPos pos)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face == NullableDirection.NONE) {
            return null;
        }
        if (!(level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)) {
            return null;
        }

        return new OutlineData(
                1F - (be.getVertexOffset(0) / 16F),
                1F - (be.getVertexOffset(1) / 16F),
                1F - (be.getVertexOffset(2) / 16F),
                1F - (be.getVertexOffset(3) / 16F)
        );
    }

    @Override
    public void draw(BlockState state, OutlineData data, LineDrawer drawer) {
        float v0 = data.v0;
        float v1 = data.v1;
        float v2 = data.v2;
        float v3 = data.v3;

        //Top
        drawer.drawLine(0, v2, 0, 0, v3, 1);
        drawer.drawLine(0, v2, 0, 1, v1, 0);
        drawer.drawLine(1, v1, 0, 1, v0, 1);
        drawer.drawLine(0, v3, 1, 1, v0, 1);

        //Bottom
        drawer.drawLine(0, 0, 0, 0, 0, 1);
        drawer.drawLine(0, 0, 0, 1, 0, 0);
        drawer.drawLine(1, 0, 0, 1, 0, 1);
        drawer.drawLine(0, 0, 1, 1, 0, 1);

        //Vertical
        drawer.drawLine(1, 0, 1, 1, v0, 1);
        drawer.drawLine(1, 0, 0, 1, v1, 0);
        drawer.drawLine(0, 0, 0, 0, v2, 0);
        drawer.drawLine(0, 0, 1, 0, v3, 1);
    }

    public record OutlineData(float v0, float v1, float v2, float v3) {}
}
