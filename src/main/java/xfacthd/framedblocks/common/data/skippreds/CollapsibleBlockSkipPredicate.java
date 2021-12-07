package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.CollapseFace;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class CollapsibleBlockSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        CollapseFace face = state.get(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE || side == face.toDirection().getOpposite())
        {
            return SideSkipPredicate.CTM.test(world, pos, state, adjState, side);
        }
        else if (side == face.toDirection())
        {
            return false;
        }

        TileEntity be = Utils.getTileEntitySafe(world, pos);
        TileEntity adjBe = Utils.getTileEntitySafe(world, pos.offset(side)); //TODO: implement

        return false;
    }
}