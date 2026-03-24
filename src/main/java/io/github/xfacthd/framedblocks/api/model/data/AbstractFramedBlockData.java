package io.github.xfacthd.framedblocks.api.model.data;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public abstract sealed class AbstractFramedBlockData permits FramedBlockData, FramedDoubleBlockData
{
    public static final ModelProperty<AbstractFramedBlockData> PROPERTY = new ModelProperty<>();

    public abstract FramedBlockData unwrap(BlockState partState);

    public abstract FramedBlockData unwrap(boolean secondary);

    public abstract boolean isCamoEmissive();

    public abstract float getCamoShadeBrightness(BlockGetter level, BlockPos pos, float frameShade);

    public abstract TriState isViewBlocking();

    @Nullable
    public abstract Holder<BlockOverlay> getBlockOverlay();

    /// Returns the offset to apply to tint indices in camo quads of the specified part
    ///
    /// @param secondPart Whether the second part is requesting the offset
    public abstract int getCamoTintIndexOffset(boolean secondPart);

    /// Returns the offset to apply to tint indices in non-camo quads
    public abstract int getPostCamoTintIndexOffset();

    @Nullable
    @Contract("_,_,!null -> !null")
    public static FramedBlockData getOrDefault(ModelData modelData, BlockState partState, @Nullable FramedBlockData defaultData)
    {
        AbstractFramedBlockData data = modelData.get(PROPERTY);
        return data != null ? data.unwrap(partState) : defaultData;
    }
}
