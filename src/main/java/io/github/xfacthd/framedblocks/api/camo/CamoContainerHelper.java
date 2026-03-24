package io.github.xfacthd.framedblocks.api.camo;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.util.network.ValidatingDecoder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public final class CamoContainerHelper
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Registry<CamoContainerFactory<?>> REGISTRY = FramedBlocksAPI.INSTANCE.getCamoContainerFactoryRegistry();
    public static final Codec<CamoContainer<?, ?>> CODEC = REGISTRY.byNameCodec()
            .dispatch(CamoContainer::getFactory, CamoContainerFactory::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoContainer<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY.key())
            .<CamoContainer<?, ?>>dispatch(CamoContainer::getFactory, CamoContainerFactory::streamCodec)
            .apply(ValidatingDecoder.of(CamoContainerHelper::validateFromNetwork));

    /**
     * Save the given the {@link CamoContainer} to a {@link CompoundTag} for sync over the network
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void writeToNetwork(ValueOutput valueOutput, CamoContainer<?, ?> camo)
    {
        CamoContainerFactory factory = camo.getFactory();
        int id = REGISTRY.getId(factory);
        Preconditions.checkState(id != -1, "Attempted to get sync ID for unregistered CamoContainerFactory");

        valueOutput.putInt("type", REGISTRY.getId(factory));
        factory.writeToNetwork(valueOutput, camo);
    }

    /**
     * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag} from a network packet
     */
    public static CamoContainer<?, ?> readFromNetwork(Optional<ValueInput> optValueInput)
    {
        if (optValueInput.isEmpty())
        {
            return EmptyCamoContainer.EMPTY;
        }

        ValueInput valueInput = optValueInput.get();
        int id = valueInput.getIntOr("type", -1);
        CamoContainerFactory<?> factory = REGISTRY.byId(id);
        if (factory == null)
        {
            LOGGER.error("Received unknown CamoContainer with ID {} from network, dropping!", id);
            return EmptyCamoContainer.EMPTY;
        }
        return validateFromNetwork(factory.readFromNetwork(valueInput));
    }

    private static CamoContainer<?, ?> validateFromNetwork(CamoContainer<?, ?> container)
    {
        return validateCamo(container) ? container : EmptyCamoContainer.EMPTY;
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
    public static boolean removeCamo(CamoContainer<?, ?> camo, Level level, BlockPos pos, Player player, ItemAccess itemAccess)
    {
        CamoContainerFactory factory = camo.getFactory();
        return factory.removeCamo(level, pos, player, itemAccess, camo);
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
        return stack.isEmpty() ? null : InternalAPI.INSTANCE.findCamoFactory(stack);
    }

    @Nullable
    public static CamoCraftingHandler<?> findCraftingHandler(ItemStack stack)
    {
        CamoContainerFactory<?> factory = findCamoFactory(stack);
        return factory != null ? factory.getCraftingHandler() : null;
    }

    /**
     * {@return whether the given {@link ItemStack} can be used to remove the {@link CamoContainer} from a framed block}
     */
    public static boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack)
    {
        return !container.isEmpty() && !stack.isEmpty() && InternalAPI.INSTANCE.isValidRemovalTool(container, stack);
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
     * @param hand The hand holding the stack used to interact with the framed block
     *
     * @return a new camo container if the camo data changes from this interaction, otherwise the given one
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CamoContainer<?, ?> handleCamoInteraction(Level level, BlockPos pos, Player player, CamoContainer<?, ?> camo, ItemStack stack, InteractionHand hand)
    {
        if (!camo.isEmpty() && !stack.isEmpty())
        {
            CamoContainerFactory factory = camo.getFactory();
            return factory.handleInteraction(level, pos, player, camo, stack, hand);
        }
        return camo;
    }

    public static final class Client
    {
        /**
         * {@return a {@link BlockStateModel } to be rendered for the given {@link CamoContent}}
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static BlockStateModel getOrCreateModel(CamoContent<?> content)
        {
            CamoContentClientHandler clientHandler = content.getClientHandler();
            return clientHandler.getOrCreateModel(content);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static int getTintCount(CamoContainer<?, ?> container)
        {
            CamoContainerClientHandler clientHandler = container.getClientHandler();
            return clientHandler.getTintCount(container);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void collectTintValues(CamoContainer<?, ?> container, BlockAndTintGetter level, BlockPos pos, IntList tintList)
        {
            CamoContainerClientHandler clientHandler = container.getClientHandler();
            clientHandler.collectTintValues(container, level, pos, tintList);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void collectTintValues(CamoContainer<?, ?> container, ItemStack stack, IntList tintList)
        {
            CamoContainerClientHandler clientHandler = container.getClientHandler();
            clientHandler.collectTintValues(container, stack, tintList);
        }

        private Client() { }
    }

    private CamoContainerHelper() { }
}
