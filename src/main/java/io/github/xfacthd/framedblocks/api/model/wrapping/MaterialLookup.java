package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;

/// Functional interface for resolving baked materials.
@FunctionalInterface
public interface MaterialLookup {
    /// {@return the baked material for the given material}
    ///
    /// @param material The material to resolve
    Material.Baked getMaterial(Material material);

    /// {@return a lookup using the given material baker for material resolution}
    ///
    /// @param materials The material baker to use for material resolution
    /// @param debugName The debug name of the model providing this lookup
    static MaterialLookup bindMaterialBaker(MaterialBaker materials, ModelDebugName debugName) {
        return id -> materials.get(id, debugName);
    }

    /// {@return a lookup that for use at the end of or outside of a resource reload}
    static MaterialLookup runtime() {
        return InternalClientAPI.INSTANCE.getRuntimeMaterialLookup();
    }
}
