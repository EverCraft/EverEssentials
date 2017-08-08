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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWorlds extends ECommand<EverEssentials> {
	
	private static final String DIM_1 = "Nether";
	private static final String DIM1 = "End";

	public EEWorlds(final EverEssentials plugin) {
		super(plugin, "worlds", "world");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WORLDS_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.WORLDS_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.getString() + " [" + EAMessages.ARGS_PLAYER.getString() + "]]")
						.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
						.color(TextColors.RED)
						.build();
		} 
		return Text.builder("/" + this.getName() + "  [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
			return suggests;
		} else if (args.size() == 2 && source.hasPermission(EEPermissions.WORLDS_OTHERS.get())) {
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			return this.commandWorldList(source);
		} else if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				return this.commandWorldTeleport((EPlayer) source, args.get(0));
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WORLDS_OTHERS.get())){
				// Si la source est bien un joueur
				if (source instanceof EPlayer) {
					Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(1));
					// Le joueur existe
					if (player.isPresent()){
						return this.commandWorldTeleportOthers((EPlayer) source, player.get(), args.get(0));
					// Le joueur est introuvable
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.replace("{player}", args.get(1))
							.sendTo(source);
					}
				// Si la source est une console ou un commande block
				} else {
					EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	public CompletableFuture<Boolean> commandWorldList(final CommandSource player) {
		List<Text> lists = new ArrayList<Text>();
		
		for (World world : this.plugin.getEServer().getWorlds()) {
			if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world)) {
				String name = world.getName();
				if (name.equalsIgnoreCase("DIM-1")) {
					name = name + " (" + DIM_1 + ")";
				} else if (name.equalsIgnoreCase("DIM1")) {
					name = name + " (" + DIM1 + ")";
				}
				
				lists.add(EEMessages.WORLDS_LIST_LINE.getFormat().toText(
						"{world}", EReplace.of(name),
						"{teleport}", EReplace.of(() -> this.getButtonTeleport(world.getName(), world.getUniqueId()))));
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.WORLDS_LIST_TITLE.getText().toBuilder()
				.onClick(TextActions.runCommand("/worlds")).build(), lists, player);
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandWorldTeleport(final EPlayer player, final String world_name) {
		Optional<World> world = this.plugin.getEServer().getEWorld(world_name);
		// Monde introuvable
		if (!world.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", world_name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		if (!this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world.get())) {
			EAMessages.NO_PERMISSION_WORLD.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", world.get().getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!player.teleportSafeZone(world.get().getSpawnLocation(), true)) {
			EEMessages.WORLDS_TELEPORT_PLAYER_ERROR.sender()
				.replace("{world}", this.getButtonPosition(world.get().getSpawnLocation()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}

		EEMessages.WORLDS_TELEPORT_PLAYER.sender()
			.replace("{world}", () -> this.getButtonPosition(player.getLocation()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandWorldTeleportOthers(final CommandSource staff, final EPlayer player, String world_name) {
		if(player.equals(staff)) {
			return this.commandWorldTeleport(player, world_name);
		}
		
		Optional<World> world = this.plugin.getEServer().getWorld(world_name);
		// Monde introuvable
		if (!world.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", world_name)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
				
		if (!this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world.get())) {
			EAMessages.NO_PERMISSION_WORLD_OTHERS.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", world.get().getName())
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!player.teleportSafeZone(world.get().getSpawnLocation(), true)) {
			EEMessages.WORLDS_TELEPORT_OTHERS_ERROR.sender()
				.replace("{player}", player.getName())
				.replace("{world}", () -> this.getButtonPosition(world.get().getSpawnLocation()))
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.WORLDS_TELEPORT_OTHERS_PLAYER.sender()
			.replace("{staff}", staff.getName())
			.replace("{world}", () -> this.getButtonPosition(player.getLocation()))
			.sendTo(player);
		EEMessages.WORLDS_TELEPORT_OTHERS_STAFF.sender()
			.replace("{player}", player.getName())
			.replace("{world}", () -> this.getButtonPosition(player.getLocation()))
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonTeleport(final String name, final UUID uuid){
		return EEMessages.WORLDS_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.WORLDS_LIST_TELEPORT_HOVER.getFormat()
							.toText("{world}", name)))
					.onClick(TextActions.runCommand("/worlds \"" + uuid + "\""))
					.build();
	}
	
	private Text getButtonPosition(final Location<World> location){
		return EEMessages.WORLDS_TELEPORT_WORLD.getFormat()
				.toText("{world}", location.getExtent().getName()).toBuilder()
					.onHover(TextActions.showText(EEMessages.WORLDS_TELEPORT_WORLD_HOVER.getFormat().toText(
								"{world}", location.getExtent().getName(),
								"{x}", String.valueOf(location.getBlockX()),
								"{y}", String.valueOf(location.getBlockY()),
								"{z}", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
