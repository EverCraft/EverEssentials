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
package fr.evercraft.essentials.command;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatVisibilities;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.sponge.UtilsGameMode;
import fr.evercraft.everapi.sponge.UtilsNetwork;

public class EEWhois extends ECommand<EverEssentials> {
	
	public EEWhois(final EverEssentials plugin) {
        super(plugin, "whois");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHOIS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WHOIS_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.WHOIS_OTHERS.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.WHOIS_OTHERS.get())){
			return this.getAllUsers(args.get(0), source);
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Nom du home inconnu
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandWhoisPlayer(source, (EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nom du home connu
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.WHOIS_OTHERS.get())) {
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()) {
					if(user.get() instanceof EPlayer) {
						resultat = this.commandWhoisPlayer(source, (EPlayer) user.get());
					} else {
						resultat = this.commandWhoisPlayer(source, user.get());
					}
				// Joueur introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandWhoisPlayer(final CommandSource staff, final EPlayer player) {
		List<Text> lists = new ArrayList<Text>();

		lists.add(this.getUUID(player));
		lists.add(this.getIP(player));
		lists.add(this.getPing(player));
		lists.add(this.getHeal(player));
		lists.add(this.getFood(player));
		lists.add(this.getExp());
		lists.add(this.getExpLevel(player));
		lists.add(this.getExpPoint(player));
		lists.add(this.getSpeed());
		lists.add(this.getSpeedWalk(player));
		lists.add(this.getSpeedFly(player));
		lists.add(this.getLocation(player));
		if (this.plugin.getEverAPI().getManagerService().getEconomy().isPresent()) {
			lists.add(this.getBalance(player));
		}
		lists.add(this.getGameMode(player));
		lists.add(this.getFly(player));
		lists.add(this.getGod(player));
		lists.add(this.getVanish(player));
		lists.add(this.getFreeze(player));
		lists.add(this.getAFK(player));
		lists.add(this.getFirstDatePlayed(player));
		lists.add(this.getLastDatePlayed(player));
		lists.add(this.getChatVisibility(player));
		lists.add(this.getViewDistance(player));
		lists.add(this.getChatColor(player));
		lists.add(this.getLocale(player));
		lists.add(this.getToggle(player));
		lists.add(this.getTotalTimePlayed(player));
		
		EEMessages title;
		if(player.getIdentifier().equals(staff.getIdentifier())) {
			title = EEMessages.WHOIS_TITLE_EQUALS;
		} else {
			title = EEMessages.WHOIS_TITLE_OTHERS;
		}
		
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				title.getFormat().toText("<player>", player.getName()).toBuilder()
					.onClick(TextActions.runCommand("/whois \"" + player.getName() + "\""))
					.build(), 
				lists, staff);
		return false;
	}
	
	private boolean commandWhoisPlayer(final CommandSource staff, final EUser user) {
		List<Text> lists = new ArrayList<Text>();

		lists.add(this.getUUID(user));
		if(user.getLastIP().isPresent()) {
			lists.add(this.getLastIp(user, user.getLastIP().get()));
		}
		lists.add(this.getHeal(user));
		lists.add(this.getFood(user));
		lists.add(this.getExp());
		lists.add(this.getExpLevel(user));
		lists.add(this.getExpPoint(user));
		lists.add(this.getSpeed());
		lists.add(this.getSpeedWalk(user));
		lists.add(this.getSpeedFly(user));
		if (this.plugin.getEverAPI().getManagerService().getEconomy().isPresent()) {
			lists.add(this.getBalance(user));
		}
		lists.add(this.getGameMode(user));
		lists.add(this.getFly(user));
		lists.add(this.getGod(user));
		lists.add(this.getVanish(user));
		lists.add(this.getFirstDatePlayed(user));
		lists.add(this.getLastDatePlayed(user));
		lists.add(this.getToggle(user));
		lists.add(this.getTotalTimePlayed(user));
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EEMessages.WHOIS_TITLE_OTHERS.getFormat().toText("<player>", user.getName()).toBuilder()
					.onClick(TextActions.runCommand("/whois \"" + user.getName() + "\""))
					.build(), 
				lists, staff);
		return false;
	}
	
	private Text getUUID(final EUser player){
		return EEMessages.WHOIS_UUID.getFormat()
				.toText("<uuid>", this.getButtomUUID(player));
	}
	
	private Text getButtomUUID(final EUser player){
		return EEMessages.WHOIS_UUID_STYLE.getFormat()
						.toText("<uuid>", player.getUniqueId().toString()).toBuilder()
					.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(player.getUniqueId().toString()))
					.onShiftClick(TextActions.insertText(player.getUniqueId().toString()))
					.build();
	}
	
	private Text getIP(final EPlayer player){
		return EEMessages.WHOIS_IP.getFormat()
				.toText("<ip>", getButtomIP(UtilsNetwork.getHostString(player.getConnection().getAddress().getAddress())));
	}
	
	private Text getLastIp(final User user, final InetAddress address) {
		return EEMessages.WHOIS_LAST_IP.getFormat()
				.toText("<ip>", this.getButtomIP(UtilsNetwork.getHostString(address)));
	}
	
	private Text getButtomIP(String address){
		return EEMessages.WHOIS_IP_STYLE.getFormat()
				.toText("<ip>", address).toBuilder()
			.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
			.onClick(TextActions.suggestCommand(address))
			.onShiftClick(TextActions.insertText(address))
			.build();
	}
	
	private Text getPing(final EPlayer player){
		return EEMessages.WHOIS_PING.getFormat()
				.toText("<ping>", String.valueOf(player.getConnection().getLatency()));
	}
	
	private Text getHeal(final EUser player){
		return EEMessages.WHOIS_HEAL.getFormat().toText(
				"<heal>", String.valueOf((int) Math.ceil(player.getHealth())),
				"<max_heal>", String.valueOf((int) Math.ceil(player.getMaxHealth())));
	}
	
	private Text getFood(final EUser player) {
		int saturation = (int) Math.ceil(player.getSaturation());
		if (saturation > 0) {
			return EEMessages.WHOIS_FOOD_SATURATION.getFormat().toText(
					"<food>", String.valueOf(player.getFood()),
					"<max_food>", String.valueOf(EPlayer.MAX_FOOD),
					"<saturation>", String.valueOf(saturation));
		} else {
			return EEMessages.WHOIS_FOOD.getFormat().toText(
					"<food>", String.valueOf(player.getFood()),
					"<max_food>", String.valueOf(EPlayer.MAX_FOOD));
		}
	}
	
	private Text getExp(){
		return EEMessages.WHOIS_EXP.getText();
	}
	
	private Text getExpLevel(final EUser player){
		return EEMessages.WHOIS_EXP_LEVEL.getFormat()
				.toText("<level>", String.valueOf(player.getLevel()));
	}
	
	private Text getExpPoint(final EUser player){
		return EEMessages.WHOIS_EXP_POINT.getFormat()
				.toText("<point>", String.valueOf(player.getLevel()));
	}
	
	private Text getSpeed(){
		return EEMessages.WHOIS_SPEED.getText();
	}
	
	private Text getSpeedFly(final EUser player){
		return EEMessages.WHOIS_SPEED_FLY.getFormat()
				.toText("<speed>", String.valueOf(UtilsDouble.round(player.getFlySpeed() / EPlayer.CONVERSION_FLY, 3)));
	}
	
	private Text getSpeedWalk(final EUser player){
		return EEMessages.WHOIS_SPEED_WALK.getFormat()
				.toText("<speed>", String.valueOf( UtilsDouble.round(player.getWalkSpeed() / EPlayer.CONVERSION_WALF, 3)));
	}
	
	private Text getLocation(final EPlayer player){
		return EEMessages.WHOIS_LOCATION.getFormat()
				.toText("<position>", getButtonLocation(player));
	}
	
	private Text getButtonLocation(final EPlayer player) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<x>", EReplace.of(String.valueOf(player.getLocation().getBlockX())));
		replaces.put("<y>", EReplace.of(String.valueOf(player.getLocation().getBlockY())));
		replaces.put("<z>", EReplace.of(String.valueOf(player.getLocation().getBlockZ())));
		replaces.put("<world>", EReplace.of(player.getLocation().getExtent().getName()));
		
		return EEMessages.WHOIS_LOCATION_POSITION.getFormat().toText2(replaces).toBuilder()
				.onHover(TextActions.showText(EEMessages.WHOIS_LOCATION_POSITION_HOVER.getFormat().toText2(replaces)))
				.build();
	}
	
	private Text getBalance(final EUser player) {
		return EEMessages.WHOIS_BALANCE.getFormat()
				.toText("<money>", this.plugin.getEverAPI().getManagerService().getEconomy().get().getDefaultCurrency().format(player.getBalance()).toPlain());
	}
	
	private Text getGameMode(final EUser player){
		return EEMessages.WHOIS_GAMEMODE.getFormat()
				.toText("<gamemode>", UtilsGameMode.getName(player.getGameMode()));
	}
	
	private Text getGod(final EUser player){
		if (player.isGod()) {
			return EEMessages.WHOIS_GOD_ENABLE.getText();
		} else {
			return EEMessages.WHOIS_GOD_DISABLE.getText();
		}
	}
	
	private Text getFly(final EUser player){
		if (player.getAllowFlight()) {
			if (player.isFlying()) {
				return EEMessages.WHOIS_FLY_ENABLE_FLY.getText();
			} else {
				return EEMessages.WHOIS_FLY_ENABLE_WALK.getText();
			}
		} else {
			return EEMessages.WHOIS_FLY_DISABLE.getText();
		}
	}
	
	@SuppressWarnings("unused")
	private Text getMute(final EPlayer player) {
		// TODO EverSanctions
		return EEMessages.WHOIS_MUTE_DISABLE.getText();
		/*
		if (player.isMute()) {
			return EChat.of(EEMessages.WHOIS_MUTE_ENABLE.get());
		} else {
			return EChat.of(EEMessages.WHOIS_MUTE_DISABLE.get());
		}*/
	}
	
	private Text getVanish(final EUser player){
		if (player.isVanish()) {
			return EEMessages.WHOIS_VANISH_ENABLE.getText();
		} else {
			return EEMessages.WHOIS_VANISH_DISABLE.getText();
		}
	}
	
	private Text getFreeze(final EPlayer player){
		if (player.isFreeze()) {
			return EEMessages.WHOIS_FREEZE_ENABLE.getText();
		} else {
			return EEMessages.WHOIS_FREEZE_DISABLE.getText();
		}
	}
	
	private Text getAFK(final EPlayer player){
		if (player.isAfk()) {
			return EEMessages.WHOIS_AFK_ENABLE.getText();
		} else {
			return EEMessages.WHOIS_AFK_DISABLE.getText();
		}
	}
	
	private Text getFirstDatePlayed(final EUser player){
		return EEMessages.WHOIS_FIRST_DATE_PLAYED.getFormat()
				.toText("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(player.getFirstDatePlayed()));
	}
	
	private Text getLastDatePlayed(final EPlayer player) {
		return EEMessages.WHOIS_LAST_DATE_PLAYED_ONLINE.getFormat()
				.toText("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(player.getLastDatePlayed()));
	}
	
	private Text getLastDatePlayed(final EUser player) {
		return EEMessages.WHOIS_LAST_DATE_PLAYED_OFFLINE.getFormat()
				.toText("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(player.getLastDatePlayed()));
	}
	
	private Text getTotalTimePlayed(final EUser player){
		return EEMessages.WHOIS_TOTAL_TIME_PLAYED.getFormat()
				.toText("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().diff(player.getTotalTimePlayed()));
	}
	
	private Text getChatVisibility(final EPlayer player){
		ChatVisibility chat = player.getChatVisibility();
		if (chat.equals(ChatVisibilities.FULL)){
			return EEMessages.WHOIS_CHAT_FULL.getText();
		} else if (chat.equals(ChatVisibilities.SYSTEM)){
			return EEMessages.WHOIS_CHAT_SYSTEM.getText();
		} else {
			return EEMessages.WHOIS_CHAT_HIDDEN.getText();
		}
	}
	
	private Text getViewDistance(final EPlayer player){
		return EEMessages.WHOIS_VIEW_DISTANCE.getFormat()
				.toText("<amount>", String.valueOf(player.getViewDistance()));
	}
	
	private Text getChatColor(final EPlayer player){
		if (player.isChatColorsEnabled()){
			return EEMessages.WHOIS_CHATCOLOR_ON.getText();
		} else {
			return EEMessages.WHOIS_CHATCOLOR_OFF.getText();
		}
	}
	
	private Text getLocale(final EPlayer player){
		return EEMessages.WHOIS_LANGUAGE.getFormat()
				.toText("<langue>", StringUtils.capitalize(player.getLocale().getDisplayLanguage()));
	}
	
	private Text getToggle(final EUser player){
		if (player.isToggle()) {
			return EEMessages.WHOIS_TOGGLE_ENABLE.getText();
		} else {
			return EEMessages.WHOIS_TOGGLE_DISABLE.getText();
		}
	}
}
