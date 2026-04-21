package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.special.FramedBannerFlagModel;
import io.github.xfacthd.framedblocks.client.render.block.state.FramedBannerRenderState;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.interactive.banner.FramedBannerBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedBannerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.WallAndGroundTransformations;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class FramedBannerRenderer implements BlockEntityRenderer<FramedBannerBlockEntity, FramedBannerRenderState> {
    private static final Identifier BLOCKSTATE_LOC = Utils.id("framed_banner_flag");
    public static final StandaloneWrapperKey<FramedBannerFlagModel> WRAPPER_KEY = new StandaloneWrapperKey<>(FBContent.BLOCK_FRAMED_WALL_BANNER, BLOCKSTATE_LOC, true);
    private static final float SCALE = 0.6666667F * (20F/16F);
    private static final WallAndGroundTransformations<Matrix4fc> FLAG_TRANSFORMS = new WallAndGroundTransformations<>(
            FramedBannerRenderer::createWallTransform, FramedBannerRenderer::createGroundTransform, 16
    );

    @Nullable
    private final FramedBannerFlagModel flagModel;
    private final BannerModel standingModel;
    private final BannerModel wallModel;
    private final SpriteGetter sprites;
    @Nullable
    private final BlockState speciaSubmitState;

    public FramedBannerRenderer(BlockEntityRendererProvider.Context ctx) {
        this(ctx.entityModelSet(), ctx.sprites(), null);
    }

    public FramedBannerRenderer(SpecialModelRenderer.BakingContext ctx, BlockState state) {
        this(ctx.entityModelSet(), ctx.sprites(), state);
    }

    private FramedBannerRenderer(EntityModelSet modelSet, SpriteGetter sprites, @Nullable BlockState speciaSubmitState) {
        this.flagModel = Minecraft.getInstance()
                .getModelManager()
                .getStandaloneModel(WRAPPER_KEY.modelKey());
        this.standingModel = new BannerModel(modelSet.bakeLayer(ModelLayers.STANDING_BANNER));
        this.wallModel = new BannerModel(modelSet.bakeLayer(ModelLayers.WALL_BANNER));
        this.sprites = sprites;
        this.speciaSubmitState = speciaSubmitState;
    }

    @Override
    public void submit(FramedBannerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submitInternal(
                state.bannerModel,
                state.bannerTransform,
                state.modelRenderState,
                state.flagTransform,
                state.swing,
                poseStack,
                collector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0,
                state.breakProgress
        );
    }

    public void submitSpecial(CamoContainer<?, ?> camo, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, int outlineColor) {
        BlockState state = Objects.requireNonNull(speciaSubmitState);

        BlockModel blockModel;
        BannerModel bannerModel;
        Transformation bannerTransform;
        Matrix4fc flagTransform;
        if (state.getBlock() instanceof FramedBannerBlock) {
            int rotation = state.getValue(BlockStateProperties.ROTATION_16);
            blockModel = Objects.requireNonNull(flagModel).getModel(rotation);
            bannerModel = standingModel;
            bannerTransform = BannerRenderer.TRANSFORMATIONS.freeTransformations(rotation);
            flagTransform = FLAG_TRANSFORMS.freeTransformations(rotation);
        } else {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            blockModel = Objects.requireNonNull(flagModel).getModel(facing);
            bannerModel = wallModel;
            bannerTransform = BannerRenderer.TRANSFORMATIONS.wallTransformation(facing);
            flagTransform = FLAG_TRANSFORMS.wallTransformation(facing);
        }

        BlockModelRenderState modelRenderState = new BlockModelRenderState();
        ModelData modelData = ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedBlockData(state, camo, false, null));
        BlockDisplayContext context = new FramedBannerFlagModel.DisplayContext(BlockAndTintGetter.EMPTY, BlockPos.ZERO, modelData);
        blockModel.update(modelRenderState, state, context, 42L);

        submitInternal(bannerModel, bannerTransform, modelRenderState, flagTransform, 0F, poseStack, collector, lightCoords, overlayCoords, outlineColor, null);
    }

    private void submitInternal(
            BannerModel bannerModel,
            Transformation bannerTransform,
            BlockModelRenderState modelRenderState,
            Matrix4fc flagTransform,
            float swing,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int lightCoords,
            int overlayCoords,
            int outlineColor,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        poseStack.pushPose();
        poseStack.mulPose(bannerTransform);
        collector.submitModel(bannerModel, Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, Sheets.BANNER_BASE, sprites, outlineColor, breakProgress);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(flagTransform);
        poseStack.mulPose(Axis.XP.rotation((-0.0125F + 0.01F * Mth.cos(Math.PI * 2 * swing)) * (float) Math.PI));
        modelRenderState.submitMultiLayer(poseStack, collector, lightCoords, overlayCoords, outlineColor);
        poseStack.popPose();
    }

    @Override
    public FramedBannerRenderState createRenderState() {
        return new FramedBannerRenderState();
    }

    @Override
    public void extractRenderState(
            FramedBannerBlockEntity blockEntity,
            FramedBannerRenderState renderState,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        Level level = Objects.requireNonNull(blockEntity.getLevel());
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();

        BlockPos topPos;
        BlockModel blockModel;
        if (state.getBlock() instanceof FramedBannerBlock) {
            int rotation = state.getValue(BlockStateProperties.ROTATION_16);
            topPos = pos.above();
            blockModel = Objects.requireNonNull(flagModel).getModel(rotation);
            renderState.bannerModel = standingModel;
            renderState.bannerTransform = BannerRenderer.TRANSFORMATIONS.freeTransformations(rotation);
            renderState.flagTransform = FLAG_TRANSFORMS.freeTransformations(rotation);
        } else {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            topPos = pos;
            blockModel = Objects.requireNonNull(flagModel).getModel(facing);
            renderState.bannerModel = wallModel;
            renderState.bannerTransform = BannerRenderer.TRANSFORMATIONS.wallTransformation(facing);
            renderState.flagTransform = FLAG_TRANSFORMS.wallTransformation(facing);
        }
        BlockDisplayContext context = new FramedBannerFlagModel.DisplayContext(ClientUtils.asTintGetter(level), topPos, level.getModelData(pos));
        blockModel.update(renderState.modelRenderState, state, context, 42L);

        long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
        renderState.swing = ((float) Math.floorMod(pos.getX() * 7L + pos.getY() * 9L + pos.getZ() * 13L + gameTime, 100L) + partialTick) / 100.0F;
    }

    @Override
    public boolean shouldRender(FramedBannerBlockEntity blockEntity, Vec3 cameraPosition) {
        return flagModel != null && BlockEntityRenderer.super.shouldRender(blockEntity, cameraPosition);
    }

    @Override
    public AABB getRenderBoundingBox(FramedBannerBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        boolean standing = blockEntity.getBlockState().getBlock() instanceof FramedBannerBlock;
        return AABB.encapsulatingFullBlocks(pos, standing ? pos.above() : pos.below());
    }

    private static Matrix4fc createWallTransform(Direction dir) {
        return createTransform(dir.toYRot(), 1F, -.175F, .125F);
    }

    private static Matrix4fc createGroundTransform(int segment) {
        return createTransform(RotationSegment.convertToDegrees(segment), 2F, -.2F, .65F);
    }

    private static Matrix4fc createTransform(float angle, float yOffPreScale, float yOffPostScale, float zOffPostScale) {
        PoseStack poseStack = new PoseStack();

        poseStack.translate(.5, 0, .5);
        poseStack.mulPose(Axis.YN.rotationDegrees(angle));
        poseStack.translate(0, yOffPreScale, -.5F);
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.translate(-.5F, yOffPostScale, zOffPostScale);

        return new Matrix4f(poseStack.last().pose());
    }
}
