package io.github.xfacthd.framedblocks.api.render.outline;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.render.Quaternions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/**
 * Provide custom outline rendering for blocks with non-axis-aligned edges such as slopes.
 * Use {@link SimpleOutlineRenderer} for blocks which only need the {@link BlockState} for
 * rendering the outline.
 * <p>
 * Must be registered in {@link RegisterOutlineRenderersEvent}
 */
public interface OutlineRenderer<T> {
    OutlineRenderer<Unit> NO_OP = new NoopOutlineRenderer();

    /**
     * Array of {@link Quaternionfc}s for rotating around the Y axis according to the horizontal direction.<br>
     * Must be indexed with {@link Direction#get2DDataValue()}
     */
    Quaternionfc[] YN_DIR = makeQuaternionArray();

    /**
     * Extract additional data required for rendering which is not available from just the {@link BlockState}.
     *
     * @return additional data or null to fall back to vanilla rendering
     */
    @Nullable T extractOutlineData(BlockState state, Level level, BlockPos pos);

    /**
     * Draw the outlines of the block. Provides access to the {@link BlockState}, {@link Level} and {@link BlockPos}
     * of the block being targeted for cases that require access to the block's
     * {@link net.minecraft.world.level.block.entity.BlockEntity}
     */
    void draw(BlockState state, T data, LineDrawer drawer);

    /**
     * Get the horizontal {@link Direction} the block is facing in
     */
    default Direction getRotationDir(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    /**
     * Manipulate the {@link PoseStack} to apply rotations and other transformations
     * @implNote The {@code PoseStack} is already centered in the target block space when this is called
     */
    default void rotateMatrix(PoseStack poseStack, BlockState state) {
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
    static void mirrorHorizontally(PoseStack pstack, boolean rotY90) {
        pstack.mulPose(Quaternions.ZP_180);
        if (rotY90) {
            pstack.mulPose(Quaternions.YN_90);
        }
    }

    private static Quaternionf[] makeQuaternionArray() {
        Quaternionf[] array = new Quaternionf[4];
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            array[dir.get2DDataValue()] = Axis.YN.rotationDegrees(dir.toYRot());
        }
        return array;
    }

    @ApiStatus.NonExtendable
    interface LineDrawer {
        void drawLine(float x1, float y1, float z1, float x2, float y2, float z2);

        void drawLines(float[] vertices);
    }
}
