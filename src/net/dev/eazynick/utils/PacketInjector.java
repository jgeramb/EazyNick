package net.dev.eazynick.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketInjector {

	public void init() {
		EazyNick eazyNick = EazyNick.getInstance();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();

		Field field = reflectUtils.getFirstFieldByType(reflectUtils.getNMSClass("NetworkManager"), Channel.class);
		field.setAccessible(true);
		
		try {
			Object craftServer = Bukkit.getServer();
			Field dedicatedServer = reflectUtils.getCraftClass("CraftServer").getDeclaredField("console");
			dedicatedServer.setAccessible(true);
			
			Object minecraftServer = dedicatedServer.get(craftServer);
			
			for(Object manager : Collections.synchronizedList((List<?>) getNetworkManagerList(minecraftServer.getClass().getMethod("getServerConnection").invoke(minecraftServer))).toArray()) {
				Channel channel = (Channel) field.get(manager);
				
				if((channel.pipeline().context("packet_handler") != null)) {
					if(channel.pipeline().context("chat_handler") != null)
						channel.pipeline().remove("chat_handler");
					
					channel.pipeline().addBefore("packet_handler", "chat_handler", new ChannelDuplexHandler() {
						
						@Override
						public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
							if (msg.getClass().getSimpleName().equals("PacketPlayOutChat"))
								super.write(ctx, constructChatPacket(msg), promise);
							else
								super.write(ctx, msg, promise);
						}
						
						@Override
					    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
					    }
						
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object getNetworkManagerList(Object serverConnection) {
		try {
			List<Field> fields = Arrays.asList(serverConnection.getClass().getDeclaredFields());
			Collections.reverse(fields);
			
			for(Field field : fields) {
				field.setAccessible(true);
				
				if(field.getType() == List.class)
					return field.get(serverConnection);
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Object constructChatPacket(Object packet) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		String version = eazyNick.getVersion();
		String lastChatMessage = utils.getLastChatMessage();
		String prefix = utils.getPrefix();
		
		try {
			Field field = packet.getClass().getDeclaredField("a");
			field.setAccessible(true);

			Object iChatBaseComponent = field.get(packet);
			Object editedComponent = null;
			
			if(iChatBaseComponent != null) {
				Class<?> iChatBaseComponentClass = reflectUtils.getNMSClass("IChatBaseComponent");
				Class<?> chatSerializer = version.equals("1_8_R1") ? reflectUtils.getNMSClass("ChatSerializer") : iChatBaseComponentClass.getDeclaredClasses()[0];
				
				String fullText = "";
				Method method = iChatBaseComponentClass.getDeclaredMethod((Bukkit.getVersion().contains("1.14.4") || version.startsWith("1_15")) ? "getSiblings" : "a");
				method.setAccessible(true);
				
				for (Object partlyIChatBaseComponent : ((List<Object>) method.invoke(iChatBaseComponent))) {
					if(partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")) {
						String[] json = ((String) chatSerializer.getMethod("a", iChatBaseComponentClass).invoke(null, partlyIChatBaseComponent)).replace("\"", "").replace("{", "").replace("}", "").split(",");
						String text = "";
						String color = "";
						boolean obfuscated = false;
						boolean bold = false;
						boolean italic = false;
						boolean strikethrough = false;
						boolean underlined = false;
						
						for (String s : json) {
							if(s.startsWith("text:"))
								text = s.replaceFirst("text:", "");
							else if(s.equals("obfuscated:true"))
								obfuscated = true;
							else if(s.equals("bold:true"))
								bold = true;
							else if(s.equals("italic:true"))
								italic = true;
							else if(s.equals("strikethrough:true"))
								strikethrough = true;
							else if(s.equals("underlined:true"))
								underlined = true;
							else if(s.equals("color:black"))
								color = "§0";
							else if(s.equals("color:dark_blue"))
								color = "§1";
							else if(s.equals("color:dark_green"))
								color = "§2";
							else if(s.equals("color:dark_aqua"))
								color = "§3";
							else if(s.equals("color:dark_red"))
								color = "§4";
							else if(s.equals("color:dark_purple"))
								color = "§5";
							else if(s.equals("color:gold"))
								color = "§6";
							else if(s.equals("color:gray"))
								color = "§7";
							else if(s.equals("color:dark_gray"))
								color = "§8";
							else if(s.equals("color:blue"))
								color = "§9";
							else if(s.equals("color:green"))
								color = "§a";
							else if(s.equals("color:aqua"))
								color = "§b";
							else if(s.equals("color:red"))
								color = "§c";
							else if(s.equals("color:light_purple"))
								color = "§d";
							else if(s.equals("color:yellow"))
								color = "§e";
							else if(s.equals("color:white"))
								color = "§f";
							else if(s.equals("color:white"))
								color = "§f";
						}
						
						fullText += (color.isEmpty() ? "§r" : color) + (obfuscated ? "§k" : "") + (bold ? "§l" : "") + (italic ? "§o" : "") + (strikethrough ? "§m" : "") + (underlined ? "§n" : "") + text;
					}
				}
				
				if(!(fullText.contains(lastChatMessage) || fullText.startsWith(prefix))) {
					for (Object partlyIChatBaseComponent : ((List<Object>) method.invoke(iChatBaseComponent))) {
						if(partlyIChatBaseComponent.getClass().getSimpleName().equals("ChatComponentText")) {
							String json = (String) chatSerializer.getMethod("a", iChatBaseComponentClass).invoke(null, partlyIChatBaseComponent);
	
							if(json.startsWith("\""))
								json = "{\"color\":\"white\",\"obfuscated\":false,\"bold\":false,\"italic\":false,\"strikethrough\":false,\"underlined\":false,\"text\":" + json + "}";
							
							for (Player all : Bukkit.getOnlinePlayers()) {
								NickManager api = new NickManager(all);
								
								if(api.isNicked())
									json = json.replaceAll(api.getRealName(), api.getNickName());
							}
		
							Object newPartlyIChatBaseComponent = chatSerializer.getMethod("a", String.class).invoke(null, json);
							
							if(editedComponent == null)
								editedComponent = newPartlyIChatBaseComponent;
							else
								iChatBaseComponentClass.getMethod("addSibling", iChatBaseComponentClass).invoke(editedComponent, newPartlyIChatBaseComponent);
						}
					}
				}
			}
			
			if(editedComponent != null)
				reflectUtils.setField(packet, "a", editedComponent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
}
