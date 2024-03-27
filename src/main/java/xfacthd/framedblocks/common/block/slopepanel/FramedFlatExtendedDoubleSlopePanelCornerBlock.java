package xfacthd.framedblocks.common.block.slopepanel;

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
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.slopepanel.FramedFlatExtendedDoubleSlopePanelCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedFlatExtendedDoubleSlopePanelCornerBlock extends AbstractFramedDoubleBlock
{
    private final boolean isInner;

    public FramedFlatExtendedDoubleSlopePanelCornerBlock(BlockType blockType)
    {
        super(blockType);
        this.isInner = blockType == BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER;
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedFlatSlopePanelCornerBlock.getStateForPlacement(this, false, context);
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
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedFlatSlopePanelCornerBlock.mirrorCorner(state, mirror);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation backRot = rotation.rotate(rotation.isVertical() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        if (getBlockType() == BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER)
        {
            return new Tuple<>(
                    FBContent.BLOCK_FRAMED_FLAT_EXTENDED_INNER_SLOPE_PANEL_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(PropertyHolder.ROTATION, rotation)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                            .setValue(PropertyHolder.ROTATION, backRot)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
        }
        else
        {
            return new Tuple<>(
                    FBContent.BLOCK_FRAMED_FLAT_EXTENDED_SLOPE_PANEL_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(PropertyHolder.ROTATION, rotation)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                            .setValue(PropertyHolder.ROTATION, backRot)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
        }
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (getBlockType() == BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER)
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            if (rotation == HorizontalRotation.UP || rotation == HorizontalRotation.RIGHT)
            {
                return DoubleBlockTopInteractionMode.FIRST;
            }
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return CamoGetter.FIRST;
        }
        if (side == facing.getOpposite())
        {
            return CamoGetter.SECOND;
        }

        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(facing);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        if (isInner && (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()))
        {
            return CamoGetter.FIRST;
        }
        else if (side.getAxis() != facing.getAxis())
        {
            if (edge == facing)
            {
                return CamoGetter.FIRST;
            }
            else if (edge == facing.getOpposite())
            {
                return CamoGetter.SECOND;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (side == facing)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == facing.getOpposite())
        {
            return SolidityCheck.SECOND;
        }

        if (isInner)
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            Direction rotDir = rotation.withFacing(facing);
            Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
            {
                return SolidityCheck.FIRST;
            }
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatExtendedDoubleSlopePanelCornerBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
