package com.simplematching.processing

import com.simplematching.models.{Equity, Order, OrderBook, Trade}

case class OrderBooks(books: Map[Equity, OrderBook] = Map.empty) {
  def addOrderEmitTrades(order: Order): (OrderBooks, Seq[Trade]) = {
    val targetOrderBook = books.getOrElse(order.equity, OrderBook.empty)
    val (newBook, trades) = targetOrderBook.addOrderAndMatch(order)
    copy(books = books.updated(order.equity, newBook)) -> trades
  }
}