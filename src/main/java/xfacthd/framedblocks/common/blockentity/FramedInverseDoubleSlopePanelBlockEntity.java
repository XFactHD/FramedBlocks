package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.Rotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedInverseDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedInverseDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedInverseDoubleSlopePanel.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();
        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing) { return false; }
        if (side == facing.getOpposite()) { return true; }

        boolean second;
        if (Utils.isZ(facing))
        {
            second = vec.z() > .5F;
        }
        else
        {
            second = vec.x() <= .5F;
        }

        if (Utils.isPositive(facing) == Utils.isZ(facing))
        {
            second = !second;
        }

        return second;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing) { return getCamoState(); }
        if (side == facing.getOpposite()) { return getCamoStateTwo(); }

        Direction rotation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == rotation) { return getCamoState(); }
        if (side == rotation.getOpposite()) { return getCamoStateTwo(); }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(PropertyHolder.ROTATION));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, Rotation rotation)
    {
        BlockState defState = FBContent.blockFramedSlopePanel.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, rotation.isVertical() ? rotation.getOpposite() : rotation)
                        .setValue(PropertyHolder.FRONT, true),
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, true)
        );
    }
}
