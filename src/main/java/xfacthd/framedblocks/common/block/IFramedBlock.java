package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

@SuppressWarnings("deprecation")
public interface IFramedBlock
{
    BlockType getBlockType();

    static Block.Properties createProperties()
    {
        return Block.Properties.create(Material.WOOD)
                .notSolid()
                .harvestTool(ToolType.AXE)
                .hardnessAndResistance(2F)
                .sound(SoundType.WOOD);
    }

    default BlockItem createItemBlock()
    {
        Block block = (Block)this;
        BlockItem item = new BlockItem(block, new Item.Properties().group(FramedBlocks.FRAMED_GROUP));
        //noinspection ConstantConditions
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    default ActionResultType handleBlockActivated(World world, BlockPos pos, PlayerEntity player, Hand hand)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            return ((FramedTileEntity) te).handleInteraction(player, hand);
        }
        return ActionResultType.FAIL;
    }

    default int getLight(IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            return ((FramedTileEntity) te).getLightValue();
        }
        return 0;
    }

    default SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            BlockState camoState = ((FramedTileEntity) te).getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return ((Block)this).getSoundType(state);
    }
}