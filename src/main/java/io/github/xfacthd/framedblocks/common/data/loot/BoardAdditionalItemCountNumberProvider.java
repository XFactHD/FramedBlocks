package io.github.xfacthd.framedblocks.common.data.loot;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public final class BoardAdditionalItemCountNumberProvider implements NumberProvider
{
    public static final BoardAdditionalItemCountNumberProvider INSTANCE = new BoardAdditionalItemCountNumberProvider();

    private BoardAdditionalItemCountNumberProvider() { }

    @Override
    public float getFloat(LootContext ctx)
    {
        BlockState state = ctx.getParameter(LootContextParams.BLOCK_STATE);
        if (state.hasProperty(PropertyHolder.FACES))
        {
            int faces = state.getValue(PropertyHolder.FACES);
            return Integer.bitCount(faces) - 1;
        }
        return 0;
    }

    @Override
    public MapCodec<BoardAdditionalItemCountNumberProvider> codec()
    {
        return FBContent.BOARD_ADDITIONAL_ITEM_COUNT_NUMBER_PROVIDER.value();
    }
}
