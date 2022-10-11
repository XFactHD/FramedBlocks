package xfacthd.framedblocks.client.screen;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedPressurePlateBlock;
import xfacthd.framedblocks.common.block.FramedWeightedPressurePlateBlock;

public class ToggleWaterloggableOverlay implements IGuiOverlay
{
    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/gui/waterloggable_symbols.png");
    public static final String MSG_IS_WATERLOGGABLE = "tooltip." + FramedConstants.MOD_ID + ".is_waterloggable.true";
    public static final String MSG_IS_NOT_WATERLOGGABLE = "tooltip." + FramedConstants.MOD_ID + ".is_waterloggable.false";
    public static final String MSG_MAKE_WATERLOGGABLE = "tooltip." + FramedConstants.MOD_ID + ".make_waterloggable.true";
    public static final String MSG_MAKE_NOT_WATERLOGGABLE = "tooltip." + FramedConstants.MOD_ID + ".make_waterloggable.false";

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height)
    {
        ItemStack stack = player().getMainHandItem();
        if (!stack.is(FBContent.itemFramedHammer.get())) { return; }

        BlockState state = getTargettedBlock();
        if (!canToggleWaterloggingOn(state.getBlock())) { return; }

        boolean waterloggable = state.hasProperty(BlockStateProperties.WATERLOGGED);

        Component textOne = Component.translatable(waterloggable ? MSG_IS_WATERLOGGABLE : MSG_IS_NOT_WATERLOGGABLE);
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), textOne, width / 2, (height / 2) + 30, -1);

        RenderSystem.setShaderTexture(0, SYMBOL_TEXTURE);
        GuiComponent.blit(poseStack, (width / 2) - 9, (height / 2) + 40, 0, waterloggable ? 21 : 1, 1, 18, 17, 40, 20);

        Component textTwo = Component.translatable(waterloggable ? MSG_MAKE_NOT_WATERLOGGABLE : MSG_MAKE_WATERLOGGABLE);
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), textTwo, width / 2, (height / 2) + 60, -1);
    }



    private static Player player() { return Preconditions.checkNotNull(Minecraft.getInstance().player); }

    private static BlockState getTargettedBlock()
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit)
        {
            return Preconditions.checkNotNull(Minecraft.getInstance().level).getBlockState(blockHit.getBlockPos());
        }
        return Blocks.AIR.defaultBlockState();
    }

    private static boolean canToggleWaterloggingOn(Block block)
    {
        return block instanceof FramedPressurePlateBlock || block instanceof FramedWeightedPressurePlateBlock;
    }
}
