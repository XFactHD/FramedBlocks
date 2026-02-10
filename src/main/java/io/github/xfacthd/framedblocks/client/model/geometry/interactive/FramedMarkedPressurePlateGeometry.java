package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
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
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public class FramedMarkedPressurePlateGeometry extends FramedPressurePlateGeometry
{
    private static final Identifier STONE_FRAME_LOCATION = Utils.id("block/stone_plate_frame");
    private static final Identifier OBSIDIAN_FRAME_LOCATION = Utils.id("block/obsidian_plate_frame");
    private static final Identifier GOLD_FRAME_LOCATION = Utils.id("block/gold_plate_frame");
    private static final Identifier IRON_FRAME_LOCATION = Utils.id("block/iron_plate_frame");
    private static final @Nullable Direction[] OVERLAY_CULL_FACES = { Direction.DOWN, null };

    private final BlockState state;
    private final TextureAtlasSprite frameSprite;
    private final BlockState frameShaderState;

    private FramedMarkedPressurePlateGeometry(GeometryFactory.Context ctx, TextureAtlasSprite frameSprite, BlockState frameShaderState, boolean powered)
    {
        super(powered, true);
        this.state = ctx.state();
        this.frameSprite = frameSprite;
        this.frameShaderState = frameShaderState;
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, ModelData data, @Nullable Object cacheKeyUserData)
    {
        AbstractFramedBlockData fbData = data.get(AbstractFramedBlockData.PROPERTY);
        if (fbData != null && !fbData.unwrap(state).getCamoContent().isEmpty())
        {
            generator.generate(OVERLAY_CULL_FACES, frameSprite, Utils::isY, ChunkSectionLayer.CUTOUT, frameShaderState);
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
        return new FramedMarkedPressurePlateGeometry(ctx, frame, Blocks.STONE.defaultBlockState(), powered);
    }

    public static FramedPressurePlateGeometry obsidian(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(PressurePlateBlock.POWERED);
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(OBSIDIAN_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(ctx, frame, Blocks.OBSIDIAN.defaultBlockState(), powered);
    }

    public static FramedPressurePlateGeometry gold(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(GOLD_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(ctx, frame, Blocks.GOLD_BLOCK.defaultBlockState(), powered);
    }

    public static FramedPressurePlateGeometry iron(GeometryFactory.Context ctx)
    {
        boolean powered = ctx.state().getValue(WeightedPressurePlateBlock.POWER) > 0;
        if (!ClientConfig.VIEW.showButtonPlateOverlay())
        {
            return new FramedPressurePlateGeometry(powered, true);
        }

        TextureAtlasSprite frame = ctx.textureLookup().get(IRON_FRAME_LOCATION);
        return new FramedMarkedPressurePlateGeometry(ctx, frame, Blocks.IRON_BLOCK.defaultBlockState(), powered);
    }
}
