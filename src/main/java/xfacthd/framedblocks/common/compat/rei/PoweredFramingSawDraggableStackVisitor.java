package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.client.screen.PoweredFramingSawScreen;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

import java.util.stream.Stream;

public final class PoweredFramingSawDraggableStackVisitor implements DraggableStackVisitor<PoweredFramingSawScreen>
{
    @Override
    public <R extends Screen> boolean isHandingScreen(R screen)
    {
        return screen instanceof PoweredFramingSawScreen;
    }

    @Override
    public Stream<BoundsProvider> getDraggableAcceptingBounds(
            DraggingContext<PoweredFramingSawScreen> context, DraggableStack draggableStack
    )
    {
        if (draggableStack.getStack().getValue() instanceof ItemStack stack)
        {
            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0)
            {
                PoweredFramingSawScreen screen = context.getScreen();
                return Stream.of(BoundsProvider.ofRectangle(new Rectangle(
                        screen.getTargetStackX() - 1, screen.getTargetStackY() - 1, 18, 18
                )));
            }
        }
        return Stream.empty();
    }

    @Override
    public DraggedAcceptorResult acceptDraggedStack(
            DraggingContext<PoweredFramingSawScreen> context, DraggableStack draggableStack
    )
    {
        PoweredFramingSawScreen screen = context.getScreen();
        int sx = screen.getTargetStackX();
        int sy = screen.getTargetStackY();
        Point pos = context.getCurrentPosition();
        if (pos == null || (pos.x < sx || pos.x >= sx + 18 || pos.y < sy || pos.y >= sy + 18))
        {
            return DraggedAcceptorResult.PASS;
        }

        if (draggableStack.getStack().getValue() instanceof ItemStack stack)
        {
            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0)
            {
                context.getScreen().selectRecipe(stack);
                return DraggedAcceptorResult.ACCEPTED;
            }
        }
        return DraggedAcceptorResult.PASS;
    }
}
