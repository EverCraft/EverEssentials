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
package fr.evercraft.essentials.command.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWorlds extends ECommand<EverEssentials> {

	public EEWorlds(final EverEssentials plugin) {
		super(plugin, "worlds", "world");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDS.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.WORLDS_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.WORLDS_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.get() + " [" + EAMessages.ARGS_PLAYER.get() + "]]")
						.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
						.color(TextColors.RED)
						.build();
		} 
		return Text.builder("/" + this.getName() + "  [" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		} else if(args.size() == 2 && source.hasPermission(EEPermissions.WORLDS_OTHERS.get())) {
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if (args.size() == 0) {
			resultat = commandWorldList(source);
		} else if (args.size() == 1) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandWorldTeleport((EPlayer) source, args.get(0));
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WORLDS_OTHERS.get())){
				// Si la source est bien un joueur
				if(source instanceof EPlayer) {
					EPlayer player = (EPlayer) source;
					Optional<EPlayer> optTarget = this.plugin.getEServer().getEPlayer(args.get(1));
					// Le joueur existe
					if(optTarget.isPresent()){
						if(!player.equals(optTarget.get())){
							resultat = commandWorldTeleportOthers((EPlayer) source, optTarget.get(), args.get(0));
						} else {
							args.remove(args.size() - 1);
							return execute(player, args);
						}
					// Le joueur est introuvable
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
					}
				// Si la source est une console ou un commande block
				} else {
					source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandWorldList(final CommandSource player) {
		List<Text> lists = new ArrayList<Text>();
		for (World world : this.plugin.getEServer().getWorlds()) {
			if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world)) {
				lists.add(ETextBuilder.toBuilder(EEMessages.WORLDS_LIST_LINE.get()
						.replaceAll("<world>", world.getName()))
					.replace("<teleport>", getButtonTeleport(world.getName(), world.getUniqueId()))
					.build());
			}
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.WORLDS_LIST_TITLE.getText().toBuilder()
				.onClick(TextActions.runCommand("/worlds")).build(), lists, player);
		return false;
	}
	
	public boolean commandWorldTeleport(final EPlayer player, final String world_name) {
		Optional<World> optWorld = this.plugin.getEServer().getEWorld(world_name);
		if(optWorld.isPresent()) {
			if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, optWorld.get())) {
				if(player.teleport(optWorld.get().getSpawnLocation())) {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WORLDS_TELEPORT_PLAYER.get())
							.replace("<world>", getButtonPosition(player.getLocation()))
							.build());
					return true;
				} else {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WORLDS_TELEPORT_PLAYER_ERROR.get())
							.replace("<world>", getButtonPosition(optWorld.get().getSpawnLocation()))
							.build());
				}
			} else {
				player.sendMessage(EAMessages.NO_PERMISSION_WORLD.getText());
			}
		// Monde introuvable
		} else {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EAMessages.WORLD_NOT_FOUND.get())
					.replace("<world>", getButtonPosition(optWorld.get().getSpawnLocation()))
					.build());
		}
		return false;
	}
	
	public boolean commandWorldTeleportOthers(final CommandSource staff, final EPlayer player, String world_name) {
		Optional<World> optWorld = this.plugin.getEServer().getWorld(world_name);
		if(optWorld.isPresent()) {
			if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, optWorld.get())) {
				if(player.teleportSafe(optWorld.get().getSpawnLocation())) {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WORLDS_TELEPORT_OTHERS_PLAYER.get()
									.replaceAll("<staff>", staff.getName()))
							.replace("<world>", getButtonPosition(player.getLocation()))
							.build());
					staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WORLDS_TELEPORT_OTHERS_STAFF.get()
									.replaceAll("<player>", player.getName()))
							.replace("<world>", getButtonPosition(player.getLocation()))
							.build());
					return true;
				} else {
					staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.WORLDS_TELEPORT_OTHERS_ERROR.get()
									.replaceAll("<player>", player.getDisplayName()))
							.replace("<world>", getButtonPosition(optWorld.get().getSpawnLocation()))
							.build());
				}
			} else {
				staff.sendMessage(EAMessages.NO_PERMISSION_WORLD_OTHERS.getText());
			}
		// Monde introuvable
		} else {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EAMessages.WORLD_NOT_FOUND.get())
					.replace("<world>", getButtonPosition(optWorld.get().getSpawnLocation()))
					.build());
		}
		return false;
	}
	
	public Text getButtonTeleport(final String name, final UUID uuid){
		return EEMessages.WORLDS_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WORLDS_LIST_TELEPORT_HOVER.get()
							.replaceAll("<world>", name))))
					.onClick(TextActions.runCommand("/worlds \"" + uuid + "\""))
					.build();
	}
	
	public Text getButtonPosition(final Location<World> location){
		return EChat.of(EEMessages.WORLDS_TELEPORT_WORLD.get()
				.replaceAll("<world>", location.getExtent().getName())).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WORLDS_TELEPORT_WORLD_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}
