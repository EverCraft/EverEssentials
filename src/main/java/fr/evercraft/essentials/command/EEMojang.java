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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.registers.MojangServer;
import fr.evercraft.everapi.services.MojangService;

public class EEMojang extends ECommand<EverEssentials> {
	
	public EEMojang(final EverEssentials plugin) {
        super(plugin, "mojang");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MOJANG.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.MOJANG_DESCRIPTION.getText();
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
		// Nom du home inconnu
		if (args.size() == 0) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
								.async()
								.execute(() -> this.commandMojang(source))
								.name("Command : Mojang").submit(this.plugin);
			return CompletableFuture.completedFuture(true);
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandMojang(final CommandSource player) {
		MojangService service = this.plugin.getEverAPI().getManagerService().getMojangService();
		
		return service.getCheck().update().exceptionally(e -> null).thenApply(result -> {
			if (!result) {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(player);
				return false;
			}
			
			List<Text> lists = new ArrayList<Text>();
			TreeSet<MojangServer> servers = new TreeSet<MojangServer>((s1, s2) -> s1.getId().compareTo(s2.getId()));
			servers.addAll(this.plugin.getGame().getRegistry().getAllOf(MojangServer.class));
			
			for (MojangServer server : servers) {
				lists.add(this.server(server));
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
					EEMessages.MOJANG_TITLE.getText().toBuilder()
						.onClick(TextActions.runCommand("/mojang ")).build(), 
					lists, player);
			return true;
		});
	}
	
	private Text server(final MojangServer server) {
		Optional<EEMessages> server_name = EEMojang.getMojangServer(server);
		Optional<EEMessages> status_name = EEMojang.getMojangColor(server.getStatus());
		if (server_name.isPresent() && status_name.isPresent()) {
			return EEMessages.MOJANG_LINE.getFormat().toText(
					"{server}", server_name.get().getText(),
					"{color}", status_name.get().getText());
		}
		return Text.of();
	}
	
	private static Optional<EEMessages> getMojangServer(MojangServer server) {
		try {
			return Optional.of(EEMessages.valueOf("MOJANG_SERVER_" + server.getId().toUpperCase()));
		} catch (IllegalArgumentException e) {}
		return Optional.empty();
	}
	
	private static Optional<EEMessages> getMojangColor(MojangServer.Status state) {
		try {
			return Optional.of(EEMessages.valueOf("MOJANG_STATUS_" + state.name()));
		} catch (IllegalArgumentException e) {}
		return Optional.empty();
	}
}
