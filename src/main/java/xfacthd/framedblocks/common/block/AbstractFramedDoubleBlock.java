package xfacthd.framedblocks.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractFramedDoubleBlock extends FramedBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType) { super(blockType); }

    @Nonnull
    @Override
    public BlockState getFacade(@Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = world.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity te)
            {
                return te.getCamoState(side);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity)
    {
        if (world.getBlockEntity(pos) instanceof FramedDoubleTileEntity te)
        {
            BlockState camoState = te.getCamoStateTwo();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }

            camoState = te.getCamoState();
            if (!camoState.isAir())
            {
                return camoState.getSoundType();
            }
        }
        return getSoundType(state);
    }

    @Override
    public MutableComponent printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));
        BlockState camoStateTwo = NbtUtils.readBlockState(beTag.getCompound("camo_state_two"));

        MutableComponent component = camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getName().withStyle(ChatFormatting.WHITE);
        component = component.copy().append(new TextComponent(" | ").withStyle(ChatFormatting.GOLD));
        component.append(camoStateTwo.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoStateTwo.getBlock().getName().withStyle(ChatFormatting.WHITE));

        return component;
    }
}