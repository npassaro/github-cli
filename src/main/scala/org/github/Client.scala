package org.github
import net.caoticode.buhtig.Buhtig
import net.caoticode.buhtig.Converters._
import org.json4s.native.JsonMethods._
import org.json4s._
object Client extends App {
  val token = "6b80d83b0d184d119f24a55ea2dbcc221d025c05" // your personal API token
  val buhtig = new Buhtig(token)
  val client = buhtig.syncClient
  val deltaworx = client.orgs.Deltaworx.repos.get[JSON]

  def f(repo: Repo) = println(s"<${repo.id},${repo.name}>")

  val regex = """.*\(spent:\s?(\d+\.?\d{0,2})\).*""".r

  def printIssues(repoName: String, issues: List[Issue]) = {
    issues.foreach { issue =>
      regex.findAllIn(issue.title).matchData foreach {
        m => println(m.group(1))
      }
    }
  }

  implicit val formats = DefaultFormats

  case class Repo(id: Int, name: String)
  case class Assignee(login: String)
  case class Milestone(title: String)
  case class Issue(title: String, assignee: Assignee, milestone: Milestone)

  println("=====DELTAWORX=====")

  val repos = deltaworx.camelizeKeys.extract[List[Repo]]

  val issuesPerRepo = repos.map { repo =>
    (client.repos("Deltaworx", repo.name).issues ? ("milestone" -> "1", "state" -> "closed")).getOpt[JSON] match {
      case Some(req) => printIssues(repo.name, req.camelizeKeys.extract[List[Issue]])
      case None => println(s"${repo.name},,,")
    }
  }

  buhtig.close()
}

// foreach { issue =>
//   issue match {
//     case Issue(title, assignee, milestone) => println(s"${title}")
//   }
// }
