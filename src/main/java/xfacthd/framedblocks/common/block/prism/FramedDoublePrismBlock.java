package xfacthd.framedblocks.common.block.prism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoublePrismBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedDoublePrismBlock extends AbstractFramedDoubleBlock
{
    public FramedDoublePrismBlock()
    {
        super(BlockType.FRAMED_DOUBLE_PRISM);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_AXIS, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedPrismBlock.getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().is(Utils.WRENCH))
        {
            level.setBlockAndUpdate(pos, state.setValue(FramedProperties.Y_SLOPE, !state.getValue(FramedProperties.Y_SLOPE)));
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.rotate(rot));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return state.setValue(PropertyHolder.FACING_AXIS, dirAxis.mirror(mirror));
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_INNER_PRISM.get()
                        .defaultBlockState()
                        .setValue(PropertyHolder.FACING_AXIS, dirAxis)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_PRISM.get()
                        .defaultBlockState()
                        .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.of(
                                dirAxis.direction().getOpposite(),
                                dirAxis.axis()
                        ))
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode getTopInteractionModeRaw(BlockState state)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        if (dirAxis.direction() == Direction.UP)
        {
            return DoubleBlockTopInteractionMode.SECOND;
        }
        else if (dirAxis.direction() == Direction.DOWN || dirAxis.axis() != Direction.Axis.Y)
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoublePrismBlockEntity(pos, state);
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_DOUBLE_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
    }
}
