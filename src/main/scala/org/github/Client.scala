package org.github

import net.caoticode.buhtig.Buhtig
import net.caoticode.buhtig.Converters._
import net.caoticode.buhtig.SyncClient

import org.json4s._

object Client {
  def main(args: Array[String]): Unit = {
    args.foreach(println)
    def normAssignee(value: Option[Assignee]) = value.getOrElse(new Assignee(""))
    def normLabel(value: Option[Label]) = value.getOrElse(new Label(""))
    def normMilestone(value: Option[Milestone]) = value.getOrElse(new Milestone(""))

    def printIssues(repoName: String, issues: List[Issue]): List[String] = {
      val regex = """.*\(spent:\s?(\d+\.?\d{0,2})\).*""".r
      issues.map { issue =>
        val spentTime = (for (m <- regex findFirstMatchIn issue.title) yield m group 1).getOrElse("0")

        val labels = issue.labels.map(normLabel(_).name).mkString(";")
        val assignee = normAssignee(issue.assignee).login
        val milestoneName = normMilestone(issue.milestone).title

        val result = s"${repoName}\t${labels}\t${issue.number}\t${issue.title}\t${assignee}\t${spentTime}\t${issue.state}\t${milestoneName}"
        result.mkString
      }

    }

    def printIssuesInEachRepo(): List[String] = {
      val token = sys.env("GITHUB_TOKEN")
      val buhtig = new Buhtig(token)
      val client = buhtig.syncClient
      val deltaworx = client.orgs.Deltaworx.repos.get[JSON]

      implicit val formats = DefaultFormats
      val repos = deltaworx.camelizeKeys.extract[List[Repo]]
      val issuesPerRepo = repos.flatMap { repo =>
        (client.repos("Deltaworx", repo.name).issues ? ("state" -> "open")).getOpt[JSON] match {
          case Some(req) => printIssues(repo.name, req.camelizeKeys.extract[List[Issue]])
          case None => List(s"${repo.name},,,")
        }
      }

      buhtig.close()
      issuesPerRepo
    }

    val f = new ResultsWriter("results.csv", printIssuesInEachRepo)
    f.writeResults
  }
}

case class Repo(id: Int, name: String)
case class Assignee(login: String)
case class Milestone(title: String)
case class Label(name: String)
case class Issue(number: Int, title: String, assignee: Option[Assignee], milestone: Option[Milestone], createdAt: String, state: String, labels: List[Option[Label]])
