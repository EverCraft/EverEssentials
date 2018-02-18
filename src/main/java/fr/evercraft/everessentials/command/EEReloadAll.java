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
package fr.evercraft.everessentials.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEReloadAll extends ECommand<EverEssentials>{
	
	public EEReloadAll(final EverEssentials plugin) {
        super(plugin, "reload", "rl");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission("sponge.command.plugins.reload");
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.RELOAD_ALL_DESCRIPTION.getText();
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
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			return this.commandReload(source);
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandReload(final CommandSource player) {
		EEMessages.RELOAD_ALL_START.sender()
			.sendAll(this.plugin.getEServer().getOnlineEPlayers());
		this.plugin.getGame().getEventManager().post(SpongeEventFactory.createGameReloadEvent(this.plugin.getCurrentCause()));
		EEMessages.RELOAD_ALL_END.sender()
			.sendAll(this.plugin.getEServer().getOnlineEPlayers());
		return CompletableFuture.completedFuture(true);
	}
}
