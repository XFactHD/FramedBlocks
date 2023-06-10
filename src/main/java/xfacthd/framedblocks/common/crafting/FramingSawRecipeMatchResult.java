package xfacthd.framedblocks.common.crafting;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Locale;

public enum FramingSawRecipeMatchResult
{
    SUCCESS(true),
    MATERIAL_VALUE(false),
    MATERIAL_LCM(false),
    MISSING_ADDITIVE_0(false, 0),
    MISSING_ADDITIVE_1(false, 1),
    MISSING_ADDITIVE_2(false, 2),
    UNEXPECTED_ADDITIVE_0(false, 0),
    UNEXPECTED_ADDITIVE_1(false, 1),
    UNEXPECTED_ADDITIVE_2(false, 2),
    INCORRECT_ADDITIVE_0(false, 0),
    INCORRECT_ADDITIVE_1(false, 1),
    INCORRECT_ADDITIVE_2(false, 2),
    INSUFFICIENT_ADDITIVE_0(false, 0),
    INSUFFICIENT_ADDITIVE_1(false, 1),
    INSUFFICIENT_ADDITIVE_2(false, 2);

    static final FramingSawRecipeMatchResult[] MISSING_ADDITIVE = new FramingSawRecipeMatchResult[] {
            MISSING_ADDITIVE_0, MISSING_ADDITIVE_1, MISSING_ADDITIVE_2
    };
    static final FramingSawRecipeMatchResult[] UNEXPECTED_ADDITIVE = new FramingSawRecipeMatchResult[] {
            UNEXPECTED_ADDITIVE_0, UNEXPECTED_ADDITIVE_1, UNEXPECTED_ADDITIVE_2
    };
    static final FramingSawRecipeMatchResult[] INCORRECT_ADDITIVE = new FramingSawRecipeMatchResult[] {
            INCORRECT_ADDITIVE_0, INCORRECT_ADDITIVE_1, INCORRECT_ADDITIVE_2
    };
    static final FramingSawRecipeMatchResult[] INSUFFICIENT_ADDITIVE = new FramingSawRecipeMatchResult[] {
            INSUFFICIENT_ADDITIVE_0, INSUFFICIENT_ADDITIVE_1, INSUFFICIENT_ADDITIVE_2
    };

    private final boolean success;
    private final int additiveSlot;
    private final Component translation;

    FramingSawRecipeMatchResult(boolean success)
    {
        this(success, -1);
    }

    FramingSawRecipeMatchResult(boolean success, int additiveSlot)
    {
        this.success = success;
        this.additiveSlot = additiveSlot;
        this.translation = Utils.translate(
                "msg", "frame_crafter.fail." + toString().toLowerCase(Locale.ROOT)
        ).withStyle(success ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    public boolean success()
    {
        return success;
    }

    public int additiveSlot()
    {
        return additiveSlot;
    }

    public Component translation()
    {
        return translation;
    }
}
