package xfacthd.framedblocks.common.block.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.IdentityHashMap;

public class FramedElevatedSlopeSlabBlock extends FramedBlock
{
    public FramedElevatedSlopeSlabBlock()
    {
        super(BlockType.FRAMED_ELEVATED_SLOPE_SLAB);
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
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
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
        if (face == state.getValue(FramedProperties.FACING_HOR).getOpposite())
        {
            double y = Utils.fraction(hit.getLocation()).y;
            boolean top = state.getValue(FramedProperties.TOP);
            if ((y > .5) != top)
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
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }



    public static final ShapeCache<Boolean> SHAPES = new ShapeCache<>(new IdentityHashMap<>(), map ->
    {
        map.put(Boolean.FALSE, ShapeUtils.orUnoptimized(
                FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE).move(0, .5, 0),
                box(0, 0, 0, 16, 8, 16)
        ));

        map.put(Boolean.TRUE, ShapeUtils.orUnoptimized(
                FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE),
                box(0, 8, 0, 16, 16, 16)
        ));
    });

    private record ShapeKey(Direction dir, boolean top) { }

    private static final ShapeCache<ShapeKey> FINAL_SHAPES = new ShapeCache<>(map ->
            ShapeUtils.makeHorizontalRotationsWithFlag(
                    SHAPES.get(Boolean.FALSE),
                    SHAPES.get(Boolean.TRUE),
                    Direction.NORTH,
                    map,
                    ShapeKey::new
            )
    );

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, FINAL_SHAPES.get(new ShapeKey(dir, top)));
        }

        return ShapeProvider.of(builder.build());
    }
}
