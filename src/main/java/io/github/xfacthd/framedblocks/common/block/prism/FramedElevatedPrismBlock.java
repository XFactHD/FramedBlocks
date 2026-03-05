package io.github.xfacthd.framedblocks.common.block.prism;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedElevatedPrismBlock extends FramedBlock implements IFramedPrismBlock
{
    public FramedElevatedPrismBlock(BlockType type, Properties props)
    {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                PropertyHolder.FACING_AXIS, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedPrismBlock.getStateForPlacement(context, this);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.mirror(mirror));
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        if (!Utils.isY(dirAxis.direction())) return dirAxis.direction();
        return Utils.getHorizontalDirection(dirAxis.axis());
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }

    @Override
    public boolean isInnerPrism()
    {
        return getBlockType() == BlockType.FRAMED_ELEVATED_INNER_PRISM;
    }
}
