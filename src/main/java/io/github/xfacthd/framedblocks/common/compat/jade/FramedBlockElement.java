package io.github.xfacthd.framedblocks.common.compat.jade;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.client.screen.pip.BlockPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.ui.Element;

final class FramedBlockElement extends Element {
    private static final int SIZE = 18;
    private static final float RENDER_SIZE = 16F;
    private static final ItemTransform DEFAULT_TRANSFORM = new ItemTransform(
            new Vector3f(30, 225, 0), new Vector3f(), new Vector3f(0.625F, 0.625F, 0.625F)
    );
    private static final Quaternionfc LIGHT_FIX_ROT = Axis.YP.rotationDegrees(285);

    private final BlockState state;
    private final IFramedBlockEntity blockEntity;
    private final BlockPictureInPictureRenderer.RenderConfig config;
    private final float scale;

    FramedBlockElement(BlockState state, IFramedBlockEntity blockEntity) {
        this.width = SIZE;
        this.height = SIZE;
        IFramedBlock block = (IFramedBlock) state.getBlock();
        this.state = block.getJadeRenderState(state);
        this.blockEntity = blockEntity;
        this.scale = block.getJadeRenderScale(this.state);
        this.config = new BlockPictureInPictureRenderer.RenderConfig(
                poseStack -> {
                    poseStack.scale(RENDER_SIZE * scale, -RENDER_SIZE * scale, -RENDER_SIZE * scale);
                    DEFAULT_TRANSFORM.apply(false, poseStack.last());
                    poseStack.translate(.5, .5, .5);
                    poseStack.last().normal().rotate(LIGHT_FIX_ROT);
                    poseStack.translate(-.5, -.5, -.5);
                },
                Lighting.Entry.ITEMS_3D,
                ClientConfig.VIEW.shouldRenderCamoInJade()
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!state.isAir()) {
            ScreenRectangle bounds = new ScreenRectangle(getX(), getY(), SIZE, SIZE).transformMaxBounds(graphics.pose());
            graphics.submitPictureInPictureRenderState(BlockPictureInPictureRenderer.RenderState.create(
                    blockEntity, state, config, bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), scale, graphics.peekScissorStack()
            ));
        }
    }

    @Override
    public @Nullable Component getNarration() {
        return state.isAir() ? null : state.getBlock().getName();
    }
}
