package xfacthd.framedblocks.api.block.cache;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Cache for constant metadata related to a specific {@link BlockState}.
 * @apiNote Custom implementations must override {@link #equals(Object)} and {@link #hashCode()}
 * in order for cache deduplication to work properly
 */
public class StateCache
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DIR_COUNT = DIRECTIONS.length;
    public static final StateCache EMPTY = new StateCache();

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
        this.fullFace = anyFullFace ? fullFace : null;
        this.conFullEdge = supportsCt ? conFullEdge : null;
        this.conDetailed = anyConDetailed ? conDetailed : null;
    }

    private StateCache()
    {
        this.anyFullFace = false;
        this.fullFace = null;
        this.conFullEdge = null;
        this.conDetailed = null;
    }

    public final boolean hasAnyFullFace()
    {
        return anyFullFace;
    }

    public final boolean isFullFace(@Nullable Direction side)
    {
        return side != null && anyFullFace && fullFace[side.ordinal()];
    }

    public final boolean canConnectFullEdge(Direction side, @Nullable Direction edge)
    {
        return conFullEdge != null && conFullEdge[side.ordinal()][Utils.maskNullDirection(edge)];
    }

    public final boolean canConnectDetailed(Direction side, Direction edge)
    {
        return conDetailed != null && conDetailed[side.ordinal()][edge.ordinal()];
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || getClass() != other.getClass())
        {
            return false;
        }
        StateCache that = (StateCache) other;
        return anyFullFace == that.anyFullFace &&
                Arrays.equals(fullFace, that.fullFace) &&
                Arrays.deepEquals(conFullEdge, that.conFullEdge) &&
                Arrays.deepEquals(conDetailed, that.conDetailed);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(anyFullFace);
        result = 31 * result + Arrays.hashCode(fullFace);
        result = 31 * result + Arrays.deepHashCode(conFullEdge);
        result = 31 * result + Arrays.deepHashCode(conDetailed);
        return result;
    }
}
