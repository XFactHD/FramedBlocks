package xfacthd.framedblocks.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractFramedDoubleBlock extends FramedBlock
{
    public AbstractFramedDoubleBlock(BlockType blockType) { super(blockType); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING);
    }

    @Nonnull
    @Override
    public BlockState getFacade(@Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull BlockPos connection)
    {
        BlockState state = level.getBlockState(pos);
        if (getCtmPredicate().test(state, side))
        {
            if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
            {
                return be.getCamo(side).getState();
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
            BlockState defaultState = be.getBlockState();
            BlockState camoOne = be.getCamo().getState();
            BlockState camoTwo = be.getCamoTwo().getState();

            level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, getId(camoOne.isAir() ? defaultState : camoOne));
            if (camoOne != camoTwo)
            {
                level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, getId(camoTwo.isAir() ? defaultState : camoTwo));
            }
        }
    }

    @Override
    public Optional<MutableComponent> printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = CamoContainer.load(beTag.getCompound("camo")).getState();
        BlockState camoStateTwo = CamoContainer.load(beTag.getCompound("camo_two")).getState();

        MutableComponent component = camoState.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoState.getBlock().getName().withStyle(ChatFormatting.WHITE);
        component = component.copy().append(Component.literal(" | ").withStyle(ChatFormatting.GOLD));
        component.append(camoStateTwo.isAir() ? FramedBlueprintItem.BLOCK_NONE : camoStateTwo.getBlock().getName().withStyle(ChatFormatting.WHITE));

        return Optional.of(component);
    }
}