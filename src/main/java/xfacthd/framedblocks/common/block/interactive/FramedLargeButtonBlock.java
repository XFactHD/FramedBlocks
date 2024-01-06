package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.Set;

public class FramedLargeButtonBlock extends FramedButtonBlock
{
    public static final StateMerger STATE_MERGER = new LargeButtonStateMerger();

    private static final VoxelShape SHAPE_BOTTOM = box(1, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_BOTTOM_PRESSED = box(1, 0, 1, 15, 1, 15);
    private static final VoxelShape SHAPE_TOP = box(1, 14, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_TOP_PRESSED = box(1, 15, 1, 15, 16, 15);
    private static final VoxelShape[] SHAPES_HORIZONTAL = makeHorizontalShapes();

    private FramedLargeButtonBlock(BlockType type, BlockSetType blockSet, int pressTime)
    {
        super(type, blockSet, pressTime);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return getShape(state);
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return state.getValue(FACE) != AttachFace.WALL;
    }



    public static VoxelShape getShape(BlockState state)
    {
        boolean pressed = state.getValue(POWERED);
        return switch (state.getValue(FACE))
        {
            case FLOOR -> pressed ? SHAPE_BOTTOM_PRESSED : SHAPE_BOTTOM;
            case CEILING -> pressed ? SHAPE_TOP_PRESSED : SHAPE_TOP;
            case WALL ->
            {
                int idx = state.getValue(FACING).get2DDataValue() + (pressed ? 4 : 0);
                yield SHAPES_HORIZONTAL[idx];
            }
        };
    }

    private static VoxelShape[] makeHorizontalShapes()
    {
        VoxelShape shape = box(1, 1, 0, 15, 15, 2);
        VoxelShape shapePressed = box(1, 1, 0, 15, 15, 1);

        return ShapeUtils.makeHorizontalRotationsWithFlag(shape, shapePressed, Direction.SOUTH);
    }

    public static FramedLargeButtonBlock wood()
    {
        return new FramedLargeButtonBlock(
                BlockType.FRAMED_LARGE_BUTTON,
                BlockSetType.OAK,
                30
        );
    }

    public static FramedLargeButtonBlock stone()
    {
        return new FramedLargeButtonBlock(
                BlockType.FRAMED_LARGE_STONE_BUTTON,
                BlockSetType.STONE,
                20
        );
    }



    private static final class LargeButtonStateMerger implements StateMerger
    {
        @Override
        public BlockState apply(BlockState state)
        {
            state = state.setValue(FramedProperties.GLOWING, false).setValue(FramedProperties.PROPAGATES_SKYLIGHT, false);

            AttachFace face = state.getValue(FramedLargeButtonBlock.FACE);
            if (face == AttachFace.WALL)
            {
                return state;
            }

            return state.setValue(FramedLargeButtonBlock.FACING, Direction.NORTH)
                    .setValue(FramedProperties.GLOWING, false);
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block)
        {
            return Set.of(FramedLargeButtonBlock.FACING, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
        }
    }
}
