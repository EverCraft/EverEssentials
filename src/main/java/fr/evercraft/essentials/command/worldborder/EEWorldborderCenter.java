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

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWorldborderCenter extends ESubCommand<EverEssentials> {
	
	public EEWorldborderCenter(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "center");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_CENTER_DESCRIPTION.get());
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.add("0");
			suggests.add("100");
		} else if (args.size() == 2){
			suggests.add("0");
			suggests.add("100");
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
		return Text.builder("/" + this.getName() + " <x> <z> [" + EAMessages.ARGS_WORLD.get() + "]")
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
		} else if (args.size() == 2){
			if (source instanceof Locatable){
				resultat = this.commandWorldborderCenter(source, ((Locatable) source).getWorld(), args.get(0), args.get(1));
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		} else if (args.size() == 3){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(2));
			if (world.isPresent()){
				resultat = this.commandWorldborderCenter(source, world.get(), args.get(0), args.get(1));
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
						.replaceAll("<world>", args.get(2))));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandWorldborderCenter(CommandSource source, World world, String x_string, String y_string) {
		try {
			int x = Integer.parseInt(x_string);
			try {
				int z = Integer.parseInt(y_string);
				
				world.getWorldBorder().setCenter(x, z);
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_CENTER_MESSAGE.get()
						.replaceAll("<world>", world.getName())
						.replaceAll("<x>", String.valueOf(x))
						.replaceAll("<z>", String.valueOf(z))));
				return true;
				
			} catch (NumberFormatException e) {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
						.replaceAll("<number>", y_string)));
			}
		} catch (NumberFormatException e) {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", x_string)));
		}
		return false;
	}
}