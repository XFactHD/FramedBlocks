package io.github.xfacthd.framedblocks.api.datagen.loot.objects;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class RetainCamoLootCondition implements LootItemCondition
{
    public static final RetainCamoLootCondition INSTANCE = new RetainCamoLootCondition();
    public static final MapCodec<RetainCamoLootCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final LootItemCondition.Builder BUILDER = () -> INSTANCE;
    private static final Holder<LootItemConditionType> TYPE = DeferredHolder.create(
            Registries.LOOT_CONDITION_TYPE, Utils.id("retain_camo")
    );

    private RetainCamoLootCondition() { }

    @Override
    public boolean test(LootContext ctx)
    {
        if (ctx.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IFramedBlockEntity be)
        {
            return !be.canTriviallyDropAllCamos();
        }
        return false;
    }

    @Override
    public LootItemConditionType getType()
    {
        return TYPE.value();
    }
}
