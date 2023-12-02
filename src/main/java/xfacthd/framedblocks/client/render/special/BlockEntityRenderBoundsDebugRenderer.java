package xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import xfacthd.framedblocks.api.util.TestProperties;
import xfacthd.framedblocks.mixin.client.AccessorLevelRenderer;

public final class BlockEntityRenderBoundsDebugRenderer
{
    public static void onRenderLevelStage(final RenderLevelStageEvent event)
    {
        if (!TestProperties.ENABLE_BER_RENDER_BOUNDS_DEBUG_RENDERER) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;

        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        for (SectionRenderDispatcher.RenderSection section : ((AccessorLevelRenderer) renderer).framedblocks$getVisibleSections())
        {
            for (BlockEntity be : section.getCompiled().getRenderableBlockEntities())
            {
                drawRenderBoundingBox(poseStack, consumer, camera, be);
            }
        }
        for (BlockEntity be : ((AccessorLevelRenderer) renderer).framedblocks$getGlobalBlockEntities())
        {
            drawRenderBoundingBox(poseStack, consumer, camera, be);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> void drawRenderBoundingBox(
            PoseStack poseStack, VertexConsumer consumer, Vec3 camera, BlockEntity be
    )
    {
        BlockEntityRenderer<T> renderer = (BlockEntityRenderer<T>) Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);
        if (renderer != null)
        {
            BlockPos pos = be.getBlockPos();
            AABB aabb = renderer.getRenderBoundingBox((T) be).move(-pos.getX(), -pos.getY(), -pos.getZ());
            Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(camera);

            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            LevelRenderer.renderLineBox(poseStack, consumer, aabb, 1F, 0F, 0F, 1F);
            poseStack.popPose();
        }
    }



    private BlockEntityRenderBoundsDebugRenderer() { }
}
