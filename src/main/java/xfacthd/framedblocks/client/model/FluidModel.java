package xfacthd.framedblocks.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.FramedConstants;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class FluidModel implements BakedModel
{
    public static final ResourceLocation BARE_MODEL = new ResourceLocation(FramedConstants.MOD_ID, "fluid/bare");
    private static final Function<ResourceLocation, TextureAtlasSprite> SPRITE_GETTER = (loc ->
            Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(loc)
    );
    private static final Supplier<ResourceLocation> WATER_STILL = Suppliers.memoize(() ->
            RenderProperties.get(Fluids.WATER).getStillTexture()
    );
    private static final Supplier<ResourceLocation> WATER_FLOWING = Suppliers.memoize(() ->
            RenderProperties.get(Fluids.WATER).getFlowingTexture()
    );
    private final Map<Direction, List<BakedQuad>> quads;
    private final TextureAtlasSprite particles;

    private FluidModel(Map<Direction, List<BakedQuad>> quads, TextureAtlasSprite particles)
    {
        this.quads = quads;
        this.particles = particles;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random)
    {
        return getQuads(state, side, random, EmptyModelData.INSTANCE);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IModelData extraData)
    {
        if (side == null) { return Collections.emptyList(); }
        return quads.get(side);
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }

    @Override
    public boolean isGui3d() { return false; }

    @Override
    public boolean usesBlockLight() { return false; }

    @Override
    public boolean isCustomRenderer() { return false; }

    @Override
    public TextureAtlasSprite getParticleIcon() { return particles; }

    @Override
    public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }



    public static FluidModel create(Fluid fluid)
    {
        Preconditions.checkNotNull(ForgeModelBakery.instance(), "FluidModel.create() called too early!");
        UnbakedModel bareModel = ForgeModelBakery.instance().getModel(BARE_MODEL);
        Preconditions.checkNotNull(bareModel, "Bare fluid model not loaded!");

        IFluidTypeRenderProperties props = RenderProperties.get(fluid);

        BakedModel model = bareModel.bake(
                ForgeModelBakery.instance(),
                matToSprite(props),
                SimpleModelState.IDENTITY,
                new ResourceLocation(FramedConstants.MOD_ID, "fluid/" + fluid.getFluidType().toString().replace(":", "_"))
        );
        Preconditions.checkNotNull(model, "Failed to bake fluid model");

        Map<Direction, List<BakedQuad>> quads = new EnumMap<>(Direction.class);
        BlockState defState = fluid.defaultFluidState().createLegacyBlock();
        RandomSource random = RandomSource.create();

        for (Direction side : Direction.values())
        {
            quads.put(side, model.getQuads(defState, side, random, EmptyModelData.INSTANCE));
        }

        return new FluidModel(quads, SPRITE_GETTER.apply(props.getStillTexture()));
    }

    private static Function<Material, TextureAtlasSprite> matToSprite(IFluidTypeRenderProperties props)
    {
        return mat ->
        {
            if (mat.texture().equals(WATER_FLOWING.get()))
            {
                return SPRITE_GETTER.apply(props.getFlowingTexture());
            }
            if (mat.texture().equals(WATER_STILL.get()))
            {
                return SPRITE_GETTER.apply(props.getStillTexture());
            }
            return mat.sprite();
        };
    }
}
