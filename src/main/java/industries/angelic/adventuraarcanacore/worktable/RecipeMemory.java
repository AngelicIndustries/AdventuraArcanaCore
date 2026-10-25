/*
 * Adapted from Forestry's RecipeMemory and MemorizedRecipe.
 * Copyright (c) 2011-2014 SirSengir and contributors. LGPL-3.0.
 */
package industries.angelic.adventuraarcanacore.worktable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class RecipeMemory {

    public static final int CAPACITY = 9;
    private final List<Entry> entries = new ArrayList<>();
    private long sequence;

    public Entry get(int index) {
        return index >= 0 && index < entries.size() ? entries.get(index) : null;
    }

    public void remember(ItemStack[] pattern, ItemStack output) {
        if (output == null || output.getItem() instanceof ItemMap) return;
        Entry replacement = new Entry(pattern, output);
        replacement.lastUsed = ++sequence;
        for (int i = 0; i < entries.size(); i++) {
            Entry old = entries.get(i);
            if (ItemStack.areItemStacksEqual(old.output, output)) {
                replacement.locked = old.locked;
                entries.set(i, replacement);
                return;
            }
        }
        if (entries.size() == CAPACITY) {
            Entry oldest = null;
            for (Entry entry : entries)
                if (!entry.locked && (oldest == null || entry.lastUsed < oldest.lastUsed)) oldest = entry;
            if (oldest == null) return;
            entries.remove(oldest);
        }
        entries.add(replacement);
    }

    public void toggleLock(int index) {
        Entry entry = get(index);
        if (entry != null) entry.locked = !entry.locked;
    }

    public int lockMask() {
        int mask = 0;
        for (int i = 0; i < entries.size(); i++) if (entries.get(i).locked) mask |= 1 << i;
        return mask;
    }

    public NBTTagList write() {
        NBTTagList list = new NBTTagList();
        for (Entry entry : entries) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("Pattern", WorktableRecipes.writeStacks(entry.pattern));
            tag.setTag("Output", entry.output.writeToNBT(new NBTTagCompound()));
            tag.setBoolean("Locked", entry.locked);
            tag.setLong("LastUsed", entry.lastUsed);
            list.appendTag(tag);
        }
        return list;
    }

    public void read(NBTTagList list) {
        entries.clear();
        sequence = 0;
        for (int i = 0; i < Math.min(CAPACITY, list.tagCount()); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            ItemStack output = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Output"));
            if (output == null || output.stackSize <= 0) continue;
            Entry entry = new Entry(WorktableRecipes.readStacks(tag.getTagList("Pattern", 10), 9, true), output);
            entry.locked = tag.getBoolean("Locked");
            entry.lastUsed = Math.max(0, tag.getLong("LastUsed"));
            sequence = Math.max(sequence, entry.lastUsed);
            entries.add(entry);
        }
    }

    public static final class Entry {

        public final ItemStack[] pattern = new ItemStack[9];
        public final ItemStack output;
        public boolean locked;
        private long lastUsed;

        private Entry(ItemStack[] pattern, ItemStack output) {
            for (int i = 0; i < 9; i++) this.pattern[i] = WorktableRecipes.copy(pattern[i]);
            this.output = output.copy();
        }
    }
}
