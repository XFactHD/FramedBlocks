package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;

import java.util.List;

public final class ReinforcementOverlay extends BlockInteractOverlay
{
    public static final String REINFORCE_MESSAGE = Utils.translationKey("tooltip", "reinforce_state");
    public static final Component STATE_NOT_REINFORCED = Utils.translate("tooltip", "reinforce_state.false")
            .withStyle(ChatFormatting.RED);
    public static final Component STATE_REINFORCED = Utils.translate("tooltip", "reinforce_state.true")
            .withStyle(ChatFormatting.GREEN);
    private static final List<Component> LIST_FALSE = List.of(
            Component.translatable(REINFORCE_MESSAGE, STATE_NOT_REINFORCED)
    );
    private static final List<Component> LIST_TRUE = List.of(
            Component.translatable(REINFORCE_MESSAGE, STATE_REINFORCED)
    );

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/reinforcement_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public ReinforcementOverlay()
    {
        super(LIST_FALSE, LIST_TRUE, TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getReinforcementMode);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.is(Utils.FRAMED_REINFORCEMENT.value());
    }

    @Override
    protected boolean isValidTarget(Target target)
    {
        return target.state().getBlock() instanceof IFramedBlock;
    }

    @Override
    protected boolean getState(Target target)
    {
        if (level().getBlockEntity(target.pos()) instanceof FramedBlockEntity be)
        {
            return be.isReinforced();
        }
        return false;
    }
}
