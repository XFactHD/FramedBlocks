package xfacthd.framedblocks.common.net.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.menu.IFramingSawMenu;

public record ServerboundSelectFramingSawRecipePayload(int containerId, int recipeIdx) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ServerboundSelectFramingSawRecipePayload> TYPE = Utils.payloadType("select_framing_saw_recipe");
    public static final StreamCodec<FriendlyByteBuf, ServerboundSelectFramingSawRecipePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundSelectFramingSawRecipePayload::containerId,
            ByteBufCodecs.VAR_INT,
            ServerboundSelectFramingSawRecipePayload::recipeIdx,
            ServerboundSelectFramingSawRecipePayload::new
    );

    @Override
    public CustomPacketPayload.Type<ServerboundSelectFramingSawRecipePayload> type()
    {
        return TYPE;
    }

    public void handle(IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
            Player player = ctx.player();
            AbstractContainerMenu menu = player.containerMenu;
            if (menu.containerId == containerId && menu instanceof IFramingSawMenu)
            {
                menu.clickMenuButton(player, recipeIdx);
            }
        });
    }
}
