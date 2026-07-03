package io.github.xfacthd.framedblocks.api.block.doubleblock;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/// Indicates which camo "slot" and double block part is accesible for a given side and edge of the block.
public enum CamoGetter {
    /// No single camo and block part can be resolved from the side and edge.
    NONE(_ -> EmptyCamoContainer.EMPTY, _ -> FramedBlockData.EMPTY, _ -> null),
    /// The first camo and block part can be resolved from the side and edge.
    FIRST(FramedDoubleBlockEntity::getCamo, data -> data.unwrap(false), DoubleBlockParts::stateOne),
    /// The second camo and block part can be resolved from the side and edge.
    SECOND(FramedDoubleBlockEntity::getCamoTwo, data -> data.unwrap(true), DoubleBlockParts::stateTwo),
    ;

    private final Function<FramedDoubleBlockEntity, CamoContainer<?, ?>> entityCamoGetter;
    private final Function<AbstractFramedBlockData, FramedBlockData> modelDataUnwrapper;
    private final Function<DoubleBlockParts, @Nullable BlockState> partGetter;

    CamoGetter(
            Function<FramedDoubleBlockEntity, CamoContainer<?, ?>> entityCamoGetter,
            Function<AbstractFramedBlockData, FramedBlockData> modelDataUnwrapper,
            Function<DoubleBlockParts, @Nullable BlockState> partGetter
    ) {
        this.entityCamoGetter = entityCamoGetter;
        this.modelDataUnwrapper = modelDataUnwrapper;
        this.partGetter = partGetter;
    }

    /// {@return the given BE's camo in the slot resolved by this camo getter}
    ///
    /// @param be The BE to resolve the camo from
    public CamoContainer<?, ?> getCamo(FramedDoubleBlockEntity be) {
        return entityCamoGetter.apply(be);
    }

    /// {@return the given block data's camo in the slot resolved by this camo getter}
    ///
    /// @param data The block data to resolve the camo from
    public CamoContainer<?, ?> getCamo(AbstractFramedBlockData data) {
        return modelDataUnwrapper.apply(data).getCamoContainer();
    }

    /// {@return the block part for the slot resolved by this camo getter}
    ///
    /// @param parts The block parts to resolve the part from
    public @Nullable BlockState getComponent(DoubleBlockParts parts) {
        return partGetter.apply(parts);
    }

    /// {@return the camo getter referred to by the given flags}
    ///
    /// @param first  Whether the first camo should be resolvable
    /// @param second Whether the second camo should be resolvable
    public static CamoGetter get(boolean first, boolean second) {
        if (first && second) {
            throw new IllegalArgumentException("Only first or second may be true");
        }
        if (first) {
            return FIRST;
        }
        if (second) {
            return SECOND;
        }
        return NONE;
    }
}
