package io.github.xfacthd.framedblocks.api.model;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class AbstractFramedBlockStateModel extends DelegateBlockStateModel {
    private final BlockState state;
    @Nullable
    private final ItemModelInfo itemModelInfo;

    protected AbstractFramedBlockStateModel(BlockStateModel baseModel, BlockState state, ItemModelInfo itemModelInfo) {
        super(baseModel);
        this.state = state;
        boolean isItemModel = state.getBlock() instanceof IFramedBlock block && block.getItemModelSource() == state;
        this.itemModelInfo = isItemModel ? itemModelInfo : null;
    }

    /// Collect the [BlockStateModelPart]s making up this model in the given level context.
    ///
    /// @param level          The level this model is being rendered in
    /// @param pos            The position this model is being rendered at
    /// @param state          The state this model is being rendered for
    /// @param random         The RNG to use for randomized elements of this model
    /// @param parts          The list to add the model parts to
    /// @param miscTintOffset The offset to apply to tint indices in non-camo, non-[BlockOverlay] geometry
    /// @return the value of `miscTintOffset` adjusted for any additional tint indices used by non-camo, non-[BlockOverlay] geometry produced by this model
    public abstract int collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts, int miscTintOffset);

    @Override
    public final void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        collectParts(level, pos, state, random, parts, 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        collectParts(BlockAndTintGetter.EMPTY, BlockPos.ZERO, state, random, parts);
    }

    @Override
    @SuppressWarnings("deprecation")
    public final Material.Baked particleMaterial() {
        return particleMaterial(BlockAndTintGetter.EMPTY, BlockPos.ZERO, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public final int materialFlags() {
        return materialFlags(BlockAndTintGetter.EMPTY, BlockPos.ZERO, state);
    }

    public void clearCache() { }

    public BlockStateModel getBaseModel() {
        return delegate;
    }

    public @Nullable ItemModelInfo getItemModelInfo() {
        return itemModelInfo;
    }
}
