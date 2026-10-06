package io.github.xfacthd.framedblocks.api.util.text;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public final class I18nUtils {
    /// Returns a text component with a translation key in the format `[prefix.]framedblocks[.postfix]`
    /// and the given formatting arguments.
    ///
    /// @param prefix    The prefix to prepend the translation key with
    /// @param postfix   The postfix to append to the translation key
    /// @param arguments The formatting arguments to insert into the translated text
    /// @return a translatable text component
    public static MutableComponent translate(@Nullable String prefix, @Nullable String postfix, Object... arguments) {
        return Component.translatable(translationKey(prefix, postfix), arguments);
    }

    /// Returns a text component with a translation key in the format `[prefix.]framedblocks[.postfix]`.
    ///
    /// @param prefix    The prefix to prepend the translation key with
    /// @param postfix   The postfix to append to the translation key
    /// @return a translatable text component
    public static MutableComponent translate(@Nullable String prefix, @Nullable String postfix) {
        return Component.translatable(translationKey(prefix, postfix));
    }

    /// {@return a translation key in the format `[prefix.]framedblocks[.postfix]`}
    ///
    /// @param prefix  The prefix to prepend the translation key with
    /// @param postfix The postfix to append to the translation key
    public static String translationKey(@Nullable String prefix, @Nullable String postfix) {
        String key = "";
        if (prefix != null) {
            key = prefix + ".";
        }
        key += FramedConstants.MOD_ID;
        if (postfix != null) {
            key += "." + postfix;
        }
        return key;
    }

    /// {@return a translation key for a config entry of the given type and config key}
    ///
    /// @param type The type of the enclosing config
    /// @param key  The key of the config entry
    public static String translateConfig(String type, String key) {
        return translationKey("config", type + "." + key);
    }

    /// {@return a user-displayable representation of the given tag key}
    ///
    /// @param tag The tag to translate
    public static MutableComponent translateTag(TagKey<?> tag) {
        String key = Tags.getTagTranslationKey(tag);
        return Component.translatableWithFallback(key, "#" + tag.location());
    }

    /// Build an array of text components indexed by the enum's ordinal in the format
    /// `prefix.framedblocks.postfix.value_serialized_name` and apply the given
    /// format modifiers to it.
    ///
    /// @param prefix     The prefix to prepend the translation keys with
    /// @param postfix    The postfix to insert between mod ID and the value name
    /// @param values     The enum values to translate
    /// @param formatting The format modifiers to apply to the text components
    /// @return the text components of the enum values
    public static <T extends Enum<T> & StringRepresentable> Component[] buildEnumTranslations(
            String prefix, String postfix, T[] values, ChatFormatting... formatting
    ) {
        return Arrays.stream(values)
                .map(v -> translate(prefix, postfix + "." + v.getSerializedName()))
                .map(c -> c.withStyle(formatting))
                .toArray(Component[]::new);
    }

    /// Build an array of text components indexed by the enum's ordinal with the enum value translations
    /// inserted as a format argument into the translation of the given key.
    ///
    /// @param key               The translation accepting a formatting argument
    /// @param values            The enum values to bind
    /// @param valueTranslations The translations of the enum values
    /// @return the text components of the bound enum value translations
    public static <T extends Enum<T>> Component[] bindEnumTranslation(
            String key, T[] values, Component[] valueTranslations
    ) {
        Preconditions.checkArgument(
                values.length == valueTranslations.length, "Value and translation arrays must have the same length"
        );
        Component[] components = new Component[values.length];
        for (T v : values) {
            components[v.ordinal()] = Component.translatable(key, valueTranslations[v.ordinal()]);
        }
        return components;
    }

    private I18nUtils() { }
}
