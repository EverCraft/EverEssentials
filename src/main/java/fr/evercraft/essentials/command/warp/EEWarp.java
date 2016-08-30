/*
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
package fr.evercraft.essentials.command.warp;

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

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWarp extends EReloadCommand<EverEssentials> {
	
	private boolean permission;
	
	public EEWarp(final EverEssentials plugin) {
        super(plugin, "warp", "warps");
        
        reload();
    }
	
	public void reload() {
		this.permission = this.plugin.getConfigs().get("warp-permission").getBoolean(true);
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WARP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.WARP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WARP.get() + "] [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source instanceof Player){
			suggests.addAll(this.plugin.getManagerServices().getWarp().getAll().keySet());
		} else if (args.size() == 2 && source.hasPermission(EEPermissions.WARP_OTHERS.get())) {
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
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandWarpTeleport((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WARP_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(1));
				// Le joueur existe
				if (optPlayer.isPresent()){
					resultat = commandWarpTeleportOthers(source, optPlayer.get(), args.get(0));
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandWarpList(final CommandSource player) throws CommandException {
		TreeMap<String, LocationSQL> warps = new TreeMap<String, LocationSQL>(this.plugin.getManagerServices().getWarp().getAllSQL());
		
		List<Text> lists = new ArrayList<Text>();
		if (player.hasPermission(EEPermissions.DELWARP.get())) {
			for (Entry<String, LocationSQL> warp : warps.entrySet()) {
				if (hasPermission(player, warp.getKey())) {
					Optional<World> world = warp.getValue().getWorld();
					if (world.isPresent()){
						lists.add(ETextBuilder.toBuilder(EEMessages.WARP_LIST_LINE_DELETE.get())
							.replace("<warp>", getButtonWarp(warp.getKey(), warp.getValue()))
							.replace("<teleport>", getButtonTeleport(warp.getKey(), warp.getValue()))
							.replace("<delete>", getButtonDelete(warp.getKey(), warp.getValue()))
							.build());
					} else {
						lists.add(ETextBuilder.toBuilder(EEMessages.WARP_LIST_LINE_DELETE_ERROR_WORLD.get())
								.replace("<warp>", getButtonWarp(warp.getKey(), warp.getValue()))
								.replace("<delete>", getButtonDelete(warp.getKey(), warp.getValue()))
								.build());
					}
				}
			}
		} else {
			for (Entry<String, LocationSQL> warp : warps.entrySet()) {
				if (hasPermission(player, warp.getKey())) {
					Optional<World> world = warp.getValue().getWorld();
					if (world.isPresent()){
						lists.add(ETextBuilder.toBuilder(EEMessages.WARP_LIST_LINE.get())
							.replace("<warp>", getButtonWarp(warp.getKey(), warp.getValue()))
							.replace("<teleport>", getButtonTeleport(warp.getKey(), warp.getValue()))
							.build());
					}
				}
			}
		}
		
		if (lists.size() == 0) {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WARP_EMPTY.get()));
		} else {
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.WARP_LIST_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/warp")).build(), lists, player);
		}			
		return false;
	}
	
	public boolean commandWarpTeleport(final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().get(name);
		// Le serveur a un warp qui porte ce nom
		if (warp.isPresent()) {
			if (hasPermission(player, name)) {
				// Le joueur a bien été téléporter au warp
				if (player.teleportSafe(warp.get())){
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WARP_TELEPORT_PLAYER.get())
							.replace("<warp>", getButtonWarp(name, warp.get()))
							.build());
					return true;
				// Erreur lors de la téléportation du joueur
				} else {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WARP_TELEPORT_PLAYER_ERROR.get())
							.replace("<warp>", getButtonWarp(name, warp.get()))
							.build());
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.WARP_NO_PERMISSION.get()
						.replaceAll("<warp>", name));
			}
		// Le serveur n'a pas de warp qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.WARP_INCONNU.get()
					.replaceAll("<warp>", name));
		}
		return false;
	}
	
	public boolean commandWarpTeleportOthers(final CommandSource staff, final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> warp = this.plugin.getManagerServices().getWarp().get(name);
		// Le serveur a un warp qui porte ce nom
		if (warp.isPresent()) {
			// Le joueur a bien été téléporter au warp
			if (player.teleportSafe(warp.get())){
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.WARP_TELEPORT_OTHERS_PLAYER.get()
								.replaceAll("<staff>", staff.getName()))
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
				staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.WARP_TELEPORT_OTHERS_STAFF.get()
								.replaceAll("<player>", player.getName()))
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
				return true;
			// Erreur lors de la téléportation du joueur
			} else {
				staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.WARP_TELEPORT_OTHERS_ERROR.get())
						.replace("<warp>", getButtonWarp(name, warp.get()))
						.build());
			}
		// Le serveur n'a pas de warp qui porte ce nom
		} else {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WARP_INCONNU.get()
					.replaceAll("<warp>", name)));
		}
		return false;
	}
	
	private Text getButtonTeleport(final String name, final LocationSQL location){
		return EEMessages.WARP_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WARP_LIST_TELEPORT_HOVER.get()
							.replaceAll("<warp>", name))))
					.onClick(TextActions.runCommand("/warp \"" + name + "\""))
					.build();
	}
	
	private Text getButtonDelete(final String name, final LocationSQL location){
		return EEMessages.WARP_LIST_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WARP_LIST_DELETE_HOVER.get()
							.replaceAll("<warp>", name))))
					.onClick(TextActions.runCommand("/delwarp \"" + name + "\""))
					.build();
	}
	
	private Text getButtonWarp(final String name, final LocationSQL location){
		return EChat.of(EEMessages.WARP_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WARP_NAME_HOVER.get()
							.replaceAll("<warp>", name)
							.replaceAll("<world>", location.getWorldName())
							.replaceAll("<x>", location.getX().toString())
							.replaceAll("<y>", location.getY().toString())
							.replaceAll("<z>", location.getZ().toString()))))
					.build();
	}
	
	private Text getButtonWarp(final String name, final Transform<World> location){
		return EChat.of(EEMessages.WARP_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WARP_NAME_HOVER.get()
							.replaceAll("<warp>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
	
	private boolean hasPermission(CommandSource player, String warp) {
		return (!this.permission || player.hasPermission(EEPermissions.WARP_NAME.get() + "." + warp));
	}
}
