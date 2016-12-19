package com.simplematching.models

object Trade {
  def fromMatchingOrders(first: Order, second: Order): Trade = {
    first.action -> second.action match {
      case (Action.Buy, Action.Sell) => fromBuySell(first, second)
      case (Action.Sell, Action.Buy) => fromBuySell(second, first)
      case _ => throw new RuntimeException(s"Cannot create trade from two orders on same side: $first $second")
    }
  }

  private def fromBuySell(buyOrder: Order, sellOrder: Order): Trade = {
    require(buyOrder.equity == sellOrder.equity)

    require(buyOrder.price == sellOrder.price)
    require(buyOrder.size == sellOrder.size)

    Trade(buyOrder.client, sellOrder.client, buyOrder.equity, buyOrder.price, buyOrder.size)
  }
}

case class Trade(buyer: String, seller: String, equity: Equity, price: Price, size: Size) {
  def affectedClients = Set(buyer, seller)

  def sum: Money = price * size
}
