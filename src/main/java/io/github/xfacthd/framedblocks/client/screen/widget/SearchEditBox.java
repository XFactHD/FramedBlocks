package io.github.xfacthd.framedblocks.client.screen.widget;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.xfacthd.framedblocks.common.compat.searchables.SearchablesCompat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class SearchEditBox extends EditBox {
    private static final long UPDATE_DELAY = 250L;
    private static final boolean NO_DELAY = SearchablesCompat.isLoaded();

    private final Consumer<String> searchHandler;
    private boolean changed = false;
    private String lastQuery = "";
    private long lastChange = 0L;

    public SearchEditBox(Font font, int x, int y, int w, int h, Component hint, Consumer<String> searchHandler, @Nullable SearchEditBox prev) {
        super(font, x, y, w, h, prev, hint);
        this.searchHandler = searchHandler;
        setHint(hint);
        setResponder(this::onSearchChanged);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && isMouseOver(event.x(), event.y())) {
            setValue("");
            if (!NO_DELAY) {
                lastChange = System.currentTimeMillis() - UPDATE_DELAY;
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private void onSearchChanged(String text) {
        if (lastQuery.equals(text)) {
            return;
        }

        if (NO_DELAY) {
            searchHandler.accept(text);
        } else {
            changed = true;
            lastChange = System.currentTimeMillis();
        }

        lastQuery = text;
    }

    public void tick() {
        if (!NO_DELAY && changed && System.currentTimeMillis() - lastChange > UPDATE_DELAY) {
            changed = false;
            searchHandler.accept(lastQuery);
        }
    }

    @Override
    public boolean isBordered() {
        return false;
    }

    @Override
    public int getInnerWidth() {
        return width - 8;
    }
}
