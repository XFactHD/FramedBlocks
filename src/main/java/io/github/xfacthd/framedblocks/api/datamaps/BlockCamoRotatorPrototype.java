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

/// Specifies the names of one or more blockstate properties to be cycled when a camo of the block this prototype
/// is built with is rotated with the Framed Screwdriver.
///
/// @param properties The names of the properties to be cycled
/// @see FramedDataMaps#blockCamoRotators()
public record BlockCamoRotatorPrototype(List<String> properties) {
    public static final Codec<BlockCamoRotatorPrototype> CODEC = ExtraCodecs.compactListCodec(Codec.STRING, Codec.STRING.listOf(1, Integer.MAX_VALUE))
            .xmap(BlockCamoRotatorPrototype::new, BlockCamoRotatorPrototype::properties);

    /// Construct a prototype for a rotator cycling a single property.
    ///
    /// @param property The name of the property to be cycled
    public BlockCamoRotatorPrototype(String property) {
        this(List.of(property));
    }

    /// {@return whether this rotator prototype is applicable to the given block}
    ///
    /// @param block The block to check against
    public boolean isApplicableTo(Block block) {
        return properties.stream()
                .map(block.getStateDefinition()::getProperty)
                .noneMatch(Objects::isNull);
    }

    /// {@return a built camo rotator from this prototype for the given block}
    ///
    /// @param block The block to build the rotator for
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
