package io.github.xfacthd.framedblocks.api.camo.empty;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.Nullable;

public final class EmptyCamoContainer extends CamoContainer<EmptyCamoContent, EmptyCamoContainer> {
    public static final EmptyCamoContainer EMPTY = new EmptyCamoContainer();
    public static final MutableComponent CAMO_NAME = Utils.translate("desc", "camo.empty").withStyle(ChatFormatting.ITALIC);
    private static final DeferredHolder<CamoContainerFactory<?>, CamoContainerFactory<EmptyCamoContainer>> FACTORY =
            DeferredHolder.create(FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_KEY, Utils.id("empty"));

    private EmptyCamoContainer() {
        super(EmptyCamoContent.EMPTY);
    }

    @Override
    public boolean canRotateCamo() {
        return false;
    }

    @Override
    public @Nullable EmptyCamoContainer rotateCamo()
    {
        return null;
    }

    @Override
    public EmptyCamoContainer adjustForCarrierRotation(Rotation rotation) {
        return this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return "EmptyCamoContainer{}";
    }

    @Override
    public CamoContainerFactory<EmptyCamoContainer> getFactory() {
        return FACTORY.value();
    }
}
