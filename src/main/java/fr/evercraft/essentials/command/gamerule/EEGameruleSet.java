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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEGameruleSet extends ESubCommand<EverEssentials> {
	
	public EEGameruleSet(final EverEssentials plugin, final EEGamerule command) {
        super(plugin, command, "set");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.GAMERULE_SET_DESCRIPTION.get());
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if(args.size() == 2) {
			if(source instanceof EPlayer) {
				resultat = this.commandGameruleSet((EPlayer) source, args.get(0), args.get(1));
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandGameruleSet(final EPlayer player, final String gamerule, final String value) {
		WorldProperties properties = player.getWorld().getProperties();
		if (properties.getGameRule(gamerule).isPresent()) {
			player.sendMessage("Set gamerule : " + gamerule + "; value : " + value);
			properties.setGameRule(gamerule, value);
		} else {
			player.sendMessage("Il n'y a pas de gamerule : " + gamerule);
		}
		return true;
	}
}