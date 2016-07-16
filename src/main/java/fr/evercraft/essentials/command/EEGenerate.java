package fr.evercraft.essentials.command;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.command.ECommand;

public class EEGenerate extends ECommand<EverEssentials> {
	
	public EEGenerate(final EverEssentials plugin) {
        super(plugin, "generate");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GENERATE.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.GENERATE_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/generate <monde>").onClick(TextActions.suggestCommand("/ping "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		} else if(args.size() == 2){
			suggests.add("confirmation");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			source.sendMessage(help(source));
		} else if(args.size() == 1) {
			// resultat = commandGenerate(source, args.get(0));
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	/*
	public boolean commandGenerate(final CommandSource source, String world_name) {
		Optional<World> optWorld = this.plugin.getEServer().getWorld(world_name);
		if(optWorld.isPresent()) {
			World world = optWorld.get();
			world.getWorldBorder().newChunkPreGenerate(world).
			ChunkPreGenerate chunck = world.getWorldBorder()
				.newChunkPreGenerate(world).;
			this.plugin.getEServer().broadcast("" + chunck.);
		}
		return true;
	}
	*/
}