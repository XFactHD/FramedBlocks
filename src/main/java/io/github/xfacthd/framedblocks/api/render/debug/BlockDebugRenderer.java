package io.github.xfacthd.framedblocks.api.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.BlockHitResult;

public interface BlockDebugRenderer<T extends IFramedBlockEntity>
{
    void extract(T be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState);

    void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector);

    /**
     * {@return whether this debug renderer is enabled}
     */
    boolean isEnabled();
}
