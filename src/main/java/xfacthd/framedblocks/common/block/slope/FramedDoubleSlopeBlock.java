package xfacthd.framedblocks.common.block.slope;

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
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.CtmPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.DoubleBlockParticleMode;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

import javax.annotation.Nullable;

public class FramedDoubleSlopeBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return dir != null && !Utils.isY(dir);
        }
        else
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            return (dir != null && Utils.isY(dir)) || dir == facing || dir == facing.getOpposite();
        }
    };

    public FramedDoubleSlopeBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLOPE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, FramedProperties.Y_SLOPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return withSlopeType(
                defaultBlockState(),
                context.getClickedFace(),
                context.getHorizontalDirection(),
                context.getClickLocation()
        );
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return Utils.mirrorFaceBlock(state, mirror);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(PropertyHolder.SLOPE_TYPE, type)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defState.setValue(PropertyHolder.SLOPE_TYPE, type == SlopeType.HORIZONTAL ? type : type.getOpposite())
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopeBlockEntity(pos, state);
    }



    public static DoubleBlockParticleMode particleMode(BlockState state)
    {
        return switch (state.getValue(PropertyHolder.SLOPE_TYPE))
        {
            case BOTTOM -> DoubleBlockParticleMode.SECOND;
            case TOP -> DoubleBlockParticleMode.FIRST;
            case HORIZONTAL -> DoubleBlockParticleMode.EITHER;
        };
    }

    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_DOUBLE_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST)
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
    }
}