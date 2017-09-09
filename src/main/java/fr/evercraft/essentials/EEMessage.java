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

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.message.EMessageBuilder;
import fr.evercraft.everapi.message.EMessageFormat;
import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplacesPlayer;
import fr.evercraft.everapi.message.replace.EReplacesServer;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EEMessage extends EMessage<EverEssentials> {

	public EEMessage(EverEssentials plugin, String name) {
		super(plugin, EEMessages.values());
	}

	public enum EEMessages implements EnumMessage {
		PREFIX("PREFIX", 									"[&4Ever&6&lEssentials&f] "),
		DESCRIPTION("DESCRIPTION", 							"Information sur EverEssentials"),
		
		AFK_DESCRIPTION("afkDescription", 					"Permet de vous signaler AFK"),
		AFK_KICK("afkKick", 								"&cPour cause d'inactivité"),
		
		AFK_ON_DESCRIPTION("afkOnDescription", 				"Rend le joueur invulnérable"),

		AFK_ON_PLAYER("afkOnPlayer", 						"&7Vous êtes désormais AFK."),
		AFK_ON_PLAYER_ERROR("afkOnPlayerError", 			"&cVous êtes déjà AFK."),
		AFK_ON_PLAYER_CANCEL("afkOnPlayerCancel", 			"&cImpossible de vous mettre AFK."),
		AFK_ON_ALL("afkOnAll", 								"&6" + EReplacesPlayer.DISPLAYNAME.getName() + " &7est désormais AFK.", "The message may be empty"),
		AFK_ON_OTHERS_PLAYER("afkOnOthersPlayer", 			"&7Vous êtes désormais AFK à cause de &6{staff}&7."),
		AFK_ON_OTHERS_STAFF("afkOnOthersStaff", 			"&6{player} &7est désormais AFK à cause de &6{staff}&7."),
		AFK_ON_OTHERS_ERROR("afkOnOthersError", 			"&6{player} &cest déjà signalé AFK."),
		AFK_ON_OTHERS_CANCEL("afkOnOthersCancel", 			"&cImpossible de rendre &6{player} &cinvulnérable."),

		AFK_OFF_DESCRIPTION("afkOffDescription", 			"Rend le joueur vulnérable"),

		AFK_OFF_PLAYER("afkOffPlayer", 						"&7Vous n'êtes plus AFK."),
		AFK_OFF_PLAYER_ERROR("afkOffPlayerError", 			"&cVous n'êtes pas AFK."),
		AFK_OFF_PLAYER_CANCEL("afkOffPlayerCancel", 		"&cImpossible de vous rendre vulnérable."),
		AFK_OFF_ALL("afkOffAll", 							"&6" + EReplacesPlayer.DISPLAYNAME.getName() + " &7n'est plus AFK.", "The message may be empty"),
		AFK_OFF_OTHERS_PLAYER("afkOffOthersPlayer", 		"&7Vous n'êtes plus AFK à cause de &6{staff}&7."),
		AFK_OFF_OTHERS_STAFF("afkOffOthersStaff", 			"&6{player} &7n'est plus AFK à cause de &6{staff}&7."),
		AFK_OFF_OTHERS_ERROR("afkOffOthersError", 			"&6{player} &cn'est pas AFK."),
		AFK_OFF_OTHERS_CANCEL("afkOffOthersCancel", 		"&cImpossible de sortir &6{player} &cd'AFK."),
		
		AFK_STATUS_DESCRIPTION("afkStatusDescription", 		"Affiche si le joueur est AFK où pas"),
		AFK_STATUS_PLAYER_ON("afkStatusPlayerOn", 			"&7Vous êtes AFK."),
		AFK_STATUS_PLAYER_OFF("afkStatusPlayerOff", 		"&7Vous n'êtes pas AFK."),
		AFK_STATUS_OTHERS_ON("afkStatusOthersOn", 			"&6{player} &7est AFK."),
		AFK_STATUS_OTHERS_OFF("afkStatusOthersOff", 		"&6{player} &7n'est pas AFK."),
		
		BACK_DESCRIPTION("backDescription",					"Retourne à la dernière position sauvegardé."),
		BACK_NAME("backName", 								"&6&lposition"),
		BACK_NAME_HOVER("backNameHover", 					"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		BACK_TELEPORT("backTeleport", 						"&7Vous avez été téléporté à votre dernière {back}&7."),
		BACK_INCONNU("backInconnu", 						"&cVous n'avez aucune position sauvegardé."),
		BACK_DELAY("backDelay", 							"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		BACK_ERROR_LOCATION("backErrorLocation", 			"&cImpossible de trouver une position pour réaliser une téléportation."),

		BED_DESCRIPTION("bedDescription", 					"Retourne à la dernière position ou vous avez dormi"),
		
		BROADCAST_DESCRIPTION("broadcastDescription", 		"Envoie un message à tous les joueurs."),
		BROADCAST_MESSAGE("broadcastMessage", 				"&7[&6&lBroadcast&7] {message}"),
		
		BOOK_DESCRIPTION("bookDescription", 				"Permet de modifier un livre."),
		BOOK_WRITABLE("bookWritable", 						""),
		BOOK_NO_WRITTEN("bookNoWritten", 					""),
		
		BUTCHER_DESCRIPTION("butcherDescription", 					"Supprime les entités dans un monde ou dans un rayon."),
		BUTCHER_ALL_DESCRIPTION("butcherAllDescription", 			"Supprime toutes les entités dans un monde ou dans un rayon."),
		BUTCHER_ANIMAL_DESCRIPTION("butcherAnimalDescription", 		"Supprime toutes les animaux dans un monde ou dans un rayon."),
		BUTCHER_MONSTER_DESCRIPTION("butcherMonsterDescription", 	"Supprime toutes les monstres dans un monde ou dans un rayon."),
		BUTCHER_TYPE_DESCRIPTION("butcherTypeDescription", 	  		"Supprime toutes les entité d'un type dans un monde ou dans un rayon."),
		
		
		BUTCHER_NOENTITY("butcherNoEntity", 						"&cIl y a aucune entité à supprimer."),
		BUTCHER_ENTITY_COLOR("butcherEntityColor", 					"&6"),
		BUTCHER_ANIMAL("butcherKillAnimal", 						"&7Suppression de &6{count} animaux &7dans ce monde."),
		BUTCHER_ANIMAL_RADIUS("butcherKillAnimalRadius", 			"&7Suppression de &6{count} animaux &7dans un rayon de &6{radius} bloc(s)&7."),
		BUTCHER_MONSTER("butcherKillMonster", 						"&7Suppression de &6{count} monstre(s) &7dans ce monde."),
		BUTCHER_MONSTER_RADIUS("butcherKillMonsterRadius", 			"&7Suppression de &6{count} monstre(s) &7dans un rayon de &6{radius} bloc(s)&7."),
		BUTCHER_ALL("butcherKillAll",								"&7Suppression de &6{count} entité(s) &7dans ce monde."),
		BUTCHER_ALL_RADIUS("butcherKillAllRadius", 					"&7Suppression de &6{count} entité(s) &7dans un rayon de &6{radius} bloc(s)&7."),
		BUTCHER_TYPE("butcherKillType", 							"&7Suppression de &6{count} {entity}&6(s)&7 dans ce monde."),
		BUTCHER_TYPE_RADIUS("butcherKillTypeRadius", 				"&7Suppression de &6{count} &6{entity}&6(s)&7 dans un rayon de &6{radius} bloc(s)&7."),
		
		CLEAREFFECT_DESCRIPTION("cleareffectDescription", 			"Supprime tous les effets de potions d'un joueur."),
		CLEAREFFECT_PLAYER("cleareffectPlayer", 					"&7Tous vos effets de potions ont été supprimés."),
		CLEAREFFECT_NOEFFECT("cleareffectNoEffect", 				"&cErreur : Aucun effet de potion à supprimer."),
		CLEAREFFECT_OTHERS_PLAYER("cleareffectOthersPlayer", 		"&7Tous les effets de potions ont été supprimés par &6{staff}&7."),
		CLEAREFFECT_OTHERS_STAFF("cleareffectOthersStaff", 			"&7Tous les effets de potions de &6{player} &7ont été supprimés."),
		
		CLEARINVENTORY_DESCRIPTION("clearinventoryDescription", 	"Supprime tous les objets de l'inventaire d'un joueur."),
		CLEARINVENTORY_PLAYER("clearinventoryPlayer", 				"&7Vous venez de supprimer &6{amount} objet(s) &7de votre inventaire."),
		CLEARINVENTORY_NOITEM("clearinventoryNoItem", 				"&cErreur : Vous n'avez aucun objet dans l'inventaire."),
		CLEARINVENTORY_OTHERS_PLAYER("clearinventoryOthersPlayer",	"&6{staff} &7vient de supprimer &6{amount} objet(s) &7de votre inventaire."),
		CLEARINVENTORY_OTHERS_STAFF("clearinventoryOthersStaff", 	"&7Vous venez de supprimer &6{amount} objet(s) &7de l'inventaire de &6{player}&7."),
		CLEARINVENTORY_OTHERS_NOITEM("clearinventoryOthersNoItem", 	"&cErreur : &6{player} &cn'a aucun objet dans l'inventaire."),
		
		COLOR_DESCRIPTION("colorDescription", 						"Affiche les différentes couleurs dans Minecraft."),
		COLOR_LIST_TITLE("colorListTitle", 							"&l&4Liste des couleurs"), 
		COLOR_LIST_MESSAGE("colorListMessage", 						"{color}█ &0: {id}-{name}"), 
		
		EFFECT_DESCRIPTION("effectDescription", 					"Ajoute un effet de potion sur un joueur."),
		EFFECT_ERROR_NAME("effectErrorName", 						"&cErreur : Nom de l'effet invalide."),
		EFFECT_ERROR_DURATION("effectErrorDuration", 				"&cErreur : La durée de l'effet doit être compris entre {min} et {max}."),
		EFFECT_ERROR_AMPLIFIER("effectErrorAmplifier", 				"&cErreur : L'amplification de l'effet doit être compris entre {min} et {max}."),
		
		ENCHANT_DESCRIPTION("enchantDescription", 					"Enchante l'objet dans votre main."),
		ENCHANT_NOT_FOUND("enchantNotFound", 						"&cErreur : Cet enchantement n'existe pas."),
		ENCHANT_LEVEL_TOO_HIGHT("enchantLevelTooHight", 			"&cErreur : Le niveau de cet enchantement est trop élevé."),
		ENCHANT_LEVEL_TOO_LOW("enchantLevelTooLow", 				"&cErreur : Le niveau de cet enchantement est trop faible."),
		ENCHANT_INCOMPATIBLE("enchantIncompatible", 				"&cErreur : L'enchantement &6{enchantment} &cest incompatible avec l'objet &b[{item}&b]&c."),
		ENCHANT_ITEM_COLOR("enchantItemColor", 						"&b"),
		ENCHANT_SUCCESSFULL("enchantSuccessfull", 					"&7L'enchantement &6{enchantment} &7a été appliqué sur l'objet &b[{item}&b]&7."),
			
		ENDERCHEST_DESCRIPTION("enderchestDescription", 			"Ouvre le coffre de l'End d'un joueur"),
		ENDERCHEST_TITLE("enderchestTitle", 						"&8Coffre de l'Ender de {player}"),
		
		EXP_DESCRIPTION("expDescription", 							"Modifie l'expérience d'un joueur."),
		EXP_GIVE_LEVEL("expGiveLevel",								"&7Vous vous êtes ajouté &6{level} &7niveau(x)."),
		EXP_GIVE_EXP("expGiveExp", 									"&7Vous vous êtes ajouté &6{experience} &7point(s) d'expérience."),
		EXP_SET_LEVEL("expSetLevel", 								"&7Vous avez défini votre niveau à &6{level}&7."),
		EXP_SET_EXP("expSetExp", 									"&7Vous avez défini votre expérience à &6{experience}&7."),
		EXP_OTHERS_PLAYER_GIVE_LEVEL("expOthersPlayerGiveLevel", 	"&7Vous avez reçu &6{level} &7niveau(x) par &6{staff}&7."),
		EXP_OTHERS_STAFF_GIVE_LEVEL("expOthersStaffGiveLevel", 		"&7Vous avez ajouté &6{level} &7niveau(x) à &6{player}&7."),
		EXP_OTHERS_PLAYER_GIVE_EXP("expOthersPlayerGiveExp", 		"&7Vous avez reçu &6{experience} &7point(s) d'expérience par &6{staff}&7."),
		EXP_OTHERS_STAFF_GIVE_EXP("expOthersStaffGiveExp", 			"&7Vous avez ajouté &6{experience} &7point(s) d'expérience à &6{player}&7."),
		EXP_OTHERS_PLAYER_SET_LEVEL("expOthersPlayerSetLevel", 		"&7Votre niveau a été modifié à &6{level} &7par &6{staff}&7."),
		EXP_OTHERS_STAFF_SET_LEVEL("expOthersStaffSetLevel", 		"&7Vous avez modifié le niveau de &6{player} &7à &6{level}&7."),
		EXP_OTHERS_PLAYER_SET_EXP("expOthersPlayerSetExp", 			"&7Votre expérience a été modifié à &6{experience} &7par &6{staff}&7."),
		EXP_OTHERS_STAFF_SET_EXP("expOthersStaffSetExp", 			"&7Vous avez modifié l'expérience de &6{player} &7à &6{experience}&7."),
		
		EXT_DESCRIPTION("extDescription", 							"Retire le feu sur un joueur."),
		EXT_PLAYER("extPlayer", 									"&7Vous n'êtes plus en feu."),
		EXT_PLAYER_ERROR("extPlayerError", 							"&7Vous n'êtes pas en feu."),
		EXT_OTHERS_PLAYER("extOthersPlayer", 						"&7Vous n'êtes plus en feu grâce à &6{staff}&7."),
		EXT_OTHERS_STAFF("extOthersStaff", 							"&7Vous avez retiré le feu sur &6{player}&7."),
		EXT_OTHERS_ERROR("extOthersError", 							"&6{player} &7n'est pas en feu."),
		EXT_ALL_STAFF("extAllStaff", 								"&7Vous avez retiré le feu sur tous les joueurs."),
		
		FEED_DESCRIPTION("feedDescription", 						"Satisfait la faim d'un joueur."),
		FEED_PLAYER("feedPlayer", 									"&7Vous vous êtes rassasié."),
		FEED_OTHERS_STAFF("feedOthersStaff", 						"&7Vous avez rassasié &6{player}."),
		FEED_OTHERS_PLAYER("feedOthersPlayer", 						"&7Vous avez été rassasié par &6{staff}&7."),
		FEED_ALL_STAFF("feedAllStaff", 								"&7Vous avez rassasié tous les joueurs."),
		
		FORMAT_DESCRIPTION("formatDescription", 					"Affiche les différents formats dans Minecraft."),
		FORMAT_LIST_TITLE("formatListTitle", 						"&l&4Liste des formats"), 
		FORMAT_LIST_MESSAGE("formatListMessage", 					"{format}Stone &0: {id}-{name}"),
		FORMAT_OBFUSCATED("formatObfuscated", 						"Obfusqué"),
		FORMAT_BOLD("formatBold", 									"Gras"),
		FORMAT_STRIKETHROUGH("formatStrikethrough", 				"Barré"),
		FORMAT_UNDERLINE("formatUnderline", 						"Souligné"),
		FORMAT_ITALIC("formatItalic", 								"Italique"),
		FORMAT_RESET("formatReset", 								"Réinitialisation"),
		
		FREEZE_DESCRIPTION("freezeDescription", 								"Gère la paralysie sur un joueur"),
		
		FREEZE_ON_DESCRIPTION("freezeOnDescription",							"Paralyse un joueur"),
		FREEZE_ON_PLAYER("freezeOnPlayer",  									"&7Vous êtes désormais paralysé."),
		FREEZE_ON_PLAYER_ERROR("freezeOnPlayerError",							"&cErreur : Vous êtes déjà paralysé."),
		FREEZE_ON_PLAYER_CANCEL("freezeOnPlayerCancel",							"&7Vous venez d'être paralysé par &6{staff}&7."),
		FREEZE_ON_OTHERS_PLAYER("freezeOnOthersStaffEnable", 					"&7Vous venez de paralyser &6{player}&7."),
		FREEZE_ON_OTHERS_STAFF("freezeOnOthersStaffEnableError", 				"&cErreur : {player} &7est déjà paralysé."),
		FREEZE_ON_OTHERS_ERROR("freezeOnOthersError", 							"&cErreur : &6{player} &cest déjà paralysé."),
		FREEZE_ON_OTHERS_CANCEL("freezeOnOthersCancel", 						"&cImpossible de paralyser &6{player}&c."),
		
		FREEZE_OFF_DESCRIPTION("freezeOffDescription", 							"Libère un joueur paralysé"),
		FREEZE_OFF_PLAYER("freezeOffPlayer", 									"&7Vous êtes désormais libre."),
		FREEZE_OFF_PLAYER_ERROR("freezeOffPlayerError", 						"&cErreur : Vous êtes déjà libre."),
		FREEZE_OFF_PLAYER_CANCEL("freezeOffPlayerCancel",						"&7Vous êtes libre grâce à &6{staff}&7."),
		FREEZE_OFF_OTHERS_PLAYER("freezeOffOthersPlayer",						"&7Vous venez de libérer &6{player}&7."),
		FREEZE_OFF_OTHERS_STAFF("freezeOffOthersStaff", 						"&cErreur : {player} &7est déjà libre."),
		FREEZE_OFF_OTHERS_ERROR("freezeOffOthersError", 						"&cErreur : &6{player} &cest déjà paralysé."),
		FREEZE_OFF_OTHERS_CANCEL("freezeOffOthersCancel", 						"&cImpossible de paralyser &6{player}&c."),
		
		FREEZE_STATUS_DESCRIPTION("freezeStatusDescription", 					"Affiche si le joueur est paralysé où libre"),
		FREEZE_STATUS_PLAYER_ON("freezeStatusPlayerOn", 						"&7Vous êtes paralysé."),
		FREEZE_STATUS_PLAYER_OFF("freezeStatusPlayerOff", 						"&7Vous êtes libre."),
		FREEZE_STATUS_OTHERS_ON("freezeStatusOthersOn", 						"&6{player} &7est paralysé."),
		FREEZE_STATUS_OTHERS_OFF("freezeStatusOthersOff", 						"&6{player} &7est libre."),
		
		FREEZE_NO_COMMAND("freezeNoCommand", 									"&7Vous ne pouvez pas exécuter de commande en étant paralysé."),

		FLY_DESCRIPTION("flyDescription", 										"Permet de vous envoler"),
		FLY_ON_DESCRIPTION("flyOnDescription", 									"Permet d'accorder le droit de s'envoler à un joueur"),

		FLY_ON_PLAYER("flyOnPlayer", 											"&7Vous pouvez désormais vous envoler."),
		FLY_ON_PLAYER_ERROR("flyOnPlayerError", 								"&cVous possèdez déjà le droit de vous envoler."),
		FLY_ON_PLAYER_CANCEL("flyOnPlayerCancel", 								"&cImpossible de vous accorder le droit de vous envoler."),
		FLY_ON_OTHERS_PLAYER("flyOnOthersPlayer", 								"&7Vous pouvez désormais vous envoler grâce à &6{staff}&7."),
		FLY_ON_OTHERS_STAFF("flyOnOthersStaff", 								"&7Vous venez d'accorder le droit de s'envoler à &6{player}&7."),
		FLY_ON_OTHERS_ERROR("flyOnOthersError", 								"&6{player} &7possède déjà le droit de s'envoler."),
		FLY_ON_OTHERS_CANCEL("flyOnOthersCancel", 								"&cImpossible d'accorder le droit de s'envoler à &6{player}&c."),

		FLY_OFF_DESCRIPTION("flyOffDescription", 								"Permet de retirer le droit de s'envoler à un joueur"),

		FLY_OFF_PLAYER("flyOffPlayer", 											"&7Vous ne pouvez plus vous envoler."),
		FLY_OFF_PLAYER_ERROR("flyOffPlayerError", 								"&7Vous ne pouvez pas vous envoler."),
		FLY_OFF_PLAYER_CREATIVE("flyOffPlayerCreative", 						"&7Vous ne pouvez pas vous enlever le droit de vous envoler quand vous êtes en mode créative."),
		FLY_OFF_PLAYER_CANCEL("flyOffPlayerCancel", 							"&cImpossible de vous retirer le droit de vous envoler."),
		FLY_OFF_OTHERS_PLAYER("flyOffOthersPlayer", 							"&7Vous ne pouvez plus vous envoler à cause de &6{staff}&7."),
		FLY_OFF_OTHERS_STAFF("flyOffOthersStaff", 								"&7Vous venez de retirer le droit de s'envoler à &6{player}&7."),
		FLY_OFF_OTHERS_ERROR("flyOffOthersError", 								"&6{player} &7ne possède pas le droit de s'envoler."),
		FLY_OFF_OTHERS_CREATIVE("flyOffOthersCreative", 						"&cVous ne pouvez pas enlever le droit de s'envoler à &6{player} &ccar il est en mode créative."),
		FLY_OFF_OTHERS_CANCEL("flyOffOthersCancel", 							"&cImpossible de retirer le droit de s'envoler à &6{player}&c."),
		
		FLY_STATUS_DESCRIPTION("flyStatusDescription", 							"Permet de savoir si un joueur peut s'envoler"),
		FLY_STATUS_PLAYER_ON("flyStatusPlayerOn", 								"&7Vous pouvez vous envoler."),
		FLY_STATUS_PLAYER_OFF("flyStatusPlayerOff", 							"&7Vous ne pouvez pas vous envoler."),
		FLY_STATUS_OTHERS_ON("flyStatusOthersOn", 								"&6{player} &7peut s'envoler."),
		FLY_STATUS_OTHERS_OFF("flyStatusOthersOff", 							"&6{player} &7ne peut pas s'envoler."),
		
		GAMEMODE_DESCRIPTION("gamemodeDescription", 							"Change le mode de jeu d'un joueur"),
		GAMEMODE_PLAYER_CHANGE("gamemodePlayerChange", 							"&7Vous êtes désormais en mode de jeu &6{gamemode}&7."),
		GAMEMODE_PLAYER_EQUAL("gamemodePlayerEqual", 							"&7Vous êtes déjà en mode de jeu &6{gamemode}&7."),
		GAMEMODE_OTHERS_STAFF_CHANGE("gamemodeOthersStaffChange", 				"&7Mode de jeu &6{gamemode} &7pour &6{player}&7."),
		GAMEMODE_OTHERS_PLAYER_CHANGE("gamemodeOthersPlayerChange", 			"&7Votre mode de jeu a été changé en &6{gamemode} &7par &6{staff}&7."),
		GAMEMODE_OTHERS_EQUAL("gamemodeOthersEqual", 							"&6{player} &7possède déjà le mode de jeu &6{gamemode}&7."),
		GAMEMODE_ERROR_NAME("gamemodeErrorName", 								"&cMode de jeu inconnu."),
		
		GAMERULE_DESCRIPTION("gameruleDescription", 							"Gère les différentes règles d'un monde"),
		GAMERULE_ADD_DESCRIPTION("gameruleAddDescription", 						"Ajoute une règle personnalisée sur un monde"),
		GAMERULE_ADD_GAMERULE("gameruleAddGamerule", 							"&7Vous venez d'ajouter la gamerule &6'{gamerule}' &7avec la valeur &6'{valeur}'&7."),
		GAMERULE_ADD_ERROR("gameruleAddError", 									"&cErreur : Cette gamerule existe déjà."),
		GAMERULE_REMOVE_DESCRIPTION("gameruleRemoveDescription", 				"Supprime une règle personnalisée sur un monde"),
		GAMERULE_SET_DESCRIPTION("gameruleSetDescription", 						"Modifie une règle sur un monde"),
		
		GAMERULE_LIST_DESCRIPTION("gameruleListDescription", 					"Affiche la liste des règles du serveur"),
		GAMERULE_LIST_TITLE("gameruleListTitle", 								"&aListe des règles du monde &6{world}"),
		GAMERULE_LIST_LINE("gameruleListLine", 									"    &6&l➤  {gamerule} &7: {statut}"),
		
		GENERATE_DESCRIPTION("generateDescription", 							"Initialise tous les chunks d'un monde"),
		GENERATE_WARNING("generateWarningMessage", 								"&cAttention : &7Générer tous les chunks d'un monde peut prendre plusieurs heures et causer des latences."
																				  + "[RT] Le nombre total de chunks a générer est de &6{chunk}&7."
																				  + "[RT] Souhaitez-vous vraiment générer tous les chuncks du monde &6{world} ? {confirmation}"),
		GENERATE_WARNING_VALID("generateWarningConfirmationValid", 				"&a[Confirmer]"),
		GENERATE_WARNING_VALID_HOVER("generateWarningConfirmationValidHover", 	"&cCliquez ici pour lancer la génération des chunks dans le monde &6{world}&7."),
		GENERATE_LAUNCH("generateLaunch", 										"&7Génération du monde &6{world} &7lancée avec succès."),
				
		GETPOS_DESCRIPTION("getposDescription", 								"Affiche les coordonnées d'un joueur"),
		GETPOS_MESSAGE("getposMessage", 										"&7Voici votre &6{position}&7."),
		GETPOS_MESSAGE_OTHERS("getposMessageOthers", 							"&7Voici la {position} &7de &6{player}&7."),
		GETPOS_POTISITON_NAME("getposPositionName", 							"&6&lposition"),
		GETPOS_POSITION_HOVER("getposPositionHover", 							"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		
		GOD_DESCRIPTION("godDescription", 										"Gère l'invulnérabilité d'un joueur"),
		
		GOD_ON_DESCRIPTION("godOnDescription", 									"Rend le joueur invulnérable"),
		GOD_ON_PLAYER("godOnPlayer", 											"&7Vous êtes désormais invulnérable."),
		GOD_ON_PLAYER_ERROR("godOnPlayerError", 								"&cErreur : Vous êtes déjà invulnérable."),
		GOD_ON_PLAYER_CANCEL("godOnPlayerCancel", 								"&cImpossible de vous rendre invulnérable."),
		GOD_ON_OTHERS_PLAYER("godOnOthersPlayer", 								"&7Vous êtes désormais invulnérable grâce à &6{staff}&7."),
		GOD_ON_OTHERS_STAFF("godOnOthersStaff", 								"&7Vous venez de rendre invulnérable &6{player}&7."),
		GOD_ON_OTHERS_ERROR("godOnOthersError", 								"&cErreur : &6{player} &cest déjà invulnérable."),
		GOD_ON_OTHERS_CANCEL("godOnOthersCancel", 								"&cImpossible de rendre &6{player} &cinvulnérable."),
		
		GOD_OFF_DESCRIPTION("godOffDescription", 								"Rend le joueur vulnérable"),
		GOD_OFF_PLAYER("godOffPlayer", 											"&7Vous êtes désormais vulnérable."),
		GOD_OFF_PLAYER_ERROR("godOffPlayerError", 								"&cErreur : Vous êtes déjà vulnérable."),
		GOD_OFF_PLAYER_CANCEL("godOffPlayerCancel", 							"&cImpossible de vous rendre vulnérable."),
		GOD_OFF_OTHERS_PLAYER("godOffOthersPlayer", 							"&7Vous n'êtes plus invulnérable à cause de &6{staff}&7."),
		GOD_OFF_OTHERS_STAFF("godOffOthersStaff", 								"&7Vous venez de rendre vulnérable &6{player}&7."),
		GOD_OFF_OTHERS_ERROR("godOffOthersError", 								"&cErreur : &6{player} &cest déjà vulnérable."),
		GOD_OFF_OTHERS_CANCEL("godOffOthersCancel", 							"&cImpossible de rendre &6{player} &cvulnérable."),
		
		GOD_STATUS_DESCRIPTION("godStatusDescription", 							"Affiche si le joueur est vulnérable où pas"),
		GOD_STATUS_PLAYER_ON("godStatusPlayerOn", 								"&7Vous êtes invulnérable."),
		GOD_STATUS_PLAYER_OFF("godStatusPlayerOff", 							"&7Vous êtes vulnérable."),
		GOD_STATUS_OTHERS_ON("godStatusOthersOn", 								"&6{player} &7est invulnérable."),
		GOD_STATUS_OTHERS_OFF("godStatusOthersOff", 							"&6{player} &7est vulnérable."),
		
		GOD_TELEPORT("godTeleport", 											"&7Vous avez été téléporté car vous étiez en train de tomber dans le vide."),
		
		HAT_DESCRIPTION("hatDescription", 										"Place l'objet dans votre main sur votre tête"),
		HAT_ITEM_COLOR("hatItemColor", 											"&6"),
		HAT_NO_EMPTY("hatNoEmpty", 												"&7Vous ne pouvez pas mettre un objet sur votre tête quand vous avez un {item}&7."),
		HAT_IS_HAT("hatIsHat", 													"&7Votre nouveau chapeau : &6{item}&7."),
		HAT_REMOVE("hatRemove", 												"&7Vous avez enlevé l'objet sur votre chapeau."),
		HAT_REMOVE_EMPTY("hatRemoveEmpty", 										"&cVous n'avez actuellement aucun chapeau."),
		
		HEAL_DESCRIPTION("healDescription", 									"Soigne un joueur."),
		HEAL_PLAYER("healPlayer", 												"&7Vous vous êtes soigné."),
		HEAL_OTHERS_PLAYER("healOthersPlayer", 									"&7Vous avez été soigné par &6{staff}&7."),
		HEAL_OTHERS_STAFF("healOthersStaff", 									"&7Vous avez soigné &6{player}&7."),
		HEAL_OTHERS_DEAD_STAFF("healOthersDeadStaff",						 	"&6{player}&7 est déjà mort."),
		HEAL_ALL_STAFF("healAllStaff", 											"&7Vous avez soigné tous les joueurs."),
		
		HELP_DESCRIPTION("helpDescription", 									"Affiche les informations sur les commandes disponibles du serveur."),
		HELP_TITLE("helpTitle", 												"&aListe des commandes"),
		HELP_SEARCH_TITLE("helpSearchtitle", 									"&aListe des commandes contenant '{command}'"),
		
		HOME_NAME("homeName", 													"&6&l{name}"),
		HOME_NAME_HOVER("homeNameHover", 										"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		
		HOME_DESCRIPTION("homeDescription", 									"Téléporte le joueur à une résidence."),
		HOME_LIST_TITLE("homeListTitle", 										"&aListe des résidences"),
		HOME_LIST_LINE("homeListLine", 											"    &6&l➤  {home} &7: {teleport} {delete}"),
		HOME_LIST_LINE_ERROR_WORLD("homeListLineErrorWorld", 					"    &6&l➤  {home} &7: {delete}"),
		HOME_LIST_TELEPORT("homeListTeleport", 									"&a[Téléporter]"),
		HOME_LIST_TELEPORT_HOVER("homeListTeleportHover",						"&cCliquez ici pour vous téléporter à la résidence &6{home}&c."),
		HOME_LIST_DELETE("homeListDelete", 										"&c[Supprimer]"),
		HOME_LIST_DELETE_HOVER("homeListDeleteHover", 							"&cCliquez ici pour supprimer la résidence &6{home}&c."),
		HOME_EMPTY("homeEmpty", 												"&cVous n'avez aucune résidence."),
		HOME_INCONNU("homeInconnu", 											"&cVous n'avez pas de résidence qui s'appelle &6{home}&c."),
		HOME_TELEPORT("homeTeleport", 											"&7Vous avez été téléporté à la résidence &6{home}&7."),

		DELHOME_DESCRIPTION("delhomeDescription", 								"Supprime une résidence"),
		DELHOME_CONFIRMATION("delhomeConfirmation", 							"&7Souhaitez-vous vraiment supprimer la résidence &6{home} &7: {confirmation}"),
		DELHOME_CONFIRMATION_VALID("delhomeConfirmationValid", 					"&a[Confirmer]"),
		DELHOME_CONFIRMATION_VALID_HOVER("delhomeConfirmationValidHover", 		"&cCliquez ici pour supprimer la résidence &6{home}&c."),
		DELHOME_DELETE("delhomeDelete",											"&7Vous avez supprimé la résidence &6{home}&7."),
		DELHOME_INCONNU("delhomeInconnu", 										"&cVous n'avez pas de résidence qui s'appelle &6{home}&c."),
		
		HOMEOTHERS_DESCRIPTION("homeOthersDescription", 						"Gère les résidences d'un joueur"),
		HOMEOTHERS_LIST_TITLE("homeOthersListTitle", 							"&aListe des résidences de {player}"),
		HOMEOTHERS_LIST_LINE("homeOthersListLine", 								"    &6&l➤  {home} &7: {teleport} {delete}"),
		HOMEOTHERS_LIST_TELEPORT("homeOthersListTeleport", 						"&a[Téléporter]"),
		HOMEOTHERS_LIST_TELEPORT_HOVER("homeOthersListTeleportHover", 			"&cCliquez ici pour vous téléporter à la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_LIST_DELETE("homeOthersListDelete", 							"&c[Supprimer]"),
		HOMEOTHERS_LIST_DELETE_HOVER("homeOthersListDeleteHover", 				"&cCliquez ici pour supprimer la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_EMPTY("homeOthersEmpty", 									"&6{player} &cn'a aucune résidence."),
		HOMEOTHERS_INCONNU("homeOthersInconnu",									"&6{player} &cn'a pas de résidence qui s'appelle &6{home}&c."),
		HOMEOTHERS_TELEPORT("homeOthersTeleport", 								"&7Vous avez été téléporté à la résidence &6{home} &7de &6{player}&7."),
		HOMEOTHERS_TELEPORT_ERROR("homeOthersTeleportError", 					"&cImpossible de vous téléporter à la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_DELETE_CONFIRMATION("homeOthersDeleteConfirmation", 			"&7Souhaitez-vous vraiment supprimer la résidence &6{home} &7de &6{player} &7: {confirmation}"),
		HOMEOTHERS_DELETEE_CONFIRMATION_VALID("homeOthersDeleteConfirmationValid", 			"&a[Confirmer]"),
		HOMEOTHERS_DELETE_CONFIRMATION_VALID_HOVER("homeOthersDeleteConfirmationValidHover", 	"&cCliquez ici pour supprimer la résidence &6{home} &cde &6{player}&c."),
		HOMEOTHERS_DELETE("homeOthersDelete", 													"&7Vous avez supprimé la résidence &6{home} &7de &6{player}&7."),
		
		SETHOME_DESCRIPTION("sethomeDescription", 						"Défini une résidence"),
		SETHOME_SET("sethomeSet", 										"&7Vous avez défini votre résidence."),
		SETHOME_SET_CANCEL("sethomeSetCancel", 							"&cImpossible de définir votre résidence."),
		SETHOME_MOVE("sethomeMove", 									"&7Vous avez redéfini votre résidence."),
		SETHOME_MOVE_CANCEL("sethomeMoveCancel", 						"&cImpossible de redéfinir votre résidence."),
		SETHOME_MULTIPLE_SET("sethomeMultipleSet", 						"&7Vous avez défini la résidence &6{home}&7."),
		SETHOME_MULTIPLE_SET_CANCEL("sethomeMultipleSetCancel", 		"&cImpossible de définir la résidence &6{home}&7."),
		SETHOME_MULTIPLE_MOVE("sethomeMultipleMove", 					"&7Vous avez redéfini la résidence &6{home}&7."),
		SETHOME_MULTIPLE_MOVE_CANCEL("sethomeMultipleMoveCancel", 		"&cImpossible de redéfinir la résidence &6{home}&7."),
		SETHOME_MULTIPLE_ERROR_MAX("sethomeMultipleErrorMax", 			"&cVous ne pouvez pas créer plus de {nombre} résidence(s)."),
		SETHOME_MULTIPLE_NO_PERMISSION("sethomeMultipleNoPermission", 	"&cVous n'avez pas la permission d'avoir plusieurs résidences."),
		
		// Ignore
		IGNORE_DESCRIPTION("ignoreDescription", 					"Gère la whitelist"),
		
		IGNORE_ADD_DESCRIPTION("ignoreAddDescription", 				"Permet d'ignorer un joueur"),
		IGNORE_ADD_PLAYER("ignoreAddPlayer", 						"&7Vous ignorez désormais &6{player}&7."),
		IGNORE_ADD_ERROR("ignoreAddError", 							"&cErreur : Vous ignorez déjà &6{player}&c."),
		IGNORE_ADD_CANCEL("ignoreAddCancel", 						"&cImpossible d'ignoré &6{player} &cpour le moment."),
		IGNORE_ADD_BYPASS("ignoreAddBypass", 						"&cImpossible d'ignoré &6{player} &ccar il fait partie des membres du staff."),
		
		IGNORE_REMOVE_DESCRIPTION("ignoreRemoveDescription", 		"Permet de plus ignorer un joueur"),
		IGNORE_REMOVE_PLAYER("ignoreRemovePlayer", 					"&7Vous n'ignorez plus &6{player}&7."),
		IGNORE_REMOVE_ERROR("ignoreRemoveError", 					"&cErreur : Vous n'ignorez pas &6{player}&c."),
		IGNORE_REMOVE_CANCEL("ignoreRemoveCancel", 					"&cImpossible d'arrêté d'ignoré &6{player} &cpour le moment."),
		
		IGNORE_LIST_DESCRIPTION("ignoreListDescription", 			"Affiche la liste des joueurs ignorer"),
		IGNORE_LIST_PLAYER_TITLE("ignoreListPlayerTitle", 			"&aListe des joueurs ignorés"),
		IGNORE_LIST_OTHERS_TITLE("ignoreListOthersTitle", 			"&aListe des joueurs ignorés par &6{player}"),
		IGNORE_LIST_LINE_DELETE("ignoreListLineDelete", 			"    &6&l➤  {player} &7: {delete}"),
		IGNORE_LIST_LINE("ignoreListLine",							"    &6&l➤  {player}"),
		IGNORE_LIST_REMOVE("ignoreListRemove", 						"&a[Supprimer]"),
		IGNORE_LIST_REMOVE_HOVER("ignoreListRemoveHover", 			"&cCliquez ici pour retirer &6{player} &cde la liste des joueurs ignorés."),
		IGNORE_LIST_EMPTY("ignoreListEmpty",						"&7Aucun joueur."),
		
		// Info
		INFO_DESCRIPTION("infoDescription", 						"Indique le type d'un objet"),
		INFO_PLAYER("infoPlayer",									"&7Le type de l'objet {item} &7est &6{type}&7."),
		INFO_ITEM_COLOR("infoItemColor", 							"&6"),
		
		ITEM_DESCRIPTION("itemDescription", 						"Donne un item spécifique"),
		ITEM_ERROR_ITEM_NOT_FOUND("itemErrorItemNotFound", 			"&cErreur : L'objet {item} n'existe pas."),
		ITEM_ERROR_ITEM_BLACKLIST("itemErrorItemBlacklist", 		"&cErreur : Vous ne pouvez pas vous donner cet objet car il se trouve dans la liste noire."),
		ITEM_ERROR_QUANTITY("itemError.quantity", 					"&cErreur : La quantité doit être compris entre &60 &7et &6{amount} &7objet(s)."),
		ITEM_ERROR_DATA("itemErrorData", 							"&cErreur : Le type de l'objet est incorrect."),
		ITEM_GIVE("itemGive", 										"&7Vous avez reçu {item}"),
		ITEM_GIVE_COLOR("itemGiveColor", 							"&6"),
		
		ITEM_LORE_DESCRIPTION("itemloreDescription", 				"Modifie la description d'un objet"),
		ITEM_LORE_ADD_DESCRIPTION("itemloreAddDescription", 		"Ajoute une ligne à la description d'un objet"),
		ITEM_LORE_ADD_LORE("itemloreAddName", 						"&7Description ajoutée à l'objet &b[{item}&b]&7."),
		ITEM_LORE_ADD_COLOR("itemloreAddColor", 					"&b"),
		ITEM_LORE_CLEAR_DESCRIPTION("itemloreClearDescription", 	"Supprime la description d'un objet"),
		ITEM_LORE_CLEAR_NAME("itemloreClearName", 					"&7La description de votre objet &b[{item}&b] &7a été supprimé."),
		ITEM_LORE_CLEAR_ERROR("itemloreClearError", 				"&cErreur : Votre objet &b[{item}&b] &cne possède pas de description."),
		ITEM_LORE_CLEAR_COLOR("itemloreClearColor", 				"&b"),
		ITEM_LORE_SET_DESCRIPTION("itemloreSetDescription", 		"Défini une ligne à la description d'un objet"),
		ITEM_LORE_SET_LORE("itemloreSetName", 						"&7La ligne &6{line} &7a été ajoutée de l'objet &b[{item}&b]&7."),
		ITEM_LORE_SET_COLOR("itemloreSetColor", 					"&b"),
		ITEM_LORE_REMOVE_DESCRIPTION("itemloreRemoveDescription",	"Supprime une ligne à la description d'un objet"),
		ITEM_LORE_REMOVE_LORE("itemloreRemoveName", 				"&7La ligne &6{line} &7a été supprimée de l'objet &b[{item}&b]&7."),
		ITEM_LORE_REMOVE_ERROR("itemloreRemoveError", 				"&cErreur : La ligne doit être comprise entre &61 &cet &6{max}&c."),
		ITEM_LORE_REMOVE_COLOR("itemloreRemoveColor", 				"&b"),
			
		ITEM_NAME_DESCRIPTION("itemnameDescription", 				"Modifie le nom d'un objet"),
		ITEM_NAME_SET_DESCRIPTION("itemnameSetDescription", 		"Défini le nom d'un objet"),
		ITEM_NAME_SET_NAME("itemnameSetName", 						"&7Vous avez renommé &b[{item-before}&b] &7en &b[{item-after}&b]&7."),
		ITEM_NAME_SET_COLOR("itemnameSetColor", 					"&b"),
		ITEM_NAME_SET_ERROR("itemnameSetError", 					"&cErreur : le nom d'un objet ne doit pas dépasser {amount} caractères."),
		ITEM_NAME_CLEAR_DESCRIPTION("itemnameClearDescription", 	"Supprime le nom d'un objet"),
		ITEM_NAME_CLEAR_NAME("itemnameClearName", 					"&7Votre nom de l'objet &b[{item}&b] &7a été supprimé."),
		ITEM_NAME_CLEAR_ERROR("itemnameClearError", 				"&cErreur : Votre objet &b[{item}&b] &cne possède pas de nom."),
		ITEM_NAME_CLEAR_COLOR("itemnameClearColor", 				"&b"),
		
		JUMP_DESCRIPTION("jumpDescription", 						"Vous téléporte à l'endroit de votre choix"),
		JUMP_TELEPORT("jumpTeleport", 								"&7Vous avez été téléporté à l'endroit de votre choix."),
		JUMP_TELEPORT_ERROR("jumpTeleportError", 					"&7Impossible de trouver une position pour vous téléporter."),
		
		KICK_DESCRIPTION("kickDescription", 						"Expulse un joueur du serveur"),
		KICK_DEFAULT_REASON("kickDefaultReason", 					"&7Veuillez respecter les règles du serveur."),
		KICK_MESSAGE("kickMessage", 								"&c&lExpulsion du serveur[RT][RT]&cRaison : &7{reason}[RT]"),
		KICK_BYPASS("kickBypass", 									"&cErreur : {player} ne peut pas être expulsé."),
		
		KICKALL_DESCRIPTION("kickallDescription",				 	"Expulse tous les joueurs du serveur"),
		KICKALL_MESSAGE("kickallMessage", 							"&c&lExpulsion du serveur[RT][RT]&cRaison : &7{reason}[RT]"),
		KICKALL_ERROR("kickallError", 								"&cErreur : Il n'y a aucun joueur à expulser du serveur."),
		
		KILL_DESCRIPTION("killDescription", 						"Tue un joueur"),
		KILL_PLAYER("killPlayer", 									"&7Vous avez été tué par &6{staff}&7."),
		KILL_PLAYER_DEATH_MESSAGE("killPlayerDeathMessage", 		"&f<" + EReplacesPlayer.DISPLAYNAME.getName() + "&f> s'est tué par {staff}&f."),
		KILL_PLAYER_CANCEL("killPlayerCancel", 						"&cImpossible de tuer &6{player}&c."),
		KILL_STAFF("killStaff", 									"&7Vous avez tué &6{player}&7."),
		KILL_EQUALS("killEquals", 									"&7Vous vous êtes suicidé."),
		
		KILL_EQUALS_DEATH_MESSAGE("killEqualsDeathMessage", 		"&f<" + EReplacesPlayer.DISPLAYNAME.getName() + "> s'est suicidé&f."),
		KILL_EQUALS_CANCEL("killEqualsCancel", 						"&cImpossible de vous suicider."),
		
		LAG_DESCRIPTION("lagDescription", 							"Connaître l'état du serveur"),
		LAG_TITLE("lagTitle", 										"&aInformations sur le serveur"),
		LAG_TIME("lagTime", 										"    &6&l➤  &6Durée de fonctionnement : &c{time}"),
		LAG_TPS("lagTps",											"    &6&l➤  &6TPS actuel : &c{tps}"),
		LAG_HISTORY_TPS("lagHistoryTps", 							"    &6&l➤  &6Historique TPS : {tps}"),
		LAG_HISTORY_TPS_HOVER("lagHistoryTpsHover", 				"&6Minute : &c{num}[RT]&6TPS : &c{tps}"),
		LAG_MEMORY("lagMemory", 									"    &6&l➤  &6RAM : &c{usage}&6/&c{total} &6Mo"),
		LAG_WORLDS("lagWorlds", 									"    &6&l➤  &6Liste des mondes : [RT]{worlds}"),
		LAG_WORLDS_SEPARATOR("lagWorldsSeparator", 					"[RT]"),
		LAG_WORLDS_WORLD("lagWorldsWorld", 							"        &6&l●  &a{world}"),
		LAG_WORLDS_WORLD_HOVER("lagWorldsWorldHover", 				"&6Chunks : &c{chunks}[RT]&6Entités : &c{entities}[RT]&6Tiles : &c{tiles}"),
		
		LIST_DESCRIPTION("listDescription", 						"Affiche la liste des joueurs connecté"),
		LIST_TITLE("listTitle", 									"&aListe des joueurs connectés : &6" + EReplacesServer.ONLINE_PLAYERS.getName() + " &a/ &6" + EReplacesServer.MAX_PLAYERS.getName() + ""),
		LIST_TITLE_VANISH("listTitleVanish", 						"&aListe des joueurs connectés : &6" + EReplacesServer.ONLINE_PLAYERS.getName() + " &a(+&6{vanish}&a) / &6" + EReplacesServer.MAX_PLAYERS.getName()),
		LIST_GROUP("listGroup", 									"&6{group}&f : {players}"),
		LIST_SEPARATOR("listSeparator", ", "),
		LIST_PLAYER("listPlayer", 									"{afk}&r{vanish}&r" + EReplacesPlayer.DISPLAYNAME.getName()),
		LIST_TAG_AFK("listTagAFK", 									"&7[AFK] "),
		LIST_TAG_VANISH("listTagVanish", 							"&7[VANISH] "),
		LIST_EMPTY("listEmpty", 									"&7Aucun joueur"),
		
		MAIL_DESCRIPTION("mailDescription", 						"Gestion de vos messages"),
		
		MAIL_READ_DESCRIPTION("mailReadDescription", 				"Lis les messages"),
		MAIL_READ_TITLE("mailReadTitle", 							"&aLa liste des messages"),
		MAIL_READ_LINE_READ("mailReadLineRead", 					"  &a&l➤&7 De &6{player}&7 le &6{date} &7à &6{time} : {read} {delete}"),
		MAIL_READ_LINE_NO_READ("mailReadLineNoRead", 				"  &6&l➤&7 De &6{player}&7 le &6{date} &7à &6{time} : {read} {delete}"),
		MAIL_READ_EMPTY("mailReadEmpty", 							"&7Vous n'avez aucun message"),
		MAIL_READ_CANCEL("mailReadCancel", 							"&cImpossible de lire le {mail}."),
		MAIL_READ_MAIL("mailReadMail", 								"&6message"),
		MAIL_READ_MAIL_HOVER("mailReadMailHover", 					"&7De &6{player}[RT]&7Le &6{date}"),
		MAIL_READ_ERROR("mailReadError", 							"&cVous n'avez pas de message qui correspond."),
		
		MAIL_DELETE_DESCRIPTION("mailDeleteDescription", 			"Supprime le message séléctionné"),
		MAIL_DELETE_MESSAGE("mailDeleteMessage", 					"&7Voulez-vous vraiment supprimer le {mail} de &6{player}&7 le &6{date} &7à &6{time} : {confirmation}."),
		MAIL_DELETE_VALID("mailDeleteValid",	 					"&a[Confirmer]"),
		MAIL_DELETE_VALID_HOVER("mailDeleteValidHover", 			"&cCliquez ici pour supprimer le message."),
		MAIL_DELETE_CONFIRMATION("mailDeleteConfirmation", 			"&7Le {mail} &7a bien été supprimé."),
		MAIL_DELETE_CANCEL("mailDeleteCancel", 						"&7Le {mail} &7n'a pas pu être supprimé."),
		MAIL_DELETE_MAIL("mailDeleteMail", 							"&6message"),
		MAIL_DELETE_MAIL_HOVER("mailDeleteMailHover", 				"&7De &6{player}[RT]&7Le &6{date}"),
		MAIL_DELETE_ERROR("mailDeleteError", 						"&cVous n'avez pas de message qui correspond."),
		
		MAIL_CLEAR_DESCRIPTION("mailClearDescription", 				"Supprime tous vos messages"),
		MAIL_CLEAR_MESSAGE("mailClearMessage", 						"&7Vous avez supprimé tous vos messages."),
		MAIL_CLEAR_CANCEL("mailClearCancel", 						"&7Impossible de supprimé tous vos messages."),
		MAIL_CLEAR_ERROR("mailClearError", 							"&cVous n'avez pas de message à supprimer."),
		
		MAIL_SEND_DESCRIPTION("mailSendDescription", 				"Envoie un message à un ou plusieurs joueurs"),
		MAIL_SEND_MESSAGE("mailSendMessage", 						"&7Votre message a été envoyé à &6{player}&7."),
		MAIL_SEND_CANCEL("mailSendCancel", 							"&cImpossible d'envoyé le message à &6{player}&7."),
		MAIL_SEND_EQUALS("mailSendEquals", 							"&7Votre message a été envoyé."),
		MAIL_SEND_ALL("mailSendAll", 								"&7Votre message a été envoyé à tous les joueurs."),
		MAIL_SEND_IGNORE_PLAYER("mailSendIgnorePlayer", 			"&cImpossible d'envoyer un mail à &6{player}&c car vous l'ignorez."),
		MAIL_SEND_IGNORE_RECEIVE("mailSendIgnoreReceive",			"&6{player}&c ne recevera pas votre mail car il vous ignore."),
		
		MAIL_BUTTON_READ("mailButtonRead", 							"&a[Lire]"),
		MAIL_BUTTON_READ_HOVER("mailButtonReadHover", 				"&cCliquez ici pour lire le message."),
		MAIL_BUTTON_DELETE("mailButtonDelete", 						"&c[Supprimer]"),
		MAIL_BUTTON_DELETE_HOVER("mailButtonDeleteHover", 			"&cCliquez ici pour supprimer le message."),
		
		MAIL_NEW_MESSAGE("mailNewMessage", 							"&7Vous avez un nouveau message. {message}"),
		MAIL_BUTTON_NEW_MESSAGE("mailButtonNewMessage", 			"&a[Cliquez ici]"),
		MAIL_BUTTON_NEW_MESSAGE_HOVER("mailButtonNewMessageHover", 	"&7Cliquez ici pour afficher la liste des messages."),
		
		ME_DESCRIPTION("meDescription", 							"Envoie un texte d'action dans le tchat"),
		ME_PLAYER("mePlayer", 										"&f* " + EReplacesPlayer.NAME.getName() + " &r{message}"),
		
		MOJANG_DESCRIPTION("mojangDescription", 					"Affiche les informations sur les serveurs de mojang"),
		MOJANG_TITLE("mojangTitle", 								"&aLes serveurs de Mojang"),
		MOJANG_LINE("mojangLine", 									"    &6&l➤  &6{server} : {color}"),
		MOJANG_SERVER_ACCOUNT("mojangServerAccount", 				"Account"),
		MOJANG_SERVER_API("mojangServerAPI", 						"API"),
		MOJANG_SERVER_MOJANG("mojangServerMojang", 					"Mojang"),
		MOJANG_SERVER_AUTH("mojangServerAuth", 						"Auth"),
		MOJANG_SERVER_AUTHSERVER("mojangServerAuthServer", 			"AuthServer"),
		MOJANG_SERVER_MINECRAFT_NET("mojangServerMinecraftNet", 	"MinecraftNet"),
		MOJANG_SERVER_SESSION("mojangServerSession", 				"Session"),
		MOJANG_SERVER_SESSIONSERVER("mojangServerSessionServer", 	"SessionServer"),
		MOJANG_SERVER_SKINS("mojangServerSkins", 					"Skins"),
		MOJANG_SERVER_TEXTURES("mojangServerTextures", 				"Textures"),
		MOJANG_STATUS_ONLINE("mojangStatusOnline", 					"&aEn ligne"),
		MOJANG_STATUS_WARN("mojangStatusWarn", 						"&6Problème de connexion"),
		MOJANG_STATUS_OFFLINE("mojangStatusOffline", 				"&4Hors ligne"),
		
		MORE_DESCRIPTION("moreDescription", 						"Donne la quantité maximum d'un objet"),
		MORE_PLAYER("morePlayer", 									"&7Vous avez maintenant &6{quantity} &6{item}&7."),
		MORE_ITEM_COLOR("moreItemColor", 							"&6"),
		MORE_MAX_QUANTITY("moreMaxQuantity", 						"&7Vous avez déjà la quantité maximum de cette objet."),
		
		MOTD_DESCRIPTION("motdDescription", 						"Affiche le message du jour."),
		
		NAMES_DESCRIPTION("namesDescription", 						"Affiche l'historique des noms d'un joueur"),
		NAMES_PLAYER_TITLE("namesPlayerTitle", 						"&aVotre historique de nom"),
		NAMES_PLAYER_LINE_ORIGINAL("namesPlayerLineOriginal", 		"    &6&l➤  &6{name} &7: &cAchat du compte"),
		NAMES_PLAYER_LINE_OTHERS("namesPlayerLineOthers", 			"    &6&l➤  &6{name} &7: &c{datetime}"),
		NAMES_PLAYER_EMPTY("namesPlayerEmpty", 						"&7Vous n'avez aucun historique de pseudo"),
		NAMES_OTHERS_TITLE("namesOthersTitle", 						"&aHistorique de &6{player}"),
		NAMES_OTHERS_LINE_ORIGINAL("namesOthersLineOriginal", 		"    &6&l➤  &6{name} &7: &cAchat du compte"),
		NAMES_OTHERS_LINE_OTHERS("namesOthersLineOthers", 			"    &6&l➤  &6{name} &7: &c{datetime}"),
		NAMES_OTHERS_EMPTY("namesOthersEmpty", 						"&6{player} &7n'a aucun historique de pseudo"),
		
		NEAR_DESCRIPTION("nearDescription", 						"Donne la liste des joueurs dans les environs"),
		NEAR_LIST_LINE("nearListLine", 								"    &6&l➤  &6{player} &7: &6{distance} bloc(s)"),
		NEAR_LIST_TITLE("nearListTitle", 							"&aListe des joueurs dans les environs"),
		NEAR_NOPLAYER("nearNoPlayer", 								"&cAucun joueur dans les environs."),
		
		PING_DESCRIPTION("pingDescription", 						"Connaître la latence d'un joueur"),
		PING_PLAYER("pingPlayer", 									"&7Votre ping : &6{ping} &7ms."),
		PING_OTHERS("pingOthers", 									"&7Le ping de &6{player} &7: &6{ping} &7ms."),
		
		PLAYED_DESCRIPTION("playedDescription", 					"Connaître le temps de jeu d'un joueur"),
		PLAYED_PLAYER("playedPlayer", 								"&7Votre temps de jeu : &6{time}&7."),
		PLAYED_OTHERS("playedOthers", 								"&7Le temps de jeu de &6{player} &7: &6{time}&7."),
		
		INVSEE_DESCRIPTION("invseeDescription", 					"Regarde l'inventaire d'un autre joueur"),
		
		RELOAD_ALL_DESCRIPTION("reloadDescription", 				"Recharge tous les plugins"),
		RELOAD_ALL_START("reloadStart", 							"&cAttention : Rechargement de tous les plugins, risque de latence"),
		RELOAD_ALL_END("reloadEnd", 								"&aRechargement terminé"),
		
		REPAIR_DESCRIPTION("repairDescription", 					"Répare les objets"),
		
		REPAIR_HAND_DESCRIPTION("repairhandDescription", 			"Répare l'objet dans votre main"),
		REPAIR_HAND_ITEM_COLOR("repairhandItemColor", 				"&b"),
		REPAIR_HAND_PLAYER("repairhandPlayer", 						"&7Vous venez de réparer l'objet &b[{item}&b]&7."),
		REPAIR_HAND_ERROR("repairhandError", 						"&7Vous ne pouvez pas réparer {item}&7."),
		REPAIR_HAND_MAX_DURABILITY("repairhandMaxDurability", 		"&6{item} &7est déjà réparé."),
		
		REPAIR_HOTBAR_DESCRIPTION("repairhotbarDescription", 		"Répare les objets dans votre barre d'action"),
		REPAIR_HOTBAR_PLAYER("repairhotbarPlayer", 					"&7Vous venez de réparer tous les objets de votre barre d'action."),
		
		REPAIR_ALL_DESCRIPTION("repairallDescription", 				"Répare tous vos objets"),
		REPAIR_ALL_PLAYER("repairallPlayer", 						"&7Vous venez de réparer tous les objets de votre inventaire."),
		
		MSG_DESCRIPTION("msgDescription", 					"Envoie un message privé à un autre joueur"),
		MSG_PLAYER_SEND("msgPlayerSend", 					"&dEnvoyer à &f{DISPLAYNAME} &d: &7{message}"),
		MSG_PLAYER_SEND_HOVER("msgPlayerSendHover", 		"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		MSG_PLAYER_RECEIVE("msgPlayerReceive", 				"&dReçu de &f{DISPLAYNAME} &d: &7{message}"),
		MSG_PLAYER_RECEIVE_HOVER("msgPlayerReceiveHover", 	"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		MSG_PLAYER_SEND_IS_AFK("msgPlayerSendIsAFK", 		"&7{player} &7est absent."),
		MSG_CONSOLE_SEND("msgConsoleSend", 					"&dEnvoyer à la &6console &d: &7{message}"),
		MSG_CONSOLE_SEND_HOVER("msgConsoleSendHover", 		"&cCliquez ici pour répondre à la console"),
		MSG_CONSOLE_RECEIVE("msgConsoleReceive", 			"&dReçu de la &6console &d: &7{message}"),
		MSG_CONSOLE_RECEIVE_HOVER("msgConsoleReceiveHover",	"&cCliquez ici pour répondre à la console"),
		MSG_CONSOLE_ERROR("msgConsoleError",				"&cImpossible de vous envoyez un message à vous même."),
		MSG_COMMANDBLOCK_RECEIVE("msgCommandblockReceive", 	"&dVous avez reçu un message &d: &7{message}"),
		MSG_IGNORE_PLAYER("msgIgnorePlayer", 				"&cImpossible d'envoyer un message à &6{player}&c car vous l'ignorez."),
		MSG_IGNORE_RECEIVE("msgIgnoreReceive",				"&6{player}&c ne recevera pas votre message car il vous ignore."),
		
		REPLY_DESCRIPTION("replyDescription", 					"Répond à un message privé d'un autre joueur"),
		REPLY_PLAYER_SEND("replyPlayerSend", 					"&dEnvoyer à &f{DISPLAYNAME} &d: &7{message}"),
		REPLY_PLAYER_SEND_HOVER("replyPlayerSendHover", 		"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		REPLY_PLAYER_RECEIVE("replyPlayerReceive", 				"&dReçu de &f{DISPLAYNAME} &d: &7{message}"),
		REPLY_PLAYER_RECEIVE_HOVER("replyPlayerReceiveHover", 	"&cCliquez ici pour répondre à {DISPLAYNAME}"),
		REPLY_CONSOLE_SEND("replyConsoleSend", 					"&dEnvoyer à la &6console &d: &7{message}"),
		REPLY_CONSOLE_SEND_HOVER("replyConsoleSendHover", 		"&cCliquez ici pour répondre à la console"),
		REPLY_CONSOLE_RECEIVE("replyConsoleReceive", 			"&dReçu de la &6console &d: &7{message}"),
		REPLY_CONSOLE_RECEIVE_HOVER("replyConsoleReceiveHover",	"&cCliquez ici pour répondre à la console"),
		REPLY_EMPTY("replyEmpty", 								"&cVous n'êtes en conversation avec aucune personne, pour en démarrez une fait &6/msg {joueur} {message}"),
		REPLY_IGNORE_PLAYER("replyIgnorePlayer", 				"&cImpossible de répondre un message à &6{player}&c car vous l'ignorez."),
		REPLY_IGNORE_RECEIVE("replyIgnoreReceive",				"&6{player}&c ne recevera pas votre message car il vous ignore."),
		
		RULES_DESCRIPTION("rulesDescription", 		"Affiche les règles d'Evercraft."),
		
		SAY_DESCRIPTION("sayDescription", 			"Envoie un message à tous les joueurs."),
		SAY_PLAYER("sayPlayer", 					"&7[&6{player}&7] {message}"),
		SAY_CONSOLE("sayConsole", 					"&7[&6Console&7] {message}"),
		SAY_COMMANDBLOCK("sayCommandblock", 		"&7[&6CommandBlock&7] {message}"),
		
		// SEED
		SEED_DESCRIPTION("seedDescription", 										"Affiche le seed d'un monde"),
		SEED_MESSAGE("seedMessage", 												"&7Le seed du monde &6{world} &7est &6{seed}&7."),
		SEED_NAME("seedName",														"&6&l{seed}"),
		
		SEEN_DESCRIPTION("seenDescription", 										"Affiche la dernière IP de connexion d'un joueur"),
		SEEN_IP("seenIp", 															"&7Votre IP est &6{ip}&7."),
		SEEN_IP_OTHERS("seenIpOthers", 												"&7L'adresse IP de &6{player} &7est &6{ip}&7."),
		SEEN_IP_OTHERS_NO_IP("seenIpOthersNoIp", 									"&6{player} &7n'a pas d'adresse IP."),
		SEEN_IP_STYLE("seenIpStyle", 												"&6{ip}"),
		SEEN_PLAYER_STYLE("seenPlayerStyle", 										"&6{player}"),
		SEEN_IP_TITLE("whoisIpTitle", 												"&aInformations : &c{ip}"),
		SEEN_IP_MESSAGE("whoisIpMessage", 											"&7L'adresse IP &6{ip} &7correspond à :"),
		SEEN_IP_LIST("whoisIpList", 												"    &6&l➤  &6{player}"),
		SEEN_IP_NO_PLAYER("whoisIpNoPlayer", 										"&7Aucun joueur"),
		
		// SKULL
		SKULL_DESCRIPTION("skullDescription", 										"Donne la tête d'un joueur"),
		SKULL_MY_HEAD("skullMyHead", 												"&7Vous avez reçu votre tête."),
		SKULL_OTHERS("skullOthers", 												"&7Vous avez reçu la tête de &6{player}&7."),
		
		// SPAWN
		SPAWN_DESCRIPTION("spawnDescription", 										"Permet de téléporter au spawn"),
		SPAWN_PLAYER("spawnPlayer", 												"&7Vous avez été téléporté au &6{spawn}&7."),
		SPAWN_DELAY("spawnDelay", 													"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		SPAWN_NAME("spawnName", 													"&6spawn"),
		SPAWN_NAME_HOVER("spawnNameHover", 											"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SPAWN_ERROR_GROUP("spawnErrorGroup", 										"&cIl y a aucun groupe qui porte le nom &6{name}."),
		SPAWN_ERROR_SET("spawnErrorSet", 											"&cIl y a aucun spawn défini pour &6{name}."),
		SPAWN_ERROR_TELEPORT("spawnErrorTeleport", 									"&cImpossible de vous téléporter au {spawn}&7."),
		
		SPAWNS_DESCRIPTION("spawnsDescription",										"Affiche la liste des spawns"),
		SPAWNS_EMPTY("spawnsEmpty", 												"&7Aucun spawn."),
		SPAWNS_TITLE("spawnsTitle", 												"&aListe des spawns"),
		SPAWNS_LINE_DELETE("spawnsLineDelete", 										"    &6&l➤  &6{spawn} &7: {teleport} {delete}"),
		SPAWNS_LINE_DELETE_ERROR_WORLD("spawnsLineDeleteErrorWorld", 				"    &6&l➤  &6{spawn} &7: {delete}"),
		SPAWNS_LINE("spawnsLine", 													"    &6&l➤  &6{spawn} &7: {teleport}"),
		SPAWNS_TELEPORT("spawnsTeleport", 											"&a[Téléporter]"),
		SPAWNS_TELEPORT_HOVER("spawnsTeleportHover", 								"&cCliquez ici pour vous téléporter au spawn &6{name}&c."),
		SPAWNS_DELETE("spawnsDelete", 												"&c[Supprimer]"),
		SPAWNS_DELETE_HOVER("spawnsDeleteHover", 									"&cCliquez ici pour supprimer le spawn &6{name}&c."),
		SPAWNS_NAME("spawnsName", 													"&6{name}"),
		SPAWNS_NAME_HOVER("spawnsNameHover", 										"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SPAWNS_PLAYER("spawnsPlayer", 												"&7Vous avez été téléporté au spawn &6{spawn}&7."),
		SPAWNS_DELAY("spawnsDelay", 												"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		SPAWNS_ERROR_TELEPORT("spawnsErrorTeleport",								"&cImpossible de vous téléporter au spawn {spawn}&7."),

		DELSPAWN_DESCRIPTION("delspawnDescription", 								"Supprime un spawn"),
		DELSPAWN_INCONNU("delspawnInconnu", 										"&cIl n'y pas de spawn qui s'appelle &6{spawn}&c."),
		DELSPAWN_NAME("delspawnName", 												"&6{name}"),
		DELSPAWN_NAME_HOVER("delspawnNameHover", 									"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		DELSPAWN_CONFIRMATION("delspawnConfirmation", 								"&7Souhaitez-vous vraiment supprimer le spawn &6{spawn} &7: {confirmation}"),
		DELSPAWN_CONFIRMATION_VALID("delspawnConfirmationValid", 					"&a[Confirmer]"),
		DELSPAWN_CONFIRMATION_VALID_HOVER("delspawnConfirmationValidHover", 		"&cCliquez ici pour supprimer le spawn &6{name}&c."),
		DELSPAWN_DELETE("delspawnDelete", 											"&7Vous avez supprimé le spawn &6{spawn}&7."),

		SETSPAWN_DESCRIPTION("setspawnDescription", 								"Permet de définir un spawn"),
		SETSPAWN_ERROR_GROUP("setspawnErrorGroup", 									"&cIl y a aucun groupe qui porte le nom &6{name}."),
		SETSPAWN_NAME("setspawnName", 												"&6{name}"),
		SETSPAWN_NAME_HOVER("setspawnNameHover", 									"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SETSPAWN_REPLACE("setspawnReplace", 										"&7Vous avez redéfini le spawn &6{name}&7."),
		SETSPAWN_NEW("setspawnNew", 												"&7Vous avez défini le spawn &6{name}&7."),
		
		// SPAWNER
		SPAWNER_DESCRIPTION("spawnerDescription", 									"Permet de modifier le type d'un mob spawner"),
		
		// SPAWNMOB
		SPAWNMOB_DESCRIPTION("spawnmobDescription", 								"Fait apparaître une entité"),
		SPAWNMOB_MOB("spawnmobMob",													"&7Vous venez d'invoquer &6{amount} {entity}(s)&7."),
		SPAWNMOB_ERROR_MOB("spawnmobErrorMob", 										"&cErreur : nom invalide."),
		
		// SPEED
		SPEED_DESCRIPTION("speedDescription", 										"Change la vitesse de déplacement"),
		SPEED_INFO_WALK("speedInfoWalk", 											"&7Votre vitesse de &6marche &7est de &6{speed}&7."),
		SPEED_INFO_FLY("speedInfoFly", 												"&7Votre vitesse de &6vol &7est de &6{speed}&7."),
		SPEED_PLAYER_WALK("speedPlayerWalk", 										"&7Vous avez défini votre vitesse de &6marche &7à &6{speed}&7."),
		SPEED_PLAYER_FLY("speedPlayerFly", 											"&7Vous avez défini votre vitesse de &6vol &7à &6{speed}&7."),
		SPEED_OTHERS_PLAYER_WALK("speedOthersPlayerWalk", 							"&7Votre vitesse de marche a été défini à &6{speed} &7par &6{staff}&7."),
		SPEED_OTHERS_STAFF_WALK("speedOthersStaffWalk", 							"&7Vous avez défini la vitesse de &6marche &7de &6{player} &7à &6{speed}&7."),
		SPEED_OTHERS_PLAYER_FLY("speedOthersPlayerFly", 							"&7Votre vitesse de vol a été défini à &6{speed} &7par &6{staff}&7."),
		SPEED_OTHERS_STAFF_FLY("speedOthersStaffFly", 								"&7Vous avez défini la vitesse de &6vol &7de &6{player} &7à &6{speed}&7."),
		
		// STOP
		STOP_DESCRIPTION("stopDescription", 										"Arrête le serveur"),
		STOP_MESSAGE("stopMessage", 												"&cArrêt du serveur par &6{staff}"),
		STOP_MESSAGE_REASON("stopMessageReason", 									"&c{reason}"),
		STOP_CONSOLE_MESSAGE("stopConsoleMessage", 									"&cArrêt du serveur"),
		STOP_CONSOLE_MESSAGE_REASON("stopConsoleMessageReason", 					"&c{reason}"),
		
		// SUDO
		SUDO_DESCRIPTION("sudoDescription", 										"Fait exécuter une commande par un autre joueur"),
		SUDO_COMMAND("sudoCommand", 												"&6commande"),
		SUDO_COMMAND_HOVER("sudoCommandHover", 										"&c{command}"),
		SUDO_PLAYER("sudoPlayer", 													"&7Votre {command} &7a bien été éxecutée par &6{player}&7."),
		SUDO_BYPASS("sudoBypass", 													"&cVous ne pouvez pas faire exécuter de commande à &6{player}&7."),
		SUDO_CONSOLE("sudoConsole", 												"&7Votre {command} &7à bien été éxecutée par la &6console&7."),
		
		// SUICIDE
		SUICIDE_DESCRIPTION("suicideDescription", 									"Permet de vous suicider"),
		SUICIDE_PLAYER("suicidePlayer", 											"&7Vous vous êtes suicidé."),
		
		SUICIDE_DEATH_MESSAGE("suicideEqualsDeathMessage", 							"&f<" + EReplacesPlayer.DISPLAYNAME.getName() + "> s'est suicidé."),
		SUICIDE_CANCEL("suicideEqualsCancel", 										"&cImpossible de vous suicider."),
		
		// TP
		TP_DESCRIPTION("tpDescription", 													"Téléporte le joueur vers un autre joueur"),
		TP_DESTINATION("tpDestination", 													"&6&l{player}"),
		TP_DESTINATION_HOVER("tpDestinationHover", 											"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TP_PLAYER("tpPlayer", 																"&7Vous avez été téléporté vers &6{destination}&7."),
		TP_PLAYER_EQUALS("tpPlayerEquals", 													"&7Vous avez été repositionné."),
		TP_OTHERS_PLAYER("tpOthersPlayer", 													"&6{staff} &7vous a téléporté vers &6{destination}."),
		TP_OTHERS_STAFF("tpOthersStaff", 													"&6{player} &7a été téléporté vers &6{destination}&7."),
		TP_OTHERS_PLAYER_REPOSITION("tpOthersPlayerReposition", 							"&6{staff} &7vient de vous repositionner."),
		TP_OTHERS_STAFF_REPOSITION("tpOthersStaffReposition", 								"&7Vous venez de repositionner &6{player}&7."),
		TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER("tpOthersStaffEqualsDestinationPlayer", 	"&6{destination} &7vous a téléporté."),
		TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF("tpOthersStaffEqualsDestinationStaff", 	"&7Vous venez de téléporter &6{player}&7."),
		TP_ERROR_LOCATION("tpErrorLocation", 												"&cImpossible de trouver une position pour réaliser une téléportation."),
		
		// TPALL
		TPALL_DESCRIPTION("tpallDescription", 										"Téléporte tous les joueurs vers un autre joueur"),
		TPALL_DESTINATION("tpallDestination", 										"&6&l{player}"),
		TPALL_DESTINATION_HOVER("tpallDestinationHover", 							"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPALL_PLAYER("tpallPlayer", 												"&6{destination} &7vous a téléporté."),
		TPALL_STAFF("tpallStaff", 													"&7Vous venez de téléporter tous les joueurs."),
		TPALL_ERROR("tpallError", 													"&cImpossible de trouver une position pour téléporter les joueurs."),
		TPALL_OTHERS_PLAYER("tpallOthersPlayer", 									"&6{staff} &7vous a téléporté vers &6{destination}."),
		TPALL_OTHERS_STAFF("tpallOthersStaff", 										"&7Tous les joueurs ont été téléportés vers &6{destination}&7."),
		
		// TPHERE
		TPHERE_DESCRIPTION("tphereDescription", 									"Téléporte le joueur vers vous"),
		TPHERE_DESTINATION("tphereDestination", 									"&6&l{player}"),
		TPHERE_DESTINATION_HOVER("tphereDestinationHover", 							"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPHERE_PLAYER("tpherePlayer", 												"&6{destination} &7vous a téléporté."),
		TPHERE_STAFF("tphereStaff", 												"&7Vous venez de téléporter &6{player}&7."),
		TPHERE_EQUALS("tphereEquals", 												"&7Vous avez été repositionné."),
		TPHERE_ERROR("tphereError", 												"&cImpossible de trouver une position pour téléporter le joueur."),
		
		// TPPOS
		TPPOS_DESCRIPTION("tpposDescription", 										"Téléporte le joueur aux coordonnées choisis"),
		TPPOS_POSITION("tpposPosition", 											"&6&lposition"),
		TPPOS_POSITION_HOVER("tpposPositionHover", 									"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPPOS_PLAYER("tpposPlayer", 												"&7Vous avez été téléporté à cette {position}&7."),
		TPPOS_PLAYER_ERROR("tpposPlayerError", 										"&7Impossible de vous téléporter à cette {position}&7."),
		TPPOS_OTHERS_PLAYER("tpposOthersPlayer", 									"&7Vous avez été téléporté à cette {position} &7par &6{staff}&7."),
		TPPOS_OTHERS_STAFF("tpposOthersStaff", 										"&7Vous téléportez &6{player} &7à cette {position}&7."),
		TPPOS_OTHERS_ERROR("tpposOthersError", 										"&7Impossible de téléporter &6{player} &7à cette {position}&7."),
		
		TELEPORT_ERROR_DELAY("teleportErrorDelay", 									"&cVous avez bougé donc votre demande de téléportation a été annulé."),
		
		// TPA
		TPA_DESCRIPTION("tpaDescription", 											"Envoie une demande de téléportation à un joueur"),
		
		TPA_DESTINATION("tpaDestination", 											"&6&l{player}"),
		TPA_DESTINATION_HOVER("tpaDestinationHover", 								"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPA_STAFF_QUESTION("tpaStaffQuestion", 										"&7Votre demande a été envoyée à &6{player}&7."),
		TPA_STAFF_ACCEPT("tpaStaffAccept", 											"&7Votre demande de téléportation a été acceptée par &6{player}&7."),
		TPA_STAFF_DENY("tpaStaffDeny", 												"&7Votre demande de téléportation a été refusée par &6{player}&7."),
		TPA_STAFF_EXPIRE("tpaStaffExpire", 											"&7Votre demande de téléportation à &6{player} &7vient d'expirer."),
		TPA_STAFF_TELEPORT("tpaStaffTeleport", 										"&7Vous avez été téléporté vers &6{destination}&7."),
		TPA_PLAYER_QUESTION("tpaPlayerQuestion", 									"&6{player} &7souhaite se téléporter vers vous : [RT]  {accept} {deny}[RT]"
																				  + "&7Cette demande de téléportation expira dans &6{delay}&7."),
		TPA_PLAYER_QUESTION_ACCEPT("tpaPlayerQuestionAccept", 						"&a[Accepter]"),
		TPA_PLAYER_QUESTION_ACCEPT_HOVER("tpaPlayerQuestionAcceptHover", 			"&cCliquez ici pour accepter la téléportation de &6{player}&c."),
		TPA_PLAYER_QUESTION_DENY("tpaPlayerQuestionDeny", 							"&c[Refuser]"),
		TPA_PLAYER_QUESTION_DENY_HOVER("tpaPlayerQuestionDenyHover", 				"&cCliquez ici pour refuser la téléportation de &6{player}&7."),
		TPA_PLAYER_DENY("tpaPlayerDeny", 											"&7Vous avez refusé la demande de téléportation de &6{player}&7."),
		TPA_PLAYER_ACCEPT("tpaPlayerAccept", 										"&6{player} &7sera téléporté dans &6{delay}&7."),
		TPA_PLAYER_EXPIRE("tpaPlayerExpire", 										"&cLa demande de téléportation de &6{player} &ca expiré."),
		TPA_PLAYER_TELEPORT("tpaPlayerTeleport", 									"&6{player} &7vient d'être téléporté."),
		TPA_ERROR_EQUALS("tpaErrorEquals", 											"&cImpossible de vous envoyer une demande à vous même."),
		TPA_ERROR_DELAY("tpaErrorDelay", 											"&cIl y a déjà une demande de téléportation en cours."),
		TPA_ERROR_LOCATION("tpaErrorLocation", 										"&cImpossible de trouver une position pour réaliser une téléportation."),
		TPA_IGNORE_PLAYER("tpaIgnorePlayer", 										"&cImpossible d'envoyer une requete de téléportaion à &6{player}&c car vous l'ignorez."),
		TPA_IGNORE_DESTINATION("tpaIgnoreDestination",								"&6{player}&c ne recevera pas votre demande de téléportation car il vous ignore."),
		
		// TPHERE
		TPAHERE_DESCRIPTION("tpahereDescription", 									"Envoie une demande de téléportation à un joueur"),
		
		TPAHERE_DESTINATION("tpahereDestination", 									"&6&l{player}"),
		TPAHERE_DESTINATION_HOVER("tpahereDestinationHover", 						"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TPAHERE_STAFF_QUESTION("tpahereStaffQuestion", 								"&7Votre demande a été envoyée à &6{player}&7."),
		TPAHERE_STAFF_ACCEPT("tpahereStaffAccept", 									"&7Votre demande de téléportation a été acceptée par &6{player}&7."),
		TPAHERE_STAFF_DENY("tpahereStaffDeny", 										"&7Votre demande de téléportation a été refusée par &6{player}&7."),
		TPAHERE_STAFF_EXPIRE("tpahereStaffExpire", 									"&7Votre demande de téléportation à &6{player} &7vient d'expirée."),
		TPAHERE_STAFF_TELEPORT("tpahereStaffTeleport", 								"&6{player}&7 a été téléporté à vous."),
		TPAHERE_PLAYER_QUESTION("tpaherePlayerQuestion", 							"&6{player} &7souhaite que vous vous téléportiez à lui/elle : {accept} {deny}[RT]"
																				  + "&7Cette demande de téléportation expira dans &6{delay}&7."),
		TPAHERE_PLAYER_QUESTION_ACCEPT("tpaherePlayerQuestionAccept", 				"&a[Se téléporter]"),
		TPAHERE_PLAYER_QUESTION_ACCEPT_HOVER("tpaherePlayerQuestionAcceptHover", 	"&cCliquez ici pour vous téléporter à &6{player}&c."),
		TPAHERE_PLAYER_QUESTION_DENY("tpaherePlayerQuestionDeny", 					"&c[Refuser]"),
		TPAHERE_PLAYER_QUESTION_DENY_HOVER("tpaherePlayerQuestionDenyHover", 		"&cCliquez ici pour refuser la téléportation de &6{player}&7."),
		TPAHERE_PLAYER_EXPIRE("tpaherePlayerExpire", 								"&cLa demande de téléportation de &6{player} &ca expirée."),
		TPAHERE_PLAYER_DENY("tpaherePlayerDeny", 									"&7La demande de &6{player} &7 a bien été refusé."),
		TPAHERE_PLAYER_ACCEPT("tpaherePlayerAccept", 								"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		TPAHERE_PLAYER_TELEPORT("tpaherePlayerTeleport", 							"&7Vous avez été téléporté vers &6{destination}&7."),
		TPAHERE_ERROR_EQUALS("tpahereErrorEquals", 									"&cImpossible de vous envoyer une demande à vous même."),
		TPAHERE_ERROR_DELAY("tpahereErrorDelay", 									"&cIl y a déjà une demande de téléportation en cours."),
		TPAHERE_ERROR_LOCATION("tpahereErrorLocation", 								"&cImpossible de trouver une position pour réaliser une téléportation."),
		TPAHERE_IGNORE_STAFF("tpahereIgnoreStaff", 									"&cImpossible d'envoyer une requete de téléportaion à &6{player}&c car vous l'ignorez."),
		TPAHERE_IGNORE_PLAYER("tpahereIgnorePlayer",								"&6{player}&c ne recevera pas votre demande de téléportation car il vous ignore."),
		
		// TPA list
		TPA_PLAYER_LIST_TITLE("tpaPlayerListTitle", 								"&aListe des demandes de téléportation"),
		TPA_PLAYER_LIST_LINE("tpaPlayerListLine", 									"    &6&l➤  &6{player} &7: {accept} {deny}"),
		TPA_PLAYER_LIST_EMPTY("tpaPlayerListEmpty", 								"&7Aucune demande"),
		TPA_PLAYER_EMPTY("tpaPlayerEmpty", 											"&cVous n'avez aucune demande de téléportation de &6{player}&c."),
		
		// TPAALL
		TPAALL_DESCRIPTION("tpaallDescription", 									"Envoie une demande de téléportation à tous les joueurs"),
		TPAALL_PLAYER("tpaallPlayer", 												"&7Votre demande de téléportation a bien été envoyée à tous les joueurs."),
		TPAALL_OTHERS_STAFF("tpaallOthersStaff", 									"&7Votre demande pour téléporter tous les joueurs à &6{player} &7a bien été envoyée."),
		TPAALL_OTHERS_PLAYER("tpaallOthersPlayer", 									"&6{staff} &7a envoyé une demande de téléportation vers vous à tous les joueurs."),
		TPAALL_ERROR_EMPTY("tpaallErrorEmpty", 										"&cErreur : Aucun joueur à téléporter."),
		TPAALL_ERROR_PLAYER_LOCATION("tpaallErrorPlayerLocation", 					"&cErreur : Impossible de trouver une position pour téléporter les joueurs."),
		TPAALL_ERROR_OTHERS_LOCATION("tpaallErrorOthersLocation", 					"&cErreur : Impossible de trouver une position pour téléporter les joueurs sur &6{player}&c."),
		
		// TPACCEPT
		TPACCEPT_DESCRIPTION("tpacceptDescription", 								"Permet d'accepter une demande de téléportation"),
		
		// TPDENY
		TPDENY_DESCRIPTION("tpdenyDescription", 									"Permet de refuser une demande de téléportation"),
		
		// TIME
		TIME_DESCRIPTION("timeDescription", 										"Gère l'heure sur un monde"),
		TIME_FORMAT("timeFormat", 													"&6{hours}h{minutes}"),
		TIME_INFORMATION("timeInformation", 										"&7Il est actuellement &6{hours} &7dans le monde &6{world}&7."),
		TIME_SET_WORLD("timeSetWorld", 												"&7Il est désormais &6{hours} &7dans le monde &6{world}&7."),
		TIME_SET_ALL_WORLD("timeSetAllWorld", 										"&7Il est désormais &6{hours} &7dans les mondes&7."),
		TIME_ERROR("timeError", 													"&cErreur : Horaire incorrect."),
		
		TIME_DAY_DESCRIPTION("timeDayDescription", 									"Mettre le jour dans votre monde"),
		TIME_NIGHT_DESCRIPTION("timeNightDescription", 								"Mettre la nuit dans votre monde"),
		
		// TOGGLE
		TOGGLE_DESCRIPTION("toggleDescription", 									"Permet de gérer les demandes de téléportation"),
		
		TOGGLE_ON_DESCRIPTION("toggleOnDescription", 								"Active les demandes de téléportation"),
		TOGGLE_ON_PLAYER("toggleOnPlayer", 											"&7Vous acceptez désormais les demandes de téléportation."),
		TOGGLE_ON_PLAYER_ERROR("toggleOnPlayerError", 								"&cVous acceptez déjà les demandes de téléportation."),
		TOGGLE_ON_PLAYER_CANCEL("toggleOnPlayerCancel", 							"&cImpossible d'accepter les demandes de téléportation."),
		TOGGLE_ON_OTHERS_PLAYER("toggleOnOthersPlayer", 							"&7Vous acceptez désormais les demandes de téléportation grâce à &6{staff}&7."),
		TOGGLE_ON_OTHERS_STAFF("toggleOnOthersStaff", 								"&6{player} &7accepte désormais les demandes de téléportation."),
		TOGGLE_ON_OTHERS_ERROR("toggleOnOthersError", 								"&6{player} &caccepte déjà les demandes de téléportation."),
		TOGGLE_ON_OTHERS_CANCEL("toggleOnOthersCancel", 							"&cImpossible que &6{player} &caccepte les demandes de téléportation."),

		TOGGLE_OFF_DESCRIPTION("toggleOffDescription", 								"Désactive les demandes de téléportation"),
		TOGGLE_OFF_PLAYER("toggleOffPlayer", 										"&7Vous refusez désormais les demandes de téléportation."),
		TOGGLE_OFF_PLAYER_ERROR("toggleOffPlayerError", 							"&cVous refusez déjà les demandes de téléportation."),
		TOGGLE_OFF_PLAYER_CANCEL("toggleOffPlayerCancel", 							"&cImpossible de refuser les demandes de téléportation."),
		TOGGLE_OFF_OTHERS_PLAYER("toggleOffOthersPlayer", 							"&7Vous refusez désormais les demandes de téléportation grâce à &6{staff}&7."),
		TOGGLE_OFF_OTHERS_STAFF("toggleOffOthersStaff", 							"&6{player} &7refuse désormais les demandes de téléportation."),
		TOGGLE_OFF_OTHERS_ERROR("toggleOffOthersError", 							"&6{player} &crefuse déjà les demandes de téléportation."),
		TOGGLE_OFF_OTHERS_CANCEL("toggleOffOthersCancel", 							"&cImpossible que &6{player} &crefuse les demandes de téléportation."),
		
		TOGGLE_STATUS_DESCRIPTION("toggleStatusDescription", 						"Gère les demandes de téléportation"),
		TOGGLE_STATUS_PLAYER_ON("toggleStatusPlayerOn", 							"&7Vous acceptez les demandes de téléportation."),
		TOGGLE_STATUS_PLAYER_OFF("toggleStatusPlayerOff", 							"&7Vous n'acceptez pas les demandes de téléportation."),
		TOGGLE_STATUS_OTHERS_ON("toggleStatusOthersOn", 							"&6{player} &7accepte les demandes de téléportation."),
		TOGGLE_STATUS_OTHERS_OFF("toggleStatusOthersOff", 							"&6{player} &7n'accepte pas les demandes de téléportation."),
		
		TOGGLE_DISABLED("toggleDisabled", 											"&6{player} &7n’accepte pas les demandes de téléportation."),
		
		// TOP
		TOP_DESCRIPTION("topDescription", 											"Téléporte le joueur à la position la plus élevée"),
		TOP_POSITION("topPosition", 												"&6&lposition"),
		TOP_POSITION_HOVER("topPositionHover", 										"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		TOP_DELAY("topDelay", 														"&7Votre téléportation commencera dans &6{delay}&7. Ne bougez pas."),
		TOP_TELEPORT("topTeleport", 												"&7Vous avez été téléporté à la {position} &7la plus élevée."),
		TOP_TELEPORT_ERROR("topTeleportError", 										"&cImpossible de trouver une position où vous téléporter."),
		
		// TREE
		TREE_DESCRIPTION("treeDescription", 										"Place un arbre"),
		TREE_INCONNU("treeInconnu", 												"&cType d'arbre inconnu : &6{type}"),
		TREE_NO_CAN_DIRT("treeNoCanDirt",											"&cErreur : Impossible de placer un arbre à cette endroit. Regarder plutôt un bloc d'herbe ou de terre."),
		TREE_NO_CAN_SAND("treeNoCanSand", 											"&cErreur : Impossible de placer un arbre à cette endroit. Regarder plutôt un bloc de sable."),
		
		// UUID
		UUID_DESCRIPTION("uuidDescription", 										"Affiche l'identifiant unique du joueur."),
		UUID_NAME("uuidName", 														"&6&l{name}"),
		UUID_UUID("uuidUuid", 														"&6&l{uuid}"),
		UUID_PLAYER_UUID("uuidPlayerUUID", 											"&7Votre UUID est &6&l{uuid}&7."),
		UUID_PLAYER_NAME("uuidPlayerName", 											"&7Votre nom est &6&l{name}&7."),
		UUID_OTHERS_PLAYER_UUID("uuidOtherPlayerUUID", 								"&7L'UUID du joueur &6{player} &7est &6&l{uuid}&7."),
		UUID_OTHERS_PLAYER_NAME("uuidOtherPlayerName", 								"&7L'UUID &6{uuid} &7correspond au pseudo &6&l{name}"),
		
		// VANISH
		VANISH_DESCRIPTION("vanishDescription", 									"Permet de vous rendre invisible."),
		
		VANISH_ON_DESCRIPTION("vanishOnDescription", 								"Rend le joueur invisible"),
		VANISH_ON_PLAYER("vanishOnPlayer", 											"&7Vous êtes désormais invisible."),
		VANISH_ON_PLAYER_ERROR("vanishOnPlayerError", 								"&cErreur : Vous êtes déjà invisible."),
		VANISH_ON_PLAYER_CANCEL("vanishOnPlayerCancel", 							"&cImpossible de vous rendre invisible."),
		VANISH_ON_OTHERS_PLAYER("vanishOnOthersPlayer", 							"&7Vous êtes désormais invisible grâce à &6{staff}&7."),
		VANISH_ON_OTHERS_STAFF("vanishOnOthersStaff", 								"&7Vous venez de rendre invisible &6{player}&7."),
		VANISH_ON_OTHERS_ERROR("vanishOnOthersError", 								"&cErreur : &6{player} &cest déjà invisible."),
		VANISH_ON_OTHERS_CANCEL("vanishOnOthersCancel", 							"&cImpossible de rendre &6{player} &cinvisible."),
		
		VANISH_OFF_DESCRIPTION("vanishOffDescription", 								"Rend le joueur visible"),
		VANISH_OFF_PLAYER("vanishOffPlayer", 										"&7Vous êtes désormais visible."),
		VANISH_OFF_PLAYER_ERROR("vanishOffPlayerError", 							"&cErreur : Vous êtes déjà visible."),
		VANISH_OFF_PLAYER_CANCEL("vanishOffPlayerCancel", 							"&cImpossible de vous rendre visible."),
		VANISH_OFF_OTHERS_PLAYER("vanishOffOthersPlayer", 							"&7Vous désormais visible à cause de &6{staff}&7."),
		VANISH_OFF_OTHERS_STAFF("vanishOffOthersStaff", 							"&7Vous venez de rendre &6{player} &7visible."),
		VANISH_OFF_OTHERS_ERROR("vanishOffOthersError", 							"&cErreur : &6{player} &cest déjà visible."),
		VANISH_OFF_OTHERS_CANCEL("vanishOffOthersCancel", 							"&cImpossible de rendre &6{player} &cvible."),
	
		VANISH_STATUS_DESCRIPTION("vanishStatusDescription", 						"Affiche si le joueur est visible où pas"),
		VANISH_STATUS_PLAYER_ON("vanishStatusPlayerOn", 							"&7Vous êtes invisible."),
		VANISH_STATUS_PLAYER_OFF("vanishStatusPlayerOff", 							"&7Vous êtes visible."),
		VANISH_STATUS_OTHERS_ON("vanishStatusOthersOn", 							"&6{player} &7est invisible."),
		VANISH_STATUS_OTHERS_OFF("vanishStatusOthersOff", 							"&6{player} &7est visible."),
		
		// WARP
		WARP_NAME("warpName", 														"&6&l{name}"),
		WARP_NAME_HOVER("warpNameHover", 											"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		WARP_INCONNU("warpInconnu", 												"&cIl n'y a pas de warp qui s'appelle &6{warp}&c."),
		WARP_NO_PERMISSION("warpNoPermission", 										"&cVous n'avez pas la permission pour vous téléporter au warp &6{warp}&c."),
		
		WARP_DESCRIPTION("warpDescription", 										"Se téléporte à un warp"),
		WARP_EMPTY("warpEmpty", 													"&7Aucun warp"),
		WARP_LIST_TITLE("warpListTitle", 											"&aListe des warps"),
		WARP_LIST_LINE_DELETE("warpListLineDelete", 								"    &6&l➤  &6{warp} &7: {teleport} {delete}"),
		WARP_LIST_LINE_DELETE_ERROR_WORLD("warpListLineDeleteErrorWorld", 			"    &6&l➤  &6{warp} &7: {delete}"),
		WARP_LIST_LINE("warpListLine", 												"    &6&l➤  &6{warp} &7: {teleport}"),
		WARP_LIST_TELEPORT("warpListTeleport", 										"&a[Téléporter]"),
		WARP_LIST_TELEPORT_HOVER("warpListTeleportHover", 							"&cCliquez ici pour vous téléporter à le warp &6{warp}&c."),
		WARP_LIST_DELETE("warpListDelete", 											"&c[Supprimer]"),
		WARP_LIST_DELETE_HOVER("warpListDeleteHover", 								"&cCliquez ici pour supprimer le warp &6{warp}&c."),
		WARP_TELEPORT_PLAYER("warpTeleportPlayer", 									"&7Vous avez été téléporté au warp &6{warp}&7."),
		WARP_TELEPORT_PLAYER_ERROR("warpTeleportPlayerError", 						"&cImpossible de vous téléporter au warp &6{warp}&c."),
		WARP_TELEPORT_OTHERS_PLAYER("warpTeleportOthersPlayer", 					"&7Vous avez été téléporté au warp &6{warp} &7par &6{staff}&7."),
		WARP_TELEPORT_OTHERS_STAFF("warpTeleportOthersStaff", 						"&7Vous avez téléporté &6{player} &7au warp &6{warp}&7."),
		WARP_TELEPORT_OTHERS_ERROR("warpTeleportOthersError", 						"&cImpossible de téléporter &6{player} &7au warp &6{warp}&c."),
		
		DELWARP_DESCRIPTION("delwarpDescription", 									"Supprime un warp"),
		DELWARP_INCONNU("delwarpInconnu", 											"&cIl n'y pas de warp qui s'appelle &6{warp}&c."),
		DELWARP_NAME("delwarpName", 												"&6&l{name}"),
		DELWARP_NAME_HOVER("delwarpNameHover", 										"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		DELWARP_CONFIRMATION("delwarpConfirmation", 								"&7Souhaitez-vous vraiment supprimer le warp &6{warp} &7: {confirmation}"),
		DELWARP_CONFIRMATION_VALID("delwarpConfirmationValid", 						"&a[Confirmer]"),
		DELWARP_CONFIRMATION_VALID_HOVER("delwarpConfirmationValidHover", 			"&cCliquez ici pour supprimer le warp &6{warp}&c."),
		DELWARP_DELETE("delwarpDelete", 											"&7Vous avez supprimé le warp &6{warp}&7."),
		DELWARP_CANCEL("delwarpCancel", 											"&cImpossible de supprimé le &6{warp} &cpour le moment."),
		
		SETWARP_DESCRIPTION("setwarpDescription", 									"Crée un warp"),
		SETWARP_NAME("setwarpName", 												"&6&l{name}"),
		SETWARP_NAME_HOVER("setwarpNameHover", 										"&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		SETWARP_REPLACE("setwarpReplace", 											"&7Vous avez redéfini le warp &6{warp}&7."),
		SETWARP_REPLACE_CANCEL("setwarpReplaceCancel", 								"&cImpossible de redéfinir le warp &6{warp} &4pour le moment."),
		SETWARP_NEW("setwarpNew", 													"&7Vous avez défini le warp &6{warp}&7."),
		SETWARP_NEW_CANCEL("setwarpNewCancel", 										"&cImpossible de définir le warp &6{warp} &4pour le moment."),
		
		WEATHER_DESCRIPTION("weatherDescription", "Change la météo d'un monde"),
		WEATHER_ERROR("weatherError", "&cVous ne pouvez pas changer la météo dans ce type de monde."),
		WEATHER_SUN("weatherSun", "&7Vous avez mis &6le beau temps &7dans le monde &6{world}&7."),
		WEATHER_RAIN("weatherRain", "&7Vous avez mis &6la pluie &7dans le monde &6{world}&7."),
		WEATHER_STORM("weatherStorm", "&7Vous avez mis &6la tempête &7dans le monde &6{world}&7."),
		WEATHER_SUN_DURATION("weatherSunDuration", "&7Vous avez mis &6le beau temps &7dans le monde &6{world}&7 pendant &6{duration}&7 minute(s)."),
		WEATHER_RAIN_DURATION("weatherRainDuration", "&7Vous avez mis &6la pluie &7dans le monde &6{world}&7 pendant &6{duration}&7 minute(s)."),
		WEATHER_STORM_DURATION("weatherStormDuration", "&7Vous avez mis &6la tempête &7dans le monde &6{world}&7 pendant &6{duration}&7 minute(s)."),
		
		WEATHER_RAIN_DESCRIPTION("weatherRainDescription", "Met la pluie dans votre monde"),
		WEATHER_STORM_DESCRIPTION("weatherStormDescription", "Met la tempête dans votre monde"),
		WEATHER_SUN_DESCRIPTION("weatherSunDescription", "Met le beau dans temps dans votre monde"),
		
		// WhiteList
		WHITELIST_DESCRIPTION("whitelistDescription", "Gère la whitelist"),
		
		WHITELIST_ON_DESCRIPTION("whitelistOnDescription", "Active la whitelist"),
		WHITELIST_ON_ACTIVATED("whitelistOnActivated", "&7La whitelist est désormais &aactivée&7."),
		WHITELIST_ON_ALREADY_ACTIVATED("whitelistOnAlreadyActivated", "&cErreur : La whitelist est déjà activée."),
		
		WHITELIST_OFF_DESCRIPTION("whitelistOffDescription", "Désactive la whitelist"),
		WHITELIST_OFF_DISABLED("whitelistOffDisabled", "&7La whitelist est désormais &cdésactivée&7."),
		WHITELIST_OFF_ALREADY_DISABLED("whitelistOffAlreadyDisabled", "&cErreur : &7La whitelist est déjà &cdésactivée&7."),
		
		WHITELIST_STATUS_DESCRIPTION("whitelistStatusDescription", "Gère la liste d'acces des joueurs"),
		WHITELIST_STATUS_ACTIVATED("whitelistStatusActivated", "&7La whitelist est &aactivée&7."),
		WHITELIST_STATUS_DISABLED("whitelistStatusDisabled", "&7La whitelist est &cdésactivée&7."),
		
		WHITELIST_ADD_DESCRIPTION("whitelistAddDescription", "Ajoute un joueur dans la whitelist"),
		WHITELIST_ADD_PLAYER("whitelistAddPlayer", "&7Le joueur &6{player} &7a été ajouté dans la whitelist."),
		WHITELIST_ADD_ERROR("whitelistAddError", "&cErreur : Le joueur {player} est déjà dans la whitelist."),
		
		WHITELIST_REMOVE_DESCRIPTION("whitelistRemoveDescription", "Supprime un joueur dans la whitelist"),
		WHITELIST_REMOVE_PLAYER("whitelistRemovePlayer", "&7Le joueur &6{player} &7a été supprimé dans la whitelist."),
		WHITELIST_REMOVE_ERROR("whitelistRemoveError", "&cErreur : Le joueur {player} n'est pas dans la whitelist."),
		WHITELIST_LIST_DESCRIPTION("whitelistListDescription", "Affiche la whitelist"),
		
		WHITELIST_LIST_TITLE("whitelistListTitle", "&aWhitelist"),
		WHITELIST_LIST_LINE_DELETE("whitelistListLineDelete", "    &6&l➤  {player} &7: {delete}"),
		WHITELIST_LIST_LINE("whitelistListLine", "    &6&l➤  {player}"),
		WHITELIST_LIST_REMOVE("whitelistListRemove", "&c[Supprimer]"),
		WHITELIST_LIST_REMOVE_HOVER("whitelistListRemoveHover", "&cCliquez ici pour retirer &6{player} &cde la whitelist."),
		WHITELIST_LIST_NO_PLAYER("whitelistListNoPlayer", "&7Aucun joueur"),
		
		// Whois
		WHOIS_DESCRIPTION("whoisDescription", "Affiche les informations d'un joueur"),
		WHOIS_TITLE_OTHERS("whoisTitleOthers", "&aInformations : &c{player}"),
		WHOIS_TITLE_EQUALS("whoisTitleEquals", "&aVos informations"),
		WHOIS_UUID("whoisUuid", "    &6&l➤  &6UUID : {uuid}"),
		WHOIS_UUID_STYLE("whoisUuidStyle", "&c{uuid}"),
		WHOIS_IP("whoisIp", "    &6&l➤  &6IP : {ip}"),
		WHOIS_LAST_IP("whoisLastIp", "    &6&l➤  &6Dernière IP : {ip}"),
		WHOIS_IP_STYLE("whoisIpStyle", "&c{ip}"),
		WHOIS_PING("whoisPing", "    &6&l➤  &6Ping : &c{ping} &6ms"),
		WHOIS_HEAL("whoisHeal", "    &6&l➤  &6Santé : &a{heal}&6/&c{max_heal}"),
		WHOIS_FOOD("whoisFood", "    &6&l➤  &6Faim : &a{food}&6/&c{max_food}"),
		WHOIS_FOOD_SATURATION("whoisFoodSaturation", "    &6&l➤  &6Faim : &a{food}&6/&c{max_food} &6(+&a{saturation} &6saturation)"),
		WHOIS_EXP("whoisExp", "    &6&l➤  &6Expérience :"),
		WHOIS_EXP_LEVEL("whoisExpLevel", "        &6&l●  &a{level} &6niveau(x)"),
		WHOIS_EXP_POINT("whoisExpPoint", "        &6&l●  &a{point} &6point(s)"),
		WHOIS_SPEED("whoisSpeed", "    &6&l➤  &6Vitesse :"),
		WHOIS_SPEED_FLY("whoisSpeedFly", "        &6&l●  &6En volant : &a{speed}"),
		WHOIS_SPEED_WALK("whoisSpeedWalk", "        &6&l●  &6En marchant : &a{speed}"),
		WHOIS_LOCATION("whoisLocation", "    &6&l➤  &6Position : {position}"),
		WHOIS_LOCATION_POSITION("whoisLocationPosition", "&6(&c{x}&6, &c{y}&6, &c{z}&6, &c{world}&6)"),
		WHOIS_LOCATION_POSITION_HOVER("whoisLocationPositionHover", "&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		WHOIS_BALANCE("whoisBalance", "    &6&l➤  &6Solde : &c{money}"),
		WHOIS_GAMEMODE("whoisGamemode", "    &6&l➤  &6Mode de jeu : &c{gamemode}"),
		WHOIS_GOD_ENABLE("whoisGodEnable", "    &6&l➤  &6Mode Dieu : &aActivé"),
		WHOIS_GOD_DISABLE("whoisGodDisable", "    &6&l➤  &6Mode Dieu : &cDésactivé"),
		WHOIS_FLY_ENABLE_FLY("whoisFlyEnableFly", "    &6&l➤  &6Fly Mode : &aActivé &6(&avol&6)"),
		WHOIS_FLY_ENABLE_WALK("whoisFlyEnableWalk", "    &6&l➤  &6Fly Mode : &aActivé &6(&cmarche&6)"),
		WHOIS_FLY_DISABLE("whoisFlyDisable", "    &6&l➤  &6Fly Mode : &cDésactivé"),
		WHOIS_MUTE_ENABLE("whoisMuteEnable", "    &6&l➤  &6Muet : &aActivé"),
		WHOIS_MUTE_DISABLE("whoisMuteDisable", "    &6&l➤  &6Muet : &cDésactivé"),
		WHOIS_VANISH_ENABLE("whoisVanishEnable", "    &6&l➤  &6Vanish : &aActivé"),
		WHOIS_VANISH_DISABLE("whoisVanishDisable", "    &6&l➤  &6Vanish : &cDésactivé"),
		WHOIS_FREEZE_ENABLE("whoisFreezeEnable", "    &6&l➤  &6Freeze : &aActivé"),
		WHOIS_FREEZE_DISABLE("whoisFreezeDisable", "    &6&l➤  &6Freeze : &cDésactivé"),
		WHOIS_AFK_ENABLE("whoisAfkEnable", "    &6&l➤  &6AFK : &aActivé"),
		WHOIS_AFK_DISABLE("whoisAfkDisable", "    &6&l➤  &6AFK : &cDésactivé"),
		WHOIS_FIRST_DATE_PLAYED("whoisFirstDatePlayed", "    &6&l➤  &6Première connexion : &a{time}"),
		WHOIS_LAST_DATE_PLAYED_ONLINE("whoisLastDatePlayedOnline", "    &6&l➤  &6Connecté depuis : &a{time}"),
		WHOIS_LAST_DATE_PLAYED_OFFLINE("whoisLastDatePlayedOffLine", "    &6&l➤  &6Dernière connexion : &a{time}"),
		WHOIS_CHAT_FULL("whoisChatFull", "    &6&l➤  &6Chat : &aVisible"),
		WHOIS_CHAT_SYSTEM("whoisChatSystem", "    &6&l➤  &6Chat : &aCommandes seulement"),
		WHOIS_CHAT_HIDDEN("whoisChatHidden", "    &6&l➤  &6Chat : &aMasqué"),
		WHOIS_VIEW_DISTANCE("whoisViewDistance", "    &6&l➤  &6Distance d'affichage : &a{amount}"),
		WHOIS_CHATCOLOR_ON("whoisChatColorOn", "    &6&l➤  &6Couleur dans le chat : &aActivé"),
		WHOIS_CHATCOLOR_OFF("whoisChatColorOff", "    &6&l➤  &6Couleur dans le chat : &cDésactivé"),
		WHOIS_LANGUAGE("whoisLanguage", "    &6&l➤  &6Langage : &a{langue}"),
		WHOIS_TOGGLE_ENABLE("whoisToggleEnable", "    &6&l➤  &6Requêtes de téléportation : &aActivé"),
		WHOIS_TOGGLE_DISABLE("whoisToggleDisable", "    &6&l➤  &6Requêtes de téléportation : &cDésactivé"),
		WHOIS_TOTAL_TIME_PLAYED("whoisTotalTimePlayed", "    &6&l➤  &6Temps de jeu : &a{time}"),
		
		WORLDBORDER_DESCRIPTION("worldborderDescription", "Gère la bordure des mondes"),
		WORLDBORDER_INFO_DESCRIPTION("worldborderInfoDescription", "Affiche les informations sur la bordure d'un monde"),
		WORLDBORDER_INFO_TITLE("worldborderInfoTitle", "&6Monde : {world}"),
		WORLDBORDER_INFO_LOCATION("worldborderInfoLocation", "    &6&l➤  &6Centre : {position}"),
		WORLDBORDER_INFO_LOCATION_POSITION("worldborderInfoLocationPosition", "&6(&c{x}&6, &c{z}&6, &c{world}&6)"),
		WORLDBORDER_INFO_LOCATION_POSITION_HOVER("worldborderInfoLocationPositionHover", "&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cZ : &6{z}"),
		WORLDBORDER_INFO_BORDER("worldborderInfoBorder", "    &6&l➤  &6La bordure est de &a{amount} &6bloc(s)"),
		WORLDBORDER_INFO_BUFFER("worldborderInfoBuffer", "    &6&l➤  &6Zone de tolérance : &a{amount} &6bloc(s)"),
		WORLDBORDER_INFO_DAMAGE("worldborderInfoDamage", "    &6&l➤  &6Dégat(s) : &a{amount} &6coeur(s)"),
		WORLDBORDER_INFO_WARNING_TIME("worldborderInfoWarningTime", "    &6&l➤  &6Avertissement du rétrecissement de la bordure : &a{amount} &6seconde(s)"),
		WORLDBORDER_INFO_WARNING_DISTANCE("worldborderInfoWarningDistance", "    &6&l➤  &6Avertissement de la bordure : &a{admount} &6bloc(s)"),
		WORLDBORDER_SET_DESCRIPTION("worldborderSetDescription", "Défini la bordure d'un monde"),
		WORLDBORDER_SET_BORDER("worldborderSetBorder", "&7La taille de la bordure du monde &6{world} &7a été défini à &6{amount} &7bloc(s) de large."),
		WORLDBORDER_SET_BORDER_INCREASE("worldborderSetBorderIncrease", "&7Agrandissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_SET_BORDER_DECREASE("worldborderSetBorderDecrease", "&7Rétrécissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_CENTER_DESCRIPTION("worldborderCenterDescription", "Défini le centre de la bordure d'un monde"),
		WORLDBORDER_CENTER_MESSAGE("worldborderCenterMessage", "&7Le centre de la bordure du monde &6{world} &7a été défini en &6X: {x} Z: {z}&7."),
		WORLDBORDER_ADD_DESCRIPTION("worldborderAddDescription", "Augmente ou diminue la taille de la bordure d'un monde"),
		WORLDBORDER_ADD_BORDER_INCREASE("worldborderAddBorderIncrease", "&7Agrandissement de la bordure du monde &6{world} &7à {amount} bloc(s)."),
		WORLDBORDER_ADD_BORDER_DECREASE("worldborderAddBorderDecrease", "&7Rétrécissement de la bordure du monde &6{world} &7à {amount} bloc(s)."),
		WORLDBORDER_ADD_BORDER_TIME_INCREASE("worldborderAddBorderTimeIncrease", "&7Agrandissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_ADD_BORDER_TIME_DECREASE("worldborderAddBorderTimeDecrease", "&7Rétrécissement de la bordure du monde &6{world} &7à {amount} bloc(s) de large en &6{time} &7seconde(s)."),
		WORLDBORDER_DAMAGE_DESCRIPTION("worldborderDamageDescription", "Configure les dégats infligés aux entités en dehors de la bordure d'un monde"),
		WORLDBORDER_DAMAGE_AMOUNT("worldborderDamageAmount", "&7La quantité de dégâts causés par la bordure dans le monde &6{world} &7a été défini à &6{amount}&7."),
		WORLDBORDER_DAMAGE_BUFFER("worldborderDamageBuffer", "&7La zone de tolérance de la bordure du monde &6{world} &7a été défini à &6{amount}&7."),
		WORLDBORDER_WARNING_DESCRIPTION("worldborderWarningDescription", "Configure l'écran d'avertissement pour les joueurs qui s'approche de la bordure d'un monde"),
		WORLDBORDER_WARNING_TIME("worldborderWarningTime", "&7L'avertissement de la bordure du monde &6{world} &7a été défini à &6{amount} &7seconde(s)."),
		WORLDBORDER_WARNING_DISTANCE("worldborderWarningDistance", "&7L'avertissement de la bordure du monde &6{world} &7a été défini à &6{amount} &7bloc(s) de distance."),
			
		WORLDS_DESCRIPTION("worldsDescription", "Téléporte un joueur dans le monde de votre choix"),
		WORLDS_END_DESCRIPTION("worldsEndDescription", "Téléporte un joueur dans le monde du néant"),
		WORLDS_NETHER_DESCRIPTION("worldsNetherDescription", "Téléporte un joueur dans le monde de l'enfer"),
		WORLDS_LIST_TITLE("worldsListTitle", "&aListe des mondes"),
		WORLDS_LIST_LINE("worldsListLine", "    &6&l➤  &6{world} &7: {teleport}"),
		WORLDS_LIST_TELEPORT("worldsListTeleport", "&a[Téléporter]"),
		WORLDS_LIST_TELEPORT_HOVER("worldsListTeleportHover", "&cCliquez ici pour vous téléporter dans le monde &6{world}&c."),
		WORLDS_TELEPORT_WORLD("worldsTeleportWorld", "&6&l{world}"),
		WORLDS_TELEPORT_WORLD_HOVER("worldsTeleportWorldHover", "&cMonde : &6{world}[RT]&cX : &6{x}[RT]&cY : &6{y}[RT]&cZ : &6{z}"),
		WORLDS_TELEPORT_PLAYER("worldsTeleportPlayer", "&7Vous avez été téléporté dans le monde &6{world}&7."),
		WORLDS_TELEPORT_PLAYER_ERROR("worldsTeleportPlayerError", "&7Impossible de vous téléporter dans le monde {world}&7."),
		WORLDS_TELEPORT_OTHERS_PLAYER("worldsTeleportOthersPlayer", "&7Vous avez été téléporté dans le monde {world} &7par &6{staff}&7."),
		WORLDS_TELEPORT_OTHERS_STAFF("worldsTeleportOthersStaff", "&7Vous téléportez &6{player} &7dans le monde {world}&7."),
		WORLDS_TELEPORT_OTHERS_ERROR("worldsTeleportOthersError", "&7Impossible de trouver une position pour téléporter &6{player} &7dans le monde &6{world}&7.");
		
		private final String path;
	    private final EMessageBuilder french;
	    private final EMessageBuilder english;
	    private EMessageFormat message;
	    private EMessageBuilder builder;
	    
	    private EEMessages(final String path, final String french) {   	
	    	this(path, EMessageFormat.builder().chat(new EFormatString(french), true));
	    }
	    
	    private EEMessages(final String path, final String french, final String english) {   	
	    	this(path, 
	    		EMessageFormat.builder().chat(new EFormatString(french), true), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true));
	    }
	    
	    private EEMessages(final String path, final EMessageBuilder french) {   	
	    	this(path, french, french);
	    }
	    
	    private EEMessages(final String path, final EMessageBuilder french, final EMessageBuilder english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french.build();
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public EMessageBuilder getFrench() {
			return this.french;
		}

		public EMessageBuilder getEnglish() {
			return this.english;
		}
		
		public EMessageFormat getMessage() {
			return this.message;
		}
		
		public EMessageBuilder getBuilder() {
			return this.builder;
		}
		
		public void set(EMessageBuilder message) {
			this.message = message.build();
			this.builder = message;
		}
	}
}
