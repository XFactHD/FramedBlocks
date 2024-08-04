package xfacthd.framedblocks.api.datagen.loot.objects;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.internal.InternalAPI;

public final class NonTrivialCamoLootCondition implements LootItemCondition
{
    public static final NonTrivialCamoLootCondition INSTANCE = new NonTrivialCamoLootCondition();
    public static final MapCodec<NonTrivialCamoLootCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final LootItemCondition.Builder BUILDER = () -> INSTANCE;

    private NonTrivialCamoLootCondition() { }

    @Override
    public boolean test(LootContext ctx)
    {
        if (ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof FramedBlockEntity be)
        {
            return !be.canTriviallyDropAllCamos();
        }
        return false;
    }

    @Override
    public LootItemConditionType getType()
    {
        return InternalAPI.INSTANCE.getNonTrivialCamoLootConditionType();
    }
}
