package xfacthd.framedblocks.common.datagen.builders.book;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonArray;
import net.minecraft.data.*;
import xfacthd.framedblocks.common.datagen.builders.book.chapter.ChapterBuilder;
import xfacthd.framedblocks.common.datagen.builders.book.chapter.SectionBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class GuidebookProvider implements DataProvider //TODO: library and template builders
{
    private final PackOutput output;
    private final String modid;
    private final List<GuidebookBuilder> books = new ArrayList<>();

    protected GuidebookProvider(PackOutput output, String modid)
    {
        this.output = output;
        this.modid = modid;
    }

    protected abstract void addBooks();

    protected final GuidebookBuilder book(String name)
    {
        GuidebookBuilder builder = new GuidebookBuilder(name);
        books.add(builder);
        return builder;
    }

    protected static ChapterBuilder chapter(String id)
    {
        return new ChapterBuilder(id);
    }

    protected static SectionBuilder section(String id)
    {
        return new SectionBuilder("section", id);
    }

    protected static SectionBuilder page(String id)
    {
        return new SectionBuilder("page", id);
    }

    protected static String sectionRef(String chapter, String section)
    {
        return chapter + ":" + section;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput cachedOutput)
    {
        addBooks();
        if (!books.isEmpty())
        {
            Path packPath = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                    .resolve(modid);

            books.forEach(GuidebookBuilder::validate);

            List<CompletableFuture<?>> futures = books.stream().map(book ->
            {
                Path path = packPath.resolve(makeBookPath(book));
                return saveBook(cachedOutput, book, path);
            }).collect(Collectors.toCollection(ArrayList::new));
            futures.add(saveBookJson(cachedOutput, packPath));
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        }
        return CompletableFuture.allOf();
    }

    @SuppressWarnings({ "UnstableApiUsage", "deprecation" })
    private static CompletableFuture<?> saveBook(CachedOutput cachedOutput, GuidebookBuilder book, Path path)
    {
        return CompletableFuture.runAsync(() ->
        {
            try
            {
                ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                HashingOutputStream hashOutStream = new HashingOutputStream(Hashing.sha1(), byteOutStream);
                book.print(hashOutStream);
                cachedOutput.writeIfNeeded(path, byteOutStream.toByteArray(), hashOutStream.hash());
            }
            catch (IOException e)
            {
                LOGGER.error("Failed to save file to {}", path, e);
            }
        });
    }

    private CompletableFuture<?> saveBookJson(CachedOutput cachedOutput, Path path)
    {
        JsonArray bookArray = new JsonArray();
        books.forEach(book -> bookArray.add(modid + ":" + makeBookPath(book)));
        return DataProvider.saveStable(cachedOutput, bookArray, path.resolve("books.json"));
    }

    private static String makeBookPath(GuidebookBuilder book)
    {
        return "books/" + book.getName() + ".xml";
    }

    @Override
    public String getName()
    {
        return "Guidebooks";
    }
}
