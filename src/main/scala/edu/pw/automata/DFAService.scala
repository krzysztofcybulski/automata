package edu.pw.automata

import edu.pw.automata.diagram.GraphService
import edu.pw.automata.fsm.{DFA, DFADemo, DeltaFunction, State}
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single.Property

import scala.concurrent.ExecutionContext.Implicits.global

object DFAService {

  var dfa = DFA.empty()

  def addState(name: Option[String]) = {
    val s = State(dfa.size, name)
    dfa = dfa.addState(s).toDFA()
    Definition.stateNames.append(s.toString)

    if(dfa.size == 1)
      dfa = dfa.setInitial(s).toDFA()

    Definition.transitions.append(Seq.fill(Definition.alphabetNames.size + 1)(s.toString))
  }

  def addSymbol(name: Option[String]) = {
    val s = name.getOrElse(dfa.alphabetSize + "")
    val symbol = Symbol(s)
    dfa = dfa.addSymbol(symbol).toDFA()
    Definition.alphabetNames.append(symbol.toString())

    val t = Definition.transitions
    t.set(t.get.map(e => e :+ e.head))
  }

  def setTransition(state: String, input: String, state2: String) = {

    val from = dfa.stateFromString(state)
    val symbol = dfa.symbolFromString(input)

    if(state2.isEmpty) {
      dfa = dfa.removeDelta(dfa.getDelta().filter(p => p.a == from.get && p.symbol == symbol.get).head).toDFA()
      var i = 0
      var j = 0

      while(Definition.transitions.get(i)(0) != from.get.toString)
        i = i + 1

      while(Definition.alphabetNames.get(j) != symbol.get.toString)
        j = j + 1

      Definition.transitions.replace(i, 1, Definition.transitions.get(i).updated(j + 1, ""))
    } else
      dfa = dfa.addDelta(new DeltaFunction(from.get, symbol.get, dfa.stateFromString(state2).get)).toDFA()

    Definition.reload
  }

  def addAccepting(state: String) = {
    val s = dfa.stateFromString(state).get

    if(dfa.isAccepting(s))
      dfa = dfa.removeAccepting(s).toDFA()
    else
      dfa = dfa.addAccepting(s).toDFA()

    Definition.reload
  }

  def setStarting(state: String): Boolean = {
    val s = dfa.stateFromString(state)

    if(s.isDefined)
      dfa = dfa.setInitial(s.get).toDFA()

    Definition.reload

    s.isDefined
  }

  def loadDemo() = dfa = DFADemo.get()

  def reload() {
    Definition.stateNames.set(dfa.getStates.map(_.toString).toSeq)
    Definition.alphabetNames.set(dfa.getAlphabet.map(_.toString).toSeq)
    Definition.transitions.set(dfa.getStates.toSeq.map(state => {
      state.toString :: dfa.getAlphabet.toList.map( a =>
        if(dfa.hasDelta(a, state)) {
          dfa.move(a, Some(state)).getOrElse("").toString
        } else ""
      )
    }))
  }

  object Definition {

    val definition    = Property[String](dfa.toString)

    val stateNames    = SeqProperty[String]
    val alphabetNames = SeqProperty[String]
    val transitions   = SeqProperty[Seq[String]]

    //stateNames.listen(_ => reload)
    //alphabetNames.listen(_ => reload)

    def reload = {
      definition.set(dfa.toString)
      GraphService.repaint()
    }
  }

}
