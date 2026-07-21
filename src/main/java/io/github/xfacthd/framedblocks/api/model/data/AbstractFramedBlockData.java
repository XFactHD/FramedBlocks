package io.github.xfacthd.framedblocks.api.model.data;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/// Base class for the primary model data object of single- and double-camo framed blocks.
public abstract sealed class AbstractFramedBlockData permits FramedBlockData, FramedDoubleBlockData {
    public static final ModelProperty<AbstractFramedBlockData> PROPERTY = new ModelProperty<>();

    /// {@return the single-block data unwrapped from this data for the given part state}
    ///
    /// @param partState The part state to unwrap for. Ignored for data from single-camo blocks
    public abstract FramedBlockData unwrap(BlockState partState);

    /// {@return the single-block data unwrapped from this data for the part indicated by the given flag}
    ///
    /// @param secondary Which part of the block to unwrap for. Ignored for data from single-camo blocks
    public abstract FramedBlockData unwrap(boolean secondary);

    /// {@return whether any camo in this data is {@linkplain BlockBehaviour.BlockStateBase#emissiveRendering(BlockGetter, BlockPos) emissive}}
    public abstract boolean isCamoEmissive();

    /// Returns the shade brightness of the framed block with the camo(s) from this data.
    ///
    /// @param level      The level the framed block is in
    /// @param pos        The position of the framed block
    /// @param frameShade The shade brightness of the framed block without any camos
    /// @return the shade brightness of the framed block
    public abstract float getCamoShadeBrightness(BlockGetter level, BlockPos pos, float frameShade);

    /// Returns whether the framed block is view-blocking with the camo(s) from this data or
    /// [TriState#DEFAULT] if the view-blocking is unknown.
    ///
    /// @return whether the framed block is view-blocking
    public abstract TriState isViewBlocking();

    /// {@return the block overlay applied to the framed block}
    public abstract @Nullable BlockOverlay getBlockOverlay();

    /// {@return the offset to apply to tint indices in camo quads of the specified part}
    ///
    /// @param secondPart Whether the second part is querying the offset
    public abstract int getCamoTintIndexOffset(boolean secondPart);

    /// {@return the offset to apply to tint indices in non-camo quads}
    public abstract int getPostCamoTintIndexOffset();

    /// {@return the block data for the given part state stored in the given model data or the given default if absent}
    ///
    /// @param modelData   The model data to read from
    /// @param partState   The part state to unwrap the data for
    /// @param defaultData The default value to fall back to
    @Contract("_,_,!null -> !null")
    public static @Nullable FramedBlockData getOrDefault(ModelData modelData, BlockState partState, @Nullable FramedBlockData defaultData) {
        AbstractFramedBlockData data = modelData.get(PROPERTY);
        return data != null ? data.unwrap(partState) : defaultData;
    }
}
