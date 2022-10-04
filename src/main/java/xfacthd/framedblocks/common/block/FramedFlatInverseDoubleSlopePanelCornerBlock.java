package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.FramedFlatInverseDoubleSlopePanelCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatInverseDoubleSlopePanelCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedFlatInverseDoubleSlopePanelCornerBlock()
    {
        super(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, BlockStateProperties.WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedFlatSlopePanelCornerBlock.getStateForPlacement(this, false, context);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction face = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(dir);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

        if (face == rotDir.getOpposite() || face == perpRotDir.getOpposite())
        {
            if (Utils.fractionInDir(hit.getLocation(), dir.getOpposite()) > .5)
            {
                face = dir.getOpposite();
            }
        }
        else if (face == rotDir || face == perpRotDir)
        {
            Vec3 vec = Utils.fraction(hit.getLocation());

            double hor = Utils.isX(dir) ? vec.x() : vec.z();
            if (Utils.isPositive(dir))
            {
                hor = 1D - hor;
            }

            Direction perpDir = face == rotDir ? perpRotDir : rotDir;
            double perpHor = Utils.isY(perpDir) ? vec.y() : (Utils.isX(dir) ? vec.z() : vec.x());
            if (perpDir == Direction.DOWN || (!Utils.isY(perpDir) && !Utils.isPositive(perpDir)))
            {
                perpHor = 1F - perpHor;
            }
            if ((hor * 2D) < perpHor)
            {
                face = dir;
            }
        }

        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (face.getAxis() == dir.getAxis())
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, rotation.rotate(rot));
        }

        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) { return rotate(state, Direction.UP, rotation); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatInverseDoubleSlopePanelCornerBlockEntity(pos, state);
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);

            HorizontalRotation frontRot = rot.getOpposite();
            VoxelShape frontShape = Shapes.join(
                    FramedSlopePanelBlock.SHAPES.get(frontRot),
                    FramedSlopePanelBlock.SHAPES.get(frontRot.rotate(Rotation.COUNTERCLOCKWISE_90)),
                    BooleanOp.AND
            ).move(0, 0, .5);

            HorizontalRotation backRot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
            VoxelShape backShape = Shapes.or(
                    FramedSlopePanelBlock.SHAPES.get(backRot),
                    FramedSlopePanelBlock.SHAPES.get(backRot.rotate(Rotation.COUNTERCLOCKWISE_90))
            ).move(0, 0, .5);
            backShape = Utils.rotateShape(Direction.NORTH, Direction.SOUTH, backShape);

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, Shapes.or(frontShape, backShape))
            );
        }

        return builder.build();
    }
}
