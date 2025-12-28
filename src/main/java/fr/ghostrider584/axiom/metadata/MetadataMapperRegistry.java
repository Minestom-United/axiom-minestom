package fr.ghostrider584.axiom.metadata;

import fr.ghostrider584.axiom.metadata.entity.DisplayMetaCodecs;
import fr.ghostrider584.axiom.metadata.entity.EntityMetaCodec;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.entity.MetadataDef;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.animal.PigMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.registry.Registries;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static fr.ghostrider584.axiom.metadata.MetadataMappers.*;
import static fr.ghostrider584.axiom.metadata.MetadataMapping.*;

public final class MetadataMapperRegistry {
	private static final Map<Class<? extends EntityMeta>, MetadataMapper<?>> REGISTRY = new ConcurrentHashMap<>();
	private static final Map<Class<? extends EntityMeta>, MetadataMapper<?>> LOOKUP_CACHE = new ConcurrentHashMap<>();

	static {
		register(EntityMeta.class, EntityMetaCodec.ENTITY);
		register(ItemDisplayMeta.class, DisplayMetaCodecs.ITEM_DISPLAY);
		register(BlockDisplayMeta.class, DisplayMetaCodecs.BLOCK_DISPLAY);
		register(TextDisplayMeta.class, DisplayMetaCodecs.TEXT_DISPLAY);

		register(PigMeta.class, extend(EntityMetaCodec.ENTITY,
				registry("variant", MetadataDef.Pig.VARIANT, Registries::pigVariant)));
	}

	public static <T extends EntityMeta> void register(Class<T> metaClass, MetadataMapper<T> codec) {
		REGISTRY.put(metaClass, codec);
		LOOKUP_CACHE.clear();
	}

	@SuppressWarnings("unchecked")
	public static <T extends EntityMeta> MetadataMapper<T> get(Class<T> metaClass) {
		return (MetadataMapper<T>) LOOKUP_CACHE.computeIfAbsent(metaClass, MetadataMapperRegistry::findCodec);
	}

	private static MetadataMapper<?> findCodec(Class<? extends EntityMeta> metaClass) {
		final var direct = REGISTRY.get(metaClass);
		if (direct != null) {
			return direct;
		}

		// walk class hierarchy to find most specific match
		MetadataMapper<?> bestMatch = null;
		Class<?> bestMatchClass = null;

		for (final var entry : REGISTRY.entrySet()) {
			final var registeredClass = entry.getKey();
			if (registeredClass.isAssignableFrom(metaClass)) {
				if (bestMatchClass == null || bestMatchClass.isAssignableFrom(registeredClass)) {
					bestMatch = entry.getValue();
					bestMatchClass = registeredClass;
				}
			}
		}

		if (bestMatch != null) {
			return bestMatch;
		}

		throw new IllegalArgumentException("No codec found for " + metaClass);
	}

	@SuppressWarnings("unchecked")
	public static <T extends EntityMeta> void applyNBT(T meta, CompoundBinaryTag nbt) {
		final var codec = (MetadataMapper<T>) get(meta.getClass());
		codec.applyFromNBT(meta, nbt);
	}

	@SuppressWarnings("unchecked")
	public static <T extends EntityMeta> CompoundBinaryTag toNBT(T meta) {
		final var codec = (MetadataMapper<T>) get(meta.getClass());
		return codec.toNBT(meta);
	}
}