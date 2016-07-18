package fr.evercraft.essentials.command.toggle;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EParentCommand;

public class EEToggle extends EParentCommand<EverEssentials> {
	
	public EEToggle(final EverEssentials plugin) {
        super(plugin, "tptoggle");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TOGGLE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.TOGGLE_DESCRIPTION.get());
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EEPermissions.TOGGLE.get());
	}
}