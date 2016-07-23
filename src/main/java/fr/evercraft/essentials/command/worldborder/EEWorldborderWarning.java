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
import java.util.Optional;

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

public class EEWorldborderWarning extends ESubCommand<EverEssentials> {
	public EEWorldborderWarning(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "warning");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_DAMAGE_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("time");
			suggests.add("distance");
		} else if(args.size() == 2){
			suggests.add("1");
			suggests.add("5");
			suggests.add("10");
		} else if(args.size() == 3){
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <time|distance> ...")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpTime(final CommandSource source) {
		return Text.builder("/" + this.getName() + " time <seconde(s)> [monde]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpDistance(final CommandSource source) {
		return Text.builder("/" + this.getName() + " distance <distance> [monde]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0){
			source.sendMessage(this.help(source));
		} else if(args.size() == 1){
			if(args.get(0).equalsIgnoreCase("time")){
				source.sendMessage(helpTime(source));
			} else if(args.get(0).equalsIgnoreCase("distance")){
				source.sendMessage(helpDistance(source));
			} else {
				source.sendMessage(this.help(source));
			}
		} else if(args.size() == 2){
			if(source instanceof EPlayer){
				resultat = commandWorldborderWarning(source, ((EPlayer)source).getWorld(), args);
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 3){
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(2));
			if(optWorld.isPresent()){
				resultat = commandWorldborderWarning(source, optWorld.get(), args);
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
						.replaceAll("<world>", args.get(2))));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWorldborderWarning(CommandSource source, World world, List<String> args) {
		try {
			int value = Integer.parseInt(args.get(1));
			if(args.get(0).equalsIgnoreCase("time")){
				world.getWorldBorder().setWarningTime(value);
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_WARNING_TIME.get()
						.replaceAll("<nb>", String.valueOf(value))
						.replaceAll("<world>", world.getName())));
				return true;
			} else if(args.get(0).equalsIgnoreCase("distance")){
				world.getWorldBorder().setWarningDistance(value);
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_WARNING_DISTANCE.get()
						.replaceAll("<nb>", String.valueOf(value))
						.replaceAll("<world>", world.getName())));
				return true;
			} else {
				source.sendMessage(this.help(source));
				return false;
			}
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", args.get(1))));
			return false;
		}
	}
}