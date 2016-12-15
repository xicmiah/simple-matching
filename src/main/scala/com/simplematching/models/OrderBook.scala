package com.simplematching.models

import com.simplematching.models.OrderBook.Side

import scala.collection.immutable.SortedMap

object OrderBook {
  type Side = SortedMap[Price, Seq[Order]]

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
    val newValue = side.getOrElse(order.price, Vector.empty) :+ order
    side.updated(order.price, newValue)
  }
}
