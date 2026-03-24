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

public abstract class BlockInteractOverlay
{
    private final List<Component> allLines;
    protected final List<Component> linesFalse;
    protected final List<Component> linesTrue;
    protected final Texture textureFalse;
    protected final Texture textureTrue;
    private final Supplier<OverlayDisplayMode> modeGetter;

    protected BlockInteractOverlay(
            List<Component> linesFalse,
            List<Component> linesTrue,
            Texture textureFalse,
            Texture textureTrue,
            Supplier<OverlayDisplayMode> modeGetter
    )
    {
        this.allLines = Utils.concat(linesFalse, linesTrue);
        this.linesFalse = linesFalse;
        this.linesTrue = linesTrue;
        this.textureFalse = textureFalse;
        this.textureTrue = textureTrue;
        this.modeGetter = modeGetter;
    }

    public abstract boolean isValidTool(Player player, ItemStack stack);

    public abstract boolean isValidTarget(Target target);

    public abstract boolean getState(Target target);

    public Texture getTexture(Target target, boolean state)
    {
        return state ? textureTrue : textureFalse;
    }

    public List<Component> getLines(Target target, boolean state)
    {
        return state ? linesTrue : linesFalse;
    }

    public void renderAfterIcon(GuiGraphicsExtractor graphics, Texture tex, int texX, int texY, Target target) { }

    @ApiStatus.Internal
    public final OverlayDisplayMode getDisplayMode()
    {
        return modeGetter.get();
    }

    @ApiStatus.Internal
    public final List<Component> getAllLines()
    {
        return allLines;
    }

    public record Target(Level level, BlockPos pos, BlockState state, Direction side, Player player) { }

    public record Texture(Identifier location, int xOff, int yOff, int width, int height, int texWidth, int texHeight)
    {
        public void draw(GuiGraphicsExtractor graphics, int x, int y)
        {
            graphics.blit(RenderPipelines.GUI_TEXTURED, location, x, y, xOff, yOff, width, height, texWidth, texHeight);
        }
    }
}
