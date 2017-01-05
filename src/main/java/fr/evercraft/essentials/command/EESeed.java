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
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EESeed extends ECommand<EverEssentials> {
	
	public EESeed(final EverEssentials plugin) {
        super(plugin, "seed");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission("minecraft.command.seed");
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SEED_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.get() + "]"))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				resultat = this.commandSeed(player, player.getWorld());
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				Optional<World> optWorld = this.plugin.getEServer().getEWorld(args.get(0));
				if(optWorld.isPresent()) {
					resultat = this.commandSeed((EPlayer) source, optWorld.get());
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
							.replace("<world>", args.get(0))));
				}
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}
	
	private boolean commandSeed(final EPlayer player, final World world) {
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.SEED_MESSAGE.get()
				.replaceAll("<world>", world.getName()))
				.replace("<seed>", this.getButtonSeed(world.getProperties().getSeed()))
				.build());				
		return true;
	}
	
	private Text getButtonSeed(final Long seed){
		return EChat.of(EEMessages.SEED_NAME.get().replace("<seed>", seed.toString())).toBuilder()
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(seed.toString()))
					.onShiftClick(TextActions.insertText(seed.toString()))
					.build();
	}
}