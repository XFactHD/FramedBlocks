package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import net.neoforged.neoforge.transfer.resource.Resource;

/// Base implementation of a camo content for [Resource]-based camos.
public abstract class ResourceCamoContent<R extends Resource, C extends ResourceCamoContent<R, C>> extends CamoContent<C> {
    protected final R resource;

    protected ResourceCamoContent(R resource) {
        this.resource = resource;
    }

    /// {@return the resource stored in this camo content}
    public final R getResource() {
        return resource;
    }
}
