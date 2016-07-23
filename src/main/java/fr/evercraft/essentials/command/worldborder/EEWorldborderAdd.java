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

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWorldborderAdd extends ESubCommand<EverEssentials> {
	public EEWorldborderAdd(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "add");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_ADD_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("100");
			suggests.add("1000");
		} else if(args.size() == 2){
			suggests.add("30");
			suggests.add("60");
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
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
		return Text.builder("/" + this.getName() + " <bloc(s)> [seconde(s)] [monde]")
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
			if(source instanceof EPlayer) {
				resultat = commandWorldborderAdd(source, ((EPlayer) source).getWorld(), args.get(0));
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 2){
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(1));
			if(optWorld.isPresent()){
				resultat = commandWorldborderAdd(source, optWorld.get(), args.get(0));
			} else {
				if(source instanceof EPlayer) {
					commandWorldborderAdd(source, ((EPlayer) source).getWorld(), args);
				} else {
					source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
				}
			}
		} else if(args.size() == 3){
			commandWorldborderAdd(source, args);
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWorldborderAdd(CommandSource source, World world, String arg) {
		try {
			double diameter =  world.getWorldBorder().getDiameter() + Integer.parseInt(arg);
			world.getWorldBorder().setDiameter(diameter);
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_ADD_BORDER.get()
					.replaceAll("<world>", world.getName())
					.replaceAll("<nb>", String.valueOf(diameter))));
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", arg)));
			return false;
		}
	}
	
	private boolean commandWorldborderAdd(CommandSource source, World world, List<String> args) {
		try {
			double diameter = world.getWorldBorder().getDiameter() + Integer.parseInt(args.get(0));
			double time = Integer.parseInt(args.get(1));
			String message;
			world.getWorldBorder().setDiameter(world.getWorldBorder().getDiameter(), diameter, (long) (time * 1000));
			if(world.getWorldBorder().getDiameter() > diameter){
				message = EEMessages.WORLDBORDER_ADD_BORDER_DECREASE.get();
			} else {
				message = EEMessages.WORLDBORDER_ADD_BORDER_INCREASE.get();
			}
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + message
					.replaceAll("<world>", world.getName())
					.replaceAll("<nb>", String.valueOf(diameter))
					.replaceAll("<time>", String.valueOf(time))));
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", e.getMessage())));
			return false;
		}
	}
	
	private boolean commandWorldborderAdd(CommandSource source, List<String> args) {
		try {
			double time = Integer.parseInt(args.get(1));
			String message;
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(2));
			if(optWorld.isPresent()){
				World world = optWorld.get();
				double diameter = world.getWorldBorder().getDiameter() + Integer.parseInt(args.get(0));
				world.getWorldBorder().setDiameter(world.getWorldBorder().getDiameter(), diameter, (long) (time * 1000));
				if(world.getWorldBorder().getDiameter() > diameter){
					message = EEMessages.WORLDBORDER_ADD_BORDER_DECREASE.get();
				} else {
					message = EEMessages.WORLDBORDER_ADD_BORDER_INCREASE.get();
				}
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + message
						.replaceAll("<world>", world.getName())
						.replaceAll("<nb>", String.valueOf(diameter))
						.replaceAll("<time>", String.valueOf(time))));
				return true;
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", args.get(2))));
				return false;
			}
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", e.getMessage())));
			return false;
		}
	}
}