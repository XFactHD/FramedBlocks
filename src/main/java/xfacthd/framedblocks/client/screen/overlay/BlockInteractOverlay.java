package xfacthd.framedblocks.client.screen.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.util.ConcatenatedListView;

import java.util.*;

public abstract class BlockInteractOverlay implements IGuiOverlay
{
    private static final int LINE_DIST = 3;
    private static final List<BlockInteractOverlay> OVERLAYS = new ArrayList<>();

    private final List<Component> linesFalse;
    private final List<Component> linesTrue;
    private final Texture textureFalse;
    private final Texture textureTrue;
    private int textWidth = 0;
    private boolean textWidthValid = false;

    BlockInteractOverlay(List<Component> linesFalse, List<Component> linesTrue, Texture textureFalse, Texture textureTrue)
    {
        this.linesFalse = linesFalse;
        this.linesTrue = linesTrue;
        this.textureFalse = textureFalse;
        this.textureTrue = textureTrue;
        OVERLAYS.add(this);
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight)
    {
        ItemStack stack = player().getMainHandItem();
        if (!isValidTool(stack)) { return; }

        BlockState block = getTargettedBlock();
        if (!isValidTarget(block)) { return; }

        boolean state = getState(block);
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        Texture tex = state ? textureTrue : textureFalse;
        renderMainIcon(gui, poseStack, tex, centerX, centerY);

        if (showDetailed())
        {
            List<Component> lines = state ? linesTrue : linesFalse;
            renderDetailed(gui, poseStack, tex, lines, centerX, screenHeight);
        }
    }

    private static void renderMainIcon(ForgeGui gui, PoseStack poseStack, Texture tex, int centerX, int centerY)
    {
        int texY = centerY - (tex.height / 2);
        gui.setupOverlayRenderState(true, false, tex.location);
        //noinspection SuspiciousNameCombination
        GuiComponent.blit(poseStack, centerX + 20, texY, 0, tex.xOff, tex.yOff, tex.width, tex.height, tex.texWidth, tex.texHeight);
    }

    private void renderDetailed(ForgeGui gui, PoseStack poseStack, Texture tex, List<Component> lines, int centerX, int screenHeight)
    {
        Font font = gui.getFont();
        if (!textWidthValid) { updateTextWidth(font); }

        int lineHeight = font.lineHeight + LINE_DIST;
        int count = lines.size();
        int contentHeight = count * lineHeight - LINE_DIST;

        int width = textWidth + tex.width + 10;
        int height = Math.max(contentHeight, tex.height);
        int x = centerX - (width / 2);
        int y = screenHeight - 80 - height;
        drawTooltipBackground(poseStack, x, y, width, height, gui.getBlitOffset());

        int textX = x + tex.width + 10;
        int yBaseOff = tex.height > contentHeight ? ((tex.height - contentHeight) / 2) : 0;
        for (int i = 0; i < count; i++)
        {
            Component text = lines.get(i);
            int yOff = yBaseOff + lineHeight * i;
            GuiComponent.drawString(poseStack, font, text, textX, y + yOff, -1);
        }

        int texY = y + (height / 2) - (tex.height / 2);
        gui.setupOverlayRenderState(true, false, tex.location);
        //noinspection SuspiciousNameCombination
        GuiComponent.blit(poseStack, x, texY, gui.getBlitOffset(), tex.xOff, tex.yOff, tex.width, tex.height, tex.texWidth, tex.texHeight);
    }

    protected abstract boolean isValidTool(ItemStack stack);

    protected abstract boolean isValidTarget(BlockState state);

    protected abstract boolean getState(BlockState state);

    protected abstract boolean showDetailed();

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

    protected static Player player()
    {
        return Objects.requireNonNull(Minecraft.getInstance().player);
    }

    protected static BlockState getTargettedBlock()
    {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof BlockHitResult blockHit)
        {
            return Objects.requireNonNull(Minecraft.getInstance().level).getBlockState(blockHit.getBlockPos());
        }
        return Blocks.AIR.defaultBlockState();
    }

    private static void drawTooltipBackground(PoseStack poseStack, int x, int y, int width, int height, int blitOffset)
    {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        TooltipRenderUtil.renderTooltipBackground(
                GuiComponent::fillGradient,
                poseStack.last().pose(),
                buffer,
                x - 2, y - 2, width + 4, height + 4, blitOffset
        );

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, .95F);
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    protected record Texture(ResourceLocation location, int xOff, int yOff, int width, int height, int texWidth, int texHeight) { }
}
