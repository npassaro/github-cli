package org.github

import scala.tools.nsc.io._

class ResultsWriter(filePathC: String, contentsC: List[String]) {
  val filePath = filePathC
  val contents = contentsC

  def writeResults = {
    contents.foreach { line =>
      File(filePath).appendAll(s"${line.toString}\n")
    }
  }
}
