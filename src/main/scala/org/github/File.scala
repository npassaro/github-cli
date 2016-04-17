package org.github

import scala.tools.nsc.io._

class ResultsWriter(filePathC: String, contentsC: List[String]) {
  var filePath = filePathC
  var contents = contentsC

  def writeResults = {
    contents.foreach { line =>
      File(filePath).appendAll(s"${line.toString}\n")
    }
  }
}
