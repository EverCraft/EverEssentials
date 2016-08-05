package fr.evercraft.essentials.command.butcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntityType;

public class EEButcherMonster extends ESubCommand<EverEssentials> {
	public EEButcherMonster(final EverEssentials plugin, final EEButcher command) {
        super(plugin, command, "monster");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BUTCHER_MONSTER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.BUTCHER_MONSTER_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1) {
			if(source.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
				suggests.add("all");
			}
			suggests.add("100");
			suggests.add("250");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_RADIUS.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(source instanceof EPlayer){
			EPlayer player = (EPlayer) source;
			if(args.size() == 1) {
				if (args.get(0).equals("all")){
					resultat = commandButcherMonster(player);
				} else {
					try {
						int radius = Integer.parseInt(args.get(0));
						resultat = commandButcherMonster(player, radius);
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
								.replaceAll("<number>", args.get(0)));
					}
				}
			} else {
				source.sendMessage(this.help(source));
			}
		}
		return resultat;
	}

	private boolean commandButcherMonster(final EPlayer player) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.MONSTERS.contains(entity.getType()) && entity.get(Keys.ANGRY).orElse(false)) {
		    		return true;
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_MONSTER.get()
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherMonster(final EPlayer player, final int radius) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.MONSTERS.contains(entity.getType()) && entity.get(Keys.ANGRY).orElse(false)) {
		    		if(entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius) {
			    		return true;
			    	}
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_MONSTER_RADIUS.get()
					.replaceAll("<radius>", String.valueOf(radius))
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
}