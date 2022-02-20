package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
public class FramedDoorBlock extends DoorBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
        if (state.get(BlockStateProperties.OPEN))
        {
            if (state.get(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.LEFT)
            {
                return facing.rotateYCCW() == dir;
            }
            else
            {
                return facing.rotateY() == dir;
            }
        }
        else
        {
            return facing.getOpposite() == dir;
        }
    };

    public FramedDoorBlock()
    {
        super(IFramedBlock.createProperties(BlockType.FRAMED_DOOR));
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
        //noinspection ConstantConditions
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        tryApplyCamoImmediately(world, pos, placer, stack);
        tryApplyCamoImmediately(world, pos.up(), placer, stack); //Apply to upper half as well
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
    public BlockType getBlockType() { return BlockType.FRAMED_DOOR; }
}