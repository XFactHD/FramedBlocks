package io.github.xfacthd.framedblocks.api.camo.resource;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/// Camo client handler implementation for [Resource]-based camos which do not have existing models loaded from RPs and
/// instead need to have their models dynamically generated from one or two textures.
public abstract class ResourceCamoContentClientHandler<R extends Resource, C extends ResourceCamoContent<R, C>> extends CamoContentClientHandler<C> {
    private final ResourceModelBaker<R, C> modelBaker = InternalClientAPI.INSTANCE.createResourceModelBaker(this);

    @Override
    public final BlockStateModel getOrCreateModel(C camo) {
        return modelBaker.getOrCreate(camo);
    }

    /// {@return the model spec for the given resource camo}
    ///
    /// @param camo The camo whose model is being queried
    public abstract ResourceModelSpec getModelSpec(C camo);

    /// Describes how the model for a given resource should be baked. If the flowing texture is non-null and not equal to the still texture then it is expected
    /// to have double the resolution of the single texture, the same way flowing fluid textures do relative to their still counterparts.
    ///
    /// @param stillMaterial   The texture to use for the vertical faces and, if no flowing texture is specified, for the horizontal faces
    /// @param flowingMaterial The texture to use for the horizontal faces or null if the resource only has one texture
    /// @param tinted          Whether the resource is tinted
    /// @param orientation     The orientation of the model if the resource camo is orientable, otherwise `null`
    public record ResourceModelSpec(Material stillMaterial, @Nullable Material flowingMaterial, boolean tinted, @Nullable Direction orientation) {
        public ResourceModelSpec(Material.Baked stillMaterial, Material.@Nullable Baked flowingMaterial, boolean tinted, @Nullable Direction orientation) {
            this(unbakeMaterial(stillMaterial), unbakeMaterial(flowingMaterial), tinted, orientation);
        }

        @Contract("!null->!null")
        private static @Nullable Material unbakeMaterial(Material.@Nullable Baked material) {
            if (material != null) {
                return new Material(material.sprite().contents().name(), material.forceTranslucent());
            }
            return null;
        }
    }

    @ApiStatus.Internal
    public interface ResourceModelBaker<R extends Resource, C extends ResourceCamoContent<R, C>> {
        BlockStateModel getOrCreate(C camo);
    }
}
