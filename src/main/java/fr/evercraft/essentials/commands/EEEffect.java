package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEffect;

public class EEEffect extends ECommand<EverEssentials> {
	
	public EEEffect(final EverEssentials plugin) {
        super(plugin, "effect", "effects");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("EFFECT"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("EFFECT_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
			return Text.builder("/effect [joueur] <effet> [niveau] [durée]").onClick(TextActions.suggestCommand("/effect "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/ping").onClick(TextActions.suggestCommand("/effect <effet> [niveau] [durée]"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> list = new ArrayList<String>();
		if (source instanceof Player){
			if(args.size() == 1){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					list = null;
				} else {
					list = UtilsEffect.getEffects();
				}
			} else if (args.size() == 2){
				if (source.hasPermission(this.plugin.getPermissions().get("EFFECT_OTHERS"))){
					list = UtilsEffect.getEffects();
				} else {
					if (UtilsEffect.getEffect(args.get(0)).isPresent()){
						UtilsEffect effect = UtilsEffect.getEffect(args.get(0)).get();
						for(int cpt = effect.getMinAmplifier(); cpt <= effect.getMaxAmplifier(); cpt++){
							list.add(String.valueOf(cpt));
						}
					} else {
						// erreur nom effect
					}
				}
			} else if (args.size() == 3){
				
			}
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandPing((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("PING_OTHERS"))){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandPingOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(this.plugin.getPermissions().noPermission());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandPing(final EPlayer player) {
		player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("PING_PLAYER")
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
		return true;
	}
	
	public boolean commandPingOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("PING_OTHERS")
					.replaceAll("<player>", player.getName())
					.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency()))));
			return true;
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
	}
}
