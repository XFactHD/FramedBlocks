package xfacthd.framedblocks.common.block.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.stairs.FramedVerticalStairsBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedThreewayCornerPillarBlock extends FramedBlock
{
    public FramedThreewayCornerPillarBlock()
    {
        super(BlockType.FRAMED_THREEWAY_CORNER_PILLAR);
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
            Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
            return state.setValue(FramedProperties.FACING_HOR, dir);
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

    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_THREEWAY_CORNER_PILLAR.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                FramedVerticalStairsBlock.SHAPES.get(new FramedVerticalStairsBlock.ShapeKey(Direction.NORTH, StairsType.TOP_CORNER)),
                FramedVerticalStairsBlock.SHAPES.get(new FramedVerticalStairsBlock.ShapeKey(Direction.NORTH, StairsType.BOTTOM_CORNER)),
                Direction.NORTH
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
