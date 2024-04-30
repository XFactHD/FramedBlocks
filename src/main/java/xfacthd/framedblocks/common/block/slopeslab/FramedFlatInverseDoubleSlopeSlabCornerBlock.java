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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slopeslab.FramedFlatInverseDoubleSlopeSlabCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedFlatInverseDoubleSlopeSlabCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedFlatInverseDoubleSlopeSlabCornerBlock()
    {
        super(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, BlockStateProperties.WATERLOGGED,
                FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction face = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        if (face == dir.getOpposite() || face == dir.getClockWise())
        {
            Vec3 vec = Utils.fraction(hit.getLocation());
            if ((vec.y > .5) != top)
            {
                face = top ? Direction.DOWN : Direction.UP;
            }
        }
        else if (face == dir || face == dir.getCounterClockWise())
        {
            Vec3 vec = Utils.fraction(hit.getLocation());

            Direction perpDir = face == dir.getClockWise() ? dir : dir.getCounterClockWise();
            double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
            if (!Utils.isPositive(perpDir))
            {
                hor = 1D - hor;
            }

            double y = vec.y();
            if (top)
            {
                y -= .5;
            }
            else
            {
                y = .5 - y;
            }
            if ((y * 2D) >= hor)
            {
                face = top ? Direction.DOWN : Direction.UP;
            }
        }

        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, top)
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, !top)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (state.getValue(FramedProperties.TOP))
        {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            boolean top = state.getValue(FramedProperties.TOP);

            if ((!top && edge == Direction.DOWN) || (top && edge == Direction.UP))
            {
                return CamoGetter.FIRST;
            }
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
        return new FramedFlatInverseDoubleSlopeSlabCornerBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBot = ShapeUtils.orUnoptimized(
                FramedFlatSlopeSlabCornerBlock.SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.SOUTH,
                        FramedFlatSlopeSlabCornerBlock.INNER_SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF)
                )
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                FramedFlatSlopeSlabCornerBlock.SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.SOUTH,
                        FramedFlatSlopeSlabCornerBlock.INNER_SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF)
                )
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
