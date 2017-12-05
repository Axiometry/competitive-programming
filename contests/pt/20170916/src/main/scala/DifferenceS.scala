object DifferenceS extends App {
  while(rec(readLine.split(" ").map(_.toInt).toList) match {
    case 0 => false
    case steps => println(steps-1); true
  }) {}
  def rec(nums: List[Int]): Int = nums match {
    case List(0, 0, 0, 0) => 0
    case _ => 1+rec((nums.last :: nums).zip(nums).map { case (l, r) => math.abs(l-r) })
  }
}
