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
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedHalfPillarBlock extends FramedBlock
{
    public FramedHalfPillarBlock(BlockType blockType)
    {
        super(blockType);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetFacing()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        if (rot != Rotation.NONE)
        {
            return state.cycle(BlockStateProperties.FACING);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        return state.setValue(BlockStateProperties.FACING, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state.setValue(BlockStateProperties.FACING, Direction.DOWN);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeNorth = box(4, 4, 0, 12, 12,  8);
        VoxelShape shapeSouth = box(4, 4, 8, 12, 12, 16);
        VoxelShape shapeEast =  box(8, 4, 4, 16, 12, 12);
        VoxelShape shapeWest =  box(0, 4, 4,  8, 12, 12);
        VoxelShape shapeUp =    box(4, 8, 4, 12, 16, 12);
        VoxelShape shapeDown =  box(4, 0, 4, 12,  8, 12);

        for (BlockState state : states)
        {
            builder.put(state, switch (state.getValue(BlockStateProperties.FACING))
            {
                case NORTH -> shapeNorth;
                case EAST -> shapeEast;
                case SOUTH -> shapeSouth;
                case WEST -> shapeWest;
                case UP -> shapeUp;
                case DOWN -> shapeDown;
            });
        }

        return ShapeProvider.of(builder.build());
    }
}