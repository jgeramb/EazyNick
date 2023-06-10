package com.justixdev.eazynick.nms.guis;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class SignGUI implements Listener {

    private final EazyNick eazyNick;

    @Getter
    private final Map<Player, Block> blocks = new HashMap<>();
    @Getter
    private final Map<Player, Material> oldTypes = new HashMap<>();
    @Getter
    private final Map<Player, EditCompleteListener> editCompleteListeners = new HashMap<>();

    public SignGUI(EazyNick eazyNick) {
        this.eazyNick = eazyNick;
    }

    public void open(Player player,
                     String line1,
                     String line2,
                     String line3,
                     String line4,
                     EditCompleteListener editCompleteListener) {
        boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                is1_18 = NMS_VERSION.startsWith("v1_18"),
                is1_19 = NMS_VERSION.startsWith("v1_19"),
                is1_20 = NMS_VERSION.startsWith("v1_20");
        Block block = player.getWorld().getBlockAt(player.getLocation().clone().add(0, 250 - player.getLocation().getBlockY(), 0));

        this.blocks.put(player, block);
        this.oldTypes.put(player, block.getType());
        this.editCompleteListeners.put(player, editCompleteListener);

        block.setType(Objects.requireNonNull(Material.getMaterial(
                VERSION_13_OR_LATER && !NMS_VERSION.startsWith("v1_13")
                        ? "OAK_SIGN"
                        : NMS_VERSION.startsWith("v1_13")
                                ? "SIGN"
                                : "SIGN_POST"
        )));

        Bukkit.getScheduler().runTaskLater(this.eazyNick, () -> {
            Sign sign = (Sign) block.getState();

            if(is1_20) {
                SignSide front = sign.getSide(Side.FRONT);
                front.setLine(0, line1);
                front.setLine(1, line2);
                front.setLine(2, line3);
                front.setLine(3, line4);
            } else {
                sign.setLine(0, line1);
                sign.setLine(1, line2);
                sign.setLine(2, line3);
                sign.setLine(3, line4);
            }

            sign.update(false, false);

            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(currentPlayer -> (currentPlayer != player))
                    .forEach(currentPlayer -> {
                        if(is1_18 || is1_19)
                            currentPlayer.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
                        else {
                            try {
                                invoke(
                                        currentPlayer,
                                        "sendBlockChange",
                                        types(Location.class, Material.class, byte.class),
                                        block.getLocation(),
                                        Material.AIR,
                                        (byte) 0
                                );
                            } catch (Exception ignore) {
                            }
                        }
                    });

            new AsyncTask(new AsyncRunnable() {

                @Override
                public void run() {
                    try {
                        boolean useCraftBlockEntityState = VERSION_13_OR_LATER
                                || Bukkit.getVersion().contains("1.12.2")
                                || Bukkit.getVersion().contains("1.12.1");
                        Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                        Object tileSign = getFieldValue(sign, useCraftBlockEntityState ? "tileEntity" : "sign");

                        setField(
                                tileSign,
                                is1_20
                                        ? "g"
                                        : NMS_VERSION.equals("v1_19_R2") || NMS_VERSION.equals("v1_19_R3")
                                                ? "h"
                                                : is1_17 || is1_18 || is1_19
                                                        ? "f"
                                                        : "isEditable",
                                !is1_20
                        );

                        setField(
                                tileSign,
                                is1_20
                                        ? "d"
                                        : NMS_VERSION.equals("v1_19_R2") || NMS_VERSION.equals("v1_19_R3")
                                                ? "i"
                                                : NMS_VERSION.startsWith("v1_13") || is1_17 || is1_18 || is1_19
                                                        ? "g"
                                                        : NMS_VERSION.startsWith("v1_15") || NMS_VERSION.startsWith("v1_16")
                                                                ? "c"
                                                                : NMS_VERSION.startsWith("v1_14")
                                                                        ? "j"
                                                                        : "h",
                                is1_17 || is1_18 || is1_19 || is1_20
                                        ? player.getUniqueId()
                                        : entityPlayer
                        );

                        Class<?> blockPositionClass = getNMSClass(
                                is1_17 || is1_18 || is1_19 || is1_20
                                        ? "core.BlockPosition"
                                        : "BlockPosition"
                        );

                        sendPacketNMS(
                                player,
                                is1_20
                                        ?  newInstance(
                                                getNMSClass("network.protocol.game.PacketPlayOutOpenSignEditor"),
                                                types(blockPositionClass, boolean.class),
                                                newInstance(
                                                        blockPositionClass,
                                                        types(int.class, int.class, int.class),
                                                        sign.getX(),
                                                        sign.getY(),
                                                        sign.getZ()
                                                ),
                                                true
                                        )
                                        : newInstance(
                                                getNMSClass(
                                                        is1_17 || is1_18 || is1_19
                                                                ? "network.protocol.game.PacketPlayOutOpenSignEditor"
                                                                : "PacketPlayOutOpenSignEditor"
                                                ),
                                                types(blockPositionClass),
                                                newInstance(
                                                        blockPositionClass,
                                                        NMS_VERSION.equals("v1_19_R3")
                                                                ? types(int.class, int.class, int.class)
                                                                : types(double.class, double.class, double.class),
                                                        sign.getX(),
                                                        sign.getY(),
                                                        sign.getZ()
                                                )
                                        )
                        );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 50L * 3).run();
        }, NMS_VERSION.startsWith("v1_16") || NMS_VERSION.startsWith("v1_17")
                ? 4
                : 2
        );
    }

    public interface EditCompleteListener {

        void onEditComplete(EditCompleteEvent event);

    }

    @AllArgsConstructor
    public static class EditCompleteEvent {

        @Getter
        private final String[] lines;

    }

}