package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public abstract class ResourceCamoContentClientHandler<R extends Resource, C extends ResourceCamoContent<R, C>> extends CamoContentClientHandler<C> {
    private final ResourceModelBaker<R, C> modelBaker = InternalClientAPI.INSTANCE.createResourceModelBaker(this);

    @Override
    public final BlockStateModel getOrCreateModel(C camo) {
        return modelBaker.getOrCreate(camo);
    }

    public abstract ResourceModelSpec getModelSpec(C camo);

    /// Describes how the model for a given resource should be baked. If the flowing texture is non-null and not equal to the still texture then it is expected
    /// to have double the resolution of the single texture, the same way flowing fluid textures do relative to their still counterparts.
    ///
    /// @param stillMaterial   The texture to use for the vertical faces and, if no flowing texture is specified, for the horizontal faces
    /// @param flowingMaterial The texture to use for the horizontal faces or null if the resource only has one texture
    /// @param tinted          Whether the resource is tinted
    /// @param orientation     The orientation of the model if the resource camo is orientable, otherwise `null`
    public record ResourceModelSpec(Material.Baked stillMaterial, Material.@Nullable Baked flowingMaterial, boolean tinted, @Nullable Direction orientation) { }

    @ApiStatus.Internal
    public interface ResourceModelBaker<R extends Resource, C extends ResourceCamoContent<R, C>> {
        BlockStateModel getOrCreate(C camo);
    }
}
