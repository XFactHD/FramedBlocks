package xfacthd.framedblocks.common.net.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.menu.FramingSawWithEncoderMenu;
import xfacthd.framedblocks.common.net.FramedByteBufCodecs;

public record EncodeFramingSawPatternPayload(int containerId, ResourceLocation recipeId, ItemStack[] inputs) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<EncodeFramingSawPatternPayload> TYPE = Utils.payloadType("encode_saw_pattern");
    public static final StreamCodec<RegistryFriendlyByteBuf, EncodeFramingSawPatternPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            EncodeFramingSawPatternPayload::containerId,
            ResourceLocation.STREAM_CODEC,
            EncodeFramingSawPatternPayload::recipeId,
            FramedByteBufCodecs.array(ItemStack.STREAM_CODEC, ItemStack[]::new, FramingSawRecipe.MAX_ADDITIVE_COUNT + 1),
            EncodeFramingSawPatternPayload::inputs,
            EncodeFramingSawPatternPayload::new
    );

    @Override
    public CustomPacketPayload.Type<EncodeFramingSawPatternPayload> type()
    {
        return TYPE;
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
