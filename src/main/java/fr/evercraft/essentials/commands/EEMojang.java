/**
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
package fr.evercraft.essentials.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.services.mojang.MojangService;
import fr.evercraft.everapi.services.mojang.check.MojangServer;

public class EEMojang extends ECommand<EverEssentials> {
	
	public EEMojang(final EverEssentials plugin) {
        super(plugin, "mojang");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MOJANG.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("MOJANG_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/mojang").onClick(TextActions.suggestCommand("/mojang"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du home inconnu
		if (args.size() == 0) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
								.async()
								.execute(() -> commandMojang(source))
								.name("Command : Mojang").submit(this.plugin);
			resultat = true;
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private void commandMojang(final CommandSource player) {
		Optional<MojangService> service = this.plugin.getEverAPI().getManagerService().getMojangService();
		if(service.isPresent()) {
			try {
				service.get().getCheck().update();

				List<Text> lists = new ArrayList<Text>();
		
				lists.add(server(MojangServer.ACCOUNT));
				lists.add(server(MojangServer.API));
				lists.add(server(MojangServer.MOJANG));
				lists.add(server(MojangServer.AUTH));
				lists.add(server(MojangServer.AUTHSERVER));
				lists.add(server(MojangServer.MINECRAFT_NET));
				lists.add(server(MojangServer.SESSION));
				lists.add(server(MojangServer.SESSIONSERVER));
				lists.add(server(MojangServer.SKINS));
				lists.add(server(MojangServer.TEXTURES));
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
						EChat.of(this.plugin.getMessages().getMessage("MOJANG_TITLE")).toBuilder()
							.onClick(TextActions.runCommand("/mojang ")).build(), 
						lists, player);
			} catch (IOException e) {
				player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
			}
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
		}
	}
	
	public Text server(final MojangServer server) {
		return EChat.of(this.plugin.getMessages().getMessage("MOJANG_LINE")
				.replaceAll("<server>", this.plugin.getMessages().getMessage("MOJANG_SERVER_" + server.name()))
				.replaceAll("<color>", this.plugin.getMessages().getMessage("MOJANG_COLOR_" + server.getColor().name())));
	}
}
