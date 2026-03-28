package io.github.xfacthd.framedblocks.client.model.item.property;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class BlueprintProperty implements ConditionalItemModelProperty {
    public static final Identifier HAS_DATA = Utils.id("blueprint_has_data");
    public static final BlueprintProperty INSTANCE = new BlueprintProperty();
    public static final MapCodec<BlueprintProperty> TYPE = MapCodec.unit(INSTANCE);

    private BlueprintProperty() { }

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext ctx) {
        return !stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY).isEmpty();
    }

    @Override
    public MapCodec<BlueprintProperty> type() {
        return TYPE;
    }
}
