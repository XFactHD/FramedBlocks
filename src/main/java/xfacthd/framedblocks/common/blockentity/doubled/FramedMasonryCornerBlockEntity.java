package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedMasonryCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedMasonryCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_MASONRY_CORNER.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();
        Vec3 hitVec = hit.getLocation();

        return switch (side)
        {
            case DOWN -> Utils.fractionInDir(hitVec, dir) > .5;
            case UP -> Utils.fractionInDir(hitVec, dir.getCounterClockWise()) > .5;
            default ->
            {
                boolean topHalf = Utils.fractionInDir(hitVec, Direction.UP) > .5;
                Direction frontFace = topHalf ? dir.getCounterClockWise() : dir;
                if (side == frontFace) yield true;
                if (side == frontFace.getOpposite()) yield false;
                yield Utils.fractionInDir(hitVec, frontFace) > .5;
            }
        };
    }
}
