package com.simplematching

import java.nio.file.{Files, Paths}

import com.simplematching.models.{Client, Order, Trade}
import com.simplematching.processing.{ClientAccounts, OrderBooks}

import scala.collection.JavaConverters._
import scala.collection.immutable.SortedMap
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    val initial = ClientAccounts {
      Source.fromFile("clients.txt").getLines()
        .map(line => Client.parseFromLine(line)
          .getOrElse(throw new RuntimeException(s"Couldn't parse client from $line")))
        .map(client => client.name -> client)
        .foldLeft(SortedMap.newBuilder[String, Client])(_ += _).result()
    }

    val orders = Source.fromFile("orders.txt").getLines()
      .map(line => Order.parseFromLine(line)
        .getOrElse(throw new RuntimeException(s"Couldn't parse order from $line")))

    val trades = orders
      .scanLeft(OrderBooks() -> Seq.empty[Trade]) {
        case ((books, _), order) => books.addOrderEmitTrades(order)
      }.flatMap(_._2)

    val resulting = trades.foldLeft(initial)(_ applyTrade _)

    Files.write(Paths.get("result.txt"), resulting.clients.values.map(_.toTsvLine).asJava)

    Source.fromFile("result.txt").getLines().foreach(println)
  }
}