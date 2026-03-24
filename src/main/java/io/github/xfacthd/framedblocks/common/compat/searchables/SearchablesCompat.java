package io.github.xfacthd.framedblocks.common.compat.searchables;

import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;
import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.menu.FramingSawMenu;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SearchablesCompat
{
    private static boolean loaded = false;

    public static void init()
    {
        if (ModList.get().isLoaded("searchables"))
        {
            try
            {
                if (Utils.CLIENT_DIST)
                {
                    GuardedAccess.init();
                    loaded = true;
                }
            }
            catch (Throwable t)
            {
                FramedBlocks.LOGGER.warn("An error occured while initializing Searchables integration!", t);
            }
        }
    }

    public static boolean isLoaded()
    {
        return loaded;
    }

    public static Consumer<String> createSearchHandler(ValueSupplier valueSupplier, ResultConsumer resultConsumer, Consumer<String> defaultHandler)
    {
        if (loaded)
        {
            return GuardedAccess.createSearchHandler(valueSupplier, resultConsumer);
        }
        else
        {
            return defaultHandler;
        }
    }

    @FunctionalInterface
    public interface ValueSupplier extends Supplier<List<FramingSawMenu.FramedRecipeHolder>> { }

    @FunctionalInterface
    public interface ResultConsumer extends Consumer<List<FramingSawMenu.FramedRecipeHolder>> { }

    private static final class GuardedAccess
    {
        private static final SearchableType<FramingSawMenu.FramedRecipeHolder> SEARCH_TYPE = new SearchableType.Builder<FramingSawMenu.FramedRecipeHolder>()
                .defaultComponent(SearchableComponent.create(
                        "result",
                        holder -> Optional.of(holder.getRecipe().getResultStack().getItemName().getString())
                ))
                .build();

        public static void init() { }

        public static Consumer<String> createSearchHandler(ValueSupplier valueSupplier, ResultConsumer resultConsumer)
        {
            return value -> resultConsumer.accept(SEARCH_TYPE.filterEntries(valueSupplier.get(), value));
        }
    }

    private SearchablesCompat() { }
}
