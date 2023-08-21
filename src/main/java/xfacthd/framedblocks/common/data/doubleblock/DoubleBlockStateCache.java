package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

import java.util.Arrays;
import java.util.Objects;

public class DoubleBlockStateCache extends StateCache
{
    private final DoubleBlockTopInteractionMode topInteractionMode;
    private final Tuple<BlockState, BlockState> statePair;
    private final SolidityCheck[] solidityChecks = new SolidityCheck[6];
    private final CamoGetter[][] camoGetters = new CamoGetter[6][7];

    public DoubleBlockStateCache(BlockState state, IBlockType type)
    {
        super(state, type);
        IFramedDoubleBlock block = (IFramedDoubleBlock) state.getBlock();
        this.topInteractionMode = block.calculateTopInteractionMode(state);
        this.statePair = block.calculateBlockPair(state);
        Utils.forAllDirections(false, side ->
        {
            solidityChecks[side.ordinal()] = block.calculateSolidityCheck(state, side);
            Utils.forAllDirections(edge ->
            {
                CamoGetter getter;
                if (edge != null && edge.getAxis() == side.getAxis())
                {
                    // null is the first value this lambda receives, so this is safe
                    getter = camoGetters[side.ordinal()][Utils.maskNullDirection(null)];
                }
                else
                {
                    getter = block.calculateCamoGetter(state, side, edge);
                }
                camoGetters[side.ordinal()][Utils.maskNullDirection(edge)] = getter;
            });
        });
    }

    public final DoubleBlockTopInteractionMode getTopInteractionMode()
    {
        return topInteractionMode;
    }

    public final Tuple<BlockState, BlockState> getBlockPair()
    {
        return statePair;
    }

    public final SolidityCheck getSolidityCheck(Direction side)
    {
        return solidityChecks[side.ordinal()];
    }

    public final CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        return camoGetters[side.ordinal()][Utils.maskNullDirection(edge)];
    }

    @Override
    public boolean equals(Object other)
    {
        if (!super.equals(other))
        {
            return false;
        }
        DoubleBlockStateCache that = (DoubleBlockStateCache) other;
        return topInteractionMode == that.topInteractionMode &&
                statePair.getA() == that.statePair.getA() &&
                statePair.getB() == that.statePair.getB() &&
                Arrays.equals(solidityChecks, that.solidityChecks) &&
                Arrays.deepEquals(camoGetters, that.camoGetters);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(topInteractionMode);
        result = 31 * result + Objects.hashCode(statePair.getA());
        result = 31 * result + Objects.hashCode(statePair.getB());
        result = 31 * result + Arrays.hashCode(solidityChecks);
        result = 31 * result + Arrays.deepHashCode(camoGetters);
        return result;
    }
}
