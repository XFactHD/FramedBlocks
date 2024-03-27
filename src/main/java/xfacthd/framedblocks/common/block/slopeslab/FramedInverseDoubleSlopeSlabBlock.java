package xfacthd.framedblocks.common.block.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slopeslab.FramedInverseDoubleSlopeSlabBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedInverseDoubleSlopeSlabBlock extends AbstractFramedDoubleBlock
{
    public FramedInverseDoubleSlopeSlabBlock()
    {
        super(BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE_SLAB.value().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, false)
                        .setValue(FramedProperties.TOP, true)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, true)
                        .setValue(FramedProperties.TOP, false)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (side == facing && edge == Direction.UP)
        {
            return CamoGetter.SECOND;
        }
        else if (side == facing.getOpposite() && edge == Direction.DOWN)
        {
            return CamoGetter.FIRST;
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return SolidityCheck.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedInverseDoubleSlopeSlabBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }



    private static final ShapeCache<Direction> SHAPES = ShapeCache.createEnum(Direction.class, map ->
    {
        VoxelShape shape = ShapeUtils.orUnoptimized(
                FramedSlopeSlabBlock.SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.SOUTH,
                        FramedSlopeSlabBlock.SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF)
                )
        );
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, SHAPES.get(dir));
        }

        return ShapeProvider.of(builder.build());
    }
}
