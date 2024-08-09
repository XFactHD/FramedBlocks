package xfacthd.framedblocks.api.camo;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.internal.InternalAPI;

public final class CamoContainerHelper
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Registry<CamoContainerFactory<?>> REGISTRY = FramedBlocksAPI.INSTANCE.getCamoContainerFactoryRegistry();
    public static final Codec<CamoContainer<?, ?>> CODEC = REGISTRY.byNameCodec()
            .dispatch(CamoContainer::getFactory, CamoContainerFactory::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoContainer<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY.key())
            .dispatch(CamoContainer::getFactory, CamoContainerFactory::streamCodec);

    /**
     * Save the given {@link CamoContainer} to a {@link CompoundTag} for saving to disk
     */
    public static Tag writeToDisk(CamoContainer<?, ?> camo)
    {
        return CODEC.encodeStart(NbtOps.INSTANCE, camo).result().orElseGet(CompoundTag::new);
    }

    /**
     * Reconstruct a {@link CamoContainer} from the given {@link CompoundTag} from disk
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CamoContainer<?, ?> readFromDisk(CompoundTag tag)
    {
        if (tag.isEmpty())
        {
            return EmptyCamoContainer.EMPTY;
        }

        return CODEC.decode(NbtOps.INSTANCE, tag)
                .ifError(err -> LOGGER.error(err.message()))
                .result()
                .map(Pair::getFirst)
                .orElse((CamoContainer) EmptyCamoContainer.EMPTY);
    }

    /**
     * Save the given the {@link CamoContainer} to a {@link CompoundTag} for sync over the network
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CompoundTag writeToNetwork(CamoContainer<?, ?> camo)
    {
        CamoContainerFactory factory = camo.getFactory();
        int id = REGISTRY.getId(factory);
        Preconditions.checkState(id != -1, "Attempted to get sync ID for unregistered CamoContainerFactory");

        CompoundTag tag = new CompoundTag();
        tag.putInt("type", REGISTRY.getId(factory));
        factory.writeToNetwork(tag, camo);
        return tag;
    }

    /**
     * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag} from a network packet
     */
    public static CamoContainer<?, ?> readFromNetwork(CompoundTag tag)
    {
        if (tag.isEmpty())
        {
            return EmptyCamoContainer.EMPTY;
        }

        int id = tag.getInt("type");
        CamoContainerFactory<?> factory = REGISTRY.byId(id);
        if (factory == null)
        {
            LOGGER.error("Received unknown CamoContainer with ID {} from network, dropping!", id);
            return EmptyCamoContainer.EMPTY;
        }
        return factory.readFromNetwork(tag);
    }

    /**
     * Validate the given {@link CamoContainer} after loading from disk
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean validateCamo(CamoContainer<?, ?> camo)
    {
        CamoContainerFactory factory = camo.getFactory();
        return factory.validateCamo(camo);
    }

    /**
     * Remove the camo and refund the resources to the player.
     * <p>
     * Called on server and client side
     *
     * @return true if the camo was successfully given to the player and can be removed
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean removeCamo(CamoContainer<?, ?> camo, Level level, BlockPos pos, Player player, ItemStack stack)
    {
        CamoContainerFactory factory = camo.getFactory();
        return factory.removeCamo(level, pos, player, stack, camo);
    }

    /**
     * Construct an {@link ItemStack} representation of this camo to be dropped when the enclosing block is destroyed or
     * the material list of a blueprint is being computed if the camo can be trivially converted to an {@link ItemStack}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ItemStack dropCamo(CamoContainer<?, ?> camo)
    {
        CamoContainerFactory factory = camo.getFactory();
        return factory.dropCamo(camo);
    }

    /**
     * {@return a {@link CamoContainerFactory} to use for applying the given {@link ItemStack} as a camo or null if none exists}
     */
    @Nullable
    public static CamoContainerFactory<?> findCamoFactory(ItemStack stack)
    {
        return InternalAPI.INSTANCE.findCamoFactory(stack);
    }

    /**
     * {@return whether the given {@link ItemStack} can be used to remove the {@link CamoContainer} from a framed block}
     */
    public static boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack)
    {
        return InternalAPI.INSTANCE.isValidRemovalTool(container, stack);
    }

    /**
     * {@return whether the given plant can survive on the camo(s) of framed blocks potentially surrounding it}
     */
    public static TriState canPlantSurviveOnCamo(BlockState camoState, BlockGetter level, BlockPos pos, Direction side, BlockState plant)
    {
        if (!camoState.isAir() && level instanceof LevelReader reader)
        {
            BlockPos plantPos = pos.relative(side);
            if (reader instanceof CamoResolvingLevelReader)
            {
                LOGGER.warn("Encountered unexpected recursion in plant sustainability check for plant '{}' at {} on side {} of a framed block", plant, plantPos, side);
                return TriState.DEFAULT;
            }
            LevelReader camoResolvingLevel = new CamoResolvingLevelReader(reader, plantPos);
            if (plant.canSurvive(camoResolvingLevel, plantPos))
            {
                return TriState.TRUE;
            }
        }
        return TriState.DEFAULT;
    }

    /**
     * Handle interactions with the given camo in the provided context. If the interaction changes the camo data,
     * then a new camo container with the new data will be returned, otherwise the given camo is returned.
     *
     * @param level The level the framed block holding the camo is in
     * @param pos The position of the framed block holding the camo
     * @param player The player interacting with the framed block
     * @param camo The camo container the player is interacting with
     * @param stack The {@link ItemStack} used to interact with the framed block
     *
     * @return a new camo container if the camo data changes from this interaction, otherwise the given one
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CamoContainer<?, ?> handleCamoInteraction(Level level, BlockPos pos, Player player, CamoContainer<?, ?> camo, ItemStack stack)
    {
        if (!camo.isEmpty() && !stack.isEmpty())
        {
            CamoContainerFactory factory = camo.getFactory();
            return factory.handleInteraction(level, pos, player, camo, stack);
        }
        return camo;
    }



    public static final class Client
    {
        /**
         * {@return a {@link BakedModel} to be rendered for the given {@link CamoContent}}
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static BakedModel getOrCreateModel(CamoContent<?> content)
        {
            CamoClientHandler clientHandler = content.getClientHandler();
            return clientHandler.getOrCreateModel(content);
        }

        /**
         * {@return the set of render types which the given {@link CamoContent} renders in}
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static ChunkRenderTypeSet getRenderTypes(CamoContent<?> content, RandomSource random, ModelData modelData)
        {
            CamoClientHandler clientHandler = content.getClientHandler();
            return clientHandler.getRenderTypes(content, random, modelData);
        }



        private Client() { }
    }



    private CamoContainerHelper() { }
}
