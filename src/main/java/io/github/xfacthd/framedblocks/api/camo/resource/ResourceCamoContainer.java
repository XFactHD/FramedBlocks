package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import net.neoforged.neoforge.transfer.resource.Resource;

/// Base implementation of a camo container for [Resource]-based camos.
public abstract class ResourceCamoContainer<R extends Resource, C extends ResourceCamoContent<R, C>, T extends ResourceCamoContainer<R, C, T>> extends CamoContainer<C, T> {
    protected ResourceCamoContainer(C content) {
        super(content);
    }

    /// {@return the resource stored in this camo container}
    public final R getResource() {
        return content.getResource();
    }
}
