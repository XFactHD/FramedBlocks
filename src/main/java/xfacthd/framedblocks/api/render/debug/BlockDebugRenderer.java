package xfacthd.framedblocks.api.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;

public interface BlockDebugRenderer<T extends FramedBlockEntity>
{
    void render(T be, BlockHitResult blockHit, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay);

    /**
     * {@return whether this debug renderer is enabled}
     *
     * @apiNote Called when this renderer is attached to a {@link BlockEntityType}
     */
    boolean isEnabled();
}
