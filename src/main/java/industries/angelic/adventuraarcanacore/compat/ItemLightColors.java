package industries.angelic.adventuraarcanacore.compat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import industries.angelic.adventuraarcanacore.Config;

/**
 * The itemLightColors option: dynamic light colors for items that aren't blocks (blocks take Supernova's), as
 * "modid:item=r,g,b" for every metadata or "modid:item:meta=r,g,b" for one, each channel 0 to 15.
 */
public final class ItemLightColors {

    /** By registry name, and by registry name and metadata ("name@meta"). */
    private static volatile Map<String, int[]> colors = new HashMap<>();

    private ItemLightColors() {}

    /** Takes the entries Config read; bad ones are skipped. */
    public static void load(String[] entries) {
        Map<String, int[]> parsed = new HashMap<>();
        for (String entry : entries) {
            Entry e = Entry.parse(entry);
            if (e != null) parsed.put(e.key(), e.rgb);
        }
        colors = parsed;
    }

    /** The color set for this item at this damage, or for all of its damage values, or null. */
    public static int[] get(String name, int damage) {
        Map<String, int[]> current = colors;
        int[] own = current.get(name + "@" + damage);
        return own != null ? own : current.get(name);
    }

    /** Whether this exact entry (one metadata, or all below 0) is set. */
    public static boolean has(String name, int meta) {
        return colors.containsKey(new Entry(name, meta, null).key());
    }

    /** Sets or, with a null color, removes an entry, and saves the config. */
    public static void set(String name, int meta, int[] rgb) {
        String key = new Entry(name, meta, null).key();
        List<String> kept = new ArrayList<>();
        for (String entry : Config.itemLightColors) {
            Entry e = Entry.parse(entry);
            if (e == null || !e.key()
                .equals(key)) kept.add(entry);
        }
        if (rgb != null) kept.add(new Entry(name, meta, rgb).toString());
        Config.setItemLightColors(kept.toArray(new String[0]));
    }

    public static final class Entry {

        public final String name;
        /** Below 0 for every metadata. */
        public final int meta;
        public final int[] rgb;

        public Entry(String name, int meta, int[] rgb) {
            this.name = name;
            this.meta = meta;
            this.rgb = rgb;
        }

        String key() {
            return meta < 0 ? name : name + "@" + meta;
        }

        /** "modid:item=r,g,b" or "modid:item:meta=r,g,b", or null if it isn't one. */
        public static Entry parse(String entry) {
            int equals = entry.indexOf('=');
            if (equals <= 0) return null;
            String target = entry.substring(0, equals)
                .trim();
            String[] channels = entry.substring(equals + 1)
                .split(",");
            if (channels.length != 3) return null;
            int[] rgb = new int[3];
            try {
                for (int i = 0; i < 3; i++) rgb[i] = Math.max(0, Math.min(15, Integer.parseInt(channels[i].trim())));
                int meta = -1;
                int last = target.lastIndexOf(':');
                if (last > 0 && target.indexOf(':') != last) {
                    meta = Integer.parseInt(target.substring(last + 1));
                    target = target.substring(0, last);
                }
                return new Entry(target, meta, rgb);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return (meta < 0 ? name : name + ":" + meta) + "=" + rgb[0] + "," + rgb[1] + "," + rgb[2];
        }
    }
}
