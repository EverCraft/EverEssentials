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
package fr.evercraft.essentials;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EMessage;

public class EEMessage extends EMessage {

	public EEMessage(EverEssentials plugin, String name) {
		super(plugin);
	}

	@Override
	public void loadDefault() {
		// Prefix
		addDefault("prefix", "[&4Ever&6&lEssentials&f] ");
		
		addDefault("afk.description", "Permet de vous signaler AFK.");
		addDefault("afk.allEnable", "&6" + EChat.DISPLAYNAME_FORMAT + " &7est désormais AFK.");
		addDefault("afk.allDisable", "&6" + EChat.DISPLAYNAME_FORMAT + " &7n'est plus AFK.");
		addDefault("afk.playerEnable", "&7Vous êtes désormais AFK.");
		addDefault("afk.playerDisable", "&7Vous n'êtes plus AFK.");
		addDefault("afk.playerEnableError", "&cVous êtes déjà AFK.");
		addDefault("afk.playerDisableError", "&cVous n'êtes pas AFK.");
		addDefault("afk.staffEnable", "&6<player> &7est désormais AFK.");
		addDefault("afk.staffDisable", "&6<player> &7n'est plus AFK.");
		addDefault("afk.staffEnableError", "&6<player> &cest déjà signalé AFK.");
		addDefault("afk.staffDisableError", "&6<player> &cn'est pas AFK.");
		
		addDefault("back.description", "Retourne à la dernière position sauvegardé.");
		addDefault("back.name", "&6&lposition");
		addDefault("back.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("back.teleport", "&7Vous avez été téléporté à votre dernière <back>&7.");
		addDefault("back.inconnu", "&cVous n'avez aucune position sauvegardé.");
		
		addDefault("bed.description", "Retourne à la dernière position ou vous avez dormi");
		
		addDefault("broadcast.description", "Envoye un message à tous les joueurs.");
		addDefault("broadcast.prefixPlayer", "&7[&6<player>&7] : ");
		addDefault("broadcast.prefixConsole", "&7[&6Console&7] : ");
		
		addDefault("butcher.description", "Supprime les entitées dans un monde ou dans un rayon.");
		addDefault("butcher.noEntity", "&cIl y a aucune entité à supprimer.");
		addDefault("butcher.entityColor", "&6");
		addDefault("butcher.killAnimal", "&7Suppression de &6<count> &7animaux dans ce monde.");
		addDefault("butcher.killAnimalRadius", "&7Suppression de &6<count> &7animaux dans un rayon de &6<radius> bloc(s)&7.");
		addDefault("butcher.killMonster", "&7Suppression de &6<count> &7monstre(s) dans ce monde.");
		addDefault("butcher.killMonsterRadius", "&7Suppression de &6<count> &7monstre(s) dans un rayon de &6<radius> bloc(s)&7.");
		addDefault("butcher.killAll", "&7Suppression de &6<count> &7entitée(s) dans ce monde.");
		addDefault("butcher.killAllRadius", "&7Suppression de &6<count> &7entitée(s) dans un rayon de &6<radius> bloc(s)&7.");
		addDefault("butcher.killType", "&7Suppression de &6<count> &6<entity>&6(s)&7 dans ce monde.");
		addDefault("butcher.killTypeRadius", "&7Suppression de &6<count> &6<entity>&6(s)&7 dans un rayon de &6<radius> bloc(s)&7.");
		
		addDefault("book.description", "Permet de modifier un livre.");
		
		addDefault("clearinventory.description", "Supprime tous les objets de l'inventaire d'un joueur.");
		addDefault("clearinventory.player", "&7Tous les objets de votre inventaire ont été supprimés.");
		addDefault("clearinventory.othersPlayer", "&7Tous les objets de votre inventaire ont été supprimés par &6<staff>&7.");
		addDefault("clearinventory.othersStaff", "&7Tous les objets de l'inventaire de &6<player> &7ont été supprimés.");
		
		addDefault("color.description", "Affiche les différentes couleurs dans Minecraft.");
		addDefault("color.list.title", "&l&7Liste des couleurs :"); 
		addDefault("color.list.message", "<color>█ &0: <id>-<name>"); 
		
		addDefault("effect.description", "Ajoute un effet de potion sur un joueur.");
		addDefault("effect.errorName", "&cErreur : nom de l'effet invalide.");
		addDefault("effect.errorDuration", "&cErreur : la durée ne peut excéder <time> seconde(s)");
		addDefault("effect.errorAmplifier", "&cErreur : l'amplification de l'effet doit être compris entre <min> et <max>.");
		
		addDefault("enchant.description", "Enchante l'objet dans votre main.");
		addDefault("enchant.notFound", "&cErreur : cet enchantement n'existe pas.");
		addDefault("enchant.levelTooHight", "&cErreur : le niveau de cet enchantement est trop élevé.");
		addDefault("enchant.incompatible", "&cErreur : cet enchantement est incompatible avec &6<item>");
		addDefault("enchant.name", "&6<item>");
		addDefault("enchant.successfull", "&7L'enchantement a bien été appliqué sur l'objet.");
				
		addDefault("exp.description", "Modifie l'expérience d'un joueur.");
		addDefault("exp.giveLevel", "&7Vous vous êtes ajouté &6<level> &7niveau(x).");
		addDefault("exp.giveExp", "&7Vous vous êtes ajouté &6<experience> &7point(s) d'expérience.");
		addDefault("exp.setLevel", "&7Vous avez défini votre niveau à &6<level>&7.");
		addDefault("exp.setExp", "&7Vous avez défini votre expérience à &6<experience>&7.");
		addDefault("exp.othersPlayerGiveLevel", "&7Vous avez reçu &6<level> &7niveau(x) par &6<staff>&7.");
		addDefault("exp.othersStaffGiveLevel", "&7Vous avez ajouté &6<level> &7niveau(x) à &6<player>&7.");
		addDefault("exp.othersPlayerGiveExp", "&7Vous avez reçu &6<experience> &7point(s) d'expérience par &6<staff>&7.");
		addDefault("exp.othersStaffGiveExp", "&7Vous avez ajouté &6<experience> &7point(s) d'expérience à &6<player>&7.");
		addDefault("exp.othersPlayerSetLevel", "&7Votre niveau a été modifié à &6<level> &7par &6<staff>&7.");
		addDefault("exp.othersStaffSetLevel", "&7Vous avez modifié le niveau de &6<player> &7à &6<level>&7.");
		addDefault("exp.othersPlayerSetExp", "&7Votre expérience a été modifié à &6<experience> &7par &6<staff>&7.");
		addDefault("exp.othersStaffSetExp", "&7Vous avez modifié l'expérience de &6<player> &7à &6<experience>&7.");
		
		addDefault("ext.description", "Enleve le feu sur un joueur.");
		addDefault("ext.player", "&7Vous n'êtes plus en feu.");
		addDefault("ext.playerError", "&7Vous n'êtes pas en feu.");
		addDefault("ext.othersPlayer", "&7Vous n'êtes plus en feu grâce à &6<staff>&7.");
		addDefault("ext.othersStaff", "&7Vous avez enlevé le feu sur &6<player>&7.");
		addDefault("ext.othersError", "&6<player> &7n'est pas en feu.");
		
		addDefault("feed.description", "Satisfait la faim d'un joueur.");
		addDefault("feed.player", "&7Vous vous êtes rassasié.");
		addDefault("feed.othersStaff", "&7Vous avez rassasié &6<player>.");
		addDefault("feed.othersPlayer", "&7Vous avez été rassasié par &6<staff>&7.");
		addDefault("feed.allStaff", "&7Vous avez rassasié tous les joueurs.");
		
		addDefault("fly.description", "Permet de vous envoler.");
		addDefault("fly.playerEnable", "&7Vous pouvez désormais vous envoler.");
		addDefault("fly.playerEnableError", "&7Vous possèdez déjà le droit de vous envoler.");
		addDefault("fly.playerDisable", "&7Vous ne pouvez plus vous envoler.");
		addDefault("fly.playerDisableError", "&7Vous ne pouvez pas vous envoler.");
		addDefault("fly.playerErrorCreative", "&7Vous ne pouvez pas vous enlever le droit de vous envoler quand vous êtes en mode créative.");
		addDefault("fly.othersPlayerEnable", "&7Vous pouvez désormais vous envoler grâce à &6<staff>&7.");
		addDefault("fly.othersPlayerDisable", "&7Vous ne pouvez plus vous envoler à cause de &6<staff>&7.");
		addDefault("fly.othersStaffEnable", "&7Vous venez d'accorder le droit de s'envoler à &6<player>&7.");
		addDefault("fly.othersStaffEnableError", "&6<player> &7possède déjà le droit de s'envoler.");
		addDefault("fly.othersStaffDisable", "&7Vous venez de retirer le droit de s'envoler à &6<player>&7.");
		addDefault("fly.othersStaffDisableError", "&6<player> &7ne possède pas le droit de s'envoler.");
		addDefault("fly.othersErrorCreative", "&7Vous ne pouvez pas enlever le droit de s'envoler à &6<player> &7 car il est en mode créative.");
		
		addDefault("gamemode.description", "Change le mode de jeu d'un joueur.");
		addDefault("gamemode.playerChange", "&7Vous êtes désormais en mode de jeu &6<gamemode>&7.");
		addDefault("gamemode.playerEqual", "&7Vous êtes déjà en mode de jeu &6<gamemode>&7.");
		addDefault("gamemode.othersStaffChange", "&7Mode de jeu &6<gamemode> &7pour &6<player>&7.");
		addDefault("gamemode.othersPlayerChange", "&7Votre mode de jeu a été changé en &6<gamemode> &7par &6<staff>&7.");
		addDefault("gamemode.othersEqual", "&6<player> &7possède déjà le mode de jeu &6<gamemode>&7.");
		addDefault("gamemode.errorName", "&cMode de jeu inconnu.");
		
		addDefault("getpos.description", "Affiche les coordonnées d'un joueur.");
		addDefault("getpos.message", "&7Voici votre &6<position>&7.");
		addDefault("getpos.messageOthers", "&7Voici la <position> &7de &6<player>&7.");
		addDefault("getpos.positionName", "&6&lposition");
		addDefault("getpos.positionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		
		addDefault("god.description", "Permet de vous rendre invulnérable.");
		addDefault("god.playerEnable", "&7Vous êtes désormais invulnérable.");
		addDefault("god.playerEnableError", "&7Vous êtes déjà invulnérable.");
		addDefault("god.playerDisable", "&7Vous êtes désormais vulnérable.");
		addDefault("god.playerDisableError", "&7Vous êtes déjà vulnérable.");
		addDefault("god.othersPlayerEnable", "&7Vous êtes désormais invulnérable grâce à &6<staff>&7.");
		addDefault("god.othersPlayerDisable", "&7Vous n'êtes plus invulnérable à cause de &6<staff>&7.");
		addDefault("god.othersStaffEnable", "&7Vous venez de rendre invulnérable &6<player>&7.");
		addDefault("god.othersStaffEnableError", "&6<player> &7est déjà invulnérable.");
		addDefault("god.othersStaffDisable", "&7Vous venez de rendre vulnérable &6<player>&7.");
		addDefault("god.othersStaffDisableError", "&6<player> &7est déjà vulnérable.");
		addDefault("god.teleport", "&7Vous avez été téléporté car vous étiez en train de tomber dans le vide.");
		
		addDefault("hat.description", "Place l'objet dans votre main sur votre tête");
		addDefault("hat.itemColor", "&6");
		addDefault("hat.isNotHat", "<item> &7n'est pas un chapeau.");
		addDefault("hat.noEmpty", "&7Vous ne pouvez pas mettre un objet sur votre tête quand vous avez un <item>&7.");
		addDefault("hat.isHat", "&7Votre nouveau chapeau : &6<item>&7.");
		addDefault("hat.null", "&7Vous n'avez pas d'objet dans votre main.");
		addDefault("hat.remove", "&7Vous avez enlevé l'objet sur votre chapeau.");
		addDefault("hat.removeEmpty", "&cVous n'avez actuellement aucun chapeau.");
		
		addDefault("heal.description", "Soigne un joueur.");
		addDefault("heal.player", "&7Vous vous êtes soigné.");
		addDefault("heal.playerDead", "&7Vous êtes déjà mort.");
		addDefault("heal.othersPlayer", "&7Vous avez été soigné par &6<staff>&7.");
		addDefault("heal.othersStaff", "&7Vous avez soigné &6<player>&7.");
		addDefault("heal.othersDeadStaff", "&6<player>&7 est déjà mort.");
		addDefault("heal.allStaff", "&7Vous avez soigné tous les joueurs.");
		
		addDefault("home.name", "&6&l<name>");
		addDefault("home.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		
		addDefault("home.description", "Téléporte le joueur à une résidence.");
		addDefault("home.listTitle", "&aListe des résidences");
		addDefault("home.listLine", "    &6&l➤  <home> &7: <teleport> <delete>");
		addDefault("home.listLineErrorWorld", "    &6&l➤  <home> &7: <delete>");
		addDefault("home.listTeleport", "&a&nTéléporter");
		addDefault("home.listTeleportHover", "&cCliquez ici pour vous téléporter à la résidence &6<home>&c.");
		addDefault("home.listDelete", "&c&nSupprimer");
		addDefault("home.listDeleteHover", "&cCliquez ici pour supprimer la résidence &6<home>&c.");
		addDefault("home.empty", "&cVous n'avez aucune résidence.");
		addDefault("home.inconnu", "&cVous n'avez pas de résidence qui s'appelle &6<home>&c.");
		addDefault("home.teleport", "&7Vous avez été téléporté à la résidence &6<home>&7.");
		addDefault("home.teleportError", "&cImpossible de vous téléporter à la résidence &6<home>&c.");
		
		addDefault("day.description", "Mettre le jour dans le monde");
		
		addDefault("delhome.description", "Supprime une résidence");
		addDefault("delhome.confirmation", "&7Souhaitez-vous vraiment supprimer la résidence &6<home> &7: <confirmation>");
		addDefault("delhome.confirmationValid", "&2&nConfirmer");
		addDefault("delhome.confirmationValidHover", "&cCliquez ici pour supprimer la résidence &6<home>&c.");
		addDefault("delhome.delete", "&7Vous avez supprimé la résidence &6<home>&7.");
		addDefault("delhome.inconnu", "&cVous n'avez pas de résidence qui s'appelle &6<home>&c.");
		
		addDefault("homeOthers.description", "Gère les résidences d'un joueur");
		addDefault("homeOthers.listTitle", "&aListe des résidences de <player>");
		addDefault("homeOthers.listLine", "    &6&l➤  <home> &7: <teleport> <delete>");
		addDefault("homeOthers.listTeleport", "&a&nTéléporter");
		addDefault("homeOthers.listTeleportHover", "&cCliquez ici pour vous téléporter à la résidence &6<home> &cde &6<player>&c.");
		addDefault("homeOthers.listDelete", "&c&nSupprimer");
		addDefault("homeOthers.listDeleteHover", "&cCliquez ici pour supprimer la résidence &6<home> &cde &6<player>&c.");
		addDefault("homeOthers.empty", "&6<player> &cn'a aucune résidence.");
		addDefault("homeOthers.inconnu", "&6<player> &cn'a pas de résidence qui s'appelle &6<home>&c.");
		addDefault("homeOthers.teleport", "&7Vous avez été téléporté à la résidence &6<home> &7de &6<player>&7.");
		addDefault("homeOthers.teleportError", "&cImpossible de vous téléporter à la résidence &6<home> &cde &6<player>&c.");
		addDefault("homeOthers.deleteConfirmation", "&7Souhaitez-vous vraiment supprimer la résidence &6<home> &7de &6<player> &7: <confirmation>");
		addDefault("homeOthers.deleteConfirmationValid", "&2&nConfirmer");
		addDefault("homeOthers.deleteConfirmationValidHover", "&cCliquez ici pour supprimer la résidence &6<home> &cde &6<player>&c.");
		addDefault("homeOthers.delete", "&7Vous avez supprimé la résidence &6<home> &7de &6<player>&7.");
		
		addDefault("sethome.description", "Défini une résidence");
		addDefault("sethome.set", "&7Vous avez défini votre résidence.");
		addDefault("sethome.multipleSet", "&7Vous avez défini la résidence &6<home>&7.");
		addDefault("sethome.multipleErrorMax", "&cVous ne pouvez pas créer plus de <nombre> résidence(s).");
		addDefault("sethome.multipleNoPermission", "&cVous n'avez pas la permission d'avoir plusieurs résidences.");
		
		addDefault("info.description", "Connaître le type d'un item");
		addDefault("info.player", "&7Le type de l'objet <item> &7est &6<type>&7.");
		addDefault("info.itemColor", "&6");
		
		addDefault("jump.description", "Vous téléporte à l'endroit de votre choix");
		addDefault("jump.teleport", "&7Vous avez été téléporté à l'endroit de votre choix.");
		addDefault("jump.teleportError", "&7Impossible de trouver une position pour vous téléporter.");
		
		addDefault("kick.description", "Expulse un joueur du serveur");
		addDefault("kick.message", "&c&lExpulsion du serveur[RT][RT]&cRaison : &7<message>[RT]");
		
		addDefault("kickall.description", "Expulse tous les joueurs du serveur");
		addDefault("kickall.message", "&c&lExpulsion du serveur[RT][RT]&cRaison : &7<message>[RT]");
		
		addDefault("kill.description", "Tue un joueur");
		addDefault("kill.player", "&7Vous avez été tué par &6<staff>&7.");
		addDefault("kill.staff", "&7Vous avez tué &6<player>&7.");
		
		addDefault("lag.description", "Connaître l'état du serveur");
		addDefault("lag.title", "&aInformations sur le serveur");
		addDefault("lag.time", "    &6&l➤  &6Durée de fonctionnement : &c<time>");
		addDefault("lag.tps", "    &6&l➤  &6TPS actuel : &c<tps>");
		addDefault("lag.historyTps", "    &6&l➤  &6Historique TPS : <tps>");
		addDefault("lag.historyTpsHover", "&6Minute : &c<num>[RT]&6TPS : &c<tps>");
		addDefault("lag.memory", "    &6&l➤  &6RAM : &c<usage>&6/&c<total> &6Mo");
		addDefault("lag.worlds", "    &6&l➤  &6Liste des mondes :");
		addDefault("lag.worldsLine", "        &6&l●  &6<world>");
		addDefault("lag.worldsLineHover", "&6Chunks : &c<chunks>[RT]&6Entités : &c<entities>[RT]&6Tiles : &c<tiles>");
		
		addDefault("list.description", "Affiche la liste des joueurs connecté");
		addDefault("list.title", "&aListe des joueurs connectés : &6<PLAYERS_NO_VANISH> &a/ &6<MAX_PLAYERS>");
		addDefault("list.titleVanish", "&aListe des joueurs connectés : &6<PLAYERS_NO_VANISH> &a(+&6<vanish>&a) / &6<MAX_PLAYERS>");
		addDefault("list.group", "&6<group>&f : <players>");
		addDefault("list.separator", ", ");
		addDefault("list.player", "<afk>&r<vanish>&r<DISPLAYNAME_FORMAT>");
		addDefault("list.tagAFK", "&7[AFK] ");
		addDefault("list.tagVanish", "&7[HIDDEN]", "&7[VANISH] ");
		addDefault("list.empty", "&7Aucun joueur");
		
		addDefault("mail.description", "Gestion de vos messages");
		addDefault("mail.readTitle", "&aLa liste des messages");
		addDefault("mail.readLineRead", "  &a&l➤&7 De &6<player>&7 le &6<date> &7à &6<time> : <read> <delete>");
		addDefault("mail.readLineNoRead", "  &6&l➤&7 De &6<player>&7 le &6<date> &7à &6<time> : <read> <delete>");
		addDefault("mail.readEmpty", "&7Vous n'avez aucun message");
		addDefault("mail.readError", "&cVous n'avez pas de message qui correspond.");
		addDefault("mail.delete", "&7Voulez-vous vraiment supprimer le <mail> de &6<player>&7 le &6<date> &7à &6<time> : <confirmation>.");
		addDefault("mail.deleteValid", "&a&nConfirmer");
		addDefault("mail.deleteValidHover", "&cCliquez ici pour supprimer le message.");
		addDefault("mail.deleteConfirmation", "&7Le <mail> &7a bien été supprimé.");
		addDefault("mail.deleteMail", "&6message");
		addDefault("mail.deleteMailHover", "&7De &6<player>[RT]&7Le &6<date>");
		addDefault("mail.deleteError", "&cVous n'avez pas de message qui correspond.");
		addDefault("mail.clear", "&7Vous avez supprimé tous vos messages.");
		addDefault("mail.clearError", "&cVous n'avez pas de message à supprimer.");
		addDefault("mail.send", "&7Votre message a bien été envoyé à &6<player>&7.");
		addDefault("mail.sendEquals", "&7Votre message vous a bien été envoyé.");
		addDefault("mail.sendAll", "&7Votre message a bien été envoyé à tous les joueurs.");
		addDefault("mail.buttomRead", "&a&nLire");
		addDefault("mail.buttomReadHover", "&cCliquez ici pour lire le message.");
		addDefault("mail.buttonDelete", "&c&nSupprimer");
		addDefault("mail.buttonDeleteHover", "&cCliquez ici pour supprimer le message.");
		
		addDefault("me.description", "Envoie un texte d'action dans le tchat");
		addDefault("me.prefix", "&f* <player> &r");
		
		addDefault("mojang.description", "Affiche les informations sur les serveurs de mojang");
		addDefault("mojang.title", "&aLes serveurs de Mojang");
		addDefault("mojang.line", "&7<server> : <color>");
		addDefault("mojang.serverAccount", "Account");
		addDefault("mojang.serverAPI", "API");
		addDefault("mojang.serverMojang", "Mojang");
		addDefault("mojang.serverAuth", "Auth");
		addDefault("mojang.serverAuthServer", "AuthServer");
		addDefault("mojang.serverMinecraftNet", "Minecraft.net");
		addDefault("mojang.serverSession", "Session");
		addDefault("mojang.serverSessionServer", "SessionServer");
		addDefault("mojang.serverSkins", "Skins");
		addDefault("mojang.serverTextures", "Textures");
		addDefault("mojang.colorGreen", "&aEn ligne");
		addDefault("mojang.colorYellow", "&6Problème de connexion");
		addDefault("mojang.colorRed", "&4Hors ligne");
		
		addDefault("more.description", "Donne la quantité maximum d'un objet");
		addDefault("more.player", "&7Vous avez maintenant &6<quantity> &6<item>&7.");
		addDefault("more.itemColor", "&6");
		addDefault("more.maxQuantity", "&7Vous avez déjà la quantité maximum de cette objet.");
		
		addDefault("motd.description", "Affiche le message du jour.");
		
		addDefault("names.description", "Affiche l'historique des noms d'un joueur");
		addDefault("names.playerTitle", "&aVotre historique de nom");
		addDefault("names.playerLineOriginal", "    &6&l➤  &6<name> &7: &cAchat du compte");
		addDefault("names.playerLineOthers", "    &6&l➤  &6<name> &7: &c<datetime>");
		addDefault("names.playerEmpty", "&7Vous n'avez aucun historique de pseudo");
		addDefault("names.othersTitle", "&aHistorique de &6<player>");
		addDefault("names.othersLineOriginal", "    &6&l➤  &6<name> &7: &cAchat du compte");
		addDefault("names.othersLineOthers", "    &6&l➤  &6<name> &7: &c<datetime>");
		addDefault("names.othersEmpty", "&6<player> &7n'a aucun historique de pseudo");
		
		addDefault("near.description", "Donne la liste des joueurs dans les environs");
		addDefault("near.list.title", "&aListe des joueurs dans les environs");
		addDefault("near.list.line", "    &6&l➤  &6<player> &7: &6<distance> bloc(s)");
		addDefault("near.noPlayer", "&cAucun joueur dans les environs.");
		
		addDefault("opme.description", "Deviens opérateur");
		
		addDefault("ping.description", "Connaître la latence d'un joueur");
		addDefault("ping.player", "&7Votre ping : &6<ping> &7ms.");
		addDefault("ping.others", "&7Le ping de &6<player> &7: &6<ping> &7ms.");
		
		addDefault("invsee.description", "Regarde l'inventaire d'un autre joueur");
		
		addDefault("repair.description", "Répare les objets");
		
		addDefault("repairhand.description", "Répare l'objet dans votre main");
		addDefault("repairhand.itemColor", "&6");
		addDefault("repairhand.player", "&7Vous venez de réparer l'objet <item>&7.");
		addDefault("repairhand.error", "&7Vous ne pouvez pas réparer <item>&7.");
		addDefault("repairhand.maxDurability", "&6<item> &7est déjà réparé.");
		
		addDefault("repairhotbar.description", "Répare les objets dans votre barre d'action");
		addDefault("repairhotbar.player", "&7Vous venez de réparer tous les objets de votre barre d'action.");
		
		addDefault("repairall.description", "Répare tous vos objets");
		addDefault("repairall.player", "&7Vous venez de réparer tous les objets de votre inventaire.");
		
		addDefault("rules.description", "&7Affiche les règles d'Evercraft.");
		
		addDefault("skull.description", "Donne la tête d'un joueur");
		addDefault("skull.myHead", "&7Vous avez reçu votre tête.");
		addDefault("skull.others", "&7Vous avez reçu la tête de &6<player>&7.");
		
		addDefault("spawner.description", "Permet de modifier le type d'un mob spawner");
		
		addDefault("spawnmob.description", "Fait apparaître une entité");
		addDefault("spawnmob.errorMob", "&cErreur : nom invalide.");
		
		addDefault("speed.description", "Change la vitesse de déplacement");
		addDefault("speed.infoWalk", "&7Votre vitesse de &6marche &7est de &6<speed>&7.");
		addDefault("speed.infoFly", "&7Votre vitesse de &6vol &7est de &6<speed>&7.");
		addDefault("speed.playerWalk", "&7Vous avez défini votre vitesse de &6marche &7à &6<speed>&7.");
		addDefault("speed.playerFly", "&7Vous avez défini votre vitesse de &6vol &7à &6<speed>&7.");
		addDefault("speed.othersPlayerWalk", "&7Votre vitesse de marche a été défini à &6<speed> &7par &6<staff>&7.");
		addDefault("speed.othersStaffWalk", "&7Vous avez défini la vitesse de &6marche &7de &6<player> &7à &6<speed>&7.");
		addDefault("speed.othersPlayerFly", "&7Votre vitesse de vol a été défini à &6<speed> &7par &6<staff>&7.");
		addDefault("speed.othersStaffFly", "&7Vous avez défini la vitesse de &6vol &7de &6<player> &7à &6<speed>&7.");
		
		addDefault("stop.description", "Arrête le serveur");
		addDefault("stop.message", "&cArrêt du serveur par &6<staff>");
		addDefault("stop.messageReason", "&c<reason>");
		addDefault("stop.consoleMessage", "&cArrêt du serveur");
		addDefault("stop.consoleMessageReason", "&c<reason>");
		
		addDefault("sudo.description", "Fait exécuter une commande par un autre joueur");
		addDefault("sudo.command", "&6commande");
		addDefault("sudo.commandHover", "&c<command>");
		addDefault("sudo.player", "&7Votre <command> &7a bien était éxecutée par &6<player>&7.");
		addDefault("sudo.bypass", "&cVous ne pouvez pas faire exécuter de commande à &6<player>&7.");
		addDefault("sudo.console", "&7Votre <command> &7à bien était éxecutée par la &6console&7.");
		
		addDefault("suicide.description", "Permet de vous suicider");
		
		addDefault("tp.description", "Téléporte le joueur vers un autre joueur");
		addDefault("tp.destination", "&6&l<player>");
		addDefault("tp.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("tp.player", "&7Vous avez été téléporté vers &6<destination>&7.");
		addDefault("tp.playerEquals", "&7Vous avez été repositionné.");
		addDefault("tp.othersPlayer", "&6<staff> &7vous a téléporté vers &6<destination>.");
		addDefault("tp.othersStaff", "&6<player> &7a été téléporté vers &6<destination>&7.");
		addDefault("tp.othersPlayerReposition", "&6<staff> &7vient de vous repositionner.");
		addDefault("tp.othersStaffReposition", "&7Vous venez de repositionner &6<player>&7.");
		addDefault("tp.othersStaffEqualsDestinationPlayer", "&6<destination> &7vous a téléporté.");
		addDefault("tp.othersStaffEqualsDestinationStaff", "&7Vous venez de téléporter &6<player>&7.");
		addDefault("tp.errorLocation", "&cImpossible de trouver une position pour réaliser une téléportation.");
		
		addDefault("tpall.description", "Téléporte tous les joueurs vers un autre joueur");
		addDefault("tpall.destination", "&6&l<player>");
		addDefault("tpall.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("tpall.player", "&6<destination> &7vous a téléporté.");
		addDefault("tpall.staff", "&7Vous venez de téléporter tous les joueurs.");
		addDefault("tpall.error", "&cImpossible de trouver une position pour téléporter les joueurs.");
		addDefault("tpall.othersPlayer", "&6<staff> &7vous a téléporté vers &6<destination>.");
		addDefault("tpall.othersStaff", "&7Tous les joueurs ont été téléportés vers &6<destination>&7.");
		
		addDefault("tphere.description", "Téléporte le joueur vers vous");
		addDefault("tphere.destination", "&6&l<player>");
		addDefault("tphere.destinationHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("tphere.player", "&6<destination> &7vous a téléporté.");
		addDefault("tphere.staff", "&7Vous venez de téléporter &6<player>&7.");
		addDefault("tphere.equals", "&7Vous avez été repositionné.");
		addDefault("tphere.error", "&cImpossible de trouver une position pour téléporter le joueur.");
		
		addDefault("tppos.description", "Téléporte le joueur aux coordonnées choisis");
		addDefault("tppos.position", "&6&lposition");
		addDefault("tppos.positionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("tppos.player", "&7Vous avez été téléporté à cette <position>&7.");
		addDefault("tppos.playerError", "&7Impossible de vous téléporter à cette <position>&7.");
		addDefault("tppos.othersPlayer", "&7Vous avez été téléporté à cette <position> &7par &6<staff>&7.");
		addDefault("tppos.othersStaff", "&7Vous téléportez &6<player> &7à cette <position>&7.");
		addDefault("tppos.othersError", "&7Impossible de téléporter &6<player> &7à cette <position>&7.");
		
		addDefault("time.description", "Gère l'heure sur les mondes");
		addDefault("time.format", "&6<hours>h<minutes>");
		addDefault("time.information", "&7Il est actuellement &6<hours> &7dans le monde &6<world>&7.");
		addDefault("time.setWorld", "&7Il est désormais &6<hours> &7dans le monde &6<world>&7.");
		addDefault("time.setAllWorld", "&7Il est désormais &6<hours> &7dans les mondes&7.");
		addDefault("time.error", "&cErreur : Horaire incorrect.");
		
		addDefault("time.dayDescription", "Mettre le jour dans votre monde");
		addDefault("time.nightDescription", "Mettre la nuit dans votre monde");
		
		addDefault("top.description", "Téléporte le joueur à la position la plus élevée");
		addDefault("top.position", "&6&lposition");
		addDefault("top.positionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("top.teleport", "&7Vous avez été téléporté à la <position> &7la plus élevée.");
		addDefault("top.teleportError", "&cImpossible de trouver une position où vous téléporter.");
		
		addDefault("tree.description", "Place un arbre");
		addDefault("tree.inconnu", "&cType d'arbre inconnu : &6<type>");
		addDefault("tree.noCan", "&cImpossible de placer un arbre à cette endroit : Regarder plutot un bloc d'herbre ou de terre.");
		
		addDefault("uuid.description", "Affiche l'identifiant unique du joueur.");
		addDefault("uuid.name", "&6&l<uuid>");
		addDefault("uuid.player", "&7Votre UUID est <uuid>");
		addDefault("uuid.otherPlayer", "L'UUID de <player> est <uuid>");
		
		addDefault("vanish.description", "Permet de vous rendre invisible.");
		addDefault("vanish.playerEnable", "&7Vous êtes désormais invisible.");
		addDefault("vanish.playerEnableError", "&7Vous êtes déjà invisible.");
		addDefault("vanish.playerDisable", "&7Vous n'êtes plus invisible.");
		addDefault("vanish.playerDisableError", "&7Vous êtes déjà visible.");
		addDefault("vanish.othersPlayerEnable", "&7Vous êtes désormais invisible grâce à &6<staff>&7.");
		addDefault("vanish.othersPlayerDisable", "&7Vous n'êtes plus invisible à cause de &6<staff>&7.");
		addDefault("vanish.othersStaffEnable", "&7Vous venez de rendre invisible &6<player>&7.");
		addDefault("vanish.othersStaffEnableError", "&6<player> &7est déjà invisible.");
		addDefault("vanish.othersStaffDisable", "&7Vous venez de rendre visible &6<player>&7.");
		addDefault("vanish.othersStaffDisableError", "&6<player> &7est déjà visible.");
		
		addDefault("warp.description", "Se téléporte à un warp");
		addDefault("warp.name", "&6&l<name>");
		addDefault("warp.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("warp.inconnu", "&cIl n'y pas de warp qui s'appelle &6<warp>&c.");
		addDefault("warp.noPermission", "&cVous n'avez pas la permission pour vous téléporter au warp &6<warp>&c.");
		addDefault("warp.empty", "&cIl n'y a aucun warp sur le serveur.");
		addDefault("warp.listTitle", "&aListe des warps");
		addDefault("warp.listLineDelete", "    &6&l➤  <warp> &7: <teleport> <delete>");
		addDefault("warp.listLineDeleteErrorWorld", "    &6&l➤  <warp> &7: <delete>");
		addDefault("warp.listLine", "    &6&l➤  <warp> &7: <teleport>");
		addDefault("warp.listTeleport", "&a&nTéléporter");
		addDefault("warp.listTeleportHover", "&cCliquez ici pour vous téléporter à le warp &6<warp>&c.");
		addDefault("warp.listDelete", "&c&nSupprimer");
		addDefault("warp.listDeleteHover", "&cCliquez ici pour supprimer le warp &6<warp>&c.");
		addDefault("warp.teleportPlayer", "&7Vous avez été téléporté au warp &6<warp>&7.");
		addDefault("warp.teleportPlayerError", "&cImpossible de vous téléporter au warp &6<warp>&c.");
		addDefault("warp.teleportOthersPlayer", "&7Vous avez été téléporté au warp &6<warp> &7par &6<player>&7.");
		addDefault("warp.teleportOthersStaff", "&7Vous avez téléporté &6<player> &7au warp &6<warp>&7.");
		addDefault("warp.teleportOthersError", "&cImpossible de téléporter &6<player> &7au warp &6<warp>&c.");
		
		addDefault("delwarp.description", "Supprime un warp");
		addDefault("delwarp.inconnu", "&cIl n'y pas de warp qui s'appelle &6<warp>&c.");
		addDefault("delwarp.name", "&6&l<name>");
		addDefault("delwarp.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("delwarp.confirmation", "&7Souhaitez-vous vraiment supprimer le warp &6<warp> &7: <confirmation>");
		addDefault("delwarp.confirmationValid", "&2&nConfirmer");
		addDefault("delwarp.confirmationValidHover", "&cCliquez ici pour supprimer le warp &6<warp>&c.");
		addDefault("delwarp.delete", "&7Vous avez supprimé le warp &6<warp>&7.");
		
		addDefault("setwarp.description", "Crée un warp");
		addDefault("setwarp.name", "&6&l<name>");
		addDefault("setwarp.nameHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("setwarp.replace", "&7Vous avez redéfini le warp &6<warp>&7.");
		addDefault("setwarp.new", "&7Vous avez défini le warp &6<warp>&7.");
		
		addDefault("weather.description", "Change la météo d'un monde");
		addDefault("weather.error", "&cVous ne pouvez pas changer la météo dans ce type de monde.");
		addDefault("weather.sun", "&7Vous avez mis &6le beau temps &7dans le monde &6<world>&7.");
		addDefault("weather.rain", "&7Vous avez mis &6la pluie &7dans le monde &6<world>&7.");
		addDefault("weather.storm", "&7Vous avez mis &6la tempête &7dans le monde &6<world>&7.");
		addDefault("weather.sunDuration", "&7Vous avez mis &6le beau temps &7dans le monde &6<world>&7 pendant &6<duration>&7 minute(s).");
		addDefault("weather.rainDuration", "&7Vous avez mis &6la pluie &7dans le monde &6<world>&7 pendant &6<duration>&7 minute(s).");
		addDefault("weather.stormDuration", "&7Vous avez mis &6la tempête &7dans le monde &6<world>&7 pendant &6<duration>&7 minute(s).");
		
		addDefault("weather.rainDescription", "Met la pluie dans votre monde");
		addDefault("weather.stormDescription", "Met la tempête dans votre monde");
		addDefault("weather.sunDescription", "Met le beau dans temps dans votre monde");
		
		addDefault("whois.description", "Affiche les informations d'un joueur");
		addDefault("whois.title", "&aInformations : &c<player>");
		addDefault("whois.uuid", "    &6&l➤  &6UUID : <uuid>");
		addDefault("whois.uuidStyle", "&c<uuid>");
		addDefault("whois.ip", "    &6&l➤  &6IP : <ip>");
		addDefault("whois.ipStyle", "&c<ip>");
		addDefault("whois.ping", "    &6&l➤  &6Ping : &c<ping> &6ms");
		addDefault("whois.heal", "    &6&l➤  &6Santé : &a<heal>&6/&c<max_heal>");
		addDefault("whois.food", "    &6&l➤  &6Faim : &a<food>&6/&c<max_food>");
		addDefault("whois.foodSaturation", "    &6&l➤  &6Faim : &a<food>&6/&c<max_food> &6(+&a<saturation> &6saturation)");
		addDefault("whois.exp", "    &6&l➤  &6Expérience :");
		addDefault("whois.expLevel", "        &6&l●  &a<level> &6niveau(x)");
		addDefault("whois.expPoint", "        &6&l●  &a<point> &6point(s)");
		addDefault("whois.speed", "    &6&l➤  &6Vitesse :");
		addDefault("whois.speedFly", "        &6&l●  &6En volant : &a<speed>");
		addDefault("whois.speedWalk", "        &6&l●  &6En marchant : &a<speed>");
		addDefault("whois.location", "    &6&l➤  &6Position : <position>");
		addDefault("whois.locationPosition", "&6(&c<x>&6, &c<y>&6, &c<z>&6, &c<world>&6)");
		addDefault("whois.locationPositionHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("whois.balance", "    &6&l➤  &6Solde : &c<money>");
		addDefault("whois.gamemode", "    &6&l➤  &6Mode de jeu : &c<gamemode>");
		addDefault("whois.godEnable", "    &6&l➤  &6Mode Dieu : &aActivé");
		addDefault("whois.godDisable", "    &6&l➤  &6Mode Dieu : &cDésactivé");
		addDefault("whois.flyEnableFly", "    &6&l➤  &6Fly Mode : &aActivé &6(&avol&6)");
		addDefault("whois.flyEnableWalk", "    &6&l➤  &6Fly Mode : &aActivé &6(&cmarche&6)");
		addDefault("whois.flyDisable", "    &6&l➤  &6Fly Mode : &cDésactivé");
		addDefault("whois.muteEnable", "    &6&l➤  &6Muet : &aActivé");
		addDefault("whois.muteDisable", "    &6&l➤  &6Muet : &cDésactivé");
		addDefault("whois.vanishEnable", "    &6&l➤  &6Vanish : &aActivé");
		addDefault("whois.vanishDisable", "    &6&l➤  &6Vanish : &cDésactivé");
		addDefault("whois.afkEnable", "    &6&l➤  &6AFK : &aActivé");
		addDefault("whois.afkDisable", "    &6&l➤  &6AFK : &cDésactivé");
		addDefault("whois.firstDatePlayed", "    &6&l➤  &6Première connexion : &a<time>");
		addDefault("whois.lastDatePlayed", "    &6&l➤  &6Connecté depuis : &a<time>");
		
		addDefault("worlds.description", "Téléporte le joueur dans le monde de votre choix");
		addDefault("worlds.endDescription", "Vous téléporte dans le monde du néant");
		addDefault("worlds.netherDescription", "Vous téléporte dans le monde de l'enfer");
		addDefault("worlds.listTitle", "&aListe des mondes");
		addDefault("worlds.listLine", "    &6&l➤  <world> &7: <teleport>");
		addDefault("worlds.listTeleport", "&2&nTéléporter");
		addDefault("worlds.listTeleportHover", "&cCliquez ici pour vous téléporter dans le monde &6<world>&c.");
		addDefault("worlds.teleportWorld", "&6&l<world>");
		addDefault("worlds.teleportWorldHover", "&cMonde : &6<world>[RT]&cX : &6<x>[RT]&cY : &6<y>[RT]&cZ : &6<z>");
		addDefault("worlds.teleportPlayer", "&7Vous avez été téléporté dans le monde &6<world>&7.");
		addDefault("worlds.teleportPlayerError", "&7Impossible de vous téléporter dans le monde <world>&7.");
		addDefault("worlds.teleportOthersPlayer", "&7Vous avez été téléporté dans le monde <world> &7par &6<staff>&7.");
		addDefault("worlds.teleportOthersStaff", "&7Vous téléportez &6<player> &7dans le monde <world>&7.");
		addDefault("worlds.teleportOthersError", "&7Impossible de téléporter &6<player> &7dans le monde&7.");
	}

	@Override
	public void loadConfig() {
		// Prefix
		addMessage("PREFIX", "prefix");
		
		addMessage("AFK_DESCRIPTION", "afk.description");
		addMessage("AFK_ALL_ENABLE", "afk.allEnable");
		addMessage("AFK_ALL_DISABLE", "afk.allDisable");
		addMessage("AFK_PLAYER_ENABLE", "afk.playerEnable");
		addMessage("AFK_PLAYER_DISABLE", "afk.playerDisable");
		addMessage("AFK_PLAYER_ENABLE_ERROR", "afk.playerEnableError");
		addMessage("AFK_PLAYER_DISABLE", "afk.playerDisableError");
		addMessage("AFK_STAFF_ENABLE", "afk.staffEnable");
		addMessage("AFK_STAFF_DISABLE", "afk.staffDisable");
		addMessage("AFK_STAFF_ENABLE_ERROR", "afk.staffEnableError");
		addMessage("AFK_STAFF_DISABLE_ERROR", "afk.staffDisableError");
		
		addMessage("BACK_DESCRIPTION", "back.description");
		addMessage("BACK_NAME", "back.name");
		addMessage("BACK_NAME_HOVER", "back.nameHover");
		addMessage("BACK_TELEPORT", "back.teleport");
		addMessage("BACK_INCONNU", "back.inconnu");
		
		addMessage("BED_DESCRIPTION", "bed.description");
		
		addMessage("BROADCAST_DESCRIPTION", "broadcast.description");
		addMessage("BROADCAST_PREFIX_PLAYER", "broadcast.prefixPlayer");
		addMessage("BROADCAST_PREFIX_CONSOLE", "broadcast.prefixConsole");
		
		addMessage("BOOK_DESCRIPTION", "book.description");
		
		addMessage("BUTCHER_DESCRIPTION", "butcher.description");
		addMessage("BUTCHER_NOENTITY", "butcher.noEntity");
		addMessage("BUTCHER_ENTITY_COLOR", "butcher.entityColor");
		addMessage("BUTCHER_ANIMAL", "butcher.killAnimal");
		addMessage("BUTCHER_ANIMAL_RADIUS", "butcher.killAnimalRadius");
		addMessage("BUTCHER_MONSTER", "butcher.killMonster");
		addMessage("BUTCHER_MONSTER_RADIUS", "butcher.killMonsterRadius");
		addMessage("BUTCHER_ALL", "butcher.killAll");
		addMessage("BUTCHER_ALL_RADIUS", "butcher.killAllRadius");
		addMessage("BUTCHER_TYPE", "butcher.killType");
		addMessage("BUTCHER_TYPE_RADIUS", "butcher.killTypeRadius");		
		
		addMessage("CLEARINVENTORY_DESCRIPTION", "clearinventory.description");
		addMessage("CLEARINVENTORY_PLAYER", "clearinventory.player");
		addMessage("CLEARINVENTORY_OTHERS_PLAYER", "clearinventory.othersPlayer");
		addMessage("CLEARINVENTORY_OTHERS_STAFF", "clearinventory.othersStaff");
		
		addMessage("COLOR_DESCRIPTION", "color.description");
		addMessage("COLOR_LIST_MESSAGE", "color.list.message");
		addMessage("COLOR_LIST_TITLE", "color.list.title");
		
		addMessage("DAY_DESCRIPTION", "day.description");
		
		addMessage("EFFECT_DESCRIPTION", "effect.description");
		addMessage("EFFECT_ERROR_NAME", "effect.errorName");
		addMessage("EFFECT_ERROR_DURATION", "effect.errorDuration");
		addMessage("EFFECT_ERROR_AMPLIFIER", "effect.errorAmplifier");
		
		addMessage("ENCHANT_DESCRIPTION", "enchant.description");
		addMessage("ENCHANT_NOT_FOUND", "enchant.notFound");
		addMessage("ENCHANT_LEVEL_TOO_HIGHT", "enchant.levelTooHight");
		addMessage("ENCHANT_INCOMPATIBLE", "enchant.incompatible");
		addMessage("ENCHANT_NAME", "enchant.name");
		addMessage("ENCHANT_SUCCESSFULL", "enchant.successfull");
		
		addMessage("EXP_DESCRIPTION", "exp.description");
		addMessage("EXP_GIVE_LEVEL", "exp.giveLevel");
		addMessage("EXP_GIVE_EXP", "exp.giveExp");
		addMessage("EXP_SET_LEVEL", "exp.setLevel");
		addMessage("EXP_SET_EXP", "exp.setExp");
		addMessage("EXP_OTHERS_PLAYER_GIVE_LEVEL", "exp.othersPlayerGiveLevel");
		addMessage("EXP_OTHERS_STAFF_GIVE_LEVEL", "exp.othersStaffGiveLevel");
		addMessage("EXP_OTHERS_PLAYER_GIVE_EXP", "exp.othersPlayerGiveExp");
		addMessage("EXP_OTHERS_STAFF_GIVE_EXP", "exp.othersStaffGiveExp");
		addMessage("EXP_OTHERS_PLAYER_SET_LEVEL", "exp.othersPlayerSetLevel");
		addMessage("EXP_OTHERS_STAFF_SET_LEVEL", "exp.othersStaffSetLevel");
		addMessage("EXP_OTHERS_PLAYER_SET_EXP", "exp.othersPlayerSetExp");
		addMessage("EXP_OTHERS_STAFF_SET_EXP", "exp.othersStaffSetExp");
		
		addMessage("EXT_DESCRIPTION", "ext.description");
		addMessage("EXT_PLAYER", "ext.player");
		addMessage("EXT_PLAYER_ERROR", "ext.playerError");
		addMessage("EXT_OTHERS_PLAYER", "ext.othersPlayer");
		addMessage("EXT_OTHERS_STAFF", "ext.othersStaff");
		addMessage("EXT_OTHERS_ERROR", "ext.othersError");
		
		addMessage("FEED_DESCRIPTION", "feed.description");
		addMessage("FEED_PLAYER", "feed.player");
		addMessage("FEED_OTHERS_STAFF", "feed.othersStaff");
		addMessage("FEED_OTHERS_PLAYER", "feed.othersPlayer");
		addMessage("FEED_ALL_STAFF", "feed.allStaff");
		
		addMessage("FLY_DESCRIPTION", "fly.description");
		addMessage("FLY_PLAYER_ENABLE", "fly.playerEnable");
		addMessage("FLY_PLAYER_ENABLE_ERROR", "fly.playerEnableError");
		addMessage("FLY_PLAYER_DISABLE", "fly.playerDisable");
		addMessage("FLY_PLAYER_DISABLE_ERROR", "fly.playerDisableError");
		addMessage("FLY_PLAYER_ERROR_CREATIVE", "fly.playerErrorCreative");
		addMessage("FLY_OTHERS_PLAYER_ENABLE", "fly.othersPlayerEnable");
		addMessage("FLY_OTHERS_PLAYER_DISABLE", "fly.othersPlayerDisable");
		addMessage("FLY_OTHERS_STAFF_ENABLE", "fly.othersStaffEnable");
		addMessage("FLY_OTHERS_STAFF_ENABLE_ERROR", "fly.othersStaffEnableError");
		addMessage("FLY_OTHERS_STAFF_DISABLE", "fly.othersStaffDisable");
		addMessage("FLY_OTHERS_STAFF_DISABLE_ERROR", "fly.othersStaffDisableError");
		addMessage("FLY_OTHERS_ERROR_CREATIVE", "fly.othersStaffErrorCreative");
		
		addMessage("GAMEMODE_DESCRIPTION", "gamemode.description");
		addMessage("GAMEMODE_PLAYER_CHANGE", "gamemode.playerChange");
		addMessage("GAMEMODE_PLAYER_EQUAL", "gamemode.playerEqual");
		addMessage("GAMEMODE_OTHERS_STAFF_CHANGE", "gamemode.othersStaffChange");
		addMessage("GAMEMODE_OTHERS_PLAYER_CHANGE", "gamemode.othersPlayerChange");
		addMessage("GAMEMODE_OTHERS_EQUAL", "gamemode.othersEqual");
		addMessage("GAMEMODE_ERROR_NAME", "gamemode.errorName");
		
		addMessage("GETPOS_DESCRIPTION", "getpos.description");
		addMessage("GETPOS_MESSAGE", "getpos.message");
		addMessage("GETPOS_MESSAGE_OTHERS", "getpos.messageOthers");
		addMessage("GETPOS_POTISITON_NAME", "getpos.positionName");
		addMessage("GETPOS_POSITION_HOVER", "getpos.positionHover");
		
		addMessage("GOD_DESCRIPTION", "god.description");
		addMessage("GOD_PLAYER_ENABLE", "god.playerEnable");
		addMessage("GOD_PLAYER_ENABLE_ERROR", "god.playerEnableError");
		addMessage("GOD_PLAYER_DISABLE", "god.playerDisable");
		addMessage("GOD_PLAYER_DISABLE_ERROR", "god.playerDisableError");
		addMessage("GOD_OTHERS_PLAYER_ENABLE", "god.othersPlayerEnable");
		addMessage("GOD_OTHERS_PLAYER_DISABLE", "god.othersPlayerDisable");
		addMessage("GOD_OTHERS_STAFF_ENABLE", "god.othersStaffEnable");
		addMessage("GOD_OTHERS_STAFF_ENABLE_ERROR", "god.othersStaffEnableError");
		addMessage("GOD_OTHERS_STAFF_DISABLE", "god.othersStaffDisable");
		addMessage("GOD_OTHERS_STAFF_DISABLE_ERROR", "god.othersStaffDisableError");
		addMessage("GOD_TELEPORT", "god.teleport");
		
		addMessage("HAT_DESCRIPTION", "hat.description");
		addMessage("HAT_ITEM_COLOR", "hat.itemColor");
		addMessage("HAT_IS_NOT_HAT", "hat.isNotHat");
		addMessage("HAT_NO_EMPTY", "hat.noEmpty");
		addMessage("HAT_IS_HAT", "hat.isHat");
		addMessage("HAT_NULL", "hat.null");
		addMessage("HAT_REMOVE", "hat.remove");
		addMessage("HAT_REMOVE_EMPTY", "hat.removeEmpty");
		
		addMessage("HEAL_DESCRIPTION", "heal.description");
		addMessage("HEAL_PLAYER", "heal.player");
		addMessage("HEAL_PLAYER_DEAD", "heal.playerDead");
		addMessage("HEAL_OTHERS_PLAYER", "heal.othersPlayer");
		addMessage("HEAL_OTHERS_STAFF", "heal.othersStaff");
		addMessage("HEAL_OTHERS_DEAD_STAFF", "heal.othersDeadStaff");
		addMessage("HEAL_ALL_STAFF", "heal.allStaff");

		addMessage("HOME_NAME", "home.name");
		addMessage("HOME_NAME_HOVER", "home.nameHover");
		
		addMessage("HOME_DESCRIPTION", "home.description");
		addMessage("HOME_LIST_TITLE", "home.listTitle");
		addMessage("HOME_LIST_LINE", "home.listLine");
		addMessage("HOME_LIST_TELEPORT", "home.listTeleport");
		addMessage("HOME_LIST_TELEPORT_HOVER", "home.listTeleportHover");
		addMessage("HOME_LIST_DELETE", "home.listDelete");
		addMessage("HOME_LIST_DELETE_HOVER", "home.listDeleteHover");
		addMessage("HOME_EMPTY", "home.empty");
		addMessage("HOME_INCONNU", "home.inconnu");
		addMessage("HOME_TELEPORT", "home.teleport");
		addMessage("HOME_TELEPORT_ERROR", "home.teleportError");
		
		addMessage("HOMEOTHERS_DESCRIPTION", "homeOthers.description");
		addMessage("HOMEOTHERS_LIST_TITLE", "homeOthers.listTitle");
		addMessage("HOMEOTHERS_LIST_LINE", "homeOthers.listLine");
		addMessage("HOMEOTHERS_LIST_LINE_ERROR_WORLD", "homeOthers.listLineErrorWorld");
		addMessage("HOMEOTHERS_LIST_TELEPORT", "homeOthers.listTeleport");
		addMessage("HOMEOTHERS_LIST_TELEPORT_HOVER", "homeOthers.listTeleportHover");
		addMessage("HOMEOTHERS_LIST_DELETE", "homeOthers.listDelete");
		addMessage("HOMEOTHERS_LIST_DELETE_HOVER", "homeOthers.listDeleteHover");
		addMessage("HOMEOTHERS_EMPTY", "homeOthers.empty");
		addMessage("HOMEOTHERS_INCONNU", "homeOthers.inconnu");
		addMessage("HOMEOTHERS_TELEPORT", "homeOthers.teleport");
		addMessage("HOMEOTHERS_TELEPORT_ERROR", "homeOthers.teleportError");
		addMessage("HOMEOTHERS_DELETE_CONFIRMATION", "homeOthers.deleteConfirmation");
		addMessage("HOMEOTHERS_DELETEE_CONFIRMATION_VALID", "homeOthers.deleteConfirmationValid");
		addMessage("HOMEOTHERS_DELETE_CONFIRMATION_VALID_HOVER", "homeOthers.deleteConfirmationValidHover");
		addMessage("HOMEOTHERS_DELETE", "homeOthers.delete");
		
		addMessage("DELHOME_DESCRIPTION", "delhome.description");
		addMessage("DELHOME_CONFIRMATION", "delhome.confirmation");
		addMessage("DELHOME_CONFIRMATION_VALID", "delhome.confirmationValid");
		addMessage("DELHOME_CONFIRMATION_VALID_HOVER", "delhome.confirmationValidHover");
		addMessage("DELHOME_DELETE", "delhome.delete");
		addMessage("DELHOME_INCONNU", "delhome.inconnu");
		
		addMessage("SETHOME_DESCRIPTION", "sethome.description");
		addMessage("SETHOME_SET", "sethome.set");
		addMessage("SETHOME_MULTIPLE_SET", "sethome.multipleSet");
		addMessage("SETHOME_MULTIPLE_ERROR_MAX", "sethome.multipleErrorMax");
		addMessage("SETHOME_MULTIPLE_NO_PERMISSION", "sethome.multipleNoPermission");
		
		addMessage("INFO_DESCRIPTION", "info.description");
		addMessage("INFO_PLAYER", "info.player");
		addMessage("INFO_ITEM_COLOR", "info.itemColor");
		
		addMessage("JUMP_DESCRIPTION", "jump.description");
		addMessage("JUMP_TELEPORT", "jump.teleport");
		addMessage("JUMP_TELEPORT_ERROR", "jump.teleportError");

		addMessage("KICK_DESCRIPTION", "kick.description");
		addMessage("KICK_MESSAGE", "kick.message");
		
		addMessage("KICKALL_DESCRIPTION", "kickall.description");
		addMessage("KICKALL_MESSAGE", "kickall.message");
		
		addMessage("KILL_DESCRIPTION", "kill.description");
		addMessage("KILL_PLAYER", "kill.player");
		addMessage("KILL_STAFF", "kill.staff");
		
		addMessage("LAG_DESCRIPTION", "lag.description");
		addMessage("LAG_TITLE", "lag.title");
		addMessage("LAG_TIME", "lag.time");
		addMessage("LAG_TPS", "lag.tps");
		addMessage("LAG_HISTORY_TPS", "lag.historyTps");
		addMessage("LAG_HISTORY_TPS_HOVER", "lag.historyTpsHover");
		addMessage("LAG_MEMORY", "lag.memory");
		addMessage("LAG_WORLDS", "lag.worlds");
		addMessage("LAG_WORLDS_LINE", "lag.worldsLine");
		addMessage("LAG_WORLDS_LINE_HOVER", "lag.worldsLineHover");
		
		addMessage("LIST_DESCRIPTION", "list.description");
		addMessage("LIST_TITLE", "list.title");
		addMessage("LIST_TITLE_VANISH", "list.titleVanish");
		addMessage("LIST_GROUP", "list.group");
		addMessage("LIST_SEPARATOR", "list.separator");
		addMessage("LIST_PLAYER", "list.player");
		addMessage("LIST_TAG_AFK", "list.tagAFK");
		addMessage("LIST_TAG_VANISH", "list.tagVanish");
		addMessage("LIST_EMPTY", "list.empty");
		
		addMessage("MAIL_DESCRIPTION", "mail.description");
		addMessage("MAIL_READ_TITLE", "mail.readTitle");
		addMessage("MAIL_READ_LINE_READ", "mail.readLineRead");
		addMessage("MAIL_READ_LINE_NO_READ", "mail.readLineNoRead");
		addMessage("MAIL_READ_EMPTY", "mail.readEmpty");
		addMessage("MAIL_READ_ERROR", "mail.readError");
		addListMessages("MAIL_READ_BOOK", "mail.readBook");
		addMessage("MAIL_DELETE", "mail.delete");
		addMessage("MAIL_DELETE_VALID", "mail.deleteValid");
		addMessage("MAIL_DELETE_VALID_HOVER", "mail.deleteValidHover");
		addMessage("MAIL_DELETE_CONFIRMATION", "mail.deleteConfirmation");
		addMessage("MAIL_DELETE_MAIL", "mail.deleteMail");
		addMessage("MAIL_DELETE_MAIL_HOVER", "mail.deleteMailHover");
		addMessage("MAIL_DELETE_ERROR", "mail.deleteError");
		addMessage("MAIL_CLEAR", "mail.clear");
		addMessage("MAIL_CLEAR_ERROR", "mail.clearError");
		addMessage("MAIL_SEND", "mail.send");
		addMessage("MAIL_SEND_EQUALS", "mail.sendEquals");
		addMessage("MAIL_SENDALL", "mail.sendAll");
		addMessage("MAIL_BUTTOM_READ", "mail.buttomRead");
		addMessage("MAIL_BUTTOM_READ_HOVER", "mail.buttomReadHover");
		addMessage("MAIL_BUTTON_DELETE", "mail.buttonDelete");
		addMessage("MAIL_BUTTON_DELETE_HOVER", "mail.buttonDeleteHover");
		
		addMessage("ME_DESCRIPTION", "me.description");
		addMessage("ME_PREFIX", "me.prefix");
		
		addMessage("MOJANG_DESCRIPTION", "mojang.description");
		addMessage("MOJANG_TITLE", "mojang.title");
		addMessage("MOJANG_LINE", "mojang.line");
		addMessage("MOJANG_SERVER_ACCOUNT", "mojang.serverAccount");
		addMessage("MOJANG_SERVER_API", "mojang.serverAPI");
		addMessage("MOJANG_SERVER_MOJANG", "mojang.serverMojang");
		addMessage("MOJANG_SERVER_AUTH", "mojang.serverAuth");
		addMessage("MOJANG_SERVER_AUTHSERVER", "mojang.serverAuthServer");
		addMessage("MOJANG_SERVER_MINECRAFT_NET", "mojang.serverMinecraftNet");
		addMessage("MOJANG_SERVER_SESSION", "mojang.serverSession");
		addMessage("MOJANG_SERVER_SESSIONSERVER", "mojang.serverSessionServer");
		addMessage("MOJANG_SERVER_SKINS", "mojang.serverSkins");
		addMessage("MOJANG_SERVER_TEXTURES", "mojang.serverTextures");
		addMessage("MOJANG_COLOR_GREEN", "mojang.colorGreen");
		addMessage("MOJANG_COLOR_YELLOW", "mojang.colorYellow");
		addMessage("MOJANG_COLOR_RED", "mojang.colorRed");
		
		addMessage("MORE_DESCRIPTION", "more.description");
		addMessage("MORE_PLAYER", "more.player");
		addMessage("MORE_ITEM_COLOR", "more.itemColor");
		addMessage("MORE_MAX_QUANTITY", "more.maxQuantity");
		
		addMessage("MOTD_DESCRIPTION", "motd.description");
		
		addMessage("NAMES_DESCRIPTION", "names.description");
		addMessage("NAMES_PLAYER_TITLE", "names.playerTitle");
		addMessage("NAMES_PLAYER_LINE_ORIGINAL", "names.playerLineOriginal");
		addMessage("NAMES_PLAYER_LINE_OTHERS", "names.playerLineOthers");
		addMessage("NAMES_PLAYER_EMPTY", "names.playerEmpty");
		addMessage("NAMES_OTHERS_TITLE", "names.othersTitle");
		addMessage("NAMES_OTHERS_LINE_ORIGINAL", "names.othersLineOriginal");
		addMessage("NAMES_OTHERS_LINE_OTHERS", "names.othersLineOthers");
		addMessage("NAMES_OTHERS_EMPTY", "names.othersEmpty");
		
		addMessage("NEAR_DESCRIPTION", "near.description");
		addMessage("NEAR_LIST_LINE", "near.list.line");
		addMessage("NEAR_LIST_TITLE", "near.list.title");
		addMessage("NEAR_NOPLAYER", "near.noPlayer");

		addMessage("OPME_DESCRIPTION", "opme.description");
		
		addMessage("PING_DESCRIPTION", "ping.description");
		addMessage("PING_PLAYER", "ping.player");
		addMessage("PING_OTHERS", "ping.others");
		
		addMessage("INVSEE_DESCRIPTION", "invsee.description");
		
		addMessage("REPAIR_DESCRIPTION", "repair.description");
		
		addMessage("REPAIR_ALL_DESCRIPTION", "repairall.description");
		addMessage("REPAIR_ALL_PLAYER", "repairall.player");
		
		addMessage("REPAIR_HAND_DESCRIPTION", "repairhand.description");
		addMessage("REPAIR_HAND_ITEM_COLOR", "repairhand.itemColor");
		addMessage("REPAIR_HAND_PLAYER", "repairhand.player");
		addMessage("REPAIR_HAND_ERROR", "repairhand.error");
		addMessage("REPAIR_HAND_MAX_DURABILITY", "repairhand.maxDurability");
		
		addMessage("REPAIR_HOTBAR_DESCRIPTION", "repairhotbar.description");
		addMessage("REPAIR_HOTBAR_PLAYER", "repairhotbar.player");
		
		addMessage("RULES_DESCRIPTION", "rules.description");
		
		addMessage("SKULL_DESCRIPTION", "skull.description");
		addMessage("SKULL_MY_HEAD", "skull.myHead");
		addMessage("SKULL_OTHERS", "skull.others");
		
		addMessage("SPAWNER_DESCRIPTION", "spawner.description");
		
		addMessage("SPAWNMOB_DESCRIPTION", "spawnmob.description");
		addMessage("SPAWNMOB_ERROR_MOB", "spawnmob.errorMob");
		
		addMessage("SPEED_DESCRIPTION", "speed.description");
		addMessage("SPEED_INFO_WALK", "speed.infoWalk");
		addMessage("SPEED_INFO_FLY", "speed.infoFly");
		addMessage("SPEED_PLAYER_WALK", "speed.playerWalk");
		addMessage("SPEED_PLAYER_FLY", "speed.playerFly");
		addMessage("SPEED_OTHERS_PLAYER_WALK", "speed.othersPlayerWalk");
		addMessage("SPEED_OTHERS_STAFF_WALK", "speed.othersStaffWalk");
		addMessage("SPEED_OTHERS_PLAYER_FLY", "speed.othersPlayerFly");
		addMessage("SPEED_OTHERS_STAFF_FLY", "speed.othersStaffFly");
		
		addMessage("STOP_DESCRIPTION", "stop.description");
		addMessage("STOP_MESSAGE", "stop.message");
		addMessage("STOP_MESSAGE_REASON", "stop.messageReason");
		addMessage("STOP_CONSOLE_MESSAGE", "stop.consoleMessage");
		addMessage("STOP_CONSOLE_MESSAGE_REASON", "stop.consoleMessageReason");
		
		addMessage("SUDO_DESCRIPTION", "sudo.description");
		addMessage("SUDO_COMMAND", "sudo.command");
		addMessage("SUDO_COMMAND_HOVER", "sudo.commandHover");
		addMessage("SUDO_PLAYER", "sudo.player");
		addMessage("SUDO_BYPASS", "sudo.bypass");
		addMessage("SUDO_CONSOLE", "sudo.console");
		
		addMessage("SUICIDE_DESCRIPTION", "suicide.description");
		
		addMessage("TP_DESCRIPTION", "tp.description");
		addMessage("TP_DESTINATION", "tp.destination");
		addMessage("TP_DESTINATION_HOVER", "tp.destinationHover");
		addMessage("TP_PLAYER", "tp.player");
		addMessage("TP_PLAYER_EQUALS", "tp.playerEquals");
		addMessage("TP_OTHERS_PLAYER", "tp.othersPlayer");
		addMessage("TP_OTHERS_STAFF", "tp.othersStaff");
		addMessage("TP_OTHERS_PLAYER_REPOSITION", "tp.othersPlayerReposition");
		addMessage("TP_OTHERS_STAFF_REPOSITION", "tp.othersStaffReposition");
		addMessage("TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER", "tp.othersStaffEqualsDestinationPlayer");
		addMessage("TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF", "tp.othersStaffEqualsDestinationStaff");
		addMessage("TP_ERROR_LOCATION", "tp.errorLocation");
		
		addMessage("TPALL_DESCRIPTION", "tpall.description");
		addMessage("TPALL_DESTINATION", "tpall.destination");
		addMessage("TPALL_DESTINATION_HOVER", "tpall.destinationHover");
		addMessage("TPALL_PLAYER", "tpall.player");
		addMessage("TPALL_STAFF", "tpall.staff");
		addMessage("TPALL_ERROR", "tpall.error");
		addMessage("TPALL_OTHERS_PLAYER", "tpall.othersPlayer");
		addMessage("TPALL_OTHERS_STAFF", "tpall.othersStaff");
		
		addMessage("TPHERE_DESCRIPTION", "tphere.description");
		addMessage("TPHERE_DESTINATION", "tphere.destination");
		addMessage("TPHERE_DESTINATION_HOVER", "tphere.destinationHover");
		addMessage("TPHERE_PLAYER", "tphere.player");
		addMessage("TPHERE_STAFF", "tphere.staff");
		addMessage("TPHERE_EQUALS", "tphere.equals");
		addMessage("TPHERE_ERROR", "tphere.error");

		addMessage("TPPOS_DESCRIPTION", "tppos.description");
		addMessage("TPPOS_POSITION", "tppos.position");
		addMessage("TPPOS_POSITION_HOVER", "tppos.positionHover");
		addMessage("TPPOS_PLAYER", "tppos.player");
		addMessage("TPPOS_PLAYER_ERROR", "tppos.playerError");
		addMessage("TPPOS_OTHERS_PLAYER", "tppos.othersPlayer");
		addMessage("TPPOS_OTHERS_STAFF", "tppos.othersStaff");
		addMessage("TPPOS_OTHERS_ERROR", "tppos.othersError");
		
		addMessage("TIME_DESCRIPTION", "time.description");
		addMessage("TIME_FORMAT", "time.format");
		addMessage("TIME_INFORMATION", "time.information");
		addMessage("TIME_SET_WORLD", "time.setWorld");
		addMessage("TIME_SET_ALL_WORLD", "time.setAllWorld");
		addMessage("TIME_ERROR", "time.error");
		
		addMessage("TIME_DAY_DESCRIPTION", "time.dayDescription");
		addMessage("TIME_NIGHT_DESCRIPTION", "time.nightDescription");
		
		addMessage("TOP_DESCRIPTION", "top.description");
		addMessage("TOP_POSITION", "top.position");
		addMessage("TOP_POSITION_HOVER", "top.positionHover");
		addMessage("TOP_TELEPORT", "top.teleport");
		addMessage("TOP_TELEPORT_ERROR", "top.teleportError");
		
		addMessage("TREE_DESCRIPTION", "tree.description");
		addMessage("TREE_INCONNU", "tree.inconnu");
		addMessage("TREE_NO_CAN", "tree.noCan");
		
		addMessage("UUID_DESCRIPTION", "uuid.description");
		addMessage("UUID_NAME", "uuid.name");
		addMessage("UUID_PLAYER", "uuid.player");
		addMessage("UUID_PLAYER_OTHERS", "uuid.otherPlayer");
		
		addMessage("VANISH_DESCRIPTION", "vanish.description");
		addMessage("VANISH_PLAYER_ENABLE", "vanish.playerEnable");
		addMessage("VANISH_PLAYER_ENABLE_ERROR", "vanish.playerEnableError");
		addMessage("VANISH_PLAYER_DISABLE", "vanish.playerDisable");
		addMessage("VANISH_PLAYER_DISABLE_ERROR", "vanish.playerDisableError");
		addMessage("VANISH_OTHERS_PLAYER_ENABLE", "vanish.othersPlayerEnable");
		addMessage("VANISH_OTHERS_PLAYER_DISABLE", "vanish.othersPlayerDisable");
		addMessage("VANISH_OTHERS_STAFF_ENABLE", "vanish.othersStaffEnable");
		addMessage("VANISH_OTHERS_STAFF_ENABLE_ERROR", "vanish.othersStaffEnableError");
		addMessage("VANISH_OTHERS_STAFF_DISABLE", "vanish.othersStaffDisable");
		addMessage("VANISH_OTHERS_STAFF_DISABLE_ERROR", "vanish.othersStaffDisableError");
		
		addMessage("WARP_NAME", "warp.name");
		addMessage("WARP_NAME_HOVER", "warp.nameHover");
		addMessage("WARP_INCONNU", "warp.inconnu");
		addMessage("WARP_NO_PERMISSION", "warp.noPermission");
		
		addMessage("WARP_DESCRIPTION", "warp.description");
		addMessage("WARP_EMPTY", "warp.empty");
		addMessage("WARP_LIST_TITLE", "warp.listTitle");
		addMessage("WARP_LIST_LINE", "warp.listLine");
		addMessage("WARP_LIST_LINE_DELETE", "warp.listLineDelete");
		addMessage("WARP_LIST_LINE_DELETE_ERROR_WORLD", "warp.listLineDeleteErrorWorld");
		addMessage("WARP_LIST_TELEPORT", "warp.listTeleport");
		addMessage("WARP_LIST_TELEPORT_HOVER", "warp.listTeleportHover");
		addMessage("WARP_LIST_DELETE", "warp.listDelete");
		addMessage("WARP_LIST_DELETE_HOVER", "warp.listDeleteHover");
		addMessage("WARP_TELEPORT_PLAYER", "warp.teleportPlayer");
		addMessage("WARP_TELEPORT_PLAYER_ERROR", "warp.teleportPlayerError");
		addMessage("WARP_TELEPORT_OTHERS_PLAYER", "warp.teleportOthersPlayer");
		addMessage("WARP_TELEPORT_OTHERS_STAFF", "warp.teleportOthersStaff");
		addMessage("WARP_TELEPORT_OTHERS_ERROR", "warp.teleportOthersError");
		
		addMessage("WEATHER_DESCRIPTION", "weather.description");
		addMessage("WEATHER_ERROR", "weather.error");
		addMessage("WEATHER_SUN", "weather.sun");
		addMessage("WEATHER_RAIN", "weather.rain");
		addMessage("WEATHER_STORM", "weather.storm");
		addMessage("WEATHER_SUN_DURATION", "weather.sunDuration");
		addMessage("WEATHER_RAIN_DURATION", "weather.rainDuration");
		addMessage("WEATHER_STORM_DURATION", "weather.stormDuration");
		
		addMessage("WEATHER_RAIN_DESCRIPTION", "weather.rainDescription");
		addMessage("WEATHER_STORM_DESCRIPTION", "weather.stormDescription");
		addMessage("WEATHER_SUN_DESCRIPTION", "weather.sunDescription");
		
		addMessage("WHOIS_DESCRIPTION", "whois.description");
		addMessage("WHOIS_TITLE", "whois.title");
		addMessage("WHOIS_UUID", "whois.uuid");
		addMessage("WHOIS_UUID_STYLE", "whois.uuidStyle");
		addMessage("WHOIS_IP", "whois.ip");
		addMessage("WHOIS_IP_STYLE", "whois.ipStyle");
		addMessage("WHOIS_PING", "whois.ping");
		addMessage("WHOIS_HEAL", "whois.heal");
		addMessage("WHOIS_FOOD", "whois.food");
		addMessage("WHOIS_FOOD_SATURATION", "whois.foodSaturation");
		addMessage("WHOIS_EXP", "whois.exp");
		addMessage("WHOIS_EXP_LEVEL", "whois.expLevel");
		addMessage("WHOIS_EXP_POINT", "whois.expPoint");
		addMessage("WHOIS_SPEED", "whois.speed");
		addMessage("WHOIS_SPEED_FLY", "whois.speedFly");
		addMessage("WHOIS_SPEED_WALK", "whois.speedWalk");
		addMessage("WHOIS_LOCATION", "whois.location");
		addMessage("WHOIS_LOCATION_POSITION", "whois.locationPosition");
		addMessage("WHOIS_LOCATION_POSITION_HOVER", "whois.locationPositionHover");
		addMessage("WHOIS_BALANCE", "whois.balance");
		addMessage("WHOIS_GAMEMODE", "whois.gamemode");
		addMessage("WHOIS_GOD_ENABLE", "whois.godEnable");
		addMessage("WHOIS_GOD_DISABLE", "whois.godDisable");
		addMessage("WHOIS_FLY_ENABLE_FLY", "whois.flyEnableFly");
		addMessage("WHOIS_FLY_ENABLE_WALK", "whois.flyEnableWalk");
		addMessage("WHOIS_FLY_DISABLE", "whois.flyDisable");
		addMessage("WHOIS_MUTE_ENABLE", "whois.muteEnable");
		addMessage("WHOIS_MUTE_DISABLE", "whois.muteDisable");
		addMessage("WHOIS_VANISH_ENABLE", "whois.vanishEnable");
		addMessage("WHOIS_VANISH_DISABLE", "whois.vanishDisable");
		addMessage("WHOIS_AFK_ENABLE", "whois.afkEnable");
		addMessage("WHOIS_AFK_DISABLE", "whois.afkDisable");
		addMessage("WHOIS_FIRST_DATE_PLAYED", "whois.firstDatePlayed");
		addMessage("WHOIS_LAST_DATE_PLAYED", "whois.lastDatePlayed");
		
		addMessage("DELWARP_DESCRIPTION", "delwarp.description");
		addMessage("DELWARP_INCONNU", "delwarp.inconnu");
		addMessage("DELWARP_NAME", "delwarp.name");
		addMessage("DELWARP_NAME_HOVER", "delwarp.nameHover");
		addMessage("DELWARP_CONFIRMATION", "delwarp.confirmation");
		addMessage("DELWARP_DELETE", "delwarp.delete");
		addMessage("DELWARP_CONFIRMATION_VALID", "delwarp.confirmationValid");
		addMessage("DELWARP_CONFIRMATION_VALID_HOVER", "delwarp.confirmationValidHover");
		
		addMessage("SETWARP_DESCRIPTION", "setwarp.description");
		addMessage("SETWARP_NAME", "setwarp.name");
		addMessage("SETWARP_NAME_HOVER", "setwarp.nameHover");
		addMessage("SETWARP_REPLACE", "setwarp.replace");
		addMessage("SETWARP_NEW", "setwarp.new");
		
		addMessage("WORLDS_DESCRIPTION", "worlds.description");
		addMessage("WORLDS_END_DESCRIPTION", "worlds.endDescription");
		addMessage("WORLDS_NETHER_DESCRIPTION", "worlds.netherDescription");
		addMessage("WORLDS_LIST_TITLE", "worlds.listTitle");
		addMessage("WORLDS_LIST_LINE", "worlds.listLine");
		addMessage("WORLDS_LIST_TELEPORT", "worlds.listTeleport");
		addMessage("WORLDS_LIST_TELEPORT_HOVER", "worlds.listTeleportHover");
		addMessage("WORLDS_TELEPORT_WORLD", "worlds.teleportWorld");
		addMessage("WORLDS_TELEPORT_WORLD_HOVER", "worlds.teleportWorldHover");
		addMessage("WORLDS_TELEPORT_PLAYER", "worlds.teleportPlayer");
		addMessage("WORLDS_TELEPORT_PLAYER_ERROR", "worlds.teleportPlayerError");
		addMessage("WORLDS_TELEPORT_OTHERS_PLAYER", "worlds.teleportOthersPlayer");
		addMessage("WORLDS_TELEPORT_OTHERS_STAFF", "worlds.teleportOthersStaff");
		addMessage("WORLDS_TELEPORT_OTHERS_ERROR", "worlds.teleportOthersError");
	}
}
