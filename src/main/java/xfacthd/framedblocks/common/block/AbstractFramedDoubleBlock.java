package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractFramedDoubleBlock extends FramedBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType) { super(blockType); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.SOLID);
    }

    @Nonnull
    @Override
    public BlockState getFacade(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof FramedDoubleTileEntity)
            {
                return ((FramedDoubleTileEntity) te).getCamoState(side);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedDoubleTileEntity)
        {
            return ((FramedDoubleTileEntity) te).getSoundType();
        }
        return getSoundType(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (world.isClientSide())
        {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof FramedDoubleTileEntity)
            {
                BlockState defaultState = FBContent.blockFramedCube.get().defaultBlockState();
                BlockState camoOne = ((FramedDoubleTileEntity) te).getCamoState();
                BlockState camoTwo = ((FramedDoubleTileEntity) te).getCamoStateTwo();

                world.levelEvent(player, 2001, pos, getId(camoOne.isAir() ? defaultState : camoOne));
                if (camoOne != camoTwo)
                {
                    world.levelEvent(player, 2001, pos, getId(camoTwo.isAir() ? defaultState : camoTwo));
                }
            }
        }

        //Copied from super-implementation
        if (is(BlockTags.GUARDED_BY_PIGLINS))
        {
            PiglinTasks.angerNearbyPiglins(player, false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public IFormattableTextComponent printCamoBlock(CompoundNBT beTag)
    {
        BlockState camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
        BlockState camoStateTwo = NBTUtil.readBlockState(beTag.getCompound("camo_state_two"));

        IFormattableTextComponent component = camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getName().withStyle(TextFormatting.WHITE);
        component = component.copy().append(new StringTextComponent(" | ").withStyle(TextFormatting.GOLD));
        component.append(camoStateTwo.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoStateTwo.getBlock().getName().withStyle(TextFormatting.WHITE));

        return component;
    }
}