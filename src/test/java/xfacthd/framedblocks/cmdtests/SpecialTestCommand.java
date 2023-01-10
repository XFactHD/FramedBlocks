package xfacthd.framedblocks.cmdtests;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.*;
import net.minecraft.network.chat.*;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FileUtils;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.cmdtests.tests.*;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpecialTestCommand
{
    private static final MethodHandle MH_CMD_SRC_SRC = Utils.unreflectField(CommandSourceStack.class, "f_81288_");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu_MM_dd-kk_mm_ss");
    private static final Path EXPORT_DIR = Path.of("./logs/test");

    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("fbtest")
                .then(Commands.literal("skippredicates")
                        .executes(async(SkipPredicates.NAME, SkipPredicates::testSkipPredicates))
                )
                .then(Commands.literal("recipecollision")
                        .executes(async(RecipeCollisions.NAME, RecipeCollisions::checkForRecipeCollisions))
                )
                .then(Commands.literal("recipepresent")
                        .executes(RecipePresent::checkForRecipePresence)
                )
        );
    }

    private static Command<CommandSourceStack> async(String testName, AsyncCommand cmd)
    {
        return ctx ->
        {
            ctx.getSource().sendSuccess(new TextComponent("Starting " + testName + " test"), false);

            CommandSource source = getSource(ctx.getSource());
            Consumer<Component> appender = msg -> ctx.getSource().getServer().submit(() ->
                    source.sendMessage(msg, Util.NIL_UUID)
            );
            runGuardedOffThread(testName, appender, () -> cmd.run(ctx, appender));

            return Command.SINGLE_SUCCESS;
        };
    }

    private static CommandSource getSource(CommandSourceStack source)
    {
        try
        {
            return (CommandSource) MH_CMD_SRC_SRC.invoke(source);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void runGuardedOffThread(String testName, Consumer<Component> msgQueueAppender, ThrowingRunnable test)
    {
        Util.backgroundExecutor().submit(() ->
        {
            try
            {
                test.run();
            }
            catch (Throwable t)
            {
                msgQueueAppender.accept(new TextComponent(
                        "Encountered an uncaught error while testing " + testName + ". See log for details"
                ));
                FramedBlocks.LOGGER.error("Encountered an error while testing {}", testName, t);
            }
        });
    }

    public static Component writeResultToFile(String filePrefix, String data)
    {
        FileUtils.getOrCreateDirectory(EXPORT_DIR, "Test results");
        String dateTime = FORMATTER.format(LocalDateTime.now());
        Path path = EXPORT_DIR.resolve("%s_%s.txt".formatted(filePrefix, dateTime));

        try
        {
            Files.writeString(path, data, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String pathText = path.toAbsolutePath().toString();
            Component pathComponent = new TextComponent(pathText).withStyle(style ->
                    style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, pathText))
                            .applyFormat(ChatFormatting.UNDERLINE)
            );
            return new TextComponent("Tests results exported to ").append(pathComponent);
        }
        catch (IOException e)
        {
            FramedBlocks.LOGGER.error("Encountered an error while exporting test results", e);
            return new TextComponent("Export of test results failed with error: %s: %s".formatted(
                    e.getClass().getSimpleName(), e.getMessage()
            ));
        }
    }

    private interface ThrowingRunnable
    {
        void run() throws CommandSyntaxException;
    }

    public interface AsyncCommand
    {
        void run(CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender) throws CommandSyntaxException;
    }



    private SpecialTestCommand() { }
}
