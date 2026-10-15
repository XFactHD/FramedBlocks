package io.github.xfacthd.framedblocks.client.render.block.state;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class FramedBannerRenderState extends BlockEntityRenderState {
    private static final Matrix4fc IDENTITY = new Matrix4f();

    public final ModelRenderState modelRenderState = new ModelRenderState();
    @UnknownNullability
    public BannerModel bannerModel = null;
    public Transformation bannerTransform = Transformation.IDENTITY;
    public Matrix4fc flagTransform = IDENTITY;
    public float swing;

    public static final class ModelRenderState extends BlockModelRenderState {
        public void submitCrumblingOverlay(PoseStack poseStack, OrderedSubmitNodeCollector collector, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
            if (modelParts != null) {
                collector.submitBreakingBlockModel(poseStack, modelParts, breakProgress.progress(), hasTranslucency);
            }
        }
    }
}
