package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import team.chisel.ctm.api.IFacade;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiPredicate;

@SuppressWarnings("deprecation")
public interface IFramedBlock extends IFacade
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

    default List<ItemStack> getDrops(List<ItemStack> drops, LootContext.Builder builder)
    {
        TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
        if (te instanceof FramedTileEntity)
        {
            ItemStack camo = ((FramedTileEntity) te).getCamoStack();
            if (!camo.isEmpty())
            {
                drops.add(camo);
            }
        }

        return drops;
    }

    default BiPredicate<BlockState, Direction> getCtmPredicate() { return getBlockType().getCtmPredicate(); }

    @Nonnull
    @Override
    @Deprecated
    default BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        return Blocks.AIR.getDefaultState();
    }

    @Nonnull
    @Override
    default BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedTileEntity)
            {
                return ((FramedTileEntity) te).getCamoState();
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    //TODO: look into special casing certain interactions like slab <-> slab, panel <-> panel, panel <-> pillar, slab <-> stairs
    default boolean isSideHidden(BlockState state, BlockState adjState, Direction side)
    {
        FramedTileEntity adjTile = null;
        if (adjState.getBlock() instanceof IFramedBlock && ((IFramedBlock)adjState.getBlock()).getCtmPredicate().test(adjState, side.getOpposite()))
        {
            TileEntity te = DataHolder.world.getTileEntity(DataHolder.pos.offset(side.getOpposite()));
            if (te instanceof FramedTileEntity)
            {
                adjTile = (FramedTileEntity)te;
                adjState = adjTile.getCamoState(side.getOpposite());
            }
        }

        if (adjState.isAir()) { return false; }

        if (((IFramedBlock)state.getBlock()).getCtmPredicate().test(state, side))
        {
            TileEntity te = DataHolder.world.getTileEntity(DataHolder.pos);
            if (te instanceof FramedTileEntity)
            {
                if (adjTile != null)
                {
                    return adjTile.getCamoState(side.getOpposite()) == ((FramedTileEntity)te).getCamoState(side);
                }
                else
                {
                    return ((FramedTileEntity)te).getCamoState(side) == adjState;
                }
            }
        }

        return false;
    }
}