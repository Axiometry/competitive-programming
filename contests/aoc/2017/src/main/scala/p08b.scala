import p08a.ops
import utils.{RegexContext, ToInt, lines}

import scala.util.Try

object p08b extends App {
  case class Op(reg: String, inc: Int, cond: (String => Int) => Boolean) {
    def apply(m: Map[String, Int]): Map[String, Int] = if(cond(m)) m.updated(reg, m(reg) + inc) else m
  }
  
  val ops = lines.map {
    case r"([a-z]+)$r (inc|dec)$op (-?[0-9]+)${ToInt(inc)} if ([a-z]+)$cr ([^ ]+)$c (-?[0-9]+)${ToInt(cn)}" =>
      Op(r, if(op == "inc") inc else -inc, c match {
        case "<" => _(cr) < cn
        case ">" => _(cr) > cn
        case "<=" => _(cr) <= cn
        case ">=" => _(cr) >= cn
        case "==" => _(cr) == cn
        case "!=" => _(cr) != cn
      })
  }
  
  val result = ops.scanLeft(Map.empty[String, Int].withDefaultValue(0))((m, op) => op(m))
  println((result.filter(_.nonEmpty).map(_.valuesIterator.max) :+ 0).max)
}
