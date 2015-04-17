package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("findMin should return min element given two elements added into empty heap") = forAll { (x: Int, y: Int) =>
    val heap = insert(x, insert(y, empty))
    findMin(heap) == math.min(x, y)
  }

  property("heap should be empty if delete min of heap with one element") = forAll { a: Int =>
    val h = insert(a, empty)
    deleteMin(h) == empty
  }

  property("melding") = forAll { (h1: H, h2: H) =>
    if (!isEmpty(h1) && !isEmpty(h2)) {
      val m1 = findMin(h1)
      val m2 = findMin(h2)
      val melded = meld(h1, h2)
      val meldedMin = findMin(melded)
      meldedMin == m1 || meldedMin == m2
    } else true
  }

  property("melding and sorting") = forAll { (h1: H, h2: H) =>
    val l1 = heapToList(h1)
    val l2 = heapToList(h2)
    val melded = meld(h1, h2)
    val lMelded = heapToList(melded)
    (l1 ++ l2).sorted == lMelded
  }

  def heapToList(heap: H): List[Int] = {
    if (isEmpty(heap)) Nil else findMin(heap) :: heapToList(deleteMin(heap))
  }

  lazy val genMap: Gen[Map[Int, Int]] = for {
    k <- arbitrary[Int]
    v <- arbitrary[Int]
    m <- oneOf(const(Map.empty[Int, Int]), genMap)
  } yield m.updated(k, v)

  lazy val genHeap: Gen[H] = for {
    k <- arbitrary[A]
    m <- oneOf(const(empty), genHeap)
  } yield insert(k, m)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)
}