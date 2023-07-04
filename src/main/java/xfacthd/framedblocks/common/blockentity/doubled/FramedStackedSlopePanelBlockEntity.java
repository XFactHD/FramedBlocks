package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedStackedSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean corner;
    private final boolean innerCorner;

    public FramedStackedSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_STACKED_SLOPE_PANEL.get(), pos, state);
        BlockType type = (BlockType) getBlockType();
        this.corner = type == BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER;
        this.innerCorner = type == BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();
        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing)
        {
            return false;
        }
        if (side == facing.getOpposite())
        {
            return true;
        }

        return Utils.fractionInDir(vec, facing.getOpposite()) > .5F;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing || (side.getAxis() != facing.getAxis() && edge == facing))
        {
            return this::getCamo;
        }

        if (!corner && edge == facing.getOpposite())
        {
            HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
            Direction rotDir = rot.withFacing(facing);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

            if (side == rotDir.getOpposite() || (innerCorner && side == perpRotDir.getOpposite()))
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return SolidityCheck.FIRST;
        }

        if (!corner)
        {
            HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
            Direction rotDir = rot.withFacing(facing);
            Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

            if (side == rotDir.getOpposite() || (innerCorner && side == perpRotDir.getOpposite()))
            {
                return SolidityCheck.BOTH;
            }
        }
        return SolidityCheck.NONE;
    }
}