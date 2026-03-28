package io.github.xfacthd.framedblocks.common.block.prism;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FramedPrismBlock extends FramedBlock implements PrismBlock, SlopeToggleBlock {
    public FramedPrismBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_AXIS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacement(context, this);
    }

    public static @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx, Block block) {
        return PlacementStateBuilder.of(block, ctx)
                .withCustom((state, modCtx) -> {
                    Direction face = modCtx.getClickedFace();
                    Direction.Axis axis = modCtx.getHorizontalDirection().getAxis();
                    if (!DirUtils.isY(face)) {
                        Vec3 subHit = MathUtils.fraction(modCtx.getClickLocation());

                        double xz = (DirUtils.isX(face) ? subHit.z() : subHit.x()) - .5;
                        double y = subHit.y() - .5;

                        if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz)) {
                            axis = face.getClockWise().getAxis();
                        } else {
                            axis = Direction.Axis.Y;
                        }
                    }
                    return state.setValue(PropertyHolder.FACING_AXIS, DirectionAxis.of(face, axis));
                })
                .withAltSlope(DirUtils.isY(ctx.getClickedFace()))
                .tryWithWater()
                .build();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.mirror(mirror));
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        if (!DirUtils.isY(dirAxis.direction())) {
            return dirAxis.direction();
        }
        return DirUtils.getHorizontalDirection(dirAxis.axis());
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism() {
        return getBlockType() != BlockType.FRAMED_PRISM;
    }
}
