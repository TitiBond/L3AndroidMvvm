package com.example.textaudioai.repositories

import io.paperdb.Paper
import java.util.Date

data class Player(
    val id: Int,
    val title:String,
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
    fun findOnePlayerById(playerId: Int): Player?;
    fun savePlayer(players: List<Player>);
    fun updatePlayer(player: Player): Player?;
    // Bool represent success or fail
    fun deletePlayer(playerId: Int): Boolean;
}

/**
 * It it possible for the player repository to throw an exception,
 * So you are required to wrap any call in a try catch.
 */
class PlayerRepository: PaperPlayersRepository {
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

    override fun savePlayer(players: List<Player>) {
        Paper.book().write(collection, players);
    }

    override fun updatePlayer(player: Player): Player? {
        val players = findAllPlayers();
        if (players.isEmpty()) return null;

        val index = players.indexOfFirst { it.id == player.id }
        if (index == -1) return null;

        val updatedPlayers = players.toMutableList();
        updatedPlayers[index] = player;
        savePlayer(updatedPlayers);
        return player;
    }

    override fun deletePlayer(playerId: Int): Boolean {
        val players = findAllPlayers();
        if (players.isEmpty()) return false;

        val index = players.indexOfFirst { it.id == playerId }
        if (index == -1) return false;

        val updatedPlayers = players.toMutableList();
        updatedPlayers.removeAt(index);
        savePlayer(updatedPlayers);
        return true;
    }
}