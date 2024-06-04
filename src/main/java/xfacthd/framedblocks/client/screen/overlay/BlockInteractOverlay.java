package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.util.ConcatenatedListView;

import java.util.*;
import java.util.function.Supplier;

public abstract class BlockInteractOverlay implements IGuiOverlay
{
    private static final int LINE_DIST = 3;
    private static final Target NO_TARGET = new Target(BlockPos.ZERO, Blocks.AIR.defaultBlockState(), Direction.NORTH);
    private static final List<BlockInteractOverlay> OVERLAYS = new ArrayList<>();

    private final List<Component> linesFalse;
    private final List<Component> linesTrue;
    private final Texture textureFalse;
    private final Texture textureTrue;
    private final Supplier<Mode> modeGetter;
    private int textWidth = 0;
    private boolean textWidthValid = false;

    BlockInteractOverlay(
            List<Component> linesFalse,
            List<Component> linesTrue,
            Texture textureFalse,
            Texture textureTrue,
            Supplier<Mode> modeGetter
    )
    {
        this.linesFalse = linesFalse;
        this.linesTrue = linesTrue;
        this.textureFalse = textureFalse;
        this.textureTrue = textureTrue;
        this.modeGetter = modeGetter;
        OVERLAYS.add(this);
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight)
    {
        Mode mode = modeGetter.get();
        if (mode == Mode.HIDDEN || player().isSpectator() || Minecraft.getInstance().options.hideGui)
        {
            return;
        }

        ItemStack stack = player().getMainHandItem();
        if (!isValidTool(stack))
        {
            return;
        }

        Target target = getTargettedBlock();
        if (!isValidTarget(target))
        {
            return;
        }

        boolean state = getState(target);
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        Texture tex = getTexture(target, state, textureFalse, textureTrue);
        int texX = centerX + 20;
        int texY = centerY - (tex.height / 2);
        tex.draw(gui, graphics, texX, texY);
        renderAfterIcon(gui, graphics, tex, texX, texY, target);

        if (mode == Mode.DETAILED)
        {
            List<Component> lines = getLines(target, state, linesFalse, linesTrue);
            renderDetailed(gui, graphics, tex, lines, centerX, screenHeight, target);
        }
    }

    private void renderDetailed(
            ForgeGui gui,
            GuiGraphics graphics,
            Texture tex,
            List<Component> lines,
            int centerX,
            int screenHeight,
            Target target
    )
    {
        Font font = gui.getFont();
        if (!textWidthValid)
        {
            updateTextWidth(font);
        }

        int lineHeight = font.lineHeight + LINE_DIST;
        int count = lines.size();
        int contentHeight = count * lineHeight - LINE_DIST;

        int width = textWidth + tex.width + 10;
        int height = Math.max(contentHeight, tex.height);
        int x = centerX - (width / 2);
        int y = screenHeight - 80 - height;
        drawTooltipBackground(graphics, x, y, width, height);

        int textX = x + tex.width + 10;
        int yBaseOff = tex.height > contentHeight ? ((tex.height - contentHeight) / 2) : 0;
        for (int i = 0; i < count; i++)
        {
            Component text = lines.get(i);
            int yOff = yBaseOff + lineHeight * i;
            graphics.drawString(font, text, textX, y + yOff, -1);
        }

        int texY = y + (height / 2) - (tex.height / 2);
        tex.draw(gui, graphics, x, texY);
        renderAfterIcon(gui, graphics, tex, x, texY, target);
    }

    protected abstract boolean isValidTool(ItemStack stack);

    protected abstract boolean isValidTarget(Target target);

    protected abstract boolean getState(Target target);

    protected Texture getTexture(Target target, boolean state, Texture texFalse, Texture texTrue)
    {
        return state ? texTrue : texFalse;
    }

    protected List<Component> getLines(Target target, boolean state, List<Component> linesFalse, List<Component> linesTrue)
    {
        return state ? linesTrue : linesFalse;
    }

    protected void renderAfterIcon(ForgeGui gui, GuiGraphics graphics, Texture tex, int texX, int texY, Target target) { }

    private void updateTextWidth(Font font)
    {
        textWidth = 0;
        for (Component line : ConcatenatedListView.of(linesFalse, linesTrue))
        {
            textWidth = Math.max(textWidth, font.width(line));
        }
        textWidthValid = true;
    }



    public static void onResourceReload(@SuppressWarnings("unused") ResourceManager manager)
    {
        OVERLAYS.forEach(overlay -> overlay.textWidthValid = false);
    }

    protected static BlockGetter level()
    {
        return Objects.requireNonNull(Minecraft.getInstance().level);
    }

    protected static Player player()
    {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    protected static Target getTargettedBlock()
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit)
        {
            BlockPos pos = blockHit.getBlockPos();
            return new Target(pos, level().getBlockState(pos), blockHit.getDirection());
        }
        return NO_TARGET;
    }

    @SuppressWarnings("deprecation")
    private static void drawTooltipBackground(GuiGraphics graphics, int x, int y, int width, int height)
    {
        graphics.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground(
                graphics,
                x - 2, y - 2, width + 4, height + 4, 0
        ));
    }

    protected record Texture(
            ResourceLocation location, int xOff, int yOff, int width, int height, int texWidth, int texHeight
    )
    {
        public void draw(ForgeGui gui, GuiGraphics graphics, int x, int y)
        {
            gui.setupOverlayRenderState(true, false);
            graphics.blit(location, x, y, 0, xOff, yOff, width, height, texWidth, texHeight);
        }
    }

    protected record Target(BlockPos pos, BlockState state, Direction side) { }

    public enum Mode
    {
        HIDDEN,
        ICON,
        DETAILED
    }
}
