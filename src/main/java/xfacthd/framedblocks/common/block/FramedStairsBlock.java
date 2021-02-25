package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.*;
import net.minecraft.world.storage.loot.LootContext;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import java.util.List;
import java.util.function.BiPredicate;

@SuppressWarnings("deprecation")
public class FramedStairsBlock extends StairsBlock implements IFramedBlock
{
    public static final BiPredicate<BlockState, Direction> CTM_PREDICATE = (state, dir) ->
    {
        if (dir == Direction.UP)
        {
            return state.get(BlockStateProperties.HALF) == Half.TOP;
        }
        else if (dir == Direction.DOWN)
        {
            return state.get(BlockStateProperties.HALF) == Half.BOTTOM;
        }
        return state.get(BlockStateProperties.STAIRS_SHAPE) == StairsShape.STRAIGHT &&
               state.get(BlockStateProperties.HORIZONTAL_FACING) == dir;
    };

    public FramedStairsBlock()
    {
        super(() -> FBContent.blockFramedCube.getDefaultState(), IFramedBlock.createProperties());
        setRegistryName(FramedBlocks.MODID, "framed_stairs");
    }

    @Override
    public final ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleBlockActivated(world, pos, player, hand);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) { return getLight(world, pos); }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
    {
        return getSound(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return IFramedBlock.super.getDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public final boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public final TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_STAIRS; }
}