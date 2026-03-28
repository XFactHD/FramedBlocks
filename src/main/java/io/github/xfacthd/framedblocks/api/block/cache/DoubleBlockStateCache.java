package io.github.xfacthd.framedblocks.api.block.cache;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class DoubleBlockStateCache extends StateCache {
    private final DoubleBlockTopInteractionMode topInteractionMode;
    private final DoubleBlockParts parts;
    private final SolidityCheck[] solidityChecks = new SolidityCheck[DIR_COUNT];
    private final CamoGetter[] camoGetters = new CamoGetter[DIR_COUNT * DIR_COUNT_N];
    private final byte solidOverlay;
    private final EdgeOverlayMask edgeOverlay;
    private final boolean mayCullNullFacePartOne;
    private final boolean mayCullNullFacePartTwo;

    public DoubleBlockStateCache(BlockState state, IBlockType type) {
        super(state, type);
        IFramedDoubleBlock block = (IFramedDoubleBlock) state.getBlock();
        this.topInteractionMode = block.calculateTopInteractionMode(state);
        this.parts = block.calculateParts(state);
        BlockOverlayPredicate overlayPredicate = type.getBlockOverlayPredicate();
        boolean supportsOverlay = type.supportsBlockOverlays();
        byte solidOverlay = 0;
        for (Direction side : DIRECTIONS) {
            solidityChecks[side.ordinal()] = block.calculateSolidityCheck(state, side);
            if (supportsOverlay && overlayPredicate.supportsSolid(state, side, true)) {
                solidOverlay |= (byte) (1 << side.ordinal());
            }
            for (Direction edge : DIRECTIONS_WITH_NULL) {
                CamoGetter getter;
                if (edge != null && edge.getAxis() == side.getAxis()) {
                    // null is the first value in DIRECTIONS_WITH_NULL, so this is safe
                    int cgNullIdx = side.ordinal() * DIR_COUNT_N + DirUtils.maskNullDirection(null);
                    getter = camoGetters[cgNullIdx];
                } else {
                    getter = block.calculateCamoGetter(state, side, edge);
                }
                int cgIdx = side.ordinal() * DIR_COUNT_N + DirUtils.maskNullDirection(edge);
                camoGetters[cgIdx] = getter;
            }
        }
        this.solidOverlay = solidOverlay;
        this.edgeOverlay = supportsOverlay ? EdgeOverlayMask.compute(state, overlayPredicate, true) : EdgeOverlayMask.NEVER;
        NullCullPredicate nullCullPredicate = type.getNullCullPredicate();
        this.mayCullNullFacePartOne = nullCullPredicate.testPartOne(state);
        this.mayCullNullFacePartTwo = nullCullPredicate.testPartTwo(state);
    }

    public final DoubleBlockTopInteractionMode getTopInteractionMode() {
        return topInteractionMode;
    }

    public final DoubleBlockParts getParts() {
        return parts;
    }

    public final SolidityCheck getSolidityCheck(Direction side) {
        return solidityChecks[side.ordinal()];
    }

    public final CamoGetter getCamoGetter(Direction side, @Nullable Direction edge) {
        return camoGetters[side.ordinal() * DIR_COUNT_N + DirUtils.maskNullDirection(edge)];
    }

    @Override
    public final boolean supportsSolidOverlay(Direction side, boolean secondPart) {
        return secondPart ? (solidOverlay & (1 << side.ordinal())) != 0 : super.supportsSolidOverlay(side, false);
    }

    @Override
    public final boolean supportsEdgeOverlay(Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        if (secondPart) {
            return edgeOverlay.isSet(side, edge, nullCullFace, unaligned);
        }
        return super.supportsEdgeOverlay(side, edge, false, nullCullFace, unaligned);
    }

    public final boolean mayCullNullFace(boolean secondPart) {
        return secondPart ? mayCullNullFacePartTwo : mayCullNullFacePartOne;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        DoubleBlockStateCache that = (DoubleBlockStateCache) other;
        return topInteractionMode == that.topInteractionMode &&
                parts.stateOne() == that.parts.stateOne() &&
                parts.stateTwo() == that.parts.stateTwo() &&
                Arrays.equals(solidityChecks, that.solidityChecks) &&
                Arrays.equals(camoGetters, that.camoGetters) &&
                solidOverlay == that.solidOverlay &&
                edgeOverlay.equals(that.edgeOverlay) &&
                mayCullNullFacePartOne == that.mayCullNullFacePartOne &&
                mayCullNullFacePartTwo == that.mayCullNullFacePartTwo;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(topInteractionMode);
        result = 31 * result + Objects.hashCode(parts.stateOne());
        result = 31 * result + Objects.hashCode(parts.stateTwo());
        result = 31 * result + Arrays.hashCode(solidityChecks);
        result = 31 * result + Arrays.hashCode(camoGetters);
        result = 31 * result + Byte.hashCode(solidOverlay);
        result = 31 * result + edgeOverlay.hashCode();
        result = 31 * result + Boolean.hashCode(mayCullNullFacePartOne);
        result = 31 * result + Boolean.hashCode(mayCullNullFacePartTwo);
        return result;
    }
}
