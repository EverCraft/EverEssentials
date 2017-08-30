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
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWorldsEnd extends ECommand<EverEssentials> {
	
	public EEWorldsEnd(final EverEssentials plugin) {
		super(plugin, "end");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WORLDS_END_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.WORLDS_OTHERS.get())) {
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getEssentials().hasPermissionWorld(source, world)) {
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
	public CompletableFuture<Boolean> execute(CommandSource source, final List<String> args) throws CommandException {
		// Erreur : Context 
		if(source instanceof EPlayer) {
			source = ((EPlayer) source).get();
		}
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			return this.commandEnd(source);
		} else if (args.size() == 1){
			return this.commandEnd(source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandEnd(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "worlds DIM1");
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandEnd(final CommandSource player, final String arg) {
		this.plugin.getGame().getCommandManager().process(player, "worlds DIM1 "+ arg);
		return CompletableFuture.completedFuture(false);
	}
}
