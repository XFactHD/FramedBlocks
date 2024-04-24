package xfacthd.framedblocks.common.net.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.menu.FramingSawWithEncoderMenu;

public record EncodeFramingSawPatternPayload(int containerId, ResourceLocation recipeId, ItemStack[] inputs) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<EncodeFramingSawPatternPayload> ID = Utils.payloadType("encode_saw_pattern");

    public EncodeFramingSawPatternPayload(FriendlyByteBuf buf)
    {
        this(buf.readInt(), buf.readResourceLocation(), buf.readArray(ItemStack[]::new, RegistryFriendlyByteBuf::readItem));
    }

    public void write(FriendlyByteBuf buf)
    {
        buf.writeInt(containerId);
        buf.writeResourceLocation(recipeId);
        buf.writeArray(inputs, RegistryFriendlyByteBuf::writeItem);
    }

    @Override
    public CustomPacketPayload.Type<EncodeFramingSawPatternPayload> type()
    {
        return ID;
    }

    public void handle(IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
            Player player = ctx.player();
            if (player.containerMenu instanceof FramingSawWithEncoderMenu menu && menu.containerId == containerId)
            {
                RecipeHolder<?> holder = player.level().getRecipeManager().byKey(recipeId).orElse(null);
                if (holder != null && holder.value() instanceof FramingSawRecipe recipe)
                {
                    menu.tryEncodePattern(recipe, inputs);
                }
            }
        });
    }
}
