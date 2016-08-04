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
package fr.evercraft.essentials;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EEMessage extends EMessage {

	public EEMessage(EverEssentials plugin, String name) {
		super(plugin, EEMessages.values());
	}

	public enum EEMessages implements EnumMessage {
		PREFIX("prefix", 									"[&4Ever&6&lEssentials&f] "),
		DESCRIPTION("description", 							"Information sur EverEssentials"),
		
		AFK_DESCRIPTION("afk.description", 										"Permet de vous signaler AFK"),
		AFK_KICK("afk.kick", 													"&cPour cause d'inactivité"),
		
		AFK_ON_DESCRIPTION("afk.on.description", 								"Rend le joueur invulnérable"),

		AFK_ON_PLAYER("afk.on.player", 											"&7Vous êtes désormais AFK."),
		AFK_ON_PLAYER_ERROR("afk.on.playerError", 								"&cVous êtes déjà AFK."),
		AFK_ON_PLAYER_CANCEL("afk.on.playerCancel", 							"&cImpossible de vous mettre AFK."),
		AFK_ON_ALL("afk.on.all", 												"&6" + EChat.DISPLAYNAME_FORMAT + " &7est désormais AFK.", "The message may be empty"),
		AFK_ON_OTHERS_PLAYER("afk.on.othersPlayer", 							"&7Vous êtes désormais AFK."),
		AFK_ON_OTHERS_STAFF("afk.on.othersStaff", 								"&6<player> &7est désormais AFK."),
		AFK_ON_OTHERS_ERROR("afk.on.othersError", 								"&6<player> &cest déjà signalé AFK."),
		AFK_ON_OTHERS_CANCEL("afk.on.othersCancel", 							"&cImpossible de rendre &6<player> &cinvulnérable."),

		AFK_OFF_DESCRIPTION("afk.off.description", 								"Rend le joueur vulnérable"),

		AFK_OFF_PLAYER("afk.off.player", 										"&7Vous n'êtes plus AFK."),
		AFK_OFF_PLAYER_ERROR("afk.off.playerError", 							"&cVous n'êtes pas AFK."),
		AFK_OFF_PLAYER_CANCEL("afk.off.playerCancel", 							"&cImpossible de vous rendre vulnérable."),
		AFK_OFF_ALL("afk.off.all", 												"&6" + EChat.DISPLAYNAME_FORMAT + " &7n'est plus AFK.", "The message may be empty"),
		AFK_OFF_OTHERS_PLAYER("afk.off.othersPlayer", 							"&7Vous n'êtes plus AFK."),
		AFK_OFF_OTHERS_STAFF("afk.off.othersStaff", 							"&6<player> &7n'est plus AFK."),
		AFK_OFF_OTHERS_ERROR("afk.off.othersError", 							"&6<player> &cn'est pas AFK."),
		AFK_OFF_OTHERS_CANCEL("afk.off.othersCancel", 							"&cImpossible de rendre sortir &6<player> &cd'AFK."),
		
		AFK_STATUS_DESCRIPTION("afk.status.description", 						"Affiche si le joueur est AFK où pas"),
		AFK_STATUS_PLAYER_ON("afk.status.playerOn", 							"&7Vous êtes AFK."),
		AFK_STATUS_PLAYER_OFF("afk.status.playerOff", 							"&7Vous n'êtes pas AFK."),
		AFK_STATUS_OTHERS_ON("afk.status.othersOn", 							"&6<player> &7est AFK."),
		AFK_STATUS_OTHERS_OFF("afk.status.othersOff", 							"&6<player> &7n'est pas AFK."),
		
		BACK_DESCRIPTION("back.description",				"Retourne à la dernière position sauvegardé."),
		BACK_NAME("back.name", 								"&6&lposition"),
		BACK_NAME_HOVER("back.nameHover", 					"&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		BACK_TELEPORT("back.teleport", 						"&7Vous avez été téléporté à votre dernière <back>&7."),
		BACK_INCONNU("back.inconnu", 						"&cVous n'avez aucune position sauvegardé."),
		BACK_DELAY("back.delay", 							"&7Votre téléportation commencera dans &6<delay>&7. Ne bougez pas."),
		BACK_ERROR_LOCATION("back.errorLocation", 			"&cImpossible de trouver une position pour réaliser une téléportation."),
		
		BED_DESCRIPTION("bed.description", 					"Retourne à la dernière position ou vous avez dormi"),
		
		BROADCAST_DESCRIPTION("broadcast.description", 		"Envoie un message à tous les joueurs."),
		BROADCAST_MESSAGE("broadcast.message", 				"&7[&6&lBroadcast&7] <message>"),
		
		BOOK_DESCRIPTION("book.description", 				"Permet de modifier un livre."),
		BOOK_WRITABLE("book.writable", 						""),
		BOOK_NO_WRITTEN("book.noWritten", 					""),
		
		BUTCHER_DESCRIPTION("butcher.description", 			"Supprime les entités dans un monde ou dans un rayon."),
		BUTCHER_NOENTITY("butcher.noEntity", 				"&cIl y a aucune entité à supprimer."),
		BUTCHER_ENTITY_COLOR("butcher.entityColor", 		"&6"),
		BUTCHER_ANIMAL("butcher.killAnimal", 				"&7Suppression de &6<count> &7animaux dans ce monde."),
		BUTCHER_ANIMAL_RADIUS("butcher.killAnimalRadius", 	"&7Suppression de &6<count> &7animaux dans un rayon de &6<radius> bloc(s)&7."),
		BUTCHER_MONSTER("butcher.killMonster", 				"&7Suppression de &6<count> &7monstre(s) dans ce monde."),
		BUTCHER_MONSTER_RADIUS("butcher.killMonsterRadius", "&7Suppression de &6<count> &7monstre(s) dans un rayon de &6<radius> bloc(s)&7."),
		BUTCHER_ALL("butcher.killAll",						"&7Suppression de &6<count> &7entité(s) dans ce monde."),
		BUTCHER_ALL_RADIUS("butcher.killAllRadius", 		"&7Suppression de &6<count> &7entité(s) dans un rayon de &6<radius> bloc(s)&7."),
		BUTCHER_TYPE("butcher.killType", 					"&7Suppression de &6<count> &6<entity>&6(s)&7 dans ce monde."),
		BUTCHER_TYPE_RADIUS("butcher.killTypeRadius", 		"&7Suppression de &6<count> &6<entity>&6(s)&7 dans un rayon de &6<radius> bloc(s)&7."),
		
		CLEAREFFECT_DESCRIPTION("cleareffect.description", 		"Supprime tous les effets de potions d'un joueur."),
		CLEAREFFECT_PLAYER("cleareffect.player", 				"&7Tous vos effets de potions ont été supprimés."),
		CLEAREFFECT_NOEFFECT("cleareffect.noEffect", 				"&cErreur : Aucun effet de potion à supprimer."),
		CLEAREFFECT_OTHERS_PLAYER("cleareffect.othersPlayer", 	"&7Tous les effets de potions ont été supprimés par &6<staff>&7."),
		CLEAREFFECT_OTHERS_STAFF("cleareffect.othersStaff", 	"&7Tous les effets de potions de &6<player> &7ont été supprimés."),
		
		CLEARINVENTORY_DESCRIPTION("clearinventory.description", 	"Supprime tous les objets de l'inventaire d'un joueur."),
		CLEARINVENTORY_PLAYER("clearinventory.player", 				"&7Vous venez de supprimer &6<amount> &7objet(s) de votre inventaire."),
		CLEARINVENTORY_NOITEM("clearinventory.noItem", 				"&cErreur : Aucun objet à supprimer."),
		CLEARINVENTORY_OTHERS_PLAYER("clearinventory.othersPlayer", "&6<staff> &7vient de supprimer &6<amount>&7 objet(s) de votre inventaire."),
		CLEARINVENTORY_OTHERS_STAFF("clearinventory.othersStaff", 	"&7Vous venez de supprimer &6<amount> &7objet(s) de l'inventaire de &6<player>&7."),
		
		COLOR_DESCRIPTION("color.description", 				"Affiche les différentes couleurs dans Minecraft."),
		COLOR_LIST_MESSAGE("color.listTitle", 				"&l&7Liste des couleurs :"), 
		COLOR_LIST_TITLE("color.listMessage", 				"<color>█ &0: <id>-<name>"), 
		
		EFFECT_DESCRIPTION("effect.description", 			"Ajoute un effet de potion sur un joueur."),
		EFFECT_ERROR_NAME("effect.errorName", 				"&cErreur : nom de l'effet invalide."),
		EFFECT_ERROR_DURATION("effect.errorDuration", 		"&cErreur : la durée de l'effet doit être compris entre <min> et <max>."),
		EFFECT_ERROR_AMPLIFIER("effect.errorAmplifier", 	"&cErreur : l'amplification de l'effet doit être compris entre <min> et <max>."),
		
		ENCHANT_DESCRIPTION("enchant.description", 			"Enchante l'objet dans votre main."),
		ENCHANT_NOT_FOUND("enchant.notFound", 				"&cErreur : cet enchantement n'existe pas."),
		ENCHANT_LEVEL_TOO_HIGHT("enchant.levelTooHight", 	"&cErreur : le niveau de cet enchantement est trop élevé."),
		ENCHANT_INCOMPATIBLE("enchant.incompatible", 		"&cErreur : cet enchantement est incompatible avec &6<item>"),
		ENCHANT_NAME("enchant.name", 						"&6<item>"),
		ENCHANT_SUCCESSFULL("enchant.successfull", 			"&7L'enchantement a bien été appliqué sur l'objet."),
				
		EXP_DESCRIPTION("exp.description", 							"Modifie l'expérience d'un joueur."),
		EXP_GIVE_LEVEL("exp.giveLevel",								"&7Vous vous êtes ajouté &6<level> &7niveau(x)."),
		EXP_GIVE_EXP("exp.giveExp", 								"&7Vous vous êtes ajouté &6<experience> &7point(s) d'expérience."),
		EXP_SET_LEVEL("exp.setLevel", 								"&7Vous avez défini votre niveau à &6<level>&7."),
		EXP_SET_EXP("exp.setExp", 									"&7Vous avez défini votre expérience à &6<experience>&7."),
		EXP_OTHERS_PLAYER_GIVE_LEVEL("exp.othersPlayerGiveLevel", 	"&7Vous avez reçu &6<level> &7niveau(x) par &6<staff>&7."),
		EXP_OTHERS_STAFF_GIVE_LEVEL("exp.othersStaffGiveLevel", 	"&7Vous avez ajouté &6<level> &7niveau(x) à &6<player>&7."),
		EXP_OTHERS_PLAYER_GIVE_EXP("exp.othersPlayerGiveExp", 		"&7Vous avez reçu &6<experience> &7point(s) d'expérience par &6<staff>&7."),
		EXP_OTHERS_STAFF_GIVE_EXP("exp.othersStaffGiveExp", 		"&7Vous avez ajouté &6<experience> &7point(s) d'expérience à &6<player>&7."),
		EXP_OTHERS_PLAYER_SET_LEVEL("exp.othersPlayerSetLevel", 	"&7Votre niveau a été modifié à &6<level> &7par &6<staff>&7."),
		EXP_OTHERS_STAFF_SET_LEVEL("exp.othersStaffSetLevel", 		"&7Vous avez modifié le niveau de &6<player> &7à &6<level>&7."),
		EXP_OTHERS_PLAYER_SET_EXP("exp.othersPlayerSetExp", 		"&7Votre expérience a été modifié à &6<experience> &7par &6<staff>&7."),
		EXP_OTHERS_STAFF_SET_EXP("exp.othersStaffSetExp", 			"&7Vous avez modifié l'expérience de &6<player> &7à &6<experience>&7."),
		
		EXT_DESCRIPTION("ext.description", 					"Enleve le feu sur un joueur."),
		EXT_PLAYER("ext.player", 							"&7Vous n'êtes plus en feu."),
		EXT_PLAYER_ERROR("ext.playerError", 				"&7Vous n'êtes pas en feu."),
		EXT_OTHERS_PLAYER("ext.othersPlayer", 				"&7Vous n'êtes plus en feu grâce à &6<staff>&7."),
		EXT_OTHERS_STAFF("ext.othersStaff", 				"&7Vous avez enlevé le feu sur &6<player>&7."),
		EXT_OTHERS_ERROR("ext.othersError", 				"&6<player> &7n'est pas en feu."),
		
		FEED_DESCRIPTION("feed.description", 				"Satisfait la faim d'un joueur."),
		FEED_PLAYER("feed.player", 							"&7Vous vous êtes rassasié."),
		FEED_OTHERS_STAFF("feed.othersStaff", 				"&7Vous avez rassasié &6<player>."),
		FEED_OTHERS_PLAYER("feed.othersPlayer", 			"&7Vous avez été rassasié par &6<staff>&7."),
		FEED_ALL_STAFF("feed.allStaff", 					"&7Vous avez rassasié tous les joueurs."),
		
		FREEZE_DESCRIPTION("freeze.description", 										"Gère la paralysie sur un joueur"),
		
		FREEZE_ON_DESCRIPTION("freeze.on.description",									"Paralyse un joueur"),
		FREEZE_ON_PLAYER("freeze.on.player",  											"&7Vous êtes paralysé."),
		FREEZE_ON_PLAYER_ERROR("freeze.on.playerError",									"&cErreur : Vous êtes déjà paralysé."),
		FREEZE_ON_PLAYER_CANCEL("freeze.on.playerCancel",								"&7Vous venez d'être paralysé par &6<staff>&7."),
		FREEZE_ON_OTHERS_PLAYER("freeze.on.othersStaffEnable", 							"&7Vous venez de paralyser &6<player>&7."),
		FREEZE_ON_OTHERS_STAFF("freeze.on.othersStaffEnableError", 						"&cErreur : <player> &7est déjà paralysé."),
		FREEZE_ON_OTHERS_ERROR("freeze.on.othersError", 								"&cErreur : &6<player> &cest déjà paralysé."),
		FREEZE_ON_OTHERS_CANCEL("freeze.on.othersCancel", 								"&cImpossible de paralyser &6<player>&c."),
		
		FREEZE_OFF_DESCRIPTION("freeze.off.description", 								"Libère un joueur paralysé"),
		FREEZE_OFF_PLAYER("freeze.off.player", 											"&7Vous êtes désormais libre."),
		FREEZE_OFF_PLAYER_ERROR("freeze.off.playerError", 								"&cErreur : Vous êtes déjà libre."),
		FREEZE_OFF_PLAYER_CANCEL("freeze.off.playerCancel",								"&7Vous êtes libre grâce à &6<staff>&7."),
		FREEZE_OFF_OTHERS_PLAYER("freeze.off.othersPlayer",								"&7Vous venez de libérer &6<player>&7."),
		FREEZE_OFF_OTHERS_STAFF("freeze.off.othersStaff", 								"&cErreur : <player> &7est déjà libre."),
		FREEZE_OFF_OTHERS_ERROR("freeze.off.othersError", 								"&cErreur : &6<player> &cest déjà paralysé."),
		FREEZE_OFF_OTHERS_CANCEL("freeze.off.othersCancel", 							"&cImpossible de paralyser &6<player>&c."),
		
		FREEZE_STATUS_DESCRIPTION("freeze.status.description", 							"Affiche si le joueur est paralysé où libre"),
		FREEZE_STATUS_PLAYER_ON("freeze.status.playerOn", 								"&7Vous êtes paralysé."),
		FREEZE_STATUS_PLAYER_OFF("freeze.status.playerOff", 							"&7Vous êtes libre."),
		FREEZE_STATUS_OTHERS_ON("freeze.status.othersOn", 								"&6<player> &7est paralysé."),
		FREEZE_STATUS_OTHERS_OFF("freeze.status.othersOff", 							"&6<player> &7est libre."),

		FLY_DESCRIPTION("fly.description", 										"Permet de vous envoler"),
		FLY_ON_DESCRIPTION("fly.on.description", 								"Permet d'accorder le droit de s'envoler à un joueur"),

		FLY_ON_PLAYER("fly.on.player", 											"&7Vous pouvez désormais vous envoler."),
		FLY_ON_PLAYER_ERROR("fly.on.playerError", 								"&cVous possèdez déjà le droit de vous envoler."),
		FLY_ON_PLAYER_CANCEL("fly.on.playerCancel", 							"&cImpossible de vous rendre invulnérable."),
		FLY_ON_OTHERS_PLAYER("fly.on.othersPlayer", 							"&7Vous pouvez désormais vous envoler grâce à &6<staff>&7."),
		FLY_ON_OTHERS_STAFF("fly.on.othersStaff", 								"&7Vous venez d'accorder le droit de s'envoler à &6<player>&7."),
		FLY_ON_OTHERS_ERROR("fly.on.othersError", 								"&6<player> &7possède déjà le droit de s'envoler."),
		FLY_ON_OTHERS_CANCEL("fly.on.othersCancel", 							"&cImpossible d'accorder le droit de s'envoler à &6<player>&c."),

		FLY_OFF_DESCRIPTION("fly.off.description", 								"Permet de retirer le droit de s'envoler à un joueur"),

		FLY_OFF_PLAYER("fly.off.player", 										"&7Vous ne pouvez plus vous envoler."),
		FLY_OFF_PLAYER_ERROR("fly.off.playerError", 							"&7Vous ne pouvez pas vous envoler."),
		FLY_OFF_PLAYER_CREATIVE("fly.off.playerCreative", 						"&7Vous ne pouvez pas vous enlever le droit de vous envoler quand vous êtes en mode créative."),
		FLY_OFF_PLAYER_CANCEL("fly.off.playerCancel", 							"&cImpossible de vous rendre vulnérable."),
		FLY_OFF_OTHERS_PLAYER("fly.off.othersPlayer", 							"&7Vous ne pouvez plus vous envoler à cause de &6<staff>&7."),
		FLY_OFF_OTHERS_STAFF("fly.off.othersStaff", 							"&7Vous venez de retirer le droit de s'envoler à &6<player>&7."),
		FLY_OFF_OTHERS_ERROR("fly.off.othersError", 							"&6<player> &7ne possède pas le droit de s'envoler."),
		FLY_OFF_OTHERS_CREATIVE("fly.off.othersCreative", 						"&cVous ne pouvez pas enlever le droit de s'envoler à &6<player> &ccar il est en mode créative."),
		FLY_OFF_OTHERS_CANCEL("fly.off.othersCancel", 							"&cImpossible de retirer le droit de s'envoler à &6<player>&c."),
		
		FLY_STATUS_DESCRIPTION("fly.status.description", 						"Permet de savoir si un joueur peut s'envoler"),
		FLY_STATUS_PLAYER_ON("fly.status.playerOn", 							"&7Vous pouvez vous envoler."),
		FLY_STATUS_PLAYER_OFF("fly.status.playerOff", 							"&7Vous ne pouvez pas vous envoler."),
		FLY_STATUS_OTHERS_ON("fly.status.othersOn", 							"&6<player> &7peut s'envoler."),
		FLY_STATUS_OTHERS_OFF("fly.status.othersOff", 							"&6<player> &7ne peut pas s'envoler."),
		
		GAMEMODE_DESCRIPTION("gamemode.description", 					"Change le mode de jeu d'un joueur"),
		GAMEMODE_PLAYER_CHANGE("gamemode.playerChange", 				"&7Vous êtes désormais en mode de jeu &6<gamemode>&7."),
		GAMEMODE_PLAYER_EQUAL("gamemode.playerEqual", 					"&7Vous êtes déjà en mode de jeu &6<gamemode>&7."),
		GAMEMODE_OTHERS_STAFF_CHANGE("gamemode.othersStaffChange", 		"&7Mode de jeu &6<gamemode> &7pour &6<player>&7."),
		GAMEMODE_OTHERS_PLAYER_CHANGE("gamemode.othersPlayerChange", 	"&7Votre mode de jeu a été changé en &6<gamemode> &7par &6<staff>&7."),
		GAMEMODE_OTHERS_EQUAL("gamemode.othersEqual", 					"&6<player> &7possède déjà le mode de jeu &6<gamemode>&7."),
		GAMEMODE_ERROR_NAME("gamemode.errorName", 						"&cMode de jeu inconnu."),
		
		GENERATE_DESCRIPTION("generate.description", 								"Initialise tous les chunks d'un monde"),
		GENERATE_WARNING("generate.warning.message", 								"&cAttention : &7Générer tous les chunks d'un monde peut prendre plusieurs heures et causer des latences."
				+ "[RT] Le nombre total de chunks a générer est de &6<chunk>&7."
				+ "[RT] Souhaitez-vous vraiment générer tous les chuncks du monde &6<world> ? <confirmation>"),
		GENERATE_WARNING_VALID("generate.warning.confirmationValid", 				"&2&nConfirmer"),
		GENERATE_WARNING_VALID_HOVER("generate.warning.confirmationValidHover", 	"&cCliquez ici pour lancer la génération des chunks dans le monde &6<world>&7."),
		GENERATE_LAUNCH("generate.launch", 											"&7Génération du monde &6<world> &7lancée avec succès."),
				
		GETPOS_DESCRIPTION("getpos.description", 		"Affiche les coordonnées d'un joueur"),
		GETPOS_MESSAGE("getpos.message", 				"&7Voici votre &6<position>&7."),
		GETPOS_MESSAGE_OTHERS("getpos.messageOthers", 	"&7Voici la <position> &7de &6<player>&7."),
		GETPOS_POTISITON_NAME("getpos.positionName", 	"&6&lposition"),
		GETPOS_POSITION_HOVER("getpos.positionHover", 	"&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		
		GOD_DESCRIPTION("god.description", 										"Gère l'invulnérabilité d'un joueur"),
		
		GOD_ON_DESCRIPTION("god.on.description", 								"Rend le joueur invulnérable"),
		GOD_ON_PLAYER("god.on.player", 											"&7Vous êtes désormais invulnérable."),
		GOD_ON_PLAYER_ERROR("god.on.playerError", 								"&cErreur : Vous êtes déjà invulnérable."),
		GOD_ON_PLAYER_CANCEL("god.on.playerCancel", 							"&cImpossible de vous rendre invulnérable."),
		GOD_ON_OTHERS_PLAYER("god.on.othersPlayer", 							"&7Vous êtes désormais invulnérable grâce à &6<staff>&7."),
		GOD_ON_OTHERS_STAFF("god.on.othersStaff", 								"&7Vous venez de rendre invulnérable &6<player>&7."),
		GOD_ON_OTHERS_ERROR("god.on.othersError", 								"&cErreur : &6<player> &cest déjà invulnérable."),
		GOD_ON_OTHERS_CANCEL("god.on.othersCancel", 							"&cImpossible de rendre &6<player> &cinvulnérable."),
		
		GOD_OFF_DESCRIPTION("god.off.description", 								"Rend le joueur vulnérable"),
		GOD_OFF_PLAYER("god.off.player", 										"&7Vous êtes désormais vulnérable."),
		GOD_OFF_PLAYER_ERROR("god.off.playerError", 							"&cErreur : Vous êtes déjà vulnérable."),
		GOD_OFF_PLAYER_CANCEL("god.off.playerCancel", 							"&cImpossible de vous rendre vulnérable."),
		GOD_OFF_OTHERS_PLAYER("god.off.othersPlayer", 							"&7Vous n'êtes plus invulnérable à cause de &6<staff>&7."),
		GOD_OFF_OTHERS_STAFF("god.off.othersStaff", 							"&7Vous venez de rendre vulnérable &6<player>&7."),
		GOD_OFF_OTHERS_ERROR("god.off.othersError", 							"&cErreur : &6<player> &cest déjà vulnérable."),
		GOD_OFF_OTHERS_CANCEL("god.off.othersCancel", 							"&cImpossible de rendre &6<player> &cvulnérable."),
		
		GOD_STATUS_DESCRIPTION("god.status.description", 						"Affiche si le joueur est vulnérable où pas"),
		GOD_STATUS_PLAYER_ON("god.status.playerOn", 							"&7Vous êtes invulnérable."),
		GOD_STATUS_PLAYER_OFF("god.status.playerOff", 							"&7Vous êtes vulnérable."),
		GOD_STATUS_OTHERS_ON("god.status.othersOn", 							"&6<player> &7est invulnérable."),
		GOD_STATUS_OTHERS_OFF("god.status.othersOff", 							"&6<player> &7est vulnérable."),
		
		GOD_TELEPORT("god.teleport", 											"&7Vous avez été téléporté car vous étiez en train de tomber dans le vide."),
		
		HAT_DESCRIPTION("hat.description", 				"Place l'objet dans votre main sur votre tête"),
		HAT_ITEM_COLOR("hat.itemColor", 				"&6"),
		HAT_NO_EMPTY("hat.noEmpty", 					"&7Vous ne pouvez pas mettre un objet sur votre tête quand vous avez un <item>&7."),
		HAT_IS_HAT("hat.isHat", 						"&7Votre nouveau chapeau : &6<item>&7."),
		HAT_REMOVE("hat.remove", 						"&7Vous avez enlevé l'objet sur votre chapeau."),
		HAT_REMOVE_EMPTY("hat.removeEmpty", 			"&cVous n'avez actuellement aucun chapeau."),
		
		HEAL_DESCRIPTION("heal.description", 			"Soigne un joueur."),
		HEAL_PLAYER("heal.player", 						"&7Vous vous êtes soigné."),
		HEAL_PLAYER_DEAD("heal.playerDead", 			"&7Vous êtes déjà mort."),
		HEAL_OTHERS_PLAYER("heal.othersPlayer", 		"&7Vous avez été soigné par &6<staff>&7."),
		HEAL_OTHERS_STAFF("heal.othersStaff", 			"&7Vous avez soigné &6<player>&7."),
		HEAL_OTHERS_DEAD_STAFF("heal.othersDeadStaff", 	"&6<player>&7 est déjà mort."),
		HEAL_ALL_STAFF("heal.allStaff", 				"&7Vous avez soigné tous les joueurs."),
		
		HELP_DESCRIPTION("help.description", 			"Affiche les informations sur les commandes disponibles sur le serveur."),
		HELP_TITLE("help.title", 						"&aListe des commandes"),
		HELP_SEARCH_TITLE("help.searchtitle", 			"&aListe des commandes contenant '<command>'"),
		
		HOME_NAME("home.name", 							"&6&l<name>"),
		HOME_NAME_HOVER("home.nameHover", 				"&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		
		HOME_DESCRIPTION("home.description", 					"Téléporte le joueur à une résidence."),
		HOME_LIST_TITLE("home.listTitle", 						"&aListe des résidences"),
		HOME_LIST_LINE("home.listLine", 						"    &6&l➤  <home> &7: <teleport> <delete>"),
		HOME_LIST_LINE_ERROR_WORLD("home.listLineErrorWorld", 	"    &6&l➤  <home> &7: <delete>"),
		HOME_LIST_TELEPORT("home.listTeleport", 				"&a&nTéléporter"),
		HOME_LIST_TELEPORT_HOVER("home.listTeleportHover",		"&cCliquez ici pour vous téléporter à la résidence &6<home>&c."),
		HOME_LIST_DELETE("home.listDelete", 					"&c&nSupprimer"),
		HOME_LIST_DELETE_HOVER("home.listDeleteHover", 			"&cCliquez ici pour supprimer la résidence &6<home>&c."),
		HOME_EMPTY("home.empty", 								"&cVous n'avez aucune résidence."),
		HOME_INCONNU("home.inconnu", 							"&cVous n'avez pas de résidence qui s'appelle &6<home>&c."),
		HOME_TELEPORT("home.teleport", 							"&7Vous avez été téléporté à la résidence &6<home>&7."),
		HOME_TELEPORT_ERROR("home.teleportError", 				"&cImpossible de vous téléporter à la résidence &6<home>&c."),

		DELHOME_DESCRIPTION("delhome.description", 							"Supprime une résidence"),
		DELHOME_CONFIRMATION("delhome.confirmation", 						"&7Souhaitez-vous vraiment supprimer la résidence &6<home> &7: <confirmation>"),
		DELHOME_CONFIRMATION_VALID("delhome.confirmationValid", 			"&2&nConfirmer"),
		DELHOME_CONFIRMATION_VALID_HOVER("delhome.confirmationValidHover", 	"&cCliquez ici pour supprimer la résidence &6<home>&c."),
		DELHOME_DELETE("delhome.delete",									"&7Vous avez supprimé la résidence &6<home>&7."),
		DELHOME_INCONNU("delhome.inconnu", 									"&cVous n'avez pas de résidence qui s'appelle &6<home>&c."),
		
		HOMEOTHERS_DESCRIPTION("homeOthers.description", 					"Gère les résidences d'un joueur"),
		HOMEOTHERS_LIST_TITLE("homeOthers.listTitle", 						"&aListe des résidences de <player>"),
		HOMEOTHERS_LIST_LINE("homeOthers.listLine", 						"    &6&l➤  <home> &7: <teleport> <delete>"),
		HOMEOTHERS_LIST_TELEPORT("homeOthers.listTeleport", 				"&a&nTéléporter"),
		HOMEOTHERS_LIST_TELEPORT_HOVER("homeOthers.listTeleportHover", 		"&cCliquez ici pour vous téléporter à la résidence &6<home> &cde &6<player>&c."),
		HOMEOTHERS_LIST_DELETE("homeOthers.listDelete", 					"&c&nSupprimer"),
		HOMEOTHERS_LIST_DELETE_HOVER("homeOthers.listDeleteHover", 			"&cCliquez ici pour supprimer la résidence &6<home> &cde &6<player>&c."),
		HOMEOTHERS_EMPTY("homeOthers.empty", 								"&6<player> &cn'a aucune résidence."),
		HOMEOTHERS_INCONNU("homeOthers.inconnu",							"&6<player> &cn'a pas de résidence qui s'appelle &6<home>&c."),
		HOMEOTHERS_TELEPORT("homeOthers.teleport", 							"&7Vous avez été téléporté à la résidence &6<home> &7de &6<player>&7."),
		HOMEOTHERS_TELEPORT_ERROR("homeOthers.teleportError", 				"&cImpossible de vous téléporter à la résidence &6<home> &cde &6<player>&c."),
		HOMEOTHERS_DELETE_CONFIRMATION("homeOthers.deleteConfirmation", 	"&7Souhaitez-vous vraiment supprimer la résidence &6<home> &7de &6<player> &7: <confirmation>"),
		HOMEOTHERS_DELETEE_CONFIRMATION_VALID("homeOthers.deleteConfirmationValid", 			"&2&nConfirmer"),
		HOMEOTHERS_DELETE_CONFIRMATION_VALID_HOVER("homeOthers.deleteConfirmationValidHover", 	"&cCliquez ici pour supprimer la résidence &6<home> &cde &6<player>&c."),
		HOMEOTHERS_DELETE("homeOthers.delete", 													"&7Vous avez supprimé la résidence &6<home> &7de &6<player>&7."),
		
		SETHOME_DESCRIPTION("sethome.description", 						"Défini une résidence"),
		SETHOME_SET("sethome.set", 										"&7Vous avez défini votre résidence."),
		SETHOME_SET_CANCEL("sethome.setCancel", 						"&cImpossible de définir votre résidence."),
		SETHOME_MOVE("sethome.move", 									"&7Vous avez redéfini votre résidence."),
		SETHOME_MOVE_CANCEL("sethome.moveCancel", 						"&cImpossible de redéfinir votre résidence."),
		SETHOME_MULTIPLE_SET("sethome.multipleSet", 					"&7Vous avez défini la résidence &6<home>&7."),
		SETHOME_MULTIPLE_SET_CANCEL("sethome.multipleSetCancel", 		"&cImpossible de définir la résidence &6<home>&7."),
		SETHOME_MULTIPLE_MOVE("sethome.multipleMove", 					"&7Vous avez redéfini la résidence &6<home>&7."),
		SETHOME_MULTIPLE_MOVE_CANCEL("sethome.multipleMoveCancel", 		"&cImpossible de redéfinir la résidence &6<home>&7."),
		SETHOME_MULTIPLE_ERROR_MAX("sethome.multipleErrorMax", 			"&cVous ne pouvez pas créer plus de <nombre> résidence(s)."),
		SETHOME_MULTIPLE_NO_PERMISSION("sethome.multipleNoPermission", 	"&cVous n'avez pas la permission d'avoir plusieurs résidences."),
		
		INFO_DESCRIPTION("info.description", "Indique le type d'un item"),
		INFO_PLAYER("info.player", "&7Le type de l'objet <item> &7est &6<type>&7."),
		INFO_ITEM_COLOR("info.itemColor", "&6"),
		
		ITEM_DESCRIPTION("item.description", "Donne un item spécifique"),
		ITEM_ERROR_ITEM_NOT_FOUND("item.error.itemNotFound", "&cErreur : L'objet <item> n'existe pas."),
		ITEM_ERROR_ITEM_BLACKLIST("item.error.itemBlacklist", "&cErreur : Vous ne pouvez pas vous donner cet objet car il se trouve dans la liste noire."),
		ITEM_ERROR_QUANTITY("item.error.quantity", "&cErreur : La quantité doit être compris entre &60 &7et &6<nb> &7objet(s)."),
		ITEM_ERROR_TYPE("item.error.type", "&cErreur : Le type de l'objet est incorrect."),
		ITEM_GIVE("item.give", "&7Vous avez reçu <item>"),
		ITEM_GIVE_COLOR("item.giveColor", "&6"),
		
		JUMP_DESCRIPTION("jump.description", "Vous téléporte à l'endroit de votre choix"),
		JUMP_TELEPORT("jump.teleport", "&7Vous avez été téléporté à l'endroit de votre choix."),
		JUMP_TELEPORT_ERROR("jump.teleportError", "&7Impossible de trouver une position pour vous téléporter."),
		
		KICK_DESCRIPTION("kick.description", "Expulse un joueur du serveur"),
		KICK_MESSAGE("kick.message", "&c&lExpulsion du serveur[RT][RT]&cRaison : &7<message>[RT]"),
		
		KICKALL_DESCRIPTION("kickall.description", "Expulse tous les joueurs du serveur"),
		KICKALL_MESSAGE("kickall.message", "&c&lExpulsion du serveur[RT][RT]&cRaison : &7<message>[RT]"),
		
		KILL_DESCRIPTION("kill.description", "Tue un joueur"),
		KILL_PLAYER("kill.player", "&7Vous avez été tué par &6<staff>&7."),
		KILL_STAFF("kill.staff", "&7Vous avez tué &6<player>&7."),
		KILL_EQUALS("kill.equals", "&7Vous vous êtes suicidé."),
		
		LAG_DESCRIPTION("lag.description", "Connaître l'état du serveur"),
		LAG_TITLE("lag.title", "&aInformations sur le serveur"),
		LAG_TIME("lag.time", "    &6&l➤  &6Durée de fonctionnement : &c<time>"),
		LAG_TPS("lag.tps", "    &6&l➤  &6TPS actuel : &c<tps>"),
		LAG_HISTORY_TPS("lag.historyTps", "    &6&l➤  &6Historique TPS : <tps>"),
		LAG_HISTORY_TPS_HOVER("lag.historyTpsHover", "&6Minute : &c<num>[RT]&6TPS : &c<tps>"),
		LAG_MEMORY("lag.memory", "    &6&l➤  &6RAM : &c<usage>&6/&c<total> &6Mo"),
		LAG_WORLDS("lag.worlds", "    &6&l➤  &6Liste des mondes :"),
		LAG_WORLDS_LINE("lag.worldsLine", "        &6&l●  &6<world>"),
		LAG_WORLDS_LINE_HOVER("lag.worldsLineHover", "&6Chunks : &c<chunks>[RT]&6Entités : &c<entities>[RT]&6Tiles : &c<tiles>"),
		
		LIST_DESCRIPTION("list.description", "Affiche la liste des joueurs connecté"),
		LIST_TITLE("list.title", "&aListe des joueurs connectés : &6" + EChat.ONLINE_PLAYERS + " &a/ &6" + EChat.MAX_PLAYERS + ""),
		LIST_TITLE_VANISH("list.titleVanish", "&aListe des joueurs connectés : &6" + EChat.ONLINE_PLAYERS + " &a(+&6<vanish>&a) / &6" + EChat.MAX_PLAYERS),
		LIST_GROUP("list.group", "&6<group>&f : <players>"),
		LIST_SEPARATOR("list.separator", ", "),
		LIST_PLAYER("list.player", "<afk>&r<vanish>&r<DISPLAYNAME_FORMAT>"),
		LIST_TAG_AFK("list.tagAFK", "&7[AFK] "),
		LIST_TAG_VANISH("list.tagVanish", "&7[VANISH] "),
		LIST_EMPTY("list.empty", "&7Aucun joueur"),
		
		MAIL_DESCRIPTION("mail.description", "Gestion de vos messages"),
		
		MAIL_READ_DESCRIPTION("mail.read.description", "Lis les messages"),
		MAIL_READ_TITLE("mail.read.title", "&aLa liste des messages"),
		MAIL_READ_LINE_READ("mail.read.lineRead", "  &a&l➤&7 De &6<player>&7 le &6<date> &7à &6<time> : <read> <delete>"),
		MAIL_READ_LINE_NO_READ("mail.read.lineNoRead", "  &6&l➤&7 De &6<player>&7 le &6<date> &7à &6<time> : <read> <delete>"),
		MAIL_READ_EMPTY("mail.read.empty", "&7Vous n'avez aucun message"),
		MAIL_READ_CANCEL("mail.read.cancel", "&cImpossible de lire le <mail>."),
		MAIL_READ_MAIL("mail.read.mail", "&6message"),
		MAIL_READ_MAIL_HOVER("mail.read.mailHover", "&7De &6<player>[RT]&7Le &6<date>"),
		MAIL_READ_ERROR("mail.read.error", "&cVous n'avez pas de message qui correspond."),
		
		MAIL_DELETE_DESCRIPTION("mail.delete.description", "Supprime le message séléctionné"),
		MAIL_DELETE_MESSAGE("mail.delete.message", "&7Voulez-vous vraiment supprimer le <mail> de &6<player>&7 le &6<date> &7à &6<time> : <confirmation>."),
		MAIL_DELETE_VALID("mail.delete.valid", "&a&nConfirmer"),
		MAIL_DELETE_VALID_HOVER("mail.delete.validHover", "&cCliquez ici pour supprimer le message."),
		MAIL_DELETE_CONFIRMATION("mail.delete.confirmation", "&7Le <mail> &7a bien été supprimé."),
		MAIL_DELETE_CANCEL("mail.delete.cancel", "&7Le <mail> &7n'a pas pu être supprimé."),
		MAIL_DELETE_MAIL("mail.delete.mail", "&6message"),
		MAIL_DELETE_MAIL_HOVER("mail.delete.mailHover", "&7De &6<player>[RT]&7Le &6<date>"),
		MAIL_DELETE_ERROR("mail.delete.error", "&cVous n'avez pas de message qui correspond."),
		
		MAIL_CLEAR_DESCRIPTION("mail.clear.description", "Supprime tous vos messages"),
		MAIL_CLEAR_MESSAGE("mail.clear.message", "&7Vous avez supprimé tous vos messages."),
		MAIL_CLEAR_CANCEL("mail.clear.message", "&7Impossible de supprimé tous vos messages."),
		MAIL_CLEAR_ERROR("mail.clear.error", "&cVous n'avez pas de message à supprimer."),
		
		MAIL_SEND_DESCRIPTION("mail.send.description", "Envoie un message à un ou plusieurs joueurs"),
		MAIL_SEND_MESSAGE("mail.send.message", "&7Votre message a bien été envoyé à &6<player>&7."),
		MAIL_SEND_CANCEL("mail.send.message", "&cImpossible d'envoyé le message à &6<player>&7."),
		MAIL_SEND_EQUALS("mail.send.equals", "&7Votre message a bien été envoyé."),
		MAIL_SEND_ALL("mail.send.all", "&7Votre message a bien été envoyé à tous les joueurs."),
		
		MAIL_BUTTON_READ("mail.button.read", "&a&nLire"),
		MAIL_BUTTON_READ_HOVER("mail.button.readHover", "&cCliquez ici pour lire le message."),
		MAIL_BUTTON_DELETE("mail.button.delete", "&c&nSupprimer"),
		MAIL_BUTTON_DELETE_HOVER("mail.button.deleteHover", "&cCliquez ici pour supprimer le message."),
		
		ME_DESCRIPTION("me.description", "Envoie un texte d'action dans le tchat"),
		ME_PREFIX("me.prefix", "&f* <player> &r"),
		
		MOJANG_DESCRIPTION("mojang.description", "Affiche les informations sur les serveurs de mojang"),
		MOJANG_TITLE("mojang.title", "&aLes serveurs de Mojang"),
		MOJANG_LINE("mojang.line", "    &6&l➤  &6<server> : <color>"),
		MOJANG_SERVER_ACCOUNT("mojang.serverAccount", "Account"),
		MOJANG_SERVER_API("mojang.serverAPI", "API"),
		MOJANG_SERVER_MOJANG("mojang.serverMojang", "Mojang"),
		MOJANG_SERVER_AUTH("mojang.serverAuth", "Auth"),
		MOJANG_SERVER_AUTHSERVER("mojang.serverAuthServer", "AuthServer"),
		MOJANG_SERVER_MINECRAFT_NET("mojang.serverMinecraftNet", "Minecraft.net"),
		MOJANG_SERVER_SESSION("mojang.serverSession", "Session"),
		MOJANG_SERVER_SESSIONSERVER("mojang.serverSessionServer", "SessionServer"),
		MOJANG_SERVER_SKINS("mojang.serverSkins", "Skins"),
		MOJANG_SERVER_TEXTURES("mojang.serverTextures", "Textures"),
		MOJANG_COLOR_GREEN("mojang.colorGreen", "&aEn ligne"),
		MOJANG_COLOR_YELLOW("mojang.colorYellow", "&6Problème de connexion"),
		MOJANG_COLOR_RED("mojang.colorRed", "&4Hors ligne"),
		
		MORE_DESCRIPTION("more.description", "Donne la quantité maximum d'un objet"),
		MORE_PLAYER("more.player", "&7Vous avez maintenant &6<quantity> &6<item>&7."),
		MORE_ITEM_COLOR("more.itemColor", "&6"),
		MORE_MAX_QUANTITY("more.maxQuantity", "&7Vous avez déjà la quantité maximum de cette objet."),
		
		MOTD_DESCRIPTION("motd.description", "Affiche le message du jour."),
		
		NAMES_DESCRIPTION("names.description", "Affiche l'historique des noms d'un joueur"),
		NAMES_PLAYER_TITLE("names.playerTitle", "&aVotre historique de nom"),
		NAMES_PLAYER_LINE_ORIGINAL("names.playerLineOriginal", "    &6&l➤  &6<name> &7: &cAchat du compte"),
		NAMES_PLAYER_LINE_OTHERS("names.playerLineOthers", "    &6&l➤  &6<name> &7: &c<datetime>"),
		NAMES_PLAYER_EMPTY("names.playerEmpty", "&7Vous n'avez aucun historique de pseudo"),
		NAMES_OTHERS_TITLE("names.othersTitle", "&aHistorique de &6<player>"),
		NAMES_OTHERS_LINE_ORIGINAL("names.othersLineOriginal", "    &6&l➤  &6<name> &7: &cAchat du compte"),
		NAMES_OTHERS_LINE_OTHERS("names.othersLineOthers", "    &6&l➤  &6<name> &7: &c<datetime>"),
		NAMES_OTHERS_EMPTY("names.othersEmpty", "&6<player> &7n'a aucun historique de pseudo"),
		
		NEAR_DESCRIPTION("near.description", "Donne la liste des joueurs dans les environs"),
		NEAR_LIST_LINE("near.list.line", "    &6&l➤  &6<player> &7: &6<distance> bloc(s)"),
		NEAR_LIST_TITLE("near.list.title", "&aListe des joueurs dans les environs"),
		NEAR_NOPLAYER("near.noPlayer", "&cAucun joueur dans les environs."),
		
		PING_DESCRIPTION("ping.description", "Connaître la latence d'un joueur"),
		PING_PLAYER("ping.player", "&7Votre ping : &6<ping> &7ms."),
		PING_OTHERS("ping.others", "&7Le ping de &6<player> &7: &6<ping> &7ms."),
		
		INVSEE_DESCRIPTION("invsee.description", "Regarde l'inventaire d'un autre joueur"),
		
		RELOAD_ALL_DESCRIPTION("reload.description", "Recharge tous les plugins"),
		RELOAD_ALL_FIRST("reload.first", "&cAttention : Rechargement de tous les plugins, risque de latence"),
		RELOAD_ALL_END("reload.end", "&aRechargement terminé"),
		
		REPAIR_DESCRIPTION("repair.description", "Répare les objets"),
		
		REPAIR_HAND_DESCRIPTION("repairhand.description", "Répare l'objet dans votre main"),
		REPAIR_HAND_ITEM_COLOR("repairhand.itemColor", "&6"),
		REPAIR_HAND_PLAYER("repairhand.player", "&7Vous venez de réparer l'objet <item>&7."),
		REPAIR_HAND_ERROR("repairhand.error", "&7Vous ne pouvez pas réparer <item>&7."),
		REPAIR_HAND_MAX_DURABILITY("repairhand.maxDurability", "&6<item> &7est déjà réparé."),
		
		REPAIR_HOTBAR_DESCRIPTION("repairhotbar.description", "Répare les objets dans votre barre d'action"),
		REPAIR_HOTBAR_PLAYER("repairhotbar.player", "&7Vous venez de réparer tous les objets de votre barre d'action."),
		
		REPAIR_ALL_DESCRIPTION("repairall.description", "Répare tous vos objets"),
		REPAIR_ALL_PLAYER("repairall.player", "&7Vous venez de réparer tous les objets de votre inventaire."),
		
		MSG_DESCRIPTION("msg.description", 					"Envoie un message privé à un autre joueur"),
		MSG_PLAYER_SEND("msg.playerSend", 					"&dEnvoyé à &f<DISPLAYNAME> &d: &7<message>"),
		MSG_PLAYER_SEND_HOVER("msg.playerSendHover", 		"&cCliquez ici pour répondre à <DISPLAYNAME>"),
		MSG_PLAYER_RECEIVE("msg.playerReceive", 			"&dReçu de &f<DISPLAYNAME> &d: &7<message>"),
		MSG_PLAYER_RECEIVE_HOVER("msg.playerReceiveHover", 	"&cCliquez ici pour répondre à <DISPLAYNAME>"),
		MSG_CONSOLE_SEND("msg.consoleSend", 				"&dEnvoyé à la &6console &d: &7<message>"),
		MSG_CONSOLE_SEND_HOVER("msg.consoleSendHover", 		"&cCliquez ici pour répondre à la console"),
		MSG_CONSOLE_RECEIVE("msg.consoleReceive", 			"&dReçu de la &6console &d: &7<message>"),
		MSG_CONSOLE_RECEIVE_HOVER("msg.consoleReceiveHover","&cCliquez ici pour répondre à la console"),
		MSG_CONSOLE_ERROR("msg.consoleError",				"&cImpossible de vous envoyez un message à vous même."),
		MSG_COMMANDBLOCK_RECEIVE("msg.commandblockReceive", "&dVous avez reçu un message &d: &7<message>"),
		
		REPLY_DESCRIPTION("reply.description", 					"Répond à un message privé d'un autre joueur"),
		REPLY_PLAYER_SEND("reply.playerSend", 					"&dEnvoyé à &f<DISPLAYNAME> &d: &7<message>"),
		REPLY_PLAYER_SEND_HOVER("reply.playerSendHover", 		"&cCliquez ici pour répondre à <DISPLAYNAME>"),
		REPLY_PLAYER_RECEIVE("reply.playerReceive", 			"&dReçu de &f<DISPLAYNAME> &d: &7<message>"),
		REPLY_PLAYER_RECEIVE_HOVER("reply.playerReceiveHover", 	"&cCliquez ici pour répondre à <DISPLAYNAME>"),
		REPLY_CONSOLE_SEND("reply.consoleSend", 				"&dEnvoyé à la &6console &d: &7<message>"),
		REPLY_CONSOLE_SEND_HOVER("reply.consoleSendHover", 		"&cCliquez ici pour répondre à la console"),
		REPLY_CONSOLE_RECEIVE("reply.consoleReceive", 			"&dReçu de la &6console &d: &7<message>"),
		REPLY_CONSOLE_RECEIVE_HOVER("reply.consoleReceiveHover","&cCliquez ici pour répondre à la console"),
		REPLY_EMPTY("reply.empty", 								"&cVous n'êtes en conversation avec aucune personne, pour en démarrez une fait &6/msg <joueur> <message>"),
		
		RULES_DESCRIPTION("rules.description", "Affiche les règles d'Evercraft."),
		
		SAY_DESCRIPTION("say.description", 		"Envoie un message à tous les joueurs."),
		SAY_PREFIX_PLAYER("say.prefixPlayer", 	"&7[&6<player>&7] <message>"),
		SAY_PREFIX_CONSOLE("say.prefixConsole", "&7[&6Console&7] <message>"),
		
		SKULL_DESCRIPTION("skull.description", "Donne la tête d'un joueur"),
		SKULL_MY_HEAD("skull.myHead", "&7Vous avez reçu votre tête."),
		SKULL_OTHERS("skull.others", "&7Vous avez reçu la tête de &6<player>&7."),
		
		SPAWN_DESCRIPTION("spawn.description", "Permet de téléporter au spawn"),
		SPAWN_PLAYER("spawn.player", "&7Vous avez été téléporté au &6<spawn>&7."),
		SPAWN_DELAY("spawn.delay", "&7Votre téléportation commencera dans &6<delay>&7. Ne bougez pas."),
		SPAWN_NAME("spawn.name", "&6spawn"),
		SPAWN_NAME_HOVER("spawn.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		SPAWN_ERROR_GROUP("spawn.errorGroup", "&cIl y a aucun groupe qui porte le nom &6<name>."),
		SPAWN_ERROR_SET("spawn.errorSet", "&cIl y a aucun spawn défini pour le groupe &6<name>."),
		SPAWN_ERROR_TELEPORT("spawn.errorTeleport", "&cImpossible de vous téléporter au <spawn>&7."),
		
		SPAWNS_DESCRIPTION("spawns.description", "Affiche la liste des spawns"),
		SPAWNS_EMPTY("spawns.empty", "&cIl n'y a aucun spawn défini sur le serveur."),
		SPAWNS_TITLE("spawns.title", "&aListe des spawns"),
		SPAWNS_LINE_DELETE("spawns.lineDelete", "    &6&l➤  &6<spawn> &7: <teleport> <delete>"),
		SPAWNS_LINE_DELETE_ERROR_WORLD("spawns.lineDeleteErrorWorld", "    &6&l➤  &6<spawn> &7: <delete>"),
		SPAWNS_LINE("spawns.line", "    &6&l➤  &6<spawn> &7: <teleport>"),
		SPAWNS_TELEPORT("spawns.teleport", "&a&nTéléporter"),
		SPAWNS_TELEPORT_HOVER("spawns.teleportHover", "&cCliquez ici pour vous téléporter au spawn &6<name>&c."),
		SPAWNS_DELETE("spawns.delete", "&c&nSupprimer"),
		SPAWNS_DELETE_HOVER("spawns.deleteHover", "&cCliquez ici pour supprimer le spawn &6<name>&c."),
		SPAWNS_NAME("spawns.name", "&6<name>"),
		SPAWNS_NAME_HOVER("spawns.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		SPAWNS_PLAYER("spawns.player", "&7Vous avez été téléporté au spawn &6<spawn>&7."),
		SPAWNS_DELAY("spawns.delay", "&7Votre téléportation commencera dans &6<delay>&7. Ne bougez pas."),
		SPAWNS_ERROR_TELEPORT("spawns.errorTeleport", "&cImpossible de vous téléporter au spawn <spawn>&7."),

		DELSPAWN_DESCRIPTION("delspawn.description", "Supprime un spawn"),
		DELSPAWN_INCONNU("delspawn.inconnu", "&cIl n'y pas de spawn qui s'appelle &6<spawn>&c."),
		DELSPAWN_NAME("delspawn.name", "&6<name>"),
		DELSPAWN_NAME_HOVER("delspawn.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		DELSPAWN_CONFIRMATION("delspawn.confirmation", "&7Souhaitez-vous vraiment supprimer le spawn &6<spawn> &7: <confirmation>"),
		DELSPAWN_CONFIRMATION_VALID("delspawn.confirmationValid", "&2&nConfirmer"),
		DELSPAWN_CONFIRMATION_VALID_HOVER("delspawn.confirmationValidHover", "&cCliquez ici pour supprimer le spawn &6<spawn>&c."),
		DELSPAWN_DELETE("delspawn.delete", "&7Vous avez supprimé le spawn &6<spawn>&7."),

		SETSPAWN_DESCRIPTION("setspawn.description", "Permet de définir un spawn"),
		SETSPAWN_ERROR_GROUP("setspawn.errorGroup", "&cIl y a aucun groupe qui porte le nom &6<name>."),
		SETSPAWN_NAME("setspawn.name", "&6<name>"),
		SETSPAWN_NAME_HOVER("setspawn.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		SETSPAWN_REPLACE("setspawn.replace", "&7Vous avez redéfini le spawn &6<name>&7."),
		SETSPAWN_NEW("setspawn.new", "&7Vous avez défini le spawn &6<name>&7."),
		
		SPAWNER_DESCRIPTION("spawner.description", "Permet de modifier le type d'un mob spawner"),
		
		SPAWNMOB_DESCRIPTION("spawnmob.description", "Fait apparaître une entité"),
		SPAWNMOB_MOB("spawnmob.mob", "&7Vous venez d'invoquer &6<nb> <entity>(s)&7."),
		SPAWNMOB_ERROR_MOB("spawnmob.errorMob", "&cErreur : nom invalide."),
		
		SPEED_DESCRIPTION("speed.description", "Change la vitesse de déplacement"),
		SPEED_INFO_WALK("speed.infoWalk", "&7Votre vitesse de &6marche &7est de &6<speed>&7."),
		SPEED_INFO_FLY("speed.infoFly", "&7Votre vitesse de &6vol &7est de &6<speed>&7."),
		SPEED_PLAYER_WALK("speed.playerWalk", "&7Vous avez défini votre vitesse de &6marche &7à &6<speed>&7."),
		SPEED_PLAYER_FLY("speed.playerFly", "&7Vous avez défini votre vitesse de &6vol &7à &6<speed>&7."),
		SPEED_OTHERS_PLAYER_WALK("speed.othersPlayerWalk", "&7Votre vitesse de marche a été défini à &6<speed> &7par &6<staff>&7."),
		SPEED_OTHERS_STAFF_WALK("speed.othersStaffWalk", "&7Vous avez défini la vitesse de &6marche &7de &6<player> &7à &6<speed>&7."),
		SPEED_OTHERS_PLAYER_FLY("speed.othersPlayerFly", "&7Votre vitesse de vol a été défini à &6<speed> &7par &6<staff>&7."),
		SPEED_OTHERS_STAFF_FLY("speed.othersStaffFly", "&7Vous avez défini la vitesse de &6vol &7de &6<player> &7à &6<speed>&7."),
		
		STOP_DESCRIPTION("stop.description", "Arrête le serveur"),
		STOP_MESSAGE("stop.message", "&cArrêt du serveur par &6<staff>"),
		STOP_MESSAGE_REASON("stop.messageReason", "&c<reason>"),
		STOP_CONSOLE_MESSAGE("stop.consoleMessage", "&cArrêt du serveur"),
		STOP_CONSOLE_MESSAGE_REASON("stop.consoleMessageReason", "&c<reason>"),
		
		SUDO_DESCRIPTION("sudo.description", "Fait exécuter une commande par un autre joueur"),
		SUDO_COMMAND("sudo.command", "&6commande"),
		SUDO_COMMAND_HOVER("sudo.commandHover", "&c<command>"),
		SUDO_PLAYER("sudo.player", "&7Votre <command> &7a bien était éxecutée par &6<player>&7."),
		SUDO_BYPASS("sudo.bypass", "&cVous ne pouvez pas faire exécuter de commande à &6<player>&7."),
		SUDO_CONSOLE("sudo.console", "&7Votre <command> &7à bien était éxecutée par la &6console&7."),
		
		SUICIDE_DESCRIPTION("suicide.description", "Permet de vous suicider"),
		SUICIDE_PLAYER("suicide.player", "&7Vous vous êtes suicidé."),
		
		TP_DESCRIPTION("tp.description", "Téléporte le joueur vers un autre joueur"),
		TP_DESTINATION("tp.destination", "&6&l<player>"),
		TP_DESTINATION_HOVER("tp.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TP_PLAYER("tp.player", "&7Vous avez été téléporté vers &6<destination>&7."),
		TP_PLAYER_EQUALS("tp.playerEquals", "&7Vous avez été repositionné."),
		TP_OTHERS_PLAYER("tp.othersPlayer", "&6<staff> &7vous a téléporté vers &6<destination>."),
		TP_OTHERS_STAFF("tp.othersStaff", "&6<player> &7a été téléporté vers &6<destination>&7."),
		TP_OTHERS_PLAYER_REPOSITION("tp.othersPlayerReposition", "&6<staff> &7vient de vous repositionner."),
		TP_OTHERS_STAFF_REPOSITION("tp.othersStaffReposition", "&7Vous venez de repositionner &6<player>&7."),
		TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER("tp.othersStaffEqualsDestinationPlayer", "&6<destination> &7vous a téléporté."),
		TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF("tp.othersStaffEqualsDestinationStaff", "&7Vous venez de téléporter &6<player>&7."),
		TP_ERROR_LOCATION("tp.errorLocation", "&cImpossible de trouver une position pour réaliser une téléportation."),
		
		TPALL_DESCRIPTION("tpall.description", "Téléporte tous les joueurs vers un autre joueur"),
		TPALL_DESTINATION("tpall.destination", "&6&l<player>"),
		TPALL_DESTINATION_HOVER("tpall.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TPALL_PLAYER("tpall.player", "&6<destination> &7vous a téléporté."),
		TPALL_STAFF("tpall.staff", "&7Vous venez de téléporter tous les joueurs."),
		TPALL_ERROR("tpall.error", "&cImpossible de trouver une position pour téléporter les joueurs."),
		TPALL_OTHERS_PLAYER("tpall.othersPlayer", "&6<staff> &7vous a téléporté vers &6<destination>."),
		TPALL_OTHERS_STAFF("tpall.othersStaff", "&7Tous les joueurs ont été téléportés vers &6<destination>&7."),
		
		TPHERE_DESCRIPTION("tphere.description", "Téléporte le joueur vers vous"),
		TPHERE_DESTINATION("tphere.destination", "&6&l<player>"),
		TPHERE_DESTINATION_HOVER("tphere.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TPHERE_PLAYER("tphere.player", "&6<destination> &7vous a téléporté."),
		TPHERE_STAFF("tphere.staff", "&7Vous venez de téléporter &6<player>&7."),
		TPHERE_EQUALS("tphere.equals", "&7Vous avez été repositionné."),
		TPHERE_ERROR("tphere.error", "&cImpossible de trouver une position pour téléporter le joueur."),
		
		TPPOS_DESCRIPTION("tppos.description", "Téléporte le joueur aux coordonnées choisis"),
		TPPOS_POSITION("tppos.position", "&6&lposition"),
		TPPOS_POSITION_HOVER("tppos.positionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TPPOS_PLAYER("tppos.player", "&7Vous avez été téléporté à cette <position>&7."),
		TPPOS_PLAYER_ERROR("tppos.playerError", "&7Impossible de vous téléporter à cette <position>&7."),
		TPPOS_OTHERS_PLAYER("tppos.othersPlayer", "&7Vous avez été téléporté à cette <position> &7par &6<staff>&7."),
		TPPOS_OTHERS_STAFF("tppos.othersStaff", "&7Vous téléportez &6<player> &7à cette <position>&7."),
		TPPOS_OTHERS_ERROR("tppos.othersError", "&7Impossible de téléporter &6<player> &7à cette <position>&7."),
		
		TELEPORT_ERROR_DELAY("teleport.errorDelay", "&cVous avez bougé donc votre demande de téléportation a été annulé."),
		
		TPA_DESCRIPTION("tpa.description", "Envoie une demande de téléportation à un joueur"),
		TPA_DESTINATION("tpa.destination", "&6&l<player>"),
		TPA_DESTINATION_HOVER("tpa.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TPA_STAFF_QUESTION("tpa.staffQuestion", "&7Votre demande a été envoyée à &6<player>&7."),
		TPA_STAFF_ACCEPT("tpa.staffAccept", "&7Votre demande de téléportation a été acceptée par &6<player>&7."),
		TPA_STAFF_DENY("tpa.staffDeny", "&7Votre demande de téléportation a été refusée par &6<player>&7."),
		TPA_STAFF_EXPIRE("tpa.staffExpire", "&7Votre demande de téléportation à &6<player> &7vient d'expirée."),
		TPA_STAFF_TELEPORT("tpa.staffTeleport", "&7Vous avez été téléporté vers &6<destination>&7."),
		TPA_PLAYER_QUESTION("tpa.playerQuestion", "&6<player> &7souhaite se téléporter vers vous : <accept> <deny>[RT]"
												+ "&7Cette demande de téléportation expira dans &6<delay>&7."),
		TPA_PLAYER_QUESTION_ACCEPT("tpa.playerQuestionAccept", "&2&l&nAccepter"),
		TPA_PLAYER_QUESTION_ACCEPT_HOVER("tpa.playerQuestionAcceptHover", "&cCliquez ici pour accepter la téléportation de &6<player>&c."),
		TPA_PLAYER_QUESTION_DENY("tpa.playerQuestionDeny", "&4&l&nRefuser"),
		TPA_PLAYER_QUESTION_DENY_HOVER("tpa.playerQuestionDenyHover", "&cCliquez ici pour refuser la téléportation de &6<player>&7."),
		TPA_PLAYER_ACCEPT("tpa.playerAccept", "&6<player> &7sera téléporté dans &6<delay>&7."),
		TPA_PLAYER_EXPIRE("tpa.playerExpire", "&cLa demande de téléportation de &6<player> &ca expirée."),
		TPA_PLAYER_TELEPORT("tpa.playerTeleport", "&6<player> &7vient d'être téléporté."),
		TPA_PLAYER_EMPTY("tpa.playerEmpty", "&cVous n'avez aucune demande de téléportation de &6<player>&c."),
		TPA_ERROR_EQUALS("tpa.errorEquals", "&cImpossible de vous envoyer une demande à vous même."),
		TPA_ERROR_DELAY("tpa.errorDelay", "&cIl y a déjà une demande de téléportation en cours."),
		TPA_ERROR_LOCATION("tpa.errorLocation", "&cImpossible de trouver une position pour réaliser une téléportation."),
		
		TPAHERE_DESCRIPTION("tpahere.description", "Envoie une demande de téléportation à un joueur"),
		TPAHERE_DESTINATION("tpahere.destination", "&6&l<player>"),
		TPAHERE_DESTINATION_HOVER("tpahere.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TPAHERE_STAFF_QUESTION("tpahere.staffQuestion", "&7Votre demande a été envoyée à &6<player>&7."),
		TPAHERE_STAFF_ACCEPT("tpahere.staffAccept", "&7Votre demande de téléportation a été acceptée par &6<player>&7."),
		TPAHERE_STAFF_DENY("tpahere.staffDeny", "&7Votre demande de téléportation a été refusée par &6<player>&7."),
		TPAHERE_STAFF_EXPIRE("tpahere.staffExpire", "&7Votre demande de téléportation à &6<player> &7vient d'expirée."),
		TPAHERE_STAFF_TELEPORT("tpahere.staffTeleport", "&6<player>&7 a été téléporté à vous."),
		TPAHERE_PLAYER_QUESTION("tpahere.playerQuestion", "&6<player> &7souhaite que vous vous téléportiez à lui/elle : <accept> <deny>[RT]"
														+ "&7Cette demande de téléportation expira dans &6<delay>&7."),
		TPAHERE_PLAYER_QUESTION_ACCEPT("tpahere.playerQuestionAccept", "&2&l&nSe téléporter"),
		TPAHERE_PLAYER_QUESTION_ACCEPT_HOVER("tpahere.playerQuestionAcceptHover", "&cCliquez ici pour vous téléporter à &6<player>&c."),
		TPAHERE_PLAYER_QUESTION_DENY("tpahere.playerQuestionDeny", "&4&l&nRefuser"),
		TPAHERE_PLAYER_QUESTION_DENY_HOVER("tpahere.playerQuestionDenyHover", "&cCliquez ici pour refuser la téléportation de &6<player>&7."),
		TPAHERE_PLAYER_EXPIRE("tpahere.playerExpire", "&cLa demande de téléportation de &6<player> &ca expirée."),
		TPAHERE_PLAYER_DENY("tpahere.playerDeny", "&7La demande de &6<player> &7 a bien été refusé."),
		TPAHERE_PLAYER_ACCEPT("tpahere.playerAccept", "&7Votre téléportation commencera dans &6<delay>&7. Ne bougez pas."),
		TPAHERE_PLAYER_TELEPORT("tpahere.playerTeleport", "&7Vous avez été téléporté vers &6<destination>&7."),
		TPAHERE_PLAYER_EMPTY("tpahere.playerEmpty", "&cVous n'avez aucune demande de téléportation de &6<player>&c."),
		TPAHERE_ERROR_EQUALS("tpahere.errorEquals", "&cImpossible de vous envoyer une demande à vous même."),
		TPAHERE_ERROR_DELAY("tpahere.errorDelay", "&cIl y a déjà une demande de téléportation en cours."),
		TPAHERE_ERROR_LOCATION("tpahere.errorLocation", "&cImpossible de trouver une position pour réaliser une téléportation."),
		
		TPA_PLAYER_LIST_TITLE("tpa.playerListTitle", "&aListe des demandes de téléportation"),
		TPA_PLAYER_LIST_LINE("tpa.playerListLine", "    &6&l➤  &6<player> &7: <accept> <deny>"),
		TPA_PLAYER_LIST_EMPTY("tpa.playerListEmpty", "&7Aucune demande"),
		
		TPAALL_DESCRIPTION("tpaall.description", "Envoie une demande de téléportation à tous les joueurs"),
		TPAALL_PLAYER("tpaall.player", "&7Votre demande de téléportation a bien été envoyée à tous les joueurs."),
		TPAALL_OTHERS_STAFF("tpaall.othersStaff", "&7Votre demande pour téléporter tous les joueurs à &6<player> &7a bien été envoyée."),
		TPAALL_OTHERS_PLAYER("tpaall.othersPlayer", "&6<staff> &7a envoyé une demande de téléportation vers vous à tous les joueurs."),
		TPAALL_ERROR_EMPTY("tpaall.errorEmpty", "&cErreur : Aucun joueur à téléporter."),
		TPAALL_ERROR_PLAYER_LOCATION("tpaall.errorPlayerLocation", "&cErreur : Impossible de trouver une position pour téléporter les joueurs."),
		TPAALL_ERROR_OTHERS_LOCATION("tpaall.errorOthersLocation", "&cErreur : Impossible de trouver une position pour téléporter les joueurs sur &6<player>&c."),
		
		TPACCEPT_DESCRIPTION("tpaccept.description", "Permet d'accepter une demande de téléportation"),
		TPACCEPT_PLAYER("tpaccept.player", "&7Vous avez accepté la demande de téléportation de &6<player>&7."),
		
		TPDENY_DESCRIPTION("tpaccept.description", "Permet de refuser une demande de téléportation"),
		TPDENY_PLAYER("tpaccept.player", "&7Vous avez refusé la demande de téléportation de &6<player>&7."),
		
		TIME_DESCRIPTION("time.description", "Gère l'heure sur un monde"),
		TIME_FORMAT("time.format", "&6<hours>h<minutes>"),
		TIME_INFORMATION("time.information", "&7Il est actuellement &6<hours> &7dans le monde &6<world>&7."),
		TIME_SET_WORLD("time.setWorld", "&7Il est désormais &6<hours> &7dans le monde &6<world>&7."),
		TIME_SET_ALL_WORLD("time.setAllWorld", "&7Il est désormais &6<hours> &7dans les mondes&7."),
		TIME_ERROR("time.error", "&cErreur : Horaire incorrect."),
		
		TIME_DAY_DESCRIPTION("time.dayDescription", "Mettre le jour dans votre monde"),
		TIME_NIGHT_DESCRIPTION("time.nightDescription", "Mettre la nuit dans votre monde"),
		
		TOGGLE_DESCRIPTION("toggle.description", "Permet de gérer les demandes de téléportation"),
		TOGGLE_ON_DESCRIPTION("toggle.on.description", "Active les demandes de téléportation"),
		TOGGLE_ON_ACTIVATED("toggle.on.activated", "&7Vous acceptez désormais les demandes de téléportation."),
		TOGGLE_ON_ALREADY_ACTIVATED("toggle.on.alreadyActivated", "&cErreur : Vous acceptez déjà les demandes de téléportation."),
		TOGGLE_OFF_DESCRIPTION("toggle.off.description", "Désactive les demandes de téléportation"),
		TOGGLE_OFF_DISABLED("toggle.off.disabled", "&7Vous refusez désormais les demandes de téléportation."),
		TOGGLE_OFF_ALREADY_DISABLED("toggle.off.alreadyDisabled", "&cErreur : Vous refusez déjà les demandes de téléportation."),
		TOGGLE_STATUS_DESCRIPTION("toggle.status.description", "Gère les demandes de téléportation"),
		TOGGLE_STATUS_MESSAGE("toggle.status.message", "&7Les demandes de téléportation sont <status>."),
		TOGGLE_STATUS_ACTIVATED("toggle.status.activated", "&aactivée"),
		TOGGLE_STATUS_DISABLED("toggle.status.disabled", "&cdésactivée"),
		TOGGLE_DISABLED("toggle.disabled", "&6<player> &7n’accepte pas les demandes de téléportation."),
		
		TOP_DESCRIPTION("top.description", "Téléporte le joueur à la position la plus élevée"),
		TOP_POSITION("top.position", "&6&lposition"),
		TOP_POSITION_HOVER("top.positionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		TOP_DELAY("top.delay", "&7Votre téléportation commencera dans &6<delay>&7. Ne bougez pas."),
		TOP_TELEPORT("top.teleport", "&7Vous avez été téléporté à la <position> &7la plus élevée."),
		TOP_TELEPORT_ERROR("top.teleportError", "&cImpossible de trouver une position où vous téléporter."),
		
		TREE_DESCRIPTION("tree.description", "Place un arbre"),
		TREE_INCONNU("tree.inconnu", "&cType d'arbre inconnu : &6<type>"),
		TREE_NO_CAN("tree.noCan", "&cErreur : Impossible de placer un arbre à cette endroit. Regarder plutôt un bloc d'herbe ou de terre."),
		
		UUID_DESCRIPTION("uuid.description", "Affiche l'identifiant unique du joueur."),
		UUID_NAME("uuid.name", "&6&l<uuid>"),
		UUID_PLAYER("uuid.player", "&7Votre UUID est <uuid>"),
		UUID_PLAYER_OTHERS("uuid.otherPlayer", "L'UUID de <player> est <uuid>"),
		
		VANISH_DESCRIPTION("vanish.description", "Permet de vous rendre invisible."),
		VANISH_PLAYER_ENABLE("vanish.playerEnable", "&7Vous êtes désormais invisible."),
		VANISH_PLAYER_ENABLE_ERROR("vanish.playerEnableError", "&7Vous êtes déjà invisible."),
		VANISH_PLAYER_DISABLE("vanish.playerDisable", "&7Vous n'êtes plus invisible."),
		VANISH_PLAYER_DISABLE_ERROR("vanish.playerDisableError", "&7Vous êtes déjà visible."),
		VANISH_OTHERS_PLAYER_ENABLE("vanish.othersPlayerEnable", "&7Vous êtes désormais invisible grâce à &6<staff>&7."),
		VANISH_OTHERS_PLAYER_DISABLE("vanish.othersPlayerDisable", "&7Vous n'êtes plus invisible à cause de &6<staff>&7."),
		VANISH_OTHERS_STAFF_ENABLE("vanish.othersStaffEnable", "&7Vous venez de rendre invisible &6<player>&7."),
		VANISH_OTHERS_STAFF_ENABLE_ERROR("vanish.othersStaffEnableError", "&6<player> &7est déjà invisible."),
		VANISH_OTHERS_STAFF_DISABLE("vanish.othersStaffDisable", "&7Vous venez de rendre visible &6<player>&7."),
		VANISH_OTHERS_STAFF_DISABLE_ERROR("vanish.othersStaffDisableError", "&6<player> &7est déjà visible."),
		
		WARP_NAME("warp.name", "&6&l<name>"),
		WARP_NAME_HOVER("warp.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		WARP_INCONNU("warp.inconnu", "&cIl n'y pas de warp qui s'appelle &6<warp>&c."),
		WARP_NO_PERMISSION("warp.noPermission", "&cVous n'avez pas la permission pour vous téléporter au warp &6<warp>&c."),
		
		WARP_DESCRIPTION("warp.description", "Se téléporte à un warp"),
		WARP_EMPTY("warp.empty", "&cIl n'y a aucun warp sur le serveur."),
		WARP_LIST_TITLE("warp.listTitle", "&aListe des warps"),
		WARP_LIST_LINE_DELETE("warp.listLineDelete", "    &6&l➤  &6<warp> &7: <teleport> <delete>"),
		WARP_LIST_LINE_DELETE_ERROR_WORLD("warp.listLineDeleteErrorWorld", "    &6&l➤  &6<warp> &7: <delete>"),
		WARP_LIST_LINE("warp.listLine", "    &6&l➤  &6<warp> &7: <teleport>"),
		WARP_LIST_TELEPORT("warp.listTeleport", "&a&nTéléporter"),
		WARP_LIST_TELEPORT_HOVER("warp.listTeleportHover", "&cCliquez ici pour vous téléporter à le warp &6<warp>&c."),
		WARP_LIST_DELETE("warp.listDelete", "&c&nSupprimer"),
		WARP_LIST_DELETE_HOVER("warp.listDeleteHover", "&cCliquez ici pour supprimer le warp &6<warp>&c."),
		WARP_TELEPORT_PLAYER("warp.teleportPlayer", "&7Vous avez été téléporté au warp &6<warp>&7."),
		WARP_TELEPORT_PLAYER_ERROR("warp.teleportPlayerError", "&cImpossible de vous téléporter au warp &6<warp>&c."),
		WARP_TELEPORT_OTHERS_PLAYER("warp.teleportOthersPlayer", "&7Vous avez été téléporté au warp &6<warp> &7par &6<player>&7."),
		WARP_TELEPORT_OTHERS_STAFF("warp.teleportOthersStaff", "&7Vous avez téléporté &6<player> &7au warp &6<warp>&7."),
		WARP_TELEPORT_OTHERS_ERROR("warp.teleportOthersError", "&cImpossible de téléporter &6<player> &7au warp &6<warp>&c."),
		
		DELWARP_DESCRIPTION("delwarp.description", "Supprime un warp"),
		DELWARP_INCONNU("delwarp.inconnu", "&cIl n'y pas de warp qui s'appelle &6<warp>&c."),
		DELWARP_NAME("delwarp.name", "&6&l<name>"),
		DELWARP_NAME_HOVER("delwarp.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		DELWARP_CONFIRMATION("delwarp.confirmation", "&7Souhaitez-vous vraiment supprimer le warp &6<warp> &7: <confirmation>"),
		DELWARP_CONFIRMATION_VALID("delwarp.confirmationValid", "&2&nConfirmer"),
		DELWARP_CONFIRMATION_VALID_HOVER("delwarp.confirmationValidHover", "&cCliquez ici pour supprimer le warp &6<warp>&c."),
		DELWARP_DELETE("delwarp.delete", "&7Vous avez supprimé le warp &6<warp>&7."),
		
		SETWARP_DESCRIPTION("setwarp.description", "Crée un warp"),
		SETWARP_NAME("setwarp.name", "&6&l<name>"),
		SETWARP_NAME_HOVER("setwarp.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		SETWARP_REPLACE("setwarp.replace", "&7Vous avez redéfini le warp &6<warp>&7."),
		SETWARP_NEW("setwarp.new", "&7Vous avez défini le warp &6<warp>&7."),
		
		WEATHER_DESCRIPTION("weather.description", "Change la météo d'un monde"),
		WEATHER_ERROR("weather.error", "&cVous ne pouvez pas changer la météo dans ce type de monde."),
		WEATHER_SUN("weather.sun", "&7Vous avez mis &6le beau temps &7dans le monde &6<world>&7."),
		WEATHER_RAIN("weather.rain", "&7Vous avez mis &6la pluie &7dans le monde &6<world>&7."),
		WEATHER_STORM("weather.storm", "&7Vous avez mis &6la tempête &7dans le monde &6<world>&7."),
		WEATHER_SUN_DURATION("weather.sunDuration", "&7Vous avez mis &6le beau temps &7dans le monde &6<world>&7 pendant &6<duration>&7 minute(s)."),
		WEATHER_RAIN_DURATION("weather.rainDuration", "&7Vous avez mis &6la pluie &7dans le monde &6<world>&7 pendant &6<duration>&7 minute(s)."),
		WEATHER_STORM_DURATION("weather.stormDuration", "&7Vous avez mis &6la tempête &7dans le monde &6<world>&7 pendant &6<duration>&7 minute(s)."),
		
		WEATHER_RAIN_DESCRIPTION("weather.rainDescription", "Met la pluie dans votre monde"),
		WEATHER_STORM_DESCRIPTION("weather.stormDescription", "Met la tempête dans votre monde"),
		WEATHER_SUN_DESCRIPTION("weather.sunDescription", "Met le beau dans temps dans votre monde"),
		
		WHITELIST_DESCRIPTION("whitelist.description", "Gère la whitelist"),
		WHITELIST_ON_DESCRIPTION("whitelist.on.description", "Active la whitelist"),
		WHITELIST_ON_ACTIVATED("whitelist.on.activated", "&7La whitelist est désormais &aactivée&7."),
		WHITELIST_ON_ALREADY_ACTIVATED("whitelist.on.alreadyActivated", "&cErreur : La whitelist est déjà activée."),
		WHITELIST_OFF_DESCRIPTION("whitelist.off.description", "Désactive la whitelist"),
		WHITELIST_OFF_DISABLED("whitelist.off.disabled", "&7La whitelist est désormais &cdésactivée&7."),
		WHITELIST_OFF_ALREADY_DISABLED("whitelist.off.alreadyDisabled", "&cErreur : &7La whitelist est déjà &cdésactivée&7."),
		WHITELIST_STATUS_DESCRIPTION("whitelist.status.description", "Gère la liste d'acces des joueurs"),
		WHITELIST_STATUS_MESSAGE("whitelist.status.message", "&7La whitelist est <status>."),
		WHITELIST_STATUS_ACTIVATED("whitelist.status.activated", "&aactivée"),
		WHITELIST_STATUS_DISABLED("whitelist.status.disabled", "&cdésactivée"),
		WHITELIST_ADD_DESCRIPTION("whitelist.add.description", "Ajoute un joueur dans la whitelist"),
		WHITELIST_ADD_PLAYER("whitelist.add.player", "&7Le joueur &6<player> &7a été ajouté dans la whitelist."),
		WHITELIST_ADD_ERROR("whitelist.add.error", "&cErreur : Le joueur <player> est déjà dans la whitelist."),
		WHITELIST_REMOVE_DESCRIPTION("whitelist.remove.description", "Supprime un joueur dans la whitelist"),
		WHITELIST_REMOVE_PLAYER("whitelist.remove.player", "&7Le joueur &6<player> &7a été supprimé dans la whitelist."),
		WHITELIST_REMOVE_ERROR("whitelist.remove.error", "&cErreur : Le joueur <player> n'est pas dans la whitelist."),
		WHITELIST_LIST_DESCRIPTION("whitelist.list.description", "Affiche la whitelist"),
		WHITELIST_LIST_TITLE("whitelist.list.title", "&aWhitelist"),
		WHITELIST_LIST_LINE("whitelist.list.line", "    &6&l➤  <player> &7: <delete>"),
		WHITELIST_LIST_REMOVE("whitelist.list.remove", "&a&nSupprimer"),
		WHITELIST_LIST_REMOVE_HOVER("whitelist.list.removeHover", "&cCliquez ici pour retirer &6<player> &cde la whitelist."),
		WHITELIST_LIST_NO_PLAYER("whitelist.list.noPlayer", "&7Il n'y a aucun joueur dans la whitelist."),
		
		WHOIS_DESCRIPTION("whois.description", "Affiche les informations d'un joueur"),
		WHOIS_TITLE("whois.title", "&aInformations : &c<player>"),
		WHOIS_UUID("whois.uuid", "    &6&l➤  &6UUID : <uuid>"),
		WHOIS_UUID_STYLE("whois.uuidStyle", "&c<uuid>"),
		WHOIS_IP("whois.ip", "    &6&l➤  &6IP : <ip>"),
		WHOIS_IP_STYLE("whois.ipStyle", "&c<ip>"),
		WHOIS_PING("whois.ping", "    &6&l➤  &6Ping : &c<ping> &6ms"),
		WHOIS_HEAL("whois.heal", "    &6&l➤  &6Santé : &a<heal>&6/&c<max_heal>"),
		WHOIS_FOOD("whois.food", "    &6&l➤  &6Faim : &a<food>&6/&c<max_food>"),
		WHOIS_FOOD_SATURATION("whois.foodSaturation", "    &6&l➤  &6Faim : &a<food>&6/&c<max_food> &6(+&a<saturation> &6saturation)"),
		WHOIS_EXP("whois.exp", "    &6&l➤  &6Expérience :"),
		WHOIS_EXP_LEVEL("whois.expLevel", "        &6&l●  &a<level> &6niveau(x)"),
		WHOIS_EXP_POINT("whois.expPoint", "        &6&l●  &a<point> &6point(s)"),
		WHOIS_SPEED("whois.speed", "    &6&l➤  &6Vitesse :"),
		WHOIS_SPEED_FLY("whois.speedFly", "        &6&l●  &6En volant : &a<speed>"),
		WHOIS_SPEED_WALK("whois.speedWalk", "        &6&l●  &6En marchant : &a<speed>"),
		WHOIS_LOCATION("whois.location", "    &6&l➤  &6Position : <position>"),
		WHOIS_LOCATION_POSITION("whois.locationPosition", "&6(&c<x>&6, &c<y>&6, &c<z>&6, &c<world>&6)"),
		WHOIS_LOCATION_POSITION_HOVER("whois.locationPositionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		WHOIS_BALANCE("whois.balance", "    &6&l➤  &6Solde : &c<money>"),
		WHOIS_GAMEMODE("whois.gamemode", "    &6&l➤  &6Mode de jeu : &c<gamemode>"),
		WHOIS_GOD_ENABLE("whois.godEnable", "    &6&l➤  &6Mode Dieu : &aActivé"),
		WHOIS_GOD_DISABLE("whois.godDisable", "    &6&l➤  &6Mode Dieu : &cDésactivé"),
		WHOIS_FLY_ENABLE_FLY("whois.flyEnableFly", "    &6&l➤  &6Fly Mode : &aActivé &6(&avol&6)"),
		WHOIS_FLY_ENABLE_WALK("whois.flyEnableWalk", "    &6&l➤  &6Fly Mode : &aActivé &6(&cmarche&6)"),
		WHOIS_FLY_DISABLE("whois.flyDisable", "    &6&l➤  &6Fly Mode : &cDésactivé"),
		WHOIS_MUTE_ENABLE("whois.muteEnable", "    &6&l➤  &6Muet : &aActivé"),
		WHOIS_MUTE_DISABLE("whois.muteDisable", "    &6&l➤  &6Muet : &cDésactivé"),
		WHOIS_VANISH_ENABLE("whois.vanishEnable", "    &6&l➤  &6Vanish : &aActivé"),
		WHOIS_VANISH_DISABLE("whois.vanishDisable", "    &6&l➤  &6Vanish : &cDésactivé"),
		WHOIS_AFK_ENABLE("whois.afkEnable", "    &6&l➤  &6AFK : &aActivé"),
		WHOIS_AFK_DISABLE("whois.afkDisable", "    &6&l➤  &6AFK : &cDésactivé"),
		WHOIS_FIRST_DATE_PLAYED("whois.firstDatePlayed", "    &6&l➤  &6Première connexion : &a<time>"),
		WHOIS_LAST_DATE_PLAYED("whois.lastDatePlayed", "    &6&l➤  &6Connecté depuis : &a<time>"),
		WHOIS_CHAT_FULL("whois.chatFull", "    &6&l➤  &6Chat : &aVisible"),
		WHOIS_CHAT_SYSTEM("whois.chatSystem", "    &6&l➤  &6Chat : &aCommandes seulement"),
		WHOIS_CHAT_HIDDEN("whois.chatHidden", "    &6&l➤  &6Chat : &aMasqué"),
		WHOIS_VIEW_DISTANCE("whois.viewDistance", "    &6&l➤  &6Distance d'affichage : &a<amount>"),
		WHOIS_CHATCOLOR_ON("whois.chatColorOn", "    &6&l➤  &6Couleur dans le chat : &aActivé"),
		WHOIS_CHATCOLOR_OFF("whois.chatColorOff", "    &6&l➤  &6Couleur dans le chat : &cDésactivé"),
		WHOIS_LANGUAGE("whois.language", "    &6&l➤  &6Langage : &a<langue>"),
		WHOIS_TOGGLE_ENABLE("whois.toggleEnable", "    &6&l➤  &6Requêtes de téléportation : &aActivé"),
		WHOIS_TOGGLE_DISABLE("whois.toggleDisable", "    &6&l➤  &6Requêtes de téléportation : &cDésactivé"),
		
		WORLDBORDER_DESCRIPTION("worldborder.description", "Gère la bordure des mondes"),
		WORLDBORDER_INFO_DESCRIPTION("worldborder.info.description", "Affiche les informations sur la bordure d'un monde"),
		WORLDBORDER_INFO_TITLE("worldborder.info.title", "&6Monde : <world>"),
		WORLDBORDER_INFO_LOCATION("worldborder.info.location", "    &6&l➤  &6Centre : <position>"),
		WORLDBORDER_INFO_LOCATION_POSITION("worldborder.info.locationPosition", "&6(&c<x>&6, &c<z>&6, &c<world>&6)"),
		WORLDBORDER_INFO_LOCATION_POSITION_HOVER("worldborder.info.locationPositionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cZ : &6<z>"),
		WORLDBORDER_INFO_BORDER("worldborder.info.border", "    &6&l➤  &6La bordure est de &a<nb> &6bloc(s)"),
		WORLDBORDER_INFO_BUFFER("worldborder.info.buffer", "    &6&l➤  &6Zone de tolérance : &a<nb> &6bloc(s)"),
		WORLDBORDER_INFO_DAMAGE("worldborder.info.damage", "    &6&l➤  &6Dégat(s) : &a<nb> &6coeur(s)"),
		WORLDBORDER_INFO_WARNING_TIME("worldborder.info.warningTime", "    &6&l➤  &6Avertissement du rétrecissement de la bordure : &a<nb> &6seconde(s)"),
		WORLDBORDER_INFO_WARNING_DISTANCE("worldborder.info.warningDistance", "    &6&l➤  &6Avertissement de la bordure : &a<nb> &6bloc(s)"),
		WORLDBORDER_SET_DESCRIPTION("worldborder.set.description", "Défini la bordure d'un monde"),
		WORLDBORDER_SET_BORDER("worldborder.set.border", "&7La taille de la bordure du monde &6<world> &7a été défini à &6<nb> &7bloc(s) de large."),
		WORLDBORDER_SET_BORDER_INCREASE("worldborder.set.borderIncrease", "&7Agrandissement de la bordure du monde &6<world> &7à <nb> bloc(s) de large en &6<time> &7seconde(s)."),
		WORLDBORDER_SET_BORDER_DECREASE("worldborder.set.borderDecrease", "&7Rétrécissement de la bordure du monde &6<world> &7à <nb> bloc(s) de large en &6<time> &7seconde(s)."),
		WORLDBORDER_CENTER_DESCRIPTION("worldborder.center.description", "Défini le centre de la bordure d'un monde"),
		WORLDBORDER_CENTER_MESSAGE("worldborder.center.message", "&7Le centre de la bordure du monde &6<world> &7a été défini en &6X: <x> Z: <z>&7."),
		WORLDBORDER_ADD_DESCRIPTION("worldborder.add.description", "Augmente ou diminue la taille de la bordure d'un monde"),
		WORLDBORDER_ADD_BORDER("worldborder.add.border", "&7La taille de la bordure du monde &6<world> &7a été défini à &6<nb> &7bloc(s) de large."),
		WORLDBORDER_ADD_BORDER_INCREASE("worldborder.add.borderIncrease", "&7Agrandissement de la bordure du monde &6<world> &7à <nb> bloc(s) de large en &6<time> &7seconde(s)."),
		WORLDBORDER_ADD_BORDER_DECREASE("worldborder.add.borderDecrease", "&7Rétrécissement de la bordure du monde &6<world> &7à <nb> bloc(s) de large en &6<time> &7seconde(s)."),
		WORLDBORDER_DAMAGE_DESCRIPTION("worldborder.damage.description", "Configure les dégats infligés aux entités en dehors de la bordure d'un monde"),
		WORLDBORDER_DAMAGE_AMOUNT("worldborder.damage.amount", "&7La quantité de dégâts causés par la bordure dans le monde &6<world> &7a été défini à &6<nb>&7."),
		WORLDBORDER_DAMAGE_BUFFER("worldborder.damage.buffer", "&7La zone de tolérance de la bordure du monde &6<world> &7a été défini à &6<nb>&7."),
		WORLDBORDER_WARNING_DESCRIPTION("worldborder.warning.description", "Configure l'écran d'avertissement pour les joueurs qui s'approche de la bordure d'un monde"),
		WORLDBORDER_WARNING_TIME("worldborder.warning.time", "&7L'avertissement de la bordure du monde &6<world> &7a été défini à &6<nb> &7seconde(s)."),
		WORLDBORDER_WARNING_DISTANCE("worldborder.warning.distance", "&7L'avertissement de la bordure du monde &6<world> &7a été défini à &6<nb> &7bloc(s) de distance."),
			
		WORLDS_DESCRIPTION("worlds.description", "Téléporte le joueur dans le monde de votre choix"),
		WORLDS_END_DESCRIPTION("worlds.endDescription", "Vous téléporte dans le monde du néant"),
		WORLDS_NETHER_DESCRIPTION("worlds.netherDescription", "Vous téléporte dans le monde de l'enfer"),
		WORLDS_LIST_TITLE("worlds.listTitle", "&aListe des mondes"),
		WORLDS_LIST_LINE("worlds.listLine", "    &6&l➤  &6<world> &7: <teleport>"),
		WORLDS_LIST_TELEPORT("worlds.listTeleport", "&2&nTéléporter"),
		WORLDS_LIST_TELEPORT_HOVER("worlds.listTeleportHover", "&cCliquez ici pour vous téléporter dans le monde &6<world>&c."),
		WORLDS_TELEPORT_WORLD("worlds.teleportWorld", "&6&l<world>"),
		WORLDS_TELEPORT_WORLD_HOVER("worlds.teleportWorldHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>"),
		WORLDS_TELEPORT_PLAYER("worlds.teleportPlayer", "&7Vous avez été téléporté dans le monde &6<world>&7."),
		WORLDS_TELEPORT_PLAYER_ERROR("worlds.teleportPlayerError", "&7Impossible de vous téléporter dans le monde <world>&7."),
		WORLDS_TELEPORT_OTHERS_PLAYER("worlds.teleportOthersPlayer", "&7Vous avez été téléporté dans le monde <world> &7par &6<staff>&7."),
		WORLDS_TELEPORT_OTHERS_STAFF("worlds.teleportOthersStaff", "&7Vous téléportez &6<player> &7dans le monde <world>&7."),
		WORLDS_TELEPORT_OTHERS_ERROR("worlds.teleportOthersError", "&7Impossible de téléporter &6<player> &7dans le monde&7.");
		
		private final String path;
	    private final Object french;
	    private final Object english;
	    private Object message;
	    
	    private EEMessages(final String path, final Object french) {   	
	    	this(path, french, french);
	    }
	    
	    private EEMessages(final String path, final Object french, final Object english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french;
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public Object getFrench() {
			return this.french;
		}

		public Object getEnglish() {
			return this.english;
		}
		
		public String get() {
			if(this.message instanceof String) {
				return (String) this.message;
			}
			return this.message.toString();
		}
			
		@SuppressWarnings("unchecked")
		public List<String> getList() {
			if(this.message instanceof List) {
				return (List<String>) this.message;
			}
			return Arrays.asList(this.message.toString());
		}
		
		public void set(Object message) {
			this.message = message;
		}

		public Text getText() {
			return EChat.of(this.get());
		}
		
		public TextColor getColor() {
			return EChat.getTextColor(this.get());
		}
		
		public boolean has() {
			return this.message != null;
		}
	}
}
