package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.*;

@SuppressWarnings("deprecation")
public class FramedSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        SlopeType type = Utils.getSlopeType(state);
        if (dir == Direction.UP && type == SlopeType.TOP)
        {
            return true;
        }
        else if (dir == Direction.DOWN && type == SlopeType.BOTTOM)
        {
            return true;
        }
        else if (type == SlopeType.HORIZONTAL)
        {
            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            return dir == facing || dir == facing.getCounterClockWise();
        }
        return Utils.getBlockFacing(state) == dir;
    };

    public FramedSlopeBlock() { super(BlockType.FRAMED_SLOPE); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = withSlopeType(defaultBlockState(), context.getClickedFace(), context.getHorizontalDirection(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() == Items.RAIL)
        {
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction face = hit.getDirection();

            if (type == SlopeType.BOTTOM && (face == dir.getOpposite() || face == Direction.UP))
            {
                Block railSlope = FBContent.blockFramedRailSlope.get();
                BlockState newState = railSlope.defaultBlockState()
                        .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, FramedRailSlopeBlock.shapeFromDirection(dir))
                        .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));

                if (!railSlope.canSurvive(newState, world, pos)) { return ActionResultType.FAIL; }

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

                    world.setBlockAndUpdate(pos, newState);

                    SoundType sound = Blocks.RAIL.getSoundType(Blocks.RAIL.defaultBlockState());
                    world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.setChanged();
                    }

                    te = world.getBlockEntity(pos);
                    if (te instanceof FramedTileEntity)
                    {
                        ((FramedTileEntity) te).setCamo(camoStack, camoState, false);
                        ((FramedTileEntity) te).setGlowing(glowing);
                        ((FramedTileEntity) te).setIntangible(intangible);
                    }
                }

                return ActionResultType.sidedSuccess(world.isClientSide());
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = VoxelShapes.or(
                box(0,  0, 0, 16,  4, 16),
                box(0,  4, 0, 16,  8, 12),
                box(0,  8, 0, 16, 12,  8),
                box(0, 12, 0, 16, 16,  4)
        ).optimize();

        VoxelShape shapeTop = VoxelShapes.or(
                box(0,  0, 0, 16,  4,  4),
                box(0,  4, 0, 16,  8,  8),
                box(0,  8, 0, 16, 12, 12),
                box(0, 12, 0, 16, 16, 16)
        ).optimize();

        VoxelShape shapeHorizontal = VoxelShapes.or(
                box( 0, 0, 0,  4, 16, 16),
                box( 4, 0, 0,  8, 16, 12),
                box( 8, 0, 0, 12, 16,  8),
                box(12, 0, 0, 16, 16,  4)
        ).optimize();

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            SlopeType type = Utils.getSlopeType(state);
            Direction dir = Utils.getBlockFacing(state);

            if (type == SlopeType.BOTTOM)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
            else if (type == SlopeType.TOP)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeHorizontal));
            }
        }

        return builder.build();
    }
}