package xfacthd.framedblocks.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.internal.InternalClientAPI;

import java.util.function.Supplier;

public final class ClientUtils
{
    public static final ResourceLocation DUMMY_TEXTURE = Utils.rl("neoforge", "white");

    @Deprecated(forRemoval = true)
    public static final Supplier<Boolean> OPTIFINE_LOADED = () -> false;

    public static void enqueueClientTask(Runnable task)
    {
        enqueueClientTask(0, task);
    }

    public static void enqueueClientTask(int delay, Runnable task)
    {
        InternalClientAPI.INSTANCE.enqueueClientTask(delay, task);
    }

    public static int getBlockColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, BlockState state, int tintIdx)
    {
        return Minecraft.getInstance().getBlockColors().getColor(state, level, pos, tintIdx);
    }

    public static int getFluidColor(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return IClientFluidTypeExtensions.of(fluid).getTintColor(fluid, level, pos);
    }

    public static int getFluidColor(FluidState fluid)
    {
        return IClientFluidTypeExtensions.of(fluid).getTintColor();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDummyTexture(BakedQuad quad)
    {
        return isTexture(quad, DUMMY_TEXTURE);
    }

    public static boolean isTexture(BakedQuad quad, ResourceLocation texture)
    {
        return quad.getSprite().contents().name().equals(texture);
    }

    public static void renderTransparentFakeItem(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        graphics.renderFakeItem(stack, x, y, 0);
        graphics.fill(RenderType.guiGhostRecipeOverlay(), x, y, x + 16, y + 16, 0x80888888);
    }

    public static boolean isLeftHand(ItemDisplayContext ctx)
    {
        return ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }



    private ClientUtils() { }
}