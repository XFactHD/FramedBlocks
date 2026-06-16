package io.github.xfacthd.framedblocks.api.util;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import org.jspecify.annotations.Nullable;

public final class ClientUtils {
    @SuppressWarnings("deprecation")
    public static final Identifier BLOCK_ATLAS = TextureAtlas.LOCATION_BLOCKS;
    public static final Identifier DUMMY_TEXTURE = Utils.id("neoforge", "white");
    public static final Material DUMMY_MATERIAL = new Material(DUMMY_TEXTURE);

    public static void enqueueClientTask(Runnable task) {
        enqueueClientTask(0, task);
    }

    public static void enqueueClientTask(int delay, Runnable task) {
        InternalClientAPI.INSTANCE.enqueueClientTask(delay, task);
    }

    public static BlockAndTintGetter asTintGetter(@Nullable BlockAndLightGetter level) {
        return level instanceof BlockAndTintGetter tintGetter ? tintGetter : BlockAndTintGetter.EMPTY;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDummyTexture(BakedQuad quad) {
        return isTexture(quad, DUMMY_TEXTURE);
    }

    public static boolean isTexture(BakedQuad quad, Identifier texture) {
        return quad.materialInfo().sprite().contents().name().equals(texture);
    }

    public static void renderTransparentFakeItem(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        graphics.fakeItem(stack, x, y, 0);
        graphics.fill(x, y, x + 16, y + 16, 0x80888888);
    }

    public static TextureAtlasSprite getBlockSprite(Identifier id) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(id);
    }

    public static RenderType getEntityRenderType(ChunkSectionLayer chunkLayer) {
        return switch (chunkLayer) {
            case SOLID -> NeoForgeRenderTypes.SOLID_BLOCK_SHEET;
            case CUTOUT -> Sheets.cutoutBlockItemSheet();
            case TRANSLUCENT -> Sheets.translucentBlockItemSheet();
        };
    }

    private ClientUtils() { }
}
