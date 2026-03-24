package io.github.xfacthd.framedblocks.common.block.prism;

import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedElevatedSlopedPrismBlock extends FramedBlock implements IFramedPrismBlock, SlopeToggleBlock
{
    public FramedElevatedSlopedPrismBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FACING_DIR, CompoundDirection.NORTH_UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedSlopedPrismBlock.getStateForPlacement(context, this);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    public BlockState getItemModelSource()
    {
        boolean inner = getBlockType() == BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM;
        CompoundDirection cmpDir = inner ? CompoundDirection.UP_EAST : CompoundDirection.UP_WEST;
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, cmpDir);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (!DirUtils.isY(cmpDir.direction())) return cmpDir.direction();
        if (!DirUtils.isY(cmpDir.orientation())) return cmpDir.orientation();
        return Direction.NORTH;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism()
    {
        return getBlockType() == BlockType.FRAMED_ELEVATED_INNER_SLOPED_PRISM;
    }
}
