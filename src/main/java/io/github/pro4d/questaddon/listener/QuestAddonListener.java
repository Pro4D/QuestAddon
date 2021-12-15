package io.github.pro4d.questaddon.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.pro4d.questaddon.QuestAddon;
import io.github.pro4d.questaddon.util.QAUtils;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("ConstantConditions")
public class QuestAddonListener implements Listener {

    private final QAUtils qaUtils;
    public QuestAddonListener(QuestAddon plugin) {
        qaUtils = plugin.getQaUtils();

        ProtocolManager manager = plugin.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                int window = packet.getIntegers().read(0);
                int slot = packet.getIntegers().read(2);

                if(window == 0 && slot == 5) {
                    ItemStack item = packet.getItemModifier().read(0);
                    Player player = event.getPlayer();

                    if(item.getType().equals(QuestAddon.pumpkinType)) {
                        if(qaUtils.getPumpkinMap().containsKey(player)) {
                            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
                            if (nmsItem.hasTag()) {
                                if (nmsItem.getTag().getString(qaUtils.getCustomItemTag()).equalsIgnoreCase(qaUtils.getTagData())) {

                                    CraftPlayer craftPlayer = (CraftPlayer) player;
                                    CraftInventoryView craftInventory = (CraftInventoryView) craftPlayer.getOpenInventory();
                                    int stateId = craftInventory.getHandle().incrementStateId();

                                    net.minecraft.world.item.ItemStack temp = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));

                                    PacketPlayOutSetSlot cursorPacket = new PacketPlayOutSetSlot(-1, stateId, -1, temp);
                                    craftPlayer.getHandle().b.sendPacket(cursorPacket);

                                    qaUtils.pumpkinPacket(player);
                                    event.setCancelled(true);
                                }
                            }
                        }

                    }
                }
            }
        });

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(qaUtils.mapContainsCheck(event.getPlayer())) {
            qaUtils.pumpkinHead(event.getPlayer(), qaUtils.getPumpkinMap().get(event.getPlayer()));
        }
    }

}
