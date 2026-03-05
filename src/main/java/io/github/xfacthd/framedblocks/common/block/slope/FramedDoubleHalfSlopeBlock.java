package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slope.FramedDoubleHalfSlopeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.item.block.VerticalAndWallBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedDoubleHalfSlopeBlock extends FramedDoubleBlock
{
    public FramedDoubleHalfSlopeBlock(Properties props)
    {
        super(BlockType.FRAMED_DOUBLE_HALF_SLOPE, props);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.RIGHT, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED,
                FramedProperties.Y_SLOPE
        );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withRight()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(PropertyHolder.RIGHT);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE) { return state; }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && Utils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isZ(dir)))
        {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }
        return state.cycle(PropertyHolder.RIGHT);
    }

    @Override
    public BlockItem createBlockItem(Item.Properties props)
    {
        return new VerticalAndWallBlockItem(
                this,
                FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.value(),
                props
        );
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_HALF_SLOPE.value().defaultBlockState();
        return new DoubleBlockParts(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, false)
                        .setValue(PropertyHolder.RIGHT, right)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, true)
                        .setValue(PropertyHolder.RIGHT, !right)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? facing.getClockWise() : facing.getCounterClockWise();
        if (edge == dirTwo)
        {
            if (side == facing || side == Direction.DOWN)
            {
                return CamoGetter.FIRST;
            }
            if (side == facing.getOpposite() || side == Direction.UP)
            {
                return CamoGetter.SECOND;
            }
        }
        else if (side == dirTwo)
        {
            if (edge == facing || edge == Direction.DOWN)
            {
                return CamoGetter.FIRST;
            }
            if (edge == facing.getOpposite() || edge == Direction.UP)
            {
                return CamoGetter.SECOND;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        if ((!right && side == facing.getCounterClockWise()) || (right && side == facing.getClockWise()))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleHalfSlopeBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
