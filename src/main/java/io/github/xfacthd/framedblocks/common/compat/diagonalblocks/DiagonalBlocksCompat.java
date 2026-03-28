package io.github.xfacthd.framedblocks.common.compat.diagonalblocks;

import fuzs.diagonalblocks.api.v2.block.type.DiagonalBlockType;
import fuzs.diagonalblocks.api.v2.block.type.DiagonalBlockTypes;
import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.api.model.wrapping.RegisterModelWrappersEvent;
import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.pane.FramedPaneBlock;
import io.github.xfacthd.framedblocks.common.block.pillar.FramedFenceBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

import java.util.Optional;

public final class DiagonalBlocksCompat {
    private static boolean loaded = false;

    public static void init(IEventBus modBus) {
        if (ModList.get().isLoaded("diagonalblocks")) {
            try {
                GuardedAccess.init(modBus);
                if (Utils.CLIENT_DIST) {
                    GuardedClientAccess.init(modBus);
                }
                loaded = true;
            } catch (Throwable t) {
                FramedBlocks.LOGGER.error("Failed to initialized Diagonal Blocks integration");
            }
        }
    }

    public static boolean isFramedFence(BlockState state) {
        return (loaded && GuardedAccess.isFramedFence(state)) || state.getBlock() instanceof FramedFenceBlock;
    }

    public static boolean isFramedPane(BlockState state) {
        return (loaded && GuardedAccess.isFramedPane(state)) || state.getBlock() instanceof FramedPaneBlock;
    }

    private static final class GuardedAccess {
        public static void init(IEventBus modBus) {
            DiagonalBlockTypes.FENCE.registerBlockFactory(
                    Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_FENCE).identifier(),
                    _ -> FramedDiagonalFenceBlock::new
            );
            DiagonalBlockTypes.WINDOW.registerBlockFactory(
                    Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_PANE).identifier(),
                    _ -> FramedDiagonalGlassPaneBlock::new
            );
            DiagonalBlockTypes.WINDOW.disableBlockFactory(Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_BARS).identifier());
            DiagonalBlockTypes.WALL.disableBlockFactory(Utils.getKeyOrThrow(FBContent.BLOCK_FRAMED_WALL).identifier());

            modBus.addListener(GuardedAccess::onBlockEntityTypeAddBlocks);
        }

        private static void onBlockEntityTypeAddBlocks(BlockEntityTypeAddBlocksEvent event) {
            getBlock(DiagonalBlockTypes.FENCE, FBContent.BLOCK_FRAMED_FENCE).ifPresent(
                    holder -> event.modify(FBContent.BE_TYPE_FRAMED_BLOCK.value(), holder.value())
            );
            getBlock(DiagonalBlockTypes.WINDOW, FBContent.BLOCK_FRAMED_PANE).ifPresent(
                    holder -> event.modify(FBContent.BE_TYPE_FRAMED_BLOCK.value(), holder.value())
            );
        }

        public static boolean isFramedFence(BlockState state) {
            return state.getBlock() instanceof FramedDiagonalFenceBlock;
        }

        public static boolean isFramedPane(BlockState state) {
            return state.getBlock() instanceof FramedDiagonalGlassPaneBlock;
        }

        private static Optional<Holder.Reference<Block>> getBlock(DiagonalBlockType type, Holder<Block> srcBlock) {
            Identifier srcName = Utils.getKeyOrThrow(srcBlock).identifier();
            Identifier destName = type.id(srcName.getNamespace() + "/" + srcName.getPath());
            return BuiltInRegistries.BLOCK.get(ResourceKey.create(Registries.BLOCK, destName));
        }

        private GuardedAccess() { }
    }

    private static final class GuardedClientAccess {
        public static void init(IEventBus modBus) {
            modBus.addListener(GuardedClientAccess::onRegisterModelWrappers);
            modBus.addListener(GuardedClientAccess::onRegisterClientExtensions);
        }

        private static void onRegisterModelWrappers(RegisterModelWrappersEvent event) {
            GuardedAccess.getBlock(DiagonalBlockTypes.FENCE, FBContent.BLOCK_FRAMED_FENCE).ifPresent(
                    holder -> WrapHelper.wrap(holder, FramedDiagonalFenceGeometry::new, WrapHelper.DEFAULT_MERGER)
            );
            GuardedAccess.getBlock(DiagonalBlockTypes.WINDOW, FBContent.BLOCK_FRAMED_PANE).ifPresent(
                    holder -> WrapHelper.wrap(holder, FramedDiagonalPaneGeometry::new, WrapHelper.DEFAULT_MERGER)
            );
        }

        private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
            GuardedAccess.getBlock(DiagonalBlockTypes.FENCE, FBContent.BLOCK_FRAMED_FENCE).ifPresent(
                    holder -> event.registerBlock(FramedClientBlockExtensions.INSTANCE, holder.value())
            );
            GuardedAccess.getBlock(DiagonalBlockTypes.WINDOW, FBContent.BLOCK_FRAMED_PANE).ifPresent(
                    holder -> event.registerBlock(FramedClientBlockExtensions.INSTANCE, holder.value())
            );
        }
    }

    private DiagonalBlocksCompat() { }
}
