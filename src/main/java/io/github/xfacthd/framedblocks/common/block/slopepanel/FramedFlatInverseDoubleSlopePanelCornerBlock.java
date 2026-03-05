package io.github.xfacthd.framedblocks.common.block.slopepanel;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedFlatInverseDoubleSlopePanelCornerBlock extends FramedDoubleBlock
{
    public FramedFlatInverseDoubleSlopePanelCornerBlock(Properties props)
    {
        super(BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION, BlockStateProperties.WATERLOGGED,
                FramedProperties.Y_SLOPE
        );
    }

    @Override
    @Nullable
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
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        return switch (mode)
        {
            case PRIMARY -> HorizontalRotation.rotate(state, direction);
            case SECONDARY -> super.rotate(state, direction, mode);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedFlatSlopePanelCornerBlock.mirrorCorner(state, mirror);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation backRot = rotation.rotate(rotation.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, backRot)
                        .setValue(PropertyHolder.FRONT, true)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation.getOpposite())
                        .setValue(PropertyHolder.FRONT, true)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (edge == facing)
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            Direction rotDir = rotation.withFacing(facing);
            Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
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
    public BlockState getItemModelSource()
    {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.LEFT);
    }
}
