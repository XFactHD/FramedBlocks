package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.*;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.net.SignUpdatePacket;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.stream.IntStream;

public class FramedSignScreen extends Screen
{
    public static final Component TITLE = Utils.translate("title", "sign.edit");

    private final AbstractFramedSignBlock signBlock;
    private final FramedSignBlockEntity sign;
    private final boolean front;
    private final int textYOffset;
    private SignText text;
    private final String[] lines;
    private int blinkCounter = 0;
    private int currLine = 0;
    private TextFieldHelper inputUtil;

    public FramedSignScreen(FramedSignBlockEntity sign, boolean front)
    {
        super(TITLE);
        this.signBlock = (AbstractFramedSignBlock) sign.getBlockState().getBlock();
        this.sign = sign;
        this.front = front;
        this.text = sign.getText(front);
        boolean filtered = Minecraft.getInstance().isTextFilteringEnabled();
        this.lines = IntStream.range(0, 4)
                .mapToObj(idx -> text.getMessage(idx, filtered))
                .map(Component::getString)
                .toArray(String[]::new);
        this.textYOffset = switch ((BlockType) sign.getBlockType())
        {
            case FRAMED_SIGN -> 67;
            case FRAMED_WALL_SIGN -> 30;
            case FRAMED_HANGING_SIGN, FRAMED_WALL_HANGING_SIGN -> 6;
            default -> throw new IllegalArgumentException("Invalid block type: " + sign.getBlockType());
        };
    }

    @Override
    protected void init()
    {
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> Minecraft.getInstance().setScreen(null))
                .pos(width / 2 - 100, height / 2 + 60)
                .size(200, 20)
                .build()
        );

        //noinspection ConstantConditions
        inputUtil = new TextFieldHelper(
                () -> lines[currLine],
                this::setLine,
                TextFieldHelper.createClipboardGetter(minecraft), TextFieldHelper.createClipboardSetter(minecraft),
                (line) -> minecraft.font.width(line) <= signBlock.getMaxTextLineWidth()
        );
    }

    private void setLine(String line)
    {
        lines[currLine] = line;
        text = text.setMessage(currLine, Component.literal(line));
        sign.setText(text, front);
    }

    @Override
    public void removed()
    {
        FramedBlocks.CHANNEL.sendToServer(new SignUpdatePacket(sign.getBlockPos(), front, lines));
    }

    @Override
    public void tick()
    {
        blinkCounter++;

        if (minecraft != null && minecraft.player != null && !sign.isRemoved() && sign.isTooFarAwayToEdit(minecraft.player))
        {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
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
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        Lighting.setupForEntityInInventory();

        //noinspection ConstantConditions
        graphics.drawCenteredString(font, title, width / 2, 40, ChatFormatting.WHITE.getColor());

        drawSignBlock(graphics);

        drawText(graphics);

        Lighting.setupFor3DItems();
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void drawSignBlock(GuiGraphics graphics)
    {
        BlockState state = sign.getBlockState();

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(width / 2F, height / 2F, 100);
        poseStack.mulPose(Axis.YN.rotationDegrees(signBlock.getYRotationDegrees(state)));
        poseStack.mulPose(Quaternions.ZP_180);
        poseStack.scale(112, 112, 112);
        poseStack.translate(-.5, -.25, -.5);

        //noinspection ConstantConditions
        BlockRenderDispatcher renderer = minecraft.getBlockRenderer();
        MultiBufferSource.BufferSource buffer = graphics.bufferSource();
        PoseStack.Pose pose = poseStack.last();
        BakedModel model = renderer.getBlockModel(state);
        ModelData modelData = sign.getModelData();

        int color = minecraft.getBlockColors().getColor(state, minecraft.level, sign.getBlockPos(), 0);
        float red = FastColor.ARGB32.red(color) / 255F;
        float green = FastColor.ARGB32.green(color) / 255F;
        float blue = FastColor.ARGB32.blue(color) / 255F;

        for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(42), modelData))
        {
            VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false));
            renderer.getModelRenderer().renderModel(
                    pose,
                    consumer,
                    state,
                    model,
                    red, green, blue,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    modelData,
                    renderType
            );
        }

        buffer.endBatch();
        poseStack.popPose();
    }

    private void drawText(GuiGraphics graphics)
    {
        graphics.pose().pushPose();
        graphics.pose().translate(width / 2D, height / 2D - textYOffset, 110);
        graphics.pose().scale(1.2F, 1.2F, 1F);

        //noinspection ConstantConditions
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();

        drawLines(graphics.pose().last().pose(), buffer, lines);
        drawCursor(graphics, buffer, lines);

        graphics.pose().popPose();
    }

    private void drawLines(Matrix4f matrix, MultiBufferSource.BufferSource buffer, String[] lines)
    {
        int color = text.getColor().getTextColor();

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
        int color = text.getColor().getTextColor();
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
}