package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

// Chunk data size test (1.19.3)
// -----------------------------
//
// Blocks per chunk without bottom bedrock (-63 to 319): 16*16*383 = 98048
//
// Test with Framed Cubes filled with one polished granite:
// +------------------------+-----------+----------+----------+----------+
// | Sync approach:         | State NBT | State ID | State ID | State ID |
// |                        | Type RL   | Type RL  | Type ID  | Type ID  |
// |                        | Def BE    | Def BE   | Def BE   | Comp BE  |
// +------------------------+-----------+----------+----------+----------+
// | Pkt max size (bytes):  |   8388608 |  8388608 |  8388608 |  8388608 |
// | Pkt real size (bytes): |  12400112 |  9262576 |  7741992 |  4752369 |
// +------------------------+-----------+----------+----------+----------+
// | Bytes per block:       |  ~126 B/b |  ~94 B/b |  ~79 B/b |  ~48 B/b |
// +------------------------+-----------+----------+----------+----------+
// | Max % of chunk filled: |      ~68% |     ~90% |    ~108% |    ~176% |
// | Max blocks per chunk:  |     66328 |    88796 |   106237 |   173068 |
// +------------------------+-----------+----------+----------+----------+
//
// Test with Framed Double Slabs filled with one polished granite and one polished diorite:
// +------------------------+-----------+----------+----------+----------+
// | Sync approach:         | State NBT | State ID | State ID | State ID |
// |                        | Type RL   | Type RL  | Type ID  | Type ID  |
// |                        | Def BE    | Def BE   | Def BE   | Comp BE  |
// +------------------------+-----------+----------+----------+----------+
// | Pkt max size (bytes):  |   8388608 |  8388608 |  8388608 |  8388608 |
// | Pkt real size (bytes): |  20562678 | 14334842 | 10474387 |  8231321 |
// +------------------------+-----------+----------+----------+----------+
// | Bytes per block:       |  ~210 B/b | ~146 B/b | ~107 B/b |  ~84 B/b |
// +------------------------+-----------+----------+----------+----------+
// | Max % of chunk filled: |      ~41% |     ~58% |      80% |    ~102% |
// | Max blocks per chunk:  |     39998 |    57376 |    78523 |    99921 |
// +------------------------+-----------+----------+----------+----------+

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChunkBanTest
{
    private static final String CONFIRMATION_KEY = "confirm";
    private static final Component MSG_NO_CONFIRM = Component.literal("Incorrect confirmation key, expected '" + CONFIRMATION_KEY + "'");
    private static final Component MSG_NOT_A_PLAYER = Component.literal("This command can only be executed by a real player");
    private static final Component MSG_ALREADY_RUNNING = Component.literal("Chunkban test preparation is already running");
    private static final Supplier<CamoContainer> CAMO_ONE_FACTORY = () ->
    {
        ItemStack stack = new ItemStack(Items.POLISHED_GRANITE);
        CamoContainer container = FBContent.FACTORY_BLOCK.get().fromItem(stack);
        Preconditions.checkState(!container.isEmpty(), "Container is empty?!");
        return container;
    };
    private static final Supplier<CamoContainer> CAMO_TWO_FACTORY = () ->
    {
        ItemStack stack = new ItemStack(Items.POLISHED_DIORITE);
        CamoContainer container = FBContent.FACTORY_BLOCK.get().fromItem(stack);
        Preconditions.checkState(!container.isEmpty(), "Container is empty?!");
        return container;
    };

    private static Consumer<Component> resultMsgConsumer = null;
    private static ResourceKey<Level> dimension = null;
    private static BlockState state = null;
    private static BlockPos startPos = null;
    private static BlockPos placePos = null;
    private static int blocksPlaced = 0;

    public static int startChunkBanTest(CommandContext<CommandSourceStack> ctx, boolean withState)
    {
        String confirmation = ctx.getArgument("confirm", String.class);
        if (!confirmation.equals(CONFIRMATION_KEY))
        {
            ctx.getSource().sendFailure(MSG_NO_CONFIRM);
            return 0;
        }

        CommandSource source = ctx.getSource().source;
        if (!(source instanceof ServerPlayer player) || source instanceof FakePlayer)
        {
            ctx.getSource().sendFailure(MSG_NOT_A_PLAYER);
            return 0;
        }

        if (dimension != null)
        {
            ctx.getSource().sendFailure(MSG_ALREADY_RUNNING);
            return 0;
        }

        if (withState)
        {
            state = BlockStateArgument.getBlock(ctx, "state").getState();
        }
        else
        {
            state = FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get().defaultBlockState();
        }

        ChunkPos chunk = new ChunkPos(new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ()));
        int minY = player.level().getMinBuildHeight();
        startPos = placePos = SectionPos.of(chunk, SectionPos.blockToSectionCoord(minY)).origin().above();
        dimension = player.level().dimension();
        resultMsgConsumer = msg -> ctx.getSource().sendSuccess(() -> msg, true);

        ctx.getSource().sendSuccess(() -> Component.literal("Starting chunkban test preparation in chunk " + chunk), true);

        return 1;
    }

    @SubscribeEvent
    public static void onLevelTick(final TickEvent.LevelTickEvent event)
    {
        if (dimension != null && event.phase == TickEvent.Phase.START && event.level.dimension() == dimension)
        {
            Level level = event.level;

            for (int i = 0; i < 16; i++)
            {
                BlockPos pos = placePos.east(i);

                level.setBlockAndUpdate(pos, state);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof FramedBlockEntity fbe)
                {
                    fbe.setCamo(CAMO_ONE_FACTORY.get(), false);
                }
                if (be instanceof FramedDoubleBlockEntity fdbe)
                {
                    fdbe.setCamo(CAMO_TWO_FACTORY.get(), true);
                }

                blocksPlaced++;
            }

            placePos = placePos.south();
            if (placePos.getZ() > startPos.getZ() + 15)
            {
                placePos = placePos.north(16).above();
                if (placePos.getY() >= level.getMaxBuildHeight())
                {
                    resultMsgConsumer.accept(Component.literal(
                            "Chunkban test preparation completed, placed " + blocksPlaced + " blocks"
                    ));

                    resultMsgConsumer = null;
                    dimension = null;
                    state = null;
                    startPos = null;
                    placePos = null;
                    blocksPlaced = 0;
                }
            }
        }
    }



    private ChunkBanTest() { }
}
