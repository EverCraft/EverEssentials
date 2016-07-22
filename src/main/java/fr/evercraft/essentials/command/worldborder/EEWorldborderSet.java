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
package fr.evercraft.essentials.command.worldborder;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWorldborderSet extends ESubCommand<EverEssentials> {
	public EEWorldborderSet(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "set");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_SET_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 0){
			suggests.add("100");
			suggests.add("1000");
		} else if(args.size() == 1){
			suggests.add("30");
			suggests.add("60");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName() + "<taille en bloc(s)> [temps en seconde(s)] [monde]"))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 1) {
			if(source instanceof EPlayer) {
				resultat = commandWorldborderSet(source, ((EPlayer) source).getWorld(), args.get(0));
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 2){
			if(source instanceof EPlayer) {
			// resultat = commandWorldborder(source, args.get(0));
			}
		} else if(args.size() == 3){
			
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWorldborderSet(CommandSource source, World world, String arg) {
		try {
			int diameter = Integer.parseInt(arg);
			world.getWorldBorder().setDiameter(diameter);
			source.sendMessage(EChat.of(EEMessages.PREFIX + EEMessages.WORLDBORDER_SET_BORDER.get()
					.replaceAll("<world>", world.getName())
					.replaceAll("<nb>", String.valueOf(diameter))));
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", arg)));
			return false;
		}
	}
}