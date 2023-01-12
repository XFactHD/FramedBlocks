package xfacthd.framedblocks.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.rail.FramedRailSlopeBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.util.*;

public abstract class AbstractFramedDoubleBlock extends FramedBlock
{
    private static final Map<BlockState, Tuple<BlockState, BlockState>> STATE_PAIRS = new IdentityHashMap<>();

    public AbstractFramedDoubleBlock(BlockType blockType) { super(blockType); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID);
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

    protected abstract Tuple<BlockState, BlockState> getBlockPair(BlockState state);



    public static void cacheStatePairs()
    {
        STATE_PAIRS.clear();

        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(AbstractFramedDoubleBlock.class::isInstance)
                .map(AbstractFramedDoubleBlock.class::cast)
                .forEach(block -> block.stateDefinition.getPossibleStates().forEach(state ->
                        STATE_PAIRS.put(state, block.getBlockPair(state))
                ));

        FramedRailSlopeBlock.cacheStatePairs(STATE_PAIRS);
    }

    public static Tuple<BlockState, BlockState> getStatePair(BlockState state)
    {
        Tuple<BlockState, BlockState> pair = STATE_PAIRS.get(state);
        if (pair == null)
        {
            throw new IllegalArgumentException("BlockState pair requested for invalid block: " + state.getBlock());
        }
        return pair;
    }
}