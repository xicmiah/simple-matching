package com.simplematching

import java.nio.file.{Files, Paths}

import com.simplematching.models.{Client, Order}

import scala.collection.JavaConverters._

object Main {
  def main(args: Array[String]): Unit = {
    val clients = Files.lines(Paths.get("clients.txt"))
      .iterator().asScala
      .map(line => Client.parseFromLine(line)
        .getOrElse(throw new RuntimeException(s"Couldn't parse client from $line")))
      .map(client => client.name -> client).toMap

    val orders = Files.lines(Paths.get("orders.txt"))
      .iterator().asScala
      .map(line => Order.parseFromLine(line)
        .getOrElse(throw new RuntimeException(s"Couldn't parse order from $line")))

    println(clients.mkString("\n"))
    println(orders.mkString("\n"))
  }
}