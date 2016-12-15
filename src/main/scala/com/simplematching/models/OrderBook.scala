package com.simplematching.models

import com.simplematching.models.OrderBook.Side

import scala.collection.immutable.SortedMap

object OrderBook {
  type Side = SortedMap[(Price, Quantity), Seq[Order]]

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
    val key = order.price -> order.quantity
    val newValue = side.getOrElse(key, Vector.empty) :+ order
    side.updated(key, newValue)
  }

  def possibleTrades: Seq[Trade] = {
    bids.keySet.intersect(asks.keySet).view
      .flatMap { key =>
        bids.getOrElse(key, Seq()).zip(asks.getOrElse(key, Seq()))
          .map { case (buyOrder, sellOrder) => Trade(buyOrder, sellOrder) }
      }.toSeq
  }

  def removeOrdersByTrade(trade: Trade): OrderBook = {
    OrderBook(removeFromSide(bids, trade.buyOrder), removeFromSide(asks, trade.sellOrder))
  }

  private def removeFromSide(side: Side, order: Order) = {
    val key = order.price -> order.quantity
    val (l, r) = side.getOrElse(key, Vector.empty).span(_ != order)
    val newValue = l ++ r.drop(1)
    side.updated(key, newValue)
  }
}
