


object p08b extends App {
  import p08._
  
  val result = ops.scanLeft(Map.empty[String, Int].withDefaultValue(0))((m, op) => op(m))
  println((result.filter(_.nonEmpty).map(_.valuesIterator.max) :+ 0).max)
}
