object p3a extends App {
  implicit class IntPow(val i: Int) extends AnyVal {
    def **(j: Int): Int = math.pow(i, j).toInt
  }
  
  def numToRing(num: Int) = ((math.sqrt(num-1)+1)/2).toInt+1
  def ringToLastNum(i: Int) = if(i == 0) 0 else (i*2-1)**2
  def numToRingOff(num: Int) = num-ringToLastNum(numToRing(num)-1)-1
  def numToCoord(num: Int) = {
    val i = numToRing(num)
    val n = numToRingOff(num)
    ((n+1) / ((i-1)*2) % 4, (n+1) % ((i-1)*2)) match {
      case (0, off) => (i-1, off-(i-1))
      case (1, off) => ((i-1)-off, i-1)
      case (2, off) => (-(i-1), (i-1)-off)
      case (3, off) => (off-(i-1), -(i-1))
    }
  }
  val manhatten = ((x: Int, y: Int) => x.abs + y.abs).tupled
  
  print(manhatten(numToCoord(readInt)))
}
