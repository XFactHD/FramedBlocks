package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedPaneBlock extends PaneBlock implements IFramedBlock
{
    private final BlockType type;

    public FramedPaneBlock(BlockType type)
    {
        super(IFramedBlock.createProperties(type));
        this.type = type;
    }

    @Override
    public final ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ActionResultType result = handleBlockActivated(world, pos, player, hand, hit);
        if (result.isSuccessOrConsume()) { return result; }

        return super.onBlockActivated(state, world, pos, player, hand, hit);
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
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        return isCamoFlammable(world, pos, face);
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        return getCamoFlammability(world, pos, face);
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
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        if (isIntangible(state, world, pos, ctx)) { return VoxelShapes.empty(); }
        return super.getShape(state, world, pos, ctx);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        if (isIntangible(state, world, pos, ctx)) { return VoxelShapes.empty(); }
        return super.getCollisionShape(state, world, pos, ctx);
    }

    @Override //The pane handles this through the SideSkipPredicate instead
    public boolean isSideInvisible(BlockState state, BlockState adjacentState, Direction side)
    {
        return this == FBContent.blockFramedBars.get() && super.isSideInvisible(state, adjacentState, side);
    }

    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager)
    {
        return IFramedBlock.suppressParticles(state, world, ((BlockRayTraceResult) target).getPos());
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager)
    {
        return IFramedBlock.suppressParticles(state, world, pos);
    }

    @Override
    public final boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public final TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public BlockType getBlockType() { return type; }
}