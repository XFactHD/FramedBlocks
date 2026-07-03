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

/// Provide custom outline rendering for blocks with non-axis-aligned edges such as slopes.
/// Use [SimpleOutlineRenderer] for blocks which only need the blockstate for
/// rendering the outline.
///
/// Must be registered in [RegisterOutlineRenderersEvent].
public interface OutlineRenderer<T> {
    OutlineRenderer<Unit> NO_OP = new NoopOutlineRenderer();

    /// Array of quaternions for rotating around the Y axis according to the horizontal direction.
    ///
    /// Must be indexed with [Direction#get2DDataValue()].
    Quaternionfc[] YN_DIR = makeQuaternionArray();

    /// Extract additional data required for rendering which is not available from just the blockstate.
    ///
    /// @return additional data or null to fall back to vanilla rendering
    @Nullable T extractOutlineData(BlockState state, Level level, BlockPos pos);

    /// Draw the outlines of the block.
    ///
    /// @param state  The blockstate of the targetted block
    /// @param data   Additional data extracted in [#extractOutlineData(BlockState, Level, BlockPos)]
    /// @param drawer The line drawer to submit line segments to
    void draw(BlockState state, T data, LineDrawer drawer);

    /// {@return the horizontal orientation of the block}
    ///
    /// @param state The state of the targetted block
    default Direction getRotationDir(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    /// Manipulate the pose stack to apply rotations and other transformations.
    ///
    /// @param poseStack The pose stack to apply transformations to
    /// @param state     The state of the targetted block
    /// @implNote The pose stack is already centered in the target block space when this is called
    default void rotateMatrix(PoseStack poseStack, BlockState state) {
        Direction dir = getRotationDir(state);
        Preconditions.checkState(dir.getAxis().isHorizontal(), "Rotation direction must be horizontal");
        poseStack.mulPose(YN_DIR[dir.get2DDataValue()]);
    }

    /// Mirrors the pose stack around the horizontal plane.
    ///
    /// @param pstack The pose stack used for rendering
    /// @param rotY90 Whether the pose stack needs to be rotated -90 degrees around the y-axis,
    ///               needed for un-symmetric shapes like corners
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

    /// Base interface of the line drawer to submit line segments of the block outline to.
    @ApiStatus.NonExtendable
    interface LineDrawer {
        /// Draw a single line segment between the points `(x1,y1,z1)` and `(x2,y2,z2)`.
        ///
        /// @param x1 The X coordinate of the first point
        /// @param y1 The Y coordinate of the first point
        /// @param z1 The Z coordinate of the first point
        /// @param x2 The X coordinate of the second point
        /// @param y2 The Y coordinate of the second point
        /// @param z2 The Z coordinate of the second point
        void drawLine(float x1, float y1, float z1, float x2, float y2, float z2);

        /// Draw all line segments given by the points in the array.
        ///
        /// The given array's size must be a multiple of six and the values must
        /// be in the order `(x1,y1,z1,x2,y2,z2)`.
        ///
        /// @param vertices The vertex positions of the line segments
        void drawLines(float[] vertices);
    }
}
