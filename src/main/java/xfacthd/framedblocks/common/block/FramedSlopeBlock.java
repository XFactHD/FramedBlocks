package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

@SuppressWarnings("deprecation")
public class FramedSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
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
        return state.getValue(PropertyHolder.FACING_HOR) == dir;
    };

    public FramedSlopeBlock() { super(BlockType.FRAMED_SLOPE); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withSlopeType(defaultBlockState(), context.getClickedFace(), context.getHorizontalDirection(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
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
                        .setValue(PropertyHolder.FACING_HOR, dir)
                        .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, FramedRailSlopeBlock.shapeFromDirection(dir))
                        .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));

                if (!railSlope.canSurvive(newState, level, pos)) { return InteractionResult.FAIL; }

                if (!level.isClientSide())
                {
                    BlockState camoState = Blocks.AIR.defaultBlockState();
                    ItemStack camoStack = ItemStack.EMPTY;
                    boolean glowing = false;

                    if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
                    {
                        camoState = be.getCamoState();
                        camoStack = be.getCamoStack();
                        glowing = be.isGlowing();
                    }

                    level.setBlockAndUpdate(pos, newState);

                    SoundType sound = Blocks.RAIL.getSoundType(Blocks.RAIL.defaultBlockState());
                    level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                        player.getInventory().setChanged();
                    }

                    if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
                    {
                        be.setCamo(camoStack, camoState, false);
                        be.setGlowing(glowing);
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = Shapes.or(
                box(0,  0, 0, 16,  4, 16),
                box(0,  4, 0, 16,  8, 12),
                box(0,  8, 0, 16, 12,  8),
                box(0, 12, 0, 16, 16,  4)
        ).optimize();

        VoxelShape shapeTop = Shapes.or(
                box(0,  0, 0, 16,  4,  4),
                box(0,  4, 0, 16,  8,  8),
                box(0,  8, 0, 16, 12, 12),
                box(0, 12, 0, 16, 16, 16)
        ).optimize();

        VoxelShape shapeHorizontal = Shapes.or(
                box( 0, 0, 0,  4, 16, 16),
                box( 4, 0, 0,  8, 16, 12),
                box( 8, 0, 0, 12, 16,  8),
                box(12, 0, 0, 16, 16,  4)
        ).optimize();

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

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