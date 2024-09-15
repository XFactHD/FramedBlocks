package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDividedPanelBlock extends AbstractFramedDoubleBlock
{
    public FramedDividedPanelBlock(BlockType type)
    {
        super(type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withWater()
                .build();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rotation.rotate(dir));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (getBlockType() == BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
        {
            BlockState defState = FBContent.BLOCK_FRAMED_SLAB_EDGE.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir);

            return new Tuple<>(defState, defState.setValue(FramedProperties.TOP, true));
        }
        else
        {
            BlockState defState = FBContent.BLOCK_FRAMED_CORNER_PILLAR.value().defaultBlockState();
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, dir),
                    defState.setValue(FramedProperties.FACING_HOR, dir.getClockWise())
            );
        }
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (getBlockType() == BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL)
        {
            return DoubleBlockTopInteractionMode.SECOND;
        }
        else
        {
            return DoubleBlockTopInteractionMode.EITHER;
        }
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (edge == null)
        {
            return CamoGetter.NONE;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean vertical = state.getBlock() == FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.value();
        if (edge == facing)
        {
            if ((!vertical && side == Direction.DOWN) || (vertical && side == facing.getCounterClockWise()))
            {
                return CamoGetter.FIRST;
            }
            if ((!vertical && side == Direction.UP) || (vertical && side == facing.getClockWise()))
            {
                return CamoGetter.SECOND;
            }
        }
        else if (side == facing)
        {
            if ((!vertical && edge == Direction.DOWN) || (vertical && edge == facing.getCounterClockWise()))
            {
                return CamoGetter.FIRST;
            }
            if ((!vertical && edge == Direction.UP) || (vertical && edge == facing.getClockWise()))
            {
                return CamoGetter.SECOND;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        if (side == state.getValue(FramedProperties.FACING_HOR))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
