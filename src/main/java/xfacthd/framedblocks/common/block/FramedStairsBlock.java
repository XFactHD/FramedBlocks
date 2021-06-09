package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

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

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }

        Direction dir = state.get(FACING);
        StairsShape shape = state.get(SHAPE);
        boolean top = state.get(HALF) == Half.TOP;

        if (adjState.getBlock() == FBContent.blockFramedStairs)
        {
            Direction adjDir = adjState.get(FACING);
            StairsShape adjShape = adjState.get(SHAPE);
            if ((isStairSide(shape, dir, side) && isStairSide(adjShape, adjDir, side.getOpposite())) ||
                (isSlabSide(shape, dir, side) && isSlabSide(adjShape, adjDir, side.getOpposite()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlab)
        {
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if (top != adjTop) { return false; }
            if (!isSlabSide(shape, dir, side)) { return false; }

            return SideSkipPredicate.compareState(world, pos, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if (top != adjTop) { return false; }

            if (adjDir == side.getOpposite())
            {
                if (!isSlabSide(shape, dir, side)) { return false; }
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if ((top && side == Direction.DOWN) || (!top && side == Direction.UP))
            {
                if (shape != StairsShape.STRAIGHT) { return false; }
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedDoubleSlab)
        {
            if (!isSlabSide(shape, dir, side)) { return false; }

            return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
        }

        if (adjState.getBlock() == FBContent.blockFramedPanel)
        {
            if (shape != StairsShape.STRAIGHT) { return false; }
            if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel)
        {
            if (shape != StairsShape.STRAIGHT) { return false; }
            if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

            Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
            if (dir == adjDir || dir.getOpposite() == adjDir)
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar)
        {
            if (shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT) { return false; }
            if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if ((shape == StairsShape.OUTER_LEFT && dir == adjDir) || (shape == StairsShape.OUTER_RIGHT && dir.rotateY() == adjDir))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner)
        {
            if (shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT) { return false; }
            if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if ((shape == StairsShape.OUTER_LEFT && dir == adjDir) || (shape == StairsShape.OUTER_RIGHT && dir.rotateY() == adjDir))
            {
                return adjTop == top && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        return false;
    };

    public static boolean isStairSide(StairsShape shape, Direction dir, Direction side)
    {
        if (shape == StairsShape.STRAIGHT) { return side == dir.rotateY() || side == dir.rotateYCCW(); }

        if (shape == StairsShape.INNER_LEFT) { return side == dir.getOpposite() || side == dir.rotateY(); }
        if (shape == StairsShape.INNER_RIGHT) { return side == dir.getOpposite() || side == dir.rotateYCCW(); }

        if (shape == StairsShape.OUTER_LEFT)  { return side == dir || side == dir.rotateYCCW(); }
        if (shape == StairsShape.OUTER_RIGHT) { return side == dir || side == dir.rotateY(); }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isSlabSide(StairsShape shape, Direction dir, Direction side)
    {
        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT) { return false; }

        if (shape == StairsShape.STRAIGHT) { return side == dir.getOpposite(); }

        if (shape == StairsShape.OUTER_LEFT)  { return side == dir.getOpposite() || side == dir.rotateY(); }
        if (shape == StairsShape.OUTER_RIGHT) { return side == dir.getOpposite() || side == dir.rotateYCCW(); }

        return false;
    }

    public FramedStairsBlock()
    {
        super(() -> FBContent.blockFramedCube.getDefaultState(), IFramedBlock.createProperties());
        setRegistryName(FramedBlocks.MODID, "framed_stairs");
    }

    @Override
    public final ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleBlockActivated(world, pos, player, hand, hit);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) { return getLight(world, pos); }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
    {
        return getSound(state, world, pos);
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
    public BlockType getBlockType() { return BlockType.FRAMED_STAIRS; }
}