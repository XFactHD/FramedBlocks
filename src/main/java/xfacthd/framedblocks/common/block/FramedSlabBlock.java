package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

@SuppressWarnings("deprecation")
public class FramedSlabBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            (state.get(PropertyHolder.TOP) && dir == Direction.UP) ||
            (!state.get(PropertyHolder.TOP) && dir == Direction.DOWN);

    public FramedSlabBlock()
    {
        super(BlockType.FRAMED_SLAB);
        setDefaultState(getDefaultState().with(PropertyHolder.TOP, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.TOP, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return withWater(withTop(getDefaultState(), context.getFace(), context.getHitVec()), context.getWorld(), context.getPos());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == FBContent.blockFramedSlab.get().asItem())
        {
            boolean top = state.get(PropertyHolder.TOP);
            Direction face = hit.getFace();
            if ((face == Direction.UP && !top) || (face == Direction.DOWN && top))
            {
                if (!world.isRemote())
                {
                    BlockState camoState = Blocks.AIR.getDefaultState();
                    ItemStack camoStack = ItemStack.EMPTY;
                    boolean glowing = false;
                    boolean intangible = false;

                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof FramedTileEntity)
                    {
                        camoState = ((FramedTileEntity) te).getCamoState();
                        camoStack = ((FramedTileEntity) te).getCamoStack();
                        glowing = ((FramedTileEntity) te).isGlowing();
                        intangible = ((FramedTileEntity) te).isIntangible(null);
                    }

                    world.setBlockState(pos, FBContent.blockFramedDoubleSlab.get().getDefaultState());

                    SoundType sound = FBContent.blockFramedCube.get().getSoundType(FBContent.blockFramedCube.get().getDefaultState());
                    world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.markDirty();
                    }

                    te = world.getTileEntity(pos);
                    if (te instanceof FramedDoubleTileEntity)
                    {
                        ((FramedDoubleTileEntity) te).setCamo(camoStack, camoState, top);
                        ((FramedDoubleTileEntity) te).setGlowing(glowing);
                        ((FramedDoubleTileEntity) te).setIntangible(intangible);
                    }
                }
                return ActionResultType.func_233537_a_(world.isRemote());
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return type == PathType.WATER && world.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape bottomShape = makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        VoxelShape topShape = makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            builder.put(state, state.get(PropertyHolder.TOP) ? topShape : bottomShape);
        }

        return builder.build();
    }
}