package com.example.textaudioai.repositories

import io.paperdb.Paper
import java.util.Date

data class Player(
    val id: Int,
    var title: String,
    val image: Int,
    val filePath: String,
    // Is it really necessary ?
    val duration: Double,
    val content: String,
    val updatedAt: Date,
    val createdAt: Date,
)

interface PaperPlayersRepository {
    fun findAllPlayers(): List<Player>;
    fun findManyByTOBEDEFINED(): List<Player>;
    fun findOnePlayerById(playerId: Int): Player?;
    fun savePlayer(player: Player): Boolean;
    fun updatePlayer(player: Player): Player?;
    // Bool represent success or fail
    fun deletePlayerById(playerId: Int): Boolean;
    fun getNewIndex(): Int;
}

/**
 * It it possible for the player repository to throw an exception,
 * So you are required to wrap any call in a try catch.
 */
class PlayersRepository: PaperPlayersRepository {
    private val collection = "players";

    override fun findAllPlayers(): List<Player> {
        return Paper.book().read(collection) ?: listOf();
    }

    override fun findOnePlayerById(playerId: Int): Player? {
        val players = findAllPlayers();
        // Checking for the size prevent the iteration of the loop.
        // But with it or without it, it will possibly return null anyway.
        if (players.isEmpty()) return null;

        return players.find { it.id == playerId };
    }

    override fun findManyByTOBEDEFINED(): List<Player> {
        TODO("Retrieve all players, iter and filter from a filter parameter of this function")
    }

    override fun savePlayer(player: Player): Boolean {
        val players = findAllPlayers();
        val index = players.indexOfFirst { it.title == player.title }
        if (index != -1) return false;

        players.plus(player);
        saveAllPlayers(players);
        return true;
    }

    private fun saveAllPlayers(players: List<Player>) {
        Paper.book().write(collection, players);
    }

    override fun updatePlayer(player: Player): Player? {
        val players = findAllPlayers();
        if (players.isEmpty()) return null;

        val index = players.indexOfFirst { it.id == player.id }
        if (index == -1) return null;

        val updatedPlayers = players.toMutableList();
        updatedPlayers[index] = player;
        saveAllPlayers(updatedPlayers);
        return player;
    }

    override fun deletePlayerById(playerId: Int): Boolean {
        val players = findAllPlayers();
        if (players.isEmpty()) return false;

        val index = players.indexOfFirst { it.id == playerId }
        if (index == -1) return false;

        val updatedPlayers = players.toMutableList();
        updatedPlayers.removeAt(index);
        saveAllPlayers(updatedPlayers);
        return true;
    }

    override fun getNewIndex(): Int {
        val players = findAllPlayers();
        return players.last().id + 1;
    }
}