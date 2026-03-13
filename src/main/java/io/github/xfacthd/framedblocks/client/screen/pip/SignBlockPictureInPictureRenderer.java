package io.github.xfacthd.framedblocks.client.screen.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.render.Quaternions;
import io.github.xfacthd.framedblocks.api.render.RenderUtils;
import io.github.xfacthd.framedblocks.api.util.SingleBlockFakeLevel;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class SignBlockPictureInPictureRenderer extends PictureInPictureRenderer<SignBlockPictureInPictureRenderer.RenderState>
{
    private static final RandomSource RANDOM = RandomSource.create();

    @Nullable
    private BlockState lastSignState;
    private BlockPos lastSignPos = BlockPos.ZERO;
    private FramedBlockData lastBlockData = FramedBlockData.EMPTY;

    public SignBlockPictureInPictureRenderer(MultiBufferSource.BufferSource bufferSource)
    {
        super(bufferSource);
    }

    @Override
    protected void renderToTexture(RenderState state, PoseStack poseStack)
    {
        poseStack.mulPose(Axis.YN.rotationDegrees(state.yRot));
        poseStack.mulPose(Quaternions.ZP_180);
        poseStack.translate(-.5, 0, -.5);

        Minecraft minecraft = Minecraft.getInstance();

        minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        BlockRenderDispatcher renderer = minecraft.getBlockRenderer();
        RANDOM.setSeed(42);
        RenderUtils.renderModel(
                state.signState,
                state.fakeLevel,
                state.signPos,
                poseStack.last(),
                bufferSource,
                renderer.getBlockModel(state.signState),
                RANDOM,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY
        );

        lastSignState = state.signState;
        lastSignPos = state.signPos;
        lastBlockData = unpackData(state.signBlockData);
    }

    @Override
    protected boolean textureIsReadyToBlit(RenderState renderState)
    {
        if (lastSignState != renderState.signState) { return false; }
        if (!lastSignPos.equals(renderState.signPos)) { return false; }

        return lastBlockData.equals(unpackData(renderState.signBlockData));
    }

    private static FramedBlockData unpackData(ModelData modelData)
    {
        AbstractFramedBlockData data = modelData.get(AbstractFramedBlockData.PROPERTY);
        return data != null ? data.unwrap(false) : FramedBlockData.EMPTY;
    }

    @Override
    protected String getTextureLabel()
    {
        return "framedblocks sign";
    }

    @Override
    public Class<RenderState> getRenderStateClass()
    {
        return RenderState.class;
    }

    public record RenderState(
            BlockAndTintGetter fakeLevel,
            BlockState signState,
            BlockPos signPos,
            ModelData signBlockData,
            float yRot,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea,
            @Nullable ScreenRectangle bounds
    ) implements PictureInPictureRenderState
    {
        public static RenderState create(
                FramedSignBlockEntity sign,
                float yRot,
                int x0,
                int y0,
                int x1,
                int y1,
                float scale,
                @Nullable ScreenRectangle scissorArea
        )
        {
            BlockState state = sign.getBlockState();
            BlockPos pos = sign.getBlockPos();
            ModelData modelData = sign.getModelData(false, state);
            BlockAndTintGetter fakeLevel = SingleBlockFakeLevel.atPos(Objects.requireNonNull(sign.getLevel()), pos, state, modelData);
            ScreenRectangle bounds = PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
            return new RenderState(fakeLevel, state, pos, modelData, yRot, x0, y0, x1, y1, scale, scissorArea, bounds);
        }
    }
}
