package xfacthd.framedblocks.common.block.slab;

import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleSlabBlockEntity;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedDoubleSlabBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleSlabBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLAB);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return new ItemStack(FBContent.BLOCK_FRAMED_SLAB.get());
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        BlockState defState = FBContent.BLOCK_FRAMED_SLAB.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.TOP, false),
                defState.setValue(FramedProperties.TOP, true)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode getTopInteractionModeRaw(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlabBlockEntity(pos, state);
    }
}