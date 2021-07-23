package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedStairsBlock extends StairBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (dir == Direction.UP)
        {
            return state.getValue(BlockStateProperties.HALF) == Half.TOP;
        }
        else if (dir == Direction.DOWN)
        {
            return state.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
        }
        else
        {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            if (shape == StairsShape.STRAIGHT)
            {
                return facing == dir;
            }
            else if (shape == StairsShape.INNER_LEFT)
            {
                return facing == dir || facing.getCounterClockWise() == dir;
            }
            else if (shape == StairsShape.INNER_RIGHT)
            {
                return facing == dir || facing.getClockWise() == dir;
            }
            else
            {
                return false;
            }
        }
    };

    public FramedStairsBlock()
    {
        super(() -> FBContent.blockFramedCube.get().defaultBlockState(), IFramedBlock.createProperties());
    }

    @Override
    public final InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(world, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(world, pos, placer, stack);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) { return getLight(world, pos); }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity)
    {
        return getCamoSound(state, world, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion)
    {
        return getCamoBlastResistance(state, world, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return isCamoFlammable(world, pos, face);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return getCamoFlammability(world, pos, face);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public float getFriction(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, world, pos, entity);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedTileEntity(pos, state); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_STAIRS; }
}