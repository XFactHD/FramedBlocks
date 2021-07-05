package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedDoorBlock extends DoorBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (state.getValue(BlockStateProperties.OPEN))
        {
            if (state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.LEFT)
            {
                return facing.getCounterClockWise() == dir;
            }
            else
            {
                return facing.getClockWise() == dir;
            }
        }
        else
        {
            return facing.getOpposite() == dir;
        }
    };

    public FramedDoorBlock()
    {
        super(IFramedBlock.createProperties());
    }

    @Override
    public final ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ActionResultType result = handleUse(world, pos, player, hand, hit);
        if (result.consumesAction()) { return result; }

        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        //noinspection ConstantConditions
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        tryApplyCamoImmediately(world, pos, placer, stack);
        tryApplyCamoImmediately(world, pos.up(), placer, stack); //Apply to upper half as well
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
    public BlockType getBlockType() { return BlockType.FRAMED_DOOR; }
}