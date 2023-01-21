package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public final class StateLockOverlay extends BlockInteractOverlay
{
    public static final String LOCK_MESSAGE = "tooltip." + FramedConstants.MOD_ID + ".lock_state";
    private static final List<Component> LINES_FALSE = List.of(
            Component.translatable(LOCK_MESSAGE, IFramedBlock.STATE_UNLOCKED)
    );
    private static final List<Component> LINES_TRUE = List.of(
            Component.translatable(LOCK_MESSAGE, IFramedBlock.STATE_LOCKED)
    );

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/gui/state_lock_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public StateLockOverlay()
    {
        super(LINES_FALSE, LINES_TRUE, TEXTURE_FALSE, TEXTURE_TRUE);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.is(FBContent.itemFramedKey.get());
    }

    @Override
    protected boolean isValidTarget(BlockState state)
    {
        return state.getBlock() instanceof IFramedBlock block && block.getBlockType().canLockState();
    }

    @Override
    protected boolean getState(BlockState state)
    {
        return state.getValue(FramedProperties.STATE_LOCKED);
    }

    @Override
    protected boolean showDetailed()
    {
        return ClientConfig.stateLockShowDetails;
    }
}
