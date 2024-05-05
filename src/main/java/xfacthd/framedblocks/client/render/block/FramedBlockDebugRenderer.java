package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.render.debug.*;

import java.util.*;

public class FramedBlockDebugRenderer implements BlockEntityRenderer<FramedBlockEntity>
{
    private static final Map<BlockEntityType<? extends FramedBlockEntity>, Set<BlockDebugRenderer<? extends FramedBlockEntity>>> RENDERERS_BY_TYPE = new IdentityHashMap<>();

    public FramedBlockDebugRenderer(@SuppressWarnings("unused") BlockEntityRendererProvider.Context ctx) { }

    @Override
    @SuppressWarnings("unchecked")
    public void render(FramedBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || !blockHit.getBlockPos().equals(be.getBlockPos()))
        {
            return;
        }

        for (BlockDebugRenderer<? extends FramedBlockEntity> renderer : RENDERERS_BY_TYPE.get(be.getType()))
        {
            poseStack.pushPose();
            ((BlockDebugRenderer<FramedBlockEntity>) renderer).render(be, blockHit, partialTick, poseStack, buffer, light, overlay);
            poseStack.popPose();
        }
    }

    public static void init()
    {
        if (FMLEnvironment.production) return;

        ModLoader.postEvent(new AttachDebugRenderersEvent((type, renderer) ->
        {
            if (!renderer.isEnabled()) return;
            RENDERERS_BY_TYPE.computeIfAbsent(type, $ -> new ReferenceOpenHashSet<>()).add(renderer);
        }));
    }

    public static Set<BlockEntityType<? extends FramedBlockEntity>> getTargetTypes()
    {
        return RENDERERS_BY_TYPE.keySet();
    }
}
