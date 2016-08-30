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
package fr.evercraft.essentials.command.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EParentCommand;

public class EEMail extends EParentCommand<EverEssentials> {
	public EEMail(final EverEssentials plugin) {
        super(plugin, "mail");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.MAIL_DESCRIPTION.get());
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL.get());
	}
	
	@Override
	protected List<String> getArg(final String arg) {
		List<String> args = super.getArg(arg);
		
		// Le message est transformer en un seul argument
		if (args.size() > 3 && args.get(0).equalsIgnoreCase("send")) {
			List<String> args_send = new ArrayList<String>();
			args_send.add(args.get(0));
			args_send.add(args.get(1));
			if (args.get(1).equalsIgnoreCase("*")) {
				args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"]*\\*[ \"][ ]*").matcher(arg).replaceAll(""));
			} else {
				args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"]*" + args.get(1) + "[ \"][ ]*").matcher(arg).replaceAll(""));
			}
			return args_send;
		}
		return args;
	}
}