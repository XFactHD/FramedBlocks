package io.github.xfacthd.framedblocks.api.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

public interface BlockDebugRenderer<T extends IFramedBlockEntity>
{
    void render(LevelRenderState renderState, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay);

    void extract(T be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState);

    /**
     * {@return whether this debug renderer is enabled}
     *
     * @apiNote Called when this renderer is attached to a {@link BlockEntityType}
     */
    boolean isEnabled();
}
