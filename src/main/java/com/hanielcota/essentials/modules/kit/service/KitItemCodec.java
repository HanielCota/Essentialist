package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.shared.Log;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes kit item lists to/from Base64 so they can be stored as plain strings in {@code
 * kits.yml}. Uses Paper's byte serialization, which preserves the full item (components, enchants,
 * custom names, NBT) — config-friendly material lists could not.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KitItemCodec {

  private static final Log LOG = Log.of(KitItemCodec.class);
  private static final Base64.Encoder ENCODER = Base64.getEncoder();
  private static final Base64.Decoder DECODER = Base64.getDecoder();

  /** Encodes non-empty items to Base64 strings, skipping air/empty slots. */
  public static List<String> encode(@NonNull List<ItemStack> items) {
    var encoded = new ArrayList<String>(items.size());

    for (var item : items) {
      if (item == null || item.getType().isAir()) {
        continue;
      }

      var bytes = item.serializeAsBytes();
      encoded.add(ENCODER.encodeToString(bytes));
    }

    return List.copyOf(encoded);
  }

  /** Decodes Base64 strings back to items, skipping any entry that fails to deserialize. */
  public static List<ItemStack> decode(@NonNull List<String> encoded) {
    var items = new ArrayList<ItemStack>(encoded.size());

    for (var entry : encoded) {
      var item = decodeOne(entry);
      if (item == null) {
        continue;
      }

      items.add(item);
    }

    return List.copyOf(items);
  }

  /**
   * Encodes a fixed set of slots positionally, using {@code ""} for empties (positions preserved).
   */
  public static List<String> encodePositional(@NonNull ItemStack[] slots) {
    var encoded = new ArrayList<String>(slots.length);

    for (var item : slots) {
      if (item == null || item.getType().isAir()) {
        encoded.add("");
        continue;
      }

      var bytes = item.serializeAsBytes();
      encoded.add(ENCODER.encodeToString(bytes));
    }

    return List.copyOf(encoded);
  }

  /**
   * Decodes a positional list to exactly {@code size} entries; empty/blank slots become {@code
   * null}.
   */
  public static List<ItemStack> decodePositional(@NonNull List<String> encoded, int size) {
    var items = new ArrayList<ItemStack>(size);

    for (var index = 0; index < size; index++) {
      var entry = index < encoded.size() ? encoded.get(index) : "";
      items.add(entry.isBlank() ? null : decodeOne(entry));
    }

    return Collections.unmodifiableList(items);
  }

  private static ItemStack decodeOne(@NonNull String entry) {
    try {
      var bytes = DECODER.decode(entry);
      return ItemStack.deserializeBytes(bytes);
    } catch (RuntimeException e) {
      LOG.warn(e, "Skipping a kit item that failed to deserialize");
      return null;
    }
  }
}
