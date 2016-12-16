name := "waves-scratchpad"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
libraryDependencies += "com.lihaoyi" %% "utest" % "0.4.4" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")