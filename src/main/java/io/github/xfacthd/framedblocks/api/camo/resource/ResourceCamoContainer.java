package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import net.neoforged.neoforge.transfer.resource.Resource;

public abstract class ResourceCamoContainer<R extends Resource, C extends ResourceCamoContent<R, C>, T extends ResourceCamoContainer<R, C, T>> extends CamoContainer<C, T> {
    protected ResourceCamoContainer(C content) {
        super(content);
    }

    public final R getResource() {
        return content.getResource();
    }
}
