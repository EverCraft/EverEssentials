package fr.evercraft.essentials.command.whitelist;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWhitelistOff extends ESubCommand<EverEssentials> {
	public EEWhitelistOff(final EverEssentials plugin, final EEWhitelist command) {
        super(plugin, command, "off");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHITELIST_MANAGE.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WHITELIST_DESCRIPTION.get());
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
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			resultat = commandWhitelistOff(source);
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWhitelistOff(final CommandSource player) {
		if(this.plugin.getEServer().hasWhitelist()){
			this.plugin.getEServer().setHasWhitelist(false);
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WHITELIST_DISABLED.get()));
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WHITELIST_ALREADY_DISABLED.get()));
		}
		return true;
	}
}

