package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import team.chisel.ctm.api.IFacade;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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

    default ActionResultType handleBlockActivated(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (world.getTileEntity(pos) instanceof FramedTileEntity te)
        {
            return te.handleInteraction(player, hand, hit);
        }
        return ActionResultType.FAIL;
    }

    default int getLight(IBlockReader world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof FramedTileEntity te)
        {
            return te.getLightValue();
        }
        return 0;
    }

    default SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof FramedTileEntity te)
        {
            BlockState camoState = te.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return ((Block)this).getSoundType(state);
    }

    default List<ItemStack> getDrops(List<ItemStack> drops, LootContext.Builder builder)
    {
        if (builder.get(LootParameters.BLOCK_ENTITY) instanceof FramedTileEntity te)
        {
            ItemStack camo = te.getCamoStack();
            if (!camo.isEmpty())
            {
                drops.add(camo);
            }

            if (te instanceof FramedDoubleTileEntity dte)
            {
                camo = dte.getCamoStackTwo();
                if (!camo.isEmpty())
                {
                    drops.add(camo);
                }
            }
        }

        return drops;
    }

    default CtmPredicate getCtmPredicate() { return getBlockType().getCtmPredicate(); }

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
            if (world.getTileEntity(pos) instanceof FramedTileEntity te)
            {
                return te.getCamoState();
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    default boolean isSideHidden(IBlockReader world, BlockPos pos, BlockState state, Direction side)
    {
        if (world == null) { return false; } //Block had no camo when loaded => world in data not set

        SideSkipPredicate pred = ClientConfig.detailedCulling ? getBlockType().getSideSkipPredicate() : SideSkipPredicate.CTM;
        return pred.test(world, pos, state, world.getBlockState(pos.offset(side)), side);
    }

    default float getCamoSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        if (world.getTileEntity(pos) instanceof FramedTileEntity te)
        {
            BlockState camoState = te.getCamoState(Direction.UP);
            if (!camoState.isAir())
            {
                return camoState.getSlipperiness(world, pos, entity);
            }
        }
        return state.getBlock().getSlipperiness();
    }
}