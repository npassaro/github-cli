package org.github

import java.io._

class ResultsWriter(filePathC: String, contentsC: List[String]) {
  val filePath = filePathC
  val contents = contentsC

  def writeResults = {
    val writer = new PrintWriter(new File(filePath))

    contents.foreach { line =>
      writer.write(line.toString)
    }
    writer.close
  }
}
