package io.github.xfacthd.framedblocks.api.block.doubleblock;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

/// Indicates which camos need to be taken into account for determining the [solidity][BlockState#isSolidRender()]
/// and ability to sustain a plant of a framed block.
public enum SolidityCheck {
    /// The face is never solid and cannot sustain a plant.
    NONE(_ -> false, null),
    /// Face solidity and plant sustainability are only dependent on the first camo.
    FIRST(data -> data.unwrap(false).getCamoContent().isSolid(), FramedDoubleBlockEntity::getCamo),
    /// Face solidity and plant sustainability are only dependent on the second camo.
    SECOND(data -> data.unwrap(true).getCamoContent().isSolid(), FramedDoubleBlockEntity::getCamoTwo),
    /// Face solidity is dependent on both camos and the face cannot sustain a plant.
    BOTH(data -> FIRST.isSolid(data) && SECOND.isSolid(data), null);

    private final Predicate<AbstractFramedBlockData> predicate;
    @Nullable
    private final CamoGetter plantableCamoGetter;

    SolidityCheck(Predicate<AbstractFramedBlockData> predicate, @Nullable CamoGetter plantableCamoGetter) {
        this.predicate = predicate;
        this.plantableCamoGetter = plantableCamoGetter;
    }

    /// {@return whether the face is solid based on the given block data}
    ///
    /// @param data The block data to test against
    public boolean isSolid(AbstractFramedBlockData data) {
        return predicate.test(data);
    }

    /// {@return whether the face of the given BE can sustain the given plant}
    ///
    /// @param be    The BE being tested for plant sustainability
    /// @param level The level which the BE is in
    /// @param side  The side being tested for plant sustainability (must match the side this solidity check was resolved from)
    /// @param plant The plant being tested for sustainability on the block
    public TriState canSustainPlant(FramedDoubleBlockEntity be, BlockGetter level, Direction side, BlockState plant) {
        if (plantableCamoGetter == null) {
            return TriState.DEFAULT;
        }
        CamoContent<?> camo = plantableCamoGetter.get(be).getContent();
        return camo.canSustainPlant(level, be.getBlockPos(), side, plant);
    }

    @FunctionalInterface
    private interface CamoGetter {
        CamoContainer<?, ?> get(FramedDoubleBlockEntity be);
    }
}
