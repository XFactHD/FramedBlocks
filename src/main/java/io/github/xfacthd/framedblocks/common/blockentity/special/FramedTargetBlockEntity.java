package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

import java.util.Optional;

public class FramedTargetBlockEntity extends FramedBlockEntity
{
    public static final DyeColor DEFAULT_COLOR = DyeColor.RED;
    public static final ModelProperty<DyeColor> COLOR_PROPERTY = new ModelProperty<>();

    private DyeColor overlayColor = DEFAULT_COLOR;

    public FramedTargetBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_TARGET.value(), pos, state);
    }

    public boolean setOverlayColor(DyeColor overlayColor)
    {
        if (this.overlayColor != overlayColor)
        {
            if (!level().isClientSide())
            {
                this.overlayColor = overlayColor;

                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                setChangedWithoutSignalUpdate();
            }

            return true;
        }
        return false;
    }

    public int getOverlayColor()
    {
        return overlayColor.getTextColor();
    }

    @Override
    protected void attachAdditionalModelData(ModelData.Builder builder)
    {
        builder.with(COLOR_PROPERTY, overlayColor);
    }

    @Override
    protected void writeToDataPacket(ValueOutput tag)
    {
        super.writeToDataPacket(tag);
        tag.putInt("overlay_color", overlayColor.getId());
    }

    @Override
    protected void readFromDataPacket(NetworkValueInput input)
    {
        super.readFromDataPacket(input);

        Optional<Integer> optOverlayColor = input.getInt("overlay_color");
        if (optOverlayColor.isPresent())
        {
            DyeColor color = DyeColor.byId(optOverlayColor.get());
            if (overlayColor != color)
            {
                overlayColor = color;
                input.requestRenderUpdate();
            }
        }
    }

    @Override
    protected BlueprintData appendCustomBlueprintData(BlueprintData blueprintData)
    {
        return blueprintData.withCustomData(FBContent.DC_TYPE_TARGET_COLOR, overlayColor);
    }

    @Override
    protected void applyCustomDataFromBlueprint(TypedDataComponent<?> auxData)
    {
        if (auxData.value() instanceof DyeColor color)
        {
            overlayColor = color;
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput)
    {
        super.removeComponentsFromTag(valueOutput);
        valueOutput.discard("overlay_color");
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        builder.set(FBContent.DC_TYPE_TARGET_COLOR, overlayColor);
    }

    @Override
    protected void applyMiscComponents(DataComponentGetter input)
    {
        overlayColor = input.getOrDefault(FBContent.DC_TYPE_TARGET_COLOR, DEFAULT_COLOR);
    }

    @Override
    public void saveAdditional(ValueOutput tag)
    {
        super.saveAdditional(tag);
        tag.putInt("overlay_color", overlayColor.getId());
    }

    @Override
    public void loadAdditional(ValueInput tag)
    {
        super.loadAdditional(tag);
        overlayColor = DyeColor.byId(tag.getIntOr("overlay_color", DEFAULT_COLOR.getId()));
    }
}
