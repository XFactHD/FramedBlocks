package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedStairsBlock extends StairsBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (dir == Direction.UP)
        {
            return state.get(BlockStateProperties.HALF) == Half.TOP;
        }
        else if (dir == Direction.DOWN)
        {
            return state.get(BlockStateProperties.HALF) == Half.BOTTOM;
        }
        else
        {
            Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape shape = state.get(BlockStateProperties.STAIRS_SHAPE);
            if (shape == StairsShape.STRAIGHT)
            {
                return facing == dir;
            }
            else if (shape == StairsShape.INNER_LEFT)
            {
                return facing == dir || facing.rotateYCCW() == dir;
            }
            else if (shape == StairsShape.INNER_RIGHT)
            {
                return facing == dir || facing.rotateY() == dir;
            }
            else
            {
                return false;
            }
        }
    };

    public FramedStairsBlock()
    {
        super(() -> FBContent.blockFramedCube.get().getDefaultState(), IFramedBlock.createProperties(BlockType.FRAMED_STAIRS));
        setDefaultState(getDefaultState().with(PropertyHolder.SOLID, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(PropertyHolder.SOLID);
    }

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
    public boolean isTransparent(BlockState state) { return state.get(PropertyHolder.SOLID); }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos)
    {
        return getCamoOcclusionShape(state, world, pos);
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return getCamoVisualShape(state, world, pos, ctx);
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
    public BlockType getBlockType() { return BlockType.FRAMED_STAIRS; }
}