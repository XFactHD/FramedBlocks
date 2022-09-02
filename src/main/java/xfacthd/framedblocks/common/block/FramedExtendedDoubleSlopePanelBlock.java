package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.blockentity.FramedExtendedDoubleSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedExtendedDoubleSlopePanelBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = CtmPredicate.HOR_DIR_AXIS.or((state, side) ->
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        return side == rot.withFacing(dir).getOpposite();
    });

    public FramedExtendedDoubleSlopePanelBlock() { super(BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();

        Direction side = context.getClickedFace();
        HorizontalRotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = HorizontalRotation.fromWallCross(context.getClickLocation(), side);
        }
        else
        {
            rotation = HorizontalRotation.fromDirection(facing, side);
        }

        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(facing));
        }
        else if (face.getAxis() == facing.getAxis())
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, rotation.rotate(rot));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedExtendedDoubleSlopePanelBlockEntity(pos, state);
    }
}
