package xfacthd.framedblocks.common.data.blueprint;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundTag;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerHelper;

import java.util.*;

public class DoubleBlockCopyBehaviour implements BlueprintCopyBehaviour
{
    static final String CAMO_CONTAINER_TWO_KEY = "camo_two";

    @Override
    public Optional<Set<CamoContainer<?, ?>>> getCamos(CompoundTag blueprintData)
    {
        Set<CamoContainer<?, ?>> camos = new ObjectArraySet<>(2);
        camos.add(CamoContainerHelper.readFromDisk(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_CONTAINER_KEY)));
        camos.add(CamoContainerHelper.readFromDisk(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_CONTAINER_TWO_KEY)));
        return Optional.of(camos);
    }
}
