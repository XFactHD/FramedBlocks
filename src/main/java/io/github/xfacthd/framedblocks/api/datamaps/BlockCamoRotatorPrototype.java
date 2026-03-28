package io.github.xfacthd.framedblocks.api.datamaps;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.MultiPropertyBlockCamoRotator;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.SinglePropertyBlockCamoRotator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BlockCamoRotatorPrototype(List<String> properties) {
    public static final Codec<BlockCamoRotatorPrototype> CODEC = ExtraCodecs.compactListCodec(Codec.STRING, Codec.STRING.listOf(1, Integer.MAX_VALUE))
            .xmap(BlockCamoRotatorPrototype::new, BlockCamoRotatorPrototype::properties);

    public BlockCamoRotatorPrototype(String property) {
        this(List.of(property));
    }

    public boolean isApplicableTo(Block block) {
        return properties.stream()
                .map(block.getStateDefinition()::getProperty)
                .noneMatch(Objects::isNull);
    }

    public BlockCamoRotator build(Block block) {
        List<Property<?>> resolvedProperties = new ArrayList<>(properties.size());
        for (String property : properties) {
            Property<?> prop = block.getStateDefinition().getProperty(property);
            Objects.requireNonNull(prop, "Tried building BlockCamoRotator with invalid property");
            resolvedProperties.add(prop);
        }
        if (resolvedProperties.size() == 1) {
            return new SinglePropertyBlockCamoRotator(resolvedProperties.getFirst());
        }
        return new MultiPropertyBlockCamoRotator(block, resolvedProperties);
    }
}
