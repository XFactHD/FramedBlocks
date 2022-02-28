package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.*;

@SuppressWarnings("deprecation")
public class FramedPanelBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) -> state.getValue(PropertyHolder.FACING_HOR) == dir;

    public FramedPanelBlock(){ super(BlockType.FRAMED_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        if (face.getAxis().isHorizontal())
        {
            state = state.setValue(PropertyHolder.FACING_HOR, face.getOpposite());
        }
        else
        {
            state = state.setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection());
        }

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == FBContent.blockFramedPanel.get().asItem())
        {
            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            if (hit.getDirection() == facing.getOpposite())
            {
                if (!world.isClientSide())
                {
                    BlockState camoState = Blocks.AIR.defaultBlockState();
                    ItemStack camoStack = ItemStack.EMPTY;
                    boolean glowing = false;
                    boolean intangible = false;

                    TileEntity te = world.getBlockEntity(pos);
                    if (te instanceof FramedTileEntity)
                    {
                        camoState = ((FramedTileEntity) te).getCamoState();
                        camoStack = ((FramedTileEntity) te).getCamoStack();
                        glowing = ((FramedTileEntity) te).isGlowing();
                        intangible = ((FramedTileEntity) te).isIntangible(null);
                    }

                    Direction newFacing = (facing == Direction.NORTH || facing == Direction.EAST) ? facing : facing.getOpposite();
                    BlockState newState = FBContent.blockFramedDoublePanel.get().defaultBlockState();
                    world.setBlockAndUpdate(pos, newState.setValue(PropertyHolder.FACING_NE, newFacing));

                    SoundType sound = FBContent.blockFramedCube.get().getSoundType(FBContent.blockFramedCube.get().defaultBlockState());
                    world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.setChanged();
                    }

                    te = world.getBlockEntity(pos);
                    if (te instanceof FramedDoubleTileEntity)
                    {
                        ((FramedDoubleTileEntity) te).setCamo(camoStack, camoState, facing != newFacing);
                        ((FramedDoubleTileEntity) te).setGlowing(glowing);
                        ((FramedDoubleTileEntity) te).setIntangible(intangible);
                    }
                }
                return ActionResultType.sidedSuccess(world.isClientSide());
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = box(0, 0, 0, 16, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
        }

        return builder.build();
    }
}