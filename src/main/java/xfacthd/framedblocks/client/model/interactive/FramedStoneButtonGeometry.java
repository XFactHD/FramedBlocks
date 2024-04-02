package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.config.ClientConfig;

public class FramedStoneButtonGeometry extends FramedButtonGeometry
{
    private static final ResourceLocation FRAME_LOCATION_FRONT = Utils.rl("block/stone_button_frame_front");
    private static final ResourceLocation FRAME_LOCATION_NARROW = Utils.rl("block/stone_button_frame_narrow");
    private static final ResourceLocation FRAME_LOCATION_WIDE = Utils.rl("block/stone_button_frame_wide");

    private final TextureAtlasSprite frameSpriteFront;
    private final TextureAtlasSprite frameSpriteNarrow;
    private final TextureAtlasSprite frameSpriteWide;

    private FramedStoneButtonGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
        this.frameSpriteFront = ctx.textureLookup().get(FRAME_LOCATION_FRONT);
        this.frameSpriteNarrow = ctx.textureLookup().get(FRAME_LOCATION_NARROW);
        this.frameSpriteWide = ctx.textureLookup().get(FRAME_LOCATION_WIDE);
    }

    @Override
    public ChunkRenderTypeSet getOverlayRenderTypes(RandomSource rand, ModelData extraData)
    {
        FramedBlockData fbData = extraData.get(FramedBlockData.PROPERTY);
        if (fbData != null && !fbData.getCamoContent().isEmpty())
        {
            return ModelUtils.CUTOUT;
        }
        return ChunkRenderTypeSet.none();
    }

    @Override
    public void getGeneratedOverlayQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType layer)
    {
        FramedBlocksClientAPI.INSTANCE.generateOverlayQuads(quadMap, facing.getOpposite(), frameSpriteFront);
        FramedBlocksClientAPI.INSTANCE.generateOverlayQuads(quadMap, null, dir ->
        {
            if (dir == facing) return frameSpriteFront;
            if (dir.getAxis() == this.dir.getClockWise().getAxis()) return frameSpriteNarrow;

            Direction.Axis wideAxis = face == AttachFace.WALL ? Direction.Axis.Y : dir.getAxis();
            if (dir.getAxis() == wideAxis) return frameSpriteWide;

            throw new IllegalArgumentException("Invalid face %s for facing %s".formatted(dir, facing));
        }, dir -> true);
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }



    public static FramedButtonGeometry create(GeometryFactory.Context ctx)
    {
        if (ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedStoneButtonGeometry(ctx);
        }
        return new FramedButtonGeometry(ctx);
    }
}
