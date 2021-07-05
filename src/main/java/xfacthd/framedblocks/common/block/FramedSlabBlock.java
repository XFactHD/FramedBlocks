package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
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
import xfacthd.framedblocks.common.util.CtmPredicate;

@SuppressWarnings("deprecation")
public class FramedSlabBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            (state.getValue(PropertyHolder.TOP) && dir == Direction.UP) ||
            (!state.getValue(PropertyHolder.TOP) && dir == Direction.DOWN);

    public FramedSlabBlock()
    {
        super(BlockType.FRAMED_SLAB);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return withWater(withTop(defaultBlockState(), context.getClickedFace(), context.getClickLocation()), context.getLevel(), context.getClickedPos());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == FBContent.blockFramedSlab.get().asItem())
        {
            boolean top = state.getValue(PropertyHolder.TOP);
            Direction face = hit.getDirection();
            if ((face == Direction.UP && !top) || (face == Direction.DOWN && top))
            {
                if (!world.isClientSide())
                {
                    BlockState camoState = Blocks.AIR.defaultBlockState();
                    ItemStack camoStack = ItemStack.EMPTY;
                    boolean glowing = false;

                    if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
                    {
                        camoState = te.getCamoState();
                        camoStack = te.getCamoStack();
                        glowing = te.isGlowing();
                    }

                    world.setBlockAndUpdate(pos, FBContent.blockFramedDoubleSlab.get().defaultBlockState());

                    SoundType sound = FBContent.blockFramedCube.get().getSoundType(FBContent.blockFramedCube.get().defaultBlockState());
                    world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.setChanged();
                    }

                    if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity te)
                    {
                        te.setCamo(camoStack, camoState, top);
                        te.setGlowing(glowing);
                    }
                }
                return ActionResultType.sidedSuccess(world.isClientSide());
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape bottomShape = box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        VoxelShape topShape = box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            builder.put(state, state.getValue(PropertyHolder.TOP) ? topShape : bottomShape);
        }

        return builder.build();
    }
}