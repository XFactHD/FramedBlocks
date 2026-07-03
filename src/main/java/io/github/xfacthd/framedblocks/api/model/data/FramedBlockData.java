package io.github.xfacthd.framedblocks.api.model.data;

import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// Primary model data object for single-camo blocks and the individual parts of double-camo blocks.
public final class FramedBlockData extends AbstractFramedBlockData {
    public static final FramedBlockData EMPTY = new FramedBlockData();
    private static final int FULL_FACE_INVERSION_MASK = 0b1111111;
    private static final int FLAG_SECOND_PART = 1;
    private static final int FLAG_REINFORCED = 1 << 1;
    private static final int FLAG_EMISSIVE = 1 << 2;

    @Nullable
    private final BlockState outerState;
    private final CamoContainer<?, ?> camoContainer;
    private final CamoContent<?> camoContent;
    private final byte cullMask;
    private final byte flags;
    private final TriState viewBlocking;
    @Nullable
    private final Holder<BlockOverlay> overlay;
    @Nullable
    private final ModelDataEntry<?> queryData;
    private final int postCamoTintIndexOffset;

    /// @param outerState    The framed block's state to get the correct overlay predicate on double blocks
    /// @param camoContainer The camo applied to the framed block or part thereof
    /// @param secondPart    Whether this data is for the first or second part of a double block (`false` for single-camo blocks)
    /// @param overlay       The block overlay applied to the framed block
    public FramedBlockData(@Nullable BlockState outerState, CamoContainer<?, ?> camoContainer, boolean secondPart, @Nullable Holder<BlockOverlay> overlay) {
        this(outerState, camoContainer, (byte) 0, secondPart, false, false, TriState.DEFAULT, overlay, null);
    }

    /// @param outerState    The framed block's state to get the correct overlay predicate on double blocks
    /// @param camoContainer The camo applied to the framed block or part thereof
    /// @param cullMask      The mask of occluded faces of the framed block or part thereof
    /// @param secondPart    Whether this data is for the first or second part of a double block (`false` for single-camo blocks)
    /// @param reinforced    Whether the framed block is reinforced
    /// @param emissive      Whether the framed block is emissive (fullbright)
    /// @param viewBlocking  Whether the framed block is view-blocking or [TriState#DEFAULT] if unknown
    /// @param overlay       The block overlay applied to the framed block
    /// @param queryData     An additional model data entry to attach to the level used for quering the camo model's part
    public FramedBlockData(
            @Nullable BlockState outerState,
            CamoContainer<?, ?> camoContainer,
            byte cullMask,
            boolean secondPart,
            boolean reinforced,
            boolean emissive,
            TriState viewBlocking,
            @Nullable Holder<BlockOverlay> overlay,
            @Nullable ModelDataEntry<?> queryData
    ) {
        this.outerState = outerState;
        this.camoContainer = camoContainer;
        this.camoContent = camoContainer.getContent();
        this.cullMask = cullMask;
        byte flags = 0;
        if (secondPart) flags |= FLAG_SECOND_PART;
        if (reinforced) flags |= FLAG_REINFORCED;
        if (emissive) flags |= FLAG_EMISSIVE;
        this.flags = flags;
        this.viewBlocking = viewBlocking;
        this.overlay = overlay;
        this.queryData = queryData;
        this.postCamoTintIndexOffset = CamoContainerHelper.Client.getTintCount(camoContainer);
    }

    private FramedBlockData() {
        this.outerState = null;
        this.camoContainer = EmptyCamoContainer.EMPTY;
        this.camoContent = camoContainer.getContent();
        this.cullMask = 0;
        this.flags = 0;
        this.viewBlocking = TriState.DEFAULT;
        this.overlay = null;
        this.queryData = null;
        this.postCamoTintIndexOffset = 0;
    }

    /// {@return the framed block's state for overlay predicate lookup}
    public @Nullable BlockState getOuterState() {
        return outerState;
    }

    /// {@return the camo container applied to the framed block or part thereof}
    public CamoContainer<?, ?> getCamoContainer() {
        return camoContainer;
    }

    /// {@return the camo content applied to the framed block or part thereof}
    public CamoContent<?> getCamoContent() {
        return camoContent;
    }

    /// {@return whether the given side is occluded on the framed block or part thereof}
    ///
    /// @param side The side to query
    public boolean isSideHidden(Direction side) {
        return (cullMask & (1 << side.ordinal())) != 0;
    }

    /// {@return whether this data refers to the first or second part of a double block or `false` for single-camo blocks}
    public boolean isSecondPart() {
        return (flags & FLAG_SECOND_PART) != 0;
    }

    /// {@return whether the framed block is reinforced}
    public boolean isReinforced() {
        return (flags & FLAG_REINFORCED) != 0;
    }

    /// {@return whether the framed block is emissive (fullbright)}
    public boolean isEmissive() {
        return (flags & FLAG_EMISSIVE) != 0;
    }

    /// Computes a bit mask of visible faces according to the occlusion state stored in this data
    /// and the full-face info stored in the provided [StateCache].
    ///
    /// @param stateCache The state cache to pull the full-face info from
    /// @param forCached  Whether the mask should be computed for the cached path ([StateCache#isFullFace(Direction)]
    ///                   returns false) or the uncached path ([StateCache#isFullFace(Direction)] returns true)
    public int computeFaceMask(StateCache stateCache, boolean forCached) {
        int mask = stateCache.getFullFaceMask();
        if (forCached) {
            mask ^= FULL_FACE_INVERSION_MASK;
        }
        return mask & ~cullMask;
    }

    @Override
    public FramedBlockData unwrap(BlockState partState) {
        return this;
    }

    @Override
    public FramedBlockData unwrap(boolean secondary) {
        return this;
    }

    @Override
    public boolean isCamoEmissive() {
        return camoContent.isEmissive();
    }

    @Override
    public float getCamoShadeBrightness(BlockGetter level, BlockPos pos, float frameShade) {
        return camoContent.getShadeBrightness(level, pos, frameShade);
    }

    @Override
    public TriState isViewBlocking() {
        return viewBlocking;
    }

    @Override
    public @Nullable Holder<BlockOverlay> getBlockOverlay() {
        return overlay;
    }

    /// {@return the additional model data entry to attach to the level used for quering the camo model's part}
    public @Nullable ModelDataEntry<?> getQueryData() {
        return queryData;
    }

    @Override
    public int getCamoTintIndexOffset(boolean secondPart) {
        return 0;
    }

    @Override
    public int getPostCamoTintIndexOffset() {
        return postCamoTintIndexOffset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FramedBlockData other) {
            return camoContainer.equals(other.camoContainer) &&
                   cullMask == other.cullMask &&
                   flags == other.flags &&
                   viewBlocking == other.viewBlocking &&
                   Objects.equals(queryData, other.queryData);
        }
        return false;
    }
}
