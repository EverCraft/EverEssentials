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
package fr.evercraft.essentials;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.plugin.command.ECommand;

public class EECommand extends ECommand<EverEssentials> {
	public EECommand(EverEssentials plugin) {
		super(plugin, "everessentials", "essentials");
    }

	@Override
	public boolean execute(CommandSource source, List<String> args) throws CommandException, PluginDisableException {
		if(args.size() == 0){
			if(source.hasPermission(EEPermissions.HELP.get())) {					
				this.plugin.getEverAPI().getManagerService().getEPagination().helpCommand(this.plugin.getManagerCommands(), source, this.plugin);
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else if(args.size() == 1){
			if(args.get(0).equalsIgnoreCase("help")) {
				if(source.hasPermission(EEPermissions.HELP.get())) {					
					this.plugin.getEverAPI().getManagerService().getEPagination().helpCommand(this.plugin.getManagerCommands(), source, this.plugin);
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			} else if(args.get(0).equalsIgnoreCase("reload")) {
				if(source.hasPermission(EEPermissions.RELOAD.get())) {
					this.plugin.reload();
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.RELOAD_COMMAND.getText()));
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			}
		}
		return false;
	}

	public List<String> tabCompleter(CommandSource source, List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("help");
			suggests.add("reload");
		}
		return suggests;
	}

	public Text help(CommandSource source) {
		return Text.of("Information sur EverEssentials");
	}

	public Text description(CommandSource source) {
		return Text.of("Information sur EverEssentials");
	}
	
	public boolean testPermission(CommandSource source) {
		return source.hasPermission(EEPermissions.EVERESSENTIALS.get());
	}
}
