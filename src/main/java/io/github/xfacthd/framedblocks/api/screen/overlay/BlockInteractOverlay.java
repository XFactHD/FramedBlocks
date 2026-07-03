package io.github.xfacthd.framedblocks.api.screen.overlay;

import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

/// Base class for HUD overlays indicating applicability of an interaction with a targetted block and/or
/// the current state of the data modified by said interaction.
public abstract class BlockInteractOverlay {
    private final List<Component> allLines;
    protected final List<Component> linesFalse;
    protected final List<Component> linesTrue;
    protected final Texture textureFalse;
    protected final Texture textureTrue;
    private final Supplier<OverlayDisplayMode> modeGetter;

    /// @param linesFalse   The text lines to display in detailed mode when [#getState(Target)] returns `false`
    /// @param linesTrue    The text lines to display in detailed mode when [#getState(Target)] returns `true`
    /// @param textureFalse The icon to display when [#getState(Target)] returns `false`
    /// @param textureTrue  The icon to display when [#getState(Target)] returns `true`
    /// @param modeGetter   A supplier providing the mode config value of this overlay
    protected BlockInteractOverlay(
            List<Component> linesFalse,
            List<Component> linesTrue,
            Texture textureFalse,
            Texture textureTrue,
            Supplier<OverlayDisplayMode> modeGetter
    ) {
        this.allLines = Utils.concat(linesFalse, linesTrue);
        this.linesFalse = linesFalse;
        this.linesTrue = linesTrue;
        this.textureFalse = textureFalse;
        this.textureTrue = textureTrue;
        this.modeGetter = modeGetter;
    }

    /// {@return whether the given stack held by the player is valid for the interaction this overlay belongs to}
    /// If this method returns `false`, then this overlay is not shown.
    ///
    /// @param player The player looking at a framed block
    /// @param stack  The item held by the player
    public abstract boolean isValidTool(Player player, ItemStack stack);

    /// {@return whether the provided target is valid for the interaction this overlay belongs to}
    /// If this method returns `false`, then this overlay is not shown.
    ///
    /// @param target The targetted block
    public abstract boolean isValidTarget(Target target);

    /// Returns the state of the interaction. The exact meaning of the return value
    /// depends on the overlay.
    ///
    /// @param target The targetted block
    /// @return the state of the interaction
    public abstract boolean getState(Target target);

    /// {@return the icon to display for the given target and interaction state}
    ///
    /// @param target The targetted block
    /// @param state  The state of the interaction
    public Texture getTexture(Target target, boolean state) {
        return state ? textureTrue : textureFalse;
    }

    /// {@return the text lines to display in detailed mode for the given target and interaction state}
    ///
    /// @param target The targetted block
    /// @param state  The state of the interaction
    public List<Component> getLines(Target target, boolean state) {
        return state ? linesTrue : linesFalse;
    }

    /// Render additional content relative to the icon.
    ///
    /// @param graphics The graphics extractor to use for rendering
    /// @param tex      The icon rendered prior to this being called
    /// @param texX     The X coordinate the icon was rendered at
    /// @param texY     The Y coordinate the icon was rendered at
    /// @param target   The targetted block
    public void renderAfterIcon(GuiGraphicsExtractor graphics, Texture tex, int texX, int texY, Target target) { }

    @ApiStatus.Internal
    public final OverlayDisplayMode getDisplayMode() {
        return modeGetter.get();
    }

    @ApiStatus.Internal
    public final List<Component> getAllLines() {
        return allLines;
    }

    /// Represents the target block the player is looking at.
    ///
    /// @param level  The level the block is in
    /// @param pos    The position of the block
    /// @param state  The state of the block
    /// @param side   The targetted side of the block
    /// @param player The player looking at the block
    public record Target(Level level, BlockPos pos, BlockState state, Direction side, Player player) { }

    /// Represents a texture to draw on the overlay.
    ///
    /// @param location  The texture's location relative to the pack root and with file extension
    /// @param xOff      The X offset of the section to render
    /// @param yOff      The Y offset of the section to render
    /// @param width     The width of the section to render
    /// @param height    The height of the section to render
    /// @param texWidth  The width of the full texture
    /// @param texHeight The height of the full texture
    public record Texture(Identifier location, int xOff, int yOff, int width, int height, int texWidth, int texHeight) {
        /// Draw this texture at the given coordinates.
        ///
        /// @param graphics The graphics extractor to use for rendering
        /// @param x        The X coordinate to render this texture at
        /// @param y        The Y coordinate to render this texture at
        public void draw(GuiGraphicsExtractor graphics, int x, int y) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, location, x, y, xOff, yOff, width, height, texWidth, texHeight);
        }
    }
}
