package io.github.xfacthd.framedblocks.api.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.BlockHitResult;

/// Super-interface for renderers of debug visualizations for framed blocks.
public interface BlockDebugRenderer<T extends IFramedBlockEntity> {
    /// Extract the render data from the given BE and attach it to the given level render state.
    ///
    /// @param be          The block entity the player is looking at
    /// @param blockHit    The raycast result against the block
    /// @param partialTick The current partial tick
    /// @param renderState The level render state to attach the data to
    void extract(T be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState);

    /// Submit the debug visualization to the given collector.
    ///
    /// @param renderState The level render state to pull render data from
    /// @param poseStack   The pose stack to use for transformation
    /// @param collector   The collector to submit geometry to
    void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector);

    /// {@return whether this debug renderer is enabled}
    boolean isEnabled();
}
