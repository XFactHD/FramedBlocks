package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.component.TargetColor;

import java.util.Optional;

public class FramedTargetBlockEntity extends FramedBlockEntity
{
    public static final DyeColor DEFAULT_COLOR = DyeColor.RED;

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
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putInt("overlay_color", overlayColor.getId());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.handleUpdateTag(nbt, provider);
        if (nbt.contains("overlay_color"))
        {
            overlayColor = DyeColor.byId(nbt.getInt("overlay_color"));
        }
    }

    @Override
    protected void writeToDataPacket(CompoundTag tag, HolderLookup.Provider lookupProvider)
    {
        super.writeToDataPacket(tag, lookupProvider);
        tag.putInt("overlay_color", overlayColor.getId());
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt, HolderLookup.Provider lookupProvider)
    {
        boolean colored = false;
        if (nbt.contains("overlay_color"))
        {
            DyeColor color = DyeColor.byId(nbt.getInt("overlay_color"));
            if (overlayColor != color)
            {
                overlayColor = color;
                colored = true;
            }
        }
        return super.readFromDataPacket(nbt, lookupProvider) || colored;
    }

    @Override
    protected Optional<AuxBlueprintData<?>> collectAuxBlueprintData()
    {
        return Optional.of(new TargetColor(overlayColor));
    }

    @Override
    protected void applyAuxDataFromBlueprint(AuxBlueprintData<?> auxData)
    {
        if (auxData instanceof TargetColor color)
        {
            overlayColor = color.color();
        }
    }

    @Override
    protected void collectMiscComponents(DataComponentMap.Builder builder)
    {
        builder.set(FBContent.DC_TYPE_TARGET_COLOR, new TargetColor(overlayColor));
    }

    @Override
    protected void applyMiscComponents(DataComponentInput input)
    {
        TargetColor color = input.getOrDefault(FBContent.DC_TYPE_TARGET_COLOR, TargetColor.DEFAULT);
        overlayColor = color.color();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putInt("overlay_color", overlayColor.getId());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        if (tag.contains("overlay_color"))
        {
            overlayColor = DyeColor.byId(tag.getInt("overlay_color"));
        }
    }
}
