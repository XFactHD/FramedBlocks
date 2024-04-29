package xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;

public record PottedFlower(Block flower) implements AuxBlueprintData<PottedFlower>
{
    public static final Codec<PottedFlower> CODEC = BuiltInRegistries.BLOCK.byNameCodec()
            .xmap(PottedFlower::new, PottedFlower::flower);
    public static final MapCodec<PottedFlower> MAP_CODEC = CODEC.fieldOf("flower");
    public static final StreamCodec<RegistryFriendlyByteBuf, PottedFlower> STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK)
            .map(PottedFlower::new, PottedFlower::flower);
    public static final PottedFlower EMPTY = new PottedFlower(Blocks.AIR);

    public boolean isEmpty()
    {
        return flower == Blocks.AIR;
    }

    @Override
    public Type<PottedFlower> type()
    {
        return FBContent.AUX_TYPE_POTTED_FLOWER.value();
    }
}
