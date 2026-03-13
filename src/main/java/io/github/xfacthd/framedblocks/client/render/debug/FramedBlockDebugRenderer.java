package io.github.xfacthd.framedblocks.client.render.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.render.debug.AttachDebugRenderersEvent;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import io.github.xfacthd.framedblocks.api.util.Utils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class FramedBlockDebugRenderer
{
    private static final Map<BlockEntityType<?>, Set<BlockDebugRenderer<? extends IFramedBlockEntity>>> RENDERERS_BY_TYPE = new IdentityHashMap<>();
    private static final Set<BlockDebugRenderer<?>> RENDERERS = new ReferenceOpenHashSet<>();
    private static final ContextKey<DebugRenderState> DATA_KEY = new ContextKey<>(Utils.id("debug_renderers"));

    @SuppressWarnings("unchecked")
    private static void onExtractRenderState(ExtractLevelRenderStateEvent event)
    {
        Set<BlockDebugRenderer<?>> activeRenderers = new ReferenceOpenHashSet<>();
        for (BlockDebugRenderer<?> renderer : RENDERERS)
        {
            if (renderer.isEnabled())
            {
                activeRenderers.add(renderer);
            }
        }

        if (activeRenderers.isEmpty()) return;

        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        BlockPos pos = blockHit.getBlockPos();
        if (!(level.getBlockEntity(pos) instanceof IFramedBlockEntity be)) return;

        Set<BlockDebugRenderer<?>> renderers = RENDERERS_BY_TYPE.get(be.getType());
        if (renderers.isEmpty()) return;

        renderers = Set.copyOf(Sets.intersection(renderers, activeRenderers));

        LevelRenderState renderState = event.getRenderState();
        float partialTick = event.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        for (BlockDebugRenderer<?> renderer : renderers)
        {
            //noinspection NullableProblems - IDEA's nullability checks are broken
            ((BlockDebugRenderer<IFramedBlockEntity>) renderer).extract(be, blockHit, partialTick, renderState);
        }
        renderState.setRenderData(DATA_KEY, new DebugRenderState(pos, renderers));
    }

    private static void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event)
    {
        LevelRenderState levelRenderState = event.getLevelRenderState();
        DebugRenderState renderState = levelRenderState.getRenderData(DATA_KEY);
        if (renderState == null) return;

        PoseStack poseStack = event.getPoseStack();

        Vec3 offset = Vec3.atLowerCornerOf(renderState.pos).subtract(levelRenderState.cameraRenderState.pos);
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        for (BlockDebugRenderer<?> renderer : renderState.renderers)
        {
            poseStack.pushPose();
            renderer.render(levelRenderState, poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        poseStack.popPose();
        buffer.endBatch();
    }

    public static void init()
    {
        if (Utils.PRODUCTION) return;

        ModLoader.postEvent(new AttachDebugRenderersEvent((type, renderer) ->
        {
            RENDERERS_BY_TYPE.computeIfAbsent(type, $ -> new ReferenceOpenHashSet<>()).add(renderer);
            RENDERERS.add(renderer);
        }));

        NeoForge.EVENT_BUS.addListener(FramedBlockDebugRenderer::onExtractRenderState);
        NeoForge.EVENT_BUS.addListener(FramedBlockDebugRenderer::onRenderLevelStage);
    }

    private record DebugRenderState(BlockPos pos, Set<BlockDebugRenderer<?>> renderers) { }

    private FramedBlockDebugRenderer() { }
}
