package com.simplematching.processing

import com.simplematching.models.{Equity, Order, OrderBook, Trade}

case class OrderBooks(books: Map[Equity, OrderBook] = Map.empty) {
  def addOrderEmitTrades(order: Order): (OrderBooks, Seq[Trade]) = {
    val targetOrderBook = books.getOrElse(order.equity, OrderBook.empty)
    val (newBook, trades) = applyOrder(targetOrderBook, order)
    copy(books = books.updated(order.equity, newBook)) -> trades
  }

  private def applyOrder(orderBook: OrderBook, order: Order): (OrderBook, Seq[Trade]) = {
    val withOrder = orderBook.addOrder(order)
    val trades = withOrder.possibleTrades
    trades.foldLeft(withOrder)(_.removeMatchedOrders(_)) -> trades
  }
}