package io.github.xfacthd.framedblocks.api.camo.block;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/// Basic camo container implementation for simple block-based camos which hold only a [BlockState] and only need minimal
/// context to be applied and removed.
public final class SimpleBlockCamoContainer extends AbstractBlockCamoContainer<SimpleBlockCamoContainer> {
    private final SimpleBlockCamoContainerFactory factory;

    public SimpleBlockCamoContainer(BlockState state, SimpleBlockCamoContainerFactory factory) {
        super(state);
        this.factory = factory;
    }

    @Override
    public int hashCode() {
        return content.hashCode() * 13 + System.identityHashCode(factory);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SimpleBlockCamoContainer other)) {
            return false;
        }
        return content.equals(other.content) && factory == other.factory;
    }

    @Override
    public String toString() {
        return "SimpleBlockCamoContainer{content=" + content + "}";
    }

    @Override
    public SimpleBlockCamoContainerFactory getFactory() {
        return factory;
    }
}
