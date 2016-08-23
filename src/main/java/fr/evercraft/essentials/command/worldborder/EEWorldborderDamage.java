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

public class EEWorldborderDamage extends ESubCommand<EverEssentials> {
	public EEWorldborderDamage(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "damage");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_DAMAGE_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.add("buffer");
			suggests.add("amount");
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

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <buffer|amount> <" + EAMessages.ARGS_VALUE.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpAmount(final CommandSource source) {
		return Text.builder("/" + this.getName() + " amount <" + EAMessages.ARGS_DAMAGE.get() + "> [" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " amount "))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpBuffer(final CommandSource source) {
		return Text.builder("/" + this.getName() + " buffer <" + EAMessages.ARGS_BLOCK.get() + "> [" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " buffer "))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if (args.size() == 0){
			source.sendMessage(this.help(source));
		} else if (args.size() == 1){
			if (args.get(0).equalsIgnoreCase("amount")){
				source.sendMessage(helpAmount(source));
			} else if (args.get(0).equalsIgnoreCase("buffer")){
				source.sendMessage(helpBuffer(source));
			} else {
				source.sendMessage(this.help(source));
			}
		} else if (args.size() == 2){
			if (source instanceof EPlayer){
				resultat = commandWorldborderDamage(source, ((EPlayer)source).getWorld(), args);
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if (args.size() == 3){
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(2));
			if (optWorld.isPresent()){
				resultat = commandWorldborderDamage(source, optWorld.get(), args);
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
						.replaceAll("<world>", args.get(2))));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWorldborderDamage(CommandSource source, World world, List<String> args) {
		try {
			double value = Integer.parseInt(args.get(1));
			if (args.get(0).equalsIgnoreCase("amount")){
				world.getWorldBorder().setDamageAmount(value);
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_DAMAGE_AMOUNT.get()
						.replaceAll("<nb>", String.valueOf(value))
						.replaceAll("<world>", world.getName())));
				return true;
			} else if (args.get(0).equalsIgnoreCase("buffer")){
				world.getWorldBorder().setDamageThreshold(value);
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WORLDBORDER_DAMAGE_BUFFER.get()
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