package io.github.xfacthd.framedblocks.api.datagen.models;

import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public abstract class AbstractFramedItemModelProvider extends ModelProvider {
    protected AbstractFramedItemModelProvider(PackOutput output, String modId) {
        super(output, modId);
    }

    @Override
    protected final Stream<Holder<Block>> getKnownBlocks() {
        return Stream.of();
    }

    @Override
    public final String getName() {
        return "Item Models - " + modId;
    }
}
