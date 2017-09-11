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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWarp extends ECommand<EverEssentials> {
	
	private boolean permission;
	
	public EEWarp(final EverEssentials plugin) {
        super(plugin, "warp", "warps");
        
       this.reload();
    }
	
	@Override
	public void reload() {
		super.reload();
		
		this.permission = this.plugin.getConfigs().get("warp-permission").getBoolean(true);
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WARP.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WARP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WARP.getString() + "] [" + EAMessages.ARGS_PLAYER.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.plugin.getWarp().getAll().keySet();
		} else if (args.size() == 2 && source.hasPermission(EEPermissions.WARP_OTHERS.get())) {
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Nom du warp inconnu
		if (args.size() == 0) {
			return this.commandWarpList(source);
		// Nom du warp connu
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandWarpTeleport((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WARP_OTHERS.get())){
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(1));
				// Le joueur existe
				if (player.isPresent()){
					return this.commandWarpTeleportOthers(source, player.get(), args.get(0));
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(1))
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	public CompletableFuture<Boolean> commandWarpList(final CommandSource player) throws CommandException {
		TreeMap<String, VirtualTransform> warps = new TreeMap<String, VirtualTransform>(this.plugin.getWarp().getAllSQL());
		
		List<Text> lists = new ArrayList<Text>();
		if (player.hasPermission(EEPermissions.DELWARP.get())) {
			
			for (Entry<String, VirtualTransform> warp : warps.entrySet()) {
				if (this.hasPermission(player, warp.getKey())) {
					Optional<World> world = warp.getValue().getWorld();
					if (world.isPresent()){
						lists.add(EEMessages.WARP_LIST_LINE_DELETE.getFormat().toText(
									"{warp}", () -> this.getButtonWarp(warp.getKey(), warp.getValue()),
									"{teleport}", () -> this.getButtonTeleport(warp.getKey(), warp.getValue()),
									"{delete}", () -> this.getButtonDelete(warp.getKey(), warp.getValue())));
					} else {
						lists.add(EEMessages.WARP_LIST_LINE_DELETE_ERROR_WORLD.getFormat().toText(
										"{warp}", () -> this.getButtonWarp(warp.getKey(), warp.getValue()),
										"{delete}", () -> this.getButtonDelete(warp.getKey(), warp.getValue())));
					}
				}
			}
			
		} else {
			
			for (Entry<String, VirtualTransform> warp : warps.entrySet()) {
				if (this.hasPermission(player, warp.getKey())) {
					Optional<World> world = warp.getValue().getWorld();
					if (world.isPresent()){
						lists.add(EEMessages.WARP_LIST_LINE.getFormat().toText(
									"{warp}", () -> this.getButtonWarp(warp.getKey(), warp.getValue()),
									"{teleport}", () -> this.getButtonTeleport(warp.getKey(), warp.getValue())));
					}
				}
			}
			
		}
		
		if (lists.size() == 0) {
			lists.add(EEMessages.WARP_EMPTY.getText());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.WARP_LIST_TITLE.getText().toBuilder()
				.onClick(TextActions.runCommand("/warp")).build(), lists, player);			
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandWarpTeleport(final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> warp = this.plugin.getWarp().get(name);
		// Le serveur n'a pas de warp qui porte ce nom
		if (!warp.isPresent()) {
			EEMessages.WARP_INCONNU.sender()
				.replace("{warp}", name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!this.hasPermission(player, name)) {
			EEMessages.WARP_NO_PERMISSION.sender()
				.replace("{warp}", name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
				
		// Erreur lors de la téléportation du joueur
		if (!player.teleportSafe(warp.get(), true)) {
			EEMessages.WARP_TELEPORT_PLAYER_ERROR.sender()
				.replace("{warp}", () -> this.getButtonWarp(name, warp.get()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		EEMessages.WARP_TELEPORT_PLAYER.sender()
			.replace("{warp}", () -> this.getButtonWarp(name, warp.get()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandWarpTeleportOthers(final CommandSource staff, final EPlayer player, final String warp_name) {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		
		Optional<Transform<World>> warp = this.plugin.getWarp().get(name);
		// Le serveur a un warp qui porte ce nom
		if (!warp.isPresent()) {
			EEMessages.WARP_INCONNU.sender()
				.replace("{warp}", name)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Erreur lors de la téléportation du joueur
		if (!player.teleportSafe(warp.get(), true)) {
			EEMessages.WARP_TELEPORT_OTHERS_ERROR.sender()
				.replace("{warp}", () -> this.getButtonWarp(name, warp.get()))
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
			
		EEMessages.WARP_TELEPORT_OTHERS_PLAYER.sender()
			.replace("{staff}", staff.getName())
			.replace("{warp}", () -> this.getButtonWarp(name, warp.get()))
			.sendTo(player);
		EEMessages.WARP_TELEPORT_OTHERS_STAFF.sender()
			.replace("{player}", player.getName())
			.replace("{warp}", () -> this.getButtonWarp(name, warp.get()))
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonTeleport(final String name, final VirtualTransform location){
		return EEMessages.WARP_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.WARP_LIST_TELEPORT_HOVER.getFormat().toText("{warp}", name)))
					.onClick(TextActions.runCommand("/warp \"" + name + "\""))
					.build();
	}
	
	private Text getButtonDelete(final String name, final VirtualTransform location){
		return EEMessages.WARP_LIST_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.WARP_LIST_DELETE_HOVER.getFormat().toText("{warp}", name)))
					.onClick(TextActions.runCommand("/delwarp \"" + name + "\""))
					.build();
	}
	
	private Text getButtonWarp(final String name, final VirtualTransform location){
		return EEMessages.WARP_NAME.getFormat().toText("{name}", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.WARP_NAME_HOVER.getFormat().toText(
							"{warp}", name,
							"{world}", location.getWorldName(),
							"{x}", String.valueOf(location.getPosition().getFloorX()),
							"{y}", String.valueOf(location.getPosition().getFloorY()),
							"{z}", String.valueOf(location.getPosition().getFloorZ()))))
					.build();
	}
	
	private Text getButtonWarp(final String name, final Transform<World> location){
		return EEMessages.WARP_NAME.getFormat().toText("{name}", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.WARP_NAME_HOVER.getFormat().toText(
								"{warp}", name,
								"{world}", location.getExtent().getName(),
								"{x}", String.valueOf(location.getLocation().getBlockX()),
								"{y}", String.valueOf(location.getLocation().getBlockY()),
								"{z}", String.valueOf(location.getLocation().getBlockZ()))))
					.build();
	}
	
	private boolean hasPermission(CommandSource player, String warp) {
		return (!this.permission || player.hasPermission(EEPermissions.WARP_NAME.get() + "." + warp));
	}
}
