package xfacthd.framedblocks.api.datagen.loot.objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public final class SplitCamoLootFunction extends LootItemConditionalFunction
{
    public static final MapCodec<SplitCamoLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst).and(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("camo_index").forGetter(func -> func.camoIndex)
    ).apply(inst, SplitCamoLootFunction::new));

    private final int camoIndex;

    private SplitCamoLootFunction(List<LootItemCondition> conditions, int camoIndex)
    {
        super(conditions);
        this.camoIndex = camoIndex;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext ctx)
    {
        CamoList camoList = stack.remove(Utils.DC_TYPE_CAMO_LIST);
        if (camoList != null && !camoList.isEmpty())
        {
            CamoContainer<?, ?> camo = camoList.getCamo(camoIndex);
            if (camo != EmptyCamoContainer.EMPTY)
            {
                stack.set(Utils.DC_TYPE_CAMO_LIST, CamoList.of(camo));
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<SplitCamoLootFunction> getType()
    {
        return InternalAPI.INSTANCE.getSplitCamoLootFunctionType();
    }



    public static SplitCamoLootFunction.Builder split(int camoIndex)
    {
        return new Builder(camoIndex);
    }



    public static final class Builder extends LootItemConditionalFunction.Builder<Builder>
    {
        private final int camoIndex;

        private Builder(int camoIndex)
        {
            this.camoIndex = camoIndex;
        }

        @Override
        protected Builder getThis()
        {
            return this;
        }

        @Override
        public LootItemFunction build()
        {
            return new SplitCamoLootFunction(getConditions(), camoIndex);
        }
    }
}
