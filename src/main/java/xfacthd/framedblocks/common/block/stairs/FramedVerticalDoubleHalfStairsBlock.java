package xfacthd.framedblocks.common.block.stairs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.CommonShapes;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.stairs.FramedVerticalDoubleHalfStairsBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;

public class FramedVerticalDoubleHalfStairsBlock extends AbstractFramedDoubleBlock
{
    public FramedVerticalDoubleHalfStairsBlock()
    {
        super(BlockType.FRAMED_VERTICAL_DOUBLE_HALF_STAIRS);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedVerticalDoubleHalfStairsBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        return new Tuple<>(
                FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(FramedProperties.TOP, top),
                FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                        .setValue(FramedProperties.TOP, top)
        );
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (edge == dirTwo && (side == dir || side == dir.getCounterClockWise()))
        {
            return CamoGetter.FIRST;
        }
        else if (side == dirTwo && (edge == dir || edge == dir.getCounterClockWise()))
        {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            builder.put(state, CommonShapes.SLAB.get(state.getValue(FramedProperties.TOP)));
        }

        return ShapeProvider.of(builder.build());
    }
}
