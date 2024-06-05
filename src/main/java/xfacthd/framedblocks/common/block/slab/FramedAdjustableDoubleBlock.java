package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.block.cube.FramedCollapsibleCopycatBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

import java.util.function.Function;

public abstract class FramedAdjustableDoubleBlock extends AbstractFramedDoubleBlock
{
    private final Function<BlockState, Direction> facingGetter;
    private final Function<BlockState, Tuple<BlockState, BlockState>> statePairBuilder;
    private final BlockEntityType.BlockEntitySupplier<FramedAdjustableDoubleBlockEntity> beSupplier;

    protected FramedAdjustableDoubleBlock(
            BlockType type,
            Function<BlockState, Direction> facingGetter,
            Function<BlockState, Tuple<BlockState, BlockState>> statePairBuilder,
            BlockEntityType.BlockEntitySupplier<FramedAdjustableDoubleBlockEntity> beSupplier
    )
    {
        super(type);
        this.facingGetter = facingGetter;
        this.statePairBuilder = statePairBuilder;
        this.beSupplier = beSupplier;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().getItem() == FBContent.ITEM_FRAMED_HAMMER.value())
        {
            if (level.getBlockEntity(pos) instanceof FramedAdjustableDoubleBlockEntity be)
            {
                return be.handleDeform(player);
            }
        }
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return beSupplier.create(pos, state);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        return statePairBuilder.apply(state);
    }

    public Direction getFacing(BlockState state)
    {
        return facingGetter.apply(state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }



    protected static Tuple<BlockState, BlockState> makeStandardStatePair(BlockState state)
    {
        Direction facing = ((FramedAdjustableDoubleBlock) state.getBlock()).getFacing(state);
        BlockState defState = FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value().defaultBlockState();
        return new Tuple<>(
                defState.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.fromDirection(facing)),
                defState.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.fromDirection(facing.getOpposite()))
        );
    }

    protected static Tuple<BlockState, BlockState> makeCopycatStatePair(BlockState state)
    {
        Direction facing = ((FramedAdjustableDoubleBlock) state.getBlock()).getFacing(state);
        BlockState defState = FBContent.BLOCK_FRAMED_COLLAPSIBLE_COPYCAT_BLOCK.value().defaultBlockState();
        int solidFirst = ~(1 << facing.ordinal()) & FramedCollapsibleCopycatBlock.ALL_SOLID;
        int solidSecond = ~(1 << facing.getOpposite().ordinal()) & FramedCollapsibleCopycatBlock.ALL_SOLID;
        return new Tuple<>(
                defState.setValue(PropertyHolder.SOLID_FACES, solidFirst),
                defState.setValue(PropertyHolder.SOLID_FACES, solidSecond)
        );
    }
}
