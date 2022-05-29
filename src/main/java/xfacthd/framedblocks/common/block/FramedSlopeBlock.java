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
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.FramedUtils;

@SuppressWarnings("deprecation")
public class FramedSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        SlopeType type = FramedUtils.getSlopeType(state);
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
        return FramedUtils.getBlockFacing(state) == dir;
    };

    public FramedSlopeBlock() { super(BlockType.FRAMED_SLOPE); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
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
                        .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, FramedRailSlopeBlock.shapeFromDirection(dir))
                        .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));

                if (!railSlope.canSurvive(newState, level, pos)) { return InteractionResult.FAIL; }

                if (!level.isClientSide())
                {
                    CamoContainer camo = EmptyCamoContainer.EMPTY;
                    boolean glowing = false;
                    boolean intangible = false;

                    if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
                    {
                        camo = be.getCamo();
                        glowing = be.isGlowing();
                        intangible = be.isIntangible(null);
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
                        be.setCamo(camo, false);
                        be.setGlowing(glowing);
                        be.setIntangible(intangible);
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }



    public static final VoxelShape SHAPE_BOTTOM = Shapes.or(
            box(0,    0, 0, 16,   .5,   16),
            box(0,   .5, 0, 16,    4, 15.5),
            box(0,    4, 0, 16,    8,   12),
            box(0,    8, 0, 16,   12,    8),
            box(0,   12, 0, 16, 15.5,    4),
            box(0, 15.5, 0, 16,   16,   .5)
    ).optimize();

    public static final VoxelShape SHAPE_TOP = Shapes.or(
            box(0,    0, 0, 16,   .5,   .5),
            box(0,   .5, 0, 16,    4,    4),
            box(0,    4, 0, 16,    8,    8),
            box(0,    8, 0, 16,   12,   12),
            box(0,   12, 0, 16, 15.5, 15.5),
            box(0, 15.5, 0, 16,   16,   16)
    ).optimize();

    public static final VoxelShape SHAPE_HORIZONTAL = Shapes.or(
            box(   0, 0, 0,   .5, 16,   16),
            box(   0, 0, 0,    4, 16, 15.5),
            box(   4, 0, 0,    8, 16,   12),
            box(   8, 0, 0,   12, 16,    8),
            box(  12, 0, 0, 15.5, 16,    4),
            box(15.5, 0, 0,   16, 16,   .5)
    ).optimize();

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            SlopeType type = FramedUtils.getSlopeType(state);
            Direction dir = FramedUtils.getBlockFacing(state);

            if (type == SlopeType.BOTTOM)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, SHAPE_BOTTOM));
            }
            else if (type == SlopeType.TOP)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, SHAPE_TOP));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, SHAPE_HORIZONTAL));
            }
        }

        return builder.build();
    }
}