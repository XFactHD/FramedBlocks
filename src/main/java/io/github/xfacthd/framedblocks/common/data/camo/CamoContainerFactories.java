package io.github.xfacthd.framedblocks.common.data.camo;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.TriggerRegistrar;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.FramedRegistries;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public final class CamoContainerFactories
{
    private static final Map<Item, CamoContainerFactory<?>> APPLICATION_ITEMS = new Reference2ObjectOpenHashMap<>();
    private static final List<FactoryPredicatePair> APPLICATION_PREDICATES = new ArrayList<>();
    private static final Map<Item, Set<CamoContainerFactory<?>>> REMOVAL_ITEMS = new Reference2ObjectOpenHashMap<>();
    private static final Map<CamoContainerFactory<?>, List<Predicate<ItemStack>>> REMOVAL_PREDICATES = new Reference2ObjectOpenHashMap<>();

    public static void registerCamoFactories()
    {
        FramedRegistries.CAMO_CONTAINER_FACTORIES.forEach(factory ->
                factory.registerTriggerItems(new TriggerRegistrarImpl(factory))
        );

        // Register builtin handling last to make sure the predicates actually act as broad fallbacks after addon ones

        TriggerRegistrar fluidRegistrar = new TriggerRegistrarImpl(FBContent.FACTORY_FLUID.value());
        fluidRegistrar.registerApplicationPredicate(stack -> stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack)) != null);
        fluidRegistrar.registerRemovalPredicate(stack -> stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack)) != null);

        TriggerRegistrar blockRegistrar = new TriggerRegistrarImpl(FBContent.FACTORY_BLOCK.value());
        blockRegistrar.registerApplicationPredicate(stack -> stack.getItem() instanceof BlockItem);
        blockRegistrar.registerRemovalPredicate(TriggerRegistrar.DEFAULT_REMOVAL);

        Set<CamoContainerFactory<?>> applicable = new ReferenceOpenHashSet<>(APPLICATION_ITEMS.values());
        APPLICATION_PREDICATES.stream().map(FactoryPredicatePair::factory).forEach(applicable::add);
        Set<CamoContainerFactory<?>> removable = new ReferenceOpenHashSet<>(REMOVAL_PREDICATES.keySet());
        REMOVAL_ITEMS.values().forEach(removable::addAll);
        int registrySize = FramedRegistries.CAMO_CONTAINER_FACTORIES.size() - 1; // Subtract one to ignore empty container
        if (applicable.size() != registrySize || removable.size() != registrySize)
        {
            StringBuilder builder = new StringBuilder("CamoContainerFactory trigger registration incomplete!\n");
            if (applicable.size() != registrySize) printMissing(builder, "application", applicable);
            if (removable.size() != registrySize) printMissing(builder, "removal", removable);
            throw new IllegalStateException(builder.toString());
        }
    }

    private static void printMissing(StringBuilder builder, String type, Set<CamoContainerFactory<?>> factories)
    {
        builder.append("\tFactories missing ").append(type).append(" items/predicates:\n");
        FramedRegistries.CAMO_CONTAINER_FACTORIES.entrySet().forEach(entry ->
        {
            if (entry.getValue() != FBContent.FACTORY_EMPTY.value() && !factories.contains(entry.getValue()))
            {
                builder.append("\t- ").append(entry.getKey().identifier()).append("\n");
            }
        });
    }

    @Nullable
    public static CamoContainerFactory<?> findCamoFactory(ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock)
        {
            return null;
        }

        CamoContainerFactory<?> factory = APPLICATION_ITEMS.get(stack.getItem());
        if (factory == null)
        {
            for (FactoryPredicatePair pair : APPLICATION_PREDICATES)
            {
                if (pair.predicate.test(stack))
                {
                    factory = pair.factory;
                    break;
                }
            }
        }
        return factory;
    }

    public static boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack)
    {
        Set<CamoContainerFactory<?>> factories = REMOVAL_ITEMS.get(stack.getItem());
        if (factories != null && factories.contains(container.getFactory()))
        {
            return true;
        }

        List<Predicate<ItemStack>> predicates = REMOVAL_PREDICATES.getOrDefault(container.getFactory(), List.of());
        if (!predicates.isEmpty())
        {
            for (Predicate<ItemStack> predicate : predicates)
            {
                if (predicate.test(stack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private record TriggerRegistrarImpl(CamoContainerFactory<?> factory) implements TriggerRegistrar
    {
        @Override
        public void registerApplicationItem(Item item)
        {
            if (APPLICATION_ITEMS.containsKey(item))
            {
                throw new IllegalArgumentException(String.format("Item %s is already registered!", item));
            }
            APPLICATION_ITEMS.put(item, factory);
        }

        @Override
        public void registerApplicationPredicate(Predicate<ItemStack> predicate)
        {
            APPLICATION_PREDICATES.add(new FactoryPredicatePair(predicate, factory));
        }

        @Override
        public void registerRemovalItem(Item item)
        {
            Set<CamoContainerFactory<?>> factories = REMOVAL_ITEMS.computeIfAbsent(item, _ -> new ReferenceOpenHashSet<>());
            if (factories.contains(factory))
            {
                String factoryName = Objects.requireNonNull(FramedRegistries.CAMO_CONTAINER_FACTORIES.getKey(factory)).toString();
                throw new IllegalArgumentException(String.format("Factory %s is already registered to item %s!", factoryName, item));
            }
            factories.add(factory);
        }

        @Override
        public void registerRemovalPredicate(Predicate<ItemStack> predicate)
        {
            REMOVAL_PREDICATES.computeIfAbsent(factory, _ -> new ArrayList<>()).add(predicate);
        }
    }

    private record FactoryPredicatePair(Predicate<ItemStack> predicate, CamoContainerFactory<?> factory) { }

    private CamoContainerFactories() { }
}
