package fr.evercraft.essentials.command.god;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EParentCommand;

public class EEGod extends EParentCommand<EverEssentials> {
	
	public EEGod(final EverEssentials plugin) {
        super(plugin, "god");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GOD.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.GOD_DESCRIPTION.get());
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EEPermissions.GOD.get());
	}
}