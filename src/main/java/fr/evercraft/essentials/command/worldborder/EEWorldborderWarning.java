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
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_WARNING_DESCRIPTION.get());
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.add("time");
			suggests.add("distance");
		} else if (args.size() == 2){
			suggests.add("1");
			suggests.add("5");
			suggests.add("10");
		} else if (args.size() == 3){
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		}
		return suggests;
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <time|distance> <" + EAMessages.ARGS_VALUE.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpTime(final CommandSource source) {
		return Text.builder("/" + this.getName() + " time <" + EAMessages.ARGS_SECONDS.get() + "> [" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " time "))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpDistance(final CommandSource source) {
		return Text.builder("/" + this.getName() + " distance <" + EAMessages.ARGS_DISTANCE.get() + "> [" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " distance "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0){
			source.sendMessage(this.help(source));
		} else if (args.size() == 1){
			
			if (args.get(0).equalsIgnoreCase("time")){
				source.sendMessage(this.helpTime(source));
			} else if (args.get(0).equalsIgnoreCase("distance")){
				source.sendMessage(this.helpDistance(source));
			} else {
				source.sendMessage(this.help(source));
			}
			
		} else if (args.size() == 2){
			if (source instanceof EPlayer){
				
				if (args.get(0).equalsIgnoreCase("time")){
					resultat = this.commandWorldborderWarningTime(source, ((EPlayer)source).getWorld(), args.get(1));
				} else if (args.get(0).equalsIgnoreCase("distance")){
					resultat = this.commandWorldborderWarningDistance(source, ((EPlayer)source).getWorld(), args.get(1));
				} else {
					source.sendMessage(this.help(source));
				}
				
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 3) {
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(2));
			if (optWorld.isPresent()) {
				
				if (args.get(0).equalsIgnoreCase("time")){
					resultat = this.commandWorldborderWarningTime(source, optWorld.get(), args.get(1));
				} else if (args.get(0).equalsIgnoreCase("distance")){
					resultat = this.commandWorldborderWarningDistance(source, optWorld.get(), args.get(1));
				} else {
					source.sendMessage(this.help(source));
				}
				
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
						.replaceAll("<world>", args.get(2))));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandWorldborderWarningTime(CommandSource source, World world, String value_string) {
		try {
			int value = Integer.parseInt(value_string);

			world.getWorldBorder().setWarningTime(value);
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_WARNING_TIME.get()
					.replaceAll("<amount>", String.valueOf(value))
					.replaceAll("<world>", world.getName())));
			
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", value_string)));
			return false;
		}
	}
	
	private boolean commandWorldborderWarningDistance(CommandSource source, World world, String value_string) {
		try {
			int value = Integer.parseInt(value_string);

			world.getWorldBorder().setWarningDistance(value);
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_WARNING_DISTANCE.get()
					.replaceAll("<amount>", String.valueOf(value))
					.replaceAll("<world>", world.getName())));
			
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", value_string)));
			return false;
		}
	}
}