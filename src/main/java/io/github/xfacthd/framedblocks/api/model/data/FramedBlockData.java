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

public final class FramedBlockData extends AbstractFramedBlockData
{
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
    private final int postCamoTintIndexOffset;

    public FramedBlockData(@Nullable BlockState outerState, CamoContainer<?, ?> camoContent, boolean secondPart, @Nullable Holder<BlockOverlay> overlay)
    {
        this(outerState, camoContent, (byte) 0, secondPart, false, false, TriState.DEFAULT, overlay);
    }

    public FramedBlockData(
            @Nullable BlockState outerState,
            CamoContainer<?, ?> camoContainer,
            byte cullMask,
            boolean secondPart,
            boolean reinforced,
            boolean emissive,
            TriState viewBlocking,
            @Nullable Holder<BlockOverlay> overlay
    )
    {
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
        this.postCamoTintIndexOffset = CamoContainerHelper.Client.getTintCount(camoContainer);
    }

    private FramedBlockData()
    {
        this.outerState = null;
        this.camoContainer = EmptyCamoContainer.EMPTY;
        this.camoContent = camoContainer.getContent();
        this.cullMask = 0;
        this.flags = 0;
        this.viewBlocking = TriState.DEFAULT;
        this.overlay = null;
        this.postCamoTintIndexOffset = 0;
    }

    @Nullable
    public BlockState getOuterState()
    {
        return outerState;
    }

    public CamoContainer<?, ?> getCamoContainer()
    {
        return camoContainer;
    }

    public CamoContent<?> getCamoContent()
    {
        return camoContent;
    }

    public boolean isSideHidden(Direction side)
    {
        return (cullMask & (1 << side.ordinal())) != 0;
    }

    public boolean isSecondPart()
    {
        return (flags & FLAG_SECOND_PART) != 0;
    }

    public boolean isReinforced()
    {
        return (flags & FLAG_REINFORCED) != 0;
    }

    public boolean isEmissive()
    {
        return (flags & FLAG_EMISSIVE) != 0;
    }

    /**
     * Computes a bit mask of visible faces according to the occlusion state stored in this {@code FramedBlockData}
     * and the full-face info stored in the provided {@link StateCache}
     *
     * @param stateCache The {@link StateCache} to pull the full-face info from
     * @param forCached Whether the mask should be computed for the cached path ({@link StateCache#isFullFace(Direction)}
     *                  returns false) or the uncached path ({@link StateCache#isFullFace(Direction)} returns true)
     */
    public int computeFaceMask(StateCache stateCache, boolean forCached)
    {
        int mask = stateCache.getFullFaceMask();
        if (forCached)
        {
            mask ^= FULL_FACE_INVERSION_MASK;
        }
        return mask & ~cullMask;
    }

    @Override
    public FramedBlockData unwrap(BlockState partState)
    {
        return this;
    }

    @Override
    public FramedBlockData unwrap(boolean secondary)
    {
        return this;
    }

    @Override
    public boolean isCamoEmissive()
    {
        return camoContent.isEmissive();
    }

    @Override
    public float getCamoShadeBrightness(BlockGetter level, BlockPos pos, float frameShade)
    {
        return camoContent.getShadeBrightness(level, pos, frameShade);
    }

    @Override
    public TriState isViewBlocking()
    {
        return viewBlocking;
    }

    @Override
    @Nullable
    public Holder<BlockOverlay> getBlockOverlay()
    {
        return overlay;
    }

    @Override
    public int getCamoTintIndexOffset(boolean secondPart)
    {
        return 0;
    }

    @Override
    public int getPostCamoTintIndexOffset()
    {
        return postCamoTintIndexOffset;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof FramedBlockData other)
        {
            return camoContainer.equals(other.camoContainer) &&
                   cullMask == other.cullMask &&
                   flags == other.flags &&
                   viewBlocking == other.viewBlocking;
        }
        return false;
    }
}
