package io.github.xfacthd.framedblocks.client.screen.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public final class SpinningItemPictureInPictureRenderer extends PictureInPictureRenderer<SpinningItemPictureInPictureRenderer.RenderState>
{
    private static final Quaternionf ROT_22_5_XP = Axis.XP.rotationDegrees(22.5F);

    private final SubmitNodeCollector submitNodeCollector;
    private final FeatureRenderDispatcher featureRenderDispatcher;
    @Nullable
    private Object lastModelIdentity = null;
    private int lastRotY = 0;

    public SpinningItemPictureInPictureRenderer(MultiBufferSource.BufferSource bufferSource)
    {
        super(bufferSource);
        this.submitNodeCollector = Minecraft.getInstance().gameRenderer.getSubmitNodeStorage();
        this.featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
    }

    @Override
    protected void renderToTexture(RenderState state, PoseStack poseStack)
    {
        TrackingItemStackRenderState renderState = state.renderState;

        poseStack.scale(1, -1, -1);
        poseStack.mulPose(ROT_22_5_XP);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotY));

        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        renderState.submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        featureRenderDispatcher.renderAllFeatures();

        lastModelIdentity = renderState.getModelIdentity();
        lastRotY = state.rotY;
    }

    @Override
    protected float getTranslateY(int height, int guiScale)
    {
        return height / 2F;
    }

    @Override
    protected boolean textureIsReadyToBlit(RenderState state)
    {
        if (state.rotY != lastRotY) { return false; }

        TrackingItemStackRenderState renderState = state.renderState;
        return !renderState.isAnimated() && renderState.getModelIdentity().equals(lastModelIdentity);
    }

    @Override
    protected String getTextureLabel()
    {
        return "framedblocks spinning item";
    }

    @Override
    public Class<RenderState> getRenderStateClass()
    {
        return RenderState.class;
    }

    public record RenderState(
            TrackingItemStackRenderState renderState,
            int rotY,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle bounds,
            @Nullable ScreenRectangle scissorArea
    ) implements PictureInPictureRenderState
    {
        public RenderState(
                TrackingItemStackRenderState renderState,
                int rotY,
                int x0,
                int y0,
                int x1,
                int y1,
                float scale,
                @Nullable ScreenRectangle scissorArea
        )
        {
            this(renderState, rotY, x0, y0, x1, y1, scale, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea), scissorArea);
        }
    }
}
