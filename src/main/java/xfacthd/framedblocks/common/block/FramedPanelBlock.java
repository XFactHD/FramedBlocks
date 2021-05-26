package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.*;

@SuppressWarnings("deprecation")
public class FramedPanelBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            state.get(PropertyHolder.FACING_HOR) == dir;

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        if (side == dir) { return SideSkipPredicate.CTM.test(world, pos, state, adjState, side); }

        if (adjState.getBlock() instanceof FramedPanelBlock && side != dir.getOpposite())
        {
            return dir == adjState.get(PropertyHolder.FACING_HOR) && SideSkipPredicate.compareState(world, pos, side, dir);
        }

        if (adjState.getBlock() instanceof FramedDoublePanelBlock && side != dir.getOpposite())
        {
            TileEntity te = world.getTileEntity(pos.offset(side));
            if (!(te instanceof FramedDoubleTileEntity)) { return false; }
            FramedDoubleTileEntity tile = (FramedDoubleTileEntity) te;

            Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
            return (dir == adjDir || dir == adjDir.getOpposite()) && SideSkipPredicate.compareState(world, pos, tile.getCamoState(dir), dir);
        }

        if (adjState.getBlock() instanceof FramedCornerPillarBlock)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if ((side == dir.rotateY() && adjDir == dir) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
            return false;
        }

        if (adjState.getBlock() instanceof FramedSlabEdgeBlock)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if (adjDir != dir) { return false; }

            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if ((side == Direction.UP && !adjTop) || (side == Direction.DOWN && adjTop))
            {
                return SideSkipPredicate.compareState(world, pos, side, dir);
            }
        }

        return false;
    };

    public FramedPanelBlock(){ super("framed_panel", BlockType.FRAMED_PANEL); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction face = context.getFace();
        if (face.getAxis().isHorizontal())
        {
            state = state.with(PropertyHolder.FACING_HOR, face.getOpposite());
        }
        else
        {
            state = state.with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing());
        }

        return withWater(state, context.getWorld(), context.getPos());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == FBContent.blockFramedPanel.asItem())
        {
            Direction facing = state.get(PropertyHolder.FACING_HOR);
            if (hit.getFace() == facing.getOpposite())
            {
                if (!world.isRemote())
                {
                    BlockState camoState = Blocks.AIR.getDefaultState();
                    ItemStack camoStack = ItemStack.EMPTY;
                    boolean glowing = false;

                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof FramedTileEntity)
                    {
                        camoState = ((FramedTileEntity) te).getCamoState();
                        camoStack = ((FramedTileEntity) te).getCamoStack();
                        glowing = ((FramedTileEntity) te).isGlowing();
                    }

                    Direction newFacing = (facing == Direction.NORTH || facing == Direction.EAST) ? facing : facing.getOpposite();
                    BlockState newState = FBContent.blockFramedDoublePanel.getDefaultState();
                    world.setBlockState(pos, newState.with(PropertyHolder.FACING_NE, newFacing));

                    SoundType sound = FBContent.blockFramedCube.getSoundType(FBContent.blockFramedCube.getDefaultState());
                    world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.markDirty();
                    }

                    te = world.getTileEntity(pos);
                    if (te instanceof FramedDoubleTileEntity)
                    {
                        ((FramedDoubleTileEntity) te).setCamo(camoStack, camoState, facing != newFacing);
                        ((FramedDoubleTileEntity) te).setGlowing(glowing);
                    }
                }
                return ActionResultType.func_233537_a_(world.isRemote());
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = makeCuboidShape(0, 0, 0, 16, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
        }

        return builder.build();
    }
}