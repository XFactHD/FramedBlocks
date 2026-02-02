package io.github.xfacthd.framedblocks.api.datamaps;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.SimpleBlockCamoRotator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Objects;

public record BlockCamoRotatorPrototype(String property)
{
    public static final Codec<BlockCamoRotatorPrototype> CODEC = Codec.STRING
            .xmap(BlockCamoRotatorPrototype::new, BlockCamoRotatorPrototype::property);

    public boolean isApplicableTo(Block block)
    {
        return block.getStateDefinition().getProperty(property) != null;
    }

    public BlockCamoRotator build(Block block)
    {
        Property<?> prop = block.getStateDefinition().getProperty(property);
        Objects.requireNonNull(prop, "Tried building BlockCamoRotator with invalid property");
        return new SimpleBlockCamoRotator(prop);
    }
}
