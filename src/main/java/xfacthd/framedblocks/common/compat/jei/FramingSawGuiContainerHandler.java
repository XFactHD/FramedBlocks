package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;
import xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;

import java.util.List;

public final class FramingSawGuiContainerHandler implements IGuiContainerHandler<FramingSawWithEncoderScreen>
{
    @Override
    public List<Rect2i> getGuiExtraAreas(FramingSawWithEncoderScreen screen)
    {
        if (screen.getMenu().isInEncoderMode())
        {
            return List.of(new Rect2i(
                    screen.getGuiLeft() + FramingSawWithEncoderScreen.TAB_X,
                    screen.getGuiTop() + FramingSawWithEncoderScreen.TAB_TOP_Y,
                    FramingSawWithEncoderScreen.TAB_WIDTH,
                    FramingSawWithEncoderScreen.TAB_HEIGHT * 2
            ));
        }
        return List.of();
    }
}
