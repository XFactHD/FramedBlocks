package io.github.xfacthd.framedblocks.client.render.debug.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import io.github.xfacthd.framedblocks.api.render.fakelevel.FreestandingBlockRenderFakeLevel;
import io.github.xfacthd.framedblocks.api.util.Triangle;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.DevToolsConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class QuadWindingDebugRenderer implements BlockDebugRenderer<IFramedBlockEntity> {
    public static final QuadWindingDebugRenderer INSTANCE = new QuadWindingDebugRenderer();
    private static final Direction[] DIRECTIONS = Arrays.copyOf(Direction.values(), 7);
    private static final int[] VERT_INDEX_COLORS = { 0xFFFFFFFF, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF };
    private static final RandomSource RANDOM = RandomSource.create();
    private static final ContextKey<QuadWindingRenderState> DATA_KEY = new ContextKey<>(Utils.id("quad_winding_debug_renderer"));

    @Override
    public void extract(IFramedBlockEntity be, BlockHitResult blockHit, float partialTick, LevelRenderState renderState) {
        BlockPos pos = be.getBlockPos();
        BlockState state = be.getBlockState();
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

        ModelData modelData = Objects.requireNonNull(be.getLevel()).getModelData(pos);
        BlockStateModel model = ModelUtils.getModel(state);
        Vec3 eyePos = player.getEyePosition(partialTick).subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3 viewVector = player.getViewVector(partialTick).normalize();
        boolean sneak = player.isShiftKeyDown();

        renderState.setRenderData(DATA_KEY, new QuadWindingRenderState(state, pos, modelData, model, eyePos, viewVector, sneak));
    }

    @Override
    public void submit(LevelRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector) {
        QuadWindingRenderState renderData = renderState.getRenderData(DATA_KEY);
        if (renderData == null) {
            return;
        }

        Vec3 eyePos = renderData.eyePos;
        Vec3 viewVector = renderData.viewVector;
        boolean sneak = renderData.sneak;
        Vector3f vertNorm = new Vector3f();
        List<BlockStateModelPart> srcParts = new ObjectArrayList<>();
        renderData.model.collectParts(renderData, renderData.pos, renderData.state, RANDOM, srcParts);
        for (BlockStateModelPart part : srcParts) {
            for (Direction side : DIRECTIONS) {
                for (BakedQuad quad : part.getQuads(side)) {
                    BakedNormals.unpack(quad.bakedNormals().normal(0), vertNorm);
                    float dot = vertNorm.dot((float) viewVector.x, (float) viewVector.y, (float) viewVector.z);
                    if (dot > -.75F || (!sneak && !checkViewIntersectsQuad(quad, eyePos, viewVector))) {
                        continue;
                    }

                    for (int i = 0; i < 4; i++) {
                        Vector3fc vertPos = quad.position(i);

                        poseStack.pushPose();
                        poseStack.translate(vertPos.x(), vertPos.y(), vertPos.z());
                        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                        poseStack.scale(1F / 16F, 1F / 16F, 1F / 16F);

                        collector.submitText(
                                poseStack,
                                -2.5F,
                                -3.5F,
                                FormattedCharSequence.forward(Integer.toString(i), Style.EMPTY),
                                false,
                                Font.DisplayMode.SEE_THROUGH,
                                LightCoordsUtil.FULL_BRIGHT,
                                VERT_INDEX_COLORS[i],
                                0x00000000,
                                0
                        );

                        poseStack.popPose();
                    }
                }
            }
        }
    }

    private static boolean checkViewIntersectsQuad(BakedQuad quad, Vec3 eyePos, Vec3 viewVector) {
        Triangle triOne = new Triangle(
                new Vec3(quad.position(0)),
                new Vec3(quad.position(1)),
                new Vec3(quad.position(2))
        );
        if (triOne.intersects(eyePos, viewVector)) {
            return true;
        }

        Triangle triTwo = new Triangle(
                new Vec3(quad.position(2)),
                new Vec3(quad.position(3)),
                new Vec3(quad.position(0))
        );
        return triTwo.intersects(eyePos, viewVector);
    }

    @Override
    public boolean isEnabled() {
        return DevToolsConfig.VIEW.isQuadWindingDebugRendererEnabled();
    }

    private record QuadWindingRenderState(
            BlockState state,
            BlockPos pos,
            ModelData modelData,
            BlockStateModel model,
            Vec3 eyePos,
            Vec3 viewVector,
            boolean sneak
    ) implements FreestandingBlockRenderFakeLevel { }
}
