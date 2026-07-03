package io.github.xfacthd.framedblocks.client.model;

import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContent;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.model.CachingModel;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ResourceCubeModel<R extends Resource, C extends ResourceCamoContent<R, C>> implements ResourceCamoContentClientHandler.ResourceModelBaker<R, C>, CachingModel {
    public static final Identifier MODEL_BARE = Utils.id("resource/fluid_column");
    public static final Identifier MODEL_BARE_TINTED = Utils.id("resource/fluid_column_tinted");
    public static final Identifier MODEL_BARE_SINGLE = Utils.id("minecraft", "block/cube_all");
    public static final Identifier MODEL_BARE_SINGLE_TINTED = Utils.id("resource/cube_all_tinted");
    private static final Lazy<ModelBaker.Interner> INTERNER = Lazy.of(ModelBakery.InternerImpl::new);
    private static final BlockModelRotation[] ROTATIONS = Util.make(new BlockModelRotation[6], arr -> {
        arr[Direction.DOWN.ordinal()] = BlockModelRotation.IDENTITY;
        arr[Direction.UP.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R180, Quadrant.R0));
        arr[Direction.NORTH.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R270, Quadrant.R0));
        arr[Direction.SOUTH.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R0));
        arr[Direction.WEST.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R90));
        arr[Direction.EAST.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R270));
    });

    private final ResourceCamoContentClientHandler<R, C> clientHandler;
    private final Map<C, BlockStateModel> modelCache = new ConcurrentHashMap<>();
    private final Function<C, BlockStateModel> baker = this::create;

    public ResourceCubeModel(ResourceCamoContentClientHandler<R, C> clientHandler) {
        this.clientHandler = clientHandler;
        CachingModel.registerPersistent(this);
    }

    @Override
    public BlockStateModel getOrCreate(C camo) {
        return modelCache.computeIfAbsent(camo, baker);
    }

    private BlockStateModel create(C camo) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        ModelBakery modelBakery = modelManager.getModelBakery();

        ModelBakery.MissingModels missingModels = modelManager.framedblocks$getMissingModels();
        MaterialBaker materialBaker = RuntimeMaterialBaker.getInstance();
        ModelBakery.ModelBakerImpl baker = modelBakery.new ModelBakerImpl(materialBaker, INTERNER.get(), missingModels);

        ResourceCamoContentClientHandler.ResourceModelSpec modelSpec = clientHandler.getModelSpec(camo);
        Material stillMaterial = modelSpec.stillMaterial();
        Material flowingMaterial = modelSpec.flowingMaterial();
        Direction orientation = Objects.requireNonNullElse(modelSpec.orientation(), Direction.DOWN);

        Identifier bareModelId;
        Map<String, Material> textures;
        if (flowingMaterial == null || flowingMaterial.sprite().equals(stillMaterial.sprite())) {
            bareModelId = modelSpec.tinted() ? MODEL_BARE_SINGLE_TINTED : MODEL_BARE_SINGLE;
            textures = Map.of("all", stillMaterial, "particle", stillMaterial);
        } else {
            bareModelId = modelSpec.tinted() ? MODEL_BARE_TINTED : MODEL_BARE;
            textures = Map.of("end", stillMaterial, "side", flowingMaterial, "particle", stillMaterial);
        }
        ResolvedModel bareModel = Objects.requireNonNull(baker.getModel(bareModelId), "Bare fluid model not loaded!");

        QuadCollection fluidQuads = bareModel.getTopGeometry().bake(
                new TextureSlots(textures),
                baker,
                ROTATIONS[orientation.ordinal()],
                bareModel,
                bareModel.getTopAdditionalProperties()
        );
        Material.Baked particleMaterial = materialBaker.get(stillMaterial, () -> "");
        return new SingleVariant(new SimpleModelWrapper(fluidQuads, false, particleMaterial));
    }

    @Override
    public void clearCache() {
        modelCache.clear();
    }

    public static void clearInterner() {
        INTERNER.invalidate();
    }
}
