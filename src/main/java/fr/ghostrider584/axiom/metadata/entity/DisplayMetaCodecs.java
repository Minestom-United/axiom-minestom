package fr.ghostrider584.axiom.metadata.entity;

import fr.ghostrider584.axiom.metadata.ExtraCodecs;
import fr.ghostrider584.axiom.metadata.MetadataMapper;
import fr.ghostrider584.axiom.metadata.MetadataMapperBuilder;
import fr.ghostrider584.axiom.metadata.common.BlockStateCodec;
import fr.ghostrider584.axiom.metadata.common.CommonCodecs;
import net.minestom.server.codec.Codec;
import net.minestom.server.entity.MetadataDef;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.item.ItemStack;

public final class DisplayMetaCodecs {

	public static final Codec<Byte> BILLBOARD = ExtraCodecs.EnumOrdinal(AbstractDisplayMeta.BillboardConstraints.class);
	public static final Codec<Byte> ALIGNMENT = ExtraCodecs.EnumOrdinal(TextDisplayMeta.Alignment.class);

	public static final Codec<Byte> ITEM_DISPLAY_TYPE = ExtraCodecs.StringToByte(
			"none", "thirdperson_lefthand", "thirdperson_righthand", "firstperson_lefthand",
			"firstperson_righthand", "head", "gui", "ground", "fixed"
	);

	public static final MetadataMapper<AbstractDisplayMeta> DISPLAY = MetadataMapperBuilder.<AbstractDisplayMeta>create()
			.mapping("billboard", MetadataDef.Display.BILLBOARD_CONSTRAINTS, BILLBOARD)
			.mapping("transformation.scale", MetadataDef.Display.SCALE, Codec.VECTOR3D)
			.mapping("transformation.translation", MetadataDef.Display.TRANSLATION, Codec.VECTOR3D)
			.mapping("transformation.left_rotation", MetadataDef.Display.ROTATION_LEFT, CommonCodecs.QUATERNION)
			.mapping("transformation.right_rotation", MetadataDef.Display.ROTATION_RIGHT, CommonCodecs.QUATERNION)
			.direct("interpolation_duration", MetadataDef.Display.TRANSFORMATION_INTERPOLATION_DURATION)
			.direct("teleport_duration", MetadataDef.Display.POSITION_ROTATION_INTERPOLATION_DURATION)
			.direct("glow_color_override", MetadataDef.Display.GLOW_COLOR_OVERRIDE)
			.direct("brightness_override", MetadataDef.Display.BRIGHTNESS_OVERRIDE)
			.direct("shadow_strength", MetadataDef.Display.SHADOW_STRENGTH)
			.direct("shadow_radius", MetadataDef.Display.SHADOW_RADIUS)
			.direct("view_range", MetadataDef.Display.VIEW_RANGE)
			.direct("width", MetadataDef.Display.WIDTH)
			.direct("height", MetadataDef.Display.HEIGHT)
			.extend(EntityMetaCodec.ENTITY);

	public static final MetadataMapper<BlockDisplayMeta> BLOCK_DISPLAY = MetadataMapperBuilder.<BlockDisplayMeta>create()
			.mapping("block_state", MetadataDef.BlockDisplay.DISPLAYED_BLOCK_STATE, BlockStateCodec.CODEC)
			.extend(DISPLAY);

	public static final MetadataMapper<ItemDisplayMeta> ITEM_DISPLAY = MetadataMapperBuilder.<ItemDisplayMeta>create()
			.mapping("item", MetadataDef.ItemDisplay.DISPLAYED_ITEM, ItemStack.CODEC)
			.mapping("item_display", MetadataDef.ItemDisplay.DISPLAY_TYPE, ITEM_DISPLAY_TYPE)
			.extend(DISPLAY);

	public static final MetadataMapper<TextDisplayMeta> TEXT_DISPLAY = MetadataMapperBuilder.<TextDisplayMeta>create()
			.mapping("text", MetadataDef.TextDisplay.TEXT, Codec.COMPONENT)
			.mapping("alignment", MetadataDef.TextDisplay.ALIGNMENT, ALIGNMENT)
			.direct("default_background", MetadataDef.TextDisplay.USE_DEFAULT_BACKGROUND_COLOR)
			.direct("background", MetadataDef.TextDisplay.BACKGROUND_COLOR)
			.direct("see_through", MetadataDef.TextDisplay.IS_SEE_THROUGH)
			.direct("text_opacity", MetadataDef.TextDisplay.TEXT_OPACITY)
			.direct("line_width", MetadataDef.TextDisplay.LINE_WIDTH)
			.direct("shadow", MetadataDef.TextDisplay.HAS_SHADOW)
			.extend(DISPLAY);
}