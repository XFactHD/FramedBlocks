package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;

public interface MaterialLookup {
    Material.Baked getMaterial(Material material);

    static MaterialLookup bindMaterialBaker(MaterialBaker materials, ModelDebugName debugName) {
        return id -> materials.get(id, debugName);
    }

    /**
     * {@return a lookup that is only usable at the end of or outside of a resource reload}
     */
    static MaterialLookup runtime() {
        return InternalClientAPI.INSTANCE.getRuntimeMaterialLookup();
    }
}
