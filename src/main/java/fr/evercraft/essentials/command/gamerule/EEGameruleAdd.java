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
package fr.evercraft.essentials.command.gamerule;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEGameruleAdd extends ESubCommand<EverEssentials> {
	public EEGameruleAdd(final EverEssentials plugin, final EEGamerule command) {
        super(plugin, command, "add");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.GAMERULE_ADD_DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 2) {
			if(source instanceof EPlayer) {
				return this.commandGameruleAdd((EPlayer) source, args.get(0), args.get(1));
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandGameruleAdd(final EPlayer player, final String gamerule, final String value) {
		WorldProperties properties = player.getWorld().getProperties();
		if (!properties.getGameRule(gamerule).isPresent()) {
			properties.setGameRule(gamerule, value);
			EEMessages.GAMERULE_ADD_GAMERULE.sender()
				.replace("{gamerule}", gamerule)
				.replace("{value}", value)
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
		} else {
			EEMessages.GAMERULE_ADD_ERROR.sender()
			.prefix(EEMessages.PREFIX)
			.sendTo(player);
		}
		return CompletableFuture.completedFuture(true);
	}
}
