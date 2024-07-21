package xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slopepanel.ExtendedSlopePanelShapes;

public final class ExtendedCornerSlopePanelShapes implements SplitShapeGenerator
{
    public static final ExtendedCornerSlopePanelShapes OUTER = new ExtendedCornerSlopePanelShapes(BooleanOp.AND, Direction.NORTH);
    public static final ExtendedCornerSlopePanelShapes INNER = new ExtendedCornerSlopePanelShapes(BooleanOp.OR, Direction.SOUTH);

    private final BooleanOp joinOp;
    private final Direction srcDir;

    private ExtendedCornerSlopePanelShapes(BooleanOp joinOp, Direction srcDir)
    {
        this.joinOp = joinOp;
        this.srcDir = srcDir;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, ExtendedSlopePanelShapes.SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, ExtendedSlopePanelShapes.OCCLUSION_SHAPES);
    }

    private ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<HorizontalRotation> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape bottomSlopeShape = cache.get(HorizontalRotation.UP);
        VoxelShape bottomShape = Shapes.joinUnoptimized(
                bottomSlopeShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, bottomSlopeShape),
                joinOp
        );

        VoxelShape topSlopeShape = cache.get(HorizontalRotation.DOWN);
        VoxelShape topShape = Shapes.joinUnoptimized(
                topSlopeShape,
                ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, topSlopeShape),
                joinOp
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(bottomShape, topShape, srcDir);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
