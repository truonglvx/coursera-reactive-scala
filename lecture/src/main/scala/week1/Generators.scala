package week1

import scala.util.Random

object Generators {

  trait Generator[+T] {
    self =>
    // an alias for "this"

    def generate: T

    def map[S](f: T => S): Generator[S] = new Generator[S] {
      // this inside is not self, it's generate
      override def generate: S = f(self.generate) //f(Generator.this.generate)
    }

    def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
      override def generate: S = f(self.generate).generate
    }
  }

  val integers = new Generator[Int] {
    override def generate = Random.nextInt()
  }

  val pairs = new Generator[(Int, Int)] {
    override def generate: (Int, Int) = (integers.generate, integers.generate)
  }

  val booleans1 = for (x <- integers) yield x > 0
  val booleans2 = integers map { x => x > 0 }
  val booleans = integers.map(_ > 0)

//  def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap {
//    x => u map { y => (x, y) }
//  }
//
//  def pairs[T, U](t: Generator[T], u: Generator[U]) = t flatMap {
//    x => new Generator[(T, U)] {
//      override def generate: (T, U) = (x, u.generate)
//    }
//  }
//
//  def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
//    override def generate = new Generator[(T, U)] {
//      override def generate = (t.generate, u.generate)
//    }.generate
//  }

  def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
    override def generate: (T, U) = (t.generate, u.generate)
  }

  def single[T](x: T): Generator[T] = new Generator[T] {
    override def generate: T = x
  }

  def choose(lo: Int, hi: Int): Generator[Int] = {
    for (x <- integers) yield lo + x % (hi - lo)
  }

  def oneOf[T](xs: T*): Generator[T] = {
    for (idx <- choose(0, xs.length)) yield xs(idx)
  }

  oneOf("red", "green", "orange")

  def emptyLists = single(Nil)

  def nonEmptyLists = for {
    head <- integers
    tail <- lists
  } yield head :: tail

  def lists: Generator[List[Int]] = for {
    isEmpty <- booleans
    list <- if (isEmpty) emptyLists else nonEmptyLists
  } yield list

  trait Tree

  case class Inner(left: Tree, right: Tree) extends Tree

  case class Leaf(x: Int) extends Tree

  def leafs: Generator[Leaf] = for {
    x <- integers
  } yield Leaf(x)

  def inners: Generator[Inner] = for {
    left <- trees
    right <- trees
  } yield Inner(left, right)

  def trees: Generator[Tree] = for {
    isLeaf <- booleans
    tree <- if (isLeaf) leafs else inners
  } yield tree

  trees.generate

  def test[T](g: Generator[T], numTimes: Int = 100)(test: T => Boolean): Unit = {
    for (i <- 0 until numTimes) {
      val value = g.generate
      assert(test(value), "test failed for " + value)
    }
    println("passed " + numTimes + " tests")
  }
}