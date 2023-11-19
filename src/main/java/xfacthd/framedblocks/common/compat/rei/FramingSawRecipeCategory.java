package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import xfacthd.framedblocks.client.screen.FramingSawScreen;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.special.FramingSawBlock;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

import java.util.ArrayList;
import java.util.List;

public final class FramingSawRecipeCategory implements DisplayCategory<FramingSawDisplay>
{
    public static final CategoryIdentifier<FramingSawDisplay> SAW_CATEGORY = CategoryIdentifier.of(ReiCompat.SAW_ID);

    private static final int WIDTH = 124;
    private static final int HEIGHT = 50;
    private static final int ARROW_X = 64;
    private static final int ARROW_Y = 15;
    private static final int INPUT_X = 23;
    private static final int INPUT_Y = 5;
    private static final int ADDITIVE_X = 5;
    private static final int ADDITIVE_Y = 29;
    private static final int ADDITIVE_DX = 18;
    private static final int OUTPUT_TEX_X = 99;
    private static final int OUTPUT_TEX_Y = 16;
    private static final int OUTPUT_SLOT_X = OUTPUT_TEX_X;
    private static final int OUTPUT_SLOT_Y = OUTPUT_TEX_Y;
    private static final int WARNING_X = 41;
    private static final int WARNING_Y = 7;
    private static final int WARNING_SIZE = 16;
    private static final float WARNING_SCALE = .75F;
    private static final int WARNING_DRAW_SIZE = (int) (WARNING_SIZE * WARNING_SCALE);

    private final Renderer icon = EntryStacks.of(FBContent.BLOCK_FRAMING_SAW.value());

    @Override
    public CategoryIdentifier<FramingSawDisplay> getCategoryIdentifier()
    {
        return SAW_CATEGORY;
    }

    @Override
    public Component getTitle()
    {
        return FramingSawBlock.SAW_MENU_TITLE;
    }

    @Override
    public Renderer getIcon()
    {
        return icon;
    }

    @Override
    public int getDisplayWidth(FramingSawDisplay display)
    {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight()
    {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(FramingSawDisplay display, Rectangle bounds)
    {
        Point topLeft = bounds.getLocation();
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(topLeft.x + ARROW_X, topLeft.y + ARROW_Y)));

        widgets.add(Widgets.createSlot(new Point(topLeft.x + INPUT_X, topLeft.y + INPUT_Y))
                .entries(display.getInputEntries().get(0))
                .markInput()
        );

        for (int i = 0; i < FramingSawRecipe.MAX_ADDITIVE_COUNT; i++)
        {
            int x = ADDITIVE_X + i * ADDITIVE_DX;
            Slot slot = Widgets.createSlot(new Point(topLeft.x + x, topLeft.y + ADDITIVE_Y)).markInput();
            int idx = i + 1;
            if (idx < display.getInputEntries().size())
            {
                slot.entries(display.getInputEntries().get(idx));
            }
            widgets.add(slot);
        }

        widgets.add(Widgets.createResultSlotBackground(new Point(topLeft.x + OUTPUT_TEX_X, topLeft.y + OUTPUT_TEX_Y)));
        widgets.add(Widgets.createSlot(new Point(topLeft.x + OUTPUT_SLOT_X, topLeft.y + OUTPUT_SLOT_Y))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput()
        );

        if (display.hasInputWithAdditives())
        {
            widgets.add(Widgets.createTexturedWidget(
                    FramingSawScreen.WARNING_ICON,
                    topLeft.x + WARNING_X, topLeft.y + WARNING_Y,
                    8, 8,
                    WARNING_DRAW_SIZE, WARNING_DRAW_SIZE,
                    WARNING_SIZE, WARNING_SIZE,
                    32, 32
            ));
            widgets.add(Widgets.createTooltip(
                    new Rectangle(topLeft.x + WARNING_X, topLeft.y + WARNING_Y, WARNING_DRAW_SIZE, WARNING_DRAW_SIZE),
                    FramingSawScreen.TOOLTIP_LOOSE_ADDITIVE
            ));
        }

        return widgets;
    }
}
