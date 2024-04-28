package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainerHelper;
import xfacthd.framedblocks.api.ghost.CamoPair;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public sealed class DoubleBlockGhostRenderBehaviour implements GhostRenderBehaviour permits DoublePanelGhostRenderBehaviour
{
    @Override
    public CamoPair readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass)
    {
        return readDoubleCamo(stack);
    }

    @Override
    public ModelData buildModelData(ItemStack stack, ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, int renderPass, CamoPair camo)
    {
        return buildModelData(camo);
    }

    public static CamoPair readDoubleCamo(ItemStack stack)
    {
        var beData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        //noinspection ConstantConditions
        if (beData != null)
        {
            CompoundTag tag = beData.getUnsafe().getCompound("camo");
            CamoContainer<?, ?> camo = CamoContainerHelper.readFromDisk(tag);

            tag = beData.getUnsafe().getCompound("camo_two");
            CamoContainer<?, ?> camoTwo = CamoContainerHelper.readFromDisk(tag);

            return new CamoPair(camo.getContent(), camoTwo.getContent());
        }
        return CamoPair.EMPTY;
    }

    public static ModelData buildModelData(CamoPair camo)
    {
        return ModelData.builder()
                .with(FramedDoubleBlockEntity.DATA_LEFT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, new FramedBlockData(camo.getCamoOne(), false))
                        .build()
                )
                .with(FramedDoubleBlockEntity.DATA_RIGHT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, new FramedBlockData(camo.getCamoTwo(), true))
                        .build()
                )
                .build();
    }
}
