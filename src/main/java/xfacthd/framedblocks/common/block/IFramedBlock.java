package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import team.chisel.ctm.api.IFacade;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({ "deprecation", "unused" })
public interface IFramedBlock extends IFacade
{
    BlockType getBlockType();

    static Block.Properties createProperties(BlockType type)
    {
        Block.Properties props = Block.Properties.of(Material.WOOD)
                .harvestTool(ToolType.AXE)
                .strength(2F)
                .sound(SoundType.WOOD)
                .isViewBlocking(IFramedBlock::isBlockSuffocating)
                .isSuffocating(IFramedBlock::isBlockSuffocating);

        if (!type.canOccludeWithSolidCamo())
        {
            props.noOcclusion();
        }

        return props;
    }

    static boolean isBlockSuffocating(BlockState state, IBlockReader level, BlockPos pos)
    {
        return ((IFramedBlock) state.getBlock()).isSuffocating(state, level, pos);
    }

    default BlockItem createItemBlock()
    {
        Block block = (Block)this;
        BlockItem item = new BlockItem(block, new Item.Properties().tab(FramedBlocks.FRAMED_GROUP));
        //noinspection ConstantConditions
        item.setRegistryName(block.getRegistryName());
        return item;
    }

    default void tryApplyCamoImmediately(World world, BlockPos pos, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (world.isClientSide()) { return; }

        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof FramedTileEntity)
            {
                ((FramedTileEntity) te).checkCamoSolid();
            }
            return;
        }

        if (placer instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) placer;

            if (player.getMainHandItem() != stack) { return; }

            ItemStack otherStack = player.getOffhandItem();
            if (otherStack.getItem() instanceof BlockItem && !(((BlockItem) otherStack.getItem()).getBlock() instanceof IFramedBlock))
            {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof FramedTileEntity && !(te instanceof FramedDoubleTileEntity))
                {
                    Vector3d hitVec = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
                    ((FramedTileEntity) te).handleInteraction(player, Hand.OFF_HAND, new BlockRayTraceResult(hitVec, Direction.UP, pos, false));
                }
            }
        }
    }

    default ActionResultType handleBlockActivated(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            return ((FramedTileEntity) te).handleInteraction(player, hand, hit);
        }
        return ActionResultType.FAIL;
    }

    default int getLight(IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            return ((FramedTileEntity) te).getLightValue();
        }
        return 0;
    }

    default SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        TileEntity te = world.getBlockEntity(pos);
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
        TileEntity te = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
        if (te instanceof FramedTileEntity)
        {
            ((FramedTileEntity) te).addCamoDrops(drops);
        }

        return drops;
    }

    default CtmPredicate getCtmPredicate() { return getBlockType().getCtmPredicate(); }

    @Nonnull
    @Override
    @Deprecated
    default BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side)
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Nonnull
    @Override
    default BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof FramedTileEntity)
            {
                return ((FramedTileEntity) te).getCamoState();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    default boolean isSideHidden(IBlockReader world, BlockPos pos, BlockState state, Direction side)
    {
        if (world == null) { return false; } //Block had no camo when loaded => world in data not set

        BlockPos neighborPos = pos.relative(side);
        BlockState neighborState = world.getBlockState(neighborPos);

        if (ServerConfig.enableIntangibleFeature && !isIntangible(state, world, pos, null))
        {
            if (neighborState.getBlock() instanceof IFramedBlock)
            {
                IFramedBlock fb = (IFramedBlock) neighborState.getBlock();
                if (fb.getBlockType().allowMakingIntangible() && fb.isIntangible(neighborState, world, neighborPos, null))
                {
                    return false;
                }
            }
        }

        SideSkipPredicate pred = ClientConfig.detailedCulling ? getBlockType().getSideSkipPredicate() : SideSkipPredicate.CTM;
        return pred.test(world, pos, state, world.getBlockState(pos.relative(side)), side);
    }

    default float getCamoSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            BlockState camoState = ((FramedTileEntity) te).getCamoState(Direction.UP);
            if (!camoState.isAir())
            {
                return camoState.getSlipperiness(world, pos, entity);
            }
        }
        return state.getBlock().getFriction();
    }

    default float getCamoBlastResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            float resistance = ((FramedTileEntity) te).getCamoBlastResistance(explosion);
            if (resistance > 0F)
            {
                return resistance;
            }
        }
        return state.getBlock().getExplosionResistance();
    }

    default boolean isCamoFlammable(IBlockReader world, BlockPos pos, Direction face)
    {
        if (CommonConfig.fireproofBlocks) { return false; }

        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            return ((FramedTileEntity) te).isCamoFlammable(face);
        }
        return true;
    }

    default int getCamoFlammability(IBlockReader world, BlockPos pos, Direction face)
    {
        if (CommonConfig.fireproofBlocks) { return 0; }

        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            int flammability = ((FramedTileEntity) te).getCamoFlammability(face);
            if (flammability > -1)
            {
                return flammability;
            }
        }
        return 20;
    }

    default boolean isIntangible(BlockState state, IBlockReader world, BlockPos pos, @Nullable ISelectionContext ctx)
    {
        if (!ServerConfig.enableIntangibleFeature || !getBlockType().allowMakingIntangible()) { return false; }

        TileEntity te = world.getBlockEntity(pos);
        return te instanceof FramedTileEntity && ((FramedTileEntity) te).isIntangible(ctx);
    }

    default boolean isSuffocating(BlockState state, IBlockReader world, BlockPos pos)
    {
        if (ServerConfig.enableIntangibleFeature && getBlockType().allowMakingIntangible())
        {
            BlockState stateAtPos = world.getBlockState(pos);
            if (state != stateAtPos || isIntangible(state, world, pos, null))
            {
                return false;
            }
        }

        // Copy of the default suffocation check
        return state.getMaterial().blocksMotion() && state.isCollisionShapeFullBlock(world, pos);
    }

    default boolean useCamoOcclusionShapeForLightOcclusion(BlockState state)
    {
        if (getBlockType() != null && !getBlockType().canOccludeWithSolidCamo()) { return false; }

        return state.hasProperty(PropertyHolder.SOLID) && state.getValue(PropertyHolder.SOLID) && !state.getValue(PropertyHolder.GLOWING);
    }

    default VoxelShape getCamoOcclusionShape(BlockState state, IBlockReader level, BlockPos pos)
    {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(PropertyHolder.SOLID))
        {
            return VoxelShapes.empty();
        }
        return state.getShape(level, pos);
    }

    default VoxelShape getCamoVisualShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx)
    {
        if (getBlockType().canOccludeWithSolidCamo() && !state.getValue(PropertyHolder.SOLID))
        {
            return VoxelShapes.empty();
        }
        return state.getCollisionShape(level, pos, ctx);
    }

    default IFormattableTextComponent printCamoBlock(CompoundNBT beTag)
    {
        BlockState camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
        return camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getName().withStyle(TextFormatting.WHITE);
    }

    static boolean suppressParticles(BlockState state, World world, BlockPos pos)
    {
        if (state.getBlock() instanceof IFramedBlock && ((IFramedBlock) state.getBlock()).getBlockType().allowMakingIntangible())
        {
            return ((IFramedBlock) state.getBlock()).isIntangible(state, world, pos, null);
        }
        return false;
    }
}