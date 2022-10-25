package xfacthd.framedblocks.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Map;

public class VerticalAndWallBlockItem extends BlockItem
{
    private final Block wallBlock;

    public VerticalAndWallBlockItem(Block verticalBlock, Block wallBlock, Item.Properties props)
    {
        super(verticalBlock, props);
        this.wallBlock = wallBlock;
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context)
    {
        if (Utils.isY(context.getClickedFace()))
        {
            return getBlock().getStateForPlacement(context);
        }
        else
        {
            return wallBlock.getStateForPlacement(context);
        }
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item)
    {
        super.registerBlocks(blockToItemMap, item);
        blockToItemMap.put(wallBlock, item);
    }

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item item)
    {
        super.removeFromBlockToItemMap(blockToItemMap, item);
        blockToItemMap.remove(wallBlock);
    }
}
