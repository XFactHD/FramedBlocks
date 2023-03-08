package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDividedSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDividedSlab.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == dir.getOpposite()) { return false; }
        if (face == dir) { return true; }

        double xz = Utils.fractionInDir(hit.getLocation(), dir);
        return xz > .5D;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return DoubleSoundMode.EITHER;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
        }
        return false;
    }
}
