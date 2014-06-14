package net.diamonddominion.esaych.util;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;

import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Class, that allows adding glow effect to item.
 */
public class EnchantGlow {
    
    /**
     * Adds the glow.
     * 
     * @param item
     *            the item
     * @return the item stack
     */
    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_6_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
    
    /**
     * Removes the glow.
     * 
     * @param item
     *            the item
     * @return the item stack
     */
    public static ItemStack removeGlow(ItemStack item) {
        net.minecraft.server.v1_6_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) return item;
        tag = nmsStack.getTag();
        tag.set("ench", null);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
}