package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.config.ClientConfig;

public class FramedLargeStoneButtonGeometry extends FramedLargeButtonGeometry
{
    private static final ResourceLocation FRAME_LOCATION_FRONT = Utils.rl("block/large_stone_button_frame_front");
    private static final ResourceLocation FRAME_LOCATION_SIDE = Utils.rl("block/large_stone_button_frame_side");

    private final TextureAtlasSprite frameSpriteFront;
    private final TextureAtlasSprite frameSpriteSide;

    private FramedLargeStoneButtonGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
        this.frameSpriteFront = ctx.textureLookup().get(FRAME_LOCATION_FRONT);
        this.frameSpriteSide = ctx.textureLookup().get(FRAME_LOCATION_SIDE);
    }

    @Override
    public ChunkRenderTypeSet getOverlayRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoState().isAir())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    public void getGeneratedOverlayQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType layer)
    {
        FramedBlocksClientAPI.INSTANCE.generateOverlayQuads(quadMap, facing.getOpposite(), frameSpriteFront);
        FramedBlocksClientAPI.INSTANCE.generateOverlayQuads(quadMap, null, dir -> dir == facing ? frameSpriteFront : frameSpriteSide, dir -> true);
    }



    public static FramedLargeButtonGeometry create(GeometryFactory.Context ctx)
    {
        if (ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedLargeStoneButtonGeometry(ctx);
        }
        return new FramedLargeButtonGeometry(ctx);
    }
}
