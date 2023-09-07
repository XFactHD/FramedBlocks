package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;

public final class CamoRotationOverlay extends BlockInteractOverlay
{
    public static final Component ROTATEABLE_FALSE = Utils.translate("tooltip", "camo_rotation.false");
    public static final Component ROTATEABLE_TRUE = Utils.translate("tooltip", "camo_rotation.true");

    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/camo_rotation_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public CamoRotationOverlay()
    {
        super(List.of(ROTATEABLE_FALSE), List.of(ROTATEABLE_TRUE), TEXTURE_FALSE, TEXTURE_TRUE, () -> ClientConfig.camoRotationMode);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.getItem() == FBContent.ITEM_FRAMED_SCREWDRIVER.get();
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
            HitResult hit = Minecraft.getInstance().hitResult;
            if (hit instanceof BlockHitResult blockHit)
            {
                return be.getCamo(blockHit).canRotateCamo();
            }
        }
        return false;
    }
}
