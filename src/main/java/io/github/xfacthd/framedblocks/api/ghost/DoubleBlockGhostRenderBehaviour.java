package io.github.xfacthd.framedblocks.api.ghost;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

/// Base implementation of a ghost render behavior for framed blocks with two camos.
public interface DoubleBlockGhostRenderBehaviour extends GhostRenderBehaviour {
    /// Default instance for double blocks with no further special behavior.
    DoubleBlockGhostRenderBehaviour INSTANCE = new DoubleBlockGhostRenderBehaviour() {};

    @Override
    default ModelData buildModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo,
            @Nullable Holder<BlockOverlay> overlay
    ) {
        return ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedDoubleBlockData(
                ((IFramedDoubleBlock) renderState.getBlock()).getCache(renderState).getParts(),
                new FramedBlockData(renderState, camo.getCamo(0), false, overlay),
                new FramedBlockData(renderState, camo.getCamo(1), true, overlay)
        ));
    }
}
