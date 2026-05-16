package io.github.xfacthd.framedblocks.common.block.prism;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FramedSlopedPrismBlock extends FramedBlock implements PrismBlock, SlopeToggleBlock {
    public FramedSlopedPrismBlock(BlockType type, Properties props) {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FACING_DIR, CompoundDirection.NORTH_UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacement(context, this);
    }

    public static <T extends Block & PrismBlock> @Nullable BlockState getStateForPlacement(BlockPlaceContext context, T block) {
        return PlacementStateBuilder.of(block, context)
                .withCustom((state, modCtx) -> {
                    Direction face = modCtx.getClickedFace();
                    Direction orientation;
                    if (DirUtils.isY(face)) {
                        orientation = modCtx.getHorizontalDirection();
                        if (block.isInnerPrism()) {
                            orientation = orientation.getOpposite();
                        }
                    } else {
                        Vec3 subHit = MathUtils.fraction(modCtx.getClickLocation());

                        double xz = (DirUtils.isX(face) ? subHit.z() : subHit.x()) - .5;
                        double y = subHit.y() - .5;

                        if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz)) {
                            if (DirUtils.isX(face)) {
                                orientation = xz < 0 ? Direction.SOUTH : Direction.NORTH;
                            } else {
                                orientation = xz < 0 ? Direction.EAST : Direction.WEST;
                            }
                        } else {
                            orientation = y < 0 ? Direction.UP : Direction.DOWN;
                        }
                    }
                    return state.setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(face, orientation));
                })
                .withAltSlope(DirUtils.isY(context.getClickedFace()))
                .tryWithWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return rotateWithWrench(state, direction, mode);
    }

    @SuppressWarnings("deprecation")
    public static BlockState rotateWithWrench(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        if (mode == WrenchRotationMode.SECONDARY) {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotateOrientation(direction));
        }
        return state.rotate(direction.toVanillaRotation());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return shouldNotifyBlockEntityOfWrenchRotation(mode, oldState);
    }

    public static TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState) {
        if (mode == WrenchRotationMode.PRIMARY || DirUtils.isY(oldState.getValue(PropertyHolder.FACING_DIR).direction())) {
            return TriState.DEFAULT;
        }
        return TriState.FALSE;
    }

    @Override
    public BlockState getItemModelSource() {
        boolean outer = getBlockType() == BlockType.FRAMED_SLOPED_PRISM;
        CompoundDirection cmpDir = outer ? CompoundDirection.UP_WEST : CompoundDirection.UP_EAST;
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, cmpDir);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return createStateCycleSpec(this);
    }

    static StateCycleSpec createStateCycleSpec(Block block) {
        return StateCycleSpec.builder(block)
                .property(PropertyHolder.FACING_DIR, builder -> builder
                        .values(CompoundDirection.CYCLE_ORDER)
                        .printer(CompoundDirection.PRINTER)
                )
                .postProcessor((state, _) -> {
                    Direction face = state.getValue(PropertyHolder.FACING_DIR).direction();
                    return state.setValue(FramedProperties.ALT_SLOPE, DirUtils.isY(face));
                })
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism() {
        return getBlockType() != BlockType.FRAMED_SLOPED_PRISM;
    }
}
