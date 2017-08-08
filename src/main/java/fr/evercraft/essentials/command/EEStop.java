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
package fr.evercraft.essentials.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;

public class EEStop extends ECommand<EverEssentials> {
	
	public EEStop(final EverEssentials plugin) {
        super(plugin ,"stop");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.STOP.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.STOP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " {" + EAMessages.ARGS_REASON.getString() + "}")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}

	@Override
	protected List<String> getArg(final String arg){
		if (arg.isEmpty()) {
			return Arrays.asList();
		}
		return Arrays.asList(arg);
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			return this.commandStop(source);
		} else {
			return this.commandStop(source, args.get(0));
		}
	}

	private CompletableFuture<Boolean> commandStop(final CommandSource player) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(this.plugin.getChat().getReplaceServer());
		replaces.put(Pattern.compile("{staff}"), EReplace.of(player.getName()));
		
		this.plugin.getELogger().info("Server shutdown by '" + player.getName() + "'");
		if (player instanceof ConsoleSource) {
			this.plugin.getGame().getServer().shutdown(EEMessages.STOP_CONSOLE_MESSAGE.getFormat().toText(replaces));
		} else {
			this.plugin.getGame().getServer().shutdown(EEMessages.STOP_MESSAGE.getFormat().toText(replaces));
		}
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandStop(final CommandSource player, String message) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(this.plugin.getChat().getReplaceServer());
		replaces.put(Pattern.compile("{staff}"), EReplace.of(player.getName()));
		replaces.put(Pattern.compile("{reason}"), EReplace.of(this.plugin.getChat().replace(message)));
		
		this.plugin.getELogger().info("Server shutdown by '" + player.getName() + "' (reason='" + message + "')");
		if (player instanceof ConsoleSource) {
			this.plugin.getGame().getServer().shutdown(EEMessages.STOP_CONSOLE_MESSAGE_REASON.getFormat().toText(replaces));
		} else {
			this.plugin.getGame().getServer().shutdown(EEMessages.STOP_MESSAGE_REASON.getFormat().toText(replaces));
		}		
		return CompletableFuture.completedFuture(true);
	}
}
