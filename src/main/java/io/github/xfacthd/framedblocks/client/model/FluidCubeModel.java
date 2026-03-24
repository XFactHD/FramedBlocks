package io.github.xfacthd.framedblocks.client.model;

import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.camo.fluid.FluidCamoContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
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
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class FluidCubeModel
{
    public static final Identifier BARE_MODEL = Utils.id("fluid/bare");
    public static final Identifier BARE_MODEL_SINGLE = Utils.id("fluid/bare_single");
    private static final Map<FluidCamoContent, BlockStateModel> FLUID_MODEL_CACHE = new ConcurrentHashMap<>();
    private static final Lazy<ModelBaker.Interner> INTERNER = Lazy.of(ModelBakery.InternerImpl::new);
    private static final BlockModelRotation[] ROTATIONS = Util.make(new BlockModelRotation[6], arr ->
    {
        arr[Direction.DOWN.ordinal()] = BlockModelRotation.IDENTITY;
        arr[Direction.UP.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R180, Quadrant.R0));
        arr[Direction.NORTH.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R270, Quadrant.R0));
        arr[Direction.SOUTH.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R0));
        arr[Direction.WEST.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R90));
        arr[Direction.EAST.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R270));
    });

    public static BlockStateModel getOrCreate(FluidCamoContent fluidCamo)
    {
        return FLUID_MODEL_CACHE.computeIfAbsent(fluidCamo, FluidCubeModel::create);
    }

    public static BlockStateModel create(FluidCamoContent fluidCamo)
    {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        ModelBakery modelBakery = modelManager.getModelBakery();

        FluidModel fluidModel = ModelUtils.getFluidModel(fluidCamo.getFluid().defaultFluidState());
        Material stillMaterial = unbakeMaterial(fluidModel.stillMaterial());
        Material flowingMaterial = unbakeMaterial(fluidModel.flowingMaterial());

        ModelBakery.MissingModels missingModels = modelManager.framedblocks$getMissingModels();
        ModelBakery.ModelBakerImpl baker = modelBakery.new ModelBakerImpl(RuntimeMaterialBaker.INSTANCE, INTERNER.get(), missingModels);

        boolean singleTexture = flowingMaterial.sprite().equals(stillMaterial.sprite());
        ResolvedModel bareModel = baker.getModel(singleTexture ? BARE_MODEL_SINGLE : BARE_MODEL);
        Objects.requireNonNull(bareModel, "Bare fluid model not loaded!");

        TextureSlots textures = new TextureSlots(Map.of(
                "end", stillMaterial,
                "side", flowingMaterial,
                "particle", stillMaterial
        ));
        QuadCollection fluidQuads = bareModel.getTopGeometry().bake(
                textures,
                baker,
                ROTATIONS[fluidCamo.getFlowDirection().ordinal()],
                bareModel,
                bareModel.getTopAdditionalProperties()
        );

        return new SingleVariant(new SimpleModelWrapper(fluidQuads, false, fluidModel.stillMaterial()));
    }

    private static Material unbakeMaterial(Material.Baked material)
    {
        return new Material(material.sprite().contents().name(), material.forceTranslucent());
    }

    public static void clearCaches()
    {
        FLUID_MODEL_CACHE.clear();
        INTERNER.invalidate();
    }

    private FluidCubeModel() { }
}
