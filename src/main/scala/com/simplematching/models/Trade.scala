package com.simplematching.models

object Trade {
  def fromMatchingOrders(buyOrder: Order, sellOrder: Order): Trade = {
    require(buyOrder.equity == sellOrder.equity)
    require(buyOrder.action == Action.Buy)
    require(sellOrder.action == Action.Sell)

    require(buyOrder.price == sellOrder.price)
    require(buyOrder.size == sellOrder.size)

    Trade(buyOrder.client, sellOrder.client, buyOrder.equity, buyOrder.price, buyOrder.size)
  }
}

case class Trade(buyer: String, seller: String, equity: Equity, price: Price, size: Size) {
  def affectedClients = Set(buyer, seller)

  def sum: Money = price * size
}
