package xfacthd.framedblocks.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class SearchEditBox extends EditBox
{
    private static final long UPDATE_DELAY = 250L;

    private final Consumer<String> searchHandler;
    private boolean changed = false;
    private String lastQuery = "";
    private long lastChange = 0L;

    public SearchEditBox(Font font, int x, int y, int w, int h, Component hint, Consumer<String> searchHandler, @Nullable SearchEditBox prev)
    {
        super(font, x, y, w, h, prev, hint);
        this.searchHandler = searchHandler;
        setHint(hint);
        setResponder(this::onSearchChanged);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn)
    {
        if (btn == 1 && this.clicked(mouseX, mouseY))
        {
            setValue("");
            lastChange = System.currentTimeMillis() - UPDATE_DELAY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, btn);
    }

    private void onSearchChanged(String text)
    {
        changed = true;
        lastQuery = text;
        lastChange = System.currentTimeMillis();
    }

    public void tick()
    {
        if (changed && System.currentTimeMillis() - lastChange > UPDATE_DELAY)
        {
            changed = false;
            searchHandler.accept(lastQuery);
        }
    }

    @Override
    public boolean isBordered()
    {
        return false;
    }

    @Override
    public int getInnerWidth()
    {
        return width - 8;
    }
}
