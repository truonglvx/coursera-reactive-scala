package week2

class Circuits extends Gates {

  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire): Unit = {
    val d = new Wire
    val e = new Wire
    this.orGate(a, b, d)
    this.andGate(a, b, c)
    this.inverter(c, e)
    this.andGate(d, e, s)
  }

  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire): Unit = {
    val s = new Wire
    val c1 = new Wire
    val c2 = new Wire
    this.halfAdder(b, cin, s, c1)
    this.halfAdder(a, s, sum, c2)
    this.orGate(c1, c2, cout)
  }
}
