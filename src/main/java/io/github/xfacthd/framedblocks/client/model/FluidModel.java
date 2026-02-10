package io.github.xfacthd.framedblocks.client.model;

import com.google.common.base.Preconditions;
import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.camo.fluid.FluidCamoContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.Map;

public final class FluidModel
{
    public static final Identifier BARE_MODEL = Utils.id("fluid/bare");
    public static final Identifier BARE_MODEL_SINGLE = Utils.id("fluid/bare_single");
    private static final ModelBaker.PartCache PART_CACHE = new ModelBakery.PartCacheImpl();
    private static final BlockModelRotation[] ROTATIONS = Util.make(new BlockModelRotation[6], arr ->
    {
        arr[Direction.DOWN.ordinal()] = BlockModelRotation.IDENTITY;
        arr[Direction.UP.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R180, Quadrant.R0));
        arr[Direction.NORTH.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R270, Quadrant.R0));
        arr[Direction.SOUTH.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R0));
        arr[Direction.WEST.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R90));
        arr[Direction.EAST.ordinal()] = BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R270));
    });
    private static final SpriteGetter TEXTURE_GETTER = new SpriteGetter()
    {
        @Override
        public TextureAtlasSprite get(Material material, ModelDebugName modelName)
        {
            boolean block = material.atlasLocation().equals(ClientUtils.BLOCK_ATLAS);
            return ClientUtils.getBlockSprite(block ? material.texture() : MissingTextureAtlasSprite.getLocation());
        }

        @Override
        public TextureAtlasSprite reportMissingReference(String ref, ModelDebugName modelName)
        {
            return ClientUtils.getBlockSprite(MissingTextureAtlasSprite.getLocation());
        }
    };

    public static BlockStateModel create(FluidCamoContent fluidCamo)
    {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        ModelBakery modelBakery = modelManager.getModelBakery();

        Fluid fluid = fluidCamo.getFluid();
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
        Identifier stillTexture = Preconditions.checkNotNull(
                props.getStillTexture(),
                "Fluid %s returned null from IClientFluidTypeExtensions#getStillTexture()",
                fluid
        );
        Identifier flowingTexture = Preconditions.checkNotNull(
                props.getFlowingTexture(),
                "Fluid %s returned null from IClientFluidTypeExtensions#getFlowingTexture()",
                fluid
        );

        ModelBakery.MissingModels missingModels = modelManager.framedblocks$getMissingModels();
        ModelBakery.ModelBakerImpl baker = modelBakery.new ModelBakerImpl(TEXTURE_GETTER, PART_CACHE, missingModels);

        boolean singleTexture = flowingTexture.equals(stillTexture);
        ResolvedModel bareModel = baker.getModel(singleTexture ? BARE_MODEL_SINGLE : BARE_MODEL);
        Preconditions.checkNotNull(bareModel, "Bare fluid model not loaded!");

        TextureSlots textures = new TextureSlots(Map.of(
                "end", new Material(ClientUtils.BLOCK_ATLAS, stillTexture),
                "side", new Material(ClientUtils.BLOCK_ATLAS, flowingTexture),
                "particle", new Material(ClientUtils.BLOCK_ATLAS, stillTexture)
        ));
        QuadCollection fluidQuads = bareModel.getTopGeometry().bake(
                textures,
                baker,
                ROTATIONS[fluidCamo.getFlowDirection().ordinal()],
                bareModel,
                bareModel.getTopAdditionalProperties()
        );
        Preconditions.checkNotNull(fluidQuads, "Failed to bake fluid model for fluid %s", fluid);
        ChunkSectionLayer chunkLayer = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());

        return new SingleVariant(new SimpleModelWrapper(fluidQuads, false, ClientUtils.getBlockSprite(stillTexture), chunkLayer));
    }

    private FluidModel() { }
}
