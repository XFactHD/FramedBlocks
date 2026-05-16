package io.github.xfacthd.framedblocks.common.block.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedHalfStairsBlock extends FramedBlock {
    public FramedHalfStairsBlock(Properties props) {
        super(BlockType.FRAMED_HALF_STAIRS, props);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.RIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.RIGHT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .withTop()
                .withRight()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return switch (mode) {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(switch (direction) {
                case CLOCKWISE -> FramedProperties.TOP;
                case COUNTERCLOCKWISE -> PropertyHolder.RIGHT;
            });
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && DirUtils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && DirUtils.isZ(dir))) {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }
        return state.cycle(PropertyHolder.RIGHT);
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return mode.getDefaultNotifyBlockEntity();
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.RIGHT, true);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return createStateCycleSpec(this);
    }

    static StateCycleSpec createStateCycleSpec(Block block) {
        return StateCycleSpec.builder(block)
                .property(FramedProperties.TOP)
                .property(FramedProperties.FACING_HOR)
                .property(PropertyHolder.RIGHT)
                .propertyPrinter(FramedProperties.FACING_HOR, PropertyLabels.FACING)
                .propertyPrinter(FramedProperties.TOP, PropertyLabels.HALF, ValuePrinters.HALF_BOOL)
                .propertyPrinter(PropertyHolder.RIGHT, PropertyHolder.Labels.RIGHT)
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
