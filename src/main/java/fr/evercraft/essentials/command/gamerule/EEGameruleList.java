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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEGameruleList extends ESubCommand<EverEssentials> {
	public EEGameruleList(final EverEssentials plugin, final EEGamerule command) {
        super(plugin, command, "list");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.GAMERULE_LIST_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 0) {
			if(source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				return this.commandGameruleList(player, player.getWorld());
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if(args.size() == 1) {
			if(source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				Optional<World> optWorld = this.plugin.getEServer().getEWorld(args.get(0));
				if(optWorld.isPresent()){
					return this.commandGameruleList(player, player.getWorld());
				} else {
					EAMessages.WORLD_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{world}", args.get(0))
						.sendTo(source);
				}
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandGameruleList(final EPlayer player, final World world) {
		Map<String, String> gamerules = world.getProperties().getGameRules();
		List<Text> lists = new ArrayList<Text>();
		for(Entry<String, String> gamerule : gamerules.entrySet()){
			lists.add(EEMessages.GAMERULE_LIST_LINE.getFormat().toText(
						"{gamerule}", gamerule.getKey(),
						"{statut}", gamerule.getValue()).toBuilder()
					.onClick(TextActions.suggestCommand("")).build());
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.GAMERULE_LIST_TITLE.getFormat()
			.toText("{world}", player.getWorld().getName()).toBuilder()
				.onClick(TextActions.runCommand("/" + this.getName())).build(), lists, player);
		return CompletableFuture.completedFuture(true);
	}
}
