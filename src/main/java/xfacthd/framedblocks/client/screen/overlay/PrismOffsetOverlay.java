package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public final class PrismOffsetOverlay extends BlockInteractOverlay
{
    public static final Component PRISM_OFFSET_FALSE = Utils.translate("tooltip", "prism_offset.false");
    public static final Component PRISM_OFFSET_TRUE = Utils.translate("tooltip", "prism_offset.true");
    public static final Component MSG_SWITCH_OFFSET = Utils.translate("msg", "prism_offset.switch");
    private static final List<Component> LINES_FALSE = List.of(PRISM_OFFSET_FALSE, MSG_SWITCH_OFFSET);
    private static final List<Component> LINES_TRUE = List.of(PRISM_OFFSET_TRUE, MSG_SWITCH_OFFSET);

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/prism_offset_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 19, 19, 38, 19);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 19, 0, 19, 19, 38, 19);

    public PrismOffsetOverlay()
    {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.getItem() == FBContent.itemFramedHammer.get();
    }

    @Override
    protected boolean isValidTarget(BlockState state)
    {
        return state.hasProperty(FramedProperties.OFFSET);
    }

    @Override
    protected boolean getState(BlockGetter level, BlockPos pos, BlockState state)
    {
        return state.getValue(FramedProperties.OFFSET);
    }

    @Override
    protected boolean showDetailed()
    {
        return ClientConfig.prismOffsetShowDetails;
    }
}
