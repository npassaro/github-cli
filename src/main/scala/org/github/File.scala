package org.github

import scala.tools.nsc.io._

object ResultsWriter {
  def deleteResultsFile(path: String) = File(path).delete()
}

class ResultsWriter(filePathC: String, contentsC: List[String]) {
  var filePath = filePathC
  var contents = contentsC

  def writeResults = {
    contents.foreach { line =>
      File(filePath).appendAll(s"${line.toString}\n")
    }
  }
}
