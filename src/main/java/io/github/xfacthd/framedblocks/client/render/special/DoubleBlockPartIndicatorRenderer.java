package io.github.xfacthd.framedblocks.client.render.special;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.model.block.FramedBlockDisplayContext;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.Objects;
import java.util.function.Supplier;

public final class DoubleBlockPartIndicatorRenderer {
    private static final Supplier<FramedBlockData> MODEL_DATA = Lazy.of(() -> new FramedBlockData(null, new SimpleBlockCamoContainer(
            Blocks.STONE.defaultBlockState(), FBContent.FACTORY_BLOCK.get()
    ), false, null));
    private static final ContextKey<BlockPartRenderState> DATA_KEY = new ContextKey<>(Utils.id("double_block_part_debug_renderer"));
    private static final int COLOR_PRIMARY = 0xFFFF0000;
    private static final int COLOR_SECONDARY = 0xFF00FF00;

    public static void onExtractRenderState(ExtractLevelRenderStateEvent event) {
        if (!ClientConfig.VIEW.isDoubleBlockPartIndicatorEnabled()) {
            return;
        }

        HitResult hit = Minecraft.getInstance().hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        BlockPos pos = blockHit.getBlockPos();
        if (!(level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)) {
            return;
        }

        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        boolean secondary = be.debugHitSecondary(blockHit, player);
        BlockPartRenderState partRenderState = new BlockPartRenderState(pos, secondary ? COLOR_SECONDARY : COLOR_PRIMARY);

        ModelData modelData = be.getModelData().derive().with(AbstractFramedBlockData.PROPERTY, MODEL_DATA.get()).build();
        BlockDisplayContext context = new FramedBlockDisplayContext(level, be.getBlockPos(), be.getBlockState(), modelData);
        Minecraft.getInstance().getBlockModelResolver().update(partRenderState, be.getParts().get(secondary), context);

        LevelRenderState renderState = event.getRenderState();
        renderState.haveGlowingEntities = true;
        renderState.setRenderData(DATA_KEY, partRenderState);
    }

    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        LevelRenderState levelRenderState = event.getLevelRenderState();
        BlockPartRenderState data = levelRenderState.getRenderData(DATA_KEY);
        if (data != null) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 offset = Vec3.atLowerCornerOf(data.pos).subtract(levelRenderState.cameraRenderState.pos);
            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);

            data.submitOnlyOutline(poseStack, event.getSubmitNodeCollector(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, data.color);

            poseStack.popPose();
        }
    }

    private static final class BlockPartRenderState extends BlockModelRenderState {
        private final BlockPos pos;
        private final int color;

        private BlockPartRenderState(BlockPos pos, int color) {
            this.pos = pos;
            this.color = color;
        }
    }

    private DoubleBlockPartIndicatorRenderer() { }
}
