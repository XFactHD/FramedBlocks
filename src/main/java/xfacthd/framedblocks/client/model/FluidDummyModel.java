package xfacthd.framedblocks.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class FluidDummyModel implements IBakedModel
{
    private final Fluid fluid;
    private final TextureAtlasSprite particles;
    private final Map<Direction, List<BakedQuad>> quads = new HashMap<>();

    FluidDummyModel(Fluid fluid)
    {
        this.fluid = fluid;

        //noinspection deprecation
        Function<ResourceLocation, TextureAtlasSprite> spriteGetter = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        particles = spriteGetter.apply(fluid.getAttributes().getStillTexture());
        buildQuads(spriteGetter);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return quads.get(side);
    }

    @Override
    public boolean isAmbientOcclusion() { return false; }

    @Override
    public boolean isGui3d() { return false; }

    @Override
    public boolean isSideLit() { return false; }

    @Override
    public boolean isBuiltInRenderer() { return false; }

    @Override
    public TextureAtlasSprite getParticleTexture() { return particles; }

    @Override
    public ItemOverrideList getOverrides() { return null; }



    private void buildQuads(Function<ResourceLocation, TextureAtlasSprite> spriteGetter)
    {
        TextureAtlasSprite stillSprite = spriteGetter.apply(fluid.getAttributes().getStillTexture());
        TextureAtlasSprite flowingSprite = spriteGetter.apply(fluid.getAttributes().getFlowingTexture());

        for (Direction dir : Direction.values())
        {
            BakedQuadBuilder builder = new BakedQuadBuilder();
            builder.setQuadOrientation(dir);
            builder.setQuadTint(1);

            TextureAtlasSprite sprite = dir.getAxis() == Direction.Axis.Y ? stillSprite : flowingSprite;
            builder.setTexture(sprite);

            boolean isX = dir.getAxis() == Direction.Axis.X;
            boolean isY = dir.getAxis() == Direction.Axis.Y;
            boolean isZ = dir.getAxis() == Direction.Axis.Z;
            boolean isPos = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;

            for (int vert = 0; vert < 4; vert++)
            {
                float x;
                float y;
                float z;
                float u = vert >= 2 ? 1F : 0F;
                float v = (vert == 0 || vert == 3) ? 0F : 1F;

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
                        new Vector2f(u, v),
                        dir.toVector3f(),
                        new Vector4f(1F, 1F, 1F, 1F),
                        new Vector2f(0xF0, 0xF0),
                        sprite
                );
            }

            quads.put(dir, Collections.singletonList(builder.build()));
        }

        quads.put(null, Collections.emptyList());
    }

    /** Copied from { @link net.minecraftforge.client.model.obj.OBJModel } */
    private void putVertexData(IVertexConsumer consumer, Vector4f pos, Vector2f tex, Vector3f normal, Vector4f color, Vector2f light, TextureAtlasSprite texture)
    {
        ImmutableList<VertexFormatElement> elements = consumer.getVertexFormat().getElements();
        for(int elem = 0; elem < elements.size(); elem++)
        {
            VertexFormatElement e = elements.get(elem);
            switch(e.getUsage())
            {
                case POSITION:
                {
                    consumer.put(elem, pos.getX(), pos.getY(), pos.getZ(), pos.getW());
                    break;
                }
                case COLOR:
                {
                    consumer.put(elem, color.getX(), color.getY(), color.getZ(), color.getW());
                    break;
                }
                case UV:
                {
                    switch (e.getIndex())
                    {
                        case 0:
                            consumer.put(elem,
                                    texture.getInterpolatedU(tex.x * 16),
                                    texture.getInterpolatedV(tex.y * 16)
                            );
                            break;
                        case 2:
                            consumer.put(elem, light.x, light.y);
                            break;
                        default:
                            consumer.put(elem);
                            break;
                    }
                    break;
                }
                case NORMAL:
                {
                    consumer.put(elem, normal.getX(), normal.getY(), normal.getZ());
                    break;
                }
                default:
                {
                    consumer.put(elem);
                    break;
                }
            }
        }
    }
}