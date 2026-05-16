package io.github.xfacthd.framedblocks.api.internal;

import io.github.xfacthd.framedblocks.api.block.item.placement.PropertySpec;
import io.github.xfacthd.framedblocks.api.block.item.placement.PlacementStatePostProcessor;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyPrinter;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.SequencedMap;

public interface StateCycleSpecAssembler {
    <T extends StateCycleSpec> T assemble(StateCycleSpecBuilder builder, EntryAssembler<T> assembler);

    interface EntryAssembler<T extends StateCycleSpec> {
        T assemble(
                Block block,
                List<PropertySpec<?>> properties,
                SequencedMap<Property<?>, PropertyPrinter<?>> propertyPrinters,
                @Nullable PlacementStatePostProcessor postProcessor,
                boolean lockState
        );
    }
}
