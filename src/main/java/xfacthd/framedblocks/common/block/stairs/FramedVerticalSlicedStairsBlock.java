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
import xfacthd.framedblocks.api.shapes.CommonShapes;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.blockentity.doubled.FramedVerticalSlicedStairsBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedVerticalSlicedStairsBlock extends AbstractFramedDoubleBlock
{
    public FramedVerticalSlicedStairsBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.RIGHT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing()
                .withRight()
                .build();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && Utils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isZ(dir)))
        {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }
        return state.cycle(PropertyHolder.RIGHT);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedVerticalSlicedStairsBlockEntity(pos, state);
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
        boolean right = state.getValue(PropertyHolder.RIGHT);
        return new Tuple<>(
                FBContent.BLOCK_FRAMED_PANEL.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, right ? dir.getClockWise() : dir.getCounterClockWise()),
                FBContent.BLOCK_FRAMED_CORNER_PILLAR.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, right ? dir : dir.getClockWise())
        );
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dirTwo)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == dir)
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? dir.getClockWise() : dir.getCounterClockWise();
        if (side == dirTwo || (side.getAxis() != dirTwo.getAxis() && edge == dirTwo))
        {
            return CamoGetter.FIRST;
        }
        else if (side == dir && edge == dirTwo.getOpposite())
        {
            return CamoGetter.SECOND;
        }
        else if (side == dirTwo.getOpposite() && edge == dir)
        {
            return CamoGetter.SECOND;
        }
        return CamoGetter.NONE;
    }

    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_VERTICAL_SLICED_STAIRS.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean right = state.getValue(PropertyHolder.RIGHT);
            builder.put(state, CommonShapes.STRAIGHT_VERTICAL_STAIRS.get(right ? dir.getClockWise() : dir));
        }

        return ShapeProvider.of(builder.build());
    }
}
