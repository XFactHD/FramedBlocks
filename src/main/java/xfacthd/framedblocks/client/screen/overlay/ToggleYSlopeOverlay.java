package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public final class ToggleYSlopeOverlay extends BlockInteractOverlay
{
    public static final String SLOPE_MESSAGE = "tooltip." + FramedConstants.MOD_ID + ".y_slope";
    public static final String TOGGLE_MESSAGE = "tooltip." + FramedConstants.MOD_ID +  ".y_slope.toggle";
    public static final Component SLOPE_HOR = Utils.translate("tooltip", "y_slope.horizontal");
    public static final Component SLOPE_VERT = Utils.translate("tooltip", "y_slope.vertical");
    private static final List<Component> LINES_FALSE = List.of(
            Component.translatable(SLOPE_MESSAGE, SLOPE_HOR),
            Component.translatable(TOGGLE_MESSAGE, SLOPE_VERT)
    );
    private static final List<Component> LINES_TRUE = List.of(
            Component.translatable(SLOPE_MESSAGE, SLOPE_VERT),
            Component.translatable(TOGGLE_MESSAGE, SLOPE_HOR)
    );

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/gui/yslope_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 20, 40, 40, 40);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 20, 0, 20, 40, 40, 40);

    public ToggleYSlopeOverlay()
    {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.getItem() == FBContent.itemFramedWrench.get();
    }

    @Override
    protected boolean isValidTarget(BlockState state)
    {
        return state.hasProperty(FramedProperties.Y_SLOPE);
    }

    @Override
    protected boolean getState(BlockState state)
    {
        return state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected boolean showDetailed()
    {
        return ClientConfig.toggleYSlopeShowDetails;
    }
}
