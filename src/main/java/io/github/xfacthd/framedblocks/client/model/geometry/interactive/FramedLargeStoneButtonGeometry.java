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
import org.jspecify.annotations.Nullable;

public class FramedLargeStoneButtonGeometry extends FramedLargeButtonGeometry
{
    private static final Identifier FRAME_LOCATION_FRONT = Utils.id("block/large_stone_button_frame_front");
    private static final Identifier FRAME_LOCATION_SIDE = Utils.id("block/large_stone_button_frame_side");

    private final TextureAtlasSprite frameSpriteFront;
    private final TextureAtlasSprite frameSpriteSide;
    private final @Nullable Direction[] overlayCullFaces;
    private final OverlayPartGenerator.SpriteGetter overlaySpriteGetter;

    private FramedLargeStoneButtonGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
        this.frameSpriteFront = ctx.textureLookup().get(FRAME_LOCATION_FRONT);
        this.frameSpriteSide = ctx.textureLookup().get(FRAME_LOCATION_SIDE);
        this.overlayCullFaces = new @Nullable Direction[] { facing.getOpposite(), null };
        this.overlaySpriteGetter = dir -> dir.getAxis() == facing.getAxis() ? frameSpriteFront : frameSpriteSide;
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

    public static FramedLargeButtonGeometry create(GeometryFactory.Context ctx)
    {
        if (ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedLargeStoneButtonGeometry(ctx);
        }
        return new FramedLargeButtonGeometry(ctx);
    }
}
