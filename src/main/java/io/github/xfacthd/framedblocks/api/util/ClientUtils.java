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

/// Provides various helpers for client-only operations.
public final class ClientUtils {
    /// Non-deprecated constant of the block texture atlas location.
    @SuppressWarnings("deprecation")
    public static final Identifier BLOCK_ATLAS = TextureAtlas.LOCATION_BLOCKS;
    /// Sprite ID of the white block texture provided by NeoForge.
    public static final Identifier DUMMY_TEXTURE = Utils.id("neoforge", "white");
    /// Material of the white block texture provided by NeoForge.
    public static final Material DUMMY_MATERIAL = new Material(DUMMY_TEXTURE);

    /// Enqueue the given task to run at the end of the current tick.
    ///
    /// @param task The task to enqueue
    public static void enqueueClientTask(Runnable task) {
        enqueueClientTask(0, task);
    }

    /// Enqueue the given task to run after the given delay.
    ///
    /// @param delay The delay in ticks
    /// @param task  The task to enqueue
    public static void enqueueClientTask(int delay, Runnable task) {
        InternalClientAPI.INSTANCE.enqueueClientTask(delay, task);
    }

    /// {@return the given level as a {@link BlockAndTintGetter} if possible, otherwise the empty tint getter}
    ///
    /// @param level The level to convert
    public static BlockAndTintGetter asTintGetter(@Nullable BlockAndLightGetter level) {
        return level instanceof BlockAndTintGetter tintGetter ? tintGetter : BlockAndTintGetter.EMPTY;
    }

    /// {@return whether the given quad uses the white block texture provided by NeoForge}
    ///
    /// @param quad The quad to check
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDummyTexture(BakedQuad quad) {
        return isTexture(quad, DUMMY_TEXTURE);
    }

    /// {@return whether the given quad uses the given texture}
    ///
    /// @param quad    The quad to check
    /// @param texture The texture to check against
    public static boolean isTexture(BakedQuad quad, Identifier texture) {
        return quad.materialInfo().sprite().contents().name().equals(texture);
    }

    /// Render a half-transparent ghost of the given itemstack.
    ///
    /// @param graphics The graphics extractor to use for rendering
    /// @param stack    The stack to render
    /// @param x        The X coordinate to render at
    /// @param y        The Y coordinate to render at
    public static void renderTransparentFakeItem(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        graphics.fakeItem(stack, x, y, 0);
        graphics.fill(x, y, x + 16, y + 16, 0x80888888);
    }

    /// {@return the sprite with the given ID on the block atlas}
    ///
    /// @param id The ID of the sprite to get
    public static TextureAtlasSprite getBlockSprite(Identifier id) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(id);
    }

    /// {@return the "entity" render type of the given chunk layer}
    ///
    /// @param chunkLayer The chunk layer to convert
    public static RenderType getEntityRenderType(ChunkSectionLayer chunkLayer) {
        return switch (chunkLayer) {
            case SOLID -> NeoForgeRenderTypes.SOLID_BLOCK_SHEET;
            case CUTOUT -> Sheets.cutoutBlockSheet();
            case TRANSLUCENT -> Sheets.translucentBlockSheet();
        };
    }

    private ClientUtils() { }
}
