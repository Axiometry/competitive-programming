object HexagramS extends App {
  while(readLine.split(" ").map(_.toInt) match {
    case arr if arr.forall(_ == 0) => false
    case arr =>
      val taken = Array.ofDim[Boolean](12)
      val hexa = Array.ofDim[Int](12)
      
      def ways(pos: Int): Int = {
        if(pos == 12) return 1
        
        @inline def check =
          if(pos == 6) hexa(0)+hexa(1)+hexa(2) == hexa(4)+hexa(5)+hexa(6)
          else if(pos == 8) hexa(3)+hexa(4)+hexa(5) == hexa(7)+hexa(8)+hexa(0)
          else if(pos == 10) hexa(6)+hexa(8)+hexa(0) == hexa(9)+hexa(1)+hexa(10)
          else if(pos == 11) hexa(2)+hexa(4)+hexa(10) == hexa(5)+hexa(7)+hexa(9) && hexa(11)+hexa(2)+hexa(4) == hexa(9)+hexa(8)+hexa(1)
          else true
        
        var w = 0
        for(i <- 0 until 12; if !taken(i)) {
          hexa(pos) = arr(i)
          if(check) {
            taken(i) = true
            w += ways(pos+1)
            taken(i) = false
          }
        }
        w
      }
      
      println(ways(0)/4)
      true
  }) {}
}
