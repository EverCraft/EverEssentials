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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWhois extends ECommand<EverEssentials> {
	
	public EEWhois(final EverEssentials plugin) {
        super(plugin, "whois");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("WHOIS"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("WHOIS_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/whois").onClick(TextActions.suggestCommand("/whois"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandWhoisPlayer(source, (EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// Nom du home connu
		} else if(args.size() == 1) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = commandWhoisPlayer(source, optPlayer.get());
			// Joueur introuvable
			} else {
				source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandWhoisPlayer(final CommandSource staff, final EPlayer player) {
		List<Text> lists = new ArrayList<Text>();

		lists.add(getUUID(player));
		lists.add(getIP(player));
		lists.add(getPing(player));
		lists.add(getHeal(player));
		lists.add(getFood(player));
		lists.add(getExp());
		lists.add(getExpLevel(player));
		lists.add(getExpPoint(player));
		lists.add(getSpeed());
		lists.add(getSpeedWalk(player));
		lists.add(getSpeedFly(player));
		lists.add(getLocation(player));
		if(this.plugin.getEverAPI().getManagerService().getEconomy().isPresent()) {
			lists.add(getBalance(player));
		}
		lists.add(getGameMode(player));
		lists.add(getFly(player));
		lists.add(getGod(player));
		lists.add(getMute(player));
		lists.add(getVanish(player));
		lists.add(getAFK(player));
		lists.add(getFirstDatePlayed(player));
		lists.add(getLastDatePlayed(player));
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(this.plugin.getMessages().getMessage("WHOIS_TITLE").replace("<player>", player.getName())).toBuilder()
					.onClick(TextActions.runCommand("/whois " + player.getName())).build(), 
				lists, staff);
		return false;
	}
	
	public Text getUUID(final EPlayer player){
		return ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WHOIS_UUID"))
				.replace("<uuid>", getButtomUUID(player))
				.build();
	}
	
	public Text getButtomUUID(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_UUID_STYLE")
						.replaceAll("<uuid>", player.getUniqueId().toString())).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getEverAPI().getMessages().getMessage("HOVER_COPY"))))
					.onClick(TextActions.suggestCommand(player.getUniqueId().toString()))
					.onShiftClick(TextActions.insertText(player.getUniqueId().toString()))
					.build();
	}
	
	public Text getIP(final EPlayer player){
		return ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WHOIS_IP"))
				.replace("<ip>", getButtomIP(player))
				.build();
	}
	
	public Text getButtomIP(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_IP_STYLE")
				.replaceAll("<ip>", player.getConnection().getAddress().getAddress().getHostAddress().toString())).toBuilder()
			.onHover(TextActions.showText(EChat.of(this.plugin.getEverAPI().getMessages().getMessage("HOVER_COPY"))))
			.onClick(TextActions.suggestCommand(player.getConnection().getAddress().getAddress().getHostAddress().toString()))
			.onShiftClick(TextActions.insertText(player.getConnection().getAddress().getAddress().getHostAddress().toString()))
			.build();
	}
	
	public Text getPing(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_PING")
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
	}
	
	public Text getHeal(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_HEAL")
				.replaceAll("<heal>", String.valueOf((int) Math.ceil(player.getHealth())))
				.replaceAll("<max_heal>", String.valueOf((int) Math.ceil(player.getMaxHealth()))));
	}
	
	public Text getFood(final EPlayer player) {
		int saturation = (int) Math.ceil(player.getSaturation());
		if(saturation > 0) {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_FOOD_SATURATION")
				.replaceAll("<food>", String.valueOf(player.getFood()))
				.replaceAll("<max_food>", String.valueOf(EPlayer.MAX_FOOD))
				.replaceAll("<saturation>", String.valueOf(saturation)));
		} else {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_FOOD")
				.replaceAll("<food>", String.valueOf(player.getFood()))
				.replaceAll("<max_food>", String.valueOf(EPlayer.MAX_FOOD)));
		}
	}
	
	public Text getExp(){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_EXP"));
	}
	
	public Text getExpLevel(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_EXP_LEVEL")
				.replaceAll("<level>", String.valueOf(player.getLevel())));
	}
	
	public Text getExpPoint(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_EXP_POINT")
				.replaceAll("<point>", String.valueOf(player.getLevel())));
	}
	
	public Text getSpeed(){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_SPEED"));
	}
	
	public Text getSpeedFly(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_SPEED_FLY")
				.replaceAll("<speed>", String.valueOf(UtilsDouble.round(player.getFlySpeed() / EPlayer.CONVERSION_FLY, 3))));
	}
	
	public Text getSpeedWalk(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_SPEED_WALK")
				.replaceAll("<speed>", String.valueOf( UtilsDouble.round(player.getWalkSpeed() / EPlayer.CONVERSION_WALF, 3))));
	}
	
	public Text getLocation(final EPlayer player){
		return ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("WHOIS_LOCATION"))
				.replace("<position>", getButtonLocation(player))
				.build();
	}
	
	public Text getButtonLocation(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_LOCATION_POSITION")
					.replaceAll("<x>", String.valueOf(player.getLocation().getBlockX()))
					.replaceAll("<y>", String.valueOf(player.getLocation().getBlockY()))
					.replaceAll("<z>", String.valueOf(player.getLocation().getBlockZ()))
					.replaceAll("<world>", player.getLocation().getExtent().getName())).toBuilder()
				.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("WHOIS_LOCATION_POSITION_HOVER")
						.replaceAll("<x>", String.valueOf(player.getLocation().getBlockX()))
						.replaceAll("<y>", String.valueOf(player.getLocation().getBlockY()))
						.replaceAll("<z>", String.valueOf(player.getLocation().getBlockZ()))
						.replaceAll("<world>", player.getLocation().getExtent().getName()))))
				.build();
	}
	
	public Text getBalance(final EPlayer player) {
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_BALANCE")
				.replaceAll("<money>", this.plugin.getEverAPI().getManagerService().getEconomy().get().getDefaultCurrency().format(player.getBalance()).toPlain()));
	}
	
	public Text getGameMode(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_GAMEMODE")
				.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(player.getGameMode())));
	}
	
	public Text getGod(final EPlayer player){
		if(player.isGod()) {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_GOD_ENABLE"));
		} else {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_GOD_DISABLE"));
		}
	}
	
	public Text getFly(final EPlayer player){
		if(player.getAllowFlight()) {
			if(player.isFlying()) {
				return EChat.of(this.plugin.getMessages().getMessage("WHOIS_FLY_ENABLE_FLY"));
			} else {
				return EChat.of(this.plugin.getMessages().getMessage("WHOIS_FLY_ENABLE_WALK"));
			}
		} else {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_FLY_DISABLE"));
		}
	}
	
	public Text getMute(final EPlayer player){
		if(player.isMute()) {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_MUTE_ENABLE"));
		} else {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_MUTE_DISABLE"));
		}
	}
	
	public Text getVanish(final EPlayer player){
		if(player.isVanish()) {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_VANISH_ENABLE"));
		} else {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_VANISH_DISABLE"));
		}
	}
	
	public Text getAFK(final EPlayer player){
		if(player.isAFK()) {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_AFK_ENABLE"));
		} else {
			return EChat.of(this.plugin.getMessages().getMessage("WHOIS_AFK_DISABLE"));
		}
	}
	
	public Text getFirstDatePlayed(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_FIRST_DATE_PLAYED")
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(player.getFirstDatePlayed())));
	}
	
	public Text getLastDatePlayed(final EPlayer player){
		return EChat.of(this.plugin.getMessages().getMessage("WHOIS_LAST_DATE_PLAYED")
				.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(player.getLastDatePlayed(), 3)));
	}
}
