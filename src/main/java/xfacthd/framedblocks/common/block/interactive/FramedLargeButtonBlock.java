package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedLargeButtonBlock extends FramedButtonBlock
{
    private static final VoxelShape SHAPE_BOTTOM = box(1, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_BOTTOM_PRESSED = box(1, 0, 1, 15, 1, 15);
    private static final VoxelShape SHAPE_TOP = box(1, 14, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_TOP_PRESSED = box(1, 15, 1, 15, 16, 15);
    private static final VoxelShape[] SHAPES_HORIZONTAL = makeHorizontalShapes();

    private FramedLargeButtonBlock(BlockType type, int pressTime, boolean arrowsCanPress, SoundEvent soundOff, SoundEvent soundOn)
    {
        super(type, pressTime, arrowsCanPress, soundOff, soundOn);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return getShape(state);
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (state.getValue(FACE) != AttachFace.WALL)
        {
            return super.getBeaconColorMultiplier(state, level, pos, beaconPos);
        }
        return null;
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

        VoxelShape[] shapes = new VoxelShape[8];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            shapes[dir.get2DDataValue()] = Utils.rotateShape(Direction.SOUTH, dir, shape);
            shapes[dir.get2DDataValue() + 4] = Utils.rotateShape(Direction.SOUTH, dir, shapePressed);
        }
        return shapes;
    }

    public static FramedLargeButtonBlock wood()
    {
        return new FramedLargeButtonBlock(
                BlockType.FRAMED_LARGE_BUTTON,
                30,
                true,
                SoundEvents.WOODEN_BUTTON_CLICK_OFF,
                SoundEvents.WOODEN_BUTTON_CLICK_ON
        );
    }

    public static FramedLargeButtonBlock stone()
    {
        return new FramedLargeButtonBlock(
                BlockType.FRAMED_LARGE_STONE_BUTTON,
                20,
                false,
                SoundEvents.STONE_BUTTON_CLICK_OFF,
                SoundEvents.STONE_BUTTON_CLICK_ON
        );
    }
}
