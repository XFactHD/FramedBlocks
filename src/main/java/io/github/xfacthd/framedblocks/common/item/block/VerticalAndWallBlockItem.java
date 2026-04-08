package io.github.xfacthd.framedblocks.common.item.block;

import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class VerticalAndWallBlockItem extends FramedBlockItem {
    private final Block wallBlock;

    public VerticalAndWallBlockItem(Block verticalBlock, Block wallBlock, Item.Properties props) {
        super(verticalBlock, props);
        this.wallBlock = wallBlock;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        return getPlacementState(context, ctx -> {
            if (DirUtils.isY(ctx.getClickedFace())) {
                return getBlock().getStateForPlacement(ctx);
            } else {
                return wallBlock.getStateForPlacement(ctx);
            }
        });
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        super.registerBlocks(blockToItemMap, item);
        blockToItemMap.put(wallBlock, item);
    }
}
