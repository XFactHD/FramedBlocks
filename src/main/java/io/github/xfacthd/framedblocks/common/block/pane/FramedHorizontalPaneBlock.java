package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedHorizontalPaneBlock extends FramedBlock
{
    public FramedHorizontalPaneBlock(Properties props)
    {
        super(BlockType.FRAMED_HORIZONTAL_PANE, props.forceSolidOn());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx).withWater().build();
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }
}
