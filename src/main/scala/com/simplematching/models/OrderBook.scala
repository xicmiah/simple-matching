package com.simplematching.models

import com.simplematching.models.OrderBook.Side

import scala.collection.immutable.SortedMap

object OrderBook {
  type Side = SortedMap[(Price, Size), Seq[Order]]

  val empty = OrderBook(SortedMap.empty, SortedMap.empty)
}

case class OrderBook(bids: Side, asks: Side) {
  def addOrder(order: Order): OrderBook = {
    order.action match {
      case Action.Buy => copy(bids = addToSide(bids, order))
      case Action.Sell => copy(asks = addToSide(asks, order))
    }
  }

  private def addToSide(side: Side, order: Order) = {
    val key = order.price -> order.size
    val newValue = side.getOrElse(key, Vector.empty) :+ order
    side.updated(key, newValue)
  }

  def possibleTrades: Seq[Trade] = {
    bids.keySet.intersect(asks.keySet).view
      .flatMap { key =>
        bids.getOrElse(key, Seq()).zip(asks.getOrElse(key, Seq()))
          .map { case (buyOrder, sellOrder) => Trade.fromMatchingOrders(buyOrder, sellOrder) }
      }.toSeq
  }

  def removeMatchedOrders(trade: Trade): OrderBook = {
    val buyOrder = Order(trade.buyer, Action.Buy, trade.equity, trade.price, trade.size)
    val sellOrder = Order(trade.seller, Action.Sell, trade.equity, trade.price, trade.size)
    OrderBook(removeFromSide(bids, buyOrder), removeFromSide(asks, sellOrder))
  }

  private def removeFromSide(side: Side, order: Order) = {
    val key = order.price -> order.size
    val orders = side.getOrElse(key, Vector.empty)
    val (l, r) = orders.span(_ != order)
    val newValue = l ++ r.drop(1)

    if (newValue.isEmpty) side - key else side.updated(key, newValue)
  }
}
