package xfacthd.framedblocks.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractFramedDoubleBlock extends FramedBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType) { super(blockType); }

    @Nonnull
    @Override
    public BlockState getFacadeDisabled(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = level.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
            {
                return be.getCamoState(side);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            return be.getSoundType();
        }
        return getSoundType(state);
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state)
    {
        if (level.isClientSide() && level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            BlockState defaultState = FBContent.blockFramedCube.get().defaultBlockState();
            BlockState camoOne = be.getCamoState();
            BlockState camoTwo = be.getCamoStateTwo();

            level.levelEvent(player, 2001, pos, getId(camoOne.isAir() ? defaultState : camoOne));
            if (camoOne != camoTwo)
            {
                level.levelEvent(player, 2001, pos, getId(camoTwo.isAir() ? defaultState : camoTwo));
            }
        }
    }

    @Override
    public Optional<MutableComponent> printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = NbtUtils.readBlockState(beTag.getCompound("camo_state"));
        BlockState camoStateTwo = NbtUtils.readBlockState(beTag.getCompound("camo_state_two"));

        MutableComponent component = camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getName().withStyle(ChatFormatting.WHITE);
        component = component.copy().append(new TextComponent(" | ").withStyle(ChatFormatting.GOLD));
        component.append(camoStateTwo.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoStateTwo.getBlock().getName().withStyle(ChatFormatting.WHITE));

        return Optional.of(component);
    }
}