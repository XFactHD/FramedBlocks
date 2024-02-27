package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;

import java.util.Collection;
import java.util.List;

public final class FramingSawExclusionZoneProvider implements ExclusionZonesProvider<FramingSawWithEncoderScreen>
{
    @Override
    public Collection<Rectangle> provide(FramingSawWithEncoderScreen screen)
    {
        if (screen.getMenu().isInEncoderMode())
        {
            return List.of(new Rectangle(
                    screen.getGuiLeft() + FramingSawWithEncoderScreen.TAB_X,
                    screen.getGuiTop() + FramingSawWithEncoderScreen.TAB_TOP_Y,
                    FramingSawWithEncoderScreen.TAB_WIDTH,
                    FramingSawWithEncoderScreen.TAB_HEIGHT * 2
            ));
        }
        return List.of();
    }
}
