import scala.annotation.switch
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}

object BurnoutS extends App {
  case class TimeData(_on: Long, _off: Long, toggle: Boolean) {
    val total = _on + _off
    def on(startOn: Boolean) = if(startOn) _on else _off
    def off(startOn: Boolean) = if(startOn) _off else _on
    def repeat(rep: Long, startOn: Boolean) = {
      if(!toggle)       TimeData(on(startOn)*rep, off(startOn)*rep, false)
      else if(rep%2==1) TimeData(on(startOn)+(total*(rep/2)), off(startOn)+(total*(rep/2)), toggle)
      else              TimeData(total*(rep/2), total*(rep/2), false)
    }
    def repeatUntil(target: Long, startOn: Boolean) = {
      val (div, mul) = if(toggle) (total, 2) else (on(startOn), 1)
      val times = (target-1)/div
      
      if(target > (times*div)+on(startOn)) repeat(times*mul+1, startOn)
      else                                 repeat(times*mul, startOn)
    }
  }
  trait Elem {
    def time: TimeData
    def sim(target: Long, startOn: Boolean): Long
  }
  class Value(v: Long) extends Elem {
    val time = TimeData(v, 0, true)
    
    override def toString: String = s"$time"
    def sim(target: Long, startOn: Boolean) = if(startOn) target else throw new RuntimeException()
  }
  class Group(val elems: Seq[Elem], val repeats: Int) extends Elem {
    val innerTime = elems.foldLeft(TimeData(0, 0, false))((acc, e) => TimeData(
      acc._on + e.time.on(!acc.toggle),
      acc._off + e.time.off(!acc.toggle),
      if(e.time.toggle) !acc.toggle else acc.toggle
    ))
    val time = innerTime.repeat(repeats, true)
    
    override def toString: String = s"($time/$innerTime/$repeats: ${elems.mkString(", ")})"
    def sim(target: Long, startOn: Boolean): Long = {
      val data = innerTime.repeatUntil(target, startOn)
      var total = data.total
      var rem = target-data._on
      var on = if(data.toggle) !startOn else startOn
      
      for(e <- elems) {
        if(rem <= e.time.on(on)) {
          return total + e.sim(rem, on)
        } else {
          total += e.time.total
          rem -= e.time.on(on)
          if(e.time.toggle) on ^= true
        }
      }
      throw new RuntimeException()
    }
  }
  def parse(line: Array[Char]): Group = {
    var i = 0
    def parseNum(): Long = {
      var n = 0L
      while(i < line.length) (line(i): @switch) match {
        case c@('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9') =>
          n = n*10+c-'0'
          i += 1
        case _ => return n
      }
      n
    }
    def parseGroup(): Group = {
      var elems = ListBuffer[Elem]()
      var repeats = 1
      
      breakable(while(i < line.length) (line(i): @switch) match {
        case c@('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9') =>
          elems += new Value(parseNum())
        case '(' =>
          i += 1
          elems += parseGroup()
        case ')' =>
          i += 2
          repeats = parseNum().toInt
          break
        case _ =>
          i += 1
      })
      
      new Group(elems, repeats)
    }
    parseGroup()
  }
  
  Iterator.continually(readInt).takeWhile(_ != 0).foreach { n =>
    val line = readLine
  
    val root = parse(line.toCharArray)
    println(root.sim(n, true))
  }
}
