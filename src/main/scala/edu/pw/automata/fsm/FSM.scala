package edu.pw.automata.fsm;

import Types._

class FSM(Q:   States,
          Σ:   Alphabet,
          δ:   Delta,
          q0:  Option[State],
          F:   States) {

  implicit def fsmFromTuple(t: (States, Alphabet, Delta, Option[State], States)): FSM
                                  = new FSM(t._1, t._2, t._3, t._4, t._5)

  def isNFA = δ.groupBy(e => (e.a, e.symbol)).values.exists(_.size > 1)

  def isDFA = !isNFA

  def isAccepting(s: State) = F contains s

  def addState(s: State): FSM           = (Q + s, Σ, δ, q0, F)
  def addSymbol(s: Symbol): FSM         = (Q, Σ + s, δ, q0, F)
  def addDelta(d: DeltaFunction): FSM   = (Q, Σ, δ + d, q0, F)
  def setInitial(s: State): FSM         = (Q, Σ, δ,Some(s), F)
  def addAccepting(s: State): FSM       = (Q, Σ, δ, q0, F + s)

  def +(a: Any) = a match {
    case s: State =>          addState(s)
    case s: Symbol =>         addSymbol(s)
    case d: DeltaFunction =>  addDelta(d)
    case _ =>                 this
  }

  def removeState(s: State): FSM         = (Q - s, Σ, δ, q0, F)
  def removeSymbol(s: Symbol): FSM       = (Q, Σ - s, δ, q0, F)
  def removeDelta(d: DeltaFunction): FSM = (Q, Σ, δ - d, q0, F)
  def removeInitial(s: State): FSM       = (Q, Σ, δ, None,   F)
  def removeAccepting(s: State): FSM     = (Q, Σ, δ, q0, F - s)

  def -(a: Any) = a match {
    case s: State =>          removeState(s)
    case s: Symbol =>         removeSymbol(s)
    case d: DeltaFunction =>  removeDelta(d)
    case _ =>                 this
  }

  def definition = Map[String, Iterable[Any]](
      "Q" -> Q,
      "Σ" -> Σ,
      "δ" -> δ,
      "q0"-> q0,
      "F" -> F
    )

  override def toString = definition.map(e => s"${e._1} => {${e._2.mkString(",")}}").mkString("\n")

}