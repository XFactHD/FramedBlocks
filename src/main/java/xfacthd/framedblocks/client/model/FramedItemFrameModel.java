package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.ClientUtils;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedItemFrameModel extends FramedBlockModel
{
    private static final int GLOWING_BRIGHTNESS = 5;

    private final Direction facing;
    private final boolean leather;
    private final boolean mapFrame;
    private final boolean glowing;
    private final float innerLength;
    private final float innerPos;
    private final float innerMin;
    private final float innerMax;
    private final float outerMin;
    private final float outerMax;

    private FramedItemFrameModel(BlockState state, BakedModel baseModel, boolean glowing)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.leather = state.getValue(PropertyHolder.LEATHER);
        this.mapFrame = state.getValue(PropertyHolder.MAP_FRAME);
        this.glowing = glowing;

        this.innerLength = mapFrame ? 15F/16F : 13F/16F;
        this.innerPos = mapFrame ? 1F/16F : 3F/16F;
        this.innerMin = mapFrame ? 1F/16F : 3F/16F;
        this.innerMax = mapFrame ? 15F/16F : 13F/16F;
        this.outerMin = mapFrame ? 0F : 2F/16F;
        this.outerMax = mapFrame ? 1F : 14F/16F;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadFace = quad.getDirection();
        if (Utils.isY(facing))
        {
            makeVerticalFrame(quadMap, quad, quadFace);
        }
        else
        {
            makeHorizontalFrame(quadMap, quad, quadFace);
        }
    }

    private void makeVerticalFrame(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction quadFace)
    {
        if (quadFace == facing)
        {
            QuadModifier.full(quad)
                    .applyIf(Modifiers.cutTopBottom(outerMin, outerMin, outerMax, outerMax), !mapFrame)
                    .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                    .export(quadMap.get(quadFace));
        }
        else if (quadFace == facing.getOpposite())
        {
            if (!leather && !mapFrame)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutTopBottom(innerMin, innerMin, innerMax, innerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(.5F/16F))
                        .export(quadMap.get(null));
            }

            if (!mapFrame || leather)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutTopBottom(outerMin, outerMin, innerMin, outerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));

                QuadModifier.full(quad)
                        .apply(Modifiers.cutTopBottom(innerMax, outerMin, outerMax, outerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));

                QuadModifier.full(quad)
                        .apply(Modifiers.cutTopBottom(innerMin, outerMin, innerMax, innerMin))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));

                QuadModifier.full(quad)
                        .apply(Modifiers.cutTopBottom(innerMin, innerMax, innerMax, outerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F / 16F))
                        .export(quadMap.get(null));
            }

            if (mapFrame && !leather)
            {
                QuadModifier.full(quad)
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(quadFace));
            }
        }
        else
        {
            boolean down = facing == Direction.UP;

            QuadModifier.full(quad)
                    .apply(Modifiers.cutSideUpDown(down, 1F/16F))
                    .applyIf(Modifiers.cutSideLeftRight(outerMax), !mapFrame)
                    .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                    .applyIf(Modifiers.setPosition(outerMax), !mapFrame)
                    .export(quadMap.get(null));

            if (!mapFrame)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutSideUpDown(!down, 15.5F / 16F))
                        .apply(Modifiers.cutSideUpDown(down, 1F / 16F))
                        .apply(Modifiers.cutSideLeftRight(innerLength))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(innerPos))
                        .export(quadMap.get(null));
            }
        }
    }

    private void makeHorizontalFrame(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction quadFace)
    {
        if (quadFace == facing)
        {
            QuadModifier.full(quad)
                    .applyIf(Modifiers.cutSide(outerMin, outerMin, outerMax, outerMax), !mapFrame)
                    .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                    .export(quadMap.get(quadFace));
        }
        else if (quadFace == facing.getOpposite())
        {
            if (!leather && !mapFrame)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutSide(innerMin, innerMin, innerMax, innerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(.5F/16F))
                        .export(quadMap.get(null));
            }

            if (!mapFrame || leather)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutSide(outerMin, outerMin, innerMin, outerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));

                QuadModifier.full(quad)
                        .apply(Modifiers.cutSide(innerMax, outerMin, outerMax, outerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));

                QuadModifier.full(quad)
                        .apply(Modifiers.cutSide(innerMin, outerMin, innerMax, innerMin))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));

                QuadModifier.full(quad)
                        .apply(Modifiers.cutSide(innerMin, innerMax, innerMax, outerMax))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(null));
            }

            if (mapFrame && !leather)
            {
                QuadModifier.full(quad)
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(1F/16F))
                        .export(quadMap.get(quadFace));
            }
        }
        else if (Utils.isY(quadFace))
        {
            QuadModifier.full(quad)
                    .apply(Modifiers.cutTopBottom(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.cutTopBottom(facing.getClockWise().getAxis(), outerMax), !mapFrame)
                    .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                    .applyIf(Modifiers.setPosition(outerMax), !mapFrame)
                    .export(quadMap.get(null));

            if (!mapFrame)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutTopBottom(facing, 15.5F/16F))
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), 1F/16F))
                        .apply(Modifiers.cutTopBottom(facing.getClockWise().getAxis(), innerLength))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(innerPos))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            QuadModifier.full(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.cutSideUpDown(outerMax), !mapFrame)
                    .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                    .applyIf(Modifiers.setPosition(outerMax), !mapFrame)
                    .export(quadMap.get(null));

            if (!mapFrame)
            {
                QuadModifier.full(quad)
                        .apply(Modifiers.cutSideLeftRight(facing, 15.5F/16F))
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), 1F/16F))
                        .apply(Modifiers.cutSideUpDown(innerLength))
                        .applyIf(Modifiers.applyLightmap(GLOWING_BRIGHTNESS), glowing)
                        .apply(Modifiers.setPosition(innerPos))
                        .export(quadMap.get(null));
            }
        }
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        if (leather)
        {
            return ModelUtils.SOLID;
        }
        return super.getAdditionalRenderTypes(rand, extraData);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType)
    {
        if (leather)
        {
            List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data, renderType);
            for (BakedQuad quad : quads)
            {
                if (!quad.getSprite().getName().equals(ClientUtils.DUMMY_TEXTURE))
                {
                    quadMap.get(null).add(quad);
                }
            }
        }
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }

    

    public static FramedItemFrameModel normal(BlockState state, BakedModel baseModel)
    {
        return new FramedItemFrameModel(state, baseModel, false);
    }

    public static FramedItemFrameModel glowing(BlockState state, BakedModel baseModel)
    {
        return new FramedItemFrameModel(state, baseModel, true);
    }
}
