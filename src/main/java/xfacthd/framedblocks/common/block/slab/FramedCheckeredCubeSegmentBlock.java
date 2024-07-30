package xfacthd.framedblocks.common.block.slab;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

@SuppressWarnings("deprecation")
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
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot != Rotation.NONE && rot != Rotation.CLOCKWISE_180)
        {
            return state.cycle(PropertyHolder.SECOND);
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror != Mirror.NONE)
        {
            return state.cycle(PropertyHolder.SECOND);
        }
        return state;
    }
}
