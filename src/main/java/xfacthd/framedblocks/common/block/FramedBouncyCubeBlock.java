package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedBouncyCubeBlock extends SlimeBlock implements IFramedBlock
{
    public FramedBouncyCubeBlock() { super(IFramedBlock.createProperties()); }

    @Override
    public final ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleBlockActivated(world, pos, player, hand, hit);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(world, pos, placer, stack);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) { return getLight(world, pos); }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
    {
        return getSound(state, world, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {
        return getCamoBlastResistance(state, world, pos, explosion);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, world, pos, entity);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_BOUNCY_CUBE; }
}