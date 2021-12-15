package io.github.pro4d.questaddon.util;

import com.mojang.datafixers.util.Pair;
import io.github.pro4d.questaddon.QuestAddon;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class QAUtils {

    private final QuestAddon plugin;
    private final Map<Player, Integer> pumpkinMap;

    private final String tag;
    private final String data;
    final List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();

    public QAUtils(QuestAddon plugin) {
        this.plugin = plugin;
        tag = "QuestAddon";
        data = "fakePumpkinHead";
        ItemStack pumpkin = pumpkinItem();
        net.minecraft.world.item.ItemStack nmsPumpkin = CraftItemStack.asNMSCopy(pumpkin);

        NBTTagCompound pumpkinCompound = (nmsPumpkin.hasTag()) ? nmsPumpkin.getTag() : new NBTTagCompound();
        pumpkinCompound.set(tag, NBTTagString.a(data));
        nmsPumpkin.setTag(pumpkinCompound);

        list.add(new Pair<>(EnumItemSlot.f, nmsPumpkin));
        pumpkinMap = new HashMap<>();
    }

    public void pumpkinHead(Player victim, int time) {
        CraftPlayer craftVictim = (CraftPlayer) victim;
        PacketPlayOutEntityEquipment pumpkin = new PacketPlayOutEntityEquipment(craftVictim.getEntityId(), list);

        craftVictim.getHandle().b.sendPacket(pumpkin);
        ItemStack helemtSlot = null;

        if(Objects.requireNonNull(victim.getEquipment()).getHelmet() != null) {
            helemtSlot = victim.getEquipment().getHelmet();
        }

        if(mapContainsCheck(victim)) {
            pumpkinMap.replace(victim, time);
        } else {
            pumpkinMap.put(victim, time);
        }

        ItemStack finalHelemtSlot = helemtSlot;
        new BukkitRunnable() {
            int timeLeft = time;
            @Override
            public void run() {
                if(victim.isOnline()) {
                    timeLeft--;
                    if(timeLeft == 0) {
                        victim.getEquipment().setHelmet(finalHelemtSlot);
                        victim.updateInventory();
                        if(mapContainsCheck(victim)) {
                            pumpkinMap.remove(victim);
                        }
                        cancel();
                    }
                } else {

                    if(mapContainsCheck(victim)) {
                        pumpkinMap.replace(victim, timeLeft);
                    } else {
                        pumpkinMap.put(victim, timeLeft);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);


    }

    public void pumpkinPacket(Player victim) {
        CraftPlayer craftVictim = (CraftPlayer) victim;
        PacketPlayOutEntityEquipment pumpkin = new PacketPlayOutEntityEquipment(craftVictim.getEntityId(), list);

        craftVictim.getHandle().b.sendPacket(pumpkin);
    }

    public ItemStack pumpkinItem() {
        return new ItemStack(QuestAddon.pumpkinType, 1);
    }

    public boolean mapContainsCheck(Player player) {
        return pumpkinMap.containsKey(player);
    }

    public Map<Player, Integer> getPumpkinMap() {
        return pumpkinMap;
    }

    public String invalidArgs() {
        return ChatColor.translateAlternateColorCodes('&', "&cInvalid command usage!");
    }

    public String notOnline(String name) {
        return ChatColor.translateAlternateColorCodes('&',
                "&cCould not find a player with the name: &r" + name + "&c, online!");
    }

    public String getCustomItemTag() {
        return tag;
    }

    public String getTagData() {
        return data;
    }
}
