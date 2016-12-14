package com.simplematching

import java.nio.file.{Files, Paths}

import com.simplematching.models.Client

import scala.collection.JavaConverters._

object Main {
  def main(args: Array[String]): Unit = {
    val clientsRaw = Files.lines(Paths.get("clients.txt"))

    val clients = clientsRaw.iterator().asScala
      .map(line => Client.parseFromLine(line)
        .getOrElse(throw new RuntimeException(s"Couldn't parse line $line")))
      .map(client => client.name -> client).toMap

    println(clients.mkString("\n"))
  }
}