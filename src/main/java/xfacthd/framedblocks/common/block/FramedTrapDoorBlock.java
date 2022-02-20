package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedTrapDoorBlock extends TrapDoorBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (state.get(BlockStateProperties.OPEN))
        {
            return state.get(BlockStateProperties.HORIZONTAL_FACING).getOpposite() == dir;
        }
        else if (state.get(BlockStateProperties.HALF) == Half.TOP)
        {
            return dir == Direction.UP;
        }
        return dir == Direction.DOWN;
    };

    public FramedTrapDoorBlock()
    {
        super(IFramedBlock.createProperties(BlockType.FRAMED_TRAPDOOR));
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
    public final boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public final TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_TRAPDOOR; }
}