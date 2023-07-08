package xfacthd.framedblocks.common.data.conpreds.pillar;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class LatticeConnectionPredicate implements ConnectionPredicate
{
    public static final LatticeConnectionPredicate INSTANCE = new LatticeConnectionPredicate();

    private LatticeConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        boolean x = state.getValue(FramedProperties.X_AXIS);
        boolean y = state.getValue(FramedProperties.Y_AXIS);
        boolean z = state.getValue(FramedProperties.Z_AXIS);
        return switch (side.getAxis())
        {
            case X -> (y && Utils.isY(edge)) || (z && Utils.isZ(edge));
            case Y -> (x && Utils.isX(edge)) || (z && Utils.isZ(edge));
            case Z -> (x && Utils.isX(edge)) || (y && Utils.isY(edge));
        };
    }
}
