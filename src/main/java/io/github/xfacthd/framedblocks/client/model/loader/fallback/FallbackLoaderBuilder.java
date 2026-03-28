package io.github.xfacthd.framedblocks.client.model.loader.fallback;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class FallbackLoaderBuilder extends CustomLoaderBuilder {
    private final List<ICondition> conditions = new ArrayList<>();
    @Nullable
    private Identifier fallback;

    public FallbackLoaderBuilder() {
        super(FallbackLoader.ID, true);
    }

    public FallbackLoaderBuilder addCondition(ICondition condition) {
        Preconditions.checkNotNull(condition, "Condition must not be null");
        conditions.add(condition);
        return this;
    }

    public FallbackLoaderBuilder setFallback(Identifier fallback) {
        Preconditions.checkNotNull(fallback, "Fallback must not be null");
        Preconditions.checkArgument(
                !fallback.equals(MissingCuboidModel.LOCATION),
                "Cannot use minecraft:builtin/missing as fallback model"
        );
        this.fallback = fallback;
        return this;
    }

    @Override
    protected CustomLoaderBuilder copyInternal() {
        FallbackLoaderBuilder builder = new FallbackLoaderBuilder();
        builder.conditions.addAll(conditions);
        builder.fallback = fallback;
        return builder;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        Preconditions.checkNotNull(fallback, "No fallback model set");
        Preconditions.checkState(!conditions.isEmpty(), "No conditions specified");

        json = super.toJson(json);
        json.add(ConditionalOps.DEFAULT_CONDITIONS_KEY, ICondition.LIST_CODEC.encodeStart(JsonOps.INSTANCE, conditions).getOrThrow());
        json.addProperty("fallback", fallback.toString());
        return json;
    }
}
