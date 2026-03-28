package io.github.xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record PottedFlower(Block flower) {
    public static final Codec<PottedFlower> CODEC = BuiltInRegistries.BLOCK.byNameCodec()
            .xmap(PottedFlower::new, PottedFlower::flower);
    public static final StreamCodec<RegistryFriendlyByteBuf, PottedFlower> STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK)
            .map(PottedFlower::new, PottedFlower::flower);
    public static final PottedFlower EMPTY = new PottedFlower(Blocks.AIR);

    public boolean isEmpty() {
        return flower == Blocks.AIR;
    }
}
