package io.github.xfacthd.framedblocks.client.screen.widget;

import io.github.xfacthd.framedblocks.api.util.Utils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class IndicatorButton extends Button.Plain {
    private static final Identifier INDICATOR_TEXTURE = Utils.id("indicator");
    private static final Identifier INDICATOR_CHECKED_TEXTURE = Utils.id("indicator_checked");
    private static final int INDICATOR_SIZE = 13;

    private final BooleanConsumer stateConsumer;
    private boolean checked;

    public IndicatorButton(int x, int y, Component text, boolean initialState, BooleanConsumer stateConsumer) {
        super(x, y, INDICATOR_SIZE, INDICATOR_SIZE, text, _ -> {}, Button.DEFAULT_NARRATION);
        this.stateConsumer = stateConsumer;
        this.checked = initialState;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        Identifier tex = checked ? INDICATOR_CHECKED_TEXTURE : INDICATOR_TEXTURE;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, tex, getX(), getY(), INDICATOR_SIZE, INDICATOR_SIZE);
    }

    @Override
    protected void extractDefaultLabel(ActiveTextCollector textCollector) {
        int x = getX() + INDICATOR_SIZE + 3;
        int y = getY() + 3;
        textCollector.accept(x, y, getMessage());
    }

    @Override
    public void onPress(InputWithModifiers input) {
        checked = !checked;
        stateConsumer.accept(checked);
    }
}
