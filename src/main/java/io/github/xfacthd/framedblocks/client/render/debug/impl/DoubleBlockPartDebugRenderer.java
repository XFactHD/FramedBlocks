package io.github.xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.SingleBlockFakeLevel;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.Objects;

public class DoubleBlockPartDebugRenderer implements BlockDebugRenderer<FramedDoubleBlockEntity>
{
    public static final DoubleBlockPartDebugRenderer INSTANCE = new DoubleBlockPartDebugRenderer();
    private static final FramedBlockData MODEL_DATA = new FramedBlockData(new SimpleBlockCamoContainer(
            Blocks.STONE.defaultBlockState(), FBContent.FACTORY_BLOCK.get()
    ), false);
    private static final ContextKey<BlockPartRenderState> DATA_KEY = new ContextKey<>(Utils.id("double_block_part_debug_renderer"));
    private static final int COLOR_PRIMARY = 0xFFFF0000;
    private static final int COLOR_SECONDARY = 0xFF00FF00;

    private DoubleBlockPartDebugRenderer() { }

    @Override
    public void extract(FramedDoubleBlockEntity be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState)
    {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof IFramedBlock)) return;

        DoubleBlockParts parts = be.getParts();
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        boolean secondary = be.debugHitSecondary(blockHit, player);
        BlockState partState = secondary ? parts.stateTwo() : parts.stateOne();
        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(partState);

        ModelData modelData = be.getModelData().derive().with(AbstractFramedBlockData.PROPERTY, MODEL_DATA).build();
        BlockAndTintGetter level = SingleBlockFakeLevel.withoutRealLevel(partState, modelData);
        int color = secondary ? COLOR_SECONDARY : COLOR_PRIMARY;

        renderState.haveGlowingEntities = true;
        renderState.setRenderData(DATA_KEY, new BlockPartRenderState(level, partState, model, color));
    }

    @Override
    public void render(LevelRenderState renderState, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        BlockPartRenderState data = renderState.getRenderData(DATA_KEY);
        if (data == null) return;

        OutlineBufferSource outlineBuffer = Minecraft.getInstance().renderBuffers().outlineBufferSource();
        outlineBuffer.setColor(data.color);

        VertexConsumer consumer = outlineBuffer.getBuffer(RenderTypes.outline(ClientUtils.BLOCK_ATLAS));
        ModelBlockRenderer.renderModel(
                poseStack.last(),
                type -> consumer,
                data.model,
                1F, 1F, 1F,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                data.level,
                BlockPos.ZERO,
                data.partState
        );

        outlineBuffer.endOutlineBatch();
    }

    @Override
    public boolean isEnabled()
    {
        return DevToolsConfig.VIEW.isDoubleBlockPartHitDebugRendererEnabled();
    }

    private record BlockPartRenderState(
            BlockAndTintGetter level,
            BlockState partState,
            BlockStateModel model,
            int color
    ) { }
}
