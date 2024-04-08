package xfacthd.framedblocks.common.compat.diagonalblocks;

import fuzs.diagonalblocks.api.v2.DiagonalBlockType;
import fuzs.diagonalblocks.api.v2.DiagonalBlockTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.model.wrapping.RegisterModelWrappersEvent;
import xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.render.color.FramedBlockColor;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.pane.FramedPaneBlock;
import xfacthd.framedblocks.common.block.pillar.FramedFenceBlock;

import java.util.Optional;

public final class DiagonalBlocksCompat
{
    private static boolean loaded = false;

    public static void init(IEventBus modBus)
    {
        if (ModList.get().isLoaded("diagonalblocks"))
        {
            try
            {
                GuardedAccess.init();
                if (FMLEnvironment.dist.isClient())
                {
                    GuardedClientAccess.init(modBus);
                }
                loaded = true;
            }
            catch (Throwable t)
            {
                FramedBlocks.LOGGER.error("Failed to initialized Diagonal Blocks integration");
            }
        }
    }

    public static boolean isFramedFence(BlockState state)
    {
        return (loaded && GuardedAccess.isFramedFence(state)) || state.getBlock() instanceof FramedFenceBlock;
    }

    public static boolean isFramedPane(BlockState state)
    {
        return (loaded && GuardedAccess.isFramedPane(state)) || state.getBlock() instanceof FramedPaneBlock;
    }



    private static final class GuardedAccess
    {
        public static void init()
        {
            DiagonalBlockTypes.FENCE.registerBlockFactory(
                    Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_FENCE).location(),
                    FramedDiagonalFenceBlock::new
            );
            DiagonalBlockTypes.WINDOW.registerBlockFactory(
                    Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_PANE).location(),
                    FramedDiagonalGlassPaneBlock::new
            );
            DiagonalBlockTypes.WINDOW.disableBlockFactory(Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_BARS).location());
            DiagonalBlockTypes.WALL.disableBlockFactory(Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_WALL).location());
        }

        public static boolean isFramedFence(BlockState state)
        {
            return state.getBlock() instanceof FramedDiagonalFenceBlock;
        }

        public static boolean isFramedPane(BlockState state)
        {
            return state.getBlock() instanceof FramedDiagonalGlassPaneBlock;
        }



        private GuardedAccess() { }
    }

    private static final class GuardedClientAccess
    {
        public static void init(IEventBus modBus)
        {
            modBus.addListener(GuardedClientAccess::onRegisterModelWrappers);
            modBus.addListener(GuardedClientAccess::onRegisterBlockColors);
        }

        private static void onRegisterModelWrappers(final RegisterModelWrappersEvent event)
        {
            getBlock(DiagonalBlockTypes.FENCE, FBContent.BLOCK_FRAMED_FENCE).ifPresent(
                    holder -> WrapHelper.wrap(holder, FramedDiagonalFenceGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK)
            );
            getBlock(DiagonalBlockTypes.WINDOW, FBContent.BLOCK_FRAMED_PANE).ifPresent(
                    holder -> WrapHelper.wrap(holder, FramedDiagonalPaneGeometry::new, WrapHelper.IGNORE_WATERLOGGED_LOCK)
            );
        }

        public static void onRegisterBlockColors(final RegisterColorHandlersEvent.Block event)
        {
            getBlock(DiagonalBlockTypes.FENCE, FBContent.BLOCK_FRAMED_FENCE).ifPresent(
                    holder -> event.register(FramedBlockColor.INSTANCE, holder.value())
            );
            getBlock(DiagonalBlockTypes.WINDOW, FBContent.BLOCK_FRAMED_PANE).ifPresent(
                    holder -> event.register(FramedBlockColor.INSTANCE, holder.value())
            );
        }

        private static Optional<Holder.Reference<Block>> getBlock(DiagonalBlockType type, Holder<Block> srcBlock)
        {
            ResourceLocation srcName = Utils.getKeyOrThrow(srcBlock).location();
            ResourceLocation destName = type.id(srcName.getNamespace() + "/" + srcName.getPath());
            return BuiltInRegistries.BLOCK.getHolder(ResourceKey.create(Registries.BLOCK, destName));
        }
    }



    private DiagonalBlocksCompat() { }
}
