package xfacthd.framedblocks.api.render;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;
import xfacthd.framedblocks.api.block.FramedProperties;

/**
 * Provide custom outline rendering for blocks with non-axis-aligned edges such as slopes.
 * <p>
 * Must be registered in {@link RegisterOutlineRenderersEvent}
 */
public interface OutlineRenderer
{
    /**
     * Array of {@link Quaternionf}s for rotating around the Y axis according to the horizontal direction.<br>
     * Must be indexed with {@link Direction#get2DDataValue()}
     */
    Quaternionf[] YN_DIR = makeQuaternionArray();

    /**
     * Draw the outlines of the block. Provides access to the {@link BlockState}, {@link Level} and {@link BlockPos}
     * of the block being targeted for cases that require access to the block's
     * {@link net.minecraft.world.level.block.entity.BlockEntity}
     */
    default void draw(BlockState state, Level level, BlockPos pos, PoseStack poseStack, VertexConsumer builder)
    {
        draw(state, poseStack, builder);
    }

    /**
     * Draw the outlines of the block. Provides access to the {@link BlockState} of the block being targeted,
     * sufficient for most blocks
     */
    void draw(BlockState state, PoseStack poseStack, VertexConsumer builder);

    /**
     * Get the horizontal {@link Direction} the block is facing in
     */
    default Direction getRotationDir(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    /**
     * Manipulate the {@link PoseStack} to apply rotations and other transformations
     * @implNote The {@code PoseStack} is already centered in the target block space when this is called
     */
    default void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        Direction dir = getRotationDir(state);
        Preconditions.checkState(dir.getAxis().isHorizontal(), "Rotation direction must be horizontal");
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
        pstack.mulPose(Quaternions.ZP_180);
        if (rotY90)
        {
            pstack.mulPose(Quaternions.YN_90);
        }
    }

    /**
     * Draw a line between the two points given by the two sets of 3D coordinates
     */
    static void drawLine(VertexConsumer builder, PoseStack poseStack, float x1, float y1, float z1, float x2, float y2, float z2)
    {
        float nX = x2 - x1;
        float nY = y2 - y1;
        float nZ = z2 - z1;
        float nLen = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);

        nX = nX / nLen;
        nY = nY / nLen;
        nZ = nZ / nLen;

        var pose = poseStack.last();
        builder.addVertex(pose, x1, y1, z1).setColor(0, 0, 0, 102).setNormal(pose, nX, nY, nZ);
        builder.addVertex(pose, x2, y2, z2).setColor(0, 0, 0, 102).setNormal(pose, nX, nY, nZ);
    }



    static Quaternionf[] makeQuaternionArray()
    {
        Quaternionf[] array = new Quaternionf[4];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            array[dir.get2DDataValue()] = Axis.YN.rotationDegrees(dir.toYRot());
        }
        return array;
    }
}
