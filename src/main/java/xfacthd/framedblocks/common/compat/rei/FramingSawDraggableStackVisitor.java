package xfacthd.framedblocks.common.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import xfacthd.framedblocks.client.screen.FramingSawWithEncoderScreen;
import xfacthd.framedblocks.common.crafting.*;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.List;
import java.util.stream.Stream;

public final class FramingSawDraggableStackVisitor implements DraggableStackVisitor<FramingSawWithEncoderScreen>
{
    @Override
    public <R extends Screen> boolean isHandingScreen(R screen)
    {
        return screen instanceof FramingSawWithEncoderScreen encoder && encoder.getMenu().isInEncoderMode();
    }

    @Override
    public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<FramingSawWithEncoderScreen> context, DraggableStack draggableStack)
    {
        if (draggableStack.getStack().getValue() instanceof ItemStack stack)
        {
            FramingSawWithEncoderScreen screen = context.getScreen();

            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0)
            {
                return Stream.of(BoundsProvider.ofRectangle(new Rectangle(
                        screen.getInputSlotX() - 1, screen.getInputSlotY(FramingSawMenu.SLOT_INPUT) - 1, 18, 18
                )));
            }

            RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getRecipes().get(screen.getMenu().getSelectedRecipeIndex()).toVanilla();
            List<FramingSawRecipeAdditive> additives = recipe.value().getAdditives();
            for (int i = 0; i < additives.size(); i++)
            {
                if (additives.get(i).ingredient().test(stack))
                {
                    return Stream.of(BoundsProvider.ofRectangle(new Rectangle(
                            screen.getInputSlotX() - 1, screen.getInputSlotY(i + 1) - 1, 18, 18
                    )));
                }
            }
        }
        return Stream.empty();
    }

    @Override
    public DraggedAcceptorResult acceptDraggedStack(DraggingContext<FramingSawWithEncoderScreen> context, DraggableStack draggableStack)
    {
        Point pos = context.getCurrentPosition();
        if (pos != null && draggableStack.getStack().getValue() instanceof ItemStack stack)
        {
            FramingSawWithEncoderScreen screen = context.getScreen();
            if (FramingSawRecipeCache.get(true).getMaterialValue(stack.getItem()) > 0)
            {
                int sx = screen.getInputSlotX();
                int sy = screen.getInputSlotY(FramingSawMenu.SLOT_INPUT);
                if (pos.x >= sx && pos.x < sx + 18 && pos.y >= sy && pos.y < sy + 18)
                {
                    screen.acceptEncodingInput(FramingSawMenu.SLOT_INPUT, stack);
                    return DraggedAcceptorResult.ACCEPTED;
                }
            }

            RecipeHolder<FramingSawRecipe> recipe = screen.getMenu().getRecipes().get(screen.getMenu().getSelectedRecipeIndex()).toVanilla();
            List<FramingSawRecipeAdditive> additives = recipe.value().getAdditives();
            for (int i = 0; i < additives.size(); i++)
            {
                int sx = screen.getInputSlotX();
                int sy = screen.getInputSlotY(i + 1);
                if (pos.x >= sx && pos.x < sx + 18 && pos.y >= sy && pos.y < sy + 18)
                {
                    if (additives.get(i).ingredient().test(stack))
                    {
                        screen.acceptEncodingInput(i + 1, stack);
                        return DraggedAcceptorResult.ACCEPTED;
                    }
                }
            }
        }
        return DraggedAcceptorResult.PASS;
    }
}
