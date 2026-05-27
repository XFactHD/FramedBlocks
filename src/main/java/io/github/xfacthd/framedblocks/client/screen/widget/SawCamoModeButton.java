package io.github.xfacthd.framedblocks.client.screen.widget;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;

public final class SawCamoModeButton extends Button {
    private static final Identifier FRAME_TEXTURE = Utils.id("block/framed_block");
    private static final Identifier GRANITE_SPRITE = Utils.id("minecraft", "block/polished_granite");
    public static final Component TOOLTIP_RAW = Utils.translate("tooltip", "framing_saw.camo_mode.raw");
    public static final Component TOOLTIP_CAMO = Utils.translate("tooltip", "framing_saw.camo_mode.camo");
    private static final Tooltip BUILT_TOOLTIP_RAW = Tooltip.create(TOOLTIP_RAW);
    private static final Tooltip BUILT_TOOLTIP_CAMO = Tooltip.create(TOOLTIP_CAMO);

    private final BooleanSupplier stateSupplier;
    private final TextureAtlasSprite frameSprite;
    private final TextureAtlasSprite graniteSprite;

    public SawCamoModeButton(Minecraft minecraft, int x, int y, OnPress onPress, BooleanSupplier stateSupplier) {
        super(x, y, 18, 18, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.stateSupplier = stateSupplier;
        TextureAtlas blockAtlas = minecraft.getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        this.frameSprite = blockAtlas.getSprite(FRAME_TEXTURE);
        this.graniteSprite = blockAtlas.getSprite(GRANITE_SPRITE);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractDefaultSprite(graphics);
        if (stateSupplier.getAsBoolean()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, graniteSprite, getX() + 3, getY() + 3, getWidth() - 6, getHeight() - 6);
            setTooltip(BUILT_TOOLTIP_CAMO);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, frameSprite, getX() + 3, getY() + 3, getWidth() - 6, getHeight() - 6);
            setTooltip(BUILT_TOOLTIP_RAW);
        }
    }
}
