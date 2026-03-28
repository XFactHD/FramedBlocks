package io.github.xfacthd.framedblocks.common.net.payload.serverbound;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.network.FramedByteBufCodecs;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipe;
import io.github.xfacthd.framedblocks.common.menu.FramingSawWithEncoderMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundEncodeFramingSawPatternPayload(int containerId, ResourceKey<Recipe<?>> recipeId, ItemStack[] inputs) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundEncodeFramingSawPatternPayload> TYPE = Utils.payloadType("encode_saw_pattern");
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEncodeFramingSawPatternPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundEncodeFramingSawPatternPayload::containerId,
            ResourceKey.streamCodec(Registries.RECIPE),
            ServerboundEncodeFramingSawPatternPayload::recipeId,
            FramedByteBufCodecs.array(ItemStack.STREAM_CODEC, ItemStack[]::new, FramingSawRecipe.MAX_ADDITIVE_COUNT + 1),
            ServerboundEncodeFramingSawPatternPayload::inputs,
            ServerboundEncodeFramingSawPatternPayload::new
    );

    @Override
    public CustomPacketPayload.Type<ServerboundEncodeFramingSawPatternPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        if (player.containerMenu instanceof FramingSawWithEncoderMenu menu && menu.containerId == containerId) {
            RecipeHolder<?> holder = player.level().recipeAccess().byKey(recipeId).orElse(null);
            if (holder != null && holder.value() instanceof FramingSawRecipe recipe) {
                menu.tryEncodePattern(recipe, inputs);
            }
        }
    }
}
