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
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWorlds extends ECommand<EverEssentials> {

	public EEWorlds(final EverEssentials plugin) {
		super(plugin, "worlds");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDS.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("WORLDS_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.WORLDS_OTHERS.get())){
			return Text.builder("/worlds [monde [joueur]]").onClick(TextActions.suggestCommand("/worlds "))
					.color(TextColors.RED).build();
		} 
		return Text.builder("/worlds  [monde]").onClick(TextActions.suggestCommand("/worlds "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (source.hasPermission(EEPermissions.WORLDS_NAME.get() + "." + world.getName())) {
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
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WORLDS_OTHERS.get())){
				// Si la source est bien un joueur
				if(source instanceof EPlayer) {
					Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(1));
					// Le joueur existe
					if(optPlayer.isPresent()){
						resultat = commandWorldTeleportOthers((EPlayer) source, optPlayer.get(), args.get(0));
					// Le joueur est introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Si la source est une console ou un commande block
				} else {
					source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
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
			if(player.hasPermission(EEPermissions.WORLDS_NAME.get() + "." + world.getName())){
				lists.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WORLDS_LIST_LINE")
						.replaceAll("<world>", world.getName()))
					.replace("<teleport>", getButtonTeleport(world.getName()))
					.build());
			}
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(this.plugin.getMessages().getText("WORLDS_LIST_TITLE").toBuilder()
				.onClick(TextActions.runCommand("/worlds")).build(), lists, player);
		return false;
	}
	
	public boolean commandWorldTeleport(final EPlayer player, final String world_name) {
		Optional<World> optWorld = this.plugin.getEServer().getWorld(world_name);
		if(optWorld.isPresent()) {
			if(player.hasPermission(EEPermissions.WORLDS_NAME.get() + "." + optWorld.get().getName())) {
				if(player.teleportSafe(optWorld.get().getSpawnLocation())) {
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_PLAYER"))
							.replace("<world>", getButtonPosition(player.getLocation()))
							.build());
					return true;
				} else {
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_PLAYER_ERROR"))
							.replace("<world>", getButtonPosition(optWorld.get().getSpawnLocation()))
							.build());
				}
			} else {
				player.sendMessage(EAMessages.NO_PERMISSION_WORLD.getText());
			}
		// Monde introuvable
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getText("WORLD_NOT_FOUND")));
		}
		return false;
	}
	
	public boolean commandWorldTeleportOthers(final CommandSource staff, final EPlayer player, String world_name) {
		Optional<World> optWorld = this.plugin.getEServer().getWorld(world_name);
		if(optWorld.isPresent()) {
			if(player.hasPermission(EEPermissions.WORLDS_NAME.get() + "." + optWorld.get().getName())) {
				if(player.teleportSafe(optWorld.get().getSpawnLocation())) {
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_OTHERS_PLAYER")
									.replaceAll("<staff>", staff.getName()))
							.replace("<world>", getButtonPosition(player.getLocation()))
							.build());
					staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_OTHERS_STAFF")
									.replaceAll("<player>", player.getName()))
							.replace("<world>", getButtonPosition(player.getLocation()))
							.build());
					return true;
				} else {
					staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_OTHERS_ERROR"))
							.replace("<world>", getButtonPosition(optWorld.get().getSpawnLocation()))
							.build());
				}
			} else {
				staff.sendMessage(EAMessages.NO_PERMISSION_WORLD_OTHERS.getText());
			}
		// Monde introuvable
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getText("WORLD_NOT_FOUND")));
		}
		return false;
	}
	
	public Text getButtonTeleport(final String name){
		return this.plugin.getMessages().getText("WORLDS_LIST_TELEPORT").toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WORLDS_LIST_TELEPORT_HOVER")
							.replaceAll("<world>", name))))
					.onClick(TextActions.runCommand("/worlds " + name))
					.build();
	}
	
	public Text getButtonPosition(final Location<World> location){
		return EChat.of(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_WORLD")
				.replaceAll("<world>", location.getExtent().getName())).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WORLDS_TELEPORT_WORLD_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}
