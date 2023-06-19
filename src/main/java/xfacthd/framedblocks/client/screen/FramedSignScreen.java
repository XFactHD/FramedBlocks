package xfacthd.framedblocks.client.screen;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.net.SignUpdatePacket;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.List;

@SuppressWarnings("deprecation")
public class FramedSignScreen extends Screen // FIXME: update to match vanilla sign screen
{
    private static final Table<BlockState, Direction, TextureAtlasSprite> SPRITE_CACHE = HashBasedTable.create();
    private static final ResourceLocation DEFAULT_TEXTURE = Utils.rl("block/framed_block");
    public static final Component TITLE = Utils.translate("title", "sign.edit");
    public static final Component DONE = Utils.translate("button", "gui.done");
    private static final int TEX_W = 128;
    private static final int TEX_H = 64;
    private static final int TEX_MAX_V = 8;

    private final FramedSignBlockEntity sign;
    private final String[] lines = new String[4];
    private int blinkCounter = 0;
    private int currLine = 0;
    private TextFieldHelper inputUtil;
    private int texX;
    private int texY;

    public FramedSignScreen(FramedSignBlockEntity sign)
    {
        super(TITLE);
        this.sign = sign;

        for (int i = 0; i < 4; i++) { lines[i] = sign.getLine(i).getString(); }
    }

    @Override
    protected void init()
    {
        addRenderableWidget(Button.builder(DONE, btn -> onClose())
                .pos(width / 2 - 100, height / 2 + 60)
                .size(200, 20)
                .build()
        );

        //noinspection ConstantConditions
        inputUtil = new TextFieldHelper(
                () -> lines[currLine],
                (line) ->
                {
                    lines[currLine] = line;
                    sign.setLine(currLine, Component.literal(line));
                },
                TextFieldHelper.createClipboardGetter(minecraft), TextFieldHelper.createClipboardSetter(minecraft),
                (line) -> minecraft.font.width(line) <= 90
        );

        texX = width / 2 - TEX_W / 2;
        texY = height / 2 - TEX_H / 2 - 20;
    }

    @Override
    public void removed()
    {
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

        if (!sign.getType().isValid(sign.getBlockState()))
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        Lighting.setupForFlatItems();

        renderBackground(graphics);
        //noinspection ConstantConditions
        graphics.drawCenteredString(font, title, width / 2, 40, ChatFormatting.WHITE.getColor());

        TextureAtlasSprite sprite = getFrontSprite();
        graphics.innerBlit(
                sprite.atlasLocation(),
                texX, texX + TEX_W, texY, texY + TEX_H, 0,
                sprite.getU0(),
                sprite.getU1(),
                sprite.getV0(),
                sprite.getV(TEX_MAX_V)
        );

        graphics.pose().pushPose();
        graphics.pose().translate(width / 2D, height / 2D - 20, 0);
        graphics.pose().scale(1.2F, 1.2F, 1F);

        //noinspection ConstantConditions
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();

        drawLines(graphics.pose().last().pose(), buffer, lines);
        drawCursor(graphics, buffer, lines);

        graphics.pose().popPose();

        Lighting.setupFor3DItems();
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void drawLines(Matrix4f matrix, MultiBufferSource.BufferSource buffer, String[] lines)
    {
        int color = sign.getTextColor().getTextColor();

        for(int line = 0; line < lines.length; line++)
        {
            String text = lines[line];
            if (text != null)
            {
                if (font.isBidirectional()) { text = font.bidirectionalShaping(text); }

                float textX = -font.width(text) / 2F;
                font.drawInBatch(text, textX, line * 10 - 20, color, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
            }
        }

        buffer.endBatch();
    }

    private void drawCursor(GuiGraphics graphics, MultiBufferSource.BufferSource buffer, String[] lines)
    {
        Matrix4f matrix = graphics.pose().last().pose();
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
                        graphics.fill(cursorX, y - 1, cursorX + 1, y + 9, 0xFF000000 | color);
                    }
                    else
                    {
                        font.drawInBatch("_", cursorX, y, color, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
                        buffer.endBatch();
                    }
                }

                if (inputUtil.getSelectionPos() != inputUtil.getCursorPos())
                {
                    int x1 = (font.width(line.substring(0, inputUtil.getSelectionPos())) - hw) * dir;
                    int x2 =   (font.width(line.substring(0, inputUtil.getCursorPos()  )) - hw) * dir;
                    int xStart = Math.min(x1, x2);
                    int xEnd = Math.max(x1, x2);

                    Tesselator tessellator = Tesselator.getInstance();
                    BufferBuilder tessBuffer = tessellator.getBuilder();

                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);

                    tessBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    tessBuffer.vertex(matrix, xStart, y + 9F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.vertex(matrix,   xEnd, y + 9F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.vertex(matrix,   xEnd, y - 1F, 0.0F).color(0, 0, 255, 255).endVertex();
                    tessBuffer.vertex(matrix, xStart, y - 1F, 0.0F).color(0, 0, 255, 255).endVertex();
                    BufferUploader.drawWithShader(tessBuffer.end());

                    RenderSystem.disableColorLogicOp();
                }
            }
        }
    }



    @SuppressWarnings("ConstantConditions")
    private TextureAtlasSprite getFrontSprite()
    {
        Direction front;

        BlockState state = sign.getBlockState();
        if (state.getBlock() == FBContent.BLOCK_FRAMED_WALL_SIGN.get())
        {
            front = state.getValue(FramedProperties.FACING_HOR);
        }
        else
        {
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            double angle = rot * 360D / 16D;
            front = Direction.fromYRot(angle);
        }

        BlockState camoState = sign.getCamo().getState();
        if (camoState.isAir())
        {
            camoState = FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
        }

        if (!SPRITE_CACHE.contains(camoState, front))
        {
            BakedModel model = minecraft.getBlockRenderer().getBlockModel(camoState);
            ChunkRenderTypeSet layers = model.getRenderTypes(camoState, minecraft.level.getRandom(), ModelData.EMPTY);
            List<BakedQuad> quads = model.getQuads(camoState, front, minecraft.level.getRandom(), ModelData.EMPTY, layers.iterator().next());

            TextureAtlasSprite sprite;
            if (!quads.isEmpty())
            {
                sprite = quads.get(0).getSprite();
            }
            else
            {
                sprite = minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(DEFAULT_TEXTURE);
            }

            SPRITE_CACHE.put(camoState, front, sprite);
        }

        return SPRITE_CACHE.get(camoState, front);
    }

    public static void onTextureStitch(final TextureStitchEvent.Post event)
    {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
        {
            SPRITE_CACHE.clear();
        }
    }
}