package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/// Builder for [ItemTransforms] of an item model.
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class ItemTransformsBuilder {
    private ItemTransform thirdPersonLeftHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform thirdPersonRightHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform firstPersonLeftHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform firstPersonRightHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform head = ItemTransform.NO_TRANSFORM;
    private ItemTransform gui = ItemTransform.NO_TRANSFORM;
    private ItemTransform ground = ItemTransform.NO_TRANSFORM;
    private ItemTransform fixed = ItemTransform.NO_TRANSFORM;
    private ItemTransform fixedFromBottom = ItemTransform.NO_TRANSFORM;
    private final Map<ItemDisplayContext, ItemTransform> moddedTransforms = new EnumMap<>(ItemDisplayContext.class);

    ItemTransformsBuilder() { }

    /// Specify transforms for the [third-person left hand][ItemDisplayContext#THIRD_PERSON_LEFT_HAND] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder thirdPersonLeftHand(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.thirdPersonLeftHand, "thirdperson_lefthand");
        this.thirdPersonLeftHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [third-person right hand][ItemDisplayContext#THIRD_PERSON_RIGHT_HAND] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder thirdPersonRightHand(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.thirdPersonRightHand, "thirdperson_righthand");
        this.thirdPersonRightHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [first-person left hand][ItemDisplayContext#FIRST_PERSON_LEFT_HAND] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder firstPersonLeftHand(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.firstPersonLeftHand, "firstperson_lefthand");
        this.firstPersonLeftHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [first-person right hand][ItemDisplayContext#FIRST_PERSON_RIGHT_HAND] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder firstPersonRightHand(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.firstPersonRightHand, "firstperson_righthand");
        this.firstPersonRightHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [head][ItemDisplayContext#HEAD] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder head(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.head, "head");
        this.head = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [gui][ItemDisplayContext#GUI] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder gui(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.gui, "gui");
        this.gui = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [ground][ItemDisplayContext#GROUND] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder ground(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.ground, "ground");
        this.ground = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [fixed][ItemDisplayContext#FIXED] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder fixed(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.fixed, "fixed");
        this.fixed = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the [fixed from bottom][ItemDisplayContext#ON_SHELF] perspective.
    ///
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder fixedFromBottom(UnaryOperator<TransformBuilder> operator) {
        assertNotSet(this.fixedFromBottom, "on_shelf");
        this.fixedFromBottom = operator.apply(new TransformBuilder()).build();
        return this;
    }

    /// Specify transforms for the given non-vanilla perspective.
    ///
    /// @param context  The perspective to set the transforms for
    /// @param operator A function to build the transforms
    /// @return this builder
    public ItemTransformsBuilder custom(ItemDisplayContext context, UnaryOperator<TransformBuilder> operator) {
        Preconditions.checkArgument(context.isModded(), "Display context %s is not a modded context", context);
        ItemTransform prev = moddedTransforms.putIfAbsent(context, operator.apply(new TransformBuilder()).build());
        assertNotSet(prev, context.getSerializedName());
        return this;
    }

    /// {@return the built item transforms}
    public ItemTransforms build() {
        ImmutableMap<ItemDisplayContext, ItemTransform> modded = ImmutableMap.copyOf(moddedTransforms);
        return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed, fixedFromBottom, modded);
    }

    private static void assertNotSet(@Nullable ItemTransform xform, String name) {
        if (xform != null && !ItemTransform.NO_TRANSFORM.equals(xform)) {
            throw new IllegalStateException("Duplicate declaration of transform for '" + name + "' perspective");
        }
    }

    /// Builder for the transforms of a specific perspective.
    public static final class TransformBuilder {
        private static final float MAX_TRANSLATION = ItemTransform.Deserializer.MAX_TRANSLATION * 16F;

        private Vector3fc rotation = ItemTransform.Deserializer.DEFAULT_ROTATION;
        private Vector3fc translation = ItemTransform.Deserializer.DEFAULT_TRANSLATION;
        private Vector3fc scale = ItemTransform.Deserializer.DEFAULT_SCALE;
        private Vector3fc rightRotation = ItemTransform.Deserializer.DEFAULT_ROTATION;

        /// Specify the rotation for this perspective in degrees.
        ///
        /// @param x The rotation around the X axis
        /// @param y The rotation around the Y axis
        /// @param z The rotation around the Z axis
        /// @return this builder
        public TransformBuilder rotation(float x, float y, float z) {
            this.rotation = new Vector3f(x, y, z);
            return this;
        }

        /// Specify the translation for this perspective in "pixels".
        /// The translations must be between -80 and +80 (+/- 5 blocks).
        ///
        /// @param x The translation along the X axis
        /// @param y The translation along the Y axis
        /// @param z The translation along the Z axis
        /// @return this builder
        public TransformBuilder translation(float x, float y, float z) {
            Preconditions.checkArgument(Math.abs(x) <= MAX_TRANSLATION, "Translation must be -80 <= x <= 80");
            Preconditions.checkArgument(Math.abs(y) <= MAX_TRANSLATION, "Translation must be -80 <= y <= 80");
            Preconditions.checkArgument(Math.abs(z) <= MAX_TRANSLATION, "Translation must be -80 <= z <= 80");
            this.translation = new Vector3f(x / 16F, y / 16F, z / 16F);
            return this;
        }

        /// Specify the scale for this perspective.
        /// The scale must be between -4 and +4.
        ///
        /// @param scale The scale for all axis
        /// @return this builder
        public TransformBuilder scale(float scale) {
            return scale(scale, scale, scale);
        }

        /// Specify the scale for this perspective.
        /// The scale must be between -4 and +4.
        ///
        /// @param x The scale along the X axis.
        /// @param y The scale along the Y axis.
        /// @param z The scale along the Z axis.
        /// @return this builder
        public TransformBuilder scale(float x, float y, float z) {
            Preconditions.checkArgument(Math.abs(x) <= ItemTransform.Deserializer.MAX_SCALE, "Scale must be -4 <= x <= 4");
            Preconditions.checkArgument(Math.abs(y) <= ItemTransform.Deserializer.MAX_SCALE, "Scale must be -4 <= y <= 4");
            Preconditions.checkArgument(Math.abs(z) <= ItemTransform.Deserializer.MAX_SCALE, "Scale must be -4 <= z <= 4");
            this.scale = new Vector3f(x, y, z);
            return this;
        }

        /// Specify the right (post-scale) rotation for this perspective.
        ///
        /// @param x The rotation around the X axis
        /// @param y The rotation around the Y axis
        /// @param z The rotation around the Z axis
        /// @return this builder
        public TransformBuilder rightRotation(float x, float y, float z) {
            this.rightRotation = new Vector3f(x, y, z);
            return this;
        }

        /// {@return the built item transform entry}
        private ItemTransform build() {
            return new ItemTransform(rotation, translation, scale, rightRotation);
        }
    }
}
