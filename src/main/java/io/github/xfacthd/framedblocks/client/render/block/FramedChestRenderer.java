package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.render.RenderUtils;
import io.github.xfacthd.framedblocks.api.util.SingleBlockFakeLevel;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.special.FramedChestLidModel;
import io.github.xfacthd.framedblocks.client.render.block.state.FramedChestRenderState;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.cube.FramedChestBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.ChestState;
import io.github.xfacthd.framedblocks.common.data.property.LatchType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class FramedChestRenderer implements BlockEntityRenderer<FramedChestBlockEntity, FramedChestRenderState>
{
    private static final Identifier BLOCKSTATE_LOC = Utils.id("framed_chest_lid");
    public static final StandaloneWrapperKey<FramedChestLidModel> WRAPPER_KEY = new StandaloneWrapperKey<>(FBContent.BLOCK_FRAMED_CHEST, BLOCKSTATE_LOC);
    private static final RandomSource RANDOM = RandomSource.create();

    @Nullable
    private final FramedChestLidModel lidModel;

    @SuppressWarnings("unused")
    public FramedChestRenderer(BlockEntityRendererProvider.Context ctx)
    {
        this.lidModel = ctx.blockRenderDispatcher()
                .getBlockModelShaper()
                .getModelManager()
                .getStandaloneModel(WRAPPER_KEY.modelKey());
    }

    @Override
    public void submit(FramedChestRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
    {
        poseStack.pushPose();

        float xOff = renderState.rotOriginX;
        float zOff = renderState.rotOriginZ;
        poseStack.translate(xOff, 9F/16F, zOff);
        poseStack.mulPose(renderState.lidAngle);
        poseStack.translate(-xOff, -9F/16F, -zOff);

        RANDOM.setSeed(42);
        // Cannot use BlockRenderDispatcher#renderBatched() due to incorrect shading of rotated surfaces
        RenderUtils.submitModel(
                renderState.state,
                renderState.level,
                renderState.pos,
                poseStack,
                submitNodeCollector,
                renderState.model,
                RANDOM,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    @Override
    public FramedChestRenderState createRenderState()
    {
        return new FramedChestRenderState();
    }

    @Override
    public void extractRenderState(
            FramedChestBlockEntity blockEntity,
            FramedChestRenderState renderState,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    )
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        Level level = Objects.requireNonNull(blockEntity.getLevel());
        BlockPos pos = renderState.pos = blockEntity.getBlockPos();
        BlockState state = renderState.state = blockEntity.getBlockState();
        renderState.level = SingleBlockFakeLevel.atPos(level, pos, state, level.getModelData(pos));

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
        LatchType latch = state.getValue(PropertyHolder.LATCH_TYPE);
        renderState.model = Objects.requireNonNull(lidModel).getModel(dir, type, latch);

        var result = FramedChestBlock.combine(blockEntity, true);
        ChestState chestState = result.apply(FramedChestBlock.STATE_COMBINER);
        long lastChange = result.apply(FramedChestBlock.OPENNESS_COMBINER).orElse(0L);
        float angle = calculateAngle(level, chestState, dir, lastChange, partialTick);
        renderState.lidAngle = Utils.isX(dir) ? Axis.ZP.rotationDegrees(angle) : Axis.XN.rotationDegrees(angle);

        renderState.rotOriginX = Utils.isX(dir) ? (Utils.isPositive(dir) ? 1F/16F : 15F/16F) : 0;
        renderState.rotOriginZ = Utils.isZ(dir) ? (Utils.isPositive(dir) ? 1F/16F : 15F/16F) : 0;
    }

    private static float calculateAngle(Level level, ChestState chestState, Direction dir, long lastChange, float partialTicks)
    {
        float diff = (float) (level.getGameTime() - lastChange) + partialTicks;

        float factor = Mth.lerp(diff / 10F, 0, 1);
        if (chestState == ChestState.CLOSING) { factor = 1F - factor; }

        factor = 1.0F - factor;
        factor = 1.0F - factor * factor * factor;

        float angle = Mth.clamp(factor * 90F, 0F, 90F);
        if (!Utils.isPositive(dir)) { angle *= -1F; }

        return angle;
    }

    @Override
    public boolean shouldRender(FramedChestBlockEntity be, Vec3 camera)
    {
        if (lidModel == null || be.isRemoved()) return false;

        ChestState state = FramedChestBlock.combine(be, true).apply(FramedChestBlock.STATE_COMBINER);
        return state != ChestState.CLOSED && BlockEntityRenderer.super.shouldRender(be, camera);
    }

    @Override
    public AABB getRenderBoundingBox(FramedChestBlockEntity blockEntity)
    {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - .25, pos.getY() + .5625, pos.getZ() - .25, pos.getX() + 1.25, pos.getY() + 1.5, pos.getZ() + 1.25);
    }
}
