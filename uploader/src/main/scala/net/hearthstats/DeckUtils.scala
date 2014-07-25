package net.hearthstats

import java.io.IOException
import java.util.ArrayList
import java.util.List
import net.hearthstats.log.Log
import org.json.simple.JSONObject
import scala.collection.JavaConversions._
import net.hearthstats.Deck

object DeckUtils {

  private var _decks: List[JSONObject] = _

  def updateDecks() {
    try {
      _decks = API.getDecks
    } catch {
      case e: IOException => Log.warn("Error occurred while loading deck list from HearthStats.net",
        e)
    }
  }

  def getDeckFromSlot(slotNum: java.lang.Integer): Option[Deck] = {
    getDecks
    for (
      i <- 0 until _decks.size if _decks.get(i).get("slot") != null &&
        _decks.get(i).get("slot").toString == slotNum.toString
    ) return Some(Deck.fromJson(_decks.get(i)))
    None
  }

  def getDecks: List[JSONObject] = {
    if (_decks == null) updateDecks()
    _decks
  }

  def getDeckLists: List[Deck] =
    for (deck <- getDecks)
      yield Deck.fromJson(deck)

  def getDeck(id: Int): Deck =
    getDeckLists.find(_.id == id) match {
      case Some(d) => d
      case None => throw new IllegalArgumentException("No deck found for id " + id)
    }
}
