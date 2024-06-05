package xfacthd.framedblocks.common.block.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCheckeredCubeSegmentBlock extends FramedBlock
{
    public FramedCheckeredCubeSegmentBlock()
    {
        super(BlockType.FRAMED_CHECKERED_CUBE_SEGMENT);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.SECOND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.SECOND, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.SECOND, Utils.isX(ctx.getHorizontalDirection())
                ))
                .withWater()
                .build();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot != Rotation.NONE && rot != Rotation.CLOCKWISE_180)
        {
            return state.cycle(PropertyHolder.SECOND);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror != Mirror.NONE)
        {
            return state.cycle(PropertyHolder.SECOND);
        }
        return state;
    }

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeFirst = ShapeUtils.or(
                box(0, 0, 0,  8,  8,  8),
                box(8, 0, 8, 16,  8, 16),
                box(8, 8, 0, 16, 16,  8),
                box(0, 8, 8,  8, 16, 16)
        );
        VoxelShape shapeSecond = ShapeUtils.rotateShapeAroundY(Direction.NORTH, Direction.EAST, shapeFirst);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            boolean second = state.getValue(PropertyHolder.SECOND);
            builder.put(state, second ? shapeSecond : shapeFirst);
        }
        return ShapeProvider.of(builder.build());
    }
}
