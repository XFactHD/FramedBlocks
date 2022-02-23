package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import xfacthd.framedblocks.api.util.Utils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class FluidDummyModel implements BakedModel
{
    private final Fluid fluid;
    private final TextureAtlasSprite particles;
    private final Map<Direction, List<BakedQuad>> quads = new HashMap<>();

    public FluidDummyModel(Fluid fluid)
    {
        this.fluid = fluid;

        //noinspection deprecation
        Function<ResourceLocation, TextureAtlasSprite> spriteGetter = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        particles = spriteGetter.apply(fluid.getAttributes().getStillTexture());
        buildQuads(spriteGetter);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
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
    public ItemOverrides getOverrides() { return null; }



    //FIXME: quad modifications break on these quads (maybe look into generating these in a custom loader)
    private void buildQuads(Function<ResourceLocation, TextureAtlasSprite> spriteGetter)
    {
        TextureAtlasSprite stillSprite = spriteGetter.apply(fluid.getAttributes().getStillTexture());
        TextureAtlasSprite flowingSprite = spriteGetter.apply(fluid.getAttributes().getFlowingTexture());

        for (Direction dir : Direction.values())
        {
            boolean isX = Utils.isX(dir);
            boolean isY = Utils.isY(dir);
            boolean isZ = Utils.isZ(dir);
            boolean isPos = Utils.isPositive(dir);

            BakedQuadBuilder builder = new BakedQuadBuilder();
            builder.setQuadOrientation(dir);
            builder.setQuadTint(1);

            TextureAtlasSprite sprite = isY ? stillSprite : flowingSprite;
            builder.setTexture(sprite);

            for (int vert = 0; vert < 4; vert++)
            {
                float x;
                float y;
                float z;
                float u = vert >= 2 ? .5F : 0F;
                float v = (vert == 0 || vert == 3) ? 0F : .5F;

                if (isY)
                {
                    x = (vert == 0 || vert == 3) ? 1F : 0F;
                    y = isPos ? 1F : 0F;
                    z = (vert >= 2) == isPos ? 1F : 0F;

                    u = (vert == 0 || vert == 3) ? 0F : 1F;
                    v = vert >= 2 ? 1F : 0F;
                }
                else if (isX)
                {
                    x = isPos ? 1F : 0F;
                    y = (vert == 0 || vert == 3) ? 1F : 0F;
                    z = (vert < 2) == isPos ? 1F : 0F;
                }
                else if (isZ)
                {
                    x = (vert >= 2) == isPos ? 1F : 0F;
                    y = (vert == 0 || vert == 3) ? 1F : 0F;
                    z = isPos ? 1F : 0F;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid dir!");
                }

                putVertexData(builder,
                        new Vector4f(x, y, z, 1F),
                        new Vec2(u, v),
                        dir.step(),
                        fluid.getAttributes().getColor(),
                        new Vec2(0xF0, 0xF0),
                        sprite
                );
            }

            quads.put(dir, Collections.singletonList(builder.build()));
        }

        quads.put(null, Collections.emptyList());
    }

    /** Copied from { @link net.minecraftforge.client.model.obj.OBJModel } */
    private static void putVertexData(IVertexConsumer consumer, Vector4f pos, Vec2 tex, Vector3f normal, int color, Vec2 light, TextureAtlasSprite texture)
    {
        ImmutableList<VertexFormatElement> elements = consumer.getVertexFormat().getElements();
        for(int elem = 0; elem < elements.size(); elem++)
        {
            VertexFormatElement e = elements.get(elem);
            switch (e.getUsage())
            {
                case POSITION -> consumer.put(elem, pos.x(), pos.y(), pos.z(), pos.w());
                case COLOR ->
                {
                    float r = ((color >> 16) & 0xFF) / 255F;
                    float g = ((color >>  8) & 0xFF) / 255F;
                    float b = ( color        & 0xFF) / 255F;
                    float a = ((color >> 24) & 0xFF) / 255F;

                    consumer.put(elem, r, g, b, a);
                }
                case UV ->
                {
                    switch (e.getIndex())
                    {
                        case 0 -> consumer.put(elem, texture.getU(tex.x * 16), texture.getV(tex.y * 16));
                        case 2 -> consumer.put(elem, light.x, light.y);
                        default -> consumer.put(elem);
                    }
                }
                case NORMAL -> consumer.put(elem, normal.x(), normal.y(), normal.z());
                default -> consumer.put(elem);
            }
        }
    }
}