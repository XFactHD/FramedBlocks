package io.github.xfacthd.framedblocks.client.screen.overlay.impl;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.screen.overlay.BlockInteractOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public final class CamoRotationOverlay extends BlockInteractOverlay
{
    public static final Component ROTATEABLE_FALSE = Utils.translate("tooltip", "camo_rotation.false");
    public static final Component ROTATEABLE_TRUE = Utils.translate("tooltip", "camo_rotation.true");

    private static final Identifier SYMBOL_TEXTURE = Utils.id("textures/overlay/camo_rotation_symbols.png");
    private static final Texture TEXTURE_FALSE = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 22, 44, 22);
    private static final Texture TEXTURE_TRUE = new Texture(SYMBOL_TEXTURE, 22, 0, 22, 22, 44, 22);

    public CamoRotationOverlay()
    {
        super(List.of(ROTATEABLE_FALSE), List.of(ROTATEABLE_TRUE), TEXTURE_FALSE, TEXTURE_TRUE, ClientConfig.VIEW::getCamoRotationMode);
    }

    @Override
    public boolean isValidTool(Player player, ItemStack stack)
    {
        return stack.getItem() == FBContent.ITEM_FRAMED_SCREWDRIVER.value();
    }

    @Override
    public boolean isValidTarget(Target target)
    {
        return target.state().getBlock() instanceof IFramedBlock;
    }

    @Override
    public boolean getState(Target target)
    {
        if (target.level().getBlockEntity(target.pos()) instanceof IFramedBlockEntity be)
        {
            HitResult hit = Minecraft.getInstance().hitResult;
            if (hit instanceof BlockHitResult blockHit)
            {
                return be.getCamo(blockHit, target.player()).canRotateCamo();
            }
        }
        return false;
    }
}
