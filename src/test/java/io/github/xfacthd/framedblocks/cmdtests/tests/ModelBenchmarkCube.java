package io.github.xfacthd.framedblocks.cmdtests.tests;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.Optional;

public final class ModelBenchmarkCube
{
    private static final String CONFIRMATION_KEY = "confirm";
    private static final Component MSG_NO_CONFIRM = Component.literal("Incorrect confirmation key, expected '" + CONFIRMATION_KEY + "'");
    private static final Component MSG_NOT_A_PLAYER = Component.literal("This command can only be executed by a real player");
    private static final Component MSG_CAMO_FAILED = Component.literal("Failed to create camo for test blocks");
    private static final Component MSG_OVERLAY_FAILED = Component.literal("Failed to resolve BlockOverlay for test blocks");
    private static final ResourceKey<BlockOverlay> OVERLAY_KEY = ResourceKey.create(FramedConstants.BLOCK_OVERLAY_REGISTRY_KEY, Utils.id("grass"));

    public static int buildBenchmarkCube(CommandContext<CommandSourceStack> ctx)
    {
        String confirmation = ctx.getArgument("confirm", String.class);
        if (!confirmation.equals(CONFIRMATION_KEY))
        {
            ctx.getSource().sendFailure(MSG_NO_CONFIRM);
            return 0;
        }

        if (!(ctx.getSource().getPlayer() instanceof ServerPlayer player) || player instanceof FakePlayer)
        {
            ctx.getSource().sendFailure(MSG_NOT_A_PLAYER);
            return 0;
        }

        ServerLevel level = ctx.getSource().getLevel();
        Direction horViewDir = Direction.getApproximateNearest(player.getViewVector(1F).multiply(1, 0, 1));
        SectionPos chunk = SectionPos.of(player.position().add(horViewDir.getUnitVec3().multiply(16, 0, 16)));
        BlockState state = FBContent.BLOCK_FRAMED_SLAB.value().defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        CamoContainer<?, ?> camo = FBContent.FACTORY_BLOCK.value().applyCamo(level, BlockPos.ZERO, player, ItemAccess.forStack(new ItemStack(Blocks.STONE)));
        if (camo == null)
        {
            ctx.getSource().sendFailure(MSG_CAMO_FAILED);
            return 0;
        }

        Holder<BlockOverlay> blockOverlay = null;
        if (ctx.getArgument("overlays", Boolean.class))
        {
            Optional<Holder.Reference<BlockOverlay>> optional = level.registryAccess().get(OVERLAY_KEY);
            if (optional.isEmpty())
            {
                ctx.getSource().sendFailure(MSG_OVERLAY_FAILED);
                return 0;
            }
            blockOverlay = optional.get();
        }

        int minX = chunk.minBlockX();
        int minY = chunk.minBlockY();
        int minZ = chunk.minBlockZ();
        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    pos.set(minX + x, minY + y, minZ + z);
                    boolean top = ((x % 2) ^ (z % 2)) != 0;
                    top |= y == 14 && x >= 7 && x <= 8 && z >= 7 && z <= 9;

                    level.setBlockAndUpdate(pos, state.setValue(FramedProperties.TOP, top));
                    if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be)
                    {
                        be.setCamo(camo, false);
                        be.setOverlay(blockOverlay);
                    }
                }
            }
        }

        pos.set(minX + 7, minY + 15, minZ + 7);
        level.setBlockAndUpdate(pos, Blocks.REDSTONE_WIRE.defaultBlockState());
        pos.set(minX + 8, minY + 15, minZ + 7);
        level.setBlockAndUpdate(pos, Blocks.REDSTONE_WIRE.defaultBlockState());

        pos.set(minX + 7, minY + 15, minZ + 8);
        level.setBlockAndUpdate(pos, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        pos.set(minX + 8, minY + 15, minZ + 8);
        level.setBlockAndUpdate(pos, Blocks.REPEATER.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));

        pos.set(minX + 7, minY + 15, minZ + 9);
        level.setBlockAndUpdate(pos, Blocks.REDSTONE_WIRE.defaultBlockState());
        pos.set(minX + 8, minY + 15, minZ + 9);
        level.setBlockAndUpdate(pos, Blocks.REDSTONE_TORCH.defaultBlockState());

        BlockPos dustPos = new BlockPos(minX + 8, minY + 15, minZ + 9);
        level.getServer().schedule(new TickTask(level.getServer().getTickCount() + 1, () ->
                level.setBlockAndUpdate(dustPos, Blocks.REDSTONE_WIRE.defaultBlockState())
        ));

        return Command.SINGLE_SUCCESS;
    }



    private ModelBenchmarkCube() { }
}
