package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolType;
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
public interface IFramedBlock extends EntityBlock//, IFacade
{
    BlockType getBlockType();

    static Block.Properties createProperties()
    {
        return Block.Properties.of(Material.WOOD)
                .noOcclusion()
                .harvestTool(ToolType.AXE)
                .strength(2F)
                .sound(SoundType.WOOD);
    }

    default BlockItem createItemBlock()
    {
        Block block = (Block)this;
        BlockItem item = new BlockItem(block, new Item.Properties().tab(FramedBlocks.FRAMED_GROUP));
        //noinspection ConstantConditions
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    default void tryApplyCamoImmediately(Level world, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (!world.isClientSide() && placer instanceof Player player)
        {
            if (player.getMainHandItem() != stack) { return; }

            ItemStack otherStack = player.getOffhandItem();
            if (otherStack.getItem() instanceof BlockItem item && !(item.getBlock() instanceof IFramedBlock))
            {
                if (world.getBlockEntity(pos) instanceof FramedTileEntity te && !(te instanceof FramedDoubleTileEntity))
                {
                    Vec3 hitVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                    te.handleInteraction(player, InteractionHand.OFF_HAND, new BlockHitResult(hitVec, Direction.UP, pos, false));
                }
            }
        }
    }

    default InteractionResult handleUse(Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            return te.handleInteraction(player, hand, hit);
        }
        return InteractionResult.FAIL;
    }

    default int getLight(BlockGetter world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            return te.getLightValue();
        }
        return 0;
    }

    default SoundType getCamoSound(BlockState state, LevelReader world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            BlockState camoState = te.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return ((Block)this).getSoundType(state);
    }

    default List<ItemStack> getCamoDrops(List<ItemStack> drops, LootContext.Builder builder)
    {
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FramedTileEntity te)
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
    //@Override //TODO: reactivate when CTM is out
    @Deprecated
    default BlockState getFacade(@Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Nonnull
    //@Override //TODO: reactivate when CTM is out
    default BlockState getFacade(@Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
            {
                return te.getCamoState();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    default boolean isSideHidden(BlockGetter world, BlockPos pos, BlockState state, Direction side)
    {
        if (world == null) { return false; } //Block had no camo when loaded => world in data not set

        SideSkipPredicate pred = ClientConfig.detailedCulling ? getBlockType().getSideSkipPredicate() : SideSkipPredicate.CTM;
        return pred.test(world, pos, state, world.getBlockState(pos.relative(side)), side);
    }

    default float getCamoSlipperiness(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            BlockState camoState = te.getCamoState(Direction.UP);
            if (!camoState.isAir())
            {
                return camoState.getFriction(world, pos, entity);
            }
        }
        return state.getBlock().getFriction();
    }

    default float getCamoBlastResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            float resistance = te.getCamoBlastResistance(explosion);
            if (resistance > 0F)
            {
                return resistance;
            }
        }
        return state.getBlock().getExplosionResistance();
    }

    default boolean isCamoFlammable(BlockGetter world, BlockPos pos, Direction face)
    {
        if (CommonConfig.fireproofBlocks) { return false; }

        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            return te.isCamoFlammable(face);
        }
        return true;
    }

    default int getCamoFlammability(BlockGetter world, BlockPos pos, Direction face)
    {
        if (CommonConfig.fireproofBlocks) { return 0; }

        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            int flammability = te.getCamoFlammability(face);
            if (flammability > -1)
            {
                return flammability;
            }
        }
        return 20;
    }
}