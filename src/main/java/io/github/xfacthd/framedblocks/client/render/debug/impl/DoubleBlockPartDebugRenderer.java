package io.github.xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.model.block.FramedBlockDisplayContext;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.Objects;
import java.util.function.Supplier;

public class DoubleBlockPartDebugRenderer implements BlockDebugRenderer<FramedDoubleBlockEntity> {
    public static final DoubleBlockPartDebugRenderer INSTANCE = new DoubleBlockPartDebugRenderer();
    private static final Supplier<FramedBlockData> MODEL_DATA = Lazy.of(() -> new FramedBlockData(null, new SimpleBlockCamoContainer(
            Blocks.STONE.defaultBlockState(), FBContent.FACTORY_BLOCK.get()
    ), false, null));
    private static final ContextKey<BlockPartRenderState> DATA_KEY = new ContextKey<>(Utils.id("double_block_part_debug_renderer"));
    private static final int COLOR_PRIMARY = 0xFFFF0000;
    private static final int COLOR_SECONDARY = 0xFF00FF00;

    private DoubleBlockPartDebugRenderer() { }

    @Override
    public void extract(FramedDoubleBlockEntity be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof IFramedBlock)) {
            return;
        }

        DoubleBlockParts parts = be.getParts();
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        boolean secondary = be.debugHitSecondary(blockHit, player);
        BlockState partState = secondary ? parts.stateTwo() : parts.stateOne();

        BlockPartRenderState partRenderState = new BlockPartRenderState(secondary ? COLOR_SECONDARY : COLOR_PRIMARY);

        BlockAndTintGetter level = ClientUtils.asTintGetter(be.level());
        ModelData modelData = be.getModelData().derive().with(AbstractFramedBlockData.PROPERTY, MODEL_DATA.get()).build();
        BlockDisplayContext context = new FramedBlockDisplayContext(level, be.getBlockPos(), state, modelData);
        Minecraft.getInstance().getBlockModelResolver().update(partRenderState, partState, context);

        renderState.haveGlowingEntities = true;
        renderState.setRenderData(DATA_KEY, partRenderState);
    }

    @Override
    public void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) {
        BlockPartRenderState data = renderState.getRenderData(DATA_KEY);
        if (data != null) {
            data.submitOnlyOutline(poseStack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, data.color);
        }
    }

    @Override
    public boolean isEnabled() {
        return DevToolsConfig.VIEW.isDoubleBlockPartHitDebugRendererEnabled();
    }

    private static final class BlockPartRenderState extends BlockModelRenderState {
        private final int color;

        private BlockPartRenderState(int color) {
            this.color = color;
        }
    }
}
