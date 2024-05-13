package xfacthd.framedblocks.common.block.cube;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedTubeBlock extends FramedBlock
{
    public FramedTubeBlock()
    {
        super(BlockType.FRAMED_TUBE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AXIS, FramedProperties.SOLID, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withClickedAxis()
                .withWater()
                .build();
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity)
    {
        return state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeY = ShapeUtils.or(
                box( 0, 0,  0, 16, 16,  2),
                box( 0, 0, 14, 16, 16, 16),
                box( 0, 0,  0,  2, 16, 16),
                box(14, 0,  0, 16, 16, 16)
        );
        VoxelShape shapeZ = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.SOUTH, shapeY);
        VoxelShape shapeX = ShapeUtils.rotateShapeAroundZ(Direction.UP, Direction.EAST, shapeY);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X -> shapeX;
                case Y -> shapeY;
                case Z -> shapeZ;
            };
            builder.put(state, shape);
        }

        return ShapeProvider.of(builder.build());
    }
}
