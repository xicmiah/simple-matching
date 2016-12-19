package com.simplematching.models

import com.simplematching.models.OrderBook.Side

import scala.collection.immutable.SortedMap

object OrderBook {
  // Well, this is not the way orders match, but if you say so...
  type Side = SortedMap[(Price, Size), Seq[Order]]

  val empty = OrderBook(SortedMap.empty, SortedMap.empty)
}

case class OrderBook(bids: Side, asks: Side) {
  def addOrderAndMatch(order: Order): (OrderBook, Seq[Trade]) = {
    if (hasMatchingOrder(order)) {
      order.action match {
        case Action.Buy =>
          val (updatedAsks, trades) = removeFirstMatch(asks, order)
          copy(asks = updatedAsks) -> trades
        case Action.Sell =>
          val (updatedBids, trades) = removeFirstMatch(bids, order)
          copy(bids = updatedBids) -> trades
      }
    } else {
      order.action match {
        case Action.Buy => copy(bids = addToSide(bids, order)) -> Seq.empty
        case Action.Sell => copy(asks = addToSide(asks, order)) -> Seq.empty
      }
    }
  }

  private def hasMatchingOrder(order: Order): Boolean = {
    val oppositeSide = order.action match {
      case Action.Buy => asks
      case Action.Sell => bids
    }
    oppositeSide.contains(order.price -> order.size)
  }

  private def removeFirstMatch(side: Side, order: Order): (Side, Seq[Trade]) = {
    val key = order.price -> order.size
    side.getOrElse(key, Seq.empty) match {
      case matched +: rest =>
        val updatedSide = if (rest.nonEmpty) side.updated(key, rest) else side - key
        updatedSide -> Seq(Trade.fromMatchingOrders(matched, order))
      case empty =>
        side -> Seq()
    }
  }

  private def addToSide(side: Side, order: Order) = {
    val key = order.price -> order.size
    val newValue = side.getOrElse(key, Seq.empty) :+ order
    side.updated(key, newValue)
  }
}
