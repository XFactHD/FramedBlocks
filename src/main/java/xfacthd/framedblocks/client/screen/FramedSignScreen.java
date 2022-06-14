package xfacthd.framedblocks.client.screen;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;
import xfacthd.framedblocks.common.net.SignUpdatePacket;

import java.util.List;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FramedSignScreen extends Screen
{
    private static final Table<BlockState, Direction, TextureAtlasSprite> SPRITE_CACHE = HashBasedTable.create();
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_block");
    private static final ITextComponent DONE = new TranslationTextComponent("gui.done");

    private final FramedSignTileEntity sign;
    private final String[] lines = new String[4];
    private int blinkCounter = 0;
    private int currLine = 0;
    private TextInputUtil inputUtil;

    public FramedSignScreen(FramedSignTileEntity sign)
    {
        super(new TranslationTextComponent("sign.edit"));
        this.sign = sign;

        for (int i = 0; i < 4; i++) { lines[i] = sign.getLine(i).getString(); }
    }

    @Override
    protected void init()
    {
        //noinspection ConstantConditions
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        addButton(new Button(width / 2 - 100, height / 2 + 60, 200, 20, DONE, btn -> onClose()));

        inputUtil = new TextInputUtil(() -> lines[currLine],
                (line) ->
                {
                    lines[currLine] = line;
                    sign.setLine(currLine, new StringTextComponent(line));
                },
                TextInputUtil.createClipboardGetter(minecraft), TextInputUtil.createClipboardSetter(minecraft),
                (line) -> minecraft.font.width(line) <= 90);
    }

    @Override
    public void removed()
    {
        //noinspection ConstantConditions
        minecraft.keyboardHandler.setSendRepeatsToGui(false);

        FramedBlocks.CHANNEL.sendToServer(new SignUpdatePacket(sign.getBlockPos(), new String[]
                {
                        sign.getLine(0).getString(),
                        sign.getLine(1).getString(),
                        sign.getLine(2).getString(),
                        sign.getLine(3).getString()
                }));
    }

    @Override
    public void tick()
    {
        blinkCounter++;

        if (!sign.getType().isValid(sign.getBlockState().getBlock()))
        {
            removed();
        }
    }

    @Override
    public boolean charTyped(char character, int modifiers)
    {
        inputUtil.charTyped(character);
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        if (key == GLFW.GLFW_KEY_UP)
        {
            currLine = currLine - 1 & 3;
            inputUtil.setCursorToEnd();
            return true;
        }
        else if (key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)
        {
            currLine = currLine + 1 & 3;
            inputUtil.setCursorToEnd();
            return true;
        }
        else
        {
            return inputUtil.keyPressed(key) || super.keyPressed(key, scanCode, modifiers);
        }
    }

    @Override
    public void render(MatrixStack mstack, int mouseX, int mouseY, float partialTicks)
    {
        RenderHelper.setupForFlatItems();

        renderBackground(mstack);
        //noinspection ConstantConditions
        drawCenteredString(mstack, font, title, width / 2, 40, TextFormatting.WHITE.getColor());

        //noinspection ConstantConditions
        minecraft.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
        TextureAtlasSprite sprite = getFrontSprite();

        int w = 128;
        int h = 64;
        int x = width / 2 - w / 2;
        int y = height / 2 - h / 2 - 20;
        innerBlit(mstack.last().pose(),
                x, x + w, y, y + h, getBlitOffset(),
                sprite.getU0(),
                sprite.getU1(),
                sprite.getV0(),
                sprite.getV(8)
        );

        mstack.pushPose();
        mstack.translate(width / 2D, height / 2D - 20, getBlitOffset());
        mstack.scale(1.2F, 1.2F, 1F);

        IRenderTypeBuffer.Impl buffer = minecraft.renderBuffers().bufferSource();

        drawLines(mstack.last().pose(), buffer, lines);
        drawCursor(mstack, buffer, lines);

        mstack.popPose();

        RenderHelper.setupFor3DItems();
        super.render(mstack, mouseX, mouseY, partialTicks);
    }

    private void drawLines(Matrix4f matrix, IRenderTypeBuffer.Impl buffer, String[] lines)
    {
        int color = sign.getTextColor().getTextColor();

        for(int line = 0; line < lines.length; line++)
        {
            String text = lines[line];
            if (text != null)
            {
                if (font.isBidirectional()) { text = font.bidirectionalShaping(text); }

                float textX = -font.width(text) / 2F;
                font.drawInBatch(text, textX, line * 10 - 20, color, false, matrix, buffer, false, 0, 15728880);
            }
        }

        buffer.endBatch();
    }

    private void drawCursor(MatrixStack mstack, IRenderTypeBuffer.Impl buffer, String[] lines)
    {
        Matrix4f matrix = mstack.last().pose();
        int color = sign.getTextColor().getTextColor();
        boolean blink = blinkCounter / 6 % 2 == 0;
        int dir = font.isBidirectional() ? -1 : 1;
        int y = currLine * 10 - 20;

        for(int i = 0; i < lines.length; ++i)
        {
            String line = lines[i];
            if (line != null && i == currLine && inputUtil.getCursorPos() >= 0)
            {
                int hw = font.width(line) / 2;
                int selectionEnd = font.width(line.substring(0, Math.max(Math.min(inputUtil.getCursorPos(), line.length()), 0)));
                int cursorX = (selectionEnd - hw) * dir;

                if (blink)
                {
                    if (inputUtil.getCursorPos() < line.length())
                    {
                        fill(mstack, cursorX, y - 1, cursorX + 1, y + 9, 0xff000000 | color);
                    }
                    else
                    {
                        font.drawInBatch("_", cursorX, y, color, false, matrix, buffer, false, 0, 15728880);
                        buffer.endBatch();
                    }
                }

                if (inputUtil.getSelectionPos() != inputUtil.getCursorPos())
                {
                    int x1 = (font.width(line.substring(0, inputUtil.getSelectionPos())) - hw) * dir;
                    int x2 =   (font.width(line.substring(0, inputUtil.getCursorPos()  )) - hw) * dir;
                    int xStart = Math.min(x1, x2);
                    int xEnd = Math.max(x1, x2);

                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder tessBuffer = tessellator.getBuilder();

                    RenderSystem.disableTexture();
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);

                    tessBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                    tessBuffer.vertex(matrix, xStart, y + 9F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.vertex(matrix,   xEnd, y + 9F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.vertex(matrix,   xEnd, y - 1F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.vertex(matrix, xStart, y - 1F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.end();
                    WorldVertexBufferUploader.end(tessBuffer);

                    RenderSystem.disableColorLogicOp();
                    RenderSystem.enableTexture();
                }
            }
        }
    }



    @SuppressWarnings("ConstantConditions")
    private TextureAtlasSprite getFrontSprite()
    {
        Direction front;

        BlockState state = sign.getBlockState();
        if (state.getBlock() == FBContent.blockFramedWallSign.get())
        {
            front = state.getValue(PropertyHolder.FACING_HOR);
        }
        else
        {
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            double angle = rot * 360D / 16D;
            front = Direction.fromYRot(angle);
        }

        BlockState camoState = sign.getCamoState();
        if (camoState == Blocks.AIR.defaultBlockState())
        {
            camoState = FBContent.blockFramedCube.get().defaultBlockState();
        }

        if (!SPRITE_CACHE.contains(camoState, front))
        {
            IBakedModel model = minecraft.getBlockRenderer().getBlockModel(camoState);
            List<BakedQuad> quads = model.getQuads(camoState, front, minecraft.level.getRandom(), EmptyModelData.INSTANCE);

            TextureAtlasSprite sprite;
            if (!quads.isEmpty())
            {
                sprite = quads.get(0).getSprite();
            }
            else
            {
                sprite = minecraft.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(DEFAULT_TEXTURE);
            }

            SPRITE_CACHE.put(camoState, front, sprite);
        }

        return SPRITE_CACHE.get(camoState, front);
    }

    @SubscribeEvent
    public static void onTextureStitch(final TextureStitchEvent.Pre event) { SPRITE_CACHE.clear(); }
}