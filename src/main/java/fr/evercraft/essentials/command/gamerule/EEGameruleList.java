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
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

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
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEGameruleList extends ESubCommand<EverEssentials> {
	public EEGameruleList(final EverEssentials plugin, final EEGamerule command) {
        super(plugin, command, "list");
    }
	
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.GAMERULE_LIST_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			if(source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				resultat = commandGameruleList(player, player.getWorld());
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1) {
			if(source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				Optional<World> optWorld = this.plugin.getEServer().getEWorld(args.get(0));
				if(optWorld.isPresent()){
					resultat = commandGameruleList(player, player.getWorld());
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
							.replace("<world>", args.get(0)));
				}
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	public boolean commandGameruleList(final EPlayer player, final World world) {
		Map<String, String> gamerules = world.getProperties().getGameRules();
		player.getLocation().getExtent().getProperties().setGameRule("rez", null);
		List<Text> lists = new ArrayList<Text>();
		for(Entry<String, String> gamerule : gamerules.entrySet()){
			lists.add(EChat.of(EEMessages.GAMERULE_LIST_LINE.get()
				.replaceAll("<gamerule>", gamerule.getKey())
				.replaceAll("<statut>", gamerule.getValue())).toBuilder()
					.onClick(TextActions.suggestCommand("")).build());
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(EEMessages.GAMERULE_LIST_TITLE.get()
			.replaceAll("<world>", player.getWorld().getName())).toBuilder()
				.onClick(TextActions.runCommand("/" + this.getName())).build(), lists, player);
		return true;
	}
}