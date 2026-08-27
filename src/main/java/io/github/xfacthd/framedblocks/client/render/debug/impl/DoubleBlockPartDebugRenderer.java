package io.github.xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.BlockHitResult;

public class DoubleBlockPartDebugRenderer implements BlockDebugRenderer<FramedDoubleBlockEntity> {
    public static final DoubleBlockPartDebugRenderer INSTANCE = new DoubleBlockPartDebugRenderer();

    private DoubleBlockPartDebugRenderer() { }

    @Override
    public void extract(FramedDoubleBlockEntity be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState) { }

    @Override
    public void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) { }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
