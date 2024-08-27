package xfacthd.framedblocks.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.*;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FluidModel implements BakedModel
{
    private static final ModelState SIMPLE_STATE = new SimpleModelState(Transformation.identity());
    public static final ModelResourceLocation BARE_MODEL = ModelResourceLocation.standalone(Utils.rl("fluid/bare"));
    public static final ModelResourceLocation BARE_MODEL_SINGLE = ModelResourceLocation.standalone(Utils.rl("fluid/bare_single"));
    @SuppressWarnings("deprecation")
    private static final Function<ResourceLocation, TextureAtlasSprite> SPRITE_GETTER = (loc ->
            Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(loc)
    );
    private static final Supplier<ResourceLocation> WATER_STILL = Suppliers.memoize(() ->
            IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture()
    );
    private static final Supplier<ResourceLocation> WATER_FLOWING = Suppliers.memoize(() ->
            IClientFluidTypeExtensions.of(Fluids.WATER).getFlowingTexture()
    );
    private static final IClientFluidTypeExtensions DUMMY_FLUID_TYPE_EXTENSIONS = new IClientFluidTypeExtensions()
    {
        @Override
        public ResourceLocation getStillTexture()
        {
            return MissingTextureAtlasSprite.getLocation();
        }

        @Override
        public ResourceLocation getFlowingTexture()
        {
            return MissingTextureAtlasSprite.getLocation();
        }
    };
    private final RenderType fluidLayer;
    private final ChunkRenderTypeSet fluidLayerSet;
    private final Map<Direction, List<BakedQuad>> quads;
    private final TextureAtlasSprite particles;

    private FluidModel(RenderType fluidLayer, Map<Direction, List<BakedQuad>> quads, TextureAtlasSprite particles)
    {
        this.fluidLayer = fluidLayer;
        this.fluidLayerSet = ChunkRenderTypeSet.of(fluidLayer);
        this.quads = quads;
        this.particles = particles;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random)
    {
        return getQuads(state, side, random, ModelData.EMPTY, RenderType.translucent());
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            RandomSource rand,
            ModelData extraData,
            RenderType layer
    )
    {
        if (side == null || layer != fluidLayer)
        {
            return Collections.emptyList();
        }
        return quads.get(side);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        return fluidLayerSet;
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean usesBlockLight()
    {
        return false;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return particles;
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }



    public static FluidModel create(Fluid fluid)
    {
        ModelBakery modelBakery = Minecraft.getInstance().getModelManager().getModelBakery();

        // Use dummy extensions for empty fluid to prevent crashes due to null textures
        IClientFluidTypeExtensions props = getFluidTypeExtensions(fluid);
        ResourceLocation stillTexture = Preconditions.checkNotNull(
                props.getStillTexture(),
                "Fluid %s returned null from IClientFluidTypeExtensions#getStillTexture()",
                fluid
        );
        ResourceLocation flowingTexture = Preconditions.checkNotNull(
                props.getFlowingTexture(),
                "Fluid %s returned null from IClientFluidTypeExtensions#getFlowingTexture()",
                fluid
        );

        boolean singleTexture = flowingTexture.equals(stillTexture);
        UnbakedModel bareModel = modelBakery.getModel(singleTexture ? BARE_MODEL_SINGLE.id() : BARE_MODEL.id());
        Preconditions.checkNotNull(bareModel, "Bare fluid model not loaded!");

        ResourceLocation fluidName = Preconditions.checkNotNull(
                NeoForgeRegistries.FLUID_TYPES.getKey(fluid.getFluidType()),
                "Cannot create FluidModel for unregistered FluidType of fluid %s",
                fluid
        );
        ModelResourceLocation modelName = new ModelResourceLocation(
                Utils.rl("fluid/" + fluidName.toString().replace(":", "_")),
                "framedblocks_dynamic_fluid"
        );
        Function<Material, TextureAtlasSprite> spriteGetter = matToSprite(stillTexture, flowingTexture);
        ModelBakery.ModelBakerImpl baker = modelBakery.new ModelBakerImpl((modelLoc, material) -> spriteGetter.apply(material), modelName);
        BakedModel model = bareModel.bake(baker, spriteGetter, SIMPLE_STATE);
        Preconditions.checkNotNull(model, "Failed to bake fluid model for fluid %s", fluid);

        Map<Direction, List<BakedQuad>> quads = new EnumMap<>(Direction.class);
        BlockState defState = fluid.defaultFluidState().createLegacyBlock();
        RandomSource random = RandomSource.create();
        RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());

        for (Direction side : Direction.values())
        {
            quads.put(side, model.getQuads(defState, side, random, ModelData.EMPTY, layer));
        }

        return new FluidModel(layer, quads, SPRITE_GETTER.apply(stillTexture));
    }

    private static IClientFluidTypeExtensions getFluidTypeExtensions(Fluid fluid)
    {
        if (fluid == Fluids.EMPTY)
        {
            return DUMMY_FLUID_TYPE_EXTENSIONS;
        }
        return IClientFluidTypeExtensions.of(fluid);
    }

    private static Function<Material, TextureAtlasSprite> matToSprite(ResourceLocation stillTexture, ResourceLocation flowingTexture)
    {
        return mat ->
        {
            if (mat.texture().equals(WATER_FLOWING.get()))
            {
                return SPRITE_GETTER.apply(flowingTexture);
            }
            if (mat.texture().equals(WATER_STILL.get()))
            {
                return SPRITE_GETTER.apply(stillTexture);
            }
            return mat.sprite();
        };
    }
}
