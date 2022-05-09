package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedInverseDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedInverseDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedInverseDoubleSlopeSlab.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        if (hit.getDirection() == Direction.DOWN) { return false; }
        return hit.getDirection() == Direction.UP || Mth.frac(hit.getLocation().y()) >= .5F;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.SECOND; }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (side == Direction.UP || side == facing)
        {
            return getCamoStateTwo();
        }
        if (side == Direction.DOWN || side == facing.getOpposite())
        {
            return getCamoState();
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side) { return false; }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(FramedProperties.FACING_HOR));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing)
    {
        BlockState defState = FBContent.blockFramedSlopeSlab.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, false)
                        .setValue(FramedProperties.TOP, true),
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, true)
                        .setValue(FramedProperties.TOP, false)
        );
    }
}