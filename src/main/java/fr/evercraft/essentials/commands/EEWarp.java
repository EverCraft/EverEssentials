/**
 * This file is part of EverEssentials.
 *
 * EverEssentials is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverEssentials is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverEssentials.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.warp.LocationSQL;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWarp extends ECommand<EverEssentials> {
	
	private boolean permission;
	
	public EEWarp(final EverEssentials plugin) {
        super(plugin, "warp", "warps");
        
        reload();
    }
	
	public void reload() {
		this.permission = this.plugin.getConfigs().get("warp-permission").getBoolean(true);
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("WARP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("WARP_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/warp [name [joueur]]").onClick(TextActions.suggestCommand("/warp "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source instanceof Player){
			for(String warp : this.plugin.getManagerServices().getWarp().getWarps().keySet()){
				suggests.add(warp);
			}
		} else if(args.size() == 2 && source.hasPermission(this.plugin.getPermissions().get("WARP_OTHERS"))) {
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du warp inconnu
		if (args.size() == 0) {
			resultat = commandWarpList(source);
		// Nom du warp connu
		} else if(args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandWarpTeleport((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if(args.size() == 2) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("WARP_OTHERS"))){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandWarpTeleportOthers(source, optPlayer.get(), args.get(1));
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(this.plugin.getPermissions().noPermission());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandWarpList(final CommandSource player) throws CommandException {
		TreeMap<String, LocationSQL> warps = new TreeMap<String, LocationSQL>(this.plugin.getManagerServices().getWarp().getAllWarps());
		
		List<Text> lists = new ArrayList<Text>();
		if(player.hasPermission(this.plugin.getPermissions().get("DELWARP"))) {
			for (Entry<String, LocationSQL> warp : warps.entrySet()) {
				if(hasPermission(player, warp.getKey())) {
					Optional<World> world = warp.getValue().getWorld();
					if(world.isPresent()){
						lists.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WARP_LIST_LINE_DELETE"))
							.replace("<warp>", getButtonWarp(warp.getKey(), warp.getValue()))
							.replace("<teleport>", getButtonTeleport(warp.getKey(), warp.getValue()))
							.replace("<delete>", getButtonDelete(warp.getKey(), warp.getValue()))
							.build());
					} else {
						lists.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WARP_LIST_LINE_DELETE_ERROR_WORLD"))
								.replace("<warp>", getButtonWarp(warp.getKey(), warp.getValue()))
								.replace("<delete>", getButtonDelete(warp.getKey(), warp.getValue()))
								.build());
					}
				}
			}
		} else {
			for (Entry<String, LocationSQL> warp : warps.entrySet()) {
				if(hasPermission(player, warp.getKey())) {
					Optional<World> world = warp.getValue().getWorld();
					if(world.isPresent()){
						lists.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WARP_LIST_LINE"))
							.replace("<warp>", getButtonWarp(warp.getKey(), warp.getValue()))
							.replace("<teleport>", getButtonTeleport(warp.getKey(), warp.getValue()))
							.build());
					}
				}
			}
		}
		
		if(lists.size() == 0) {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("WARP_EMPTY")));
		} else {
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(this.plugin.getMessages().getText("WARP_LIST_TITLE").toBuilder()
					.onClick(TextActions.runCommand("/warp")).build(), lists, player);
		}			
		return false;
	}
	
	public boolean commandWarpTeleport(final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().getWarp(name);
		// Le serveur a un warp qui porte ce nom
		if(warp.isPresent()) {
			if(hasPermission(player, name)) {
				// Le joueur a bien été téléporter au warp
				if(player.teleportSafe(warp.get())){
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WARP_TELEPORT_PLAYER"))
							.replace("<warp>", getButtonWarp(name, warp.get()))
							.build());
					return true;
				// Erreur lors de la téléportation du joueur
				} else {
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WARP_TELEPORT_PLAYER_ERROR"))
							.replace("<warp>", getButtonWarp(name, warp.get()))
							.build());
				}
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("WARP_NO_PERMISSION")
						.replaceAll("<warp>", name));
			}
		// Le serveur n'a pas de warp qui porte ce nom
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("WARP_INCONNU")
					.replaceAll("<warp>", name));
		}
		return false;
	}
	
	public boolean commandWarpTeleportOthers(final CommandSource staff, final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().getWarp(name);
		// Le serveur a un warp qui porte ce nom
		if(warp.isPresent()) {
			// Le joueur a bien été téléporter au warp
			if(player.teleportSafe(warp.get())){
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("WARP_TELEPORT_OTHERS_PLAYER")
								.replaceAll("<staff>", staff.getName()))
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
				staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("WARP_TELEPORT_OTHERS_STAFF")
								.replaceAll("<player>", player.getName()))
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
				return true;
			// Erreur lors de la téléportation du joueur
			} else {
				staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("WARP_TELEPORT_OTHERS_ERROR"))
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
			}
		// Le serveur n'a pas de warp qui porte ce nom
		} else {
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("WARP_INCONNU")
					.replaceAll("<warp>", name)));
		}
		return false;
	}
	
	public Text getButtonTeleport(final String name, final LocationSQL location){
		return this.plugin.getMessages().getText("WARP_LIST_TELEPORT").toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WARP_LIST_TELEPORT_HOVER")
							.replaceAll("<warp>", name))))
					.onClick(TextActions.runCommand("/warp \"" + name + "\""))
					.build();
	}
	
	public Text getButtonDelete(final String name, final LocationSQL location){
		return this.plugin.getMessages().getText("WARP_LIST_DELETE").toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WARP_LIST_DELETE_HOVER")
							.replaceAll("<warp>", name))))
					.onClick(TextActions.runCommand("/delwarp \"" + name + "\""))
					.build();
	}
	
	public Text getButtonWarp(final String name, final LocationSQL location){
		return EChat.of(this.plugin.getMessages().getMessage("WARP_NAME").replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WARP_NAME_HOVER")
							.replaceAll("<warp>", name)
							.replaceAll("<world>", location.getWorldName())
							.replaceAll("<x>", location.getX().toString())
							.replaceAll("<y>", location.getY().toString())
							.replaceAll("<z>", location.getZ().toString()))))
					.build();
	}
	
	public Text getButtonWarp(final String name, final Transform<World> location){
		return EChat.of(this.plugin.getMessages().getMessage("WARP_NAME").replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WARP_NAME_HOVER")
							.replaceAll("<warp>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
	
	private boolean hasPermission(CommandSource player, String warp) {
		return (!this.permission || player.hasPermission(this.plugin.getPermissions().get("DELWARP") + "." + warp));
	}
}
