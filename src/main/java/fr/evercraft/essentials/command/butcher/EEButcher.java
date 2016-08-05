package fr.evercraft.essentials.command.butcher;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EParentCommand;

public class EEButcher extends EParentCommand<EverEssentials> {
	
	public EEButcher(final EverEssentials plugin) {
        super(plugin, "butcher", "killall");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BUTCHER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.BUTCHER_DESCRIPTION.get());
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EEPermissions.BUTCHER.get());
	}
}