package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.EnumMap;

@SuppressWarnings("deprecation")
public class FramedSlopeBlock extends FramedBlock
{
    public FramedSlopeBlock()
    {
        super(BlockType.FRAMED_SLOPE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withSlopeType(
                defaultBlockState(),
                context.getClickedFace(),
                context.getHorizontalDirection(),
                context.getClickLocation()
        );
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && FramedUtils.isRailItem(stack.getItem()))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction face = hit.getDirection();

            if (type == SlopeType.BOTTOM && (face == dir.getOpposite() || face == Direction.UP))
            {
                Block railSlope = FramedUtils.getRailSlopeBlock(stack.getItem());
                BlockState newState = railSlope.defaultBlockState()
                        .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, FramedUtils.getAscendingRailShapeFromDirection(dir))
                        .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));

                if (!railSlope.canSurvive(newState, level, pos)) { return InteractionResult.FAIL; }

                if (!level.isClientSide())
                {
                    Utils.wrapInStateCopy(level, pos, player, stack, false, true, () ->
                            level.setBlockAndUpdate(pos, newState)
                    );

                    SoundType sound = Blocks.RAIL.getSoundType(Blocks.RAIL.defaultBlockState());
                    level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (Utils.isY(face) || (type != SlopeType.HORIZONTAL && face == dir.getOpposite()))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE && face == dir)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return Utils.mirrorFaceBlock(state, mirror);
        }
    }



    public static final ShapeCache<SlopeType> SHAPES = new ShapeCache<>(new EnumMap<>(SlopeType.class), map ->
    {
        map.put(SlopeType.BOTTOM, ShapeUtils.orUnoptimized(
                box(0,    0, 0, 16,   .5,   16),
                box(0,   .5, 0, 16,    4, 15.5),
                box(0,    4, 0, 16,    8,   12),
                box(0,    8, 0, 16,   12,    8),
                box(0,   12, 0, 16, 15.5,    4),
                box(0, 15.5, 0, 16,   16,   .5)
        ));

        map.put(SlopeType.TOP, ShapeUtils.orUnoptimized(
                box(0,    0, 0, 16,   .5,   .5),
                box(0,   .5, 0, 16,    4,    4),
                box(0,    4, 0, 16,    8,    8),
                box(0,    8, 0, 16,   12,   12),
                box(0,   12, 0, 16, 15.5, 15.5),
                box(0, 15.5, 0, 16,   16,   16)
        ));

        map.put(SlopeType.HORIZONTAL, ShapeUtils.orUnoptimized(
                box(   0, 0, 0,   .5, 16,   16),
                box(   0, 0, 0,    4, 16, 15.5),
                box(   4, 0, 0,    8, 16,   12),
                box(   8, 0, 0,   12, 16,    8),
                box(  12, 0, 0, 15.5, 16,    4),
                box(15.5, 0, 0,   16, 16,   .5)
        ));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 3];
        for (SlopeType type : SlopeType.values())
        {
            for (Direction dir : Direction.Plane.HORIZONTAL)
            {
                int idx = dir.get2DDataValue() | (type.ordinal() << 2);
                shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, SHAPES.get(type));
            }
        }

        for (BlockState state : states)
        {
            SlopeType type = FramedUtils.getSlopeType(state);
            Direction dir = FramedUtils.getSlopeBlockFacing(state);
            int idx = dir.get2DDataValue() | (type.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}