package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import org.jspecify.annotations.Nullable;

public class FramedStoneButtonGeometry extends FramedButtonGeometry
{
    private static final Identifier FRAME_LOCATION_FRONT = Utils.id("block/stone_button_frame_front");
    private static final Identifier FRAME_LOCATION_NARROW = Utils.id("block/stone_button_frame_narrow");
    private static final Identifier FRAME_LOCATION_WIDE = Utils.id("block/stone_button_frame_wide");

    private final TextureAtlasSprite frameSpriteFront;
    private final TextureAtlasSprite frameSpriteNarrow;
    private final TextureAtlasSprite frameSpriteWide;
    private final @Nullable Direction[] overlayCullFaces;
    private final OverlayPartGenerator.SpriteGetter overlaySpriteGetter;

    private FramedStoneButtonGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
        this.frameSpriteFront = ctx.textureLookup().get(FRAME_LOCATION_FRONT);
        this.frameSpriteNarrow = ctx.textureLookup().get(FRAME_LOCATION_NARROW);
        this.frameSpriteWide = ctx.textureLookup().get(FRAME_LOCATION_WIDE);
        this.overlayCullFaces = new @Nullable Direction[] { facing.getOpposite(), null };
        Direction.Axis wideAxis = face == AttachFace.WALL ? Direction.Axis.Y : dir.getAxis();
        this.overlaySpriteGetter = dir ->
        {
            if (dir.getAxis() == facing.getAxis()) return frameSpriteFront;
            if (dir.getAxis() == wideAxis) return frameSpriteWide;
            return frameSpriteNarrow;
        };
    }

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        return !blockData.getCamoContent().isEmpty();
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData)
    {
        generator.generate(overlayCullFaces, overlaySpriteGetter, frameSpriteFront, ChunkSectionLayer.CUTOUT, Blocks.STONE.defaultBlockState());
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
