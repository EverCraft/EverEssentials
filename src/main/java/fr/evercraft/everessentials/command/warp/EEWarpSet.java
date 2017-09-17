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
package fr.evercraft.everessentials.command.warp;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEWarpSet extends ECommand<EverEssentials> {
	
	public EEWarpSet(final EverEssentials plugin) {
        super(plugin, "setwarp", "setwarps");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SETWARP.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SETWARP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_WARP.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("...");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandSetWarp((EPlayer) source, args.get(0)); 
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSetWarp(final EPlayer player, final String warp_name) throws ServerDisableException {
		String name = EChat.fixLength(warp_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> warp = this.plugin.getWarp().get(name);
		if (warp.isPresent()) {
			if (this.plugin.getWarp().update(name, player.getTransform())) {
				EEMessages.SETWARP_REPLACE.sender()
					.replace("{warp}", () -> this.getButtonWarp(name, player.getLocation()))
					.sendTo(player);
				return CompletableFuture.completedFuture(true);
			} else {
				EEMessages.SETWARP_REPLACE_CANCEL.sender()
					.replace("{warp}", name)
					.sendTo(player);
			}
		} else {
			if (this.plugin.getWarp().add(name, player.getTransform())) {
				EEMessages.SETWARP_NEW.sender()
					.replace("{warp}", () -> this.getButtonWarp(name, player.getLocation()))
					.sendTo(player);
				return CompletableFuture.completedFuture(true);
			} else {
				EEMessages.SETWARP_NEW_CANCEL.sender()
					.replace("{warp}", name)
					.sendTo(player);
			}
		}
		return CompletableFuture.completedFuture(false);
	}

	private Text getButtonWarp(final String name, final Location<World> location){
		return EEMessages.SETWARP_NAME.getFormat().toText("{name}", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.SETWARP_NAME_HOVER.getFormat().toText(
								"{warp}", name,
								"{world}", location.getExtent().getName(),
								"{x}", String.valueOf(location.getBlockX()),
								"{y}", String.valueOf(location.getBlockY()),
								"{z}", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
