package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;

public class FramedMarkedPressurePlateGeometry extends FramedPressurePlateGeometry
{
    private static final ResourceLocation STONE_FRAME_LOCATION = Utils.rl("block/stone_plate_frame");
    private static final ResourceLocation OBSIDIAN_FRAME_LOCATION = Utils.rl("block/obsidian_plate_frame");
    private static final ResourceLocation GOLD_FRAME_LOCATION = Utils.rl("block/gold_plate_frame");
    private static final ResourceLocation IRON_FRAME_LOCATION = Utils.rl("block/iron_plate_frame");

    private final TextureAtlasSprite frameSprite;

    private FramedMarkedPressurePlateGeometry(TextureAtlasSprite frameSprite, boolean powered)
    {
        super(powered, true);
        this.frameSprite = frameSprite;
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
        if (layer == RenderType.cutout())
        {
            FramedBlocksClientAPI.INSTANCE.generateOverlayQuads(quadMap, null, frameSprite, dir -> dir == Direction.UP);
            FramedBlocksClientAPI.INSTANCE.generateOverlayQuads(quadMap, Direction.DOWN, frameSprite);
        }
    }



    public static FramedPressurePlateGeometry stone(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(STONE_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static FramedPressurePlateGeometry obsidian(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(OBSIDIAN_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static FramedPressurePlateGeometry gold(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(GOLD_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }

    public static FramedPressurePlateGeometry iron(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(IRON_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(frame, powered);
    }
}
