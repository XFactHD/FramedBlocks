package io.github.xfacthd.framedblocks.client.model.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.function.Function;

public final class ItemTransformsCodec {
    private static final Keyable MODDED_CTX_KEYS = StringRepresentable.keys(
            Arrays.stream(ItemDisplayContext.values())
                    .filter(ItemDisplayContext::isModded)
                    .toArray(ItemDisplayContext[]::new)
    );
    private static final Codec<ItemTransform> XFORM_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.VECTOR3F.optionalFieldOf("rotation", ItemTransform.Deserializer.DEFAULT_ROTATION).forGetter(ItemTransform::rotation),
            ExtraCodecs.VECTOR3F.optionalFieldOf("translation", ItemTransform.Deserializer.DEFAULT_TRANSLATION).xmap(
                    ItemTransformsCodec::scaleAndClampTranslationDecode,
                    ItemTransformsCodec::scaleAndClampTranslationEncode
            ).forGetter(ItemTransform::translation),
            ExtraCodecs.VECTOR3F.optionalFieldOf("scale", ItemTransform.Deserializer.DEFAULT_SCALE).xmap(
                    ItemTransformsCodec::clampScale,
                    ItemTransformsCodec::clampScale
            ).forGetter(ItemTransform::scale),
            ExtraCodecs.VECTOR3F.optionalFieldOf("right_rotation", ItemTransform.Deserializer.DEFAULT_ROTATION).forGetter(ItemTransform::rightRotation)
    ).apply(inst, ItemTransform::new));
    public static final Codec<ItemTransforms> XFORMS_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.THIRD_PERSON_LEFT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::thirdPersonLeftHand),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::thirdPersonRightHand),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIRST_PERSON_LEFT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::firstPersonLeftHand),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::firstPersonRightHand),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.HEAD.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::head),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.GUI.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::gui),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.GROUND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::ground),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIXED.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::fixed),
            XFORM_CODEC.optionalFieldOf(ItemDisplayContext.ON_SHELF.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::fixedFromBottom),
            Codec.simpleMap(ItemDisplayContext.CODEC, XFORM_CODEC, MODDED_CTX_KEYS).xmap(ImmutableMap::copyOf, Function.identity()).forGetter(ItemTransforms::moddedTransforms)
    ).apply(inst, ItemTransforms::new));

    private static Vector3fc scaleAndClampTranslationDecode(Vector3fc vec) {
        Vector3f result = new Vector3f(vec);
        result.mul(1F/16F);
        result.set(
                Mth.clamp(result.x, -ItemTransform.Deserializer.MAX_TRANSLATION, ItemTransform.Deserializer.MAX_TRANSLATION),
                Mth.clamp(result.y, -ItemTransform.Deserializer.MAX_TRANSLATION, ItemTransform.Deserializer.MAX_TRANSLATION),
                Mth.clamp(result.z, -ItemTransform.Deserializer.MAX_TRANSLATION, ItemTransform.Deserializer.MAX_TRANSLATION)
        );
        return result;
    }

    private static Vector3fc scaleAndClampTranslationEncode(Vector3fc vec) {
        Vector3f result = new Vector3f(vec);
        result.set(
                Mth.clamp(result.x, -ItemTransform.Deserializer.MAX_SCALE, ItemTransform.Deserializer.MAX_SCALE),
                Mth.clamp(result.y, -ItemTransform.Deserializer.MAX_SCALE, ItemTransform.Deserializer.MAX_SCALE),
                Mth.clamp(result.z, -ItemTransform.Deserializer.MAX_SCALE, ItemTransform.Deserializer.MAX_SCALE)
        );
        result.mul(16F);
        return result;
    }

    private static Vector3fc clampScale(Vector3fc vec) {
        return new Vector3f(
                Mth.clamp(vec.x(), -ItemTransform.Deserializer.MAX_SCALE, ItemTransform.Deserializer.MAX_SCALE),
                Mth.clamp(vec.y(), -ItemTransform.Deserializer.MAX_SCALE, ItemTransform.Deserializer.MAX_SCALE),
                Mth.clamp(vec.z(), -ItemTransform.Deserializer.MAX_SCALE, ItemTransform.Deserializer.MAX_SCALE)
        );
    }

    private ItemTransformsCodec() { }
}
