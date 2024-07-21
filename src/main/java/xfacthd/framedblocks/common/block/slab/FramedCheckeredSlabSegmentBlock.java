package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCheckeredSlabSegmentBlock extends FramedBlock
{
    public FramedCheckeredSlabSegmentBlock()
    {
        super(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.SECOND, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, PropertyHolder.SECOND, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.SECOND, Utils.isX(ctx.getHorizontalDirection())
                ))
                .withWater()
                .build();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        if (Utils.isY(side))
        {
            if (rot != Rotation.NONE && rot != Rotation.CLOCKWISE_180)
            {
                return state.cycle(PropertyHolder.SECOND);
            }
        }
        else
        {
            if (rot != Rotation.NONE)
            {
                return state.cycle(FramedProperties.TOP);
            }
        }
        return super.rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror != Mirror.NONE)
        {
            return state.cycle(PropertyHolder.SECOND);
        }
        return super.mirror(state, mirror);
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
}
