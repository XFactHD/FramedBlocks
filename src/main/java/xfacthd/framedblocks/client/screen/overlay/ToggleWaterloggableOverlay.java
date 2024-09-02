package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.interactive.pressureplate.FramedPressurePlateBlock;
import xfacthd.framedblocks.common.block.interactive.pressureplate.FramedWeightedPressurePlateBlock;

import java.util.List;

public final class ToggleWaterloggableOverlay extends BlockInteractOverlay
{
    public static final Component MSG_IS_WATERLOGGABLE = Utils.translate("tooltip", "is_waterloggable.true");
    public static final Component MSG_IS_NOT_WATERLOGGABLE = Utils.translate("tooltip", "is_waterloggable.false");
    public static final Component MSG_MAKE_WATERLOGGABLE = Utils.translate("tooltip", "make_waterloggable.true");
    public static final Component MSG_MAKE_NOT_WATERLOGGABLE = Utils.translate("tooltip", "make_waterloggable.false");
    private static final List<Component> LINES_FALSE = List.of(MSG_IS_NOT_WATERLOGGABLE, MSG_MAKE_WATERLOGGABLE);
    private static final List<Component> LINES_TRUE = List.of(MSG_IS_WATERLOGGABLE, MSG_MAKE_NOT_WATERLOGGABLE);

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/waterloggable_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 20, 20, 40, 20);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 20, 0, 20, 20, 40, 20);

    public ToggleWaterloggableOverlay()
    {
        super("toggle_waterloggable", LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getToggleWaterlogMode);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.is(FBContent.ITEM_FRAMED_HAMMER.value());
    }

    @Override
    protected boolean isValidTarget(Target target)
    {
        Block block = target.state().getBlock();
        return block instanceof FramedPressurePlateBlock || block instanceof FramedWeightedPressurePlateBlock;
    }

    @Override
    protected boolean getState(Target target)
    {
        return target.state().hasProperty(BlockStateProperties.WATERLOGGED);
    }
}
