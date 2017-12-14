import scala.util.Try
import scala.util.matching.Regex

object utils {
  def readLines(): Iterator[String] = io.Source.fromInputStream(System.in).getLines()
  lazy val lines: Seq[String] = readLines().toSeq
  
  implicit class RegexContext(sc: StringContext) {
    def r = new Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }
  
  object Iterable {
    def unapplySeq[T](s: Iterable[T]): Option[Seq[T]] = Some(s.toSeq)
  }
  
  object ToInt {
    def unapply(s: String): Option[Int] = Try(s.toInt).toOption
  }
}
