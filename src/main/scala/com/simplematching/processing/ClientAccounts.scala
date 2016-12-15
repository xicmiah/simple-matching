package com.simplematching.processing

import com.simplematching.models.{Client, Trade}

import scala.collection.immutable.SortedMap

case class ClientAccounts(clients: SortedMap[String, Client] = SortedMap.empty) {
  def applyTrade(trade: Trade): ClientAccounts = {
    updateClient(trade.buyer, applyBuy(trade, _))
      .updateClient(trade.seller, applySell(trade, _))
  }

  private def updateClient(name: String, f: Client => Client) = {
    for {
      client <- clients.get(name)
    } yield copy(clients.updated(name, f(client)))
  }.getOrElse(this)

  private def applyBuy(trade: Trade, client: Client) = {
    client.applyChange(-trade.sum, trade.equity, trade.size)
  }

  private def applySell(trade: Trade, client: Client) = {
    client.applyChange(trade.sum, trade.equity, -trade.size)
  }
}
