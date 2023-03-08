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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopedPrismBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlopedPrismBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
            side != state.getValue(PropertyHolder.ORIENTATION);

    public FramedDoubleSlopedPrismBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLOPED_PRISM);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, PropertyHolder.ORIENTATION, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedSlopedPrismBlock.getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot == Rotation.NONE) { return state; }

        Direction dir = state.getValue(BlockStateProperties.FACING);
        Direction orientation = state.getValue(PropertyHolder.ORIENTATION);

        if (Utils.isY(dir))
        {
            return state.setValue(PropertyHolder.ORIENTATION, rot.rotate(orientation));
        }
        else
        {
            if (!Utils.isY(orientation))
            {
                state = state.setValue(PropertyHolder.ORIENTATION, rot.rotate(orientation));
            }
            return state.setValue(BlockStateProperties.FACING, rot.rotate(dir));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction orientation = state.getValue(PropertyHolder.ORIENTATION);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.blockFramedInnerSlopedPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing)
                        .setValue(PropertyHolder.ORIENTATION, orientation)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.blockFramedSlopedPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing.getOpposite())
                        .setValue(PropertyHolder.ORIENTATION, orientation)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopedPrismBlockEntity(pos, state);
    }
}
