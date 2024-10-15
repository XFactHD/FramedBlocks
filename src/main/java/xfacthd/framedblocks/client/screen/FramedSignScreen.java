package xfacthd.framedblocks.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.common.block.sign.AbstractFramedHangingSignBlock;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.net.payload.ServerboundSignUpdatePayload;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.util.Arrays;
import java.util.stream.IntStream;

public class FramedSignScreen extends Screen
{
    private static final Component TITLE_NORMAL = Component.translatable("sign.edit");
    private static final Component TITLE_HANGING = Component.translatable("hanging_sign.edit");
    private static final SignConfig CFG_STANDING = new SignConfig(90F, 56F, 95F, 0F, 1F);
    private static final SignConfig CFG_WALL = new SignConfig(90F, 56F, 95F, 30F, 1F);
    private static final SignConfig CFG_HANGING = new SignConfig(100F, 30F, 75F, 26F, 1F);

    private final AbstractFramedSignBlock signBlock;
    private final FramedSignBlockEntity sign;
    private final boolean front;
    private final SignConfig signConfig;
    private SignText text;
    private final String[] lines;
    private int blinkCounter = 0;
    private int currLine = 0;
    private TextFieldHelper inputUtil;

    public FramedSignScreen(FramedSignBlockEntity sign, boolean front)
    {
        super(getTitle(sign));
        this.signBlock = (AbstractFramedSignBlock) sign.getBlockState().getBlock();
        this.sign = sign;
        this.front = front;
        this.text = sign.getText(front);
        boolean filtered = Minecraft.getInstance().isTextFilteringEnabled();
        this.lines = IntStream.range(0, 4)
                .mapToObj(idx -> text.getMessage(idx, filtered))
                .map(Component::getString)
                .toArray(String[]::new);
        this.signConfig = switch ((BlockType) sign.getBlockType())
        {
            case FRAMED_SIGN -> CFG_STANDING;
            case FRAMED_WALL_SIGN -> CFG_WALL;
            case FRAMED_HANGING_SIGN, FRAMED_WALL_HANGING_SIGN -> CFG_HANGING;
            default -> throw new IllegalArgumentException("Invalid block type: " + sign.getBlockType());
        };
    }

    private static Component getTitle(FramedSignBlockEntity be)
    {
        boolean hanging = be.getBlockState().getBlock() instanceof AbstractFramedHangingSignBlock;
        return hanging ? TITLE_HANGING : TITLE_NORMAL;
    }

    @Override
    protected void init()
    {
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> Minecraft.getInstance().setScreen(null))
                .pos(width / 2 - 100, height / 4 + 144)
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
        PacketDistributor.sendToServer(new ServerboundSignUpdatePayload(sign.getBlockPos(), front, Arrays.copyOf(lines, lines.length)));
    }

    @Override
    public void tick()
    {
        blinkCounter++;

        if (minecraft != null && minecraft.player != null && (sign.isRemoved() || sign.isTooFarAwayToEdit(minecraft.player)))
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);

        Lighting.setupForFlatItems();
        graphics.drawCenteredString(font, title, width / 2, 40, 0xFFFFFF);
        drawSign(graphics);
        Lighting.setupFor3DItems();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderTransparentBackground(graphics);
    }

    @SuppressWarnings("deprecation")
    private void drawSign(GuiGraphics graphics)
    {
        graphics.pose().pushPose();
        graphics.pose().translate(width / 2F, signConfig.baseYOff, 150F);
        graphics.pose().pushPose();
        Lighting.setupLevel();
        RenderSystem.runAsFancy(() -> drawSignBlock(graphics));
        Lighting.setupForFlatItems();
        graphics.pose().popPose();
        drawText(graphics);
        graphics.pose().popPose();
    }

    private void drawSignBlock(GuiGraphics graphics)
    {
        BlockState state = sign.getBlockState();

        PoseStack poseStack = graphics.pose();
        poseStack.translate(0, signConfig.addYOff, 0);
        poseStack.mulPose(Axis.YN.rotationDegrees(signBlock.getYRotationDegrees(state)));
        poseStack.mulPose(Quaternions.ZP_180);
        poseStack.scale(signConfig.signScale, signConfig.signScale, signConfig.signScale);
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
    }

    private void drawText(GuiGraphics graphics)
    {
        graphics.pose().translate(0F, signConfig.textYOff, 0F);

        //noinspection ConstantConditions
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();

        drawLines(graphics.pose().last().pose(), buffer, lines);
        drawCursor(graphics, buffer, lines);
    }

    private void drawLines(Matrix4f matrix, MultiBufferSource.BufferSource buffer, String[] lines)
    {
        int color = text.getColor().getTextColor();

        for (int line = 0; line < lines.length; line++)
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

        for (int i = 0; i < lines.length; ++i)
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

                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);

                    BufferBuilder tessBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    tessBuffer.addVertex(matrix, xStart, y + 9F, 0.0F).setColor(0, 0, 255, 255);
                    tessBuffer.addVertex(matrix,   xEnd, y + 9F, 0.0F).setColor(0, 0, 255, 255);
                    tessBuffer.addVertex(matrix,   xEnd, y - 1F, 0.0F).setColor(0, 0, 255, 255);
                    tessBuffer.addVertex(matrix, xStart, y - 1F, 0.0F).setColor(0, 0, 255, 255);
                    BufferUploader.drawWithShader(tessBuffer.buildOrThrow());

                    RenderSystem.disableColorLogicOp();
                }
            }
        }
    }

    private record SignConfig(float baseYOff, float addYOff, float signScale, float textYOff, float textScale) { }
}
