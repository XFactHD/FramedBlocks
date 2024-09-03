package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;

public final class SplitLineOverlay extends BlockInteractOverlay
{
    public static final Component SPLIT_LINE_FALSE = Utils.translate("tooltip", "split_line.false");
    public static final Component SPLIT_LINE_TRUE = Utils.translate("tooltip", "split_line.true");
    public static final Component MSG_SWITCH_SPLIT_LINE = Utils.translate("msg", "split_line.switch");
    private static final List<Component> LINES_FALSE = List.of(SPLIT_LINE_FALSE, MSG_SWITCH_SPLIT_LINE);
    private static final List<Component> LINES_TRUE = List.of(SPLIT_LINE_TRUE, MSG_SWITCH_SPLIT_LINE);

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/split_line_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 20, 20, 40, 20);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 20, 0, 20, 20, 40, 20);

    public SplitLineOverlay()
    {
        super("split_line", LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getSplitLineMode);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.getItem() == FBContent.ITEM_FRAMED_WRENCH.value();
    }

    @Override
    protected boolean isValidTarget(Target target)
    {
        return target.state().getBlock() == FBContent.BLOCK_FRAMED_COLLAPSIBLE_BLOCK.value();
    }

    @Override
    protected boolean getState(Target target)
    {
        return target.state().getValue(PropertyHolder.ROTATE_SPLIT_LINE);
    }
}
