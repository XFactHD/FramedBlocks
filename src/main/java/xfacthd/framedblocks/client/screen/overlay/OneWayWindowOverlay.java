package xfacthd.framedblocks.client.screen.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.cube.FramedOneWayWindowBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

import java.util.*;

public final class OneWayWindowOverlay extends BlockInteractOverlay
{
    private static final ResourceLocation SYMBOL_TEXTURE = Utils.rl("textures/overlay/one_way_window_symbols.png");
    private static final ResourceLocation EYE_TEXTURE = Utils.rl("minecraft", "textures/item/ender_eye.png");
    private static final Texture TEXTURE_BG = new Texture(SYMBOL_TEXTURE, 0, 0, 22, 38, 37, 38);
    private static final Texture TEXTURE_CROSS = new Texture(SYMBOL_TEXTURE, 22, 0, 15, 15, 37, 38);
    private static final Texture TEXTURE_EYE = new Texture(EYE_TEXTURE, 0, 0, 16, 16, 16, 16);

    public static final String LINE_CURR_FACE = Utils.translationKey("tooltip", "one_way_window.curr_face");
    public static final String LINE_SET_FACE = Utils.translationKey("tooltip", "one_way_window.set_face");
    public static final Component LINE_CLEAR_FACE = Utils.translate("tooltip", "one_way_window.clear_face");
    public static final Component[] DIR_VALUE_LINES = Utils.buildEnumTranslations("tooltip", "one_way_window.dir", Direction.values(), ChatFormatting.GOLD);
    public static final Component[] FACE_VALUE_LINES = Utils.buildEnumTranslations("tooltip", "one_way_window.face", NullableDirection.values(), ChatFormatting.GOLD);
    public static final Component[] FACE_VALUE_ABBRS = Utils.buildEnumTranslations("tooltip", "one_way_window.face_abbr", NullableDirection.values());
    private static final Component[] CURR_FACE_LINES = Utils.bindEnumTranslation(LINE_CURR_FACE, NullableDirection.values(), FACE_VALUE_LINES);
    private static final Component[] SET_FACE_LINES = Utils.bindEnumTranslation(LINE_SET_FACE, Direction.values(), DIR_VALUE_LINES);
    private static final List<Component> LINES = packLineList();

    public OneWayWindowOverlay()
    {
        super("one_way_window", LINES, List.of(), null, null, ClientConfig.VIEW::getOneWayWindowMode);
    }

    @Override
    protected boolean isValidTool(ItemStack stack)
    {
        return stack.is(FBContent.ITEM_FRAMED_WRENCH.value());
    }

    @Override
    protected boolean isValidTarget(Target target)
    {
        if (target.state().getBlock() != FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.value())
        {
            return false;
        }

        return FramedOneWayWindowBlock.isOwnedBy(level(), target.pos(), player());
    }

    @Override
    protected boolean getState(Target target)
    {
        return false;
    }

    @Override
    protected Texture getTexture(Target target, boolean state, Texture texFalse, Texture texTrue)
    {
        return TEXTURE_BG;
    }

    @Override
    protected List<Component> getLines(Target target, boolean state, List<Component> linesFalse, List<Component> linesTrue)
    {
        NullableDirection face = target.state().getValue(PropertyHolder.NULLABLE_FACE);

        return List.of(
                CURR_FACE_LINES[face.ordinal()],
                SET_FACE_LINES[target.side().ordinal()],
                LINE_CLEAR_FACE
        );
    }

    @Override
    protected void renderAfterIcon(GuiGraphics graphics, Texture tex, int texX, int texY, Target target)
    {
        NullableDirection face = target.state().getValue(PropertyHolder.NULLABLE_FACE);

        graphics.pose().pushPose();
        graphics.pose().translate(-.5, -.5, 0);

        TEXTURE_EYE.draw(graphics, texX + 3, texY + 3);
        if (face == NullableDirection.NONE)
        {
            TEXTURE_CROSS.draw(graphics, texX + 4, texY + 4);
        }

        graphics.pose().popPose();

        Font font = Minecraft.getInstance().font;
        int x = texX + (tex.width() / 2);
        int y = texY + (tex.height() * 3 / 4) - (font.lineHeight / 2);
        graphics.drawCenteredString(font, FACE_VALUE_ABBRS[face.ordinal()], x, y, -1/*0x555555*/);
    }



    private static List<Component> packLineList()
    {
        List<Component> lines = new ArrayList<>();

        lines.add(LINE_CLEAR_FACE);
        lines.addAll(Arrays.asList(CURR_FACE_LINES));
        lines.addAll(Arrays.asList(SET_FACE_LINES));

        return lines;
    }
}
