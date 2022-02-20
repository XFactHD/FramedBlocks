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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
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
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedDoubleTileEntity)
            {
                return ((FramedDoubleTileEntity) te).getCamoState(side);
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSound(BlockState state, IWorldReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedDoubleTileEntity)
        {
            return ((FramedDoubleTileEntity) te).getSoundType();
        }
        return getSoundType(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (world.isRemote())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedDoubleTileEntity)
            {
                BlockState defaultState = FBContent.blockFramedCube.get().getDefaultState();
                BlockState camoOne = ((FramedDoubleTileEntity) te).getCamoState();
                BlockState camoTwo = ((FramedDoubleTileEntity) te).getCamoStateTwo();

                world.playEvent(player, 2001, pos, getStateId(camoOne.isAir() ? defaultState : camoOne));
                if (camoOne != camoTwo)
                {
                    world.playEvent(player, 2001, pos, getStateId(camoTwo.isAir() ? defaultState : camoTwo));
                }
            }
        }

        //Copied from super-implementation
        if (isIn(BlockTags.GUARDED_BY_PIGLINS))
        {
            PiglinTasks.func_234478_a_(player, false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public IFormattableTextComponent printCamoBlock(CompoundNBT beTag)
    {
        BlockState camoState = NBTUtil.readBlockState(beTag.getCompound("camo_state"));
        BlockState camoStateTwo = NBTUtil.readBlockState(beTag.getCompound("camo_state_two"));

        IFormattableTextComponent component = camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getTranslatedName().mergeStyle(TextFormatting.WHITE);
        component = component.deepCopy().appendSibling(new StringTextComponent(" | ").mergeStyle(TextFormatting.GOLD));
        component.appendSibling(camoStateTwo.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoStateTwo.getBlock().getTranslatedName().mergeStyle(TextFormatting.WHITE));

        return component;
    }
}