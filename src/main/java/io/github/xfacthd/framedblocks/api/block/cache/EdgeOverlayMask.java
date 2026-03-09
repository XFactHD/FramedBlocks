package io.github.xfacthd.framedblocks.api.block.cache;

import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

record EdgeOverlayMask(int maskNullCullFace, int maskUnaligned, int maskBoth)
{
    private static final ObjectOpenHashSet<EdgeOverlayMask> MASK_INTERNER = new ObjectOpenHashSet<>();
    private static final Direction[] DIRECTIONS = Direction.values();
    static final EdgeOverlayMask NEVER = intern(new EdgeOverlayMask(0, 0, 0));

    boolean isSet(Direction side, Direction edge, boolean nullCullFace, boolean unaligned)
    {
        int mask = nullCullFace ? (unaligned ? maskBoth : maskNullCullFace) : maskUnaligned;
        return (mask & getBitMask(side, edge)) != 0;
    }

    private static int getBitMask(Direction side, Direction edge)
    {
        return 1 << (side.ordinal() << 2 | DirUtils.get2dValueAround(side.getAxis(), edge));
    }

    static EdgeOverlayMask compute(BlockState state, BlockOverlayPredicate predicate, boolean secondPart)
    {
        int maskNullCullFace = 0;
        int maskUnaligned = 0;
        int maskBoth = 0;
        for (Direction side : DIRECTIONS)
        {
            for (Direction edge : DIRECTIONS)
            {
                if (side.getAxis() == edge.getAxis()) continue;

                for (EdgeOverlayCondition condition : EdgeOverlayCondition.VALUES)
                {
                    if (!predicate.supportsEdge(state, side, edge, secondPart, condition.nullCullFace, condition.unaligned))
                    {
                        continue;
                    }

                    int bitMask = getBitMask(side, edge);
                    switch (condition)
                    {
                        case NULL_CULL -> maskNullCullFace |= bitMask;
                        case UNALIGNED -> maskUnaligned |= bitMask;
                        case BOTH -> maskBoth |= bitMask;
                    }
                }
            }
        }
        return intern(new EdgeOverlayMask(maskNullCullFace, maskUnaligned, maskBoth));
    }

    private static EdgeOverlayMask intern(EdgeOverlayMask mask)
    {
        return MASK_INTERNER.addOrGet(mask);
    }

    private enum EdgeOverlayCondition
    {
        NULL_CULL(true, false),
        UNALIGNED(false, true),
        BOTH(true, true),
        ;

        private static final EdgeOverlayCondition[] VALUES = values();

        private final boolean nullCullFace;
        private final boolean unaligned;

        EdgeOverlayCondition(boolean nullCullFace, boolean unaligned)
        {
            this.nullCullFace = nullCullFace;
            this.unaligned = unaligned;
        }
    }
}
