package io.github.xfacthd.framedblocks.common.compat.jade;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.SingleBlockFakeLevel;
import io.github.xfacthd.framedblocks.client.screen.pip.BlockPictureInPictureRenderer;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.ui.Element;

import java.util.Objects;

final class FramedBlockElement extends Element
{
    private static final int SIZE = 18;
    private final BlockState state;
    private final SingleBlockFakeLevel fakeLevel;
    private final float scale;

    FramedBlockElement(BlockState state, FramedBlockEntity blockEntity)
    {
        this.width = SIZE;
        this.height = SIZE;
        IFramedBlock block = (IFramedBlock) state.getBlock();
        this.state = block.getJadeRenderState(state);
        boolean renderCamo = ClientConfig.VIEW.shouldRenderCamoInJade();
        ModelData modelData = renderCamo ? blockEntity.getModelData(false, this.state) : ModelData.EMPTY;
        this.fakeLevel = SingleBlockFakeLevel.atPos(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos(), state, modelData);
        this.scale = block.getJadeRenderScale(this.state);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (!state.isAir())
        {
            ScreenRectangle bounds = new ScreenRectangle(getX(), getY(), SIZE, SIZE).transformMaxBounds(graphics.pose());
            graphics.submitPictureInPictureRenderState(BlockPictureInPictureRenderer.RenderState.of(
                    state, fakeLevel, bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), scale, graphics.peekScissorStack()
            ));
        }
    }

    @Override
    @Nullable
    public Component getNarration()
    {
        return state.isAir() ? null : state.getBlock().getName();
    }
}
