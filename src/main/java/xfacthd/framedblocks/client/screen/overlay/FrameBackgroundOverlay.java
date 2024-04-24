package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.FramedItemFrameBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;

public final class FrameBackgroundOverlay extends BlockInteractOverlay
{
    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/frame_background_symbols.png");
    private static final ResourceLocation LEATHER_TEXTURE = new ResourceLocation("textures/item/leather.png");
    private static final Texture TEXTURE_BG = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 38, 22);
    private static final Texture TEXTURE_CROSS = new Texture(SYMBOL_TEXTURE, 22, 0, 16, 16, 38, 22);
    private static final Texture TEXTURE_LEATHER = new Texture(LEATHER_TEXTURE, 0, 0, 16, 16, 16, 16);
    public static final Component LINE_USE_CAMO_BG = Utils.translate("tooltip", "frame_bg.use_camo");
    public static final Component LINE_USE_LEATHER_BG = Utils.translate("tooltip", "frame_bg.use_leather");
    public static final Component LINE_SET_CAMO_BG = Utils.translate("tooltip", "frame_bg.set_camo");
    public static final Component LINE_SET_LEATHER_BG = Utils.translate("tooltip", "frame_bg.set_leather");
    private static final List<Component> LINES_FALSE = List.of(LINE_USE_CAMO_BG, LINE_SET_LEATHER_BG);
    private static final List<Component> LINES_TRUE = List.of(LINE_USE_LEATHER_BG, LINE_SET_CAMO_BG);

    public FrameBackgroundOverlay()
    {
        super(LINES_FALSE, LINES_TRUE, null, null, ClientConfig.VIEW::getFrameBackgroundMode);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.is(FBContent.ITEM_FRAMED_HAMMER.value());
    }

    @Override
    protected boolean isValidTarget(Target target)
    {
        return target.state().getBlock() instanceof FramedItemFrameBlock;
    }

    @Override
    protected boolean getState(Target target)
    {
        return target.state().getValue(PropertyHolder.LEATHER);
    }

    @Override
    protected Texture getTexture(Target target, boolean state, Texture texFalse, Texture texTrue)
    {
        return TEXTURE_BG;
    }

    @Override
    protected void renderAfterIcon(GuiGraphics graphics, Texture tex, int texX, int texY, Target target)
    {
        TEXTURE_LEATHER.draw(graphics, texX + 3, texY + 3);
        if (!target.state().getValue(PropertyHolder.LEATHER))
        {
            TEXTURE_CROSS.draw(graphics, texX + 3, texY + 3);
        }
    }
}
