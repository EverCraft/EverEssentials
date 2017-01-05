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
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWorldborderAdd extends ESubCommand<EverEssentials> {
	
	public EEWorldborderAdd(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "add");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_ADD_DESCRIPTION.get());
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.add("100");
			suggests.add("1000");
		} else if (args.size() == 2){
			suggests.add("30");
			suggests.add("60");
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_BLOCK.get() + "> [" + EAMessages.ARGS_SECONDS.get() + "] "
						+ "[" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
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
			if (source instanceof Locatable) {
				resultat = this.commandWorldborderAdd(source, ((Locatable) source).getWorld(), args.get(0));
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 2){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(1));
			if (world.isPresent()){
				resultat = this.commandWorldborderAdd(source, world.get(), args.get(0));
			} else {
				if (source instanceof Locatable) {
					resultat = this.commandWorldborderAdd(source, ((Locatable) source).getWorld(), args.get(0), args.get(1));
				} else {
					EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			}
		} else if (args.size() == 3){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(2));
			if (world.isPresent()){
				resultat = this.commandWorldborderAdd(source, world.get(), args.get(0), args.get(1));
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
						.replaceAll("<world>", args.get(2))));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandWorldborderAdd(final CommandSource source, final World world, final String diameter_string) {
		try {
			double diameter =  world.getWorldBorder().getDiameter() + Integer.parseInt(diameter_string);
			world.getWorldBorder().setDiameter(diameter);
			
			String message;
			if (world.getWorldBorder().getDiameter() > diameter){
				message = EEMessages.WORLDBORDER_ADD_BORDER_DECREASE.get();
			} else {
				message = EEMessages.WORLDBORDER_ADD_BORDER_INCREASE.get();
			}
			
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + message
					.replaceAll("<world>", world.getName())
					.replaceAll("<amount>", String.valueOf(diameter))));
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", diameter_string)));
			return false;
		}
	}
	
	private boolean commandWorldborderAdd(final CommandSource source, final World world, final String diameter_string, final String time_string) {
		try {
			double diameter = world.getWorldBorder().getDiameter() + Integer.parseInt(diameter_string);
			double time = Integer.parseInt(time_string);

			world.getWorldBorder().setDiameter(world.getWorldBorder().getDiameter(), diameter, (long) (time * 1000));
			
			String message;
			if (world.getWorldBorder().getDiameter() > diameter){
				message = EEMessages.WORLDBORDER_ADD_BORDER_TIME_DECREASE.get();
			} else {
				message = EEMessages.WORLDBORDER_ADD_BORDER_TIME_INCREASE.get();
			}
			
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + message
					.replaceAll("<world>", world.getName())
					.replaceAll("<amount>", String.valueOf(diameter))
					.replaceAll("<time>", String.valueOf(time))));
			return true;
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", diameter_string)));
			return false;
		}
	}
}