package xfacthd.framedblocks.api.block.cache;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;

public final class StateCache
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DIR_COUNT = DIRECTIONS.length;

    private final boolean anyFullFace;
    private final boolean[] fullFace;
    private final boolean[][] conFullEdge;
    private final boolean[][] conDetailed;

    public StateCache(BlockState state, IBlockType type)
    {
        boolean anyFullFace = false;
        boolean anyConDetailed = false;
        boolean[] fullFace = new boolean[DIR_COUNT];
        boolean[][] conFullEdge = new boolean[DIR_COUNT][DIR_COUNT + 1];
        boolean[][] conDetailed = new boolean[DIR_COUNT][DIR_COUNT];

        FullFacePredicate facePred = type.getFullFacePredicate();
        ConnectionPredicate conPred = type.getConnectionPredicate();
        boolean supportsCt = type.supportsConnectedTextures();

        for (Direction side : DIRECTIONS)
        {
            int sideOrd = side.ordinal();

            boolean full = facePred.test(state, side);
            anyFullFace |= full;
            fullFace[sideOrd] = full;

            if (!supportsCt)
            {
                continue;
            }

            conFullEdge[sideOrd][Utils.maskNullDirection(null)] = conPred.canConnectFullEdge(state, side, null);

            for (Direction edge : DIRECTIONS)
            {
                conFullEdge[sideOrd][Utils.maskNullDirection(edge)] = conPred.canConnectFullEdge(state, side, edge);

                boolean detailed = conPred.canConnectDetailed(state, side, edge);
                anyConDetailed |= detailed;
                conDetailed[sideOrd][edge.ordinal()] = detailed;
            }
        }

        this.anyFullFace = anyFullFace;
        this.fullFace = fullFace;
        this.conFullEdge = supportsCt ? conFullEdge : null;
        this.conDetailed = anyConDetailed ? conDetailed : null;
    }

    public boolean hasAnyFullFace()
    {
        return anyFullFace;
    }

    public boolean isFullFace(@Nullable Direction side)
    {
        return side != null && fullFace[side.ordinal()];
    }

    public boolean canConnectFullEdge(Direction side, @Nullable Direction edge)
    {
        return conFullEdge != null && conFullEdge[side.ordinal()][Utils.maskNullDirection(edge)];
    }

    public boolean canConnectDetailed(Direction side, Direction edge)
    {
        return conDetailed != null && conDetailed[side.ordinal()][edge.ordinal()];
    }
}
